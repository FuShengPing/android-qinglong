package auto.qinglong.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.qinglong.MyApplication;

public class SettingSP {
    private static final String TABLE = "SETTING";
    public static final String FIELD_NOTIFY = "notify";

    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(TABLE, Context.MODE_PRIVATE);
    }

    public static boolean isNotify() {
        return sp.getBoolean(FIELD_NOTIFY, true);
    }

    public static void setBoolean(String field, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(field, value);
        editor.apply();
    }
}
