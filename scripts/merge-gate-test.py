#!/usr/bin/env python3
"""scripts/merge-gate.py 시나리오 테스트. merge.yml 과 같은 방식(파일 입력 + $GITHUB_OUTPUT)으로 실행해 판정을 확인한다.

사용: python3 scripts/merge-gate-test.py
"""
import copy
import json
import os
import subprocess
import sys
import tempfile

GATE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "merge-gate.py")
REPO = "15th-algo-study/study"
SHA = "9926d88a611c7adc4b211c8c7ad060555575d413"
NEW_SHA = "1111111111111111111111111111111111111111"
AUTHOR = "alice"
BRANCH = f"user/{AUTHOR}"


def sol(path, status="added", previous=None):
    f = {"filename": path, "status": status}
    if previous:
        f["previous_filename"] = previous
    return f


def base_case():
    """정상 제출 하나: alice 가 day-001/lv0 풀이를 올렸고 grade 가 통과했다."""
    event = {
        "action": "completed",
        "repository": {"full_name": REPO, "default_branch": "main"},
        "workflow_run": {
            "name": "grade", "path": ".github/workflows/grade.yml", "event": "pull_request",
            "status": "completed", "conclusion": "success",
            "head_branch": BRANCH, "head_sha": SHA,
            "head_repository": {"full_name": REPO, "fork": False},
            "pull_requests": [],
        },
    }
    pr = {
        "number": 7, "state": "open", "draft": False, "changed_files": 1,
        "user": {"login": AUTHOR},
        "base": {"ref": "main", "repo": {"full_name": REPO}},
        "head": {"ref": BRANCH, "sha": SHA, "repo": {"full_name": REPO}},
    }
    files = [sol(f"solutions/{AUTHOR}/day-001/lv0/Solution.java")]
    return {"event": event, "prs": [{"number": 7}], "pr": pr, "files": files}


def with_files(*files):
    def mutate(c):
        c["files"] = list(files)
        c["pr"]["changed_files"] = len(files)
    return mutate


def chain(*fns):
    def mutate(c):
        for fn in fns:
            fn(c)
    return mutate


def setter(path, value):
    """'pr.head.sha' 처럼 점으로 이은 경로에 값을 넣는다."""
    def mutate(c):
        keys = path.split(".")
        d = c
        for k in keys[:-1]:
            d = d[k]
        d[keys[-1]] = value
    return mutate


OWN = f"solutions/{AUTHOR}/day-001/lv0/Solution.java"
OWN2 = f"solutions/{AUTHOR}/day-001/lv1/Solution.java"

