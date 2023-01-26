package auto.qinglong.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import auto.qinglong.MyApplication;

public class FileUtil {
    public static final String TAG = "FileUtil";
    public static final String internalStorage;
    public static final String externalStorage;
    public static final String packageName = "qinglong";
    private static final String scriptPath = "scripts";
    private static final String envPath = "environments";
    private static final String taskPath = "tasks";

    static {
        internalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        externalStorage = MyApplication.getContext().getFilesDir().getAbsolutePath();
    }

    public static boolean save(String parentPath, String fileName, String content) {
        File file = new File(parentPath, fileName);
        try {
            if (!file.mkdirs()) {
                return false;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getEnvPath() {
        return externalStorage + "/" + packageName + "/" + envPath;
    }

    public static String getScriptPath() {
        return externalStorage + "/" + packageName + "/" + scriptPath;
    }

    public static String getTaskPath() {
        return externalStorage + "/" + packageName + "/" + taskPath;
    }

    public static boolean checkPermission() {
        return ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }
}
