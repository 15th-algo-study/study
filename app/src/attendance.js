import { spawn } from 'node:child_process';
import path from 'node:path';
import { REPO_ROOT } from './config.js';
import { mergedPaths } from './git.js';

function runPython(cmd, args, input) {
  return new Promise((resolve, reject) => {
    const child = spawn(cmd, args, { cwd: REPO_ROOT, stdio: ['pipe', 'pipe', 'pipe'] });
    const out = [], err = [];
    child.stdout.on('data', (d) => out.push(d));
    child.stderr.on('data', (d) => err.push(d));
    child.on('error', reject);
    child.on('close', (code) => {
      if (code === 0) resolve(Buffer.concat(out).toString('utf8'));
      else reject(new Error(Buffer.concat(err).toString('utf8').trim() || `exit ${code}`));
    });
    child.stdin.end(input);
  });
}

/** scripts/attendance.py 를 origin/main 의 solutions 목록으로 실행해 JSON 을 받는다 (README 와 같은 규칙). */
export async function attendanceData() {
  const paths = await mergedPaths();
  const script = path.join(REPO_ROOT, 'scripts', 'attendance.py');
  let lastError;
  for (const py of ['python3', 'python']) {
    try {
      return JSON.parse(await runPython(py, ['-I', script, '--root', REPO_ROOT, '--json', '-', '--paths', '-'], paths));
    } catch (e) {
      lastError = e;
      if (e.code !== 'ENOENT') break; // 파이썬은 있는데 스크립트가 실패한 경우
    }
  }
  throw new Error(`출석표 계산 실패: ${lastError?.message}`);
}
