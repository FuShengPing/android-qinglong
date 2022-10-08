package auto.qinglong.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.qinglong.MyApplication;
import auto.qinglong.module.app.account.Account;

public class AccountSP {
    private static final String TAG = "ACCOUNT";
    private static final String field_username = "username";
    private static final String field_password = "password";
    private static final String field_address = "address";
    private static final String field_token = "token";
    private static final String defaultValue = "";

    private static Account currentAccount;
    private static SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    /**
     * @return 上次登录成功的账号
     */
    public static Account getAccount() {
        String address = sp.getString(field_address, defaultValue);
        if (address.isEmpty()) {
            return null;
        }
        String username = sp.getString(field_username, defaultValue);
        String password = sp.getString(field_password, defaultValue);
        String token = sp.getString(field_token, defaultValue);
        return new Account(username, password, address, token);
    }

    /**
     * @return 本次登录成功的账号
     */
    public static Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     * 保存本次登录成功的账号
     *
     * @param account
     */
    public static void saveAccount(Account account) {
        currentAccount = account;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(field_username, account.getUsername());
        editor.putString(field_password, account.getPassword());
        editor.putString(field_address, account.getAddress());
        editor.putString(field_token, account.getToken());
        editor.apply();
    }


}
