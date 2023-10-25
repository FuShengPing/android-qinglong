package auto.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import java.net.MalformedURLException;
import java.net.URL;

public class NetUnit {
    private static final String TAG = "NetUnit";

    public static String getHost(String str) {
        try {
            URL url = new URL(str);
            return url.getHost() + ":" + url.getPort();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
