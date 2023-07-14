package auto.panel.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import auto.panel.bean.app.Version;

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
}
