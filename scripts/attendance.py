#!/usr/bin/env python3
"""출석표 생성기.

solutions/<login>/day-NNN/lv<N>/Solution.java 를 훑어
  --readme README.md : 마커(<!-- attendance:start --> ~ <!-- attendance:end -->) 사이를 갱신
  --json  경로|-     : 앱이 쓰는 JSON 을 저장(또는 표준 출력)
표준 라이브러리만 사용한다.

일차 계산: config.json 의 start_date 부터 매일(주말 포함) 세고, holidays 에 적힌 날짜만 건너뛴다.
start_date 가 null 이면 스터디 시작 전으로 보고 총 풀이 수만 표시한다.
"""
from __future__ import annotations  # 3.9 호환 (X | None 표기)

import argparse
import datetime as dt
import json
import re
import sys
from pathlib import Path
from zoneinfo import ZoneInfo

START_MARK = "<!-- attendance:start -->"
END_MARK = "<!-- attendance:end -->"
TOTAL_DAYS = 150
LEVELS = 4
SOLUTION_RE = re.compile(r"^day-(\d{3})/lv([0-3])/Solution\.java$")


def load_config(root: Path) -> dict:
    cfg = json.loads((root / "config.json").read_text(encoding="utf-8"))
    cfg.setdefault("holidays", [])
    cfg.setdefault("timezone", "Asia/Seoul")
    return cfg


def study_dates(start: dt.date, holidays: set[dt.date], count: int = TOTAL_DAYS) -> list[dt.date]:
    """1일차부터 count일차까지의 날짜. holidays 만 건너뛴다 (주말 포함 매일)."""
    dates, d = [], start
    while len(dates) < count:
        if d not in holidays:
            dates.append(d)
        d += dt.timedelta(days=1)
    return dates


def current_day(dates: list[dt.date], today: dt.date) -> int:
    """오늘이 몇 일차인지 (시작 전이면 0, 종료 후면 TOTAL_DAYS)."""
    return sum(1 for d in dates if d <= today)


PATH_RE = re.compile(r"^solutions/([^/]+)/day-(\d{3})/lv([0-3])/Solution\.java$")


def solutions_from_paths(lines) -> dict[str, set[tuple[int, int]]]:
    """`git ls-tree -r --name-only origin/main -- solutions` 출력처럼 경로 목록에서 login → {(day, level)} 를 만든다."""
    members: dict[str, set[tuple[int, int]]] = {}
    for line in lines:
        m = PATH_RE.match(line.strip())
        if m:
            members.setdefault(m.group(1), set()).add((int(m.group(2)), int(m.group(3))))
    return members


def scan_solutions(root: Path) -> dict[str, set[tuple[int, int]]]:
    """login → {(day, level)}"""
    members: dict[str, set[tuple[int, int]]] = {}
    base = root / "solutions"
    if not base.is_dir():
        return members
    for user_dir in sorted(base.iterdir()):
        if not user_dir.is_dir() or user_dir.name.startswith("."):
            continue
        solved = set()
        for f in user_dir.rglob("Solution.java"):
            m = SOLUTION_RE.match(f.relative_to(user_dir).as_posix())
            if m:
                solved.add((int(m.group(1)), int(m.group(2))))
        if solved:
            members[user_dir.name] = solved
    return members


def member_stats(login: str, solved: set[tuple[int, int]], today_day: int) -> dict:
    per_day: dict[int, list[int]] = {}
    for day, lv in sorted(solved):
        per_day.setdefault(day, []).append(lv)
    join_day = min(per_day)
    attended_days = {d for d in per_day if d <= today_day}
    # 현재 스트릭: 오늘 아직 안 풀었으면 어제까지로 센다
    streak, d = 0, today_day
    if today_day and today_day not in attended_days:
        d = today_day - 1
    while d >= join_day and d in attended_days:
        streak += 1
        d -= 1
    longest = run = 0
    for day in range(join_day, today_day + 1):
        run = run + 1 if day in attended_days else 0
        longest = max(longest, run)
    unlocked_since_join = max(0, today_day - join_day + 1)
    return {
        "login": login,
        "join_day": join_day,
        "total": len(solved),
        "attended": len(attended_days),
        "unlocked_since_join": unlocked_since_join,
        "streak": streak,
        "longest": longest,
        "solved": {str(day): lvls for day, lvls in sorted(per_day.items())},
    }


