package study.runner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** 기대값과 실제값 비교. 정수는 정확히, 실수는 1e-6 오차까지 허용. */
public final class Compare {
    private static final double EPS = 1e-6;

    private Compare() {}

    public static boolean equal(JsonElement expected, JsonElement actual) {
        if (expected == null || expected.isJsonNull()) return actual == null || actual.isJsonNull();
        if (actual == null || actual.isJsonNull()) return false;
        if (expected.isJsonPrimitive() && actual.isJsonPrimitive()) {
            return primitive(expected.getAsJsonPrimitive(), actual.getAsJsonPrimitive());
        }
        if (expected.isJsonArray() && actual.isJsonArray()) {
            JsonArray e = expected.getAsJsonArray(), a = actual.getAsJsonArray();
            if (e.size() != a.size()) return false;
            for (int i = 0; i < e.size(); i++) if (!equal(e.get(i), a.get(i))) return false;
            return true;
        }
        if (expected.isJsonObject() && actual.isJsonObject()) {
            JsonObject e = expected.getAsJsonObject(), a = actual.getAsJsonObject();
            if (!e.keySet().equals(a.keySet())) return false;
            for (String k : e.keySet()) if (!equal(e.get(k), a.get(k))) return false;
            return true;
        }
        return false;
    }

    private static boolean primitive(JsonPrimitive e, JsonPrimitive a) {
        if (e.isNumber() && a.isNumber()) {
            String es = e.getAsString(), as = a.getAsString();
            if (isInteger(es) && isInteger(as)) return new BigDecimal(es).compareTo(new BigDecimal(as)) == 0;
            double ed = e.getAsDouble(), ad = a.getAsDouble();
            return Math.abs(ed - ad) <= EPS * Math.max(1.0, Math.abs(ed));
        }
        if (e.isNumber() != a.isNumber()) return false;
        return e.getAsString().equals(a.getAsString());
    }

    private static boolean isInteger(String s) {
        return !(s.contains(".") || s.contains("e") || s.contains("E") || s.contains("N") || s.contains("I"));
    }

    /** 출력이 너무 큰 large 케이스는 expected 대신 sha256 을 저장한다. */
    public static String sha256(JsonElement actual) {
        try {
            byte[] h = MessageDigest.getInstance("SHA-256").digest(Json.compact(actual).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(h);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
