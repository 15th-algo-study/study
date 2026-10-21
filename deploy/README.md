# 서버 배포 (다중 사용자 모드)

로컬에서 Docker 를 돌리기 어려운 멤버를 위해, 호스트가 서버 한 대에 앱을 띄워 **브라우저 주소 하나**로 쓰게 하는 방법입니다. 로컬 실행(`docker compose up`)은 그대로 쓸 수 있고, 둘을 섞어 써도 됩니다.

## 무엇이 달라지나

| | 로컬 (기본) | 서버 (`MULTI_USER=1`) |
|---|---|---|
| 사용자 | 1명 | 여러 명 — 브라우저마다 쿠키 세션, 사용자마다 토큰·작성 중 코드 분리 |
| 로그인 | 아무 GitHub 계정 | **레포 쓰기 권한이 있는 멤버만** (`members` 팀). 그 외는 로그인 거부 |
| 실행·제출 | 비로그인도 실행 가능 | 실행(basic)은 비로그인도 가능 — 저장 안 됨, 서버 전체에서 한 번에 1개, 20초 상한, 멤버 채점이 먼저 (`ANON_RUN=0` 이면 끔). **자동 저장·제출은 로그인해야** 가능 |
| 풀이 파일 | 작업 트리에 저장 | 작업 트리에 쓰지 않음 (제출 상태는 원격 브랜치로 판단) |
| 새 문제·남의 풀이 | 동기화 버튼 | 5분마다 자동 동기화 (+ 버튼) |
| 채점 | 한 번에 1개 | 슬롯 수만큼 동시 (`RUNNER_SLOTS`), 한 사람당 1개 |
| 제출 코드 실행 | 앱과 같은 사용자 | 별도 uid(10001) — 토큰이 있는 `/data` 와 레포를 읽기 전용/접근 불가로 격리 |

## 서버 사양

채점은 Gradle 을 거치지 않고 `java` 로 바로 실행합니다(채점 하나에 JVM 1개, 메모리 최대 약 1.2 GB). 속도는 **CPU 단일 코어 성능**이 좌우합니다 — 공유형(버스터블) vCPU 보다 컴퓨트 최적화 타입이 체감상 빠릅니다.

| 동시 사용 | 사양 | `.env` |
|---|---|---|
| ~5명 | 2 vCPU / 4 GB / 40 GB SSD | `RUNNER_SLOTS=1`, `APP_MEM_LIMIT=3g` |
| ~10명 | 2~4 vCPU / 8 GB / 40 GB SSD | `RUNNER_SLOTS=2`, `APP_MEM_LIMIT=6g` |

- 채점 한 번은 보통 0.3~1.5초(서버 CPU 에 따라)라, 슬롯 1개여도 몇 명이 겹치면 1~2초 기다리는 정도입니다.
- 앱이 시작될 때(또는 러너 코드가 바뀐 뒤 처음) 슬롯마다 러너 jar 와 CDS 아카이브를 한 번 만듭니다(느린 서버에서 10~30초). 그동안 들어온 채점은 줄을 서서 기다립니다.
- 서버 속도 점검: `docker compose -f deploy/compose.yml --env-file deploy/.env exec -u 10001 -e HOME=/tmp app bash /workspace/scripts/bench-runner.sh`
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
| 중지 | `docker compose -f deploy/compose.yml --env-file deploy/.env down` (볼륨 유지. 자동 복구를 설치했다면 5절대로 워치독을 먼저 멈춤) |
| 모든 로그인 강제 해제 | `docker compose -f deploy/compose.yml --env-file deploy/.env exec app sh -c 'rm -rf /data/sessions/* /data/users/*'` |
| 메모리 확인 | `docker stats` |

- **백업**: 잃으면 안 되는 것은 `data` 볼륨(작성 중 코드)뿐입니다. 토큰은 다시 로그인하면 되고, 풀이는 GitHub 에 있습니다.
- 앱 컨테이너 포트는 바깥에 열지 않고 Caddy(80·443)만 열립니다.

