package auto.qinglong.database.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import auto.qinglong.MyApplication;
import auto.qinglong.database.object.Account;

public class AccountDBHelper {
    private static MyDBHelper myDBHelper;
    private static String key_address = "address";
    private static String key_username = "username";
    private static String key_password = "password";
    private static String key_token = "token";
    private static String key_state = "state";

    static {
        myDBHelper = new MyDBHelper(MyApplication.getContext(), MyDBHelper.DB_NAME, null, MyDBHelper.VERSION);
    }

    @SuppressLint("Range")
    public static List<Account> getAllAccount() {
        List<Account> accounts = new ArrayList<>();
        Cursor cursor = myDBHelper.getWritableDatabase().query(MyDBHelper.db_account, null, null, null, null, null, null);
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
        Cursor cursor = myDBHelper.getWritableDatabase().query(MyDBHelper.db_account, null, sql, new String[]{address}, null, null, null);
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
        myDBHelper.getWritableDatabase().insert(MyDBHelper.db_account, null, values);

    }

    public static void deleteAccount(String address) {
        String where = "address = ?";
        myDBHelper.getWritableDatabase().delete(MyDBHelper.db_account, where, new String[]{address});
    }

    public static boolean isAccountExist(String address) {
        String where = "address = ?";
        Cursor cursor = myDBHelper.getWritableDatabase().query(MyDBHelper.db_account, null, where, new String[]{address}, null, null, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }
}
