class Solution {
    public long[] solution(String[] ranges) {
        long[] result = new long[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            int colon = ranges[i].indexOf(':');
            long[] a = parse(ranges[i].substring(0, colon));
            long[] b = parse(ranges[i].substring(colon + 1));
            long cols = Math.abs(a[0] - b[0]) + 1;
            long rows = Math.abs(a[1] - b[1]) + 1;
            result[i] = cols * rows; // long × long
        }
        return result;
    }

    /** "AB12" → {열 번호, 행 번호} */
    private long[] parse(String cell) {
        int pos = 0;
        long col = 0;
        while (pos < cell.length() && Character.isLetter(cell.charAt(pos))) {
            col = col * 26 + (cell.charAt(pos) - 'A' + 1); // 0이 없는 26진법: A=1 … Z=26
            pos++;
        }
        long row = Long.parseLong(cell.substring(pos));
        return new long[]{col, row};
    }
}
