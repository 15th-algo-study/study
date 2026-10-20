import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { DATA_DIR, GITHUB_WEB, loadConfig, MULTI_USER } from './config.js';
import { currentUser, hasWriteAccess } from './github.js';

/*
 * 인증 상태 저장 위치
 * - 단일 사용자(로컬): DATA_DIR/token.json 하나. 쿠키 없음.
 * - 다중 사용자(서버): 브라우저마다 쿠키 세션 DATA_DIR/sessions/<sid>.json = { login },
 *   사용자마다 토큰 DATA_DIR/users/<login>/token.json.
 * 라우트는 req.ctx 만 넘기고 어느 모드인지 몰라도 된다.
 */

const COOKIE = 'algo_sid';
const SESSION_DAYS = 30;
const SID_RE = /^[A-Za-z0-9_-]{32}$/;
const LOGIN_RE = /^[A-Za-z0-9-]{1,39}$/;

/** 진행 중인 Device Flow: 세션(또는 단일 모드)마다 하나 */
const pending = new Map();

// ───────── 세션 쿠키 ─────────

function parseCookies(header = '') {
  const out = {};
  for (const part of header.split(';')) {
    const i = part.indexOf('=');
    if (i > 0) out[part.slice(0, i).trim()] = decodeURIComponent(part.slice(i + 1).trim());
  }
  return out;
}

/** req.ctx = { key, sid } 를 붙인다. 다중 모드에서 쿠키가 없으면 새로 발급한다. */
export function sessionMiddleware(req, res, next) {
  if (!MULTI_USER) {
    req.ctx = { key: 'single', sid: null };
    return next();
  }
  let sid = parseCookies(req.headers.cookie)[COOKIE];
  if (!sid || !SID_RE.test(sid)) {
    sid = crypto.randomBytes(24).toString('base64url');
    const secure = req.secure ? '; Secure' : '';
    res.setHeader('Set-Cookie', `${COOKIE}=${sid}; Path=/; HttpOnly; SameSite=Lax; Max-Age=${SESSION_DAYS * 86400}${secure}`);
  }
  req.ctx = { key: sid, sid };
  next();
}

const sessionFile = (sid) => path.join(DATA_DIR, 'sessions', `${sid}.json`);
const userTokenFile = (login) => path.join(DATA_DIR, 'users', login, 'token.json');
const SINGLE_TOKEN_FILE = path.join(DATA_DIR, 'token.json');

function readJson(p) {
  try { return JSON.parse(fs.readFileSync(p, 'utf8')); } catch { return null; }
}

function writeJson(p, data) {
  fs.mkdirSync(path.dirname(p), { recursive: true, mode: 0o700 });
  fs.writeFileSync(p, JSON.stringify(data, null, 2), { mode: 0o600 });
}

function sessionLogin(ctx) {
  const s = readJson(sessionFile(ctx.sid));
  if (!s || !LOGIN_RE.test(s.login || '')) return null;
  if (Date.now() - (s.seen || 0) > 3600_000) writeJson(sessionFile(ctx.sid), { ...s, seen: Date.now() }); // 1시간에 한 번만 갱신
  return s.login;
}

function readRecord(ctx) {
  if (!MULTI_USER) return readJson(SINGLE_TOKEN_FILE);
  const login = sessionLogin(ctx);
  return login ? readJson(userTokenFile(login)) : null;
}

function writeRecord(ctx, rec) {
  if (!MULTI_USER) return writeJson(SINGLE_TOKEN_FILE, rec);
  writeJson(userTokenFile(rec.user.login), rec);
  writeJson(sessionFile(ctx.sid), { login: rec.user.login, created: Date.now(), seen: Date.now() });
}

/** 30일 넘게 쓰지 않은 세션 파일을 지운다 (서버 시작 시). */
export function pruneSessions() {
  if (!MULTI_USER) return;
  const dir = path.join(DATA_DIR, 'sessions');
  for (const f of fs.existsSync(dir) ? fs.readdirSync(dir) : []) {
    const s = readJson(path.join(dir, f));
    if (!s || Date.now() - (s.seen || 0) > SESSION_DAYS * 86400_000) fs.rmSync(path.join(dir, f), { force: true });
  }
}

// ───────── Device Flow ─────────

async function oauthPost(pathname, params) {
  const res = await fetch(`${GITHUB_WEB}${pathname}`, {
    method: 'POST',
    headers: { Accept: 'application/json', 'Content-Type': 'application/x-www-form-urlencoded', 'User-Agent': 'algo-study-app' },
    body: new URLSearchParams(params),
  });
  if (!res.ok) throw new Error(`GitHub 응답 ${res.status}`);
  return res.json();
}

