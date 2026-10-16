#!/usr/bin/env bash
# 모든(또는 지정한) 문제의 구조를 검사하고 editorial 을 러너로 일괄 채점한다.
# 사용법: scripts/verify-problems.sh [day-spec]   예) scripts/verify-problems.sh 1-5
set -u
cd "$(dirname "$0")/.."

SPEC="${1:-all}"
errors=0

in_spec() { # day 번호가 SPEC 에 포함되는지
  local d=$((10#$1)) part a b
  [ "$SPEC" = "all" ] && return 0
  IFS=',' read -ra parts <<< "$SPEC"
  for part in "${parts[@]}"; do
    if [[ "$part" == *-* ]]; then a=${part%-*}; b=${part#*-}; else a=$part; b=$part; fi
    (( d >= 10#$a && d <= 10#$b )) && return 0
  done
  return 1
}

count=0
for dir in problems/day-*/lv*; do
  [ -d "$dir" ] || continue
  day=${dir#problems/day-}; day=${day%%/*}
  in_spec "$day" || continue
  count=$((count + 1))
  for f in README.md Solution.java editorial/README.md editorial/Solution.java; do
    [ -f "$dir/$f" ] || { echo "✘ $dir: $f 없음"; errors=$((errors + 1)); }
  done
  basic=$(ls "$dir"/cases/basic_*.json 2>/dev/null | wc -l | tr -d ' ')
  large=$(ls "$dir"/cases/large_*.json 2>/dev/null | wc -l | tr -d ' ')
  (( basic >= 5 && basic <= 8 )) || { echo "✘ $dir: basic 케이스 $basic개 (5~8개 필요)"; errors=$((errors + 1)); }
  (( large >= 2 && large <= 3 )) || { echo "✘ $dir: large 케이스 $large개 (2~3개 필요)"; errors=$((errors + 1)); }
  for c in "$dir"/cases/*.json; do
    grep -q '"expected' "$c" || { echo "✘ $c: expected 없음 (runner 에서 ./gradlew fillExpected -Pday=$day)"; errors=$((errors + 1)); }
  done
  lv=${dir##*/lv}
  grep -q "^# \[lv$lv\]" "$dir/README.md" 2>/dev/null || { echo "✘ $dir/README.md: 제목이 '# [lv$lv] ...' 형식이 아님"; errors=$((errors + 1)); }
done

if (( count == 0 )); then echo "검사할 문제가 없습니다 (spec=$SPEC)"; exit 1; fi
echo "구조 검사: 문제 ${count}개, 오류 ${errors}개"
(( errors == 0 )) || exit 1

echo "editorial 채점 중..."
( cd runner && ./gradlew test -Puser=editorial -Pday="$SPEC" -q ) || exit 1
