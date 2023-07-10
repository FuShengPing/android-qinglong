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

    public static void checkAccountToken(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.BaseCallBack callBack) {
        Call<SystemConfigRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getSystemConfig(authorization);

        call.enqueue(new Callback<SystemConfigRes>() {
            @Override
            public void onResponse(Call<SystemConfigRes> call, Response<SystemConfigRes> response) {
                SystemConfigRes res = response.body();
                if (auto.qinglong.net.panel.ApiController.checkResponse(response.code(), res, callBack)) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<SystemConfigRes> call, Throwable t) {
                auto.qinglong.net.panel.ApiController.handleRequestError(call, t, callBack);
            }
        });

    }

    public static void getTasks(String baseUrl, String authorization, String searchValue, auto.qinglong.net.panel.ApiController.TaskListCallBack callBack) {
        Call<TasksRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getTasks(authorization, searchValue, 1, 300);

        call.enqueue(new Callback<TasksRes>() {
            @Override
            public void onResponse(@NonNull Call<TasksRes> call, @NonNull Response<TasksRes> response) {
                TasksRes res = response.body();
                if (auto.qinglong.net.panel.ApiController.checkResponse(response.code(), res, callBack)) {
                    callBack.onSuccess(Converter.convertTasks(res.getData().getData()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TasksRes> call, @NonNull Throwable t) {
                auto.qinglong.net.panel.ApiController.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getScripts(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.FileListCallBack callBack) {
        Call<ScriptFilesRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getScriptFiles(authorization);

        call.enqueue(new Callback<ScriptFilesRes>() {
            @Override
            public void onResponse(Call<ScriptFilesRes> call, Response<ScriptFilesRes> response) {
                ScriptFilesRes res = response.body();
                if (auto.qinglong.net.panel.ApiController.checkResponse(response.code(), res, callBack)) {
                    callBack.onSuccess(Converter.convertScriptFiles(res.getData()));
                }
            }

            @Override
            public void onFailure(Call<ScriptFilesRes> call, Throwable t) {
                auto.qinglong.net.panel.ApiController.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getLogs(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.FileListCallBack callBack) {
        Call<LogFilesRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getLogFiles(authorization);

        call.enqueue(new Callback<LogFilesRes>() {
            @Override
            public void onResponse(Call<LogFilesRes> call, Response<LogFilesRes> response) {
                LogFilesRes res = response.body();
                if (auto.qinglong.net.panel.ApiController.checkResponse(response.code(), res, callBack)) {
                    callBack.onSuccess(Converter.convertLogFiles(res.getData()));
                }
            }

            @Override
            public void onFailure(Call<LogFilesRes> call, Throwable t) {
                auto.qinglong.net.panel.ApiController.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getDependencies(@NonNull String baseUrl, @NonNull String authorization, String searchValue, String type, auto.qinglong.net.panel.ApiController.DependenceListCallBack callBack) {
        Call<DependenciesRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getDependencies(authorization, searchValue, type);

        call.enqueue(new Callback<DependenciesRes>() {
            @Override
            public void onResponse(Call<DependenciesRes> call, Response<DependenciesRes> response) {
                DependenciesRes res = response.body();
                if (auto.qinglong.net.panel.ApiController.checkResponse(response.code(), res, callBack)) {
                    callBack.onSuccess(Converter.convertDependencies(res.getData()));
                }
            }

            @Override
            public void onFailure(Call<DependenciesRes> call, Throwable t) {
                auto.qinglong.net.panel.ApiController.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getSystemConfig(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.SystemConfigCallBack callBack) {
        Call<SystemConfigRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getSystemConfig(authorization);

        call.enqueue(new Callback<SystemConfigRes>() {
            @Override
            public void onResponse(Call<SystemConfigRes> call, Response<SystemConfigRes> response) {
                SystemConfigRes res = response.body();
                if (auto.qinglong.net.panel.ApiController.checkResponse(response.code(), res, callBack)) {
                    callBack.onSuccess(Converter.convertSystemConfig(res.getData()));
                }
            }

            @Override
            public void onFailure(Call<SystemConfigRes> call, Throwable t) {
                auto.qinglong.net.panel.ApiController.handleRequestError(call, t, callBack);
            }
        });
    }
}
