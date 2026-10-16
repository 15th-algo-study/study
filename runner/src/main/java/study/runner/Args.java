package study.runner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 케이스 JSON 값 → 메서드 파라미터 타입 변환.
 * 일반 값은 Gson 이 시그니처 타입(int, long, String, int[], int[][], String[], List 등)으로 변환하고,
 * {"$gen": ...} 객체는 결정적 생성기로 큰 입력을 만든다.
 */
public final class Args {
    private Args() {}

    public static Object convert(JsonElement value, Type type) {
        if (Json.isGenerator(value)) return generate(value.getAsJsonObject(), type);
        Class<?> raw = rawClass(type);
        // 배열 안에 생성기가 섞여 있으면 원소별로 변환한다 (예: ["...", {"$gen":...}])
        if (value.isJsonArray() && raw.isArray() && containsGenerator(value)) {
            var arr = value.getAsJsonArray();
            Object out = Array.newInstance(raw.getComponentType(), arr.size());
            for (int i = 0; i < arr.size(); i++) Array.set(out, i, convert(arr.get(i), componentType(type)));
            return out;
        }
        return Json.GSON.fromJson(value, type);
    }

    private static boolean containsGenerator(JsonElement e) {
        if (Json.isGenerator(e)) return true;
        if (e.isJsonArray()) for (JsonElement c : e.getAsJsonArray()) if (containsGenerator(c)) return true;
        return false;
    }

    // ───────────────────────── 생성기 ─────────────────────────

    static Object generate(JsonObject spec, Type type) {
        String kind = spec.get("$gen").getAsString();
        Object raw = switch (kind) {
            case "ints" -> ints(spec);
            case "matrix" -> matrix(spec);
            case "string" -> string(spec);
            case "strings" -> strings(spec);
            case "grid" -> grid(spec);
            case "repeat" -> repeat(spec, type);
            case "concat" -> concat(spec, type);
            default -> throw new IllegalArgumentException("알 수 없는 생성기: " + kind);
        };
        return adapt(raw, type);
    }

    /** {"$gen":"ints","n":N,"min":a,"max":b,"seed":s,"distinct":false,"sorted":"asc|desc"} */
    private static long[] ints(JsonObject s) {
        int n = s.get("n").getAsInt();
        long lo = s.get("min").getAsLong(), hi = s.get("max").getAsLong();
        Rng rng = new Rng(seed(s));
        long[] a = new long[n];
        if (s.has("distinct") && s.get("distinct").getAsBoolean()) {
            long span = hi - lo + 1;
            if (span > 0 && span < n) throw new IllegalArgumentException("distinct: 범위가 n 보다 작습니다");
            if (span > 0 && span <= 2L * n) {
                long[] all = new long[(int) span];
                for (int i = 0; i < span; i++) all[i] = lo + i;
                for (int i = 0; i < n; i++) {
                    int j = i + rng.index(all.length - i);
                    long t = all[i]; all[i] = all[j]; all[j] = t;
                    a[i] = all[i];
                }
            } else {
                Set<Long> seen = new HashSet<>(n * 2);
                for (int i = 0; i < n; ) {
                    long v = rng.range(lo, hi);
                    if (seen.add(v)) a[i++] = v;
                }
            }
        } else {
            for (int i = 0; i < n; i++) a[i] = rng.range(lo, hi);
        }
        if (s.has("sorted")) {
            String order = s.get("sorted").getAsString();
            java.util.Arrays.sort(a);
            if (order.equals("desc")) {
                for (int i = 0, j = n - 1; i < j; i++, j--) { long t = a[i]; a[i] = a[j]; a[j] = t; }
            }
        }
        return a;
    }

