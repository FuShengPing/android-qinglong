package auto.qinglong;

import android.app.Application;
import android.content.Context;

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

}
