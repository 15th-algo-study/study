import express from 'express';
import { GA_MEASUREMENT_ID, loadConfig, MULTI_USER, remoteUrl, TOTAL_DAYS } from './config.js';
import { schedule } from './schedule.js';
import { authorOf, getAuth, logout, pollDeviceFlow, startDeviceFlow } from './auth.js';
import { createPullRequest, GitHubError, openPullRequest } from './github.js';
import {
  commitAndPush, fetchRemote, hasRef, initRepo, isRepo, mergedSolutions, pendingSolutions, showMerged, syncWorkingTree,
} from './git.js';
import { queueLength, runTests } from './runner.js';
import {
  hasDraft, listProblems, pad, readDraft, readEditorial, readLocalSolution, readProblem,
  solutionRelPath, writeDraft, writeLocalSolution,
} from './problems.js';
import { attendanceData } from './attendance.js';

export class HttpError extends Error {
  constructor(status, message) { super(message); this.status = status; }
}

const wrap = (fn) => (req, res, next) => fn(req, res, next).catch(next);

async function session(req) {
  const cfg = loadConfig();
  const auth = await getAuth(cfg.github_app_client_id, req.ctx);
  return { cfg, token: auth?.token || null, user: auth?.user || null, url: remoteUrl(cfg) };
}

/** 작성 중 코드의 주인. 단일 모드의 비로그인 사용자는 'guest' (서버 모드는 로그인해야 저장한다) */
function draftOwner(s) {
  return s.user?.login || 'guest';
}

/** 서버 모드에서는 실행·제출·저장에 로그인이 필요하다 (누구나 접속할 수 있으므로 임의 코드 실행을 막는다) */
function requireLoginInServer(s) {
  if (MULTI_USER && !s.user) throw new HttpError(401, '로그인한 스터디 멤버만 실행·제출할 수 있습니다.');
}

/** 한 사람이 채점을 여러 개 동시에 걸지 못하게 한다 */
const busy = new Set();
async function exclusive(key, task) {
  if (busy.has(key)) throw new HttpError(429, '이전 실행이 아직 끝나지 않았습니다. 잠시 후 다시 시도하세요.');
  busy.add(key);
  try { return await task(); } finally { busy.delete(key); }
}

function parseTarget(req) {
  const day = Number(req.params.day);
  const level = Number(req.params.level);
  if (!Number.isInteger(day) || day < 1 || day > 999 || !Number.isInteger(level) || level < 0 || level > 3) {
    throw new HttpError(400, '일차 또는 레벨이 올바르지 않습니다.');
  }
  if (!readProblem(day, level)) throw new HttpError(404, `문제가 없습니다: day-${pad(day)}/lv${level}`);
  return { day, level, key: `${pad(day)}/${level}` };
}

function codeOf(req) {
  const code = req.body?.code;
  if (typeof code !== 'string') throw new HttpError(400, 'code 가 필요합니다.');
  if (code.length > 200_000) throw new HttpError(400, '코드가 너무 깁니다.');
  return code;
}

/** 내 풀이 상태: merged(머지됨) > submitted(PR 대기) > draft(작성 중) > none */
function statusOf(login, mine, pending, day, level) {
  const key = `${pad(day)}/${level}`;
  if (login && mine.has(key)) return 'merged';
  if (login && (pending.has(key) || (!MULTI_USER && readLocalSolution(login, day, level) !== null))) return 'submitted';
  if (hasDraft(login || 'guest', day, level)) return 'draft';
  return 'none';
}

