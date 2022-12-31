package auto.qinglong.utils;

import androidx.annotation.NonNull;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.http.Url;


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

    public static boolean isValidUrl(String url) {
        return url != null && url.matches("^(https|http)://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$");
    }

    public static String getHost(@NonNull String url) {
        Pattern pattern = Pattern.compile("^(https|http)://[^/]+");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    public static String getPath(@NonNull String url, String def) {
        String mUrl = url.replaceFirst("//", "");
        Pattern pattern = Pattern.compile("/.+");
        Matcher matcher = pattern.matcher(mUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return def;
        }
    }
}
