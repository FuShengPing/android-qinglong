package auto.panel.database.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        database.close();
    }

    public void insertOrUpdateAccount(String address, String name, String password, String token, String version) {
        if (isAccountExist(address)) {
            updateAccount(address, name, password, token, version);
        } else {
            insertAccount(address, name, password, token, version);
        }
    }

    public long insertAccount(String address, String name, String password, String token, String version) {
        ContentValues values = new ContentValues();
        values.put(AccountContract.AccountEntry.COLUMN_ADDRESS, address);
        values.put(AccountContract.AccountEntry.COLUMN_NAME, name);
        values.put(AccountContract.AccountEntry.COLUMN_PASSWORD, password);
        values.put(AccountContract.AccountEntry.COLUMN_TOKEN, token);
        values.put(AccountContract.AccountEntry.COLUMN_VERSION, version);
        return database.insert(AccountContract.AccountEntry.TABLE_NAME, null, values);
    }

    public int updateAccount(String address, String name, String password, String token, String version) {
        ContentValues values = new ContentValues();
        values.put(AccountContract.AccountEntry.COLUMN_NAME, name);
        values.put(AccountContract.AccountEntry.COLUMN_PASSWORD, password);
        values.put(AccountContract.AccountEntry.COLUMN_TOKEN, token);
        values.put(AccountContract.AccountEntry.COLUMN_VERSION, version);
        String selection = AccountContract.AccountEntry.COLUMN_ADDRESS + " = ?";
        String[] selectionArgs = {address};
        return database.update(AccountContract.AccountEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateAccount(Account account){
        ContentValues values = new ContentValues();
        if(account.getUsername() != null){
            values.put(AccountContract.AccountEntry.COLUMN_NAME, account.getUsername());
        }
        if(account.getPassword() != null){
            values.put(AccountContract.AccountEntry.COLUMN_PASSWORD, account.getPassword());
        }
        if(account.getToken() != null){
            values.put(AccountContract.AccountEntry.COLUMN_TOKEN, account.getToken());
        }
        if(account.getVersion() != null){
            values.put(AccountContract.AccountEntry.COLUMN_VERSION, account.getVersion());
        }
        String selection = AccountContract.AccountEntry.COLUMN_ADDRESS + " = ?";
        String[] selectionArgs = {account.getAddress()};
        return database.update(AccountContract.AccountEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void deleteAccount(String address) {
        String selection = AccountContract.AccountEntry.COLUMN_ADDRESS + " = ?";
        String[] selectionArgs = {address};
        database.delete(AccountContract.AccountEntry.TABLE_NAME, selection, selectionArgs);
    }

    public boolean isAccountExist(String address) {
        String[] columns = {
                AccountContract.AccountEntry.COLUMN_ADDRESS
        };
        String selection = AccountContract.AccountEntry.COLUMN_ADDRESS + " = ?";
        String[] selectionArgs = {address};
        Cursor cursor = database.query(
                AccountContract.AccountEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    @SuppressLint("Range")
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String[] columns = {
                AccountContract.AccountEntry.COLUMN_ADDRESS,
                AccountContract.AccountEntry.COLUMN_NAME,
                AccountContract.AccountEntry.COLUMN_PASSWORD,
                AccountContract.AccountEntry.COLUMN_TOKEN,
                AccountContract.AccountEntry.COLUMN_VERSION
        };

        Cursor cursor = database.query(
                AccountContract.AccountEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            Account account = new Account();
            account.setAddress(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_ADDRESS)));
            account.setUsername(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_NAME)));
            account.setPassword(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_PASSWORD)));
            account.setToken(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_TOKEN)));
            account.setVersion(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_VERSION)));
            accounts.add(account);
        }

        cursor.close();

        return accounts;
    }

    @SuppressLint("Range")
    public Account getAccount(String address) {
        String[] columns = {
                AccountContract.AccountEntry.COLUMN_ADDRESS,
                AccountContract.AccountEntry.COLUMN_NAME,
                AccountContract.AccountEntry.COLUMN_PASSWORD,
                AccountContract.AccountEntry.COLUMN_TOKEN,
                AccountContract.AccountEntry.COLUMN_VERSION
        };
        String selection = AccountContract.AccountEntry.COLUMN_ADDRESS + " = ?";
        String[] selectionArgs = {address};
        Cursor cursor = database.query(
                AccountContract.AccountEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Account account = null;
        if (cursor.moveToFirst()) {
            account = new Account();
            account.setAddress(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_ADDRESS)));
            account.setUsername(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_NAME)));
            account.setPassword(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_PASSWORD)));
            account.setToken(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_TOKEN)));
            account.setVersion(cursor.getString(cursor.getColumnIndex(AccountContract.AccountEntry.COLUMN_VERSION)));
        }
        cursor.close();
        return account;
    }
}

