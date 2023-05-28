package auto.qinglong.network.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Objects;

import auto.qinglong.bean.app.Account;
import auto.qinglong.bean.ql.QLDependence;
import auto.qinglong.bean.ql.QLEnvironment;
import auto.qinglong.bean.ql.QLLog;
import auto.qinglong.bean.ql.QLLoginLog;
import auto.qinglong.bean.ql.QLScript;
import auto.qinglong.bean.ql.QLSystem;
import auto.qinglong.bean.ql.QLTask;
import auto.qinglong.bean.ql.network.QLBaseRes;
import auto.qinglong.bean.ql.network.QLDependenceRes;
import auto.qinglong.bean.ql.network.QLDependenciesRes;
import auto.qinglong.bean.ql.network.QLEnvEditRes;
import auto.qinglong.bean.ql.network.QLEnvironmentRes;
import auto.qinglong.bean.ql.network.QLLogRemoveRes;
import auto.qinglong.bean.ql.network.QLLoginLogsRes;
import auto.qinglong.bean.ql.network.QLLoginRes;
import auto.qinglong.bean.ql.network.QLLogsRes;
import auto.qinglong.bean.ql.network.QLScriptsRes;
import auto.qinglong.bean.ql.network.QLSimpleRes;
import auto.qinglong.bean.ql.network.QLSystemRes;
import auto.qinglong.bean.ql.network.QLTaskEditRes;
import auto.qinglong.bean.ql.network.QLTasksRes;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.utils.LogUnit;
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
public class QLApiController {
    private static final String ERROR_NO_BODY = "响应异常";
    private static final String ERROR_INVALID_AUTH = "登录失效";

