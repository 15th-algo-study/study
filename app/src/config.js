import fs from 'node:fs';
import path from 'node:path';

const here = path.dirname(new URL(import.meta.url).pathname);

/** 레포 루트. Docker 에서는 /workspace, 로컬 실행에서는 app/ 의 상위 폴더. */
export const REPO_ROOT = path.resolve(process.env.REPO_ROOT || path.join(here, '..', '..'));
/** 토큰·작성 중 코드·임시 파일. Docker 에서는 named volume. */
export const DATA_DIR = path.resolve(process.env.DATA_DIR || path.join(here, '..', 'data'));
export const PORT = Number(process.env.PORT || 15015);
/** 테스트용으로 바꿀 수 있는 GitHub 주소. */
export const GITHUB_WEB = (process.env.GITHUB_WEB_URL || 'https://github.com').replace(/\/$/, '');
export const GITHUB_API = (process.env.GITHUB_API_URL || 'https://api.github.com').replace(/\/$/, '');

export const TOTAL_DAYS = 150;

export function loadConfig() {
  const cfg = JSON.parse(fs.readFileSync(path.join(REPO_ROOT, 'config.json'), 'utf8'));
  cfg.holidays ??= [];
  cfg.timezone ??= 'Asia/Seoul';
  return cfg;
}

/** push·fetch 에 쓰는 원격 주소. 멤버의 clone 이 SSH 여도 앱은 항상 HTTPS + 토큰으로 접근한다. */
export function remoteUrl(cfg) {
  return process.env.GIT_REMOTE_URL || `${GITHUB_WEB}/${cfg.org}/${cfg.repo}.git`;
}

export function ensureDirs() {
  // tmp 는 임시 인덱스·러너 입력용이라 시작할 때 비운다 (비정상 종료로 남은 찌꺼기 제거)
  fs.rmSync(path.join(DATA_DIR, 'tmp'), { recursive: true, force: true });
  for (const d of ['drafts', 'tmp']) fs.mkdirSync(path.join(DATA_DIR, d), { recursive: true });
}
