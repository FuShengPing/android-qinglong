package auto.panel.database.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import auto.panel.bean.app.Account;

/**
 * @author: ASman
 * @date: 2024/2/4
 * @description:
 */


public class AccountDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public AccountDataSource(Context context) {
        dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.VERSION);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertAccount(String url, String name, String password, String token, int version) {
        ContentValues values = new ContentValues();
        values.put(AccountContract.AccountEntry.COLUMN_URL, url);
        values.put(AccountContract.AccountEntry.COLUMN_NAME, name);
        values.put(AccountContract.AccountEntry.COLUMN_PASSWORD, password);
        values.put(AccountContract.AccountEntry.COLUMN_TOKEN, token);
        values.put(AccountContract.AccountEntry.COLUMN_VERSION, version);
        return database.insert(AccountContract.AccountEntry.TABLE_NAME, null, values);
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String[] columns = {
                AccountContract.AccountEntry.COLUMN_URL,
                AccountContract.AccountEntry.COLUMN_NAME,
                AccountContract.AccountEntry.COLUMN_PASSWORD,
                AccountContract.AccountEntry.COLUMN_TOKEN,
                AccountContract.AccountEntry.COLUMN_VERSION
        };
        database.query(
                AccountContract.AccountEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
        return accounts;
    }
    // 其他数据库操作方法可以根据需要添加
}

