# 해설 — 접시 쌓기

## 접근
"맨 위에 올리고 맨 위에서 꺼낸다"는 **스택**입니다. 자바에서는 `ArrayDeque`를 스택으로 씁니다.

- 올리기: `stack.push(v)` (= `addFirst`)
- 꺼내기: 비어 있지 않을 때만 `stack.pop()` (= `removeFirst`)

마지막에 남은 접시를 **아래부터** 나열해야 하는데, `ArrayDeque`를 `push`/`pop`으로 쓰면 앞쪽이 맨 위라 그냥 순회하면 위→아래 순서가 나옵니다. `descendingIterator()`로 거꾸로 순회하거나, 처음부터 `addLast`/`pollLast`/`peekLast`로 뒤쪽을 맨 위로 삼아 자연 순서대로 순회하면 됩니다.

## 주의할 점
- 빈 스택에서 `pop()`을 호출하면 `NoSuchElementException`이 납니다. 반드시 `isEmpty()`를 먼저 확인하세요.
- `java.util.Stack` 클래스도 있지만 오래된 동기화 클래스라 `ArrayDeque`를 쓰는 것이 관례입니다.
- 결과 배열 크기는 `stack.size()`로 미리 알 수 있습니다.

## 복잡도
- 시간 O(n), 공간 O(n)
