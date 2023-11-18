package auto.panel.net.panel.v15;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

import auto.base.util.TextUnit;
import auto.panel.bean.panel.PanelFile;
import auto.panel.bean.panel.PanelSystemConfig;
import auto.panel.net.panel.BaseRes;
import auto.panel.net.panel.Handler;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    public static void checkAccountToken(@NonNull String baseUrl, @NonNull String authorization, auto.panel.net.panel.ApiController.BaseCallBack callBack) {
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
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Call<SystemConfigRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });

    }

    public static void getTasks(@NonNull String baseUrl, @NonNull String authorization, String searchValue, auto.panel.net.panel.ApiController.TaskListCallBack callBack) {
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
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertTasks(res.getData().getData()));
            }

            @Override
            public void onFailure(@NonNull Call<TasksRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getEnvironments(@NonNull String baseUrl, @NonNull String authorization, @NonNull String searchValue, auto.panel.net.panel.ApiController.EnvironmentListCallBack callBack) {
        Call<EnvironmentsRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getEnvironments(authorization, searchValue);

        call.enqueue(new Callback<EnvironmentsRes>() {
            @Override
            public void onResponse(Call<EnvironmentsRes> call, Response<EnvironmentsRes> response) {
                EnvironmentsRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertEnvironments(res.getData()));
            }

            @Override
            public void onFailure(Call<EnvironmentsRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getDependencies(@NonNull String baseUrl, @NonNull String authorization, String searchValue, String type, auto.panel.net.panel.ApiController.DependenceListCallBack callBack) {
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
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertDependencies(res.getData()));
            }

            @Override
            public void onFailure(Call<DependenciesRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void deleteDependencies(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, auto.panel.net.panel.ApiController.BaseCallBack callBack) {
        RequestBody body = auto.panel.net.panel.ApiController.buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .deleteDependencies(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getScripts(@NonNull String baseUrl, @NonNull String authorization, auto.panel.net.panel.ApiController.FileListCallBack callBack) {
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
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertScriptFiles(res.getData()));
            }

            @Override
            public void onFailure(Call<ScriptFilesRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void addScript(@NonNull String baseUrl, @NonNull String authorization, @NonNull PanelFile file, auto.panel.net.panel.ApiController.BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        if (file.isDir()) {
            jsonObject.addProperty("directory", file.getTitle());
        } else {
            jsonObject.addProperty("filename", file.getTitle());
            jsonObject.addProperty("content", file.getContent());
        }
        jsonObject.addProperty("path", file.getParentPath());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addScript(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getLogs(@NonNull String baseUrl, @NonNull String authorization, auto.panel.net.panel.ApiController.FileListCallBack callBack) {
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
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertLogFiles(res.getData()));
            }

            @Override
            public void onFailure(Call<LogFilesRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getSystemConfig(@NonNull String baseUrl, @NonNull String authorization, auto.panel.net.panel.ApiController.SystemConfigCallBack callBack) {
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
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertSystemConfig(res.getData().getInfo()));
            }

            @Override
            public void onFailure(Call<SystemConfigRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void updateSystemConfig(@NonNull String baseUrl, @NonNull String authorization, PanelSystemConfig config, @NonNull auto.panel.net.panel.ApiController.BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cronConcurrency", config.getCronConcurrency());
        jsonObject.addProperty("logRemoveFrequency", config.getLogRemoveFrequency());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateSystemConfig(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static String getLogFilePath(String scriptKey, String fileName, String fileParent) {
        String path;
        if (TextUnit.isFull(scriptKey)) {//任务日志
            path = "api/crons/" + scriptKey + "/log";
        } else {//脚本日志
            path = "api/logs/" + fileName + "?path=" + (TextUnit.isFull(fileParent) ? fileParent : "");
        }
        return path;
    }
}
