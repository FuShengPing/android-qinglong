package auto.qinglong.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUnit {
    private static final String TAG = "NetUnit";

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
}
