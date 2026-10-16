import { spawn } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { DATA_DIR, REPO_ROOT } from './config.js';

const RUNNER_DIR = path.join(REPO_ROOT, 'runner');
const TIMEOUT_MS = 10 * 60 * 1000;

/** Gradle 은 같은 프로젝트에서 동시에 돌리면 락 경합이 나므로 한 번에 하나씩 실행한다. */
let queue = Promise.resolve();
export function enqueue(task) {
  const next = queue.then(task, task);
  queue = next.catch(() => {});
  return next;
}

/**
 * 러너 실행. code 를 임시 파일에 쓰고 -Psource 로 넘긴다.
 * @returns {{ status: 'PASS'|'FAIL'|'ERROR', report: string, result: object|null }}
 */
export function runTests({ login, day, level, code, basicOnly, timeScale = 1 }) {
  return enqueue(() => new Promise((resolve) => {
    const work = fs.mkdtempSync(path.join(DATA_DIR, 'tmp', 'run-'));
    const source = path.join(work, 'Solution.java');
    const jsonOut = path.join(work, 'result.json');
    fs.writeFileSync(source, code);
    const gradlew = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
    const args = [
      'test', `-Puser=${login}`, `-Pday=${day}`, `-Plevel=${level}`,
      `-Pcases=${basicOnly ? 'basic' : 'all'}`, `-Psource=${source}`, `-Pjson=${jsonOut}`, `-PtimeScale=${timeScale}`,
      '-q', '--console=plain',
    ];
    const child = spawn(gradlew, args, { cwd: RUNNER_DIR, env: { ...process.env, TERM: 'dumb' } });
    let out = '';
    child.stdout.on('data', (d) => { out += d; });
    child.stderr.on('data', (d) => { out += d; });
    const timer = setTimeout(() => child.kill('SIGKILL'), TIMEOUT_MS);
    child.on('close', (code) => {
      clearTimeout(timer);
      let result = null;
      try { result = JSON.parse(fs.readFileSync(jsonOut, 'utf8')); } catch { /* 러너가 결과를 못 남김 */ }
      fs.rmSync(work, { recursive: true, force: true });
      const report = out
        .split('\n')
        .filter((l) => !/^(FAILURE: Build failed|\* (What went wrong|Try|Get more help)|> (Check|Run with|Get more|채점 실패)|Execution failed|BUILD (FAILED|SUCCESSFUL)|\d+ actionable task|\d+ tests? completed)/.test(l))
        .join('\n').trim();
      resolve({
        status: result ? result.status : 'ERROR',
        report: result ? report : `러너 실행 실패 (exit ${code})\n${report}`,
        result,
      });
    });
    child.on('error', (e) => {
      clearTimeout(timer);
      fs.rmSync(work, { recursive: true, force: true });
      resolve({ status: 'ERROR', report: `러너를 시작할 수 없습니다: ${e.message}`, result: null });
    });
  }));
}
