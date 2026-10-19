# 해설 — 다랑논에 물이 고이는 칸

## 접근
모든 칸을 이중 반복문으로 순회하면서, 방향 배열로 네 이웃을 확인합니다. 칸마다 `boolean low = true`로 시작해, 격자 안에 있는 이웃 중 하나라도 **자기 높이 이하**이면 `low = false`로 바꿉니다. 네 방향을 모두 본 뒤에도 `low`가 참이면 셉니다.

```java
boolean low = true;
for (int d = 0; d < 4; d++) {
    int nr = r + dr[d], nc = c + dc[d];
    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue; // 밖은 비교하지 않음
    if (paddy[nr][nc] <= paddy[r][c]) { low = false; break; }
}
```

## 주의할 점
- 조건은 "이웃이 모두 **더 높다**"입니다. 높이가 같은 이웃이 있으면 고이지 않으므로 `<=`로 비교해 탈락시킵니다.
- 격자 밖은 "비교 대상에서 제외"이지 "높은 벽"도 "낮은 땅"도 아닙니다. 1×1 격자의 유일한 칸이 답에 포함되는 이유입니다.
- 이웃을 확인하기 전에 범위를 먼저 검사해야 `ArrayIndexOutOfBoundsException`이 나지 않습니다.

## 복잡도
- 시간 O(rows × cols), 공간 O(1)
