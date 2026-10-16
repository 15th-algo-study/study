# 해설 — 번갈아 대소문자

## 접근
"지금까지 본 영문자 수"를 세는 변수 `count`를 하나 두고 문자열을 한 번 훑습니다. 영문자를 만나면 `count`가 짝수일 때 대문자, 홀수일 때 소문자로 바꿔 `StringBuilder`에 붙이고 `count`를 1 늘립니다. 영문자가 아니면 그대로 붙입니다.

## 주의할 점
- 문자열의 인덱스 `i`가 아니라 **영문자의 순번**으로 홀짝을 정해야 합니다. 숫자나 공백이 끼어 있으면 `i % 2`로는 틀립니다.
- `Character.isLetter(ch)`, `Character.toUpperCase(ch)`, `Character.toLowerCase(ch)`를 쓰면 대소문자 판별과 변환을 직접 계산하지 않아도 됩니다.
- 결과는 `StringBuilder`로 모읍니다. `+=`는 O(n²)입니다.

## 복잡도
- 시간 O(n), 공간 O(n)
