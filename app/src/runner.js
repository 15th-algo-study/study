import { spawn, spawnSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { REPO_ROOT, RUNNER_SLOTS, RUNNER_SLOTS_DIR, RUNNER_UID, RUNS_DIR } from './config.js';

const RUNNER_DIR = path.join(REPO_ROOT, 'runner');
const TIMEOUT_MS = 10 * 60 * 1000;

/*
 * 채점 슬롯. 같은 Gradle 프로젝트를 동시에 돌리면 build 폴더가 충돌하므로
 * 슬롯마다 build 폴더(-PrunnerBuildDir)와 프로젝트 캐시(--project-cache-dir)를 따로 쓴다.
 * 슬롯 수 = 동시에 진행되는 채점 수. 나머지 요청은 줄을 선다.
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

function prepareDir(dir) {
  fs.mkdirSync(dir, { recursive: true });
  if (dropPrivileges) fs.chownSync(dir, RUNNER_UID, RUNNER_UID);
}

/**
 * 러너 실행. code 를 임시 파일에 쓰고 -Psource 로 넘긴다.
 * @returns {{ status: 'PASS'|'FAIL'|'ERROR', report: string, result: object|null }}
 */
export async function runTests({ login, day, level, code, basicOnly, timeScale = 1 }) {
  const slot = await acquire();
  try {
    return await runInSlot(slot, { login, day, level, code, basicOnly, timeScale });
  } finally {
    release(slot);
  }
}

function runInSlot(slot, { login, day, level, code, basicOnly, timeScale }) {
  return new Promise((resolve) => {
    const slotDir = path.join(RUNNER_SLOTS_DIR, String(slot));
    prepareDir(slotDir);
    const work = fs.mkdtempSync(path.join(RUNS_DIR, 'run-'));
    if (dropPrivileges) fs.chownSync(work, RUNNER_UID, RUNNER_UID);
    const source = path.join(work, 'Solution.java');
    const jsonOut = path.join(work, 'result.json');
    fs.writeFileSync(source, code, { mode: 0o644 });

    const gradlew = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
    const args = [
      'test', `-Puser=${login}`, `-Pday=${day}`, `-Plevel=${level}`,
      `-Pcases=${basicOnly ? 'basic' : 'all'}`, `-Psource=${source}`, `-Pjson=${jsonOut}`, `-PtimeScale=${timeScale}`,
      `-PrunnerBuildDir=${path.join(slotDir, 'build')}`, `--project-cache-dir=${path.join(slotDir, 'gradle-project')}`,
      '-q', '--console=plain',
    ];
    const child = spawn(gradlew, args, {
      cwd: RUNNER_DIR,
      env: { ...process.env, TERM: 'dumb', ...(dropPrivileges ? { HOME: '/tmp' } : {}) },
      ...(dropPrivileges ? { uid: RUNNER_UID, gid: RUNNER_UID } : {}),
    });
    let out = '';
    child.stdout.on('data', (d) => { out += d; });
    child.stderr.on('data', (d) => { out += d; });
    const timer = setTimeout(() => child.kill('SIGKILL'), TIMEOUT_MS);
    const finish = (exit, startError) => {
      clearTimeout(timer);
      let result = null;
      try { result = JSON.parse(fs.readFileSync(jsonOut, 'utf8')); } catch { /* 러너가 결과를 못 남김 */ }
      fs.rmSync(work, { recursive: true, force: true });
      if (startError) {
        resolve({ status: 'ERROR', report: `러너를 시작할 수 없습니다: ${startError.message}`, result: null });
        return;
      }
      const report = out
        .split('\n')
        .filter((l) => !/^(FAILURE: Build failed|\* (What went wrong|Try|Get more help)|> (Check|Run with|Get more|채점 실패)|Execution failed|BUILD (FAILED|SUCCESSFUL)|\d+ actionable task|\d+ tests? completed)/.test(l))
        .join('\n').trim();
      resolve({
        status: result ? result.status : 'ERROR',
        report: result ? report : `러너 실행 실패 (exit ${exit})\n${report}`,
        result,
      });
    };
    child.on('close', (exit) => finish(exit, null));
    child.on('error', (e) => finish(null, e));
  });
}
