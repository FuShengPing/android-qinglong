package auto.qinglong.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DB_NAME = "QingLong";
    public static final String TABLE_ACCOUNT = "account";
    public static final String TABLE_PLUGIN_WEB_RULE = "pluginWebRule";

    private final String CREATE_ACCOUNT = "create table account (" +
            "address text Primary Key," +
            "username text," +
            "password text," +
            "token text," +
            "state int)";

    private final String CREATE_PLUGIN_WEB_RULE = "create table pluginWebRule (" +
            "id INTEGER Primary Key autoincrement ," +
            "envName text," +
            "name text," +
            "url text," +
            "target text," +
            "main text," +
            "checked int)";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT);
        db.execSQL(CREATE_PLUGIN_WEB_RULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