## 5. 자동 복구 (권장)

2단계까지 끝낸 뒤 한 번만 실행합니다. VM 이 켜지면 서비스가 저절로 뜨고, 죽거나 멈추면 단계적으로 되살립니다.

```bash
cd ~/study
sudo deploy/install-autostart.sh                  # 시스템 전체가 멈추는 것까지 대비하려면 --with-softdog
```

| 상황 | 누가 복구하나 | 걸리는 시간 |
|---|---|---|
| 컨테이너 프로세스가 죽음 | Docker 재시작 정책 (`restart: unless-stopped`) | 즉시 |
| 앱이 떠 있지만 응답 없음 | healthcheck(30초 × 3회) → 워치독이 컨테이너 재시작 | 약 4~5분 |
| Caddy(80 포트) 응답 없음 | 워치독이 Caddy 재시작 | 약 3분 |
| Docker 데몬이 응답 없음 | 워치독이 Docker 재시작 → 서비스 다시 올림 | 약 3분 |
| 재시작해도 계속 실패 | 워치독이 **VM 재부팅** (부팅 후 30분 안이거나 6시간 안에 이미 재부팅했으면 하지 않음) | 약 10분 |
| VM 부팅 | `algo-study.service` 가 Docker 뒤에 compose 를 띄움 | 부팅 후 1~2분 |
| 커널 패닉 | `kernel.panic=10` 으로 자동 재부팅 | 10초 |
| 시스템 전체 멈춤 | `--with-softdog` 를 줬을 때만: 소프트웨어 워치독이 재부팅 | 약 1분 |

- 워치독은 `algo-study-watchdog.timer` 가 1분마다 root 로 `deploy/watchdog.sh` 를 실행합니다. 상태 기준은 앱의 `/healthz`, Docker 데몬 응답, Caddy 응답입니다.
- 로그: `journalctl -u algo-study-watchdog -n 50` (`[watchdog]` 줄은 이상을 감지했거나 복구했을 때만 남습니다)
- **일부러 서비스를 내려 둘 때**(점검, `down` 등)는 워치독이 3분 뒤 다시 올리므로 먼저 멈춥니다.
  ```bash
  sudo touch /var/lib/algo-study-watchdog/paused    # 점검이 끝나면 sudo rm /var/lib/algo-study-watchdog/paused
  ```
- 레포 경로를 옮기면 설치를 다시 실행합니다. 제거: `sudo deploy/install-autostart.sh --uninstall` (실행 중인 컨테이너는 그대로).

### GCP(Compute Engine) 에서 함께 켤 것

VM 안의 워치독은 VM 이 살아 있어야 동작합니다. 호스트 장애·유지보수는 GCP 쪽 설정으로 대비합니다. **Compute Engine → VM 인스턴스 → (VM) → 수정 → 관리 → 가용성 정책**:

| 항목 | 값 |
|---|---|
| VM 프로비저닝 모델 | **표준** (Spot 은 언제든 회수되고 자동 재시작이 안 됩니다) |
| 호스트 유지보수 시 | **VM 인스턴스 이전** (Migrate) |
| 자동 다시 시작 | **사용** — 호스트 장애·크래시로 VM 이 꺼지면 GCP 가 다시 켜고, 부팅하면 위의 자동 실행이 서비스를 띄웁니다 |

- (선택) **Monitoring → 업타임 체크** 에서 `https://<DOMAIN>/healthz` 를 5분 간격으로 등록하고 이메일 알림을 걸어 두면, 자동 복구로도 안 될 때(디스크 가득 참, 설정 오류 등) 알 수 있습니다.
- 외부 IP 는 **고정(예약)** 으로 바꿔 두세요. 임시 IP 는 VM 을 중지·시작하면 바뀌어 `DOMAIN`(sslip.io 포함)이 깨집니다.

