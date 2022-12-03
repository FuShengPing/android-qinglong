package auto.qinglong.utils;

import java.util.List;

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

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isFull(String str) {
        return str != null && !str.isEmpty();
    }
}
