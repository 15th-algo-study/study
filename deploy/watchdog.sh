#!/usr/bin/env bash
# 알고리즘 스터디 서버 워치독 — systemd 타이머가 1분마다 root 로 실행한다 (설치: sudo deploy/install-autostart.sh)
#
# 확인하는 것
#   docker : Docker 데몬이 응답하는가 (docker info)
#   app    : 앱 컨테이너가 떠 있고 healthcheck 가 unhealthy 가 아닌가 (/healthz)
#   caddy  : Caddy 가 80 포트에서 응답하는가 (HTTPS 로 넘기는 응답이면 정상)
#
# 연속 실패 횟수에 따라 단계적으로 복구한다
#   RESTART_AFTER(기본 3)회 = 약 3분  → 컨테이너 재시작 (Docker 가 문제면 Docker 재시작), 이후 같은 간격으로 반복
#   REBOOT_AFTER(기본 10)회 = 약 10분 → VM 재부팅 (부팅 후 30분 안이거나 6시간 안에 이미 재부팅했으면 하지 않음)
#
# 잠시 멈추기(점검 중 오작동 방지):  sudo touch /var/lib/algo-study-watchdog/paused   (다시 켜려면 rm)
# 로그:                             journalctl -u algo-study-watchdog -n 50
set -u
REPO="$(cd "$(dirname "$0")/.." && pwd)"
ENV_FILE="$REPO/deploy/.env"
STATE_DIR="${WATCHDOG_STATE_DIR:-/var/lib/algo-study-watchdog}"
RESTART_AFTER="${RESTART_AFTER:-3}"
REBOOT_AFTER="${REBOOT_AFTER:-10}"
MIN_UPTIME_FOR_REBOOT="${MIN_UPTIME_FOR_REBOOT:-1800}"
REBOOT_COOLDOWN="${REBOOT_COOLDOWN:-21600}"
DRY_RUN="${WATCHDOG_DRY_RUN:-0}"

mkdir -p "$STATE_DIR"
log() { echo "[watchdog] $*"; }
act() { if [ "$DRY_RUN" = 1 ]; then log "(dry-run) $*"; else "$@"; fi; }
compose() { docker compose -f "$REPO/deploy/compose.yml" --env-file "$ENV_FILE" "$@"; }
now() { date +%s; }

if [ -f "$STATE_DIR/paused" ]; then log "일시 정지 중 ($STATE_DIR/paused) — 건너뜀"; exit 0; fi
[ -f "$ENV_FILE" ] || { log "deploy/.env 가 없습니다 — 건너뜀"; exit 0; }
DOMAIN="$(sed -n 's/^DOMAIN=//p' "$ENV_FILE" | tail -1)"

docker_ok() { timeout 20 docker info >/dev/null 2>&1; }

app_ok() {
  local id status
  id="$(timeout 20 docker compose -f "$REPO/deploy/compose.yml" --env-file "$ENV_FILE" ps -q app 2>/dev/null)"
  [ -n "$id" ] || return 1
  status="$(timeout 10 docker inspect -f '{{.State.Status}} {{if .State.Health}}{{.State.Health.Status}}{{end}}' "$id" 2>/dev/null)"
  case "$status" in
    "running healthy"|"running starting"|"running ") return 0 ;;
    *) return 1 ;;
  esac
}

caddy_ok() {
  local code
  code="$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 -H "Host: ${DOMAIN:-localhost}" http://127.0.0.1/healthz 2>/dev/null)"
  case "$code" in 200|301|302|307|308) return 0 ;; *) return 1 ;; esac
}

fails="$(cat "$STATE_DIR/fails" 2>/dev/null || echo 0)"
problem=""
if ! docker_ok; then
  problem="docker"
else
  app_ok || problem="app"
  caddy_ok || problem="${problem:+$problem,}caddy"
fi

if [ -z "$problem" ]; then
  [ "$fails" -gt 0 ] && log "정상으로 돌아옴 (연속 실패 ${fails}회 후)"
  echo 0 > "$STATE_DIR/fails"
  exit 0
fi

fails=$((fails + 1))
echo "$fails" > "$STATE_DIR/fails"
log "이상 감지: $problem (연속 ${fails}회)"

if [ "$fails" -ge "$REBOOT_AFTER" ]; then
  uptime_sec="$(cut -d. -f1 /proc/uptime 2>/dev/null || echo 0)"
  last_reboot="$(cat "$STATE_DIR/last_reboot" 2>/dev/null || echo 0)"
  if [ "$uptime_sec" -lt "$MIN_UPTIME_FOR_REBOOT" ]; then
    log "재부팅 보류: 부팅한 지 ${uptime_sec}초 (최소 ${MIN_UPTIME_FOR_REBOOT}초) — 컨테이너 재시작만 시도"
  elif [ $(( $(now) - last_reboot )) -lt "$REBOOT_COOLDOWN" ]; then
    log "재부팅 보류: 최근 워치독 재부팅 후 ${REBOOT_COOLDOWN}초가 지나지 않음 — 컨테이너 재시작만 시도"
  else
    log "복구 실패가 이어져 VM 을 재부팅합니다"
    now > "$STATE_DIR/last_reboot"
    echo 0 > "$STATE_DIR/fails"
    act systemctl reboot
    exit 0
  fi
fi

if [ $((fails % RESTART_AFTER)) -eq 0 ]; then
  if [ "$problem" = "docker" ]; then
    log "Docker 재시작"
    act systemctl restart docker
    act compose up -d
  else
    log "컨테이너 다시 올리기 (up -d) + 재시작: ${problem}"
    act compose up -d
    for svc in ${problem//,/ }; do act compose restart "$svc"; done
  fi
fi
