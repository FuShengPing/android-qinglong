package auto.panel.net.panel.v10;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

import auto.panel.bean.panel.PanelFile;
import auto.panel.bean.panel.PanelSystemConfig;
import auto.panel.net.NetManager;
import auto.panel.net.RetrofitFactory;
import auto.panel.net.panel.BaseRes;
import auto.panel.net.panel.NetHandler;
import auto.panel.utils.TextUnit;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * QL API统一请求类
 */
public class ApiController {
    private static final String ERROR_NO_BODY = "响应异常";
    private static final String ERROR_INVALID_AUTH = "登录信息失效";

    public static void checkAccountToken(auto.panel.net.panel.ApiController.BaseCallBack callBack) {
        Call<SystemConfigRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getSystemConfig();

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

    public static void getTasks(String searchValue, auto.panel.net.panel.ApiController.TaskListCallBack callBack) {
        Call<TasksRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getTasks(searchValue);

        call.enqueue(new Callback<TasksRes>() {
            @Override
            public void onResponse(@NonNull Call<TasksRes> call, @NonNull Response<TasksRes> response) {
                TasksRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertTasks(res.getData()));
            }

            @Override
            public void onFailure(@NonNull Call<TasksRes> call, @NonNull Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getEnvironments(@NonNull String searchValue, auto.panel.net.panel.ApiController.EnvironmentListCallBack callBack) {
        Call<EnvironmentsRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getEnvironments(searchValue);

        call.enqueue(new Callback<EnvironmentsRes>() {
            @Override
            public void onResponse(Call<EnvironmentsRes> call, Response<EnvironmentsRes> response) {
                EnvironmentsRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertEnvironments(res.getData()));
            }

            @Override
            public void onFailure(Call<EnvironmentsRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void moveEnvironment(@NonNull String requestId, @NonNull String id, int from, int to, @NonNull NetBaseCallback callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fromIndex", from);
        jsonObject.addProperty("toIndex", to);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).moveEnvironment(id, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {

            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void getDependencies(String searchValue, String type, auto.panel.net.panel.ApiController.DependenceListCallBack callBack) {
        Call<DependenciesRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getDependencies(searchValue, type);

        call.enqueue(new Callback<DependenciesRes>() {
            @Override
            public void onResponse(@NonNull Call<DependenciesRes> call, @NonNull Response<DependenciesRes> response) {
                DependenciesRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertDependencies(res.getData()));
            }

            @Override
            public void onFailure(@NonNull Call<DependenciesRes> call, @NonNull Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });

    }

    public static void deleteDependencies(List<Object> keys, auto.panel.net.panel.ApiController.BaseCallBack callBack) {
        RequestBody body = auto.panel.net.panel.ApiController.buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).deleteDependencies(body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getLogFiles(auto.panel.net.panel.ApiController.FileListCallBack callBack) {
        Call<LogFilesRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getLogFiles();

        call.enqueue(new Callback<LogFilesRes>() {
            @Override
            public void onResponse(Call<LogFilesRes> call, Response<LogFilesRes> response) {
                LogFilesRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertLogFiles(res.getDirs()));
            }

            @Override
            public void onFailure(Call<LogFilesRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getScriptFiles(auto.panel.net.panel.ApiController.FileListCallBack callBack) {
        Call<ScriptFilesRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getScriptFiles();

        call.enqueue(new Callback<ScriptFilesRes>() {
            @Override
            public void onResponse(Call<ScriptFilesRes> call, Response<ScriptFilesRes> response) {
                ScriptFilesRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertScriptFiles(res.getData()));
            }

            @Override
            public void onFailure(Call<ScriptFilesRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void addScript(@NonNull PanelFile file, auto.panel.net.panel.ApiController.BaseCallBack callBack) {
    }

    public static void getSystemConfig(auto.panel.net.panel.ApiController.SystemConfigCallBack callBack) {
        Call<SystemConfigRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getSystemConfig();

        call.enqueue(new Callback<SystemConfigRes>() {
            @Override
            public void onResponse(@NonNull Call<SystemConfigRes> call, @NonNull Response<SystemConfigRes> response) {
                SystemConfigRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertSystemConfig(res.getData()));
            }

            @Override
            public void onFailure(@NonNull Call<SystemConfigRes> call, @NonNull Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void updateSystemConfig(PanelSystemConfig config, @NonNull auto.panel.net.panel.ApiController.BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("frequency", config.getLogRemoveFrequency());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).updateSystemConfig(requestBody);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });

    }

    public static String getLogFilePath(String scriptKey, String fileName, String fileParent) {
        String path;
        if (TextUnit.isFull(scriptKey)) {//任务日志
            path = "api/crons/" + scriptKey + "/log";
        } else if (TextUnit.isFull(fileParent)) {//脚本日志
            path = "api/logs/" + fileParent + "/" + fileName;
        } else {
            path = "api/logs/" + fileName;
        }
        return path;
    }

    public interface NetBaseCallback {
        void onSuccess();

        void onFailure(String msg);
    }

}
