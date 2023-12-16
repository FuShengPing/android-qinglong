package auto.panel.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author wsfsp4
 * @version 2023.07.14
 */
public class PackageUtil {

    public static Version getVersion(Context context) {
        Version version = new Version();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version.setVersionCode(info.versionCode);
            version.setVersionName(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static class Version {
        private int versionCode;

        private String versionName;

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }
    }
}
