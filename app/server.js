import express from 'express';
import path from 'node:path';
import { createRequire } from 'node:module';
import { DATA_DIR, ensureDirs, GITHUB_API, GITHUB_WEB, loadConfig, PORT, REPO_ROOT } from './src/config.js';
import { createRouter, HttpError } from './src/routes.js';
import { GitHubError } from './src/github.js';
import { GitError, isRepo } from './src/git.js';

const require = createRequire(import.meta.url);
const here = path.dirname(new URL(import.meta.url).pathname);

ensureDirs();
const app = express();
app.disable('x-powered-by');
app.use(express.json({ limit: '1mb' }));

// 프론트에 필요한 라이브러리는 node_modules 에서 직접 서빙한다 (번들러 없음, 오프라인 동작)
app.use('/vendor/monaco', express.static(path.dirname(require.resolve('monaco-editor/package.json')) + '/min', { maxAge: '7d' }));
app.use('/vendor/marked', express.static(path.dirname(require.resolve('marked/package.json')), { maxAge: '7d' }));
app.use(express.static(path.join(here, 'public')));

app.use('/api', createRouter());

app.use((err, req, res, next) => { // eslint-disable-line no-unused-vars
  const status = err instanceof HttpError ? err.status : err instanceof GitHubError ? 502 : err instanceof GitError ? 500 : 500;
  if (status >= 500) console.error(err);
  res.status(status).json({ error: err.message || String(err) });
});

const server = app.listen(PORT, () => {
  const cfg = loadConfig();
  console.log(`알고리즘 스터디 앱: http://localhost:${PORT}`);
  console.log(`  레포      : ${REPO_ROOT}${isRepo() ? '' : '  (git 저장소가 아닙니다 — 제출·동기화 불가)'}`);
  console.log(`  데이터    : ${DATA_DIR}`);
  console.log(`  GitHub    : ${GITHUB_WEB} / ${GITHUB_API} (${cfg.org}/${cfg.repo})`);
  if (!cfg.github_app_client_id) console.log('  경고      : config.json 의 github_app_client_id 가 비어 있어 로그인할 수 없습니다.');
  if (!cfg.start_date) console.log('  안내      : start_date 가 비어 있어 모든 일차가 잠겨 있습니다.');
});

server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(`\n포트 ${PORT} 를 다른 프로그램이 이미 쓰고 있습니다.`);
    console.error('다른 포트로 실행하려면 PORT 환경 변수를 바꾸세요. 예) PORT=15016 (Docker 는 docker-compose.yml 의 ports 를 수정)');
    process.exit(1);
  }
  throw err;
});
