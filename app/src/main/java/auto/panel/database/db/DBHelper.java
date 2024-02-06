package auto.panel.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DB_NAME = "Panel";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ACCOUNT_TABLE = "CREATE TABLE " + AccountContract.AccountEntry.TABLE_NAME + " ("
                + AccountContract.AccountEntry.COLUMN_ADDRESS + " TEXT NOT NULL PRIMARY KEY, "
                + AccountContract.AccountEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + AccountContract.AccountEntry.COLUMN_PASSWORD + " TEXT NOT NULL, "
                + AccountContract.AccountEntry.COLUMN_TOKEN + " TEXT NOT NULL, "
                + AccountContract.AccountEntry.COLUMN_VERSION + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ACCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