def build(root: Path, today: dt.date | None = None, start_override: str | None = None,
          paths: list[str] | None = None) -> dict:
    cfg = load_config(root)
    tz = ZoneInfo(cfg["timezone"])
    now = dt.datetime.now(tz)
    today = today or now.date()
    start_str = start_override or cfg.get("start_date")
    start = dt.date.fromisoformat(start_str) if start_str else None
    holidays = {dt.date.fromisoformat(h) for h in cfg["holidays"]}
    dates = study_dates(start, holidays) if start else []
    today_day = current_day(dates, today) if start else 0

    found = solutions_from_paths(paths) if paths is not None else scan_solutions(root)
    members = [member_stats(login, solved, today_day) for login, solved in found.items()]
    members.sort(key=lambda m: (-m["total"], m["login"]))
    return {
        "generated_at": now.strftime("%Y-%m-%d %H:%M %Z"),
        "today": today.isoformat(),
        "start_date": start.isoformat() if start else None,
        "today_day": today_day,
        "total_days": TOTAL_DAYS,
        "dates": {str(i + 1): d.isoformat() for i, d in enumerate(dates)},
        "members": members,
    }


def render_markdown(data: dict, recent: int) -> str:
    out = []
    today_day = data["today_day"]
    if not data["start_date"]:
        out.append(f"**스터디 시작 전** — `config.json` 의 `start_date` 가 아직 비어 있습니다. (갱신: {data['generated_at']})")
    elif today_day == 0:
        out.append(f"**{data['start_date']} 시작 예정** (갱신: {data['generated_at']})")
    else:
        out.append(f"**{today_day}일차 / {TOTAL_DAYS}일** · {data['dates'][str(today_day)]} · 갱신: {data['generated_at']}")
    out.append("")
    if not data["members"]:
        out.append("아직 제출한 사람이 없습니다. 첫 PR 이 머지되면 자동으로 등록됩니다.")
        return "\n".join(out)

    first = max(1, today_day - recent + 1)
    days = list(range(first, today_day + 1))
    header = ["멤버", "합류", "출석", "총 풀이", "스트릭", "최장"]
    header += [f"{d}<br>{data['dates'][str(d)][5:]}" for d in days]
    out.append("| " + " | ".join(header) + " |")
    out.append("|" + "---|" * len(header))
    for m in data["members"]:
        if today_day:
            attend = f"{m['attended']}/{m['unlocked_since_join']}"
            streak = f"🔥 {m['streak']}" if m["streak"] else "0"
            longest = str(m["longest"])
        else:
            attend = streak = longest = "-"
        row = [f"@{m['login']}", f"{m['join_day']}일차", attend, str(m["total"]), streak, longest]
        for d in days:
            if d < m["join_day"]:
                row.append("-")
            else:
                n = len(m["solved"].get(str(d), []))
                row.append("·" if n == 0 else ("**4**" if n == LEVELS else str(n)))
        out.append("| " + " | ".join(row) + " |")
    out.append("")
    out.append("출석 = 합류 이후 해금된 일차 중 1문제 이상 푼 날 · 칸의 숫자 = 그날 푼 문제 수(최대 4) · `-` = 합류 전 · 스트릭은 연속 출석일(휴일 제외)")
    return "\n".join(out)


def update_readme(path: Path, body: str) -> bool:
    text = path.read_text(encoding="utf-8") if path.exists() else "# study\n"
    block = f"{START_MARK}\n{body}\n{END_MARK}"
    if START_MARK in text and END_MARK in text:
        pre, rest = text.split(START_MARK, 1)
        _, post = rest.split(END_MARK, 1)
        new = pre + block + post
    else:
        new = text.rstrip("\n") + "\n\n## 출석표\n\n" + block + "\n"
    if new == text:
        return False
    path.write_text(new, encoding="utf-8")
    return True


def main() -> int:
    ap = argparse.ArgumentParser(description="출석표 생성")
    ap.add_argument("--root", type=Path, default=Path(__file__).resolve().parent.parent, help="레포 루트")
    ap.add_argument("--readme", type=Path, help="갱신할 README 경로")
    ap.add_argument("--json", help="JSON 저장 경로 ('-' 면 표준 출력)")
    ap.add_argument("--today", help="오늘 날짜 덮어쓰기 (YYYY-MM-DD, 테스트용)")
    ap.add_argument("--start-date", help="start_date 덮어쓰기 (테스트용)")
    ap.add_argument("--recent", type=int, default=10, help="README 에 보여줄 최근 일차 수")
    ap.add_argument("--paths", help="solutions/ 를 훑는 대신 경로 목록 파일('-' 는 표준 입력)을 쓴다 (예: git ls-tree 출력)")
    args = ap.parse_args()

    today = dt.date.fromisoformat(args.today) if args.today else None
    paths = None
    if args.paths:
        paths = (sys.stdin if args.paths == "-" else open(args.paths, encoding="utf-8")).read().splitlines()
    data = build(args.root, today, args.start_date, paths)
    if args.json == "-":
        json.dump(data, sys.stdout, ensure_ascii=False, indent=2)
        print()
    elif args.json:
        Path(args.json).parent.mkdir(parents=True, exist_ok=True)
        Path(args.json).write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    if args.readme:
        changed = update_readme(args.readme, render_markdown(data, args.recent))
        print("README 갱신됨" if changed else "README 변경 없음")
    if not args.json and not args.readme:
        print(render_markdown(data, args.recent))
    return 0


if __name__ == "__main__":
    sys.exit(main())
