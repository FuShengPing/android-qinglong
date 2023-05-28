package auto.qinglong.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUnit {
    public static final String TAG = "NetUnit";

    /**
     * 获取本机局域网IP
     *
     * @return IP/null
     */
    public static String getIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface inf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAdder = inf.getInetAddresses(); enumIpAdder.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAdder.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            LogUnit.log(TAG, ex.getMessage());
            return null;
        }
        return null;
    }

    public static boolean isConnected(@NonNull Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
