# 서버 배포 (다중 사용자 모드)

로컬에서 Docker 를 돌리기 어려운 멤버를 위해, 호스트가 서버 한 대에 앱을 띄워 **브라우저 주소 하나**로 쓰게 하는 방법입니다. 로컬 실행(`docker compose up`)은 그대로 쓸 수 있고, 둘을 섞어 써도 됩니다.

## 무엇이 달라지나

| | 로컬 (기본) | 서버 (`MULTI_USER=1`) |
|---|---|---|
| 사용자 | 1명 | 여러 명 — 브라우저마다 쿠키 세션, 사용자마다 토큰·작성 중 코드 분리 |
| 로그인 | 아무 GitHub 계정 | **레포 쓰기 권한이 있는 멤버만** (`members` 팀). 그 외는 로그인 거부 |
| 실행·제출 | 비로그인도 실행 가능 | **로그인해야** 실행·자동 저장·제출 가능 |
| 풀이 파일 | 작업 트리에 저장 | 작업 트리에 쓰지 않음 (제출 상태는 원격 브랜치로 판단) |
| 새 문제·남의 풀이 | 동기화 버튼 | 5분마다 자동 동기화 (+ 버튼) |
| 채점 | 한 번에 1개 | 슬롯 수만큼 동시 (`RUNNER_SLOTS`), 한 사람당 1개 |
| 제출 코드 실행 | 앱과 같은 사용자 | 별도 uid(10001) — 토큰이 있는 `/data` 와 레포를 읽기 전용/접근 불가로 격리 |

## 서버 사양

채점 하나에 메모리 약 1.5 GB(Gradle 데몬 + 테스트 JVM)가 들고, CPU 보다 메모리가 병목입니다.

| 동시 사용 | 사양 | `.env` |
|---|---|---|
| ~5명 | 2 vCPU / 4 GB / 40 GB SSD | `RUNNER_SLOTS=1`, `APP_MEM_LIMIT=3g` |
| ~10명 | 2~4 vCPU / 8 GB / 40 GB SSD | `RUNNER_SLOTS=2`, `APP_MEM_LIMIT=6g` |

- 채점은 보통 1~7초라, 슬롯 1개여도 몇 명이 겹치면 몇 초 기다리는 정도입니다.
- 리눅스(Ubuntu 22.04/24.04 권장), x86_64·ARM 모두 됩니다.
- 열어야 할 포트: **80, 443** (Let's Encrypt 인증서 발급·HTTPS). 22(SSH)는 관리용.

## 1. 준비

1. 서버에 Docker Engine 설치: <https://docs.docker.com/engine/install/ubuntu/> (설치 후 `sudo usermod -aG docker $USER` → 재로그인)
2. 접속 주소 정하기
   - 도메인이 있으면 A 레코드를 서버 공인 IP 로.
   - 없으면 `sslip.io` 를 쓰면 됩니다: IP 가 `1.2.3.4` 라면 `1-2-3-4.sslip.io` (별도 설정 없이 그 IP 로 연결되고, 인증서도 발급됩니다).
3. **일반 사용자 계정으로** 레포 clone (root 로 clone 하지 마세요 — 제출 코드가 레포에 쓰지 못하게 하는 격리가 이 소유권에 기댑니다)
   ```bash
   git clone https://github.com/15th-algo-study/study.git ~/study
   cd ~/study
   cp deploy/.env.example deploy/.env
   nano deploy/.env        # DOMAIN, RUNNER_SLOTS, APP_MEM_LIMIT
   ```

## 2. 실행

```bash
docker compose -f deploy/compose.yml --env-file deploy/.env up -d --build
```

- 첫 빌드는 몇 분 걸립니다. 인증서 발급은 첫 접속 때 수십 초.
- 로그 확인:
  ```bash
  docker compose -f deploy/compose.yml --env-file deploy/.env logs -f app
  ```
  `모드: 서버(다중 사용자) · 채점 슬롯 N개 · 제출 코드 uid 10001` 이 보여야 합니다.
  **`경고: 제출 코드 실행 사용자(uid 10001)가 레포 폴더에 쓸 수 있습니다`** 가 보이면 레포를 root 로 clone 했거나 권한이 열려 있는 것입니다 → 1-3 단계대로 일반 사용자로 다시 clone 하세요.
- 브라우저에서 `https://<DOMAIN>` → GitHub 로그인 → 멤버 계정이면 바로 사용.

## 3. 멤버 안내

로컬 설치가 어려운 멤버에게는 주소만 보내면 됩니다.

> 브라우저에서 `https://<DOMAIN>` 접속 → 오른쪽 위 **GitHub 로그인** → 코드 입력 → Authorize. 이후 사용법은 README 6~9절과 같습니다.

- 조직 초대를 **수락한 멤버만** 로그인됩니다. "스터디 멤버가 아닙니다" 가 뜨면 초대 수락 여부를 확인하세요.
- 같은 계정으로 서버와 로컬 앱을 둘 다 써도 됩니다 (작성 중 코드는 각자 따로 저장됩니다).

## 4. 운영

| 작업 | 명령 |
|---|---|
| 앱 업데이트 (main 에 앱·러너 변경이 들어왔을 때) | `git pull && docker compose -f deploy/compose.yml --env-file deploy/.env up -d --build` |
| 문제·풀이 갱신 | 자동(5분). 즉시 하려면 앱의 **동기화** 버튼 |
| 재시작 | `docker compose -f deploy/compose.yml --env-file deploy/.env restart app` |
| 중지 | `docker compose -f deploy/compose.yml --env-file deploy/.env down` (볼륨 유지) |
| 모든 로그인 강제 해제 | `docker compose -f deploy/compose.yml --env-file deploy/.env exec app sh -c 'rm -rf /data/sessions/* /data/users/*'` |
| 메모리 확인 | `docker stats` |

- **백업**: 잃으면 안 되는 것은 `data` 볼륨(작성 중 코드)뿐입니다. 토큰은 다시 로그인하면 되고, 풀이는 GitHub 에 있습니다.
- 앱 컨테이너 포트는 바깥에 열지 않고 Caddy(80·443)만 열립니다.

## 보안 메모

- 서버는 누구나 접속할 수 있으므로 **로그인한 멤버만** 코드를 실행할 수 있습니다. 멤버 확인은 로그인할 때 GitHub API 로 레포 쓰기 권한을 봅니다.
- 제출 코드는 uid 10001 로 실행되어 `/data`(모든 사용자의 토큰)를 읽을 수 없습니다. 테스트 JVM 의 네트워크는 막지 않습니다.
- 토큰은 GitHub App 사용자 토큰이라 이 레포의 contents·pull requests 권한으로만 제한됩니다.
- 세션 쿠키는 HttpOnly·SameSite=Lax·Secure(HTTPS) 이고 30일 미사용 시 정리됩니다.
