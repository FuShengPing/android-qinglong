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

    private static String mAuthorization = null;
    private static String mBaseUrl = null;
    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getContext().getSharedPreferences(TABLE, Context.MODE_PRIVATE);
    }

    /**
     * 获取当前登录成功过的账号
     *
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
     * 获取当前账号的登录会话 只能在登录后调用 否则将导致窜号问题.
     *
     * @return the current token
     */
    public static String getAuthorization() {
        if (mAuthorization == null) {
            mAuthorization = "Bearer " + sp.getString(FIELD_TOKEN, DEFAULT_VALUE);
        }
        return mAuthorization;
    }

    /**
     * 获取指定地址的会话 登录前调用检验是否有效 避免重复登录.
     *
     * @param address the address
     * @return the authorization 指定地址的会话
     */
    public static String getAuthorization(String address, String username, String password) {
        String curAddress = sp.getString(FIELD_ADDRESS, DEFAULT_VALUE);
        String curUsername = sp.getString(FIELD_USERNAME, DEFAULT_VALUE);
        String curPassword = sp.getString(FIELD_PASSWORD, DEFAULT_VALUE);
        boolean flag = curAddress.equals(address) && curPassword.equals(password) && curUsername.equals(username);
        if (flag) {
            return sp.getString(FIELD_TOKEN, DEFAULT_VALUE);
        } else {
            return null;
        }
    }

    public static String getBaseUrl() {
        if (mBaseUrl == null) {
            mBaseUrl = "http://" + sp.getString(FIELD_ADDRESS, DEFAULT_VALUE) + "/";
        }
        return mBaseUrl;
    }

    /**
     * 更新当前账号信息
     * 改密场景：只更新用户名和密码,同时清除token
     * 登录场景：更新全部字段
     */
    public static void updateCurrentAccount(Account account) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(FIELD_USERNAME, account.getUsername());
        editor.putString(FIELD_PASSWORD, account.getPassword());
        editor.putString(FIELD_TOKEN, account.getToken());

        if (account.getAddress() != null) {
            editor.putString(FIELD_ADDRESS, account.getAddress());
        }

        mBaseUrl = null;
        mAuthorization = null;

        editor.apply();
    }
}
