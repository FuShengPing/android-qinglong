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

    public interface TaskCallBack {
        void onSuccess(List<Task> tasks);

        void onFailure(String msg);
    }

    public interface BaseCallBack {
        void onSuccess();

        void onFailure(String msg);
    }
}


