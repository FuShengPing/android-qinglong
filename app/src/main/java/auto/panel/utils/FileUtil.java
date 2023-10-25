package auto.panel.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import auto.base.util.LogUnit;
import auto.panel.MyApplication;

/**
 * 文件操作工具类，注意权限问题
 */
public class FileUtil {
    public static final String TAG = "FileUtil";
    private static final String SCRIPT_PATH = "/scripts";
    private static final String ENVIRONMENT_PATH = "/environments";
    private static final String TASK_PATH = "/tasks";

    public static final String internalStorage;
    public static final String externalStorage;

    static {
        externalStorage = MyApplication.getInstance().getExternalFilesDir(null).getAbsolutePath();
        internalStorage = MyApplication.getInstance().getFilesDir().getAbsolutePath();
    }

    /**
     * 保存文件.
     *
     * @param dirPath  the dir path
     * @param fileName the file name
     * @param content  the content
     * @return the boolean
     * @throws Exception the exception
     */
    public static boolean save(String dirPath, String fileName, String content) throws Exception {
        File file = new File(dirPath, fileName);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
        fileOutputStream.close();
        return true;
    }

    /**
     * 获取指定文件夹下子文件.
     *
     * @param dir    the dir
     * @param filter the filter
     * @return the files
     */
    public static List<File> getFiles(String dir, FilenameFilter filter) {
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            File[] list = file.listFiles(filter);
            if (list != null) {
                return Arrays.asList(list);
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取环境变量文件路径.
     *
     * @return the env path
     */
    public static String getEnvironmentPath() {
        return externalStorage + ENVIRONMENT_PATH;
    }

    /**
     * 获取定时任务文件路径.
     *
     * @return the task path
     */
    public static String getTaskPath() {
        return externalStorage + TASK_PATH;
    }

    /**
     * 获取脚本文件路径.
     *
     * @return the script path
     */
    public static String getScriptPath() {
        return externalStorage + SCRIPT_PATH;
    }

    /**
     * Is need request permission boolean.
     *
     * @return the boolean true is ok
     */
    public static boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Request permission.
     *
     * @param activity the activity
     */
    public static void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                LogUnit.log(TAG, "shouldShowRequestPermissionRationale");
            }
        }
    }
}