    public static void getSystemInfo(@NonNull String requestId, @NonNull Account account, @NonNull NetSystemCallback callback) {
        Call<QLSystemRes> call = new Retrofit.Builder()
                .baseUrl(account.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getSystemInfo();

        call.enqueue(new Callback<QLSystemRes>() {
            @Override
            public void onResponse(@NonNull Call<QLSystemRes> call, @NonNull Response<QLSystemRes> response) {
                NetManager.finishCall(requestId);
                QLSystemRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLSystemRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void checkToken(@NonNull String requestId, @NonNull Account account, @NonNull NetBaseCallback callback) {
        Call<QLLogRemoveRes> call = new Retrofit.Builder()
                .baseUrl(account.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getLogRemove(account.getAuthorization());

        call.enqueue(new Callback<QLLogRemoveRes>() {
            @Override
            public void onResponse(@NonNull Call<QLLogRemoveRes> call, @NonNull Response<QLLogRemoveRes> response) {
                NetManager.finishCall(requestId);
                QLLogRemoveRes res = response.body();
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
            public void onFailure(@NonNull Call<QLLogRemoveRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void login(@NonNull String requestId, @NonNull Account account, @NonNull NetLoginCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<QLLoginRes> call = new Retrofit.Builder()
                .baseUrl(account.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .login(body);

        call.enqueue(new Callback<QLLoginRes>() {
            @Override
            public void onResponse(@NonNull Call<QLLoginRes> call, @NonNull Response<QLLoginRes> response) {
                NetManager.finishCall(requestId);
                QLLoginRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        //设置会话信息
                        account.setToken(res.getData().getToken());
                        callback.onSuccess(account);
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLLoginRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
        NetManager.addCall(call, requestId);
    }

    public static void getTasks(@NonNull String requestId, @Nullable String searchValue, @NonNull NetGetTasksCallback callback) {
        Call<QLTasksRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getTasks(AccountSP.getAuthorization(), searchValue);
        call.enqueue(new Callback<QLTasksRes>() {
            @Override
            public void onResponse(@NonNull Call<QLTasksRes> call, @NonNull Response<QLTasksRes> response) {
                NetManager.finishCall(requestId);
                QLTasksRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLTasksRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void runTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .runTasks(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void stopTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .stopTasks(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void enableTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .enableTasks(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void disableTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .disableTasks(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void pinTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .pinTasks(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void unpinTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .unpinTasks(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void deleteTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .deleteTasks(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void editTask(@NonNull String requestId, @NonNull QLTask QLTask, @NonNull NetEditTaskCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", QLTask.getName());
        jsonObject.addProperty("_id", QLTask.getId());
        jsonObject.addProperty("command", QLTask.getCommand());
        jsonObject.addProperty("schedule", QLTask.getSchedule());

        String json = jsonObject.toString();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLTaskEditRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .updateTask(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLTaskEditRes>() {
            @Override
            public void onResponse(@NonNull Call<QLTaskEditRes> call, @NonNull Response<QLTaskEditRes> response) {
                NetManager.finishCall(requestId);
                QLTaskEditRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLTaskEditRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void addTask(@NonNull String requestId, @NonNull QLTask QLTask, @NonNull NetEditTaskCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", QLTask.getName());
        jsonObject.addProperty("command", QLTask.getCommand());
        jsonObject.addProperty("schedule", QLTask.getSchedule());

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLTaskEditRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .addTask(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLTaskEditRes>() {
            @Override
            public void onResponse(@NonNull Call<QLTaskEditRes> call, @NonNull Response<QLTaskEditRes> response) {
                NetManager.finishCall(requestId);
                QLTaskEditRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY + response.code());
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLTaskEditRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getEnvironments(@NonNull String requestId, @NonNull String searchValue, @NonNull NetGetEnvironmentsCallback callback) {
        Call<QLEnvironmentRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getEnvironments(AccountSP.getAuthorization(), searchValue);
        call.enqueue(new Callback<QLEnvironmentRes>() {
            @Override
            public void onResponse(@NonNull Call<QLEnvironmentRes> call, @NonNull Response<QLEnvironmentRes> response) {
                NetManager.finishCall(requestId);
                QLEnvironmentRes environmentRes = response.body();
                if (environmentRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (environmentRes.getCode() == 200) {
                        callback.onSuccess(environmentRes.getData());
                    } else {
                        callback.onFailure(environmentRes.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLEnvironmentRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void addEnvironment(@NonNull String requestId, @NonNull List<QLEnvironment> environments, @NonNull NetGetEnvironmentsCallback callback) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        for (QLEnvironment environment : environments) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("name", environment.getName());
            jsonObject.addProperty("remarks", environment.getRemarks());
            jsonObject.addProperty("value", environment.getValue());
            jsonArray.add(jsonObject);
        }
        String json = jsonArray.toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLEnvironmentRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .addEnvironments(AccountSP.getAuthorization(), requestBody);

        call.enqueue(new Callback<QLEnvironmentRes>() {
            @Override
            public void onResponse(@NonNull Call<QLEnvironmentRes> call, @NonNull Response<QLEnvironmentRes> response) {
                NetManager.finishCall(requestId);
                QLEnvironmentRes environmentRes = response.body();
                if (environmentRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (environmentRes.getCode() == 200) {
                        callback.onSuccess(environmentRes.getData());
                    } else {
                        callback.onFailure(environmentRes.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLEnvironmentRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void updateEnvironment(@NonNull String requestId, @NonNull QLEnvironment environment, @NonNull NetEditEnvCallback callback) {
        JsonObject jsonObject;
        jsonObject = new JsonObject();
        jsonObject.addProperty("name", environment.getName());
        jsonObject.addProperty("remarks", environment.getRemarks());
        jsonObject.addProperty("value", environment.getValue());
        jsonObject.addProperty("_id", environment.getId());

        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLEnvEditRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .updateEnvironment(AccountSP.getAuthorization(), requestBody);

        call.enqueue(new Callback<QLEnvEditRes>() {
            @Override
            public void onResponse(@NonNull Call<QLEnvEditRes> call, @NonNull Response<QLEnvEditRes> response) {
                NetManager.finishCall(requestId);
                QLEnvEditRes editEnvRes = response.body();
                if (editEnvRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (editEnvRes.getCode() == 200) {
                        callback.onSuccess(editEnvRes.getData());
                    } else {
                        callback.onFailure(editEnvRes.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLEnvEditRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void deleteEnvironments(@NonNull String requestId, @NonNull List<String> envIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < envIds.size(); i++) {
            jsonArray.add(envIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .deleteEnvironments(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void enableEnvironments(@NonNull String requestId, @NonNull List<String> envIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < envIds.size(); i++) {
            jsonArray.add(envIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .enableEnv(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void disableEnvironments(@NonNull String requestId, @NonNull List<String> envIds, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < envIds.size(); i++) {
            jsonArray.add(envIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .disableEnv(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void moveEnvironment(@NonNull String requestId, @NonNull String id, int from, int to, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fromIndex", from);
        jsonObject.addProperty("toIndex", to);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .moveEnv(AccountSP.getAuthorization(), id, body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                LogUnit.log(call.request().url().toString());
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void getLogs(@NonNull String requestId, @NonNull NetGetLogsCallback callback) {
        Call<QLLogsRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getLogs(AccountSP.getAuthorization());
        call.enqueue(new Callback<QLLogsRes>() {
            @Override
            public void onResponse(@NonNull Call<QLLogsRes> call, @NonNull Response<QLLogsRes> response) {
                NetManager.finishCall(requestId);
                QLLogsRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getDirs());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLLogsRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getLogDetail(@NonNull String requestId, @NonNull String logPath, @NonNull NetSimpleCallBack callback) {
        Call<QLSimpleRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getLogDetail(logPath, AccountSP.getAuthorization());

        call.enqueue(new Callback<QLSimpleRes>() {
            @Override
            public void onResponse(@NonNull Call<QLSimpleRes> call, @NonNull Response<QLSimpleRes> response) {
                NetManager.finishCall(requestId);
                QLSimpleRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLSimpleRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
        NetManager.addCall(call, requestId);
    }

    public static void getConfigDetail(@NonNull String requestId, @NonNull NetConfigCallback callback) {
        Call<QLSimpleRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getConfig(AccountSP.getAuthorization());

        call.enqueue(new Callback<QLSimpleRes>() {
            @Override
            public void onResponse(@NonNull Call<QLSimpleRes> call, @NonNull Response<QLSimpleRes> response) {
                NetManager.finishCall(requestId);
                QLSimpleRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLSimpleRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
        NetManager.addCall(call, requestId);
    }

    public static void saveConfig(@NonNull String requestId, @NonNull String content, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("name", "config.sh");

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .updateConfig(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getScripts(@NonNull String requestId, @NonNull NetGetScriptsCallback callback) {
        Call<QLScriptsRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getScripts(AccountSP.getAuthorization());
        call.enqueue(new Callback<QLScriptsRes>() {
            @Override
            public void onResponse(@NonNull Call<QLScriptsRes> call, @NonNull Response<QLScriptsRes> response) {
                NetManager.finishCall(requestId);
                QLScriptsRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLScriptsRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getScriptDetail(@NonNull String requestId, @NonNull String scriptPath, @NonNull NetSimpleCallBack callback) {
        Call<QLSimpleRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getScriptDetail(scriptPath, AccountSP.getAuthorization());

        call.enqueue(new Callback<QLSimpleRes>() {
            @Override
            public void onResponse(@NonNull Call<QLSimpleRes> call, @NonNull Response<QLSimpleRes> response) {
                NetManager.finishCall(requestId);
                QLSimpleRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLSimpleRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
        NetManager.addCall(call, requestId);
    }

    public static void saveScript(@NonNull String requestId, @NonNull String content, @NonNull String filename, String path, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("filename", filename);
        jsonObject.addProperty("path", path == null ? "" : path);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .updateScript(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void createScript(@NonNull String requestId, @NonNull String fileName, @Nullable String path, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filename", fileName);
        jsonObject.addProperty("path", path == null ? "" : path);
        jsonObject.addProperty("content", "");

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .createScript(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void deleteScript(@NonNull String requestId, @NonNull String fileName, @Nullable String path, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filename", fileName);
        jsonObject.addProperty("path", path == null ? "" : path);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .deleteScript(AccountSP.getAuthorization(), body);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getDependencies(@NonNull String requestId, @Nullable String searchValue, String type, @NonNull NetGetDependenciesCallback callback) {
        Call<QLDependenciesRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getDependencies(AccountSP.getAuthorization(), searchValue, type);

        call.enqueue(new Callback<QLDependenciesRes>() {
            @Override
            public void onResponse(@NonNull Call<QLDependenciesRes> call, @NonNull Response<QLDependenciesRes> response) {
                NetManager.finishCall(requestId);
                QLDependenciesRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLDependenciesRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
        NetManager.addCall(call, requestId);

    }

    public static void addDependencies(@NonNull String requestId, @NonNull List<QLDependence> dependencies, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        for (QLDependence QLDependence : dependencies) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("name", QLDependence.getName());
            jsonObject.addProperty("type", QLDependence.getType());
            jsonArray.add(jsonObject);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .addDependencies(AccountSP.getAuthorization(), requestBody);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void deleteDependencies(@NonNull String requestId, @NonNull List<String> ids, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (String id : ids) {
            jsonArray.add(id);
        }
        String json = jsonArray.toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .deleteDependencies(AccountSP.getAuthorization(), requestBody);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getDependence(@NonNull String requestId, @NonNull String path, @NonNull NetGetDependenceCallback callback) {
        Call<QLDependenceRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getDependence(path, AccountSP.getAuthorization());

        call.enqueue(new Callback<QLDependenceRes>() {
            @Override
            public void onResponse(@NonNull Call<QLDependenceRes> call, @NonNull Response<QLDependenceRes> response) {
                NetManager.finishCall(requestId);
                QLDependenceRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLDependenceRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });
        NetManager.addCall(call, requestId);
    }

    public static void reinstallDependencies(@NonNull String requestId, @NonNull List<String> ids, @NonNull NetBaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (String id : ids) {
            jsonArray.add(id);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .reinstallDependencies(AccountSP.getAuthorization(), requestBody);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getLoginLogs(@NonNull String requestId, @NonNull NetGetLoginLogsCallback callback) {
        Call<QLLoginLogsRes> call = new Retrofit.Builder()
                .baseUrl(Objects.requireNonNull(AccountSP.getCurrentAccount()).getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getLoginLogs(AccountSP.getAuthorization());

        call.enqueue(new Callback<QLLoginLogsRes>() {
            @Override
            public void onResponse(@NonNull Call<QLLoginLogsRes> call, @NonNull Response<QLLoginLogsRes> response) {
                NetManager.finishCall(requestId);
                QLLoginLogsRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLLoginLogsRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getLogRemove(@NonNull String requestId, @NonNull NetGetLogRemoveCallback callback) {
        Call<QLLogRemoveRes> call = new Retrofit.Builder()
                .baseUrl(Objects.requireNonNull(AccountSP.getCurrentAccount()).getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .getLogRemove(AccountSP.getAuthorization());

        call.enqueue(new Callback<QLLogRemoveRes>() {
            @Override
            public void onResponse(@NonNull Call<QLLogRemoveRes> call, @NonNull Response<QLLogRemoveRes> response) {
                NetManager.finishCall(requestId);
                QLLogRemoveRes res = response.body();
                if (res == null) {
                    if (response.code() == 401) {
                        callback.onFailure(ERROR_INVALID_AUTH);
                    } else {
                        callback.onFailure(ERROR_NO_BODY);
                    }
                } else {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res.getData().getFrequency());
                    } else {
                        callback.onFailure(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QLLogRemoveRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void updateLogRemove(@NonNull String requestId, int frequency, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("frequency", frequency);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .updateLogRemove(AccountSP.getAuthorization(), requestBody);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);

    }

    public static void updateUser(@NonNull String requestId, Account account, @NonNull NetBaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<QLBaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QLApi.class)
                .updateUser(AccountSP.getAuthorization(), requestBody);

        call.enqueue(new Callback<QLBaseRes>() {
            @Override
            public void onResponse(@NonNull Call<QLBaseRes> call, @NonNull Response<QLBaseRes> response) {
                NetManager.finishCall(requestId);
                QLBaseRes res = response.body();
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
            public void onFailure(@NonNull Call<QLBaseRes> call, @NonNull Throwable t) {
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

    public interface NetSimpleCallBack {
        void onSuccess(String content);

        void onFailure(String msg);
    }

    public interface NetSystemCallback {
        void onSuccess(QLSystem system);

        void onFailure(String msg);

    }

    public interface NetConfigCallback {
        void onSuccess(String content);

        void onFailure(String msg);
    }

    public interface NetLoginCallback {
        void onSuccess(Account account);

        void onFailure(String msg);
    }

    public interface NetRunTaskCallback {
        void onSuccess(String msg);

        void onFailure(String msg);
    }

    public interface NetEditTaskCallback {
        void onSuccess(QLTask QLTask);

        void onFailure(String msg);
    }

    public interface NetGetTasksCallback {
        void onSuccess(List<QLTask> tasks);

        void onFailure(String msg);
    }

    public interface NetGetScriptsCallback {
        void onSuccess(List<QLScript> scripts);

        void onFailure(String msg);
    }

    public interface NetGetLogsCallback {
        void onSuccess(List<QLLog> logs);

        void onFailure(String msg);
    }

    public interface NetGetEnvironmentsCallback {
        void onSuccess(List<QLEnvironment> environments);

        void onFailure(String msg);
    }

    public interface NetGetDependenciesCallback {
        void onSuccess(List<QLDependence> dependencies);

        void onFailure(String msg);
    }

    public interface NetGetDependenceCallback {
        void onSuccess(QLDependence dependence);

        void onFailure(String msg);
    }

    public interface NetEditEnvCallback {
        void onSuccess(QLEnvironment environment);

        void onFailure(String msg);
    }

    public interface NetGetLoginLogsCallback {
        void onSuccess(List<QLLoginLog> logs);

        void onFailure(String msg);
    }

    public interface NetGetLogRemoveCallback {
        void onSuccess(int frequency);

        void onFailure(String msg);
    }
}
