#!/usr/bin/env bash
# 다중 사용자(서버) 모드 종단 테스트 — mock GitHub + 시연용 bare 원격만 사용한다.
#   app/dev/multi-test.sh
# 안전장치: 테스트 포트(APP 15025, MOCK 15099)가 이미 쓰이고 있으면 시작하지 않고,
# 띄운 서버가 다중 사용자 모드 + mock GitHub 로 떠 있는지 확인한 뒤에만 요청을 보낸다.
# (실제 앱이 쓰는 15015 에는 절대 요청하지 않는다)
set -u
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
APP_PORT=15025
MOCK_PORT=15099
D="$ROOT/app/dev/.demo-multi"
B="http://localhost:$APP_PORT/api"
M="http://localhost:$MOCK_PORT"
J="$(mktemp -d)"
FAIL=0
pids=()

cleanup() { for p in "${pids[@]}"; do kill "$p" 2>/dev/null; done; rm -rf "$J"; }
trap cleanup EXIT
ok()   { echo "  ✔ $1"; }
bad()  { echo "  ✘ $1"; FAIL=$((FAIL + 1)); }
check() { if [ "$2" = "$3" ]; then ok "$1"; else bad "$1 (기대 '$3', 실제 '$2')"; fi; }
json() { python3 -c "import json,sys; d=json.load(sys.stdin); print($1)"; }

for p in $APP_PORT $MOCK_PORT; do
  if lsof -nP -iTCP:$p -sTCP:LISTEN >/dev/null 2>&1; then echo "포트 $p 가 이미 사용 중입니다. 테스트를 중단합니다."; exit 2; fi
done

# ── 시연 환경 (별도 폴더) ──
DEMO_DIR="$D" "$ROOT/app/dev/demo.sh" setup >/dev/null
MOCK_PORT=$MOCK_PORT MOCK_BARE_REPO="$D/origin.git" node "$ROOT/app/dev/mock-github.js" > "$D/mock.log" 2>&1 &
pids+=($!)
REPO_ROOT="$D/work" DATA_DIR="$D/data" PORT=$APP_PORT MULTI_USER=1 RUNNER_SLOTS=2 SYNC_INTERVAL_MIN=0 \
GITHUB_WEB_URL="$M" GITHUB_API_URL="$M" GIT_REMOTE_URL="$D/origin.git" \
  node "$ROOT/app/server.js" > "$D/server.log" 2>&1 &
pids+=($!)
for _ in $(seq 1 40); do grep -q "localhost:$APP_PORT" "$D/server.log" 2>/dev/null && break; sleep 0.25; done

echo "== 0. 안전 확인"
grep -q "알고리즘 스터디 앱: http://localhost:$APP_PORT" "$D/server.log" || { echo "테스트 서버가 뜨지 않았습니다."; cat "$D/server.log"; exit 2; }
grep -q "서버(다중 사용자)" "$D/server.log" || { echo "서버가 다중 사용자 모드가 아닙니다. 중단."; exit 2; }
grep -q "GitHub    : $M" "$D/server.log" || { echo "서버가 mock GitHub 를 가리키지 않습니다. 중단."; exit 2; }
check "응답 서버가 다중 사용자 모드" "$(curl -s $B/state | json "d['multiUser']")" "True"
curl -s "$M/login/device" | grep -q "(가짜) GitHub" && ok "mock GitHub 응답" || { echo "mock 이 아닙니다. 중단."; exit 2; }

code() { python3 -c "import json,sys;print(json.dumps({'code':open(sys.argv[1]).read()}))" "$1"; }
L0=$(code "$ROOT/problems/day-001/lv0/editorial/Solution.java")
post() { curl -s -b "$J/$1" -c "$J/$1" -X POST -H 'Content-Type: application/json' "${@:3}" "$B/$2"; }
login() { # $1 jar, $2 login
  local uc
  uc=$(curl -s -c "$J/$1" -b "$J/$1" -X POST $B/auth/device | json "d['user_code']")
  curl -s -o /dev/null -X POST -d "code=$uc&login=$2" "$M/login/device/approve"
  sleep 1.3
  curl -s -c "$J/$1" -b "$J/$1" -X POST $B/auth/poll
}

