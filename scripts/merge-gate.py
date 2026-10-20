#!/usr/bin/env python3
"""grade 워크플로가 끝난 뒤, 그 제출 PR 을 자동 머지해도 되는지 판정한다 (.github/workflows/merge.yml 이 호출).

merge.yml 은 workflow_run 이벤트로 항상 main 의 정의로 실행되지만, 성공한 grade 실행 자체는 PR 쪽 정의였을 수
있으므로 결과를 그대로 믿지 않고 다음을 다시 확인한다.

  1. grade 실행: conclusion 이 success, 이벤트가 pull_request, 워크플로 파일이 .github/workflows/grade.yml,
     head 레포가 이 레포 (fork 아님)
  2. PR: 그 브랜치의 열린 PR 이 정확히 하나, base 가 기본 브랜치, head 브랜치·SHA 가 채점한 것과 같음
     (채점 뒤 새 커밋이 들어왔으면 머지하지 않는다 — 새 커밋의 grade 가 다시 판정한다)
  3. 변경 파일: 전부 solutions/<PR 작성자>/day-NNN/lv<N>/Solution.java (이름이 바뀐 파일은 이전 경로도),
     목록이 잘리지 않았고, 추가·수정된 풀이가 하나 이상
     → PR 이 워크플로·러너·케이스를 건드리지 않았으므로, 그 PR 에서 돈 grade 는 main 의 정의로 채점했다.

입력 (merge.yml 이 GitHub API 로 받아 둔 파일):
  --event  workflow_run 이벤트 페이로드 ($GITHUB_EVENT_PATH)
  --prs    GET /repos/{repo}/pulls?state=open&head={owner}:{head_branch} 결과 (JSON 배열)
  --pr     GET /repos/{repo}/pulls/{n} 결과 (위 목록이 정확히 하나일 때만. 아니면 {})
  --files  GET /repos/{repo}/pulls/{n}/files 결과 (JSON 배열, 또는 한 줄에 객체 하나씩)

출력: 표준 출력에 판정 한 줄 (MERGE ... / SKIP: 이유). $GITHUB_OUTPUT 이 있으면 ok, number, author, sha 를 쓴다.
판정이 나면 머지 여부와 관계없이 종료 코드 0, 입력 파일을 읽지 못하면 0 이 아닌 값.
로컬 시나리오 테스트: python3 scripts/merge-gate-test.py
"""
import argparse
import json
import os
import re
import sys

GRADE_WORKFLOW = ".github/workflows/grade.yml"
LOGIN = re.compile(r"[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?")  # GitHub 사용자 이름 ([bot] 계정 제외)
SHA = re.compile(r"[0-9a-f]{40}|[0-9a-f]{64}")
MAX_FILES = 3000  # pulls/{n}/files API 가 돌려주는 최대 개수. 넘으면 목록이 잘린다


class Skip(Exception):
    pass


def require(cond, reason):
    if not cond:
        raise Skip(reason)


def q(s):
    # 파일 이름·브랜치 이름은 PR 작성자가 정한다. 줄바꿈 등을 이스케이프해 로그·출력에 그대로 섞이지 않게 한다
    return json.dumps(s, ensure_ascii=False)


