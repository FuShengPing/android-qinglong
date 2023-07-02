package auto.qinglong.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.base.BaseApplication;

public class SettingPreference {
    private static final String NAME = "SettingPreference";
    public static final String FIELD_NOTIFY = "notify";
    public static final String FIELD_VIBRATE = "vibrate";
    private static final SharedPreferences sp;

    static {
        sp = BaseApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static boolean isNotify() {
        return sp.getBoolean(FIELD_NOTIFY, true);
    }

    public static boolean isVibrate() {
        return sp.getBoolean(FIELD_VIBRATE, false);
    }

    public static void setBoolean(String field, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(field, value);
        editor.apply();
    }
}