export function createRouter() {
  const r = express.Router();

  // ───── 인증 ─────
  r.get('/me', wrap(async (req, res) => {
    const s = await session(req);
    res.json({ user: s.user });
  }));
  r.post('/auth/device', wrap(async (req, res) => {
    const cfg = loadConfig();
    res.json(await startDeviceFlow(cfg.github_app_client_id, req.ctx));
  }));
  r.post('/auth/poll', wrap(async (req, res) => {
    const cfg = loadConfig();
    res.json(await pollDeviceFlow(cfg.github_app_client_id, req.ctx));
  }));
  r.post('/auth/logout', (req, res) => { logout(req.ctx); res.json({ ok: true }); });

  // ───── 전체 상태 (일차 목록) ─────
  r.get('/state', wrap(async (req, res) => {
    const s = await session(req);
    const sch = schedule(s.cfg);
    const merged = await mergedSolutions();
    const login = s.user?.login || null;
    const mine = login ? merged.get(login) || new Set() : new Set();
    const pending = await pendingSolutions(login);
    const solverCount = new Map();
    for (const set of merged.values()) for (const k of set) solverCount.set(k, (solverCount.get(k) || 0) + 1);

    const days = new Map();
    for (const p of listProblems()) {
      if (!days.has(p.day)) {
        days.set(p.day, { day: p.day, date: sch.dates[p.day - 1] || null, unlocked: sch.started && p.day <= sch.todayDay, levels: [] });
      }
      days.get(p.day).levels.push({
        level: p.level,
        title: p.title,
        status: statusOf(login, mine, pending, p.day, p.level),
        solvers: solverCount.get(`${pad(p.day)}/${p.level}`) || 0,
      });
    }
    res.json({
      repo: isRepo(),
      multiUser: MULTI_USER,
      org: s.cfg.org, repoName: s.cfg.repo,
      startDate: s.cfg.start_date, endDate: s.cfg.end_date,
      today: sch.today, todayDay: sch.todayDay, totalDays: TOTAL_DAYS,
      user: s.user,
      gaId: GA_MEASUREMENT_ID,
      days: [...days.values()],
    });
  }));

  // ───── 문제 + 내 코드 ─────
  r.get('/problem/:day/:level', wrap(async (req, res) => {
    const { day, level } = parseTarget(req);
    const s = await session(req);
    const login = draftOwner(s);
    const problem = readProblem(day, level);
    let code = MULTI_USER && !s.user ? null : readDraft(login, day, level);
    let codeSource = 'draft';
    if (code === null && s.user && !MULTI_USER) { code = readLocalSolution(login, day, level); codeSource = 'local'; }
    if (code === null && s.user) { code = await showMerged(solutionRelPath(login, day, level)); codeSource = 'merged'; }
    if (code === null) { code = problem.template; codeSource = 'template'; }
    const merged = await mergedSolutions();
    const mine = s.user ? merged.get(login) || new Set() : new Set();
    const pending = await pendingSolutions(s.user?.login);
    res.json({
      day, level, readme: problem.readme, template: problem.template, code, codeSource,
      status: statusOf(s.user ? login : null, mine, pending, day, level),
      solvers: [...merged.entries()].filter(([, set]) => set.has(`${pad(day)}/${level}`)).map(([l]) => l).sort(),
    });
  }));

  r.put('/draft/:day/:level', wrap(async (req, res) => {
    const { day, level } = parseTarget(req);
    const s = await session(req);
    requireLoginInServer(s);
    writeDraft(draftOwner(s), day, level, codeOf(req));
    res.json({ ok: true });
  }));

  // ───── 실행 (basic) ─────
  r.post('/run/:day/:level', wrap(async (req, res) => {
    const { day, level } = parseTarget(req);
    const s = await session(req);
    requireLoginInServer(s);
    const login = draftOwner(s);
    const code = codeOf(req);
    writeDraft(login, day, level, code);
    res.json(await exclusive(MULTI_USER ? login : 'single', () => runTests({ login, day: pad(day), level, code, basicOnly: true })));
  }));

  // ───── 제출 (전체 케이스 → 커밋·푸시 → PR) ─────
  r.post('/submit/:day/:level', wrap(async (req, res) => {
    const { day, level } = parseTarget(req);
    const s = await session(req);
    if (!s.user) throw new HttpError(401, '제출하려면 GitHub 로그인이 필요합니다.');
    if (!isRepo()) throw new HttpError(500, '레포 루트가 git 저장소가 아닙니다.');
    const login = s.user.login;
    const code = codeOf(req);
    writeDraft(login, day, level, code);
    res.json(await exclusive(MULTI_USER ? login : 'single', () => submit(s, { login, day, level, code })));
  }));

  async function submit(s, { login, day, level, code }) {
    // 로컬 기기 차이를 감안해 제한 시간에 여유 배수를 둔다. 최종 판정은 CI(배수 1).
    const grade = await runTests({ login, day: pad(day), level, code, basicOnly: false, timeScale: Number(s.cfg.local_time_scale) || 2 });
    if (grade.status !== 'PASS') return { grade, pr: null };

    // 서버는 여러 사람이 쓰는 clone 이므로 작업 트리에 풀이를 쓰지 않는다 (제출 상태는 원격 브랜치로 판단)
    if (!MULTI_USER) writeLocalSolution(login, day, level, code);
    await fetchRemote({ url: s.url, token: s.token, login });
    const head = `user/${login}`;
    let pr = await openPullRequest({ token: s.token, org: s.cfg.org, repo: s.cfg.repo, head });
    const stack = Boolean(pr) && await hasRef(`refs/remotes/origin/${head}`);
    const pushed = await commitAndPush({
      url: s.url, token: s.token, login, author: authorOf(s.user),
      relPath: solutionRelPath(login, day, level), content: code,
      message: `solve: day-${pad(day)} lv${level}`, stack,
    });
    let created = false;
    if (!pr) {
      try {
        pr = await createPullRequest({
          token: s.token, org: s.cfg.org, repo: s.cfg.repo, head,
          title: `solve: day-${pad(day)} lv${level} (@${login})`,
          body: `- day-${pad(day)} lv${level}\n\n앱에서 자동 생성된 PR 입니다. CI 통과 시 자동 머지됩니다.`,
        });
        created = true;
      } catch (e) {
        if (e instanceof GitHubError && e.status === 422) {
          pr = await openPullRequest({ token: s.token, org: s.cfg.org, repo: s.cfg.repo, head });
        }
        if (!pr) throw e;
      }
    }
    return { grade, pr: { number: pr.number, url: pr.html_url, created, stacked: stack, pushed: pushed.pushed, note: pushed.reason || null } };
  }

  // ───── 다른 사람 풀이 · 해설 ─────
  r.get('/solutions/:day/:level', wrap(async (req, res) => {
    const { day, level } = parseTarget(req);
    const merged = await mergedSolutions();
    const solvers = [...merged.entries()].filter(([, set]) => set.has(`${pad(day)}/${level}`)).map(([l]) => l).sort();
    res.json({ solvers, editorial: readEditorial(day, level) });
  }));
  r.get('/solutions/:day/:level/:login', wrap(async (req, res) => {
    const { day, level } = parseTarget(req);
    const login = req.params.login;
    if (!/^[A-Za-z0-9-]{1,39}$/.test(login)) throw new HttpError(400, 'login 이 올바르지 않습니다.');
    const code = await showMerged(solutionRelPath(login, day, level));
    if (code === null) throw new HttpError(404, '아직 머지된 풀이가 없습니다.');
    res.json({ login, code });
  }));

  // ───── 출석표 · 동기화 ─────
  r.get('/attendance', wrap(async (req, res) => res.json(await attendanceData())));
  // ZIP 으로 받은 폴더를 저장소로 전환 (로그인 필요 — private 레포를 가져와야 하므로)
  r.post('/repo/init', wrap(async (req, res) => {
    if (MULTI_USER) throw new HttpError(400, '서버 모드에서는 사용하지 않습니다.');
    const s = await session(req);
    if (!s.user) throw new HttpError(401, '저장소를 연결하려면 먼저 GitHub 로그인이 필요합니다.');
    res.json(await initRepo({ url: s.url, token: s.token }));
  }));
  r.post('/sync', wrap(async (req, res) => {
    const s = await session(req);
    requireLoginInServer(s);
    if (!isRepo()) throw new HttpError(500, '레포 루트가 git 저장소가 아닙니다.');
    // 서버 모드: 작업 트리에 풀이 파일이 없으므로 정리할 것이 없고, 원격 user 브랜치만 새로 받는다
    res.json(await syncWorkingTree({ url: s.url, token: s.token, login: s.user?.login, cleanUntracked: !MULTI_USER }));
  }));

  r.get('/queue', (req, res) => res.json({ waiting: queueLength() }));

  return r;
}
