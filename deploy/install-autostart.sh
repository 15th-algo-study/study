#!/usr/bin/env bash
# 서버 자동 복구 설치 (리눅스, systemd):
#   - VM 이 켜지면 Docker 와 스터디 서비스(compose)를 자동으로 띄운다      → algo-study.service
#   - 1분마다 상태를 확인해 컨테이너 재시작 → Docker 재시작 → VM 재부팅 순으로 복구한다 → algo-study-watchdog.timer
#   - 커널 패닉이 나면 10초 뒤 자동 재부팅                                      → /etc/sysctl.d/90-algo-study.conf
#   - (선택) --with-softdog: 시스템 전체가 멈추면 소프트웨어 워치독이 재부팅
#
#   sudo deploy/install-autostart.sh               # 설치
#   sudo deploy/install-autostart.sh --with-softdog
#   sudo deploy/install-autostart.sh --uninstall   # 제거
set -euo pipefail
[ "$(id -u)" = 0 ] || { echo "sudo 로 실행하세요: sudo $0 $*"; exit 1; }
REPO="$(cd "$(dirname "$0")/.." && pwd)"
UNIT_DIR=/etc/systemd/system

if [ "${1:-}" = "--uninstall" ]; then
  systemctl disable --now algo-study-watchdog.timer algo-study.service 2>/dev/null || true
  rm -f "$UNIT_DIR"/algo-study.service "$UNIT_DIR"/algo-study-watchdog.service "$UNIT_DIR"/algo-study-watchdog.timer
  rm -f /etc/sysctl.d/90-algo-study.conf /etc/systemd/system.conf.d/90-algo-study-watchdog.conf /etc/modules-load.d/algo-study-softdog.conf
  systemctl daemon-reload
  echo "제거했습니다. (실행 중인 컨테이너는 그대로입니다)"
  exit 0
fi

DOCKER="$(command -v docker)" || { echo "docker 가 없습니다."; exit 1; }
[ -f "$REPO/deploy/.env" ] || { echo "$REPO/deploy/.env 가 없습니다. 먼저 deploy/README.md 의 1~2단계를 끝내세요."; exit 1; }
chmod +x "$REPO/deploy/watchdog.sh"

systemctl enable docker >/dev/null 2>&1 || true

cat > "$UNIT_DIR/algo-study.service" <<EOF
[Unit]
Description=알고리즘 스터디 서버 (docker compose)
Requires=docker.service
After=docker.service network-online.target
Wants=network-online.target

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=$REPO
ExecStart=$DOCKER compose -f deploy/compose.yml --env-file deploy/.env up -d
ExecStop=$DOCKER compose -f deploy/compose.yml --env-file deploy/.env stop
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOF

cat > "$UNIT_DIR/algo-study-watchdog.service" <<EOF
[Unit]
Description=알고리즘 스터디 워치독 (상태 확인·자동 복구)
After=docker.service algo-study.service

[Service]
Type=oneshot
ExecStart=$REPO/deploy/watchdog.sh
EOF

cat > "$UNIT_DIR/algo-study-watchdog.timer" <<EOF
[Unit]
Description=알고리즘 스터디 워치독 (1분마다)

[Timer]
OnBootSec=3min
OnUnitActiveSec=1min
AccuracySec=10s

[Install]
WantedBy=timers.target
EOF

cat > /etc/sysctl.d/90-algo-study.conf <<'EOF'
# 커널 패닉이 나면 10초 뒤 자동 재부팅 (알고리즘 스터디 서버)
kernel.panic = 10
EOF
sysctl -q -p /etc/sysctl.d/90-algo-study.conf || true

if [ "${1:-}" = "--with-softdog" ]; then
  # 사용자 공간이 통째로 멈추면(systemd 가 60초 동안 응답 없음) 커널의 소프트웨어 워치독이 재부팅한다
  echo softdog > /etc/modules-load.d/algo-study-softdog.conf
  modprobe softdog || true
  mkdir -p /etc/systemd/system.conf.d
  printf '[Manager]\nRuntimeWatchdogSec=60s\n' > /etc/systemd/system.conf.d/90-algo-study-watchdog.conf
  systemctl daemon-reexec
fi

systemctl daemon-reload
systemctl enable --now algo-study.service
systemctl enable --now algo-study-watchdog.timer

echo
echo "설치 완료"
echo "  부팅 시 자동 실행 : $(systemctl is-enabled algo-study.service)  (docker: $(systemctl is-enabled docker 2>/dev/null))"
echo "  워치독 타이머     : $(systemctl is-active algo-study-watchdog.timer)"
echo "  커널 패닉 재부팅  : $(sysctl -n kernel.panic)초"
[ "${1:-}" = "--with-softdog" ] && echo "  소프트웨어 워치독 : $(lsmod | grep -c '^softdog') (RuntimeWatchdogSec=60s)"
echo
echo "로그 보기   : journalctl -u algo-study-watchdog -n 50"
echo "잠시 멈추기 : sudo touch /var/lib/algo-study-watchdog/paused   (점검 끝나면 sudo rm 같은 경로)"
