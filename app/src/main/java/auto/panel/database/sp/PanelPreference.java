package auto.panel.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.panel.MyApplication;

public class PanelPreference {
    private static final String NAME = "PanelPreference";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_TOKEN = "token";
    private static final String DEFAULT_EMPTY = "";
    private static String mAuthorization = null;
    private static String mBaseUrl = null;
    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getInstance().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static void setAuthorization(String token) {
        mAuthorization = null;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    /**
     * 获取当前账号的登录会话
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

    public static void setAddress(String address) {
        mBaseUrl = null;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }

    public static String getAddress() {
        return sp.getString(KEY_ADDRESS, DEFAULT_EMPTY);
    }

}
