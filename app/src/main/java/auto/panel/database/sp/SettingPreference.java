package auto.panel.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.base.BaseApplication;

public class SettingPreference {
    private static final String NAME = "SettingPreference";
    public static final String FIELD_NOTIFY = "notify";
    public static final String FIELD_VIBRATE = "vibrate";
    public static final String FIELD_GROUP_KEY = "groupKey";
    public static final String FIELD_SHARE_TEXT = "shareText";

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

    public static String getGroupKey() {
        return sp.getString(FIELD_GROUP_KEY, "4e4W5fel2H5x7XKwYTB7PkBY46ZGXpaE");
    }

    public static String getShareText() {
        return sp.getString(FIELD_SHARE_TEXT, "青龙面板APP下载地址：https://gitee.com/wsfsp4/QingLong，QQ交流群：309836858");
    }
}
