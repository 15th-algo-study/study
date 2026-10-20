import { spawn, spawnSync } from 'node:child_process';
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { REPO_ROOT, RUNNER_SLOTS, RUNNER_SLOTS_DIR, RUNNER_UID, RUNS_DIR } from './config.js';

const RUNNER_DIR = path.join(REPO_ROOT, 'runner');
const TIMEOUT_MS = 10 * 60 * 1000;
const JAVA = process.env.JAVA_HOME ? path.join(process.env.JAVA_HOME, 'bin', 'java') : 'java';
/** 채점 JVM 옵션 — runner/build.gradle.kts 의 tasks.test 와 같게 맞춘다 (시간 측정 조건을 CI 와 동일하게) */
const JVM_ARGS = ['-Xmx1g', '-Xss64m', '-XX:+UseParallelGC', '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8'];

/*
 * 채점은 Gradle 을 거치지 않고 java 로 study.runner.Main 을 바로 실행한다 (채점 내용은 ./gradlew test 와 같음).
 * 슬롯 = 동시에 진행되는 채점 하나. 슬롯 폴더 구성:
 *   <slot>/runner/  레포 runner/ 의 복사본 — Gradle 로 클래스패스(jar)를 만들 때만 쓴다 (실행 사용자 소유)
 *   <slot>/lib/     러너 jar + Gson + CDS 아카이브 — 매 채점이 읽기만 한다 (서버 모드에서는 root 소유, 제출 코드가 못 바꿈)
 * 러너 소스가 바뀌면 lib 를 다시 만든다.
 */
const free = Array.from({ length: RUNNER_SLOTS }, (_, i) => i);
const waiters = [];

function acquire() {
  return free.length ? Promise.resolve(free.shift()) : new Promise((resolve) => waiters.push(resolve));
}

function release(slot) {
  const next = waiters.shift();
  if (next) next(slot);
  else free.push(slot);
}

export function queueLength() {
  return waiters.length;
}

/**
 * 서버 모드 점검: 제출 코드를 실행하는 uid 가 레포에 쓸 수 있으면 경고한다.
 * (리눅스 서버에서는 레포가 호스트 사용자 소유라 쓸 수 없어야 정상. Docker Desktop 의 파일 공유는 권한을 무시한다)
 */
export function checkRunnerIsolation() {
  if (RUNNER_UID === null || process.getuid?.() !== 0) return null;
  const probe = path.join(REPO_ROOT, `.runner-write-test-${process.pid}`);
  const r = spawnSync('sh', ['-c', `touch "${probe}"`], { uid: RUNNER_UID, gid: RUNNER_UID });
  const writable = r.status === 0;
  fs.rmSync(probe, { force: true });
  return writable
    ? `경고: 제출 코드 실행 사용자(uid ${RUNNER_UID})가 레포 폴더에 쓸 수 있습니다. 리눅스 서버에서 레포를 일반 사용자 소유로 clone 하세요.`
    : null;
}

/** 제출 코드를 별도 uid 로 실행할 수 있는지 (서버 모드, root 로 실행 중) */
const dropPrivileges = RUNNER_UID !== null && process.getuid?.() === 0;
const asRunner = dropPrivileges ? { uid: RUNNER_UID, gid: RUNNER_UID } : {};

function ownByRunner(p) {
  if (dropPrivileges) fs.chownSync(p, RUNNER_UID, RUNNER_UID);
}

function ownByRoot(p) {
  if (dropPrivileges) fs.chownSync(p, 0, 0);
}

function childEnv() {
  return { ...process.env, TERM: 'dumb', ...(dropPrivileges ? { HOME: '/tmp' } : {}) };
}

/** 자식 프로세스를 끝까지 기다린다 → { code, out } */
function runProcess(cmd, args, { cwd, timeoutMs = TIMEOUT_MS } = {}) {
  return new Promise((resolve) => {
    const child = spawn(cmd, args, { cwd, env: childEnv(), ...asRunner });
    let out = '';
    child.stdout.on('data', (d) => { out += d; });
    child.stderr.on('data', (d) => { out += d; });
    const timer = setTimeout(() => child.kill('SIGKILL'), timeoutMs);
    child.on('close', (code) => { clearTimeout(timer); resolve({ code, out }); });
    child.on('error', (e) => { clearTimeout(timer); resolve({ code: null, out: `${e.message}` }); });
  });
}

