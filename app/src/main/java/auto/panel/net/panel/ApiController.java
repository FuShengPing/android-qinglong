package auto.panel.net.panel;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import auto.panel.bean.panel.PanelAccount;
import auto.panel.bean.panel.PanelDependence;
import auto.panel.bean.panel.PanelEnvironment;
import auto.panel.bean.panel.PanelFile;
import auto.panel.bean.panel.PanelLoginLog;
import auto.panel.bean.panel.PanelSystemConfig;
import auto.panel.bean.panel.PanelSystemInfo;
import auto.panel.bean.panel.PanelTask;
import auto.panel.net.RetrofitFactory;
import auto.panel.net.panel.v15.SystemConfigRes;
import auto.panel.utils.TextUnit;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class ApiController {

    public static void getSystemInfo(@NonNull String baseUrl, @NonNull SystemInfoCallBack callBack) {
        Call<SystemInfoRes> call = RetrofitFactory.build(Api.class, baseUrl).getSystemInfo();

        call.enqueue(new Callback<SystemInfoRes>() {
            @Override
            public void onResponse(@NonNull Call<SystemInfoRes> call, @NonNull Response<SystemInfoRes> response) {
                SystemInfoRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                PanelSystemInfo system = new PanelSystemInfo();
                system.setInitialized(res.getData().isInitialized());
                system.setVersion(res.getData().getVersion());
                callBack.onSuccess(system);
            }

            @Override
            public void onFailure(@NonNull Call<SystemInfoRes> call, @NonNull Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void initAccount(@NonNull String baseUrl, @NonNull PanelAccount account, @NonNull BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = RetrofitFactory.build(Api.class, baseUrl).initAccount(requestBody);

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

    public static void login(@NonNull String baseUrl, @NonNull PanelAccount account, @NonNull LoginCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());
        if (account.getCode() != null) {
            jsonObject.addProperty("code", account.getCode());
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<LoginRes> call;

        if (account.getCode() == null) {
            call = RetrofitFactory.build(Api.class, baseUrl).login(requestBody);
        } else {
            call = RetrofitFactory.build(Api.class, baseUrl).twoFactorLogin(requestBody);
        }

        call.enqueue(new Callback<LoginRes>() {
            @Override
            public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                LoginRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(res.getData().getToken());
            }

            @Override
            public void onFailure(Call<LoginRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void checkAccountToken(@NonNull String baseUrl, @NonNull String token, @NonNull BaseCallBack callBack) {
        Call<SystemConfigRes> call = RetrofitFactory.build(Api.class, baseUrl).checkToken(token);

        call.enqueue(new Callback<SystemConfigRes>() {
            @Override
            public void onResponse(Call<SystemConfigRes> call, Response<SystemConfigRes> response) {
                SystemConfigRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Call<SystemConfigRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void asyncLogin(@NonNull PanelAccount account, @NonNull AsyncLoginCallback callBack) {
        try {
            boolean flag = false;
            //系统信息
            Call<SystemInfoRes> call = RetrofitFactory.build(Api.class, account.getBaseUrl()).getSystemInfo();
            Response<SystemInfoRes> r1 = call.execute();
            PanelSystemInfo system = new PanelSystemInfo();
            system.setInitialized(r1.body().getData().isInitialized());
            system.setVersion(r1.body().getData().getVersion());
            flag = callBack.onSystemInfo(system);
            if (!flag) {
                return;
            }

            //登录


        } catch (Exception e) {
            e.printStackTrace();
            callBack.onFailure(e.toString());
        }
    }

    public static void getTasks(String searchValue, int pageNo, int pageSize, TaskListCallBack callback) {
        auto.panel.net.panel.v15.ApiController.getTasks(searchValue, pageNo, pageSize, callback);
    }

    public static void runTasks(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).runTasks(body);

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

    public static void stopTasks(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).stopTasks(body);

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

    public static void enableTasks(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).enableTasks(body);

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

    public static void disableTasks(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).disableTasks(body);

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

    public static void pinTasks(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).pinTasks(body);

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

    public static void unpinTasks(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).unpinTasks(body);

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

    public static void addTask(PanelTask task, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).addTask(body);

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

    public static boolean addTaskSync(PanelTask task) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).addTask(body);

        try {
            Response<BaseRes> res = call.execute();
            return res.body() != null && res.body().getCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static void updateTask(PanelTask task, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        if (task.getKey() instanceof String) {
            jsonObject.addProperty("_id", (String) task.getKey());
        } else {
            jsonObject.addProperty("id", (Integer) task.getKey());
            jsonObject.add("labels", new JsonArray());
        }
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).updateTask(body);

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

    public static void deleteTasks(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).deleteTasks(body);

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

    public static void getEnvironments(@NonNull String searchValue, EnvironmentListCallBack callBack) {
        auto.panel.net.panel.v15.ApiController.getEnvironments(searchValue, callBack);
    }

    public static void enableEnvironments(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).enableEnvironments(body);

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

    public static void disableEnvironments(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).disableEnvironments(body);

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

    public static void addEnvironments(List<PanelEnvironment> environments, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        for (PanelEnvironment environment : environments) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("name", environment.getName());
            jsonObject.addProperty("remarks", environment.getRemark());
            jsonObject.addProperty("value", environment.getValue());
            jsonArray.add(jsonObject);
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).addEnvironments(body);

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

    public static boolean addEnvironmentSync(PanelEnvironment environment) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        jsonObject = new JsonObject();
        jsonObject.addProperty("name", environment.getName());
        jsonObject.addProperty("remarks", environment.getRemark());
        jsonObject.addProperty("value", environment.getValue());
        jsonArray.add(jsonObject);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).addEnvironments(body);

        try {
            Response<BaseRes> res = call.execute();
            return res.code() == 200 && res.body() != null && res.body().getCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static void updateEnvironment(PanelEnvironment environment, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        if (environment.getKey() instanceof Integer) {
            jsonObject.addProperty("id", (Integer) environment.getKey());
        } else {
            jsonObject.addProperty("_id", (String) environment.getKey());
        }
        jsonObject.addProperty("name", environment.getName());
        jsonObject.addProperty("remarks", environment.getRemark());
        jsonObject.addProperty("value", environment.getValue());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).updateEnvironment(body);

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

    public static void deleteEnvironments(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).deleteEnvironments(body);

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

    public static void getConfigFileContent(ContentCallBack callBack) {
        String path = "api/configs/config.sh";
        getFileContent(path, callBack);
    }

    public static void updateConfigFileContent(String content, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("name", "config.sh");

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).updateConfigContent(body);

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

    public static void getDependencies(String searchValue, String type, DependenceListCallBack callBack) {
        auto.panel.net.panel.v15.ApiController.getDependencies(searchValue, type, callBack);
    }

    public static void addDependencies(List<PanelDependence> dependencies, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (PanelDependence dependence : dependencies) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", dependence.getTitle());
            jsonObject.addProperty("type", dependence.getTypeCode());
            jsonArray.add(jsonObject);
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).addDependencies(body);

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

    public static void reinstallDependencies(List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).reinstallDependencies(body);

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

    public static void deleteDependencies(List<Object> keys, BaseCallBack callBack) {
        auto.panel.net.panel.v15.ApiController.deleteDependencies(keys, callBack);
    }

    public static void getDependenceLogContent(Object key, ContentCallBack callBack) {
        String path = "api/dependencies/" + key;

        Call<DependenceLogRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getDependenceLog(path);

        call.enqueue(new Callback<DependenceLogRes>() {
            @Override
            public void onResponse(@NonNull Call<DependenceLogRes> call, @NonNull Response<DependenceLogRes> response) {
                DependenceLogRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                StringBuilder content = new StringBuilder();
                for (String line : res.getData().getLog()) {
                    content.append(line).append("\n");
                }
                callBack.onSuccess(content.toString());
            }

            @Override
            public void onFailure(@NonNull Call<DependenceLogRes> call, @NonNull Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getScripts(FileListCallBack callBack) {
        auto.panel.net.panel.v15.ApiController.getScripts(callBack);
    }

    public static void addScript(@NonNull PanelFile file, BaseCallBack callBack) {
        auto.panel.net.panel.v15.ApiController.addScript(file, callBack);
    }

    public static void deleteScript(PanelFile file, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filename", file.getTitle());
        jsonObject.addProperty("path", file.getParentPath());
        if (file.isDir()) {
            jsonObject.addProperty("type", "directory");
        } else {
            jsonObject.addProperty("type", "file");
        }

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).deleteScript(body);

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

    public static void getScriptContent(String fileName, String fileParent, ContentCallBack callBack) {
        String path = "api/scripts/" + fileName + "?path=" + (TextUnit.isFull(fileParent) ? fileParent : "");
        getFileContent(path, callBack);
    }

    public static String getScriptContentSync(String fileName, String fileParent) {
        String path = "api/scripts/" + fileName + "?path=" + (TextUnit.isFull(fileParent) ? fileParent : "");

        Call<FileContentRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getFileContent(path);

        try {
            Response<FileContentRes> res = call.execute();
            if (res.code() == 200 && res.body() != null && res.body().getCode() == 200) {
                return res.body().getData();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static void updateScriptContent(String fileName, String fileParent, String content, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("filename", fileName);
        jsonObject.addProperty("path", fileParent == null ? "" : fileParent);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).updateScript(body);

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

    public static void getLogs(FileListCallBack callBack) {
        auto.panel.net.panel.v15.ApiController.getLogs(callBack);
    }

    public static void getLogContent(String scriptKey, String fileName, String fileParent, ContentCallBack callBack) {
        String path;
        path = auto.panel.net.panel.v15.ApiController.getLogFilePath(scriptKey, fileName, fileParent);
        getFileContent(path, callBack);
    }

    public static void getLoginLogs(LoginLogListCallBack callBack) {
        Call<LoginLogsRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getLoginLogs();

        call.enqueue(new Callback<LoginLogsRes>() {
            @Override
            public void onResponse(Call<LoginLogsRes> call, Response<LoginLogsRes> response) {
                LoginLogsRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertLoginLogs(res.getData()));
            }

            @Override
            public void onFailure(Call<LoginLogsRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getSystemConfig(SystemConfigCallBack callBack) {

        Call<SystemConfigRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getSystemConfig();

        call.enqueue(new Callback<SystemConfigRes>() {
            @Override
            public void onResponse(Call<SystemConfigRes> call, Response<SystemConfigRes> response) {
                SystemConfigRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(auto.panel.net.panel.v15.Converter.convertSystemConfig(res.getData().getInfo()));
            }

            @Override
            public void onFailure(Call<SystemConfigRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void updateSystemConfig(PanelSystemConfig config, BaseCallBack callBack) {
        auto.panel.net.panel.v15.ApiController.updateSystemConfig(config, callBack);
    }

    public static void updateAccount(PanelAccount account, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = RetrofitFactory.buildWithAuthorization(Api.class).updateAccount(requestBody);

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

    private static void getFileContent(String path, ContentCallBack callBack) {
        Call<FileContentRes> call = RetrofitFactory.buildWithAuthorization(Api.class).getFileContent(path);

        call.enqueue(new Callback<FileContentRes>() {
            @Override
            public void onResponse(Call<FileContentRes> call, Response<FileContentRes> response) {
                FileContentRes res = response.body();
                if (NetHandler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(res.getData());
            }

            @Override
            public void onFailure(Call<FileContentRes> call, Throwable t) {
                NetHandler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static RequestBody buildArrayJson(List<Object> objects) {
        JsonArray jsonArray = new JsonArray();
        for (Object object : objects) {
            if (object instanceof String) {
                jsonArray.add((String) object);
            } else {
                jsonArray.add((Integer) object);
            }
        }
        return RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());
    }

    public interface BaseCallBack {
        default void onSuccess() {
        }

        default void onFailure(String msg) {
        }
    }

    public interface SystemInfoCallBack extends BaseCallBack {
        void onSuccess(PanelSystemInfo system);
    }

    public interface LoginCallBack extends BaseCallBack {
        void onSuccess(String token);
    }

    public interface AsyncLoginCallback extends BaseCallBack {
        boolean onSystemInfo(PanelSystemInfo system);

        boolean onLogin(String token);
    }

    public interface TaskListCallBack extends BaseCallBack {
        void onSuccess(List<PanelTask> tasks);
    }

    public interface EnvironmentListCallBack extends BaseCallBack {
        void onSuccess(List<PanelEnvironment> environments);
    }

    public interface DependenceListCallBack extends BaseCallBack {
        void onSuccess(List<PanelDependence> dependencies);
    }

    public interface FileListCallBack extends BaseCallBack {
        void onSuccess(List<PanelFile> files);
    }

    public interface LoginLogListCallBack extends BaseCallBack {
        void onSuccess(List<PanelLoginLog> loginLogs);
    }

    public interface ContentCallBack extends BaseCallBack {
        void onSuccess(String content);
    }

    public interface SystemConfigCallBack extends BaseCallBack {
        void onSuccess(PanelSystemConfig config);
    }
}