    /** {"$gen":"matrix","rows":R,"cols":C,"min":a,"max":b,"seed":s} 또는 "columns":[[lo,hi],...] (열마다 범위) */
    private static long[][] matrix(JsonObject s) {
        int rows = s.get("rows").getAsInt();
        Rng rng = new Rng(seed(s));
        long[][] lohi;
        if (s.has("columns")) {
            var cols = s.getAsJsonArray("columns");
            lohi = new long[cols.size()][];
            for (int c = 0; c < cols.size(); c++) {
                var r = cols.get(c).getAsJsonArray();
                lohi[c] = new long[]{r.get(0).getAsLong(), r.get(1).getAsLong()};
            }
        } else {
            int cols = s.get("cols").getAsInt();
            lohi = new long[cols][];
            for (int c = 0; c < cols; c++) lohi[c] = new long[]{s.get("min").getAsLong(), s.get("max").getAsLong()};
        }
        long[][] m = new long[rows][lohi.length];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < lohi.length; c++) m[r][c] = rng.range(lohi[c][0], lohi[c][1]);
        return m;
    }

    /**
     * {"$gen":"string","n":N,"chars":"ab","seed":s}  → 길이 N, chars 에서 무작위 (중복 문자는 가중치)
     * {"$gen":"string","n":N,"pattern":"{1..9}{+|-}","sep":"","seed":s} → 패턴 N회 이어 붙임
     */
    private static String string(JsonObject s) {
        int n = s.get("n").getAsInt();
        Rng rng = new Rng(seed(s));
        if (s.has("pattern")) {
            Pattern p = Pattern.parse(s.get("pattern").getAsString());
            String sep = s.has("sep") ? s.get("sep").getAsString() : "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (i > 0) sb.append(sep);
                p.append(sb, rng);
            }
            return sb.toString();
        }
        String chars = s.get("chars").getAsString();
        char[] out = new char[n];
        for (int i = 0; i < n; i++) out[i] = chars.charAt(rng.index(chars.length()));
        return new String(out);
    }

    /**
     * {"$gen":"strings","n":N,"pattern":"..."} 또는 {"$gen":"strings","n":N,"chars":"..","len":L|[lo,hi]}
     */
    private static String[] strings(JsonObject s) {
        int n = s.get("n").getAsInt();
        Rng rng = new Rng(seed(s));
        String[] out = new String[n];
        if (s.has("pattern")) {
            Pattern p = Pattern.parse(s.get("pattern").getAsString());
            for (int i = 0; i < n; i++) {
                StringBuilder sb = new StringBuilder();
                p.append(sb, rng);
                out[i] = sb.toString();
            }
            return out;
        }
        String chars = s.get("chars").getAsString();
        long[] len = lenRange(s.get("len"));
        for (int i = 0; i < n; i++) {
            int l = (int) rng.range(len[0], len[1]);
            char[] cs = new char[l];
            for (int j = 0; j < l; j++) cs[j] = chars.charAt(rng.index(chars.length()));
            out[i] = new String(cs);
        }
        return out;
    }

    /** {"$gen":"grid","rows":R,"cols":C,"chars":"..#","seed":s,"set":[[r,c,"S"],...]} → String[] */
    private static String[] grid(JsonObject s) {
        int rows = s.get("rows").getAsInt(), cols = s.get("cols").getAsInt();
        String chars = s.get("chars").getAsString();
        Rng rng = new Rng(seed(s));
        char[][] g = new char[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) g[r][c] = chars.charAt(rng.index(chars.length()));
        if (s.has("set")) {
            for (JsonElement e : s.getAsJsonArray("set")) {
                var t = e.getAsJsonArray();
                g[t.get(0).getAsInt()][t.get(1).getAsInt()] = t.get(2).getAsString().charAt(0);
            }
        }
        String[] out = new String[rows];
        for (int r = 0; r < rows; r++) out[r] = new String(g[r]);
        return out;
    }

    /**
     * {"$gen":"repeat","value":v,"n":N}
     * 문자열이면 v 를 N번 이어 붙이고, 배열이면 원소 v 를 N개 채운다. v 가 JSON 배열이면 그 배열 전체를 N번 이어 붙인다.
     */
    private static Object repeat(JsonObject s, Type type) {
        int n = s.get("n").getAsInt();
        Class<?> raw = rawClass(type);
        if (raw == String.class) {
            return convert(s.get("value"), String.class).toString().repeat(n);
        }
        if (raw.isArray() && s.get("value").isJsonArray()) {
            Object block = convert(s.get("value"), type);
            int len = Array.getLength(block);
            Object arr = Array.newInstance(raw.getComponentType(), len * n);
            for (int i = 0; i < n; i++) System.arraycopy(block, 0, arr, i * len, len);
            return arr;
        }
        if (raw.isArray()) {
            Type component = componentType(type);
            Object v = convert(s.get("value"), component);
            Object arr = Array.newInstance(raw.getComponentType(), n);
            for (int i = 0; i < n; i++) Array.set(arr, i, v);
            return arr;
        }
        throw new IllegalArgumentException("repeat 를 지원하지 않는 타입: " + type);
    }

    /** {"$gen":"concat","parts":[literal | {"$gen":..}, ...]} — 문자열 또는 배열을 이어 붙인다. */
    private static Object concat(JsonObject s, Type type) {
        Class<?> raw = rawClass(type);
        var parts = s.getAsJsonArray("parts");
        if (raw == String.class) {
            StringBuilder sb = new StringBuilder();
            for (JsonElement p : parts) sb.append((String) convert(p, String.class));
            return sb.toString();
        }
        if (raw.isArray()) {
            List<Object> arrays = new ArrayList<>();
            int total = 0;
            for (JsonElement p : parts) {
                Object a = convert(p, type);
                arrays.add(a);
                total += Array.getLength(a);
            }
            Object out = Array.newInstance(raw.getComponentType(), total);
            int pos = 0;
            for (Object a : arrays) {
                int len = Array.getLength(a);
                System.arraycopy(a, 0, out, pos, len);
                pos += len;
            }
            return out;
        }
        throw new IllegalArgumentException("concat 를 지원하지 않는 타입: " + type);
    }

    // ───────────────────────── 타입 맞추기 ─────────────────────────

    /** 생성기 결과(long[], long[][], String, String[])를 파라미터 타입으로 바꾼다. */
    static Object adapt(Object raw, Type type) {
        Class<?> target = rawClass(type);
        if (target.isInstance(raw)) return raw;
        if (raw instanceof long[] a) {
            if (target == int[].class) {
                int[] out = new int[a.length];
                for (int i = 0; i < a.length; i++) out[i] = Math.toIntExact(a[i]);
                return out;
            }
            if (target == double[].class) {
                double[] out = new double[a.length];
                for (int i = 0; i < a.length; i++) out[i] = a[i];
                return out;
            }
        }
        if (raw instanceof long[][] m && target == int[][].class) {
            int[][] out = new int[m.length][];
            for (int r = 0; r < m.length; r++) out[r] = (int[]) adapt(m[r], int[].class);
            return out;
        }
        if (raw instanceof String s && target == char[].class) return s.toCharArray();
        if (raw instanceof String[] g && target == char[][].class) {
            char[][] out = new char[g.length][];
            for (int i = 0; i < g.length; i++) out[i] = g[i].toCharArray();
            return out;
        }
        // 그 밖의 타입(List<Integer> 등)은 JSON 을 거쳐 Gson 에 맡긴다
        return Json.GSON.fromJson(Json.GSON.toJsonTree(raw), type);
    }

    static Class<?> rawClass(Type t) {
        if (t instanceof Class<?> c) return c;
        if (t instanceof ParameterizedType p) return (Class<?>) p.getRawType();
        if (t instanceof GenericArrayType g) return Array.newInstance(rawClass(g.getGenericComponentType()), 0).getClass();
        return Object.class;
    }

    private static Type componentType(Type t) {
        if (t instanceof GenericArrayType g) return g.getGenericComponentType();
        return rawClass(t).getComponentType();
    }

    private static long seed(JsonObject s) {
        return s.has("seed") ? s.get("seed").getAsLong() : 1L;
    }

    private static long[] lenRange(JsonElement e) {
        if (e.isJsonArray()) return new long[]{e.getAsJsonArray().get(0).getAsLong(), e.getAsJsonArray().get(1).getAsLong()};
        return new long[]{e.getAsLong(), e.getAsLong()};
    }

    /** 패턴: 일반 문자 + {a|b|c}(택1) + {lo..hi}(정수, lo 가 0으로 시작하면 그 자릿수로 0 채움). */
    private record Pattern(List<Object> parts) {
        static Pattern parse(String src) {
            List<Object> parts = new ArrayList<>();
            StringBuilder lit = new StringBuilder();
            for (int i = 0; i < src.length(); i++) {
                char ch = src.charAt(i);
                if (ch != '{') { lit.append(ch); continue; }
                int end = src.indexOf('}', i);
                if (end < 0) throw new IllegalArgumentException("패턴의 { 가 닫히지 않았습니다: " + src);
                if (!lit.isEmpty()) { parts.add(lit.toString()); lit.setLength(0); }
                String body = src.substring(i + 1, end);
                if (body.contains("..")) {
                    String[] r = body.split("\\.\\.");
                    int width = r[0].length() > 1 && r[0].startsWith("0") ? r[0].length() : 0;
                    parts.add(new long[]{Long.parseLong(r[0]), Long.parseLong(r[1]), width});
                } else {
                    parts.add(body.split("\\|", -1));
                }
                i = end;
            }
            if (!lit.isEmpty()) parts.add(lit.toString());
            return new Pattern(parts);
        }

        void append(StringBuilder sb, Rng rng) {
            for (Object p : parts) {
                if (p instanceof String s) sb.append(s);
                else if (p instanceof String[] choices) sb.append(choices[rng.index(choices.length)]);
                else {
                    long[] r = (long[]) p;
                    long v = rng.range(r[0], r[1]);
                    String txt = Long.toString(v);
                    for (int k = txt.length(); k < r[2]; k++) sb.append('0');
                    sb.append(txt);
                }
            }
        }
    }
}
