package auto.base.util;

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
