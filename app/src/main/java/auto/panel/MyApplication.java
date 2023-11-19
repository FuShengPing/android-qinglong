package auto.panel;

import androidx.annotation.NonNull;

import auto.base.BaseApplication;
import auto.base.util.TimeUnit;
import auto.panel.utils.FileUtil;
import auto.panel.utils.thread.AppLogTask;

/**
 * @author wsfsp4
 * @version 2023.06.01
 */
public class MyApplication extends BaseApplication implements Thread.UncaughtExceptionHandler {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        // 单例对象
        instance = this;
        // 全局异常捕捉
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        try {
            String fileName = String.format("app-%s.log", TimeUnit.formatDate());
            String filePath = FileUtil.getPathOfLog();

            FileUtil.save(filePath, fileName, AppLogTask.buildContentOfStack(throwable), true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

