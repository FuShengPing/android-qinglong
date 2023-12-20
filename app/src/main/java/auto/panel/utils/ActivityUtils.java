package auto.panel.utils;

import android.app.Activity;
import android.content.Context;
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


    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    private static void closeAllActivities() {
        for (Activity activity : activityList) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    public static void clearAndStartActivity(Context context, Class<?> cls) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            closeAllActivities();
            Intent intent = new Intent(context, cls);
            context.startActivity(intent);
        });
    }

    public static void clearAndStartActivity(Context context, Intent intent) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            closeAllActivities();
            context.startActivity(intent);
        });
    }

    public static Activity getTopActivity() {
        if (activityList.isEmpty()) {
            return null;
        } else {
            return activityList.get(activityList.size() - 1);
        }
    }
}