echo "== 1. 비로그인 사용자"
curl -s -c "$J/guest" -b "$J/guest" $B/state >/dev/null
grep -q algo_sid "$J/guest" && ok "세션 쿠키 발급" || bad "세션 쿠키 없음"
check "비로그인 실행 허용" "$(curl -s -b "$J/guest" -X POST -H 'Content-Type: application/json' -d "$L0" $B/run/1/0 | json "d['status']")" "PASS"
check "초안 저장 거부(401)" "$(curl -s -o /dev/null -w '%{http_code}' -b "$J/guest" -X PUT -H 'Content-Type: application/json' -d '{"code":"x"}' $B/draft/1/0)" "401"
check "제출 거부(401)" "$(curl -s -o /dev/null -w '%{http_code}' -b "$J/guest" -X POST -H 'Content-Type: application/json' -d "$L0" $B/submit/1/0)" "401"
# 비로그인 실행은 서버 전체에서 한 번에 하나 — 다른 브라우저(쿠키)여도 두 번째는 429
LOOP='{"code":"class Solution { public int solution(int[] numbers) { while (true) {} } }"}'
curl -s -b "$J/guest" -X POST -H 'Content-Type: application/json' -d "$LOOP" $B/run/1/0 > "$J/loop.json" &
p0=$!
sleep 1
check "비로그인 동시 실행 거부(429)" "$(curl -s -o /dev/null -w '%{http_code}' -X POST -H 'Content-Type: application/json' -d "$L0" $B/run/1/0)" "429"
wait $p0
check "무한 루프는 20초에 중단" "$(json "'초를 넘어 중단' in d['report']" < "$J/loop.json")" "True"

echo "== 2. 로그인"
check "alice 로그인" "$(login A alice | json "d['status']")" "ok"
check "bob 로그인" "$(login Bb bob | json "d['status']")" "ok"
r=$(login C outsider-x); check "멤버 아닌 사람 거부" "$(echo "$r" | json "d['status']")" "error"; echo "    → $(echo "$r" | json "d['message']")"
check "outsider 는 비로그인 상태" "$(curl -s -b "$J/C" $B/me | json "d['user']")" "None"
check "alice 세션 = alice" "$(curl -s -b "$J/A" $B/me | json "d['user']['login']")" "alice"
check "bob 세션 = bob" "$(curl -s -b "$J/Bb" $B/me | json "d['user']['login']")" "bob"

echo "== 3. 동시 제출 (alice, bob 같은 문제)"
post A submit/1/0 -d "$L0" > "$J/subA.json" &
p1=$!
post Bb submit/1/0 -d "$L0" > "$J/subB.json" &
p2=$!
sleep 0.4
check "alice 중복 실행 거부(429)" "$(curl -s -o /dev/null -w '%{http_code}' -b "$J/A" -X POST -H 'Content-Type: application/json' -d "$L0" $B/run/1/0)" "429"
wait $p1 $p2
check "alice 채점" "$(json "d['grade']['status']" < "$J/subA.json")" "PASS"
check "bob 채점" "$(json "d['grade']['status']" < "$J/subB.json")" "PASS"
PA=$(json "d['pr']['number']" < "$J/subA.json"); PB=$(json "d['pr']['number']" < "$J/subB.json")
[ "$PA" != "$PB" ] && ok "PR 이 사람마다 따로 생성 (#$PA, #$PB)" || bad "PR 번호가 같음"
check "user/alice author" "$(git -C "$D/origin.git" log -1 --format='%an' user/alice)" "alice"
check "user/bob author" "$(git -C "$D/origin.git" log -1 --format='%an' user/bob)" "bob"
check "작업 트리에 풀이 파일 없음" "$(git -C "$D/work" status --short -- solutions | wc -l | tr -d ' ')" "0"

echo "== 4. 사용자별 상태·초안 분리"
curl -s -b "$J/A" -X PUT -H 'Content-Type: application/json' -d '{"code":"// alice 초안"}' $B/draft/1/1 >/dev/null
check "alice lv0 = submitted" "$(curl -s -b "$J/A" $B/state | json "d['days'][0]['levels'][0]['status']")" "submitted"
check "alice lv1 = draft" "$(curl -s -b "$J/A" $B/state | json "d['days'][0]['levels'][1]['status']")" "draft"
check "bob lv1 = none (alice 초안이 안 보임)" "$(curl -s -b "$J/Bb" $B/state | json "d['days'][0]['levels'][1]['status']")" "none"
check "bob 이 여는 lv1 = 템플릿" "$(curl -s -b "$J/Bb" $B/problem/1/1 | json "d['codeSource']")" "template"

