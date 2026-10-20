import { GITHUB_API } from './config.js';

export class GitHubError extends Error {
  constructor(status, message, body) {
    super(message);
    this.status = status;
    this.body = body;
  }
}

/** GitHub REST 호출. 실패하면 GitHubError. */
export async function api(path, { method = 'GET', token, body } = {}) {
  const res = await fetch(`${GITHUB_API}${path}`, {
    method,
    headers: {
      Accept: 'application/vnd.github+json',
      'X-GitHub-Api-Version': '2022-11-28',
      'User-Agent': 'algo-study-app',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(body ? { 'Content-Type': 'application/json' } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = { raw: text }; }
  if (!res.ok) {
    throw new GitHubError(res.status, data?.message || `GitHub API ${res.status}`, data);
  }
  return data;
}

export async function currentUser(token) {
  const u = await api('/user', { token });
  return { login: u.login, id: u.id, name: u.name || u.login, avatar_url: u.avatar_url };
}

/** 이 사용자가 레포에 쓰기(push) 권한이 있는지 = 스터디 멤버인지. */
export async function hasWriteAccess({ token, org, repo, login }) {
  const r = await api(`/repos/${org}/${repo}`, { token });
  if (r.permissions) return Boolean(r.permissions.push || r.permissions.maintain || r.permissions.admin);
  // 응답에 permissions 가 없으면 협업자 권한 API 로 한 번 더 확인
  const p = await api(`/repos/${org}/${repo}/collaborators/${encodeURIComponent(login)}/permission`, { token });
  return ['admin', 'maintain', 'write'].includes(p.permission);
}

/** 해당 head 브랜치에서 main 으로 열린 PR (없으면 null). */
export async function openPullRequest({ token, org, repo, head }) {
  const list = await api(`/repos/${org}/${repo}/pulls?state=open&head=${encodeURIComponent(`${org}:${head}`)}&base=main`, { token });
  return list.length ? list[0] : null;
}

export async function createPullRequest({ token, org, repo, head, title, body }) {
  return api(`/repos/${org}/${repo}/pulls`, { method: 'POST', token, body: { title, head, base: 'main', body } });
}
