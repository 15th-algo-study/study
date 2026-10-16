# app — 멤버용 로컬 앱

Express + Monaco. 번들러 없이 `node_modules` 의 Monaco·marked 를 그대로 서빙한다 (오프라인 동작).

## 실행

멤버는 레포 루트에서 `docker compose up` 으로 실행한다 (4단계 참고). 개발할 때는 Node 로 직접:

```bash
cd app && npm install && npm start      # http://localhost:15015
```

| 환경 변수 | 기본값 | 설명 |
|---|---|---|
| `REPO_ROOT` | `app/..` | 레포 루트 (Docker 에서는 `/workspace`) |
| `DATA_DIR` | `app/data` | 토큰(`token.json`)·작성 중 코드(`drafts/`)·임시 파일 |
| `PORT` | `15015` | 포트 |
| `GITHUB_WEB_URL` / `GITHUB_API_URL` | github.com / api.github.com | 시연용 mock 으로 바꿀 때 |
| `GIT_REMOTE_URL` | `https://github.com/<org>/<repo>.git` | push·fetch 대상 (항상 HTTPS + 토큰, 멤버의 clone 이 SSH 여도 무관) |

## GitHub 없이 전체 흐름 시연

```bash
app/dev/demo.sh                 # mock GitHub(15099) + bare 원격 + 작업 clone 을 만들고 앱 실행(15015)
```

- 로그인 코드는 `ABCD-1234`, 인증 페이지는 `http://localhost:15099/login/device`
- 제출하면 PR 페이지(`http://localhost:15099/15th-algo-study/study/pull/1`)에 **CI 통과 → squash 머지 (흉내)** 버튼이 있다. 누른 뒤 앱에서 **동기화**하면 머지됨·출석표에 반영된다.
- 환경을 다시 만들려면 `app/dev/demo.sh setup`.

## 구조

```
server.js            엔트리 (정적 파일, /vendor, /api)
src/config.js        경로·환경 변수·config.json
src/schedule.js      시작일·휴일로 오늘 일차 계산 (scripts/attendance.py 와 같은 규칙)
src/auth.js          Device Flow, token.json, 토큰 갱신
src/github.js        REST (user, pulls)
src/git.js           origin/main 읽기, 임시 인덱스로 커밋·푸시, 작업 트리 fast-forward
src/runner.js        runner/gradlew 실행 (한 번에 하나), result.json 파싱
src/problems.js      problems/ 읽기, drafts, solutions/<login> 쓰기
src/attendance.js    scripts/attendance.py --json 호출
src/routes.js        /api/*
public/              index.html, app.js(해시 라우팅), style.css
dev/                 mock GitHub, 시연 스크립트
```

## git 격리 원칙

- 멤버의 작업 트리와 인덱스는 건드리지 않는다. 다른 사람 풀이는 `git show origin/main:<path>`, 제출 커밋은 `GIT_INDEX_FILE` 임시 인덱스 + `commit-tree` 로 만들어 `user/<login>` 에 푸시한다.
- 열린 PR 이 있으면 그 브랜치 위에 쌓고, 없으면 `origin/main` 에서 새로 시작해 `--force` 푸시한다 (CI 가 머지 후 브랜치를 지우므로 보통 새로 시작).
- **동기화**만 작업 트리를 바꾼다: `origin/main` 으로 fast-forward. 앱이 써 둔 `solutions/<login>/…` untracked 파일이 머지된 내용과 같으면 먼저 지워 충돌을 피한다.
