#!/usr/bin/env bash
# 호스트용: GitHub username 목록을 읽어 조직에 초대하고 members 팀에 넣는다.
#
#   scripts/invite.sh usernames.txt            # 초대 실행
#   scripts/invite.sh usernames.txt --dry-run  # 누구를 초대할지 보기만
#
# usernames.txt: 한 줄에 하나. 앞의 @ 와 빈 줄, # 주석은 무시한다.
# 필요: gh CLI 로 조직 owner 계정 로그인 (gh auth login). 팀은 미리 만들어 두고 레포에 Write 권한을 준다 (docs/HOST.md 2절).
set -euo pipefail

ORG="${ORG:-15th-algo-study}"
TEAM="${TEAM:-members}"
FILE="${1:-}"
DRY="${2:-}"

if [ -z "$FILE" ] || [ ! -f "$FILE" ]; then
  echo "사용법: $0 <usernames 파일> [--dry-run]" >&2
  exit 1
fi
command -v gh >/dev/null || { echo "gh CLI 가 필요합니다: https://cli.github.com" >&2; exit 1; }
gh auth status >/dev/null 2>&1 || { echo "gh auth login 으로 먼저 로그인하세요." >&2; exit 1; }

# 팀이 있는지 확인
if ! gh api "orgs/$ORG/teams/$TEAM" >/dev/null 2>&1; then
  echo "조직 $ORG 에 팀 '$TEAM' 이 없습니다. 먼저 만들어 주세요 (Organization → Teams → New team)." >&2
  exit 1
fi

ok=0; skip=0; fail=0
while IFS= read -r raw || [ -n "$raw" ]; do
  u="${raw%%#*}"; u="${u//[[:space:]]/}"; u="${u#@}"
  [ -z "$u" ] && continue
  if ! gh api "users/$u" --jq .login >/dev/null 2>&1; then
    echo "✘ $u: 존재하지 않는 사용자"; fail=$((fail + 1)); continue
  fi
  state=$(gh api "orgs/$ORG/memberships/$u" --jq .state 2>/dev/null || echo "none")
  if [ "$state" = "active" ]; then
    echo "· $u: 이미 멤버 (팀만 확인)"; skip=$((skip + 1))
  elif [ "$state" = "pending" ]; then
    echo "· $u: 이미 초대됨 (수락 대기)"; skip=$((skip + 1))
  elif [ -n "$DRY" ]; then
    echo "→ $u: 초대 예정"; ok=$((ok + 1)); continue
  fi
  if [ -z "$DRY" ]; then
    # 팀 멤버십 PUT 은 조직 밖 사용자에게 초대 메일을 보내고, 수락하면 팀에 들어간다
    if gh api -X PUT "orgs/$ORG/teams/$TEAM/memberships/$u" -f role=member --silent 2>/dev/null; then
      [ "$state" = "none" ] && { echo "✔ $u: 초대 보냄"; ok=$((ok + 1)); }
    else
      echo "✘ $u: 초대 실패 (권한·플랜 제한 확인)"; fail=$((fail + 1))
    fi
  fi
done < "$FILE"

echo
echo "초대 ${ok}명 · 건너뜀 ${skip}명 · 실패 ${fail}명"
[ -n "$DRY" ] && echo "(--dry-run: 실제로 보내지 않았습니다)"
echo "초대 메일을 받은 사람은 수락 후 docs/ONBOARDING.md 를 따라 시작하면 됩니다."