// ───────── 슬롯 준비 ─────────

const GENERATED = new Set(['build', '.gradle', '.kotlin']);

/** 레포 runner/ → 슬롯 복사본 (바뀐 파일만 다시 쓴다). 내용 해시를 hash 에 누적한다. */
function syncTree(src, dst, hash, rel = '') {
  fs.mkdirSync(dst, { recursive: true });
  ownByRunner(dst);
  const keep = new Set();
  const entries = fs.readdirSync(src, { withFileTypes: true }).sort((a, b) => a.name.localeCompare(b.name));
  for (const ent of entries) {
    if (GENERATED.has(ent.name)) continue;
    keep.add(ent.name);
    const s = path.join(src, ent.name);
    const d = path.join(dst, ent.name);
    if (ent.isDirectory()) { syncTree(s, d, hash, `${rel}${ent.name}/`); continue; }
    if (!ent.isFile()) continue;
    const content = fs.readFileSync(s);
    hash.update(`${rel}${ent.name}\0`).update(content);
    let same = false;
    try { same = content.equals(fs.readFileSync(d)); } catch { /* 아직 없음 */ }
    if (!same) fs.writeFileSync(d, content);
    fs.chmodSync(d, fs.statSync(s).mode & 0o777);
    ownByRunner(d);
  }
  for (const ent of fs.readdirSync(dst, { withFileTypes: true })) {
    if (!GENERATED.has(ent.name) && !keep.has(ent.name)) fs.rmSync(path.join(dst, ent.name), { recursive: true, force: true });
  }
}

/** lib 폴더의 jar 를 정해진 순서로 나열한 클래스패스 (CDS 아카이브는 클래스패스가 같아야 쓰인다) */
function classpathOf(lib) {
  return fs.readdirSync(lib).filter((f) => f.endsWith('.jar')).sort().map((f) => path.join(lib, f)).join(path.delimiter);
}

/** CDS 아카이브를 만드는 예열 채점: 신뢰할 수 있는 editorial 풀이로 한 번 돌린다 */
async function buildArchive(lib) {
  const work = fs.mkdtempSync(path.join(RUNS_DIR, 'cds-'));
  ownByRunner(work);
  try {
    const jsa = path.join(work, 'app.jsa');
    const source = path.join(REPO_ROOT, 'problems', 'day-001', 'lv0', 'editorial', 'Solution.java');
    if (!fs.existsSync(source)) return false;
    await runProcess(JAVA, [
      `-XX:ArchiveClassesAtExit=${jsa}`, ...JVM_ARGS, ...runnerProps({
        user: 'warmup', day: '001', level: 0, cases: 'all', source, json: path.join(work, 'r.json'), timeScale: 1, outDir: path.join(work, 'out'),
      }),
      '-cp', classpathOf(lib), 'study.runner.Main',
    ], { cwd: work });
    if (!fs.existsSync(jsa)) return false;
    fs.copyFileSync(jsa, path.join(lib, 'app.jsa'));
    fs.chmodSync(path.join(lib, 'app.jsa'), 0o644);
    ownByRoot(path.join(lib, 'app.jsa'));
    return true;
  } finally {
    fs.rmSync(work, { recursive: true, force: true });
  }
}

/** 슬롯을 채점 가능한 상태로: 러너 소스가 바뀌었으면 Gradle 로 jar 를 다시 만들고 CDS 아카이브도 다시 만든다 */
async function ensureSlot(slot) {
  const slotDir = path.join(RUNNER_SLOTS_DIR, String(slot));
  fs.mkdirSync(slotDir, { recursive: true });
  ownByRoot(slotDir);
  fs.chmodSync(slotDir, 0o755);
  const project = path.join(slotDir, 'runner');
  const lib = path.join(slotDir, 'lib');
  const hash = crypto.createHash('sha256');
  syncTree(RUNNER_DIR, project, hash);
  const stamp = hash.digest('hex');
  const stampFile = path.join(lib, 'stamp');
  if (fs.existsSync(stampFile) && fs.readFileSync(stampFile, 'utf8') === stamp) return lib;

  // 클래스패스 만들기 (러너가 바뀌었을 때만, 일회용 Gradle — 데몬을 남기지 않는다)
  const gradlew = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
  const built = await runProcess(gradlew, ['runnerLib', '--no-daemon', '-q', '--console=plain'], { cwd: project });
  const libSrc = path.join(project, 'build', 'runner-lib');
  if (built.code !== 0 || !fs.existsSync(libSrc)) throw new Error(`러너 준비(Gradle) 실패\n${built.out.trim()}`);

  fs.rmSync(lib, { recursive: true, force: true });
  fs.mkdirSync(lib);
  ownByRoot(lib);
  fs.chmodSync(lib, 0o755);
  for (const f of fs.readdirSync(libSrc)) {
    fs.copyFileSync(path.join(libSrc, f), path.join(lib, f));
    fs.chmodSync(path.join(lib, f), 0o644);
    ownByRoot(path.join(lib, f));
  }
  const cds = await buildArchive(lib).catch(() => false);
  if (!cds) console.warn(`[채점 슬롯 ${slot}] CDS 아카이브를 만들지 못해 없이 실행합니다.`);
  fs.writeFileSync(stampFile, stamp);
  ownByRoot(stampFile);
  return lib;
}

