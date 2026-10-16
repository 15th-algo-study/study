# 해설 — 낚시 대회 순위

## 접근
결과로 돌려줄 것은 참가자 **번호**이므로, 번호 배열 `0, 1, …, n-1`을 만들고 그 번호가 가리키는 기록을 기준으로 정렬합니다. `Arrays.sort(Integer[], Comparator)` 에 다중 기준 비교자를 넘기면 됩니다.

```java
Arrays.sort(order, (a, b) -> {
    if (catches[a][0] != catches[b][0]) return Integer.compare(catches[b][0], catches[a][0]); // 무게 내림차순
    if (catches[a][1] != catches[b][1]) return Integer.compare(catches[b][1], catches[a][1]); // 마릿수 내림차순
    return Integer.compare(a, b);                                                               // 번호 오름차순
});
```

`Comparator.comparingInt(...).reversed().thenComparingInt(...)` 체인으로 써도 같습니다.

## 주의할 점
- 비교자를 넘기려면 `int[]`가 아니라 **`Integer[]`** 여야 합니다 (기본형 배열에는 `Comparator`를 쓸 수 없습니다).
- 내림차순은 `b - a` 대신 `Integer.compare(b, a)`로 쓰는 습관을 들이세요. 값이 커지면 뺄셈이 넘칠 수 있습니다.
- 모든 기준이 같을 때 번호 순이어야 하므로 마지막에 반드시 번호를 비교합니다. (`Arrays.sort(Object[])`는 안정 정렬이라 생략해도 결과는 같지만, 규칙을 코드에 드러내는 편이 안전합니다.)
- 직접 선택 정렬·버블 정렬을 짜면 O(n²)이라 20만 명에서 시간 초과입니다.

## 복잡도
- 시간 O(n log n), 공간 O(n)
