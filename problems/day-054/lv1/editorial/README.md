# 해설 — 구호품 창고 배분

## 접근
"세 가지 기준으로 가장 앞서는 창고를 꺼내 값을 바꾸고 다시 넣는다"를 `m`번 하므로 **다중 기준 비교자를 가진 힙**입니다.

힙에는 창고 번호 `i`만 넣고, 비교자에서 `stock[i]`, `names[i]`, `i`를 차례로 봅니다.

```java
Comparator<Integer> byRule = (a, b) -> {
    if (stock[a] != stock[b]) return Integer.compare(stock[a], stock[b]);
    int byName = names[a].compareTo(names[b]);
    return byName != 0 ? byName : Integer.compare(a, b);
};
```

상자마다 `poll` → 이름 기록 → `stock[i]++` → 같은 번호를 다시 `offer`합니다. 힙 밖에서 값을 바꾼 뒤 다시 넣으므로 힙의 순서가 깨지지 않습니다.

## 주의할 점
- 상자마다 창고 `n`곳을 훑으면 O(n·m) = 4×10^10이라 시간 초과입니다.
- 힙에 **들어 있는** 원소의 키(`stock[i]`)를 바꾸면 힙이 망가집니다. 반드시 꺼낸 뒤 바꾸고 다시 넣으세요.
- `Comparator.comparingInt((Integer i) -> stock[i]).thenComparing(i -> names[i]).thenComparingInt(i -> i)`처럼 체인으로 써도 됩니다. 람다의 타입 추론이 안 되면 첫 람다에 타입을 써 줍니다.
- 이름이 같은 창고가 있으므로 이름만으로는 순서가 정해지지 않습니다. 세 번째 기준(번호)을 빠뜨리면 `PriorityQueue`가 같은 원소를 임의 순서로 꺼내 결과가 달라질 수 있습니다.

## 복잡도
- 시간 O((n + m) log n), 공간 O(n + m)