## 6. 방문 통계 (Google Analytics, 선택)

서버 배포에서만 켤 수 있습니다. 로컬 실행(`MULTI_USER` 없음)에서는 `GA_MEASUREMENT_ID` 를 넣어도 무시합니다.

1. <https://analytics.google.com> → **관리 → 만들기 → 속성** (시간대 대한민국, 통화 KRW)
2. **데이터 스트림 → 웹** → URL `https://<DOMAIN>` → **측정 ID**(`G-XXXXXXXXXX`) 복사
3. 같은 스트림의 **향상된 측정 → 페이지 조회 → 고급 설정**에서 **브라우저 기록 이벤트 기반 페이지 변경**을 끕니다 (앱이 화면을 바꿀 때 직접 보내므로, 켜 두면 중복으로 셉니다)
4. 서버의 `deploy/.env` 에 `GA_MEASUREMENT_ID=G-XXXXXXXXXX` 를 넣고 적용
   ```bash
   docker compose -f deploy/compose.yml --env-file deploy/.env up -d
   ```
   로그에 `방문 통계 : Google Analytics G-…` 가 보이면 켜진 것입니다.
5. (권장) **관리 → 맞춤 정의 → 맞춤 측정기준 만들기**(범위: 이벤트)로 `day`, `level`, `result` 를 등록해야 보고서에서 이벤트를 일차·레벨·결과별로 나눠 볼 수 있습니다.

보내는 것

| 이벤트 | 언제 | 값 |
|---|---|---|
| `page_view` | 화면이 바뀔 때 | 경로 `/`(일차 목록), `/day/001/lv0`(문제), `/attendance`(출석표) |
| `run_code` | 실행 결과가 나왔을 때 | `day`, `level`, `result`: `pass` · `fail` · `error` |
| `submit_solution` | 제출 결과가 나왔을 때 | `day`, `level`, `result`: `submitted` · `fail` · `error` |

GitHub 로그인 이름·코드처럼 사람을 알아볼 수 있는 값은 보내지 않습니다. 멤버들에게 "방문 통계(GA)를 수집한다"고 한 줄 알려 두세요.

## 보안 메모

- 서버는 누구나 접속할 수 있고, 기본값(`ANON_RUN=1`)에서는 **비로그인 방문자도 코드를 실행**할 수 있습니다. 비로그인 실행은 서버 전체에서 한 번에 하나, 20초 상한이고 멤버 채점보다 뒤에 섭니다. 테스트 JVM 의 네트워크는 막지 않으므로 낯선 사람이 서버에서 외부로 요청을 보낼 수 있습니다 — 남용이 보이면 `deploy/.env` 에 `ANON_RUN=0` 을 넣고 `up -d` 하면 실행도 로그인한 멤버만 할 수 있습니다.
- 제출·저장은 **로그인한 멤버만** 할 수 있습니다. 멤버 확인은 로그인할 때 GitHub API 로 레포 쓰기 권한을 봅니다.
- 제출 코드는 uid 10001 로 실행되어 `/data`(모든 사용자의 토큰)를 읽을 수 없고, 다음 채점이 읽는 러너 jar·CDS 아카이브(root 소유)도 바꿀 수 없습니다. 최종 판정은 어차피 CI 가 다시 합니다. 테스트 JVM 의 네트워크는 막지 않습니다.
- 토큰은 GitHub App 사용자 토큰이라 이 레포의 contents·pull requests 권한으로만 제한됩니다.
- 세션 쿠키는 HttpOnly·SameSite=Lax·Secure(HTTPS) 이고 30일 미사용 시 정리됩니다.
- GA 를 켜면 Google 스크립트(`googletagmanager.com`)가 페이지에서 실행됩니다. 세션 쿠키는 HttpOnly 라 이 스크립트가 읽을 수 없습니다.