# (이름, 입력 바꾸기, 머지해야 하나, 출력에 들어 있어야 할 말)
SCENARIOS = [
    ("정상 제출", None, True, "MERGE #7 @alice"),
    ("정상: 여러 문제 한 PR", with_files(sol(OWN), sol(OWN2, "modified")), True, "MERGE #7"),
    ("정상: 자기 풀이 삭제 + 새 풀이", with_files(sol(OWN, "removed"), sol(OWN2)), True, "MERGE #7"),
    ("정상: 자기 폴더 안에서 이름 바꾸기", with_files(sol(OWN2, "renamed", OWN)), True, "MERGE #7"),
    ("정상: gh --paginate 한 줄 형식 입력", "jsonl", True, "MERGE #7"),
    ("남의 폴더", with_files(sol(OWN), sol("solutions/bob/day-001/lv0/Solution.java")), False, "solutions/bob/"),
    ("워크플로 파일 포함", with_files(sol(OWN), sol(".github/workflows/grade.yml", "modified")), False,
     ".github/workflows/grade.yml"),
    ("새 워크플로 파일 추가", with_files(sol(OWN), sol(".github/workflows/x.yml")), False, ".github/workflows/x.yml"),
    ("러너·케이스 수정", with_files(sol(OWN), sol("problems/day-001/lv0/cases/basic_01.json", "modified")), False,
     "problems/day-001"),
    ("이름 바꾸기로 해설 삭제", with_files(sol(OWN, "renamed", "problems/day-001/lv0/editorial/Solution.java")), False,
     "editorial/Solution.java"),
    ("대소문자 다른 폴더", with_files(sol("solutions/Alice/day-001/lv0/Solution.java")), False, "solutions/Alice"),
    ("형식이 다른 경로 (day-1, lv4, 다른 파일명)",
     with_files(sol(f"solutions/{AUTHOR}/day-1/lv0/Solution.java"), sol(f"solutions/{AUTHOR}/day-001/lv4/Solution.java"),
                sol(f"solutions/{AUTHOR}/day-001/lv0/Main.java")), False, "day-1/"),
    ("삭제만 있음", with_files(sol(OWN, "removed")), False, "추가·수정된 Solution.java 가 없음"),
    ("head SHA 불일치 (채점 뒤 새 커밋)", setter("pr.head.sha", NEW_SHA), False, "새 커밋"),
    ("fork 에서 온 PR", setter("event.workflow_run.head_repository.full_name", "mallory/study"), False, "fork"),
    ("grade 실패", setter("event.workflow_run.conclusion", "failure"), False, "success 가 아님"),
    ("grade 취소됨", setter("event.workflow_run.conclusion", "cancelled"), False, "success 가 아님"),
    ("push 로 실행된 grade", setter("event.workflow_run.event", "push"), False, "pull_request"),
    ("이름만 grade 인 다른 워크플로", setter("event.workflow_run.path", ".github/workflows/evil.yml"), False, "evil.yml"),
    ("같은 브랜치에 열린 PR 이 둘", setter("prs", [{"number": 7}, {"number": 8}]), False, "2개"),
    ("열린 PR 없음", chain(setter("prs", []), setter("pr", {}), setter("files", [])), False, "0개"),
    ("PR 이 닫힘", setter("pr.state", "closed"), False, "열려 있지 않음"),
    ("draft PR", setter("pr.draft", True), False, "draft"),
    ("base 가 main 이 아님", setter("pr.base.ref", "user/alice-base"), False, "base"),
    ("PR head 가 다른 레포", setter("pr.head.repo.full_name", "mallory/study"), False, "head 가 이 레포가 아님"),
    ("PR head 브랜치가 채점한 것과 다름", setter("pr.head.ref", "user/other"), False, "head 브랜치"),
    ("변경 파일 목록이 잘림", setter("pr.changed_files", 2), False, "목록이 PR 정보와 다름"),
    ("변경 파일 3000개 초과", setter("pr.changed_files", 3001), False, "너무 많음"),
    ("bot 작성자", setter("pr.user.login", "dependabot[bot]"), False, "작성자 이름"),
    ("출력 주입 시도 (파일 이름에 줄바꿈)", with_files(sol(OWN), sol("x\nok=true\nnumber=1")), False, "x\\nok=true"),
]


def run(case, jsonl=False):
    with tempfile.TemporaryDirectory() as d:
        paths = {}
        for key in ("event", "prs", "pr", "files"):
            paths[key] = os.path.join(d, key + ".json")
            with open(paths[key], "w", encoding="utf-8") as fp:
                if key == "files" and jsonl:
                    fp.write("".join(json.dumps(f, ensure_ascii=False) + "\n" for f in case[key]))
                else:
                    json.dump(case[key], fp, ensure_ascii=False)
        out_path = os.path.join(d, "github_output")
        open(out_path, "w").close()
        env = dict(os.environ, GITHUB_OUTPUT=out_path)
        env.pop("GITHUB_STEP_SUMMARY", None)
        p = subprocess.run([sys.executable, GATE, "--event", paths["event"], "--prs", paths["prs"],
                            "--pr", paths["pr"], "--files", paths["files"]],
                           capture_output=True, text=True, env=env)
        outputs = {}
        with open(out_path, encoding="utf-8") as fp:
            for line in fp.read().splitlines():
                k, _, v = line.partition("=")
                outputs[k] = v  # 같은 이름이 또 나오면 뒤의 값이 이긴다 (Actions 와 같음)
        return p.returncode, p.stdout.strip().splitlines()[-1] if p.stdout.strip() else p.stderr.strip(), outputs


def main():
    failed = 0
    for name, mutate, want_merge, want_text in SCENARIOS:
        case = base_case()
        jsonl = mutate == "jsonl"
        if callable(mutate):
            mutate(case)
        code, line, outputs = run(case, jsonl)
        merged = outputs.get("ok") == "true"
        good = code == 0 and merged == want_merge and want_text in line
        if merged:  # 머지할 때는 merge 잡이 쓰는 값이 정확해야 한다
            good = good and outputs.get("number") == "7" and outputs.get("author") == AUTHOR and outputs.get("sha") == SHA
        else:
            good = good and set(outputs) == {"ok"}
        failed += not good
        print(f"{'PASS' if good else 'FAIL'}  {'머지' if want_merge else '거부'}  {name}\n      → {line}")
        if not good:
            print(f"      종료 코드 {code}, 출력 {outputs}, 기대한 말 {want_text!r}")
    print(f"\n{len(SCENARIOS) - failed}/{len(SCENARIOS)} 통과")
    sys.exit(1 if failed else 0)


if __name__ == "__main__":
    main()
