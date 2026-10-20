#!/bin/sh
# 서버 모드(RUNNER_UID 지정)일 때만: 제출 코드를 실행할 사용자가 쓸 폴더를 넘겨 주고, 토큰이 있는 /data 는 root 만 읽게 한다.
# 로컬 모드(기본)에서는 아무것도 하지 않는다.
set -e
if [ "$(id -u)" = "0" ] && [ -n "${RUNNER_UID:-}" ]; then
  : "${RUNS_DIR:=/runs/jobs}" "${RUNNER_SLOTS_DIR:=/runs/slots}" "${DATA_DIR:=/data}" "${GRADLE_USER_HOME:=/gradle-home}"
  mkdir -p "$RUNS_DIR" "$RUNNER_SLOTS_DIR" "$DATA_DIR"
  chown -R "$RUNNER_UID:$RUNNER_UID" "$GRADLE_USER_HOME" "$RUNS_DIR"
  # 슬롯 폴더는 root 소유: 그 안의 lib(러너 jar·CDS 아카이브)를 제출 코드가 바꾸지 못하게 한다 (runner/ 복사본만 실행 사용자 소유)
  chown root:root "$RUNNER_SLOTS_DIR" && chmod 755 "$RUNNER_SLOTS_DIR"
  chown root:root "$DATA_DIR"
  chmod 700 "$DATA_DIR"
fi
exec "$@"
