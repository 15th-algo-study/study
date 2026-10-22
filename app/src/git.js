import { spawn } from 'node:child_process';
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { MULTI_USER, REPO_ROOT } from './config.js';

export class GitError extends Error {}

/**
 * 서버 모드에서 root 로 돌고 있으면 git 은 레포 폴더 주인(uid)으로 실행한다.
 * 그래야 컨테이너가 바인드 마운트한 호스트 clone 에 root 소유 파일이 생기지 않아 호스트의 git pull 이 깨지지 않는다.
 */
let owner;
function repoOwner() {
  if (owner !== undefined) return owner;
  owner = null;
  if (MULTI_USER && process.getuid?.() === 0) {
    const st = fs.statSync(REPO_ROOT);
    if (st.uid !== 0) owner = { uid: st.uid, gid: st.gid };
  }
  return owner;
}

/** fetch·push·커밋처럼 ref 를 바꾸는 작업은 한 번에 하나씩 (여러 사용자가 동시에 제출할 때 ref 잠금 충돌 방지) */
let chain = Promise.resolve();
function withLock(task) {
  const next = chain.then(task, task);
  chain = next.catch(() => {});
  return next;
}

/**
 * 레포 루트에서 git 실행. 사용자의 작업 트리·인덱스는 건드리지 않는다:
 * 읽기는 origin/main 참조에서, 커밋은 임시 인덱스로 만든다.
 * input 을 주면 표준 입력으로 넘기고, 아니면 stdin 을 닫는다 (git 이 입력을 기다리며 멈추지 않도록).
 */
export function git(args, { env = {}, input } = {}) {
  return new Promise((resolve, reject) => {
    const o = repoOwner();
    const child = spawn('git', args, {
      cwd: REPO_ROOT,
      env: { ...process.env, GIT_TERMINAL_PROMPT: '0', LC_ALL: 'C', ...(o ? { HOME: '/tmp' } : {}), ...env },
      stdio: ['pipe', 'pipe', 'pipe'],
      ...(o || {}),
    });
    const out = [], err = [];
    child.stdout.on('data', (d) => out.push(d));
    child.stderr.on('data', (d) => err.push(d));
    child.on('error', (e) => reject(new GitError(`git 실행 실패: ${e.message}`)));
    child.on('close', (code) => {
      if (code === 0) resolve(Buffer.concat(out).toString('utf8'));
      else reject(new GitError(`git ${args[0]} 실패: ${Buffer.concat(err).toString('utf8').trim() || `exit ${code}`}`));
    });
    if (input !== undefined) child.stdin.write(input);
    child.stdin.end();
  });
}

export function isRepo() {
  return fs.existsSync(path.join(REPO_ROOT, '.git'));
}

/** 토큰을 argv 가 아니라 환경 변수로 넘긴다 (ps 에 노출되지 않음). */
function authEnv(token) {
  if (!token) return {};
  const basic = Buffer.from(`x-access-token:${token}`).toString('base64');
  return {
    GIT_CONFIG_COUNT: '1',
    GIT_CONFIG_KEY_0: 'http.extraheader',
    GIT_CONFIG_VALUE_0: `AUTHORIZATION: basic ${basic}`,
  };
}

/** origin/main (과 지정한 사용자 브랜치) 참조만 갱신한다. */
export function fetchRemote(opts) {
  return withLock(() => fetchUnlocked(opts));
}

async function fetchUnlocked({ url, token, login }) {
  await git(['fetch', '--quiet', url, '+refs/heads/main:refs/remotes/origin/main'], { env: authEnv(token) });
  if (login) {
    try {
      await git(['fetch', '--quiet', url, `+refs/heads/user/${login}:refs/remotes/origin/user/${login}`], { env: authEnv(token) });
    } catch {
      // 사용자 브랜치가 없으면(머지 후 삭제됨) 지역 참조도 지운다
      await git(['update-ref', '-d', `refs/remotes/origin/user/${login}`]).catch(() => {});
    }
  }
}

export async function hasRef(ref) {
  try { await git(['rev-parse', '--verify', '--quiet', ref]); return true; } catch { return false; }
}

