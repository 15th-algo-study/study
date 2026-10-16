#!/usr/bin/env python3
"""출제 보조: 한 줄에 케이스 하나를 적어 cases/*.json 으로 만든다.

사용법:  python3 scripts/make-cases.py problems/day-006/lv1 <<'END'
basic_01 | 문제 예시 1 | [[1,2,3], 2] | 5
large_01 | 최대 크기 | [{"$gen":"ints","n":200000,"min":1,"max":1000000000,"seed":601}, 7]
END
형식:  이름 | note | args(JSON 배열) | expected(JSON, 생략하면 fillExpected 로 채움)
"""
import json
import os
import sys

if len(sys.argv) != 2:
    sys.exit(__doc__)
d = os.path.join(sys.argv[1], "cases")
os.makedirs(d, exist_ok=True)
n = 0
for line in sys.stdin:
    line = line.strip()
    if not line or line.startswith("#"):
        continue
    parts = [p.strip() for p in line.split(" | ")]
    if len(parts) < 3:
        sys.exit(f"형식 오류: {line}")
    name, note, args = parts[0], parts[1], json.loads(parts[2])
    if not isinstance(args, list):
        sys.exit(f"args 는 JSON 배열이어야 합니다: {name}")
    obj = {"note": note, "args": args}
    if len(parts) > 3 and parts[3]:
        obj["expected"] = json.loads(parts[3])
    with open(os.path.join(d, name + ".json"), "w", encoding="utf-8") as f:
        f.write("{\n" + ",\n".join(
            "  %s: %s" % (json.dumps(k, ensure_ascii=False), json.dumps(v, ensure_ascii=False, separators=(",", ":")))
            for k, v in obj.items()) + "\n}\n")
    n += 1
print(f"{d}: {n}개 작성")
