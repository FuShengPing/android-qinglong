package auto.qinglong.net.panel;

import androidx.annotation.NonNull;

import java.util.List;

import auto.qinglong.bean.panel.SystemInfo;
import auto.qinglong.bean.views.Task;
import auto.qinglong.net.SystemLogConfigRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class ApiController {
    private static final String ERROR_NO_BODY = "响应异常";
    private static final String ERROR_INVALID_AUTH = "登录信息失效";

    public static void getSystemInfo(@NonNull String baseUrl,@NonNull SystemCallBack callBack) {
        Call<SystemInfoRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getSystemInfo();

        call.enqueue(new Callback<SystemInfoRes>() {
            @Override
            public void onResponse(@NonNull Call<SystemInfoRes> call, @NonNull Response<SystemInfoRes> response) {
                SystemInfoRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        SystemInfo system = new SystemInfo();
                        system.setInitialized(res.getData().isInitialized());
                        system.setVersion(res.getData().getVersion());
                        callBack.onSuccess(system);
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SystemInfoRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void checkAccountToken(@NonNull String baseUrl, @NonNull String authorization, BaseCallBack callBack) {
        Call<SystemLogConfigRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getSystemLogConfig(authorization);

        call.enqueue(new Callback<SystemLogConfigRes>() {
            @Override
            public void onResponse(@NonNull Call<SystemLogConfigRes> call, @NonNull Response<SystemLogConfigRes> response) {
                SystemLogConfigRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess();
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SystemLogConfigRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getTasks(@NonNull String baseUrl, @NonNull String authorization, String searchValue, TaskCallBack callback) {
        auto.qinglong.net.panel.v10.ApiController.getTasks(baseUrl, authorization, searchValue, callback);
    }

    public static void runTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.runTasks(baseUrl, authorization, keys, callBack);
    }

    public static void stopTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.stopTasks(baseUrl, authorization, keys, callBack);
    }

    public static void enableTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.enableTasks(baseUrl, authorization, keys, callBack);
    }

    public static void disableTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.disableTasks(baseUrl, authorization, keys, callBack);
    }

    public static void pinTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.pinTasks(baseUrl, authorization, keys, callBack);
    }

    public static void unpinTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.unpinTasks(baseUrl, authorization, keys, callBack);
    }

    public static void deleteTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.deleteTasks(baseUrl, authorization, keys, callBack);
    }

    public static void updateTask(@NonNull String baseUrl, @NonNull String authorization, Task task, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.updateTask(baseUrl, authorization, task, callBack);
    }

    public static void createTask(@NonNull String baseUrl, @NonNull String authorization, Task task, BaseCallBack callBack) {
        auto.qinglong.net.panel.v10.ApiController.createTask(baseUrl, authorization, task, callBack);
    }

    public interface SystemCallBack {
        void onSuccess(SystemInfo system);

        void onFailure(String msg);
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