/** 서버 시작 시 슬롯을 미리 준비해 첫 채점이 느리지 않게 한다 */
export async function prewarm() {
  for (let i = 0; i < RUNNER_SLOTS; i++) {
    const slot = await acquire();
    try {
      const t0 = Date.now();
      await ensureSlot(slot);
      console.log(`[채점 슬롯 ${slot}] 준비 완료 (${Date.now() - t0} ms)`);
    } catch (e) {
      console.warn(`[채점 슬롯 ${slot}] 준비 실패: ${e.message.split('\n')[0]}`);
    } finally {
      release(slot);
    }
  }
}

// ───────── 채점 ─────────

/** runner.* 시스템 프로퍼티 (Gradle 의 -P 값과 같은 이름) */
function runnerProps({ user, day, level, cases, source, json, timeScale, outDir }) {
  return [
    `-Drunner.root=${REPO_ROOT}`, `-Drunner.outDir=${outDir}`, `-Drunner.user=${user}`, `-Drunner.day=${day}`,
    `-Drunner.level=${level}`, `-Drunner.cases=${cases}`, `-Drunner.source=${source}`, `-Drunner.json=${json}`,
    `-Drunner.timeScale=${timeScale}`,
  ];
}

/**
 * 채점. code 를 임시 파일에 쓰고 study.runner.Main 으로 실행한다.
 * @returns {{ status: 'PASS'|'FAIL'|'ERROR', report: string, result: object|null }}
 */
export async function runTests({ login, day, level, code, basicOnly, timeScale = 1 }) {
  const slot = await acquire();
  try {
    let lib;
    try {
      lib = await ensureSlot(slot);
    } catch (e) {
      return { status: 'ERROR', report: `채점 준비 실패: ${e.message}`, result: null };
    }
    return await runInSlot(lib, { login, day, level, code, basicOnly, timeScale });
  } finally {
    release(slot);
  }
}

async function runInSlot(lib, { login, day, level, code, basicOnly, timeScale }) {
  const work = fs.mkdtempSync(path.join(RUNS_DIR, 'run-'));
  ownByRunner(work);
  try {
    const source = path.join(work, 'Solution.java');
    const jsonOut = path.join(work, 'result.json');
    fs.writeFileSync(source, code, { mode: 0o644 });
    const jsa = path.join(lib, 'app.jsa');
    const cds = fs.existsSync(jsa) ? [`-XX:SharedArchiveFile=${jsa}`, '-Xshare:auto', '-Xlog:cds=off', '-Xlog:cds+dynamic=off'] : [];
    const { code: exit, out } = await runProcess(JAVA, [
      ...cds, ...JVM_ARGS,
      ...runnerProps({ user: login, day, level, cases: basicOnly ? 'basic' : 'all', source, json: jsonOut, timeScale, outDir: path.join(work, 'out') }),
      '-cp', classpathOf(lib), 'study.runner.Main',
    ], { cwd: work });
    let result = null;
    try { result = JSON.parse(fs.readFileSync(jsonOut, 'utf8')); } catch { /* 러너가 결과를 못 남김 */ }
    const report = out.trim();
    return {
      status: result ? result.status : 'ERROR',
      report: result ? report : `러너 실행 실패 (exit ${exit})\n${report}`,
      result,
    };
  } finally {
    fs.rmSync(work, { recursive: true, force: true });
  }
}
