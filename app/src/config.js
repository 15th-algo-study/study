import fs from 'node:fs';
import path from 'node:path';

const here = path.dirname(new URL(import.meta.url).pathname);

/** 레포 루트. Docker 에서는 /workspace, 로컬 실행에서는 app/ 의 상위 폴더. */
export const REPO_ROOT = path.resolve(process.env.REPO_ROOT || path.join(here, '..', '..'));
/** 토큰·작성 중 코드·세션. Docker 에서는 named volume. */
export const DATA_DIR = path.resolve(process.env.DATA_DIR || path.join(here, '..', 'data'));
export const PORT = Number(process.env.PORT || 15015);
/** 테스트용으로 바꿀 수 있는 GitHub 주소. */
export const GITHUB_WEB = (process.env.GITHUB_WEB_URL || 'https://github.com').replace(/\/$/, '');
export const GITHUB_API = (process.env.GITHUB_API_URL || 'https://api.github.com').replace(/\/$/, '');

/**
 * 다중 사용자(서버) 모드. 켜면
 * - 브라우저마다 쿠키 세션, 사용자마다 토큰·작성 중 코드를 따로 둔다
 * - 레포 쓰기 권한이 있는 사람(스터디 멤버)만 로그인할 수 있다
 * - 로그인해야 실행·제출할 수 있고, 작업 트리에 풀이 파일을 쓰지 않는다
 */
export const MULTI_USER = process.env.MULTI_USER === '1';
/** 동시에 돌릴 채점 수. 채점 하나에 메모리 약 1.5 GB. */
export const RUNNER_SLOTS = Math.max(1, Number(process.env.RUNNER_SLOTS || 1));
/** 지정하면(그리고 root 로 실행 중이면) 제출 코드를 이 uid 로 실행해 /data 의 토큰을 읽지 못하게 한다. */
export const RUNNER_UID = process.env.RUNNER_UID ? Number(process.env.RUNNER_UID) : null;
/** 채점 입력·결과 임시 폴더, 슬롯별 Gradle build 폴더. RUNNER_UID 가 쓸 수 있어야 한다. */
export const RUNS_DIR = path.resolve(process.env.RUNS_DIR || path.join(DATA_DIR, 'tmp'));
export const RUNNER_SLOTS_DIR = path.resolve(process.env.RUNNER_SLOTS_DIR || path.join(DATA_DIR, 'runner-slots'));
/** origin/main 을 자동으로 받아 오는 주기(분). 0 이면 끈다. 서버 모드 기본 5분. */
export const SYNC_INTERVAL_MIN = Number(process.env.SYNC_INTERVAL_MIN ?? (MULTI_USER ? 5 : 0));
/**
 * Google Analytics 4 측정 ID (G-XXXXXXXXXX). 서버 배포(deploy/.env)에서만 켠다.
 * 로컬 모드이거나 비어 있거나 형식이 틀리면 null → 프론트가 GA 를 불러오지 않는다.
 */
const GA_ENV = process.env.GA_MEASUREMENT_ID || '';
export const GA_MEASUREMENT_ID = MULTI_USER && /^G-[A-Z0-9]{4,20}$/.test(GA_ENV) ? GA_ENV : null;

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
  // 채점 임시 폴더는 시작할 때 비운다 (비정상 종료로 남은 찌꺼기 제거)
  fs.rmSync(RUNS_DIR, { recursive: true, force: true });
  fs.mkdirSync(RUNS_DIR, { recursive: true });
  fs.mkdirSync(RUNNER_SLOTS_DIR, { recursive: true });
  for (const d of ['drafts', 'sessions', 'users']) fs.mkdirSync(path.join(DATA_DIR, d), { recursive: true });
}
