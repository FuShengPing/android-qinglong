package auto.qinglong.database.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import auto.qinglong.MyApplication;
import auto.qinglong.bean.app.Account;

public class AccountDBHelper {
    protected static DBHelper DBHelper;
    protected static final String key_address = "address";
    protected static final String key_username = "username";
    protected static final String key_password = "password";
    protected static final String key_token = "token";
    protected static final String key_state = "state";

    static {
        DBHelper = new DBHelper(MyApplication.getContext(), auto.qinglong.database.db.DBHelper.DB_NAME, null, auto.qinglong.database.db.DBHelper.VERSION);
    }

    @SuppressLint("Range")
    public static List<Account> getAllAccount() {
        List<Account> accounts = new ArrayList<>();
        Cursor cursor = DBHelper.getWritableDatabase().query(auto.qinglong.database.db.DBHelper.TABLE_ACCOUNT, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Account account = new Account();
                account.setAddress(cursor.getString(cursor.getColumnIndex(key_address)));
                account.setUsername(cursor.getString(cursor.getColumnIndex(key_username)));
                account.setPassword(cursor.getString(cursor.getColumnIndex(key_password)));
                account.setToken(cursor.getString(cursor.getColumnIndex(key_token)));
                account.setState(cursor.getInt(cursor.getColumnIndex(key_state)));
                accounts.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }

    @SuppressLint("Range")
    public static Account getAccount(String address) {
        Account account = null;
        String sql = "address = ?";
        Cursor cursor = DBHelper.getWritableDatabase().query(auto.qinglong.database.db.DBHelper.TABLE_ACCOUNT, null, sql, new String[]{address}, null, null, null);
        if (cursor.moveToFirst()) {
            account = new Account();
            account.setAddress(cursor.getString(cursor.getColumnIndex(key_address)));
            account.setUsername(cursor.getString(cursor.getColumnIndex(key_username)));
            account.setPassword(cursor.getString(cursor.getColumnIndex(key_password)));
            account.setToken(cursor.getString(cursor.getColumnIndex(key_token)));
            account.setState(cursor.getInt(cursor.getColumnIndex(key_state)));
        }
        cursor.close();
        return account;
    }

    public static void insertAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(key_address, account.getAddress());
        values.put(key_username, account.getUsername());
        values.put(key_password, account.getPassword());
        values.put(key_token, account.getToken());
        values.put(key_state, account.getState());
        if (isAccountExist(account.getAddress())) {
            deleteAccount(account.getAddress());
        }
        DBHelper.getWritableDatabase().insert(auto.qinglong.database.db.DBHelper.TABLE_ACCOUNT, null, values);

    }

    public static void deleteAccount(String address) {
        String where = "address = ?";
        DBHelper.getWritableDatabase().delete(auto.qinglong.database.db.DBHelper.TABLE_ACCOUNT, where, new String[]{address});
    }

    public static boolean isAccountExist(String address) {
        String where = "address = ?";
        Cursor cursor = DBHelper.getWritableDatabase().query(auto.qinglong.database.db.DBHelper.TABLE_ACCOUNT, null, where, new String[]{address}, null, null, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }
}
