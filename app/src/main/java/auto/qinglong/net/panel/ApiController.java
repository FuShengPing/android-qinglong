package auto.qinglong.net.panel;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import auto.base.util.TextUnit;
import auto.qinglong.bean.panel.Account;
import auto.qinglong.bean.panel.File;
import auto.qinglong.bean.panel.SystemInfo;
import auto.qinglong.bean.views.Task;
import auto.qinglong.database.sp.PanelPreference;
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
    private static final String ERROR_NO_BODY = "响应异常";
    private static final String ERROR_INVALID_AUTH = "登录信息失效";

    public static void getSystemInfo(@NonNull String baseUrl, @NonNull SystemCallBack callBack) {
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

    public static void login(@NonNull String baseUrl, @NonNull Account account, LoginCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());

        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
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
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess(res.getData().getToken());
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginRes> call, Throwable t) {
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

    public static void getTasks(@NonNull String baseUrl, @NonNull String authorization, String searchValue, TaskListCallBack callback) {
        if (PanelPreference.isLowVersion()) {
            auto.qinglong.net.panel.v10.ApiController.getTasks(baseUrl, authorization, searchValue, callback);
        } else {
            auto.qinglong.net.panel.v15.ApiController.getTasks(baseUrl, authorization, searchValue, callback);
        }

    }

    public static void runTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) instanceof String) {
                jsonArray.add((String) keys.get(i));
            } else {
                jsonArray.add((Integer) keys.get(i));
            }
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void stopTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) instanceof String) {
                jsonArray.add((String) keys.get(i));
            } else {
                jsonArray.add((Integer) keys.get(i));
            }
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void enableTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) instanceof String) {
                jsonArray.add((String) keys.get(i));
            } else {
                jsonArray.add((Integer) keys.get(i));
            }
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void disableTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) instanceof String) {
                jsonArray.add((String) keys.get(i));
            } else {
                jsonArray.add((Integer) keys.get(i));
            }
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void pinTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) instanceof String) {
                jsonArray.add((String) keys.get(i));
            } else {
                jsonArray.add((Integer) keys.get(i));
            }
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void unpinTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) instanceof String) {
                jsonArray.add((String) keys.get(i));
            } else {
                jsonArray.add((Integer) keys.get(i));
            }
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void deleteTasks(@NonNull String baseUrl, @NonNull String authorization, List<Object> keys, BaseCallBack callBack) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) instanceof String) {
                jsonArray.add((String) keys.get(i));
            } else {
                jsonArray.add((Integer) keys.get(i));
            }
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void updateTask(@NonNull String baseUrl, @NonNull String authorization, Task task, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        if (task.getKey() instanceof String) {
            jsonObject.addProperty("_id", (String) task.getKey());
        } else {
            jsonObject.addProperty("id", (Integer) task.getKey());
            jsonObject.add("labels", new JsonArray());
        }
        jsonObject.addProperty("name", task.getTitle());
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void createTask(@NonNull String baseUrl, @NonNull String authorization, Task task, BaseCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getTitle());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .createTask(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getLogFiles(@NonNull String baseUrl, @NonNull String authorization, FileListCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.qinglong.net.panel.v10.ApiController.getLogFiles(baseUrl, authorization, callBack);
        } else {
            auto.qinglong.net.panel.v15.ApiController.getLogFiles(baseUrl, authorization, callBack);
        }
    }

    public static void getLogFileContent(@NonNull String baseUrl, @NonNull String authorization, String scriptKey, String fileName, String fileParent, ContentCallBack callBack) {
        String path = auto.qinglong.net.panel.v10.ApiController.getLogFilePath(scriptKey, fileName, fileParent);

        getFileContent(baseUrl, authorization, path, callBack);
    }

    public static void getConfigFileContent(@NonNull String baseUrl, @NonNull String authorization, ContentCallBack callBack) {
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
                .updateConfig(authorization, body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(@NonNull Call<BaseRes> call, @NonNull Response<BaseRes> response) {
                BaseRes res = response.body();
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getScriptFiles(@NonNull String baseUrl, @NonNull String authorization, FileListCallBack callBack) {
        if (PanelPreference.isLowVersion()) {
            auto.qinglong.net.panel.v10.ApiController.getScriptFiles(baseUrl, authorization, callBack);
        } else {
            auto.qinglong.net.panel.v15.ApiController.getScriptFiles(baseUrl, authorization, callBack);
        }

    }

    public static void deleteScriptFile(@NonNull String baseUrl, @NonNull String authorization, File file, BaseCallBack callBack) {
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
            public void onFailure(Call<BaseRes> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getScriptFileContent(@NonNull String baseUrl, @NonNull String authorization, String fileName, String fileParent, ContentCallBack callBack) {
        String path = "api/scripts/" + fileName + "?path=" + (TextUnit.isFull(fileParent) ? fileParent : "");
        getFileContent(baseUrl, authorization, path, callBack);
    }

    public static void saveScriptFileContent(@NonNull String baseUrl, @NonNull String authorization, String fileName, String fileParent, String content, BaseCallBack callBack) {
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
            public void onFailure(@NonNull Call<BaseRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
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
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        StringBuilder content = new StringBuilder();
                        for (String line : res.getData().getLog()) {
                            content.append(line).append("\n");
                        }
                        callBack.onSuccess(content.toString());
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DependenceLogRes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
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
                if (res == null) {
                    if (response.code() == 401) {
                        callBack.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callBack.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        callBack.onSuccess(res.getData());
                    } else {
                        callBack.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<FileContentRes> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public interface SystemCallBack {
        void onSuccess(SystemInfo system);

        void onFailure(String msg);
    }

    public interface LoginCallBack {
        void onSuccess(String token);

        void onFailure(String msg);
    }

    public interface TaskListCallBack {
        void onSuccess(List<Task> tasks);

        void onFailure(String msg);
    }

    public interface FileListCallBack {
        void onSuccess(List<File> files);

        void onFailure(String msg);
    }

    public interface ContentCallBack {
        void onSuccess(String content);

        void onFailure(String msg);
    }

    public interface BaseCallBack {
        void onSuccess();

        void onFailure(String msg);
    }
}


