package auto.panel.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ASman
 * @date: 2023/12/11
 * @description:
 */


public class ActivityUtils {

    private static List<Activity> activityList = new ArrayList<>();


    public static synchronized void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static synchronized void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    private static synchronized void closeAllActivities() {
        for (Activity activity : activityList) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    public static synchronized void clearAndStartActivity(Activity context, Class<?> cls) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Intent intent = new Intent(context, cls);
            context.startActivity(intent);
            closeAllActivities();
        });
    }

    public static synchronized void clearAndStartActivity(Activity context, Intent intent) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            context.startActivity(intent);
            closeAllActivities();
        });
    }

    public static synchronized Activity getTopActivity() {
        if (activityList.isEmpty()) {
            return null;
        } else {
            return activityList.get(activityList.size() - 1);
        }
    }
}

