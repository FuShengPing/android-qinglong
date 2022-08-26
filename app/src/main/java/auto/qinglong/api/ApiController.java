package auto.qinglong.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import auto.qinglong.api.object.Environment;
import auto.qinglong.api.object.Log;
import auto.qinglong.api.object.Script;
import auto.qinglong.api.object.Task;
import auto.qinglong.api.res.BaseRes;
import auto.qinglong.api.res.EditEnvRes;
import auto.qinglong.api.res.EditTaskRes;
import auto.qinglong.api.res.EnvRes;
import auto.qinglong.api.res.LogRes;
import auto.qinglong.api.res.LoginRes;
import auto.qinglong.api.res.ScriptRes;
import auto.qinglong.api.res.TasksRes;
import auto.qinglong.database.object.Account;
import auto.qinglong.database.sp.AccountSP;
import auto.qinglong.tools.LogUnit;
import auto.qinglong.tools.CallManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API统一请求类
 * 1.响应码 200-300 响应成功 GSON解析响应体
 * 2.响应码 >300 响应失败 GSON不解析响应体 body为null
 * 3.每个请求都返回请求对象call
 */
public class ApiController {
    public static String RES_NO_BODY = "响应异常";
    public static String RES_INVALID_AUTH = "无效会话";


    public static void checkToken(@NonNull String requestId, @NonNull Account account, @NonNull LoginCallback callback) {
        Call<TasksRes> call = new Retrofit.Builder()
                .baseUrl(account.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getTasks(account.getAuthorization(), "");

        call.enqueue(new Callback<TasksRes>() {
            @Override
            public void onResponse(Call<TasksRes> call, Response<TasksRes> response) {
                TasksRes tasksRes = response.body();
                if (tasksRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {

                    if (tasksRes.getCode() == 200) {
                        callback.onSuccess(account);
                    } else {
                        callback.onFailure(tasksRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<TasksRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void login(@NonNull String requestId, @NonNull Account account, @NonNull LoginCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", account.getUsername());
        jsonObject.addProperty("password", account.getPassword());
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<LoginRes> call = new Retrofit.Builder()
                .baseUrl(account.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .login(body);

        call.enqueue(new Callback<LoginRes>() {
            @Override
            public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    LoginRes loginRes = response.body();
                    if (loginRes.getCode() == 200) {
                        //设置会话信息
                        account.setToken(loginRes.getData().getToken());
                        callback.onSuccess(account);
                    } else {
                        callback.onFailure(loginRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<LoginRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });
        CallManager.addCall(call, requestId);
    }

    public static void getTasks(@NonNull String requestId, @NonNull String searchValue, @NonNull GetTasksCallback callback) {
        Call<TasksRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getTasks(AccountSP.getCurrentAccount().getAuthorization(), searchValue);
        call.enqueue(new Callback<TasksRes>() {
            @Override
            public void onResponse(Call<TasksRes> call, Response<TasksRes> response) {
                TasksRes tasksRes = response.body();
                if (tasksRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (tasksRes.getCode() == 200) {
                        callback.onSuccess(tasksRes);
                    } else {
                        callback.onFailure(tasksRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<TasksRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void runTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull RunTaskCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .runTasks(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    callback.onSuccess(response.body().getMessage());
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void stopTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull RunTaskCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .stopTasks(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getMessage());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void enableTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull RunTaskCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .enableTasks(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getMessage());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void disableTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull RunTaskCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .disableTasks(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getMessage());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void pinTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull RunTaskCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .pinTasks(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getMessage());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void unpinTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull RunTaskCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .unpinTasks(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getMessage());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void deleteTasks(@NonNull String requestId, @NonNull List<String> taskIds, @NonNull BaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < taskIds.size(); i++) {
            jsonArray.add(taskIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .deleteTasks(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getMessage());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void editTask(@NonNull String requestId, @NonNull Task task, @NonNull EditTaskCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("_id", task.get_id());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        String json = jsonObject.toString();
        LogUnit.log(json);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<EditTaskRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .editTask(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<EditTaskRes>() {
            @Override
            public void onResponse(Call<EditTaskRes> call, Response<EditTaskRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getData());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EditTaskRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void addTask(@NonNull String requestId, @NonNull Task task, @NonNull EditTaskCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<EditTaskRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .addTask(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<EditTaskRes>() {
            @Override
            public void onResponse(Call<EditTaskRes> call, Response<EditTaskRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(response.body().getData());
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EditTaskRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void getEnvs(@NonNull String requestId, @NonNull String searchValue, @NonNull GetEnvsCallback callback) {
        Call<EnvRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getEnvs(AccountSP.getCurrentAccount().getAuthorization(), searchValue);
        call.enqueue(new Callback<EnvRes>() {
            @Override
            public void onResponse(Call<EnvRes> call, Response<EnvRes> response) {
                EnvRes envRes = response.body();
                if (envRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (envRes.getCode() == 200) {
                        callback.onSuccess(envRes);
                    } else {
                        callback.onFailure(envRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EnvRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void addEnvs(@NonNull String requestId, @NonNull List<Environment> environments, @NonNull GetEnvsCallback callback) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        for (Environment environment : environments) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("name", environment.getName());
            jsonObject.addProperty("remarks", environment.getRemarks());
            jsonObject.addProperty("value", environment.getValue());
            jsonArray.add(jsonObject);
        }
        String json = jsonArray.toString();
        LogUnit.log(json);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<EnvRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .addEnvs(AccountSP.getCurrentAccount().getAuthorization(), requestBody);

        call.enqueue(new Callback<EnvRes>() {
            @Override
            public void onResponse(Call<EnvRes> call, Response<EnvRes> response) {
                EnvRes envRes = response.body();
                if (envRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (envRes.getCode() == 200) {
                        callback.onSuccess(envRes);
                    } else {
                        callback.onFailure(envRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EnvRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void updateEnv(@NonNull String requestId, @NonNull Environment environment, @NonNull EditEnvCallback callback) {
        JsonObject jsonObject;
        jsonObject = new JsonObject();
        jsonObject.addProperty("name", environment.getName());
        jsonObject.addProperty("remarks", environment.getRemarks());
        jsonObject.addProperty("value", environment.getValue());
        jsonObject.addProperty("_id", environment.get_id());

        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<EditEnvRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .updateEnv(AccountSP.getCurrentAccount().getAuthorization(), requestBody);

        call.enqueue(new Callback<EditEnvRes>() {
            @Override
            public void onResponse(Call<EditEnvRes> call, Response<EditEnvRes> response) {
                EditEnvRes editEnvRes = response.body();
                if (editEnvRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (editEnvRes.getCode() == 200) {
                        callback.onSuccess(editEnvRes.getData());
                    } else {
                        callback.onFailure(editEnvRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EditEnvRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void deleteEnvs(@NonNull String requestId, @NonNull List<String> envIds, @NonNull BaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < envIds.size(); i++) {
            jsonArray.add(envIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .deleteEnvs(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes baseRes = response.body();
                if (baseRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(baseRes.getMessage());
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void enableEnvs(@NonNull String requestId, @NonNull List<String> envIds, @NonNull BaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < envIds.size(); i++) {
            jsonArray.add(envIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .enableEnv(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes baseRes = response.body();
                if (baseRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(baseRes.getMessage());
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void disableEnvs(@NonNull String requestId, @NonNull List<String> envIds, @NonNull BaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < envIds.size(); i++) {
            jsonArray.add(envIds.get(i));
        }

        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .disableEnv(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes baseRes = response.body();
                if (baseRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {
                    if (response.body().getCode() == 200) {
                        callback.onSuccess(baseRes.getMessage());
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }

                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);

    }

    public static void getLogs(@NonNull String requestId, @NonNull GetLogsCallback callback) {
        Call<LogRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getLogs(AccountSP.getCurrentAccount().getAuthorization());
        call.enqueue(new Callback<LogRes>() {
            @Override
            public void onResponse(Call<LogRes> call, Response<LogRes> response) {
                LogRes logRes = response.body();
                if (logRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (logRes.getCode() == 200) {
                        callback.onSuccess(logRes.getDirs());
                    } else {
                        callback.onFailure(logRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<LogRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void getLogDetail(@NonNull String requestId, @NonNull String logPath, @NonNull BaseCallback callback) {
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getLogDetail(logPath, AccountSP.getCurrentAccount().getAuthorization());

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    callback.onSuccess(response.body().getData());
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });
        CallManager.addCall(call, requestId);
    }

    public static void getConfigDetail(@NonNull String requestId, @NonNull BaseCallback callback) {
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getConfig(AccountSP.getCurrentAccount().getAuthorization());

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    callback.onSuccess(response.body().getData());
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });
        CallManager.addCall(call, requestId);
    }

    public static void saveConfig(@NonNull String requestId, @NonNull String content, @NonNull BaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("name", "config.sh");

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .saveConfig(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    callback.onSuccess(response.body().getMessage());
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void getScripts(@NonNull String requestId, @NonNull GetScriptsCallback callback) {
        Call<ScriptRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getScripts(AccountSP.getCurrentAccount().getAuthorization());
        call.enqueue(new Callback<ScriptRes>() {
            @Override
            public void onResponse(Call<ScriptRes> call, Response<ScriptRes> response) {
                ScriptRes scriptRes = response.body();
                if (scriptRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (scriptRes.getCode() == 200) {
                        callback.onSuccess(scriptRes.getData());
                    } else {
                        callback.onFailure(scriptRes.getMessage());
                    }
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<ScriptRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public static void getScriptDetail(@NonNull String requestId, @NonNull String scriptPath, @NonNull BaseCallback callback) {
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .getScriptDetail(scriptPath, AccountSP.getCurrentAccount().getAuthorization());

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    callback.onSuccess(response.body().getData());
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });
        CallManager.addCall(call, requestId);
    }

    public static void saveScript(@NonNull String requestId, @NonNull String content, @NonNull String filename, String path, @NonNull BaseCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("filename", filename);
        jsonObject.addProperty("path", path == null ? "" : path);

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QL.class)
                .saveScript(AccountSP.getCurrentAccount().getAuthorization(), body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    callback.onSuccess(response.body().getMessage());
                }
                CallManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                CallManager.finishCall(requestId);
            }
        });

        CallManager.addCall(call, requestId);
    }

    public interface BaseCallback {
        void onSuccess(String msg);

        void onFailure(String msg);
    }

    public interface LoginCallback {
        void onSuccess(Account account);

        void onFailure(String msg);
    }

    public interface RunTaskCallback {
        void onSuccess(String msg);

        void onFailure(String msg);
    }

    public interface EditTaskCallback {
        void onSuccess(Task task);

        void onFailure(String msg);
    }

    public interface GetTasksCallback {
        void onSuccess(TasksRes data);

        void onFailure(String msg);
    }

    public interface GetScriptsCallback {
        void onSuccess(List<Script> scripts);

        void onFailure(String msg);
    }

    public interface GetLogsCallback {
        void onSuccess(List<Log> logs);

        void onFailure(String msg);
    }

    public interface GetEnvsCallback {
        void onSuccess(EnvRes data);

        void onFailure(String msg);
    }

    public interface EditEnvCallback {
        void onSuccess(Environment environment);

        void onFailure(String msg);
    }
}
