package auto.qinglong.database.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import auto.qinglong.MyApplication;
import auto.qinglong.bean.app.WebRule;

public class WebRuleDBHelper {
    public static final String TAG = "WebRuleDBHelper";
    private static final DBHelper DBHelper;
    public static final String key_id = "id";
    public static final String key_name = "name";
    public static final String key_env_name = "envName";
    public static final String key_url = "url";
    public static final String key_target = "target";
    public static final String key_main = "main";
    public static final String key_join_char = "joinChar";
    public static final String key_checked = "checked";

    static {
        DBHelper = new DBHelper(MyApplication.getContext(), auto.qinglong.database.db.DBHelper.DB_NAME, null, auto.qinglong.database.db.DBHelper.VERSION);
    }

    @SuppressLint("Range")
    public static List<WebRule> getAll() {
        List<WebRule> webRules = new ArrayList<>();
        Cursor cursor = DBHelper.getWritableDatabase().query(auto.qinglong.database.db.DBHelper.TABLE_PLUGIN_WEB_RULE, null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            do {
                WebRule webRule = new WebRule();
                webRule.setId(cursor.getInt(cursor.getColumnIndex(key_id)));
                webRule.setEnvName(cursor.getString(cursor.getColumnIndex(key_env_name)));
                webRule.setName(cursor.getString(cursor.getColumnIndex(key_name)));
                webRule.setUrl(cursor.getString(cursor.getColumnIndex(key_url)));
                webRule.setTarget(cursor.getString(cursor.getColumnIndex(key_target)));
                webRule.setMain(cursor.getString(cursor.getColumnIndex(key_main)));
                webRule.setJoinChar(cursor.getString(cursor.getColumnIndex(key_join_char)));
                webRule.setChecked(cursor.getInt(cursor.getColumnIndex(key_checked)) > 0);
                webRules.add(webRule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return webRules;
    }

    public static void insert(WebRule webRule) {
        ContentValues values = new ContentValues();
        values.put(key_env_name, webRule.getEnvName());
        values.put(key_name, webRule.getName());
        values.put(key_url, webRule.getUrl());
        values.put(key_target, webRule.getTarget());
        values.put(key_main, webRule.getMain());
        values.put(key_join_char, webRule.getJoinChar());
        values.put(key_checked, webRule.isChecked() ? 1 : 0);

        DBHelper.getWritableDatabase().insert(auto.qinglong.database.db.DBHelper.TABLE_PLUGIN_WEB_RULE, null, values);

    }

    public static void update(int id, boolean isChecked) {
        String where = "id = ?";
        ContentValues values = new ContentValues();
        values.put(key_checked, isChecked ? 1 : 0);
        DBHelper.getWritableDatabase().update(auto.qinglong.database.db.DBHelper.TABLE_PLUGIN_WEB_RULE, values, where, new String[]{String.valueOf(id)});
    }

    public static void delete(int id) {
        String where = "id = ?";
        DBHelper.getWritableDatabase().delete(auto.qinglong.database.db.DBHelper.TABLE_PLUGIN_WEB_RULE, where, new String[]{String.valueOf(id)});
    }

    public static boolean isExist(int id) {
        String where = "id = ?";
        Cursor cursor = DBHelper.getWritableDatabase().query(auto.qinglong.database.db.DBHelper.TABLE_PLUGIN_WEB_RULE, null, where, new String[]{String.valueOf(id)}, null, null, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }
}

