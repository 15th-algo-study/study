/* 데일리 알고리즘 스터디 — 프론트 (번들러 없음, ES 모듈) */

const $ = (sel, el = document) => el.querySelector(sel);
const view = $('#view');
const LV_LABEL = ['lv0', 'lv1', 'lv2', 'lv3'];
const STATUS_LABEL = { merged: '머지됨', submitted: 'PR 대기', draft: '작성 중', none: '' };

let state = null;        // GET /api/state
let editor = null;       // Monaco 인스턴스
let current = null;      // 열려 있는 문제 {day, level, data}
let draftTimer = null;
let monacoReady = null;

// ───────────── 공용 ─────────────
async function api(path, opts = {}) {
  const res = await fetch(`/api${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...opts,
    body: opts.body !== undefined ? JSON.stringify(opts.body) : undefined,
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || `요청 실패 (${res.status})`);
  return data;
}

function toast(msg, kind = '') {
  const t = $('#toast');
  t.textContent = msg;
  t.className = `toast ${kind}`;
  t.hidden = false;
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => { t.hidden = true; }, kind === 'bad' ? 6000 : 3000);
}

function esc(s) {
  return String(s ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function pad(n) { return String(n).padStart(3, '0'); }

function md(src) {
  return marked.parse(src || '', { gfm: true, breaks: false });
}

function loadMonaco() {
  if (monacoReady) return monacoReady;
  monacoReady = new Promise((resolve) => {
    require.config({ paths: { vs: '/vendor/monaco/vs' } });
    require(['vs/editor/editor.main'], () => resolve(window.monaco));
  });
  return monacoReady;
}

async function refreshState() {
  state = await api('/state');
  renderTopbar();
  return state;
}

// ───────────── 방문 통계 (GA) ─────────────
// 서버 배포에서 측정 ID 를 줬을 때만 켜진다 (state.gaId, 로컬 모드는 항상 null).
// GitHub 로그인 이름처럼 사람을 알아볼 수 있는 값은 보내지 않는다.
let gaOn = false;
let lastPage = null;

function initAnalytics(id) {
  if (gaOn || !id) return;
  gaOn = true;
  window.dataLayer = window.dataLayer || [];
  window.gtag = function gtag() { window.dataLayer.push(arguments); };
  window.gtag('js', new Date());
  window.gtag('config', id, { send_page_view: false });   // 해시 라우팅이라 page_view 는 trackPage 가 직접 보낸다
  const s = document.createElement('script');
  s.async = true;
  s.src = `https://www.googletagmanager.com/gtag/js?id=${encodeURIComponent(id)}`;
  document.head.appendChild(s);
}

function track(name, params) {
  if (gaOn) window.gtag('event', name, params);
}

/** GA 는 # 뒤를 페이지로 구분하지 않으므로 #/day/1/0 → /day/001/lv0 처럼 경로로 바꿔 보낸다. */
function trackPage(hash) {
  if (hash === lastPage) return;   // 로그인·로그아웃 뒤 같은 화면을 다시 그릴 때는 세지 않는다
  lastPage = hash;
  const m = hash.match(/^#\/day\/(\d+)\/([0-3])$/);
  const [page, title] = m ? [`/day/${pad(m[1])}/lv${m[2]}`, `${Number(m[1])}일차 lv${m[2]}`]
    : hash.startsWith('#/attendance') ? ['/attendance', '출석표'] : ['/', '일차 목록'];
  track('page_view', { page_location: location.origin + page, page_title: title });
}

/** 채점 응답 → 'pass' | 'fail' | 'error'(러너 오류) */
function outcome(grade) {
  if (!grade?.result) return 'error';
  return grade.result.status === 'PASS' ? 'pass' : 'fail';
}

// ───────────── 상단바 · 로그인 ─────────────
function renderTopbar() {
  const info = $('#dayinfo');
  if (!state.startDate) info.textContent = '시작 전 (start_date 미설정)';
  else if (state.todayDay === 0) info.textContent = `${state.startDate} 시작 예정`;
  else info.textContent = `${state.todayDay}일차 / ${state.totalDays}일 · ${state.today}`;

  const box = $('#userbox');
  if (state.user) {
    box.innerHTML = `${state.user.avatar_url ? `<img src="${esc(state.user.avatar_url)}" alt="">` : ''}<span>@${esc(state.user.login)}</span><button class="ghost" id="logoutBtn">로그아웃</button>`;
    $('#logoutBtn').onclick = async () => { await api('/auth/logout', { method: 'POST' }); await refreshState(); route(); };
  } else {
    box.innerHTML = '<button class="primary" id="loginBtn">GitHub 로그인</button>';
    $('#loginBtn').onclick = openLogin;
  }
}

async function openLogin() {
  const dlg = $('#loginDialog');
  const body = $('#loginBody');
  body.innerHTML = '<p class="muted">코드를 발급받는 중…</p>';
  dlg.showModal();
  let stop = false;
  $('#loginClose').onclick = () => { stop = true; dlg.close(); };
  try {
    const d = await api('/auth/device', { method: 'POST' });
    body.innerHTML = `
      <p>아래 코드를 GitHub 기기 인증 페이지에 입력하세요.</p>
      <div class="usercode" id="userCode">${esc(d.user_code)}</div>
      <div class="row">
        <a class="primary" href="${esc(d.verification_uri)}" target="_blank" rel="noopener"><button class="primary">${esc(d.verification_uri)} 열기</button></a>
        <button id="copyCode" class="ghost">코드 복사</button>
      </div>
      <p class="muted" id="pollStatus" style="margin-top:12px"><span class="spinner"></span> 승인을 기다리는 중… (${Math.round(d.expires_in / 60)}분 안에 입력)</p>`;
    $('#copyCode').onclick = () => navigator.clipboard.writeText(d.user_code).then(() => toast('복사했습니다'));
    let interval = (d.interval || 5) * 1000;
    while (!stop) {
      await new Promise((r) => setTimeout(r, interval));
      if (stop) break;
      const p = await api('/auth/poll', { method: 'POST' });
      if (p.status === 'ok') {
        dlg.close();
        toast(`@${p.user.login} 로 로그인했습니다`, 'ok');
        await refreshState();
        route();
        return;
      }
      if (p.status === 'pending') { if (p.interval) interval = p.interval * 1000; continue; }
      $('#pollStatus').innerHTML = `<span style="color:var(--bad)">${esc(p.status === 'expired' ? '코드가 만료되었습니다. 다시 시도하세요.' : p.message || p.status)}</span>`;
      return;
    }
  } catch (e) {
    body.innerHTML = `<p style="color:var(--bad)">${esc(e.message)}</p>`;
  }
}

$('#syncBtn').onclick = async () => {
  const btn = $('#syncBtn');
  btn.disabled = true; btn.textContent = '동기화 중…';
  try {
    const r = await api('/sync', { method: 'POST' });
    toast(r.message, r.ok ? 'ok' : 'bad');
    await refreshState();
    route();
  } catch (e) { toast(e.message, 'bad'); } finally { btn.disabled = false; btn.textContent = '동기화'; }
};

// ───────────── 일차 목록 ─────────────
function renderDays() {
  const unlocked = state.days.filter((d) => d.unlocked);
  const locked = state.days.filter((d) => !d.unlocked);
  let notice = '';
  if (!state.repo) {
    notice += `<div class="notice">⚠️ 이 폴더는 아직 git 저장소가 아닙니다 (ZIP 으로 내려받은 경우). 문제는 볼 수 있지만 제출·동기화는 되지 않습니다.
      ${state.user ? '<button id="repoInitBtn" class="primary" style="margin-left:8px">GitHub 저장소와 연결</button>' : '<b>GitHub 로그인</b> 후 여기서 연결할 수 있습니다.'}</div>`;
  }
  if (!state.startDate) notice += '<div class="notice">스터디 시작 전입니다. 호스트가 <code>config.json</code> 의 <code>start_date</code> 를 채우면 일차가 열립니다.</div>';
  else if (!state.user && state.multiUser && state.anonRun) notice += '<div class="notice">문제 풀이와 <b>실행</b>은 로그인 없이 할 수 있습니다 (작성한 코드는 저장되지 않습니다). <b>제출</b>하려면 오른쪽 위에서 GitHub 로그인을 해 주세요 (스터디 멤버만 가능).</div>';
  else if (!state.user && state.multiUser) notice += '<div class="notice">문제는 로그인 없이 볼 수 있습니다. <b>실행·제출</b>하려면 오른쪽 위에서 GitHub 로그인을 해 주세요 (스터디 멤버만 가능).</div>';
  else if (!state.user) notice += '<div class="notice">문제는 로그인 없이 풀어볼 수 있습니다. <b>제출</b>하려면 오른쪽 위에서 GitHub 로그인을 해 주세요.</div>';

  const card = (d) => `
    <a class="daycard ${d.unlocked ? '' : 'locked'}" href="#/day/${d.day}/0" ${d.unlocked ? '' : 'tabindex="-1"'}>
      <div class="head"><span class="num">${d.day}일차</span><span class="muted">${d.date ? d.date.slice(5) : ''}${d.unlocked ? '' : ' · 🔒'}</span></div>
      <div class="lvrow">${d.levels.map((l) => `<span class="pill ${l.status}" title="${esc(l.title)} · ${l.solvers}명 풀이">${LV_LABEL[l.level]}</span>`).join('')}</div>
    </a>`;

  view.innerHTML = `<div class="page">
    ${notice}
    <div class="daygrid">${[...unlocked].reverse().map(card).join('')}</div>
    ${locked.length ? `<h3 class="muted" style="margin-top:28px">잠김 (${locked.length}일)</h3><div class="daygrid">${locked.slice(0, 10).map(card).join('')}</div>` : ''}
    <div class="legend muted"><span class="pill merged">lv</span> 머지됨 &nbsp; <span class="pill submitted">lv</span> PR 대기 &nbsp; <span class="pill draft">lv</span> 작성 중 &nbsp; <span class="pill">lv</span> 미시작</div>
  </div>`;
  const initBtn = $('#repoInitBtn');
  if (initBtn) initBtn.onclick = async () => {
    initBtn.disabled = true; initBtn.textContent = '연결 중…';
    try { const r = await api('/repo/init', { method: 'POST' }); toast(r.message, 'ok'); await refreshState(); route(); }
    catch (e) { toast(e.message, 'bad'); initBtn.disabled = false; initBtn.textContent = 'GitHub 저장소와 연결'; }
  };
}

// ───────────── 문제 화면 ─────────────
async function renderProblem(day, level) {
  const dayInfo = state.days.find((d) => d.day === day);
  if (!dayInfo) { view.innerHTML = '<div class="page">없는 일차입니다.</div>'; return; }
  if (!dayInfo.unlocked) { view.innerHTML = `<div class="page"><div class="notice">🔒 ${day}일차는 아직 해금되지 않았습니다.${dayInfo.date ? ` (${dayInfo.date})` : ''}</div></div>`; return; }

  flushDraft();
  const data = await api(`/problem/${day}/${level}`);
  current = { day, level, data };

  view.innerHTML = `<div class="problem">
    <div class="problem-bar">
      <a href="#/" class="muted">← 일차 목록</a>
      <b>${day}일차</b>
      <span class="lvtabs">${dayInfo.levels.map((l) => `<a href="#/day/${day}/${l.level}" class="${l.level === level ? 'active' : ''}">${LV_LABEL[l.level]}<span class="dot ${l.status}"></span></a>`).join('')}</span>
      <span class="spacer"></span>
      <span class="muted" id="statusLabel">${STATUS_LABEL[data.status] || ''}</span>
      <button id="runBtn" ${canRun() ? '' : 'disabled title="로그인이 필요합니다"'}>▶ 실행 <span class="muted">(basic)</span></button>
      <button id="submitBtn" class="primary" ${state.user ? '' : 'disabled title="로그인이 필요합니다"'}>제출 <span style="opacity:.8">(전체 케이스 → PR)</span></button>
    </div>
    <div class="split">
      <section class="left">
        <div class="subtabs">
          <button class="active" data-tab="readme">문제</button>
          <button data-tab="solutions">풀이 보기 <span class="muted">(${data.solvers.length})</span></button>
          <button data-tab="editorial">해설</button>
        </div>
        <div id="leftBody" class="md">${md(data.readme)}</div>
      </section>
      <section class="right">
        <div class="editor-wrap">
          <div class="editor-tools">
            <span class="muted" id="saveState" style="font-size:12px;align-self:center"></span>
            <button id="resetBtn" title="템플릿으로 되돌립니다">초기화</button>
          </div>
          <div id="editor"></div>
        </div>
        <div class="result">
          <div class="result-bar"><b>실행 결과</b><span id="resultSummary" class="muted">실행 또는 제출을 누르면 결과가 표시됩니다.</span></div>
          <div class="result-body" id="resultBody"></div>
        </div>
      </section>
    </div>
  </div>`;

  const monaco = await loadMonaco();
  if (editor) { editor.dispose(); editor = null; }
  editor = monaco.editor.create($('#editor'), {
    value: data.code, language: 'java', theme: 'vs-dark', automaticLayout: true,
    fontSize: 14, minimap: { enabled: false }, tabSize: 4, insertSpaces: true, scrollBeyondLastLine: false,
  });
  editor.onDidChangeModelContent(() => scheduleDraft());
  if (!canSave()) $('#saveState').textContent = '로그인하면 자동 저장됩니다';
  if (data.codeSource !== 'draft' && data.codeSource !== 'template') toast(`${data.codeSource === 'merged' ? '머지된' : '제출한'} 풀이를 불러왔습니다`);

  $('#resetBtn').onclick = () => { if (confirm('작성 중인 코드를 템플릿으로 되돌릴까요?')) editor.setValue(data.template); };
  $('#runBtn').onclick = () => runOrSubmit(false);
  $('#submitBtn').onclick = () => runOrSubmit(true);
  for (const b of view.querySelectorAll('.subtabs button')) b.onclick = () => showLeftTab(b.dataset.tab);
}

/** 실행(basic): 로컬은 항상, 서버는 호스트가 비로그인 실행을 막지 않았으면 누구나 */
function canRun() {
  return Boolean(state.user) || state.anonRun;
}

/** 자동 저장: 서버 모드에서는 로그인해야 한다 (비로그인 실행은 저장하지 않음) */
function canSave() {
  return Boolean(state.user) || !state.multiUser;
}

function scheduleDraft() {
  if (!canSave()) return;
  $('#saveState').textContent = '저장 대기…';
  clearTimeout(draftTimer);
  draftTimer = setTimeout(flushDraft, 800);
}

async function flushDraft() {
  clearTimeout(draftTimer);
  if (!editor || !current || !canSave()) return;
  const code = editor.getValue();
  if (code === current.lastSaved) return;
  try {
    await api(`/draft/${current.day}/${current.level}`, { method: 'PUT', body: { code } });
    current.lastSaved = code;
    const s = $('#saveState'); if (s) s.textContent = '자동 저장됨';
  } catch (e) { const s = $('#saveState'); if (s) s.textContent = `저장 실패: ${e.message}`; }
}
window.addEventListener('beforeunload', () => { if (editor && current && canSave() && editor.getValue() !== current.lastSaved) navigator.sendBeacon?.(`/api/draft/${current.day}/${current.level}`, new Blob([JSON.stringify({ code: editor.getValue() })], { type: 'application/json' })); });

async function showLeftTab(tab) {
  for (const b of view.querySelectorAll('.subtabs button')) b.classList.toggle('active', b.dataset.tab === tab);
  const body = $('#leftBody');
  body.className = tab === 'readme' ? 'md' : '';
  if (tab === 'readme') { body.innerHTML = md(current.data.readme); return; }
  body.innerHTML = '<p class="md muted">불러오는 중…</p>';
  const sol = await api(`/solutions/${current.day}/${current.level}`);
  const monaco = await loadMonaco();
  const colorize = async (code) => `<pre>${await monaco.editor.colorize(code, 'java', { tabSize: 4 })}</pre>`;
  if (tab === 'editorial') {
    body.innerHTML = `<div class="md">${md(sol.editorial.readme)}<h2>모범 풀이</h2></div><div class="codeview">${await colorize(sol.editorial.code)}</div>`;
    return;
  }
  if (!sol.solvers.length) { body.innerHTML = '<div class="md muted">아직 이 문제를 푼(머지된) 사람이 없습니다. 상단의 <b>동기화</b>로 최신 상태를 받아올 수 있습니다.</div>'; return; }
  body.innerHTML = `<div class="solvers">${sol.solvers.map((l) => `<button data-login="${esc(l)}">@${esc(l)}</button>`).join('')}</div><div class="codeview" id="solCode"><p class="muted">사람을 선택하세요.</p></div>`;
  for (const b of body.querySelectorAll('.solvers button')) {
    b.onclick = async () => {
      for (const x of body.querySelectorAll('.solvers button')) x.classList.toggle('active', x === b);
      $('#solCode').innerHTML = '<p class="muted">불러오는 중…</p>';
      try {
        const r = await api(`/solutions/${current.day}/${current.level}/${b.dataset.login}`);
        $('#solCode').innerHTML = await colorize(r.code);
      } catch (e) { $('#solCode').innerHTML = `<p style="color:var(--bad)">${esc(e.message)}</p>`; }
    };
  }
  body.querySelector('.solvers button')?.click();
}

function fmtTime(c) {
  if (c.status === 'TLE') return `>${Math.round(c.timeMs)} ms`;
  if (!c.timeMs && c.status !== 'PASS') return '';
  return c.timeMs < 1 ? '<1 ms' : `${Math.round(c.timeMs)} ms`;
}

function renderGrade(grade, mode) {
  const sum = $('#resultSummary');
  const body = $('#resultBody');
  if (!grade.result) {
    sum.innerHTML = '<span class="summary fail">러너 오류</span>';
    body.innerHTML = `<pre class="case-detail">${esc(grade.report)}</pre>`;
    return;
  }
  const r = grade.result;
  const p = r.problems[0] || { cases: [] };
  const ok = r.status === 'PASS';
  const scale = r.timeScale && r.timeScale !== 1 ? ` · 로컬 시간 제한 ×${r.timeScale} (CI 는 ×1)` : '';
  sum.innerHTML = `<span class="summary ${ok ? 'pass' : 'fail'}">${ok ? '✅ 통과' : '❌ 실패'}</span> <span class="muted">${r.summary.passed}/${r.summary.total} 케이스 · ${mode}${scale}</span>`;
  let html = '';
  if (p.compileError) html += `<div class="case fail"><span class="mark">✘</span><b>컴파일 오류</b></div><pre class="case-detail">${esc(p.compileError)}</pre>`;
  for (const c of p.cases) {
    if (c.kind === 'template') continue;
    if (c.status === 'COMPILE_ERROR') continue;
    const pass = c.status === 'PASS';
    const mark = pass ? '✔' : c.status === 'TLE' ? '⏱' : '✘';
    html += `<div class="case ${pass ? 'pass' : 'fail'}"><span class="mark">${mark}</span><span class="name">${esc(c.name)}</span><span class="time">${fmtTime(c)}${c.limitMs ? ` <small>/ ${c.limitMs}</small>` : ''}</span><span>${pass ? '' : `<b>${esc(c.statusLabel)}</b> `}${c.note ? `<span class="muted">— ${esc(c.note)}</span>` : ''}</span></div>`;
    if (!pass) {
      let d = '';
      if (c.input) d += `입력  : ${c.input}\n`;
      if (c.status === 'WRONG') d += `기대값: ${c.expected}\n실제값: ${c.actual}\n`;
      else if (c.message) d += `${c.message}\n`;
      html += `<div class="case-detail">${esc(d.trimEnd())}</div>`;
    }
    // 실행(basic)일 때만 러너가 담아 준다 — 풀이가 System.out/err 로 찍은 내용
    if (c.output) html += `<div class="case-output"><div class="muted">콘솔 출력</div><pre>${esc(c.output.replace(/\n$/, ''))}</pre></div>`;
  }
  body.innerHTML = html || `<pre class="case-detail">${esc(grade.report)}</pre>`;
}

async function runOrSubmit(submit) {
  await flushDraft();
  const code = editor.getValue();
  const { day, level } = current;
  const event = submit ? 'submit_solution' : 'run_code';
  const run = $('#runBtn'), sub = $('#submitBtn'), sum = $('#resultSummary'), body = $('#resultBody');
  run.disabled = sub.disabled = true;
  sum.innerHTML = `<span class="spinner"></span> ${submit ? '전체 케이스로 채점 중… (시간 제한 적용)' : 'basic 케이스 실행 중…'}`;
  body.innerHTML = '';
  try {
    if (!submit) {
      const grade = await api(`/run/${day}/${level}`, { method: 'POST', body: { code } });
      renderGrade(grade, 'basic');
      track(event, { day, level, result: outcome(grade) });
      return;
    }
    const r = await api(`/submit/${day}/${level}`, { method: 'POST', body: { code } });
    renderGrade(r.grade, '전체');
    track(event, { day, level, result: r.pr ? 'submitted' : outcome(r.grade) });
    if (r.pr) {
      body.insertAdjacentHTML('afterbegin', `<div class="prbox">🎉 제출 완료 — <a href="${esc(r.pr.url)}" target="_blank" rel="noopener">PR #${r.pr.number}</a>${r.pr.created ? '을 만들었습니다' : '에 추가했습니다'}. CI 채점을 통과하면 자동으로 머지되고 출석표에 반영됩니다.${r.pr.note ? ` <span class="muted">(${esc(r.pr.note)})</span>` : ''}</div>`);
      $('#statusLabel').textContent = STATUS_LABEL.submitted;
      await refreshState();
    } else {
      body.insertAdjacentHTML('afterbegin', '<div class="notice">전체 케이스를 통과해야 제출됩니다. 실패한 케이스를 확인하세요.</div>');
    }
  } catch (e) {
    sum.innerHTML = '<span class="summary fail">오류</span>';
    body.innerHTML = `<div class="notice" style="border-color:var(--bad)">${esc(e.message)}</div>`;
    track(event, { day, level, result: 'error' });
  } finally {
    run.disabled = !canRun(); sub.disabled = !state.user;
  }
}

// ───────────── 출석표 ─────────────
async function renderAttendance() {
  view.innerHTML = '<div class="page"><p class="muted">출석표를 계산하는 중…</p></div>';
  let a;
  try { a = await api('/attendance'); } catch (e) { view.innerHTML = `<div class="page"><div class="notice">${esc(e.message)}</div></div>`; return; }
  const me = state.user?.login;
  const days = [];
  for (let d = 1; d <= a.today_day; d++) days.push(d);
  const head = `<tr><th>멤버</th><th>합류</th><th>출석</th><th>총 풀이</th><th>스트릭</th><th>최장</th>${days.map((d) => `<th title="${a.dates[d]}">${d}<br><small class="muted">${a.dates[d].slice(5)}</small></th>`).join('')}</tr>`;
  const rows = a.members.map((m) => {
    const cells = days.map((d) => {
      if (d < m.join_day) return '<td class="muted">-</td>';
      const n = (m.solved[String(d)] || []).length;
      return `<td class="n${n}" title="lv ${(m.solved[String(d)] || []).join(', ')}">${n || '·'}</td>`;
    }).join('');
    return `<tr class="${m.login === me ? 'me' : ''}"><td>@${esc(m.login)}</td><td>${m.join_day}일차</td><td>${a.start_date ? `${m.attended}/${m.unlocked_since_join}` : '-'}</td><td>${m.total}</td><td>${m.streak ? `🔥 ${m.streak}` : '0'}</td><td>${m.longest}</td>${cells}</tr>`;
  }).join('');
  view.innerHTML = `<div class="page">
    <div class="row" style="margin-bottom:12px"><h2 style="margin:0">출석표</h2><span class="muted">${a.start_date ? `${a.today_day}일차 / ${a.total_days}일` : '시작 전'} · 머지된 풀이 기준 · 갱신 ${esc(a.generated_at)}</span></div>
    ${a.members.length ? `<div class="att-wrap"><table class="att-table">${head}${rows}</table></div>` : '<div class="notice">아직 머지된 풀이가 없습니다. 첫 PR 이 머지되면 자동으로 등록됩니다.</div>'}
  </div>`;
}

// ───────────── 라우팅 ─────────────
async function route() {
  const hash = location.hash || '#/';
  trackPage(hash);
  for (const a of document.querySelectorAll('.tabs a')) a.classList.toggle('active', a.dataset.nav === (hash.startsWith('#/attendance') ? 'attendance' : 'days'));
  if (!hash.startsWith('#/day/')) { flushDraft(); if (editor) { editor.dispose(); editor = null; } current = null; }
  try {
    const m = hash.match(/^#\/day\/(\d+)\/([0-3])$/);
    if (m) return await renderProblem(Number(m[1]), Number(m[2]));
    if (hash.startsWith('#/attendance')) return await renderAttendance();
    renderDays();
  } catch (e) {
    view.innerHTML = `<div class="page"><div class="notice" style="border-color:var(--bad)">${esc(e.message)}</div></div>`;
  }
}

window.addEventListener('hashchange', route);
refreshState().then(() => { initAnalytics(state.gaId); return route(); }).catch((e) => { view.innerHTML = `<div class="page"><div class="notice">서버에 연결할 수 없습니다: ${esc(e.message)}</div></div>`; });
