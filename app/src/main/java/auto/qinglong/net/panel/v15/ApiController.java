package auto.qinglong.net.panel.v15;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author wsfsp4
 * @version 2023.07.06
 */
public class ApiController {
    private static final String ERROR_NO_BODY = "响应异常";
    private static final String ERROR_INVALID_AUTH = "登录信息失效";

    public static void getTasks(String baseUrl, String authorization, String searchValue, auto.qinglong.net.panel.ApiController.TaskListCallBack callback) {
        Call<TaskListRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getTasks(authorization, searchValue, 1, 300);

        call.enqueue(new Callback<TaskListRes>() {
            @Override
            public void onResponse(@NonNull Call<TaskListRes> call, @NonNull Response<TaskListRes> response) {
                TaskListRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(Converter.convertTasks(res.getData().getData()));
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TaskListRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getScriptFiles(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.FileListCallBack callBack) {
        Call<ScriptFileListRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getScriptFiles(authorization);

        call.enqueue(new Callback<ScriptFileListRes>() {
            @Override
            public void onResponse(Call<ScriptFileListRes> call, Response<ScriptFileListRes> response) {
                ScriptFileListRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess(Converter.convertScriptFiles(res.getData()));
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ScriptFileListRes> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getLogFiles(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.FileListCallBack callBack) {
        Call<LogFileListRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getLogFiles(authorization);

        call.enqueue(new Callback<LogFileListRes>() {
            @Override
            public void onResponse(Call<LogFileListRes> call, Response<LogFileListRes> response) {
                LogFileListRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess(Converter.convertLogFiles(res.getData()));
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LogFileListRes> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

}
