package auto.qinglong.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.qinglong.MyApplication;
import auto.qinglong.bean.app.Account;

public class AccountSP {
    private static final String TABLE = "ACCOUNT";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_TOKEN = "token";
    public static final String DEFAULT_VALUE = "";

    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(TABLE, Context.MODE_PRIVATE);
    }

    /**
     * @return 当前账号
     */
    public static Account getCurrentAccount() {
        String address = sp.getString(FIELD_ADDRESS, DEFAULT_VALUE);
        if (address.isEmpty()) {
            return null;
        }
        String username = sp.getString(FIELD_USERNAME, DEFAULT_VALUE);
        String password = sp.getString(FIELD_PASSWORD, DEFAULT_VALUE);
        String token = sp.getString(FIELD_TOKEN, DEFAULT_VALUE);
        Account account = new Account(username, password, address, token);
        account.setCurrent(true);
        return account;
    }

    /**
     * 保存当前账号
     */
    public static void saveCurrentAccount(Account account) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(FIELD_USERNAME, account.getUsername());
        editor.putString(FIELD_PASSWORD, account.getPassword());
        editor.putString(FIELD_ADDRESS, account.getAddress());
        editor.putString(FIELD_TOKEN, account.getToken());
        editor.apply();
    }

}
