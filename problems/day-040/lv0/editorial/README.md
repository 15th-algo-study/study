# 해설 — 전광판 패널 교체

## 접근
1. `board`를 복사해 새 배열을 만듭니다: `Arrays.copyOf(board, board.length)` 또는 `board.clone()`.
2. 복사본의 `pos`번부터 `patch`를 덮어씁니다. 반복문으로 `result[pos + i] = patch[i]`를 해도 되고, `System.arraycopy(patch, 0, result, pos, patch.length)` 한 줄로도 됩니다.

`System.arraycopy(원본, 원본 시작, 대상, 대상 시작, 길이)` 순서를 익혀 두면 배열 일부를 옮기는 작업이 모두 한 줄이 됩니다.

## 주의할 점
- `int[] result = board;`는 복사가 아닙니다. 그대로 덮어쓰면 원본 `board`가 바뀝니다.
- 덮어쓰는 범위는 `pos`부터 `pos + patch.length − 1`까지입니다. 제한에서 밖으로 나가지 않음을 보장하므로 범위 검사는 필요 없지만, `pos + patch.length ≤ n`인지 한 번 생각해 보는 습관이 좋습니다.
- 100만 개짜리 배열을 매번 새로 만들어도 O(n)이라 충분히 빠릅니다.

## 복잡도
- 시간 O(n), 공간 O(n)
