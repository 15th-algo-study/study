// 개발·시연용 가짜 GitHub. Device Flow, /user, PR 생성·조회만 흉내 낸다.
// 실행: MOCK_PORT=9999 MOCK_BARE_REPO=/path/origin.git node app/dev/mock-github.js
// "CI 머지" 흉내: POST /__admin/merge/<pr번호> → bare 레포의 main 을 user 브랜치로 옮기고 브랜치를 지운다.
import express from 'express';
import { execFileSync } from 'node:child_process';

const PORT = Number(process.env.MOCK_PORT || 15099);
const BARE = process.env.MOCK_BARE_REPO;
const USER = { login: process.env.MOCK_LOGIN || 'demo-user', id: 424242, name: '데모 사용자', avatar_url: '' };

const state = { approved: false, deviceCode: null, prs: [], nextPr: 1 };
const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use((req, res, next) => { console.log(`[mock] ${req.method} ${req.url}`); next(); });

// ── Device Flow ──
app.post('/login/device/code', (req, res) => {
  state.approved = false;
  state.deviceCode = `dev-${Date.now()}`;
  // MOCK_VERIFICATION_URI 를 주면 화면에 그 주소를 보여 준다 (문서 캡처용). 승인은 여전히 이 서버의 /login/device 에서 한다.
  const verificationUri = process.env.MOCK_VERIFICATION_URI || `http://localhost:${PORT}/login/device`;
  res.json({ device_code: state.deviceCode, user_code: 'ABCD-1234', verification_uri: verificationUri, expires_in: 900, interval: 1 });
});
app.get('/login/device', (req, res) => {
  res.send(`<!doctype html><meta charset="utf-8"><title>mock GitHub — Device Activation</title>
  <body style="font-family:sans-serif;max-width:420px;margin:60px auto;text-align:center">
  <h2>(가짜) GitHub 기기 인증</h2><p>앱에 표시된 코드를 입력하세요.</p>
  <form method="post" action="/login/device/approve"><input name="code" placeholder="XXXX-XXXX" style="font-size:20px;padding:8px;text-align:center" required>
  <br><br><button style="font-size:16px;padding:8px 20px">Continue</button></form>
  ${state.approved ? '<p style="color:green">승인됨 ✔ — 앱으로 돌아가세요.</p>' : ''}</body>`);
});
app.post('/login/device/approve', (req, res) => {
  if ((req.body.code || '').toUpperCase().replace(/\s/g, '') !== 'ABCD-1234') return res.status(400).send('코드가 틀립니다. <a href="/login/device">다시</a>');
  state.approved = true;
  res.redirect('/login/device');
});
app.post('/login/oauth/access_token', (req, res) => {
  if (req.body.grant_type === 'refresh_token') return res.json({ access_token: 'mock-token-refreshed', token_type: 'bearer', scope: '' });
  if (req.body.device_code !== state.deviceCode) return res.json({ error: 'expired_token', error_description: '알 수 없는 device_code' });
  if (!state.approved) return res.json({ error: 'authorization_pending', error_description: '아직 승인 전' });
  res.json({ access_token: 'mock-token', token_type: 'bearer', scope: '' });
});

// ── REST ──
function auth(req, res, next) {
  if (!/^Bearer mock-token/.test(req.headers.authorization || '')) return res.status(401).json({ message: 'Bad credentials' });
  next();
}
app.get('/user', auth, (req, res) => res.json(USER));
app.get('/repos/:org/:repo/pulls', auth, (req, res) => {
  const head = req.query.head ? String(req.query.head).split(':')[1] : null;
  res.json(state.prs.filter((p) => p.state === 'open' && (!head || p.head.ref === head)));
});
app.post('/repos/:org/:repo/pulls', auth, (req, res) => {
  const { title, head, base, body } = req.body;
  if (state.prs.some((p) => p.state === 'open' && p.head.ref === head)) return res.status(422).json({ message: 'A pull request already exists' });
  const number = state.nextPr++;
  const pr = { number, title, body, state: 'open', head: { ref: head }, base: { ref: base }, user: USER, html_url: `http://localhost:${PORT}/${req.params.org}/${req.params.repo}/pull/${number}` };
  state.prs.push(pr);
  res.status(201).json(pr);
});
app.get('/:org/:repo/pull/:n', (req, res) => {
  const pr = state.prs.find((p) => p.number === Number(req.params.n));
  if (!pr) return res.status(404).send('없음');
  res.send(`<!doctype html><meta charset="utf-8"><body style="font-family:sans-serif;max-width:600px;margin:40px auto">
  <h2>#${pr.number} ${pr.title} <small>[${pr.state}]</small></h2><p>${pr.head.ref} → ${pr.base.ref}</p><pre>${pr.body}</pre>
  ${pr.state === 'open' ? `<form method="post" action="/__admin/merge/${pr.number}"><button>CI 통과 → squash 머지 (흉내)</button></form>` : '<p>머지됨 ✔</p>'}</body>`);
});
// CI 머지 흉내: main ← user 브랜치 (내용이 같으므로 fast-forward 로 대신한다), 브랜치 삭제
app.post('/__admin/merge/:n', (req, res) => {
  const pr = state.prs.find((p) => p.number === Number(req.params.n) && p.state === 'open');
  if (!pr) return res.status(404).send('열린 PR 이 없습니다');
  if (BARE) {
    execFileSync('git', ['-C', BARE, 'update-ref', 'refs/heads/main', `refs/heads/${pr.head.ref}`]);
    execFileSync('git', ['-C', BARE, 'update-ref', '-d', `refs/heads/${pr.head.ref}`]);
  }
  pr.state = 'closed'; pr.merged = true;
  res.redirect(pr.html_url);
});

app.listen(PORT, () => console.log(`mock GitHub: http://localhost:${PORT}  (bare: ${BARE || '없음'})`));
