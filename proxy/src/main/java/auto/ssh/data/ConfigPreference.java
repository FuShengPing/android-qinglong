package auto.ssh.data;

import android.content.Context;
import android.content.SharedPreferences;

import auto.ssh.MyApplication;
import auto.ssh.bean.ConfigParams;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public class ConfigPreference {
    private static final String NAME = "config";

    public static final String KEY_LOCAL_ADDRESS = "localAddress";
    public static final String KEY_LOCAL_PORT = "localPort";

    public static final String KEY_REMOTE_ADDRESS = "remoteAddress";
    public static final String KEY_REMOTE_PORT = "remotePort";
    public static final String KEY_REMOTE_USERNAME = "remoteUsername";
    public static final String KEY_REMOTE_PASSWORD = "remotePassword";
    public static final String KEY_REMOTE_FORWARD_ADDRESS = "remoteForwardAddress";
    public static final String KEY_REMOTE_FORWARD_PORT = "remoteForwardPort";

    private static final String DEFAULT_LOCAL_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_LOCAL_PORT = 9100;
    private static final String DEFAULT_REMOTE_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_REMOTE_PORT = 22;
    private static final String DEFAULT_REMOTE_USERNAME = "root";
    private static final String DEFAULT_REMOTE_FORWARD_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_REMOTE_FORWARD_PORT = 9100;

    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static ConfigParams getConfig() {
        ConfigParams config = new ConfigParams();
        config.setLocalAddress(getLocalAddress());
        config.setLocalPort(getLocalPort());
        config.setRemoteAddress(getRemoteAddress());
        config.setRemotePort(getRemotePort());
        config.setRemoteUsername(getRemoteUsername());
        config.setRemotePassword(getRemotePassword());
        config.setRemoteForwardAddress(getRemoteForwardAddress());
        config.setRemoteForwardPort(getRemoteForwardPort());

        return config;
    }

    public static String getLocalAddress() {
        return sp.getString(KEY_LOCAL_ADDRESS, DEFAULT_LOCAL_ADDRESS);
    }

    public static void setLocalAddress(String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_LOCAL_ADDRESS, value);
        editor.apply();
    }

    public static int getLocalPort() {
        return sp.getInt(KEY_LOCAL_PORT, DEFAULT_LOCAL_PORT);
    }

    public static void setLocalPort(int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_LOCAL_PORT, value);
        editor.apply();
    }

    public static String getRemoteAddress() {
        return sp.getString(KEY_REMOTE_ADDRESS, DEFAULT_REMOTE_ADDRESS);
    }

    public static void setRemoteAddress(String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_REMOTE_ADDRESS, value);
        editor.apply();
    }

    public static int getRemotePort() {
        return sp.getInt(KEY_REMOTE_PORT, DEFAULT_REMOTE_PORT);
    }

    public static void setRemotePort(int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_REMOTE_PORT, value);
        editor.apply();
    }

    public static String getRemoteUsername() {
        return sp.getString(KEY_REMOTE_USERNAME, DEFAULT_REMOTE_USERNAME);
    }

    public static void setRemoteUsername(String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_REMOTE_USERNAME, value);
        editor.apply();
    }

    public static String getRemotePassword() {
        return sp.getString(KEY_REMOTE_PASSWORD, "");
    }

    public static void setRemotePassword(String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_REMOTE_PASSWORD, value);
        editor.apply();
    }

    public static String getRemoteForwardAddress() {
        return sp.getString(KEY_REMOTE_FORWARD_ADDRESS, DEFAULT_REMOTE_FORWARD_ADDRESS);
    }

    public static void setRemoteForwardAddress(String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_REMOTE_FORWARD_ADDRESS, value);
        editor.apply();
    }

    public static int getRemoteForwardPort() {
        return sp.getInt(KEY_REMOTE_FORWARD_PORT, DEFAULT_REMOTE_FORWARD_PORT);
    }

    public static void setRemoteForwardPort(int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_REMOTE_FORWARD_PORT, value);
        editor.apply();
    }
}
