package auto.ssh.data;

import android.content.Context;
import android.content.SharedPreferences;

import auto.ssh.MyApplication;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public class ConfigPreference {
    private static final String NAME = "config";
    public static final String KEY_REMOTE_USERNAME = "remoteUsername";
    public static final String KEY_REMOTE_PASSWORD = "remotePassword";
    public static final String KEY_REMOTE_ADDRESS = "remoteAddress";
    public static final String KEY_REMOTE_PORT = "remotePort";
    public static final String KEY_LOCAL_ADDRESS = "localAddress";
    public static final String KEY_LOCAL_PORT = "localPort";

    private static final String DEFAULT_REMOTE_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_REMOTE_PORT = 9100;
    private static final String DEFAULT_LOCAL_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_LOCAL_PORT = 9100;
    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static String getRemoteUsername() {
        return sp.getString(KEY_REMOTE_USERNAME, "");
    }

    public static String getRemotePassword() {
        return sp.getString(KEY_REMOTE_PASSWORD, "");
    }

    public static String getRemoteAddress() {
        return sp.getString(KEY_REMOTE_ADDRESS, DEFAULT_REMOTE_ADDRESS);
    }

    public static int getRemotePort() {
        return sp.getInt(KEY_REMOTE_PORT, DEFAULT_REMOTE_PORT);
    }

    public static String getLocalAddress() {
        return sp.getString(KEY_LOCAL_ADDRESS, DEFAULT_LOCAL_ADDRESS);
    }

    public static int getLocalPort() {
        return sp.getInt(KEY_LOCAL_PORT, DEFAULT_LOCAL_PORT);
    }
}
