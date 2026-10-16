#!/usr/bin/env bash
# 로컬 시연 환경: 실제 GitHub 없이 로그인 → 풀이 → 제출 → PR → (가짜) 머지 → 출석표 흐름을 재현한다.
#   app/dev/demo.sh setup   # bare 원격 + 작업 clone + 시작일=오늘 설정
#   app/dev/demo.sh run     # mock GitHub(15099) 와 앱(15015) 실행
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
DEMO="${DEMO_DIR:-$ROOT/app/dev/.demo}"
MOCK_PORT="${MOCK_PORT:-15099}"

setup() {
  rm -rf "$DEMO"; mkdir -p "$DEMO"
  git init -q --bare -b main "$DEMO/origin.git"
  mkdir "$DEMO/work"
  rsync -a --exclude .git --exclude node_modules --exclude runner/build --exclude runner/.gradle \
        --exclude app/data --exclude app/dev/.demo --exclude solutions "$ROOT/" "$DEMO/work/"
  python3 - "$DEMO/work/config.json" <<'EOF'
import json, sys, datetime
p = sys.argv[1]; c = json.load(open(p))
c["start_date"] = datetime.date.today().isoformat()   # 오늘을 1일차로
c["github_app_client_id"] = "mock-client-id"
json.dump(c, open(p, "w"), ensure_ascii=False, indent=2)
EOF
  git -C "$DEMO/work" init -q -b main
  git -C "$DEMO/work" -c user.name=host -c user.email=host@example.com add -A
  git -C "$DEMO/work" -c user.name=host -c user.email=host@example.com commit -q -m "demo: 초기 레포"
  git -C "$DEMO/work" remote add origin "$DEMO/origin.git"
  git -C "$DEMO/work" push -q origin main
  ln -s "$ROOT/app/node_modules" "$DEMO/work/app/node_modules"
  echo "시연 환경 준비 완료: $DEMO"
}

run() {
  [ -d "$DEMO/work" ] || setup
  MOCK_PORT="$MOCK_PORT" MOCK_BARE_REPO="$DEMO/origin.git" node "$ROOT/app/dev/mock-github.js" &
  MOCK_PID=$!
  trap 'kill $MOCK_PID 2>/dev/null' EXIT
  REPO_ROOT="$DEMO/work" DATA_DIR="$DEMO/data" PORT="${PORT:-15015}" \
  GITHUB_WEB_URL="http://localhost:$MOCK_PORT" GITHUB_API_URL="http://localhost:$MOCK_PORT" \
  GIT_REMOTE_URL="$DEMO/origin.git" \
    node "$ROOT/app/server.js"
}

case "${1:-run}" in
  setup) setup ;;
  run) run ;;
  *) echo "사용법: $0 [setup|run]"; exit 1 ;;
esac
