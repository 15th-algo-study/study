# 해설 — 모음 세기

## 접근
문자를 하나씩 보며 모음 목록 문자열 `"aeiouAEIOU"`에 들어 있는지 `indexOf`로 확인합니다. `Character.toLowerCase(ch)`로 바꾼 뒤 `"aeiou"`에서 찾아도 됩니다.

## 주의할 점
- `s.split("")`이나 `s.substring(i, i + 1)`로 문자마다 새 문자열을 만들면 길이 100만에서 불필요하게 느려집니다. `charAt(i)`나 `toCharArray()`를 쓰세요.
- `switch`로 10가지 문자를 나열해도 좋습니다.

## 복잡도
- 시간 O(n), 공간 O(n) (`toCharArray` 사용 시. `charAt`이면 O(1))