def decide(event, prs, pr, files):
    """머지해도 되면 {number, author, sha} 를 돌려주고, 아니면 Skip 을 던진다."""
    run = event.get("workflow_run") or {}
    repository = event.get("repository") or {}
    repo = repository.get("full_name")
    base = repository.get("default_branch") or "main"
    require(repo, "이벤트에 레포 정보가 없음")

    # 1. grade 실행
    require(run.get("conclusion") == "success", f"grade 결과가 success 가 아님 ({q(run.get('conclusion'))})")
    require(run.get("event") == "pull_request", f"pull_request 로 실행된 grade 가 아님 ({q(run.get('event'))})")
    path = (run.get("path") or "").split("@")[0]
    require(path == GRADE_WORKFLOW, f"{GRADE_WORKFLOW} 가 아닌 워크플로 ({q(run.get('path'))})")
    head_repo = (run.get("head_repository") or {}).get("full_name")
    require(head_repo == repo, f"다른 레포(fork)에서 온 PR ({q(head_repo)})")
    sha, branch = run.get("head_sha") or "", run.get("head_branch") or ""
    require(SHA.fullmatch(sha), f"채점한 커밋 SHA 가 올바르지 않음 ({q(sha)})")
    require(branch, "채점한 브랜치 이름이 없음")

    # 2. PR
    require(len(prs) == 1, f"브랜치 {q(branch)} 의 열린 PR 이 {len(prs)}개 (정확히 1개여야 함)")
    number = pr.get("number")
    require(isinstance(number, int) and number == prs[0].get("number"), "PR 정보가 목록과 맞지 않음")
    require(pr.get("state") == "open", f"PR #{number} 이 열려 있지 않음")
    require(not pr.get("draft"), f"PR #{number} 이 draft")
    pr_base, pr_head = pr.get("base") or {}, pr.get("head") or {}
    require(pr_base.get("ref") == base, f"PR #{number} 의 base 가 {base} 가 아님 ({q(pr_base.get('ref'))})")
    require((pr_head.get("repo") or {}).get("full_name") == repo, f"PR #{number} 의 head 가 이 레포가 아님")
    require(pr_head.get("ref") == branch, f"PR #{number} 의 head 브랜치가 채점한 브랜치와 다름")
    require(pr_head.get("sha") == sha,
            f"PR #{number} 의 head({q(pr_head.get('sha'))})가 채점한 커밋({sha})과 다름 — 채점 뒤 새 커밋이 들어옴")
    author = (pr.get("user") or {}).get("login") or ""
    require(LOGIN.fullmatch(author), f"PR 작성자 이름이 올바르지 않음 ({q(author)})")

    # 3. 변경 파일
    changed = pr.get("changed_files")
    require(isinstance(changed, int) and changed <= MAX_FILES,
            f"변경 파일이 너무 많음 ({q(changed)}개) — 목록이 잘려 전부 확인할 수 없음")
    require(changed == len(files), f"변경 파일 목록이 PR 정보와 다름 (목록 {len(files)}개, PR {changed}개)")
    allowed = re.compile(rf"solutions/{re.escape(author)}/day-[0-9]{{3}}/lv[0-3]/Solution\.java")
    bad, graded = [], 0
    for f in files:
        # 이름 바꾸기는 이전 경로를 지우는 것이므로 이전 경로도 허용 범위여야 한다
        for p in (f.get("filename"), f.get("previous_filename")):
            if p is not None and not (isinstance(p, str) and allowed.fullmatch(p)):
                bad.append(p)
        if f.get("status") != "removed":
            graded += 1
    require(not bad, f"solutions/{author}/day-NNN/lv<N>/Solution.java 가 아닌 파일: " + ", ".join(q(p) for p in bad[:20])
            + (f" 외 {len(bad) - 20}개" if len(bad) > 20 else ""))
    require(graded > 0, "추가·수정된 Solution.java 가 없음")

    return {"number": number, "author": author, "sha": sha}


def load(path):
    with open(path, encoding="utf-8") as fp:
        return json.load(fp)


def load_list(path):
    """JSON 배열 또는 한 줄에 JSON 하나씩(gh api --paginate --jq '.[] | @json') 인 파일."""
    with open(path, encoding="utf-8") as fp:
        text = fp.read()
    if text.lstrip().startswith("["):
        return json.loads(text)
    return [json.loads(line) for line in text.splitlines() if line.strip()]


def escape_command(s):
    # 워크플로 명령(::warning::) 메시지 이스케이프
    return s.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")


def main():
    ap = argparse.ArgumentParser(description="grade 통과 PR 을 자동 머지해도 되는지 판정")
    ap.add_argument("--event", required=True)
    ap.add_argument("--prs", required=True)
    ap.add_argument("--pr", required=True)
    ap.add_argument("--files", required=True)
    args = ap.parse_args()

    event, prs, pr, files = load(args.event), load(args.prs), load(args.pr), load_list(args.files)
    if not isinstance(prs, list):
        sys.exit(f"--prs 가 JSON 배열이 아님: {args.prs}")
    try:
        result = decide(event, prs, pr or {}, files)
        ok = True
        line = f"MERGE #{result['number']} @{result['author']} {result['sha']}"
    except Skip as e:
        result, ok = {}, False
        line = f"SKIP: {e}".replace("\r", " ").replace("\n", " ")
        print(f"::warning title=자동 머지 안 함::{escape_command(str(e))}")
    print(line)

    if os.environ.get("GITHUB_OUTPUT"):
        with open(os.environ["GITHUB_OUTPUT"], "a", encoding="utf-8") as out:
            if ok:  # 셋 다 위에서 형식을 검사한 값이라 줄바꿈이 섞일 수 없다
                out.write(f"number={result['number']}\nauthor={result['author']}\nsha={result['sha']}\n")
            out.write(f"ok={'true' if ok else 'false'}\n")
    if os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as summary:
            summary.write(("### ✅ 자동 머지 조건 충족\n\n" if ok else "### ⏭️ 자동 머지 안 함\n\n") + line + "\n")


if __name__ == "__main__":
    main()
