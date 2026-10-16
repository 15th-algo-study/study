import fs from 'node:fs';
import path from 'node:path';
import { DATA_DIR, GITHUB_WEB } from './config.js';
import { currentUser } from './github.js';

const TOKEN_FILE = path.join(DATA_DIR, 'token.json');

/** 진행 중인 Device Flow (프로세스당 하나면 충분하다 — 로컬 1인용 앱) */
let pending = null;

function readToken() {
  try { return JSON.parse(fs.readFileSync(TOKEN_FILE, 'utf8')); } catch { return null; }
}

function writeToken(data) {
  fs.mkdirSync(DATA_DIR, { recursive: true });
  fs.writeFileSync(TOKEN_FILE, JSON.stringify(data, null, 2), { mode: 0o600 });
}

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
export async function startDeviceFlow(clientId) {
  if (!clientId) throw new Error('config.json 의 github_app_client_id 가 비어 있습니다. 호스트에게 문의하세요.');
  const d = await oauthPost('/login/device/code', { client_id: clientId });
  if (d.error) throw new Error(d.error_description || d.error);
  pending = { device_code: d.device_code, interval: d.interval || 5, expires_at: Date.now() + d.expires_in * 1000 };
  return { user_code: d.user_code, verification_uri: d.verification_uri, expires_in: d.expires_in, interval: pending.interval };
}

/** 2단계: 토큰 폴링. 프론트가 interval 마다 호출한다. */
export async function pollDeviceFlow(clientId) {
  if (!pending) return { status: 'none' };
  if (Date.now() > pending.expires_at) { pending = null; return { status: 'expired' }; }
  const d = await oauthPost('/login/oauth/access_token', {
    client_id: clientId,
    device_code: pending.device_code,
    grant_type: 'urn:ietf:params:oauth:grant-type:device_code',
  });
  if (d.error === 'authorization_pending') return { status: 'pending' };
  if (d.error === 'slow_down') { pending.interval = (d.interval || pending.interval + 5); return { status: 'pending', interval: pending.interval }; }
  if (d.error) { pending = null; return { status: 'error', message: d.error_description || d.error }; }
  const user = await currentUser(d.access_token);
  writeToken(tokenRecord(d, user));
  pending = null;
  return { status: 'ok', user };
}

async function refresh(rec, clientId) {
  const d = await oauthPost('/login/oauth/access_token', {
    client_id: clientId,
    refresh_token: rec.refresh_token,
    grant_type: 'refresh_token',
  });
  if (d.error) throw new Error(`토큰 갱신 실패: ${d.error_description || d.error}`);
  const next = tokenRecord(d, rec.user);
  writeToken(next);
  return next;
}

/** 유효한 액세스 토큰 (없으면 null). 만료가 가까우면 refresh_token 으로 갱신한다. */
export async function getToken(clientId) {
  let rec = readToken();
  if (!rec) return null;
  if (rec.expires_at && Date.now() > rec.expires_at - 60_000) {
    if (!rec.refresh_token) { logout(); return null; }
    try { rec = await refresh(rec, clientId); } catch (e) { console.warn(e.message); logout(); return null; }
  }
  return rec.access_token;
}

export function getUser() {
  return readToken()?.user || null;
}

export function logout() {
  try { fs.unlinkSync(TOKEN_FILE); } catch { /* 없음 */ }
  pending = null;
}

/** 커밋 author: login 과 noreply 이메일 */
export function authorOf(user) {
  return { name: user.login, email: `${user.id}+${user.login}@users.noreply.github.com` };
}
