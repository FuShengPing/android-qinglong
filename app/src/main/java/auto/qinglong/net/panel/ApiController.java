package auto.qinglong.net.panel;

import androidx.annotation.Nullable;

import java.util.List;

import auto.qinglong.bean.views.Task;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class ApiController {
    public static void getTasks(@Nullable String baseUrl, @Nullable String authorization, String searchValue, TaskCallBack callback) {
        auto.qinglong.net.panel.v10.ApiController.getTasks(baseUrl, authorization, searchValue, callback);
    }

    public static void runTasks(@Nullable String baseUrl, @Nullable String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.runTasks(baseUrl, authorization, keys, callBack);
    }

    public static void stopTasks(@Nullable String baseUrl, @Nullable String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.stopTasks(baseUrl, authorization, keys, callBack);
    }

    public static void enableTasks(@Nullable String baseUrl, @Nullable String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.enableTasks(baseUrl, authorization, keys, callBack);
    }

    public static void disableTasks(@Nullable String baseUrl, @Nullable String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.disableTasks(baseUrl, authorization, keys, callBack);
    }

    public static void pinTasks(@Nullable String baseUrl, @Nullable String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.pinTasks(baseUrl, authorization, keys, callBack);
    }

    public static void unpinTasks(@Nullable String baseUrl, @Nullable String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.unpinTasks(baseUrl, authorization, keys, callBack);
    }

    public static void deleteTasks(@Nullable String baseUrl, @Nullable String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.deleteTasks(baseUrl, authorization, keys, callBack);
    }

    public static void createTask(@Nullable String baseUrl, @Nullable String authorization, Task task, BaseCallBack callBack) {

    }


    public interface TaskCallBack {
        void onSuccess(List<Task> tasks);

        void onFailure(String msg);
    }

    public interface BaseCallBack {
        void onSuccess();

        void onFailure(String msg);
    }
}


