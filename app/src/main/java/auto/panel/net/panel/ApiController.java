package auto.panel.net.panel;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import auto.base.util.TextUnit;
import auto.panel.bean.panel.Account;
import auto.panel.bean.panel.Dependence;
import auto.panel.bean.panel.Environment;
import auto.panel.bean.panel.File;
import auto.panel.bean.panel.LoginLog;
import auto.panel.bean.panel.SystemConfig;
import auto.panel.bean.panel.SystemInfo;
import auto.panel.bean.panel.Task;
import auto.panel.database.sp.PanelPreference;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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

    public static void getSystemInfo(@NonNull String baseUrl, @NonNull SystemInfoCallBack callBack) {
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
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                SystemInfo system = new SystemInfo();
                system.setInitialized(res.getData().isInitialized());
                system.setVersion(res.getData().getVersion());
                callBack.onSuccess(system);
            }

            @Override
            public void onFailure(@NonNull Call<SystemInfoRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void login(@NonNull String baseUrl, @NonNull Account account, LoginCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<LoginRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .login(requestBody);

        call.enqueue(new Callback<LoginRes>() {
            @Override
            public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                LoginRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(res.getData().getToken());
            }

            @Override
            public void onFailure(Call<LoginRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void checkAccountToken(@NonNull String baseUrl, @NonNull String authorization, BaseCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.checkAccountToken(baseUrl, authorization, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.checkAccountToken(baseUrl, authorization, callBack);
        }
    }

    public static void getTasks(@NonNull String baseUrl, @NonNull String authorization, String searchValue, TaskListCallBack callback) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.getTasks(baseUrl, authorization, searchValue, callback);
        } else {
            auto.panel.net.panel.v15.ApiController.getTasks(baseUrl, authorization, searchValue, callback);
        }
    }

    public static void runTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .runTasks(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void stopTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .stopTasks(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void enableTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .enableTasks(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void disableTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .disableTasks(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void pinTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .pinTasks(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void unpinTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .unpinTasks(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void addTask(@NonNull String baseUrl, @NonNull String authorization, Task task, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addTask(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static boolean addTaskSync(@NonNull String baseUrl, @NonNull String authorization, Task task) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addTask(authorization, body);

        try {
            Response<BaseRes> res = call.execute();
            return res.body() != null && res.body().getCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static void updateTask(@NonNull String baseUrl, @NonNull String authorization, Task task, BaseCallBack callBack) {
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
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateTask(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void deleteTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .deleteTasks(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getEnvironments(@NonNull String baseUrl, @NonNull String authorization, @NonNull String searchValue, EnvironmentListCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.getEnvironments(baseUrl, authorization, searchValue, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.getEnvironments(baseUrl, authorization, searchValue, callBack);
        }
    }

    public static void enableEnvironments(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .enableEnvironments(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void disableEnvironments(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .disableEnvironments(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void addEnvironments(@NonNull String baseUrl, @NonNull String authorization, List<Environment> environments, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        for (Environment environment : environments) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("name", environment.getName());
            jsonObject.addProperty("remarks", environment.getRemark());
            jsonObject.addProperty("value", environment.getValue());
            jsonArray.add(jsonObject);
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addEnvironments(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static boolean addEnvironmentSync(@NonNull String baseUrl, @NonNull String authorization, Environment environment) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        jsonObject = new JsonObject();
        jsonObject.addProperty("name", environment.getName());
        jsonObject.addProperty("remarks", environment.getRemark());
        jsonObject.addProperty("value", environment.getValue());
        jsonArray.add(jsonObject);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addEnvironments(authorization, body);

        try {
            Response<BaseRes> res = call.execute();
            return res.body() != null && res.body().getCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static void updateEnvironment(@NonNull String baseUrl, @NonNull String authorization, Environment environment, BaseCallBack callBack) {
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

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateEnvironment(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void deleteEnvironments(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .deleteEnvironments(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getConfigContent(@NonNull String baseUrl, @NonNull String authorization, ContentCallBack callBack) {
        String path = "api/configs/config.sh";
        getFileContent(baseUrl, authorization, path, callBack);
    }

    public static void saveConfigFileContent(@NonNull String baseUrl, @NonNull String authorization, String content, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("name", "config.sh");

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateConfigContent(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getDependencies(@NonNull String baseUrl, @NonNull String authorization, String searchValue, String type, DependenceListCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.getDependencies(baseUrl, authorization, searchValue, type, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.getDependencies(baseUrl, authorization, searchValue, type, callBack);
        }
    }

    public static void addDependencies(@NonNull String baseUrl, @NonNull String authorization, List<Dependence> dependencies, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (auto.panel.bean.panel.Dependence dependence : dependencies) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", dependence.getTitle());
            jsonObject.addProperty("type", dependence.getTypeCode());
            jsonArray.add(jsonObject);
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addDependencies(authorization, body);

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

    public static void reinstallDependencies(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        RequestBody body = buildArrayJson(keys);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .reinstallDependencies(authorization, body);

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

    public static void deleteDependencies(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.deleteDependencies(baseUrl, authorization, keys, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.deleteDependencies(baseUrl, authorization, keys, callBack);
        }
    }

    public static void getDependenceLogContent(@NonNull String baseUrl, @NonNull String authorization, Object key, ContentCallBack callBack) {
        String path = "api/dependencies/" + key;

        Call<DependenceLogRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getDependenceLog(path, authorization);

        call.enqueue(new Callback<DependenceLogRes>() {
            @Override
            public void onResponse(@NonNull Call<DependenceLogRes> call, @NonNull Response<DependenceLogRes> response) {
                DependenceLogRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
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
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getScripts(@NonNull String baseUrl, @NonNull String authorization, FileListCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.getScriptFiles(baseUrl, authorization, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.getScripts(baseUrl, authorization, callBack);
        }
    }

    public static void deleteScript(@NonNull String baseUrl, @NonNull String authorization, File file, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filename", file.getTitle());
        jsonObject.addProperty("path", file.getPath());
        if (file.isDir()) {
            jsonObject.addProperty("type", "directory");
        } else {
            jsonObject.addProperty("type", "file");
        }

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .deleteScript(authorization, body);

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

    public static void getScriptContent(@NonNull String baseUrl, @NonNull String authorization, String fileName, String fileParent, ContentCallBack callBack) {
        String path = "api/scripts/" + fileName + "?path=" + (TextUnit.isFull(fileParent) ? fileParent : "");
        getFileContent(baseUrl, authorization, path, callBack);
    }

    public static void saveScriptContent(@NonNull String baseUrl, @NonNull String authorization, String fileName, String fileParent, String content, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("filename", fileName);
        jsonObject.addProperty("path", fileParent == null ? "" : fileParent);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateScript(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess();
            }

            @Override
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getLogs(@NonNull String baseUrl, @NonNull String authorization, FileListCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.getLogFiles(baseUrl, authorization, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.getLogs(baseUrl, authorization, callBack);
        }
    }

    public static void getLogContent(@NonNull String baseUrl, @NonNull String authorization, String scriptKey, String fileName, String fileParent, ContentCallBack callBack) {
        String path;
        if (PanelPreference.isLowVersion()) {
            path = auto.panel.net.panel.v10.ApiController.getLogFilePath(scriptKey, fileName, fileParent);
        } else {
            path = auto.panel.net.panel.v15.ApiController.getLogFilePath(scriptKey, fileName, fileParent);
        }
        getFileContent(baseUrl, authorization, path, callBack);
    }

    public static void getLoginLogs(@NonNull String baseUrl, @NonNull String authorization, LoginLogListCallBack callBack) {
        Call<LoginLogsRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getLoginLogs(authorization);

        call.enqueue(new Callback<LoginLogsRes>() {
            @Override
            public void onResponse(Call<LoginLogsRes> call, Response<LoginLogsRes> response) {
                LoginLogsRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(Converter.convertLoginLogs(res.getData()));
            }

            @Override
            public void onFailure(Call<LoginLogsRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
            }
        });
    }

    public static void getSystemConfig(@NonNull String baseUrl, @NonNull String authorization, SystemConfigCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.getSystemConfig(baseUrl, authorization, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.getSystemConfig(baseUrl, authorization, callBack);
        }
    }

    public static void updateSystemConfig(@NonNull String baseUrl, @NonNull String authorization, SystemConfig config, BaseCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.panel.net.panel.v10.ApiController.updateSystemConfig(baseUrl, authorization, config, callBack);
        } else {
            auto.panel.net.panel.v15.ApiController.updateSystemConfig(baseUrl, authorization, config, callBack);
        }
    }

    public static void updateAccount(@NonNull String baseUrl, @NonNull String authorization, Account account, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .updateAccount(authorization, requestBody);

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

    private static void getFileContent(@NonNull String baseUrl, @NonNull String authorization, String path, ContentCallBack callBack) {
        Call<FileContentRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getFileContent(path, authorization);

        call.enqueue(new Callback<FileContentRes>() {
            @Override
            public void onResponse(Call<FileContentRes> call, Response<FileContentRes> response) {
                FileContentRes res = response.body();
                if (Handler.handleResponse(response.code(), res, callBack)) {
                    return;
                }
                callBack.onSuccess(res.getData());
            }

            @Override
            public void onFailure(Call<FileContentRes> call, Throwable t) {
                Handler.handleRequestError(call, t, callBack);
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

    public interface SystemInfoCallBack extends BaseCallBack {
        void onSuccess(SystemInfo system);
    }

    public interface LoginCallBack extends BaseCallBack {
        void onSuccess(String token);
    }

    public interface TaskListCallBack extends BaseCallBack {
        void onSuccess(List<Task> tasks);
    }

    public interface EnvironmentListCallBack extends BaseCallBack {
        void onSuccess(List<Environment> environments);
    }

    public interface DependenceListCallBack extends BaseCallBack {
        void onSuccess(List<Dependence> dependencies);
    }

    public interface FileListCallBack extends BaseCallBack {
        void onSuccess(List<File> files);
    }

    public interface LoginLogListCallBack extends BaseCallBack {
        void onSuccess(List<LoginLog> loginLogs);
    }

    public interface ContentCallBack extends BaseCallBack {
        void onSuccess(String content);
    }

    public interface SystemConfigCallBack extends BaseCallBack {
        void onSuccess(SystemConfig config);
    }

    public interface BaseCallBack {
        default void onSuccess() {
        }

        void onFailure(String msg);
    }
}


