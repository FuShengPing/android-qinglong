package auto.qinglong.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {
    public static int VERSION = 1;
    public static String DB_NAME = "QingLong";
    public static String db_account = "account";
    public static String db_script = "script";
    public static String db_log = "log";

    private String create_account = "create table account (" + "" +
            "address text Primary Key," +
            "username text," +
            "password text," +
            "token text," +
            "state int)";

    public MyDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_account);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
