package auto.qinglong;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /**
     * @return 全局context
     */
    public static Context getContext() {
        return context;
    }

    /**
     * 检测权限是否完全
     *
     * @return the boolean
     */
    public static boolean checkSelfPermission() {
        //存储权限
        int f1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int f2 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return f1 == PackageManager.PERMISSION_GRANTED && f2 == PackageManager.PERMISSION_GRANTED;
    }

}
