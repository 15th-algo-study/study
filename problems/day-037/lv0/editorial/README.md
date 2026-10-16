# 해설 — 단체 사진 줄 뒤집기

## 접근
1. 배열을 **복사**합니다. `heights.clone()` 또는 `Arrays.copyOf(heights, n)`.
2. 복사본에서 `l`과 `r`에서 출발하는 두 인덱스를 가운데로 좁혀 가며 두 값을 맞바꿉니다.

```java
int[] result = heights.clone();
for (int i = l, j = r; i < j; i++, j--) {
    int t = result[i]; result[i] = result[j]; result[j] = t;
}
```

또는 `result[l + t] = heights[r - t]` (0 ≤ t ≤ r − l) 처럼 원본에서 거꾸로 읽어 복사본에 쓸 수도 있습니다.

## 주의할 점
- `int[] result = heights;`는 복사가 아니라 **같은 배열을 가리키는 참조**입니다. 이렇게 하고 바꾸면 원본도 바뀝니다. 복사는 `clone()`, `Arrays.copyOf`, `System.arraycopy`로 합니다.
- 맞바꾸기 반복은 `i < j`에서 멈춥니다. `i <= j`까지 돌리면 가운데를 자기 자신과 바꿀 뿐이지만, `i`가 `j`를 지나쳐도 계속 바꾸면 원래대로 돌아가 버립니다.
- 구간 바깥 원소는 복사본에 이미 들어 있으므로 따로 처리할 필요가 없습니다.

## 복잡도
- 시간 O(n), 공간 O(n) (복사본)
