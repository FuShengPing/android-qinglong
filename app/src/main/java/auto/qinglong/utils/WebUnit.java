package auto.qinglong.utils;

import java.util.HashMap;
import java.util.Map;

public class WebUnit {
    public static final String TAG = "WebUnit";

    public static Map<String, String> parseCookies(String cookies) {
        Map<String, String> map = new HashMap<>();
        if (cookies != null && !cookies.isEmpty()) {
            String[] cks = cookies.split(";");
            for (String ck : cks) {
                String[] kv = ck.split("=", 2);
                if (kv.length == 2) {
                    map.put(kv[0].trim(), kv[1].trim());
                } else {
                    map.put(kv[0].trim(), "");
                }
            }
        }
        return map;
    }

    public static String getRoute(String url) {
        if (TextUnit.isFull(url)) {
            return url.split("\\?", 2)[0];
        } else {
            return "";
        }
    }
}
