package auto.qinglong.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import auto.base.util.TextUnit;


public class WebUnit {
    public static final String TAG = "WebUnit";

    public static Map<String, String> parseCookies(String cookies) {
        Map<String, String> map = new HashMap<>();
        if (TextUnit.isFull(cookies)) {
            cookies = cookies.replace(" ", "");
            String[] cks = cookies.split(";");
            for (String ck : cks) {
                String[] kv = ck.split("=", 2);
                if (kv.length == 2) {
                    map.put(kv[0], kv[1]);
                } else {
                    map.put(kv[0], "");
                }
            }
        }
        return map;
    }

    public static String joinMap(Map<String, String> map, String split) {
        StringJoiner result = new StringJoiner(split);
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first) {
                result = new StringJoiner(split, entry.getKey() + "=" + entry.getValue(), "");
                first = false;
            } else {
                result.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        return result.toString();

    }

    public static boolean isInvalid(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static String getHost(@NonNull String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getProtocol() + "://" + url.getHost();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPath(@NonNull String urlString, String def) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            String query = url.getQuery();
            if (path != null && query != null) {
                return path + "?" + query;
            } else if (path != null) {
                return path;
            } else {
                return def;
            }
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 通过手机浏览器打开指定网页.
     *
     * @param url the url
     */
    public static void open(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
