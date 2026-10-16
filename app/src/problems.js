import fs from 'node:fs';
import path from 'node:path';
import { DATA_DIR, REPO_ROOT } from './config.js';

const PROBLEMS = path.join(REPO_ROOT, 'problems');

export function pad(day) {
  return String(day).padStart(3, '0');
}

export function problemDir(day, level) {
  return path.join(PROBLEMS, `day-${pad(day)}`, `lv${level}`);
}

export function solutionRelPath(login, day, level) {
  return `solutions/${login}/day-${pad(day)}/lv${level}/Solution.java`;
}

function readIf(p) {
  try { return fs.readFileSync(p, 'utf8'); } catch { return null; }
}

function titleOf(readme) {
  const m = readme?.match(/^#\s*\[lv\d\]\s*(.+)$/m);
  return m ? m[1].trim() : '(제목 없음)';
}

/** problems/ 폴더의 모든 문제: [{day, level, title}] */
export function listProblems() {
  if (!fs.existsSync(PROBLEMS)) return [];
  const out = [];
  for (const d of fs.readdirSync(PROBLEMS).sort()) {
    const dm = d.match(/^day-(\d{3})$/);
    if (!dm) continue;
    for (const l of fs.readdirSync(path.join(PROBLEMS, d)).sort()) {
      const lm = l.match(/^lv([0-3])$/);
      if (!lm) continue;
      out.push({ day: Number(dm[1]), level: Number(lm[1]), title: titleOf(readIf(path.join(PROBLEMS, d, l, 'README.md'))) });
    }
  }
  return out;
}

export function readProblem(day, level) {
  const dir = problemDir(day, level);
  if (!fs.existsSync(dir)) return null;
  return {
    readme: readIf(path.join(dir, 'README.md')) || '',
    template: readIf(path.join(dir, 'Solution.java')) || 'class Solution {\n}\n',
  };
}

export function readEditorial(day, level) {
  const dir = path.join(problemDir(day, level), 'editorial');
  return { readme: readIf(path.join(dir, 'README.md')) || '', code: readIf(path.join(dir, 'Solution.java')) || '' };
}

// ───────── 작성 중 코드 (볼륨에 자동 저장) ─────────

function draftPath(login, day, level) {
  return path.join(DATA_DIR, 'drafts', login, `day-${pad(day)}`, `lv${level}`, 'Solution.java');
}

export function readDraft(login, day, level) {
  return readIf(draftPath(login, day, level));
}

export function writeDraft(login, day, level, code) {
  const p = draftPath(login, day, level);
  fs.mkdirSync(path.dirname(p), { recursive: true });
  fs.writeFileSync(p, code);
}

export function hasDraft(login, day, level) {
  return fs.existsSync(draftPath(login, day, level));
}

/** 작업 트리의 내 풀이 (제출 시 저장됨) */
export function readLocalSolution(login, day, level) {
  return readIf(path.join(REPO_ROOT, solutionRelPath(login, day, level)));
}

export function writeLocalSolution(login, day, level, code) {
  const p = path.join(REPO_ROOT, solutionRelPath(login, day, level));
  fs.mkdirSync(path.dirname(p), { recursive: true });
  fs.writeFileSync(p, code);
}
