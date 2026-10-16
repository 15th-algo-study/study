import java.util.ArrayList;
import java.util.List;

class Solution {
    public long[] solution(long n) {
        List<Long> small = new ArrayList<>(); // √n 이하의 약수 (오름차순)
        List<Long> large = new ArrayList<>(); // 짝이 되는 큰 약수 (내림차순으로 쌓임)
        for (long i = 1; i * i <= n; i++) {
            if (n % i != 0) continue;
            small.add(i);
            if (i != n / i) large.add(n / i); // 완전제곱수의 제곱근은 한 번만
        }
        long[] answer = new long[small.size() + large.size()];
        int idx = 0;
        for (long d : small) answer[idx++] = d;
        for (int i = large.size() - 1; i >= 0; i--) answer[idx++] = large.get(i); // 뒤집어서 이어 붙인다
        return answer;
    }
}
