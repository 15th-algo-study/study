# 해설 — 묘목의 키 예측

## 접근
배율 `factor = Math.pow(1 + rate / 100.0, years)`를 **한 번만** 계산해 두고, 묘목마다 `Math.min(limit, heights[i] * factor)`를 넣습니다. `Math.pow(밑, 지수)`는 실수 거듭제곱을 돌려주고, `Math.min`은 `double`끼리도 됩니다.

## 주의할 점
- `rate / 100`은 `int / int`라 `rate`가 100 미만이면 항상 0입니다. `rate / 100.0`처럼 한쪽을 실수로 만들어야 `0.1` 같은 값이 나옵니다. `(double) rate / 100`도 같습니다.
- `Math.pow`의 결과는 `double`입니다. `heights[i]`는 `int`지만 `double`과 곱하면 자동으로 `double`이 됩니다.
- `Math.min(limit, ...)`에서 `limit`은 `int`, 다른 쪽은 `double`이므로 `Math.min(double, double)`이 선택되고 결과도 `double`입니다. 결과를 `int`로 캐스팅하면 소수점이 잘려 틀립니다.
- 묘목마다 `Math.pow`를 다시 부르면 100만 번 계산하지만 그래도 충분히 빠릅니다. 다만 같은 값을 반복 계산하지 않는 습관이 좋습니다.

## 복잡도
- 시간 O(n), 공간 O(n)
