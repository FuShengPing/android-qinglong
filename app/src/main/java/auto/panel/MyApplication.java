package auto.panel;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import auto.base.BaseApplication;
import auto.base.util.TimeUnit;

/**
 * @author wsfsp4
 * @version 2023.06.01
 */
public class MyApplication extends BaseApplication implements Thread.UncaughtExceptionHandler {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        // 全局异常捕捉
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 单例对象
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        try {
            // 将异常信息写入本地文件
            StringBuilder sb = new StringBuilder();
            String datetime = TimeUnit.formatDatetimeA();

            sb.append("[").append(datetime).append("]\n");
            sb.append(throwable.getMessage());
            for (StackTraceElement element : throwable.getCause().getStackTrace()) {
                sb.append(element.toString()).append("\n");
            }
            sb.append("\n");

            String fileName = TimeUnit.formatDate() + ".log";

            File file = new File(getExternalFilesDir(null), fileName);
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append(sb.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 结束应用
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}

