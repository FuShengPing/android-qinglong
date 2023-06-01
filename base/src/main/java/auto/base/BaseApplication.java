package auto.base;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
    private static Context context;
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
    }

    /**
     * @return 全局context
     */
    public static Context getContext() {
        return context;
    }

    /**
     * @return Application实例
     */
    public static Application getInstance() {
        return instance;
    }

}
