package auto.panel;

import androidx.annotation.NonNull;

import com.baidu.mobstat.StatService;

import auto.base.BaseApplication;
import auto.panel.utils.FileUtil;
import auto.panel.utils.TimeUnit;
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

        // SDK初始化，该函数不会采集用户个人信息，也不会向百度移动统计后台上报数据
        StatService.init(this, "17a6942080", "Github");

        // 通过该接口可以控制敏感数据采集，true表示可以采集，false表示不可以采集，
        // 该方法一定要最优先调用，请在StatService.start(this)之前调用，采集这些数据可以帮助App运营人员更好的监控App的使用情况，
        // 建议有用户隐私策略弹窗的App，用户未同意前设置false,同意之后设置true
        StatService.setAuthorizedState(this, true);

        // 自动埋点，建议在Application中调用。否则可能造成部分页面遗漏，无法完整统计。
        StatService.autoTrace(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        StatService.recordException(this, throwable);
        try {
            String fileName = String.format("app-%s.log", TimeUnit.formatDate());
            String filePath = FileUtil.getPathOfLog();

            FileUtil.save(filePath, fileName, AppLogTask.buildContentOfStack(throwable), true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