/** origin/main 의 solutions 목록 → Map<login, Set<"day/lv">> */
export async function mergedSolutions() {
  if (!(await hasRef('refs/remotes/origin/main'))) return new Map();
  const out = await git(['ls-tree', '-r', '--name-only', 'refs/remotes/origin/main', '--', 'solutions']);
  const map = new Map();
  for (const line of out.split('\n')) {
    const m = line.match(/^solutions\/([^/]+)\/day-(\d{3})\/lv([0-3])\/Solution\.java$/);
    if (!m) continue;
    if (!map.has(m[1])) map.set(m[1], new Set());
    map.get(m[1]).add(`${m[2]}/${m[3]}`);
  }
  return map;
}

/** 아직 머지되지 않았을 수 있는 내 제출: 원격 user/<login> 브랜치의 풀이 목록 → Set<"day/lv"> */
export async function pendingSolutions(login) {
  const ref = `refs/remotes/origin/user/${login}`;
  if (!login || !(await hasRef(ref))) return new Set();
  const out = await git(['ls-tree', '-r', '--name-only', ref, '--', `solutions/${login}`]).catch(() => '');
  const set = new Set();
  for (const line of out.split('\n')) {
    const m = line.match(/^solutions\/[^/]+\/day-(\d{3})\/lv([0-3])\/Solution\.java$/);
    if (m) set.add(`${m[1]}/${m[2]}`);
  }
  return set;
}

export async function mergedPaths() {
  if (!(await hasRef('refs/remotes/origin/main'))) return '';
  return git(['ls-tree', '-r', '--name-only', 'refs/remotes/origin/main', '--', 'solutions']);
}

/** origin/main 의 파일 내용 (없으면 null) */
export async function showMerged(relPath) {
  try { return await git(['show', `refs/remotes/origin/main:${relPath}`]); } catch { return null; }
}

/**
 * 작업 트리를 건드리지 않고 커밋을 만들어 user/<login> 으로 푸시한다.
 * stack=true 면 기존 user/<login> 위에 쌓고(열린 PR 이 있을 때), 아니면 origin/main 에서 새로 시작한다.
 */
export function commitAndPush(opts) {
  return withLock(() => commitAndPushUnlocked(opts));
}

async function commitAndPushUnlocked({ url, token, login, author, relPath, content, message, stack }) {
  const base = stack ? `refs/remotes/origin/user/${login}` : 'refs/remotes/origin/main';
  // 임시 인덱스는 .git 안에 둔다 (git 을 실행하는 uid 가 항상 쓸 수 있는 곳)
  const indexFile = path.join(REPO_ROOT, '.git', `algo-index-${crypto.randomBytes(6).toString('hex')}`);
  const env = { GIT_INDEX_FILE: indexFile };
  try {
    await git(['read-tree', base], { env });
    const blob = (await git(['hash-object', '-w', '--stdin'], { input: content })).trim();
    await git(['update-index', '--add', '--cacheinfo', `100644,${blob},${relPath}`], { env });
    const tree = (await git(['write-tree'], { env })).trim();
    const baseTree = (await git(['rev-parse', `${base}^{tree}`])).trim();
    if (tree === baseTree) return { pushed: false, reason: '이미 같은 내용이 올라가 있습니다.' };
    const commit = (await git(['commit-tree', tree, '-p', base, '-m', message], {
      env: {
        GIT_AUTHOR_NAME: author.name, GIT_AUTHOR_EMAIL: author.email,
        GIT_COMMITTER_NAME: author.name, GIT_COMMITTER_EMAIL: author.email,
      },
    })).trim();
    const pushArgs = ['push', '--quiet', url, `${commit}:refs/heads/user/${login}`];
    if (!stack) pushArgs.splice(1, 0, '--force'); // 머지된 뒤 남은 옛 브랜치는 덮어쓴다
    await git(pushArgs, { env: authEnv(token) });
    await git(['update-ref', `refs/remotes/origin/user/${login}`, commit]);
    return { pushed: true, commit };
  } finally {
    fs.rmSync(indexFile, { force: true });
  }
}

