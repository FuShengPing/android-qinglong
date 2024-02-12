package auto.panel.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import java.net.MalformedURLException;
import java.net.URL;

public class NetUnit {
    public static final String TAG = "NetUnit";

    public static boolean checkUrlValid(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            if (!str.startsWith("http://") && !str.startsWith("https://")) {
                str = "http://" + str;
            }
            URL url = new URL(str);
            return url.getPort() >= -1 && url.getPort() <= 65535;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getHost(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        } else if (!str.startsWith("http://") && !str.startsWith("https://")) {
            str = "http://" + str;
        }

        try {
            URL url = new URL(str);
            if (url.getPort() > -1) {
                return url.getHost() + ":" + url.getPort();
            } else {
                return url.getHost();
            }
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static String getRetrofitBaseUrl(String str) {
        StringBuilder sb = new StringBuilder();
        if (!str.startsWith("http")) {
            sb.append("http://");
        }
        sb.append(str);
        if (!str.endsWith("/")) {
            sb.append("/");
        }
        return sb.toString();
    }

    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
