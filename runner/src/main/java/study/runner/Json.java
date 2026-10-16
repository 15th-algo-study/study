package study.runner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** 공용 Gson 인스턴스와 표시용 헬퍼. */
public final class Json {
    public static final Gson GSON = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    private Json() {}

    public static String compact(JsonElement e) {
        return GSON.toJson(e);
    }

    /** 긴 값은 앞부분만 남기고 전체 길이를 덧붙인다. */
    public static String truncate(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, max) + "… (총 " + s.length() + "자)";
    }

    public static boolean isGenerator(JsonElement e) {
        return e != null && e.isJsonObject() && e.getAsJsonObject().has("$gen");
    }

    /** cases/*.json 저장 포맷: 키마다 한 줄, 값은 한 줄 JSON. */
    public static String formatCase(JsonObject o) {
        StringBuilder sb = new StringBuilder("{\n");
        int i = 0;
        for (var e : o.entrySet()) {
            sb.append("  ").append(GSON.toJson(e.getKey())).append(": ").append(GSON.toJson(e.getValue()));
            if (++i < o.size()) sb.append(',');
            sb.append('\n');
        }
        return sb.append("}\n").toString();
    }
}
