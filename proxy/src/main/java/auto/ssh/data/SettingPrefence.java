package auto.ssh.data;

import android.content.Context;
import android.content.SharedPreferences;

import auto.base.util.Logger;
import auto.ssh.MyApplication;
import auto.ssh.bean.SettingParams;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public class SettingPrefence {
    private static final String NAME = "setting";

    public static final String KEY_SERVICE_WAKEUP = "serviceWakeup";
    public static final String KEY_SERVICE_REFRESH_INTERVAL = "serviceRefreshInterval";
    public static final String KEY_LOG_LEVEL = "logLevel";
    public static final String KEY_LOG_DELETE_FREQUENCY = "logDeleteFrequency";

    public static final boolean DEFAULT_SERVICE_WAKEUP = true;
    public static final int DEFAULT_SERVICE_REFRESH_INTERVAL = 120;
    public static final int DEFAULT_LOG_LEVEL = Logger.LEVEL_INFO;
    public static final int DEFAULT_LOG_DELETE_FREQUENCY = 7;

    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static SettingParams getSettingParams() {
        SettingParams params = new SettingParams();
        params.setServiceWakeup(getServiceWakeup());
        params.setServiceRefreshInterval(getServiceRefreshInterval());
        params.setLogLevel(getLogLevel());
        params.setLogDeleteFrequency(getLogDeleteFrequency());
        return params;
    }

    public static boolean getServiceWakeup() {
        return sp.getBoolean(KEY_SERVICE_WAKEUP, DEFAULT_SERVICE_WAKEUP);
    }

    public static void setServiceWakeup(boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_SERVICE_WAKEUP, value);
        editor.apply();
    }

    public static int getServiceRefreshInterval() {
        return sp.getInt(KEY_SERVICE_REFRESH_INTERVAL, DEFAULT_SERVICE_REFRESH_INTERVAL);
    }

    public static void setServiceRefreshInterval(int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_SERVICE_REFRESH_INTERVAL, value);
        editor.apply();
    }

    public static int getLogLevel() {
        return sp.getInt(KEY_LOG_LEVEL, DEFAULT_LOG_LEVEL);
    }

    public static void setLogLevel(int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_LOG_LEVEL, value);
        editor.apply();
    }

    public static int getLogDeleteFrequency() {
        return sp.getInt(KEY_LOG_DELETE_FREQUENCY, DEFAULT_LOG_DELETE_FREQUENCY);
    }

    public static void setLogDeleteFrequency(int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_LOG_DELETE_FREQUENCY, value);
        editor.apply();
    }
}
