package auto.qinglong.net.panel.v10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import auto.base.util.LogUnit;
import auto.base.util.TextUnit;
import auto.qinglong.bean.panel.Account;
import auto.qinglong.bean.panel.SystemConfig;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.net.NetManager;
import auto.qinglong.net.panel.BaseRes;
import auto.qinglong.net.panel.Handler;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * QL API统一请求类
 */
public class ApiController {
    private static final String ERROR_NO_BODY = "响应异常";
    private static final String ERROR_INVALID_AUTH = "登录信息失效";

    public static void checkAccountToken(String baseUrl, String authorization, auto.qinglong.net.panel.ApiController.BaseCallBack callBack) {
        Call<SystemConfigRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getSystemConfig(authorization);

        call.enqueue(new Callback<SystemConfigRes>() {
            @Override
            public void onResponse(@NonNull Call<SystemConfigRes> call, @NonNull Response<SystemConfigRes> response) {
                SystemConfigRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY);
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
            public void onFailure(@NonNull Call<SystemConfigRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getTasks(String baseUrl, String authorization, String searchValue, auto.qinglong.net.panel.ApiController.TaskListCallBack callback) {
        Call<TasksRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getTasks(authorization, searchValue);

        call.enqueue(new Callback<TasksRes>() {
            @Override
            public void onResponse(@NonNull Call<TasksRes> call, @NonNull Response<TasksRes> response) {
                TasksRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(Converter.convertTasks(res.getData()));
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TasksRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getEnvironments(@NonNull String baseUrl, @NonNull String authorization, @NonNull String searchValue, auto.qinglong.net.panel.ApiController.EnvironmentListCallBack callBack) {
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

    public static void moveEnvironment(@NonNull String requestId, @NonNull String id, int from, int to, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fromIndex", from);
        jsonObject.addProperty("toIndex", to);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(PanelPreference.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .moveEnv(PanelPreference.getAuthorization(), id, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                NetManager.finishCall(requestId);
                LogUnit.log(call.request().url().toString());
                BaseRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void getLogFiles(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.FileListCallBack callBack) {
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
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess(Converter.convertLogFiles(res.getDirs()));
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LogFilesRes> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static String getLogFilePath(String scriptKey, String fileName, String fileParent) {
        String path;
        if (TextUnit.isFull(scriptKey)) {
            path = "api/crons/" + scriptKey + "/log";
        } else if (TextUnit.isFull(fileParent)) {
            path = "api/logs/" + fileParent + "/" + fileName;
        } else {
            path = "api/logs/" + scriptKey;
        }
        return path;
    }

    public static void getScriptFiles(@NonNull String baseUrl, @NonNull String authorization, auto.qinglong.net.panel.ApiController.FileListCallBack callBack) {
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
            public void onFailure(Call<ScriptFilesRes> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void createScript(@NonNull String requestId, @NonNull String fileName, @Nullable String path, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filename", fileName);
        jsonObject.addProperty("path", path == null ? "" : path);
        jsonObject.addProperty("content", "");

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(PanelPreference.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .createScript(PanelPreference.getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                NetManager.finishCall(requestId);
                BaseRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
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
            public void onResponse(@NonNull Call<DependenciesRes> call, @NonNull Response<DependenciesRes> response) {
                DependenciesRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess(Converter.convertDependencies(res.getData()));
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DependenciesRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
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
            public void onResponse(@NonNull Call<SystemConfigRes> call, @NonNull Response<SystemConfigRes> response) {
                SystemConfigRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess(Converter.convertSystemConfig(res.getData()));
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SystemConfigRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void updateSystemConfig(@NonNull String baseUrl, @NonNull String authorization, SystemConfig config, @NonNull auto.qinglong.net.panel.ApiController.BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("frequency", config.getLogRemoveFrequency());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateSystemConfig(authorization, requestBody);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (auto.qinglong.net.panel.ApiController.checkResponse(response.code(), res, callBack)) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                auto.qinglong.net.panel.ApiController.handleRequestError(call, t, callBack);
            }
        });

    }

    public static void updateUser(@NonNull String requestId, Account account, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(PanelPreference.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateUserInfo(PanelPreference.getAuthorization(), requestBody);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                NetManager.finishCall(requestId);
                BaseRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public interface NetBaseCallback {
        void onSuccess();

        void onFailure(String msg);
    }

}
