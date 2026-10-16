class Solution {
    public long solution(String route) {
        String[] points = route.split(" ");
        long total = 0;
        long prevX = 0, prevY = 0;
        for (int i = 0; i < points.length; i++) {
            String p = points[i];
            int comma = p.indexOf(',');
            long x = Long.parseLong(p.substring(1, comma));               // '(' 다음부터 ',' 앞까지
            long y = Long.parseLong(p.substring(comma + 1, p.length() - 1)); // ',' 다음부터 ')' 앞까지
            if (i > 0) total += Math.abs(x - prevX) + Math.abs(y - prevY);
            prevX = x;
            prevY = y;
        }
        return total;
    }
}
