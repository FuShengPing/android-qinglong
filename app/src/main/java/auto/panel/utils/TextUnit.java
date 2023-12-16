package auto.panel.utils;

import java.util.List;
import java.util.Map;

public class TextUnit {
    public static final String TAG = "TextUnit";

    public static String join(List<String> list, String split) {
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            if (sb.length() != 0) {
                sb.append(split);
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static String joinMap(Map<String, String> map, String split) {
        StringBuilder result = new StringBuilder();
        for (String key : map.keySet()) {
            if (result.length() > 0) {
                result.append(split).append(key).append("=").append(map.get(key));
            } else {
                result = new StringBuilder(key + "=" + map.get(key));
            }
        }
        return result.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isFull(String str) {
        return str != null && !str.isEmpty();
    }
}
