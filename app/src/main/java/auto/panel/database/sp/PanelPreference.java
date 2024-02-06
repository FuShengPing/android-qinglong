package auto.panel.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.panel.MyApplication;
import auto.panel.bean.panel.PanelAccount;

public class PanelPreference {
    private static final String NAME = "PanelPreference";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_VERSION = "version";

    private static final String DEFAULT_EMPTY = "";

    private static String mAuthorization = null;
    private static String mBaseUrl = null;
    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getInstance().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取当前登录成功过的账号
     *
     * @return 当前账号
     */
    public static PanelAccount getCurrentAccount() {
        String address = sp.getString(KEY_ADDRESS, DEFAULT_EMPTY);
        if (address.isEmpty()) {
            return null;
        }
        String username = sp.getString(KEY_USERNAME, DEFAULT_EMPTY);
        String password = sp.getString(KEY_PASSWORD, DEFAULT_EMPTY);
        String token = sp.getString(KEY_TOKEN, DEFAULT_EMPTY);
        return new PanelAccount(username, password, address, token);
    }

    /**
     * 获取当前账号的登录会话 只能在登录后调用 否则将导致窜号问题.
     *
     * @return the current token
     */
    public static String getAuthorization() {
        if (mAuthorization == null) {
            mAuthorization = "Bearer " + sp.getString(KEY_TOKEN, DEFAULT_EMPTY);
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
        String curAddress = sp.getString(KEY_ADDRESS, DEFAULT_EMPTY);
        String curUsername = sp.getString(KEY_USERNAME, DEFAULT_EMPTY);
        String curPassword = sp.getString(KEY_PASSWORD, DEFAULT_EMPTY);
        assert curAddress != null;
        assert curUsername != null;
        assert curPassword != null;
        boolean flag = curAddress.equals(address) && curPassword.equals(password) && curUsername.equals(username);
        if (flag) {
            return sp.getString(KEY_TOKEN, DEFAULT_EMPTY);
        } else {
            return null;
        }
    }

    /**
     * Gets retrofit baseurl.
     *
     * @return retrofit baseurl
     */
    public static String getBaseUrl() {
        if (mBaseUrl == null) {
            String address = sp.getString(KEY_ADDRESS, DEFAULT_EMPTY);
            assert address != null;
            if (!address.startsWith("http://") && !address.startsWith("https://")) {
                address = "http://" + address; // 默认http
            }
            if (!address.endsWith("/")) {
                address = address + "/";
            }
            mBaseUrl = address;
        }
        return mBaseUrl;
    }

    public static String getAddress() {
        return sp.getString(KEY_ADDRESS, DEFAULT_EMPTY);
    }

    /**
     * 更新当前账号信息
     * 改密场景：只更新用户名和密码,同时清除token
     * 登录场景：更新全部字段
     */
    public static void updateCurrentAccount(PanelAccount account) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_USERNAME, account.getUsername());
        editor.putString(KEY_PASSWORD, account.getPassword());
        editor.putString(KEY_TOKEN, account.getToken());

        if (account.getAddress() != null) {
            editor.putString(KEY_ADDRESS, account.getAddress());
        }

        mBaseUrl = null;
        mAuthorization = null;

        editor.apply();
    }

    public static String getVersion() {
        return sp.getString(KEY_VERSION, DEFAULT_EMPTY);
    }

    public static void setVersion(String version) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_VERSION, version);
        editor.apply();
    }

}
