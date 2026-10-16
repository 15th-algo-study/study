# [lv1] 오늘의 인기 메뉴

카페의 하루 주문 기록이 들어온 순서대로 문자열 배열 `orders`에 담겨 있습니다. 각 원소는 주문된 메뉴 이름입니다.

가장 많이 주문된 메뉴 이름을 return 하도록 `solution` 함수를 완성해 주세요. 가장 많이 주문된 메뉴가 여러 개라면 그중 **가장 먼저 주문된** 메뉴를 return 합니다. 메뉴 이름은 대소문자를 구분합니다.

## 제한사항
- 1 ≤ `orders`의 길이 ≤ 200,000
- 메뉴 이름은 길이 1 이상 20 이하의 영문 대소문자·숫자로만 이루어져 있습니다.

## 입출력 예

| orders | result |
|---|---|
| ["latte", "americano", "latte", "mocha", "americano", "latte"] | "latte" |
| ["tea", "cocoa", "cocoa", "tea"] | "tea" |
| ["juice"] | "juice" |

## 입출력 예 설명
- 예 #1: latte 3번, americano 2번, mocha 1번입니다.
- 예 #2: tea와 cocoa가 2번씩이지만 tea가 먼저 주문되었습니다.
- 예 #3: 메뉴가 하나뿐입니다.
