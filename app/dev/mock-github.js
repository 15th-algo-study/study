// 개발·시연용 가짜 GitHub. Device Flow, /user, 레포 권한, PR 생성·조회만 흉내 낸다.
// 실행: MOCK_PORT=15099 MOCK_BARE_REPO=/path/origin.git node app/dev/mock-github.js
// - 여러 사용자: 인증 페이지에서 코드와 함께 아이디를 입력한다 (기본 demo-user). 'outsider' 로 시작하는 아이디는 레포 쓰기 권한이 없다.
// - "CI 머지" 흉내: POST /__admin/merge/<pr번호> → bare 레포에서 squash 머지(merge-tree)하고 브랜치를 지운다.
import express from 'express';
import { execFileSync } from 'node:child_process';

const PORT = Number(process.env.MOCK_PORT || 15099);
const BARE = process.env.MOCK_BARE_REPO;
const DEFAULT_LOGIN = process.env.MOCK_LOGIN || 'demo-user';

const flows = new Map(); // device_code → { user_code, login|null }
const state = { prs: [], nextPr: 1, nextCode: 1 };
const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use((req, res, next) => { console.log(`[mock] ${req.method} ${req.url}`); next(); });

const idOf = (login) => [...login].reduce((h, c) => (h * 31 + c.charCodeAt(0)) % 9_000_000, 7) + 100_000;
const userOf = (login) => ({ login, id: idOf(login), name: login, avatar_url: '' });

// ── Device Flow ──
app.post('/login/device/code', (req, res) => {
  const device_code = `dev-${Date.now()}-${Math.random().toString(36).slice(2)}`;
  // 첫 코드는 문서 캡처와 같은 ABCD-1234, 이후는 ABCD-0002 … 처럼 매번 다르게
  const n = state.nextCode++;
  const user_code = n === 1 ? 'ABCD-1234' : `ABCD-${String(n).padStart(4, '0')}`;
  flows.set(device_code, { user_code, login: null });
  // MOCK_VERIFICATION_URI 를 주면 화면에 그 주소를 보여 준다 (문서 캡처용). 승인은 여전히 이 서버의 /login/device 에서 한다.
  const verificationUri = process.env.MOCK_VERIFICATION_URI || `http://localhost:${PORT}/login/device`;
  res.json({ device_code, user_code, verification_uri: verificationUri, expires_in: 900, interval: 1 });
});
app.get('/login/device', (req, res) => {
  res.send(`<!doctype html><meta charset="utf-8"><title>mock GitHub — Device Activation</title>
  <body style="font-family:sans-serif;max-width:420px;margin:60px auto;text-align:center">
  <h2>(가짜) GitHub 기기 인증</h2><p>앱에 표시된 코드와 로그인할 아이디를 입력하세요.</p>
  <form method="post" action="/login/device/approve">
  <input name="code" placeholder="XXXX-XXXX" style="font-size:20px;padding:8px;text-align:center" required><br><br>
  <input name="login" placeholder="아이디" value="${DEFAULT_LOGIN}" style="font-size:16px;padding:6px;text-align:center"><br><br>
  <button style="font-size:16px;padding:8px 20px">Continue</button></form>
  ${req.query.ok ? `<p style="color:green">@${req.query.ok} 승인됨 ✔ — 앱으로 돌아가세요.</p>` : ''}</body>`);
});
app.post('/login/device/approve', (req, res) => {
  const code = (req.body.code || '').toUpperCase().replace(/\s/g, '');
  const login = (req.body.login || DEFAULT_LOGIN).trim();
  const flow = [...flows.values()].find((f) => f.user_code === code && !f.login);
  if (!flow) return res.status(400).send('코드가 틀리거나 이미 사용되었습니다. <a href="/login/device">다시</a>');
  flow.login = login;
  res.redirect(`/login/device?ok=${encodeURIComponent(login)}`);
});
app.post('/login/oauth/access_token', (req, res) => {
  if (req.body.grant_type === 'refresh_token') return res.json({ error: 'bad_refresh_token' });
  const flow = flows.get(req.body.device_code);
  if (!flow) return res.json({ error: 'expired_token', error_description: '알 수 없는 device_code' });
  if (!flow.login) return res.json({ error: 'authorization_pending', error_description: '아직 승인 전' });
  flows.delete(req.body.device_code);
  res.json({ access_token: `mock-token:${flow.login}`, token_type: 'bearer', scope: '' });
});

