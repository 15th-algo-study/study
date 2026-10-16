# 해설 — 단체 사진에서 빠지는 사람

## 접근
"가운데에서 하나를 빼면 뒤가 당겨진다"는 동작은 `ArrayList`의 `remove(int index)`가 그대로 해 줍니다.

1. `ArrayList<Integer>`에 1부터 `n`까지 넣습니다.
2. `leave`의 각 위치 `p`에 대해 `list.remove(p - 1)`을 호출합니다 (1부터 세는 위치 → 0부터 세는 인덱스).
3. 남은 리스트를 `int[]`로 바꿔 return 합니다.

## 주의할 점
- `ArrayList<Integer>`에는 `remove(int index)`와 `remove(Object o)`가 둘 다 있습니다. `list.remove(p - 1)`처럼 `int`를 넘기면 **인덱스**로 지우고, `Integer` 값을 넘기면 **값**을 찾아 지웁니다. 이 문제는 인덱스 삭제라 `int`를 넘겨야 합니다.
- 위치는 1부터 세므로 `-1`을 잊지 마세요.
- `n`이 작아서 `remove`가 뒤 원소를 모두 당기는 O(n) 비용이어도 전체 O(n²) = 10^8 이내로 충분합니다.

## 복잡도
- 시간 O(n²) (삭제마다 최대 n개 이동), 공간 O(n)