function tokenRecord(t, user) {
  return {
    access_token: t.access_token,
    refresh_token: t.refresh_token || null,
    // expires_in 이 있으면(만료되는 사용자 토큰 설정) 만료 시각을 기록한다
    expires_at: t.expires_in ? Date.now() + t.expires_in * 1000 : null,
    user,
  };
}

/** 1단계: 코드 발급. 사용자는 verification_uri 에서 user_code 를 입력한다. */
export async function startDeviceFlow(clientId, ctx) {
  if (!clientId) throw new Error('config.json 의 github_app_client_id 가 비어 있습니다. 호스트에게 문의하세요.');
  const d = await oauthPost('/login/device/code', { client_id: clientId });
  if (d.error) throw new Error(d.error_description || d.error);
  pending.set(ctx.key, { device_code: d.device_code, interval: d.interval || 5, expires_at: Date.now() + d.expires_in * 1000 });
  return { user_code: d.user_code, verification_uri: d.verification_uri, expires_in: d.expires_in, interval: d.interval || 5 };
}

/** 2단계: 토큰 폴링. 프론트가 interval 마다 호출한다. */
export async function pollDeviceFlow(clientId, ctx) {
  const p = pending.get(ctx.key);
  if (!p) return { status: 'none' };
  if (Date.now() > p.expires_at) { pending.delete(ctx.key); return { status: 'expired' }; }
  const d = await oauthPost('/login/oauth/access_token', {
    client_id: clientId,
    device_code: p.device_code,
    grant_type: 'urn:ietf:params:oauth:grant-type:device_code',
  });
  if (d.error === 'authorization_pending') return { status: 'pending' };
  if (d.error === 'slow_down') { p.interval = d.interval || p.interval + 5; return { status: 'pending', interval: p.interval }; }
  pending.delete(ctx.key);
  if (d.error) return { status: 'error', message: d.error_description || d.error };

  const user = await currentUser(d.access_token);
  if (MULTI_USER) {
    // 서버는 누구나 접속할 수 있으므로 레포에 쓰기 권한이 있는 스터디 멤버만 받는다
    const cfg = loadConfig();
    let ok = false;
    try { ok = await hasWriteAccess({ token: d.access_token, org: cfg.org, repo: cfg.repo, login: user.login }); } catch (e) {
      return { status: 'error', message: `멤버 확인에 실패했습니다: ${e.message}` };
    }
    if (!ok) return { status: 'error', message: `@${user.login} 은(는) 스터디 멤버가 아닙니다. 호스트에게 초대를 요청하세요.` };
  }
  writeRecord(ctx, tokenRecord(d, user));
  return { status: 'ok', user };
}

async function refresh(ctx, rec, clientId) {
  const d = await oauthPost('/login/oauth/access_token', {
    client_id: clientId,
    refresh_token: rec.refresh_token,
    grant_type: 'refresh_token',
  });
  if (d.error) throw new Error(`토큰 갱신 실패: ${d.error_description || d.error}`);
  const next = tokenRecord(d, rec.user);
  writeRecord(ctx, next);
  return next;
}

/** 현재 요청의 { token, user } (로그인 안 됐으면 null). 만료가 가까우면 refresh_token 으로 갱신한다. */
export async function getAuth(clientId, ctx) {
  let rec = readRecord(ctx);
  if (!rec?.access_token) return null;
  if (rec.expires_at && Date.now() > rec.expires_at - 60_000) {
    if (!rec.refresh_token) { logout(ctx); return null; }
    try { rec = await refresh(ctx, rec, clientId); } catch (e) { console.warn(e.message); logout(ctx); return null; }
  }
  return { token: rec.access_token, user: rec.user };
}

export function logout(ctx) {
  pending.delete(ctx.key);
  if (!MULTI_USER) { fs.rmSync(SINGLE_TOKEN_FILE, { force: true }); return; }
  const login = readJson(sessionFile(ctx.sid))?.login;
  fs.rmSync(sessionFile(ctx.sid), { force: true });
  if (login && LOGIN_RE.test(login)) fs.rmSync(userTokenFile(login), { force: true });
}

/** 커밋 author: login 과 noreply 이메일 */
export function authorOf(user) {
  return { name: user.login, email: `${user.id}+${user.login}@users.noreply.github.com` };
}
