# 해설 — 거꾸로 쓴 이름표

## 접근
뒤에서부터 한 글자씩 보며 `StringBuilder`에 붙입니다. 글자가 대문자면 `Character.toLowerCase`, 소문자면 `Character.toUpperCase`를 적용하고, 숫자는 그대로 붙입니다. `Character.isUpperCase`/`isLowerCase`로 판별하면 됩니다.

다른 방법으로, 대소문자만 먼저 바꾼 뒤 `StringBuilder.reverse()`를 한 번 호출해도 됩니다.

## 주의할 점
- `answer = ch + answer`처럼 문자열 앞에 이어 붙이면 글자마다 전체를 복사해 O(n²)이 됩니다. 길이 100만이면 시간 초과입니다. `StringBuilder`를 쓰세요.
- 대소문자 변환을 `ch - 32`, `ch + 32`로 직접 하는 것도 가능하지만, 숫자에 적용하면 엉뚱한 글자가 됩니다. 반드시 영문자인지 먼저 확인하세요.

## 복잡도
- 시간 O(n), 공간 O(n)
