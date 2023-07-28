package auto.ssh.data;

import android.content.Context;
import android.content.SharedPreferences;

import auto.ssh.MyApplication;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public class SettingPrefence {
    private static final String NAME = "setting";

    public static final String KEY_SERVICE_KEEP_ALIVE = "serviceKeepAlive";
    public static final String KEY_SERVICE_REFRESH_FREQUENCY = "serviceRefreshFrequency";
    public static final String KEY_LOG_LEVEL = "logLevel";
    public static final String KEY_LOG_DELETE_FREQUENCY = "logDeleteFrequency";
    
    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }
}
