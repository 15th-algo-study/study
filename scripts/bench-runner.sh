#!/usr/bin/env bash
# 채점 시간 측정 (서버 성능 점검용): 앱이 쓰는 직접 실행(java + CDS)과 예전 Gradle 경로를 비교한다.
#
# 서버 컨테이너 안에서 (앱이 시작되며 슬롯 준비를 마친 뒤):
#   docker compose -f deploy/compose.yml --env-file deploy/.env exec -u 10001 -e HOME=/tmp app bash /workspace/scripts/bench-runner.sh
# 로컬(앱의 DATA_DIR 이 app/data 일 때):
#   scripts/bench-runner.sh app/data/runner-slots/0
set -u
REPO="$(cd "$(dirname "$0")/.." && pwd)"
SLOT="${1:-/runs/slots/0}"
LIB="$SLOT/lib"
PROJECT="$SLOT/runner"
[ -f "$LIB/stamp" ] || { echo "채점 슬롯이 아직 준비되지 않았습니다: $SLOT (앱 시작 후 잠시 기다리거나 '실행'을 한 번 누르세요)"; exit 1; }
ms() { echo $(( ($(date +%s%N) - $1) / 1000000 )); }
CP=$(ls "$LIB"/*.jar | sort | paste -sd: -)
SRC="$REPO/problems/day-001/lv0/editorial/Solution.java"
OUT=$(mktemp -d)
JVM=(-Xmx1g -Xss64m -XX:+UseParallelGC -Dfile.encoding=UTF-8
     -Drunner.root="$REPO" -Drunner.outDir="$OUT" -Drunner.user=bench -Drunner.day=1 -Drunner.level=0
     -Drunner.source="$SRC" -Drunner.json="$OUT/r.json" -Drunner.timeScale=1)

echo "== 환경"
echo "  CPU   : $(grep -m1 'model name' /proc/cpuinfo 2>/dev/null | cut -d: -f2 | sed 's/^ //') · $(nproc 2>/dev/null) vCPU"
echo "  Java  : $(java -version 2>&1 | head -1)"
echo "  CDS   : $([ -f "$LIB/app.jsa" ] && echo "있음 ($(du -h "$LIB/app.jsa" | cut -f1))" || echo 없음)"

echo
echo "== 앱 방식: java 로 바로 채점 (day-001 lv0 editorial, 3회)"
for i in 1 2 3; do
  s=$(date +%s%N)
  if [ -f "$LIB/app.jsa" ]; then
    java -XX:SharedArchiveFile="$LIB/app.jsa" -Xshare:auto -Xlog:cds=off -Xlog:cds+dynamic=off "${JVM[@]}" -cp "$CP" study.runner.Main >/dev/null 2>&1
  else
    java "${JVM[@]}" -cp "$CP" study.runner.Main >/dev/null 2>&1
  fi
  echo "  $i회: $(ms "$s") ms  ($(python3 -c "import json;d=json.load(open('$OUT/r.json'));print(d['status'], d['summary'])" 2>/dev/null))"
done

echo
echo "== 참고: 예전 Gradle 경로 (1회, 데몬 없이 측정되면 더 오래 걸림)"
if [ -x "$PROJECT/gradlew" ]; then
  s=$(date +%s%N)
  (cd "$PROJECT" && ./gradlew test -Puser=editorial -Pday=1 -Plevel=0 -PrepoRoot="$REPO" -q >/dev/null 2>&1)
  echo "  $(ms "$s") ms"
else
  echo "  (러너 복사본 없음)"
fi
rm -rf "$OUT"