// ── REST ──
function auth(req, res, next) {
  const m = (req.headers.authorization || '').match(/^Bearer mock-token:([A-Za-z0-9-]+)$/);
  if (!m) return res.status(401).json({ message: 'Bad credentials' });
  req.login = m[1];
  next();
}
app.get('/user', auth, (req, res) => res.json(userOf(req.login)));
app.get('/repos/:org/:repo', auth, (req, res) => {
  const push = !req.login.startsWith('outsider');
  res.json({ full_name: `${req.params.org}/${req.params.repo}`, permissions: { admin: false, maintain: false, push, triage: push, pull: true } });
});
app.get('/repos/:org/:repo/pulls', auth, (req, res) => {
  const head = req.query.head ? String(req.query.head).split(':')[1] : null;
  res.json(state.prs.filter((p) => p.state === 'open' && (!head || p.head.ref === head)));
});
app.post('/repos/:org/:repo/pulls', auth, (req, res) => {
  const { title, head, base, body } = req.body;
  if (state.prs.some((p) => p.state === 'open' && p.head.ref === head)) return res.status(422).json({ message: 'A pull request already exists' });
  const number = state.nextPr++;
  const pr = { number, title, body, state: 'open', head: { ref: head }, base: { ref: base }, user: userOf(req.login), html_url: `http://localhost:${PORT}/${req.params.org}/${req.params.repo}/pull/${number}` };
  state.prs.push(pr);
  res.status(201).json(pr);
});
app.get('/:org/:repo/pull/:n', (req, res) => {
  const pr = state.prs.find((p) => p.number === Number(req.params.n));
  if (!pr) return res.status(404).send('없음');
  res.send(`<!doctype html><meta charset="utf-8"><body style="font-family:sans-serif;max-width:600px;margin:40px auto">
  <h2>#${pr.number} ${pr.title} <small>[${pr.state}]</small></h2><p>@${pr.user.login} · ${pr.head.ref} → ${pr.base.ref}</p><pre>${pr.body}</pre>
  ${pr.state === 'open' ? `<form method="post" action="/__admin/merge/${pr.number}"><button>CI 통과 → squash 머지 (흉내)</button></form>` : '<p>머지됨 ✔</p>'}</body>`);
});
// CI 머지 흉내: main 과 user 브랜치를 squash 머지한 커밋을 main 에 올리고 브랜치를 지운다
app.post('/__admin/merge/:n', (req, res) => {
  const pr = state.prs.find((p) => p.number === Number(req.params.n) && p.state === 'open');
  if (!pr) return res.status(404).send('열린 PR 이 없습니다');
  if (BARE) {
    const g = (args, env = {}) => execFileSync('git', ['-C', BARE, ...args], { env: { ...process.env, ...env } }).toString().trim();
    const tree = g(['merge-tree', '--write-tree', 'refs/heads/main', `refs/heads/${pr.head.ref}`]).split('\n')[0];
    const who = { GIT_AUTHOR_NAME: pr.user.login, GIT_AUTHOR_EMAIL: `${pr.user.id}+${pr.user.login}@users.noreply.github.com`, GIT_COMMITTER_NAME: 'mock', GIT_COMMITTER_EMAIL: 'mock@example.com' };
    const commit = g(['commit-tree', tree, '-p', 'refs/heads/main', '-m', `solve: @${pr.user.login} (#${pr.number})`], who);
    g(['update-ref', 'refs/heads/main', commit]);
    g(['update-ref', '-d', `refs/heads/${pr.head.ref}`]);
  }
  pr.state = 'closed'; pr.merged = true;
  res.redirect(pr.html_url);
});

app.listen(PORT, () => console.log(`mock GitHub: http://localhost:${PORT}  (bare: ${BARE || '없음'})`));