/**
 * ZIP 으로 내려받은 폴더를 clone 과 같은 상태로 만든다 (멤버 PC 에 git 이 없을 때).
 * git init → origin/main 가져오기 → main 을 거기에 맞추고 작업 트리를 그 내용으로 맞춘다.
 * 이미 저장소면 아무것도 하지 않는다.
 */
export function initRepo(opts) {
  return withLock(() => initRepoUnlocked(opts));
}

async function initRepoUnlocked({ url, token }) {
  if (isRepo()) return { ok: true, message: '이미 git 저장소입니다.' };
  await git(['init', '-q', '-b', 'main']);
  await git(['remote', 'add', 'origin', url]).catch(() => {});
  await fetchUnlocked({ url, token });
  await git(['update-ref', 'refs/heads/main', 'refs/remotes/origin/main']);
  await git(['symbolic-ref', 'HEAD', 'refs/heads/main']);
  await git(['reset', '-q', '--hard']);
  await git(['branch', '--set-upstream-to=origin/main', 'main']).catch(() => {});
  return { ok: true, message: 'GitHub 저장소와 연결했습니다.' };
}

async function currentBranch() {
  try { return (await git(['symbolic-ref', '--short', '-q', 'HEAD'])).trim(); } catch { return null; }
}

/**
 * 작업 트리를 origin/main 으로 fast-forward 한다 (새 문제·남의 풀이 받기).
 * 앱이 solutions/<login>/ 에 써 둔 untracked 파일이 origin/main 과 내용이 같으면 먼저 지워 충돌을 피한다.
 */
/**
 * 저장소에 CRLF 로 커밋된 파일은 .gitattributes 의 text 규칙 때문에 내용이 같아도 늘 "수정됨"으로 보이고,
 * 그 파일이 바뀌는 merge 를 "로컬 변경이 덮어써진다"며 막는다. 줄바꿈만 다른 파일은 실제 변경이 아니므로
 * 인덱스를 LF 로 맞춰 둔다 (원격도 LF 로 정리되면 그대로 받아지고, 다른 내용으로 바뀌어도 막히지 않는다).
 */
async function renormalizeLineEndingOnly() {
  const changed = (await git(['diff', '--name-only', '-z'])).split('\0').filter(Boolean);
  for (const f of changed) {
    const crOnly = await git(['diff', '--quiet', '--ignore-cr-at-eol', '--', f]).then(() => true, () => false);
    if (crOnly) await git(['add', '--renormalize', '--', f]);
  }
}

export function syncWorkingTree(opts) {
  return withLock(() => syncUnlocked(opts));
}

async function syncUnlocked({ url, token, login, cleanUntracked = true }) {
  await fetchUnlocked({ url, token, login });
  const branch = await currentBranch();
  if (branch !== 'main') {
    return { ok: false, message: `작업 트리가 main 브랜치가 아니라서(${branch ?? 'detached'}) 원격 참조만 갱신했습니다.` };
  }
  if (login && cleanUntracked) {
    const out = await git(['ls-tree', '-r', 'refs/remotes/origin/main', '--', `solutions/${login}`]).catch(() => '');
    for (const line of out.split('\n')) {
      const m = line.match(/^\d+ blob ([0-9a-f]+)\t(.+)$/);
      if (!m) continue;
      const abs = path.join(REPO_ROOT, m[2]);
      if (!fs.existsSync(abs)) continue;
      const tracked = (await git(['ls-files', '--', m[2]])).trim();
      if (tracked) continue;
      const localBlob = (await git(['hash-object', '--', m[2]])).trim();
      if (localBlob === m[1]) fs.rmSync(abs);
    }
  }
  try {
    await renormalizeLineEndingOnly();
    const before = (await git(['rev-parse', 'HEAD'])).trim();
    await git(['merge', '--ff-only', '--quiet', 'refs/remotes/origin/main']);
    const after = (await git(['rev-parse', 'HEAD'])).trim();
    return { ok: true, changed: before !== after, message: before === after ? '이미 최신입니다.' : '최신 상태로 갱신했습니다.' };
  } catch (e) {
    return { ok: false, message: `작업 트리를 갱신하지 못했습니다. 로컬 변경 사항을 확인하세요. (${e.message})` };
  }
}