echo "== 5. 두 PR 머지 → 동기화"
for n in $PA $PB; do curl -s -o /dev/null -X POST "$M/__admin/merge/$n"; done
post A sync >/dev/null
check "main 에 두 사람 풀이" "$(git -C "$D/origin.git" ls-tree -r --name-only main -- solutions | wc -l | tr -d ' ')" "2"
check "작업 트리도 따라옴(fast-forward)" "$(ls "$D/work/solutions" 2>/dev/null | tr '\n' ' ')" "alice bob "
check "alice lv0 = merged" "$(curl -s -b "$J/A" $B/state | json "d['days'][0]['levels'][0]['status']")" "merged"
check "lv0 푼 사람 2명" "$(curl -s -b "$J/Bb" $B/state | json "d['days'][0]['levels'][0]['solvers']")" "2"
check "출석표 2명" "$(curl -s $B/attendance | json "len(d['members'])")" "2"
check "alice 가 bob 풀이 열람" "$(curl -s -b "$J/A" $B/solutions/1/0/bob | json "'class Solution' in d['code']")" "True"

echo "== 6. 다시 제출(새 브랜치) · 로그아웃"
check "bob 재제출 PR 생성" "$(post Bb submit/1/0 -d "$(code "$ROOT/problems/day-001/lv0/editorial/Solution.java" | sed 's/answer/total/g')" | json "d['pr']['created']")" "True"
post A auth/logout >/dev/null
check "alice 로그아웃" "$(curl -s -b "$J/A" $B/me | json "d['user']")" "None"
check "bob 은 그대로" "$(curl -s -b "$J/Bb" $B/me | json "d['user']['login']")" "bob"

echo "== 7. 줄바꿈 (CRLF)"
crlf() { python3 -c "import json,sys;print(json.dumps({'code':open(sys.argv[1]).read().replace('\n','\r\n')}))" "$1"; }
check "carol 로그인" "$(login Cc carol | json "d['status']")" "ok"
post Cc submit/1/0 -d "$(crlf "$ROOT/problems/day-001/lv0/editorial/Solution.java")" >/dev/null
check "CRLF 로 제출해도 LF 로 커밋" "$(git -C "$D/origin.git" cat-file -p user/carol:solutions/carol/day-001/lv0/Solution.java | tr -cd '\r' | wc -c | tr -d ' ')" "0"
# 예전 앱처럼 CRLF 블롭이 main 에 들어간 상황 → 작업 트리에 "수정됨"이 생긴다
P="$D/pusher"; git clone -q "$D/origin.git" "$P"
git -C "$P" -c user.name=t -c user.email=t@t commit -q --allow-empty -m base
blob=$(printf 'class Solution {\r\n}\r\n' | git -C "$P" hash-object -w --stdin)
git -C "$P" update-index --add --cacheinfo "100644,$blob,solutions/dave/day-001/lv0/Solution.java"
git -C "$P" -c user.name=t -c user.email=t@t commit -q -m "crlf 풀이" && git -C "$P" push -q origin HEAD:main
post Bb sync >/dev/null
check "CRLF 파일이 작업 트리에서 수정됨으로 보임" "$(git -C "$D/work" status --short -- solutions/dave | cut -c1-2)" " M"
git -C "$P" checkout -q -- solutions/dave && git -C "$P" add --renormalize solutions/dave && git -C "$P" -c user.name=t -c user.email=t@t commit -q -m "줄바꿈 정리" && git -C "$P" push -q origin HEAD:main
check "LF 정리 커밋도 동기화됨" "$(post Bb sync | json "d['ok']")" "True"
check "작업 트리 = origin/main" "$(git -C "$D/work" rev-parse HEAD)" "$(git -C "$D/origin.git" rev-parse main)"
check "작업 트리 깨끗함" "$(git -C "$D/work" status --short -- solutions | wc -l | tr -d ' ')" "0"

echo
[ $FAIL -eq 0 ] && echo "✅ 다중 사용자 모드 테스트 통과" || echo "❌ 실패 $FAIL건 (로그: $D/server.log)"
exit $FAIL
