package auto.base.util;

import java.util.List;
import java.util.Map;

public class TextUnit {
    public static final String TAG = "TextUnit";

    public static String join(List<String> list, String split) {
        String result = null;
        for (String str : list) {
            if (result != null) {
                result += split + str;
            } else {
                result = str;
            }
        }
        return result;
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
