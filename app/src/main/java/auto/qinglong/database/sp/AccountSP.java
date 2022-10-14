package auto.qinglong.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.qinglong.MyApplication;
import auto.qinglong.activity.app.account.Account;

public class AccountSP {
    private static final String TAG = "ACCOUNT";
    private static final String field_username = "username";
    private static final String field_password = "password";
    private static final String field_address = "address";
    private static final String field_token = "token";
    private static final String defaultValue = "";

    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    /**
     * @return 当前账号
     */
    public static Account getCurrentAccount() {
        String address = sp.getString(field_address, defaultValue);
        if (address.isEmpty()) {
            return null;
        }
        String username = sp.getString(field_username, defaultValue);
        String password = sp.getString(field_password, defaultValue);
        String token = sp.getString(field_token, defaultValue);
        Account account = new Account(username, password, address, token);
        account.setCurrent(true);
        return account;
    }

    /**
     * 保存当前账号
     */
    public static void saveCurrentAccount(Account account) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(field_username, account.getUsername());
        editor.putString(field_password, account.getPassword());
        editor.putString(field_address, account.getAddress());
        editor.putString(field_token, account.getToken());
        editor.apply();
    }

}
