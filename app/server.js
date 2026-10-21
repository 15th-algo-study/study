import express from 'express';
import path from 'node:path';
import { createRequire } from 'node:module';
import {
  DATA_DIR, ensureDirs, GITHUB_API, GITHUB_WEB, loadConfig, MULTI_USER, PORT, remoteUrl, REPO_ROOT,
  RUNNER_SLOTS, RUNNER_UID, SYNC_INTERVAL_MIN,
} from './src/config.js';
import { createRouter, HttpError } from './src/routes.js';
import { GitHubError } from './src/github.js';
import { GitError, isRepo, syncWorkingTree } from './src/git.js';
import { pruneSessions, sessionMiddleware } from './src/auth.js';
import { checkRunnerIsolation, prewarm } from './src/runner.js';

const require = createRequire(import.meta.url);
const here = path.dirname(new URL(import.meta.url).pathname);

ensureDirs();
pruneSessions();
const app = express();
app.disable('x-powered-by');
// 서버 모드는 Caddy 같은 리버스 프록시 뒤에서 HTTPS 로 동작한다 (req.secure → 쿠키 Secure)
if (MULTI_USER) app.set('trust proxy', 1);
app.use(express.json({ limit: '1mb' }));

// 헬스 체크 (Docker healthcheck·서버 워치독용): 이벤트 루프가 살아 있고 레포 설정을 읽을 수 있으면 200
app.get('/healthz', (req, res) => {
  try {
    loadConfig();
    res.json({ ok: true, uptimeSec: Math.round(process.uptime()) });
  } catch (e) {
    res.status(503).json({ ok: false, error: e.message });
  }
});

// 프론트에 필요한 라이브러리는 node_modules 에서 직접 서빙한다 (번들러 없음, 오프라인 동작)
app.use('/vendor/monaco', express.static(path.dirname(require.resolve('monaco-editor/package.json')) + '/min', { maxAge: '7d' }));
app.use('/vendor/marked', express.static(path.dirname(require.resolve('marked/package.json')), { maxAge: '7d' }));
app.use(express.static(path.join(here, 'public')));

app.use('/api', sessionMiddleware, createRouter());

app.use((err, req, res, next) => { // eslint-disable-line no-unused-vars
  const status = err instanceof HttpError ? err.status : err instanceof GitHubError ? 502 : err instanceof GitError ? 500 : 500;
  if (status >= 500) console.error(err);
  res.status(status).json({ error: err.message || String(err) });
});

/** 새 문제·다른 사람 풀이를 주기적으로 받아 온다 (서버 모드 기본 5분). 공개 레포라 토큰이 필요 없다. */
function startAutoSync() {
  if (!SYNC_INTERVAL_MIN || !isRepo()) return;
  const tick = async () => {
    try {
      const r = await syncWorkingTree({ url: remoteUrl(loadConfig()), token: null, login: null, cleanUntracked: false });
      if (r.changed || !r.ok) console.log(`[자동 동기화] ${r.message}`);
    } catch (e) {
      console.warn(`[자동 동기화] 실패: ${e.message}`);
    }
  };
  tick();
  setInterval(tick, SYNC_INTERVAL_MIN * 60_000).unref();
}

const server = app.listen(PORT, () => {
  const cfg = loadConfig();
  console.log(`알고리즘 스터디 앱: http://localhost:${PORT}`);
  console.log(`  모드      : ${MULTI_USER ? `서버(다중 사용자) · 채점 슬롯 ${RUNNER_SLOTS}개${RUNNER_UID !== null ? ` · 제출 코드 uid ${RUNNER_UID}` : ''}` : '로컬(단일 사용자)'}`);
  console.log(`  레포      : ${REPO_ROOT}${isRepo() ? '' : '  (git 저장소가 아닙니다 — 제출·동기화 불가)'}`);
  console.log(`  데이터    : ${DATA_DIR}`);
  console.log(`  GitHub    : ${GITHUB_WEB} / ${GITHUB_API} (${cfg.org}/${cfg.repo})`);
  if (SYNC_INTERVAL_MIN) console.log(`  자동 동기화: ${SYNC_INTERVAL_MIN}분마다 origin/main`);
  if (!cfg.github_app_client_id) console.log('  경고      : config.json 의 github_app_client_id 가 비어 있어 로그인할 수 없습니다.');
  if (!cfg.start_date) console.log('  안내      : start_date 가 비어 있어 모든 일차가 잠겨 있습니다.');
  const isolation = checkRunnerIsolation();
  if (isolation) console.warn(`  ${isolation}`);
  startAutoSync();
  // 채점 슬롯을 미리 준비 (러너 jar·CDS 아카이브). 끝나기 전에 들어온 채점은 줄을 서서 기다린다.
  prewarm();
});

server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(`\n포트 ${PORT} 를 다른 프로그램이 이미 쓰고 있습니다.`);
    console.error('다른 포트로 실행하려면 PORT 환경 변수를 바꾸세요. 예) PORT=15016 (Docker 는 docker-compose.yml 의 ports 를 수정)');
    process.exit(1);
  }
  throw err;
});
