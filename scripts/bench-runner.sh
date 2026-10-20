#!/usr/bin/env bash
# 채점 시간이 어디서 드는지 구간별로 잰다 (서버 성능 점검용).
#
# 서버 컨테이너 안에서 (앱에서 '실행'을 한 번 누른 뒤):
#   docker compose -f deploy/compose.yml --env-file deploy/.env exec -u 10001 -e HOME=/tmp app bash /workspace/scripts/bench-runner.sh
# 로컬에서:
#   scripts/bench-runner.sh runner
set -u
REPO="$(cd "$(dirname "$0")/.." && pwd)"
PROJECT="${1:-/runs/slots/0/runner}"
[ -x "$PROJECT/gradlew" ] || { echo "러너 프로젝트가 없습니다: $PROJECT (앱에서 '실행'을 한 번 누른 뒤 다시 시도하세요)"; exit 1; }
cd "$PROJECT" || exit 1
ms() { echo $(( ($(date +%s%N) - $1) / 1000000 )); }

echo "== 환경"
echo "  CPU   : $(grep -m1 'model name' /proc/cpuinfo 2>/dev/null | cut -d: -f2 | sed 's/^ //') · $(nproc 2>/dev/null) vCPU"
echo "  Java  : $(java -version 2>&1 | head -1)"
echo "  데몬  : $(./gradlew --status 2>/dev/null | grep -cE 'IDLE|BUSY') 개 실행 중"

echo
echo "== Gradle 채점 3회 (day-001 lv0 editorial)"
printf "  %-4s %8s %8s %8s %8s %8s  %s\n" "회차" "전체" "Startup" "설정" "작업" ":test" "데몬"
for i in 1 2 3; do
  s=$(date +%s%N)
  out=$(./gradlew test -Puser=editorial -Pday=1 -Plevel=0 -PrepoRoot="$REPO" --profile --console=plain 2>&1)
  total=$(ms "$s")
  daemon=$(echo "$out" | grep -q "Starting a Gradle Daemon" && echo "새로 시작" || echo "재사용")
  f=$(ls -t build/reports/profile/*.html 2>/dev/null | head -1)
  read -r startup config tasks test < <(python3 - "$f" <<'PY'
import re, sys
h = open(sys.argv[1]).read()
def g(label, cls=''):
    m = re.search(r'<td%s>%s</td>\s*<td class="numeric">([^<]+)</td>' % (cls, re.escape(label)), h)
    return m.group(1) if m else '?'
print(g('Startup'), g('Configuring Projects'), g('Task Execution'), g(':test', ' class="indentPath"'))
PY
)
  printf "  %-4s %6sms %8s %8s %8s %8s  %s\n" "$i" "$total" "$startup" "$config" "$tasks" "$test" "$daemon"
done

echo
echo "== JVM 자체 비용 (Gradle 없이)"
s=$(date +%s%N); java -version >/dev/null 2>&1; echo "  JVM 기동         : $(ms "$s") ms"
tmp=$(mktemp -d)
cp "$REPO/problems/day-001/lv0/editorial/Solution.java" "$tmp/"
s=$(date +%s%N); javac -d "$tmp" "$tmp/Solution.java"; echo "  javac 1회(콜드)  : $(ms "$s") ms"
rm -rf "$tmp"

cat <<'EOF'

읽는 법
- 데몬이 매번 '새로 시작'이면 데몬이 재사용되지 않는 것 → 그것부터 고치면 크게 줄어듭니다.
- ':test' 가 대부분이면 테스트 JVM 기동 + 제출 코드 컴파일(javac) 비용 → 위 'JVM 자체 비용' 과 함께 CPU 성능 문제.
- 'Startup' 이 1초를 넘으면 Gradle 클라이언트 JVM 기동이 느린 것 (역시 CPU).
EOF
