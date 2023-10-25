package auto.panel.database.sp;

import android.content.Context;
import android.content.SharedPreferences;

import auto.panel.MyApplication;
import auto.panel.bean.app.Config;
import auto.panel.bean.app.Version;

public class SettingPreference {
    private static final String NAME = "SettingPreference";
    //
    public static final String FIELD_NOTIFY = "notify";
    public static final String FIELD_VIBRATE = "vibrate";
    //
    public static final String FIELD_VERSION_CODE = "versionCode";
    public static final String FIELD_VERSION_NAME = "versionName";
    public static final String FIELD_DOWNLOAD_URL = "downloadUrl";
    //
    public static final String FIELD_DOCUMENT_URL = "documentUrl";
    public static final String FIELD_GITEE_URL = "giteeUrl";
    public static final String FIELD_GITHUB_URL = "githubUrl";
    public static final String FIELD_GROUP_KEY = "groupKey";
    public static final String FIELD_SHARE_TEXT = "shareText";

    private static final SharedPreferences sp;

    static {
        sp = MyApplication.getInstance().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static void updateVersion(Version version) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(FIELD_VERSION_NAME, version.getVersionName());
        editor.putString(FIELD_DOWNLOAD_URL, version.getDownloadUrl());
        editor.putInt(FIELD_VERSION_CODE, version.getVersionCode());
        editor.apply();
    }

    public static void updateConfig(Config config) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(FIELD_DOCUMENT_URL, config.getDocumentUrl());
        editor.putString(FIELD_GROUP_KEY, config.getGroupKey());
        editor.putString(FIELD_GITEE_URL, config.getGiteeUrl());
        editor.putString(FIELD_GITHUB_URL, config.getGithubUrl());
        editor.putString(FIELD_SHARE_TEXT, config.getShareText());
        editor.apply();
    }

    public static void setBoolean(String field, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(field, value);
        editor.apply();
    }

    public static boolean isNotify() {
        return sp.getBoolean(FIELD_NOTIFY, true);
    }

    public static boolean isVibrate() {
        return sp.getBoolean(FIELD_VIBRATE, false);
    }

    public static int getNewVersionCode() {
        return sp.getInt(FIELD_VERSION_CODE, 9);
    }

    public static String getNewVersionName() {
        return sp.getString(FIELD_VERSION_NAME, "2.0.0");
    }

    public static String getGroupKey() {
        return sp.getString(FIELD_GROUP_KEY, "4e4W5fel2H5x7XKwYTB7PkBY46ZGXpaE");
    }

    public static String getShareText() {
        return sp.getString(FIELD_SHARE_TEXT, "青龙面板APP下载地址：https://gitee.com/wsfsp4/QingLong，QQ交流群：309836858");
    }

    public static String getDocumentUrl() {
        return sp.getString(FIELD_GITEE_URL, "https://gitee.com/wsfsp4/QingLong/blob/master/README.md");
    }

    public static String getGiteeUrl() {
        return sp.getString(FIELD_GITEE_URL, "https://gitee.com/wsfsp4/QingLong");
    }

    public static String getGithubUrl() {
        return sp.getString(FIELD_GITHUB_URL, "https://github.com/FuShengPing/android-qinglong");
    }

    public static String getDownloadUrl() {
        return sp.getString(FIELD_DOWNLOAD_URL, "https://gitee.com/wsfsp4/QingLong/releases");
    }
}
