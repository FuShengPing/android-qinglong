package auto.qinglong.network;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import auto.qinglong.activity.service.dependence.Dependence;
import auto.qinglong.activity.service.environment.Environment;
import auto.qinglong.activity.service.log.Log;
import auto.qinglong.activity.service.script.Script;
import auto.qinglong.activity.service.task.Task;
import auto.qinglong.network.response.BaseRes;
import auto.qinglong.network.response.DependenceRes;
import auto.qinglong.network.response.EditEnvRes;
import auto.qinglong.network.response.EditTaskRes;
import auto.qinglong.network.response.EnvironmentRes;
import auto.qinglong.network.response.LogRes;
import auto.qinglong.network.response.LoginRes;
import auto.qinglong.network.response.ScriptRes;
import auto.qinglong.network.response.SystemRes;
import auto.qinglong.network.response.TasksRes;
import auto.qinglong.activity.app.account.Account;
import auto.qinglong.database.sp.AccountSP;
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

    public static void getSystemInfo(@NonNull String requestId, @NonNull Account account, @NonNull SystemCallback callback) {
        Call<SystemRes> call = new Retrofit.Builder()
                .baseUrl(account.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getSystemInfo();

        call.enqueue(new Callback<SystemRes>() {
            @Override
            public void onResponse(Call<SystemRes> call, Response<SystemRes> response) {
                SystemRes systemRes = response.body();
                if (systemRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY + response.code());
                    }
                } else {

                    if (systemRes.getCode() == 200) {
                        callback.onSuccess(systemRes);
                    } else {
                        callback.onFailure(systemRes.getMessage());
                    }
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<SystemRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void checkToken(@NonNull String requestId, @NonNull Account account, @NonNull LoginCallback callback) {
        Call<TasksRes> call = new Retrofit.Builder()
                .baseUrl(account.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<TasksRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<LoginRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });
        RequestManager.addCall(call, requestId);
    }

    public static void getTasks(@NonNull String requestId, @NonNull String searchValue, @NonNull GetTasksCallback callback) {
        Call<TasksRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<TasksRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

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
                .create(Api.class)
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
                        callback.onSuccess();
                    } else {
                        callback.onFailure(response.body().getMessage());
                    }

                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

    }

    public static void editTask(@NonNull String requestId, @NonNull Task task, @NonNull EditTaskCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", task.getName());
        jsonObject.addProperty("_id", task.get_id());
        jsonObject.addProperty("command", task.getCommand());
        jsonObject.addProperty("schedule", task.getSchedule());

        String json = jsonObject.toString();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Call<EditTaskRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EditTaskRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
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
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EditTaskRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getEnvironments(@NonNull String requestId, @NonNull String searchValue, @NonNull GetEnvironmentsCallback callback) {
        Call<EnvironmentRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getEnvironments(AccountSP.getCurrentAccount().getAuthorization(), searchValue);
        call.enqueue(new Callback<EnvironmentRes>() {
            @Override
            public void onResponse(Call<EnvironmentRes> call, Response<EnvironmentRes> response) {
                EnvironmentRes environmentRes = response.body();
                if (environmentRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (environmentRes.getCode() == 200) {
                        callback.onSuccess(environmentRes);
                    } else {
                        callback.onFailure(environmentRes.getMessage());
                    }
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EnvironmentRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void addEnvironment(@NonNull String requestId, @NonNull List<Environment> environments, @NonNull GetEnvironmentsCallback callback) {
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

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<EnvironmentRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addEnvironments(AccountSP.getCurrentAccount().getAuthorization(), requestBody);

        call.enqueue(new Callback<EnvironmentRes>() {
            @Override
            public void onResponse(Call<EnvironmentRes> call, Response<EnvironmentRes> response) {
                EnvironmentRes environmentRes = response.body();
                if (environmentRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (environmentRes.getCode() == 200) {
                        callback.onSuccess(environmentRes);
                    } else {
                        callback.onFailure(environmentRes.getMessage());
                    }
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EnvironmentRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void updateEnvironment(@NonNull String requestId, @NonNull Environment environment, @NonNull EditEnvCallback callback) {
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
                .create(Api.class)
                .updateEnvironment(AccountSP.getCurrentAccount().getAuthorization(), requestBody);

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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<EditEnvRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void deleteEnvironments(@NonNull String requestId, @NonNull List<String> envIds, @NonNull BaseCallback callback) {
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
                .create(Api.class)
                .deleteEnvironments(AccountSP.getCurrentAccount().getAuthorization(), body);

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
                        callback.onSuccess();
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }

                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

    }

    public static void enableEnvironments(@NonNull String requestId, @NonNull List<String> envIds, @NonNull BaseCallback callback) {
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
                .create(Api.class)
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
                        callback.onSuccess();
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }

                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

    }

    public static void disableEnvironments(@NonNull String requestId, @NonNull List<String> envIds, @NonNull BaseCallback callback) {
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
                .create(Api.class)
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
                        callback.onSuccess();
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }

                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);

    }

    public static void getLogs(@NonNull String requestId, @NonNull GetLogsCallback callback) {
        Call<LogRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<LogRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getLogDetail(@NonNull String requestId, @NonNull String logPath, @NonNull BaseCallback callback) {
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                    callback.onSuccess();
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });
        RequestManager.addCall(call, requestId);
    }

    public static void getConfigDetail(@NonNull String requestId, @NonNull BaseCallback callback) {
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                    callback.onSuccess();
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });
        RequestManager.addCall(call, requestId);
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
                .create(Api.class)
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
                    callback.onSuccess();
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getScripts(@NonNull String requestId, @NonNull GetScriptsCallback callback) {
        Call<ScriptRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<ScriptRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getScriptDetail(@NonNull String requestId, @NonNull String scriptPath, @NonNull BaseCallback callback) {
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
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
                    callback.onSuccess();
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });
        RequestManager.addCall(call, requestId);
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
                .create(Api.class)
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
                    callback.onSuccess();
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getDependencies(@NonNull String requestId, String searchValue, String type, @NonNull GetDependenciesCallback callback) {
        Call<DependenceRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getDependencies(AccountSP.getCurrentAccount().getAuthorization(), searchValue, type);

        call.enqueue(new Callback<DependenceRes>() {
            @Override
            public void onResponse(Call<DependenceRes> call, Response<DependenceRes> response) {
                if (response.body() == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }
                } else {
                    callback.onSuccess(response.body());
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<DependenceRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });
        RequestManager.addCall(call, requestId);

    }

    public static void addDependencies(@NonNull String requestId, @NonNull List<Dependence> dependencies, @NonNull BaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject;
        for (Dependence dependence : dependencies) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("name", dependence.getName());
            jsonObject.addProperty("type", dependence.getType());
            jsonArray.add(jsonObject);
        }
        String json = jsonArray.toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .addDependencies(AccountSP.getCurrentAccount().getAuthorization(), requestBody);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes baseRes = response.body();
                if (baseRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (baseRes.getCode() == 200) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void deleteDependencies(@NonNull String requestId, @NonNull List<String> ids, @NonNull BaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (String id : ids) {
            jsonArray.add(id);
        }
        String json = jsonArray.toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .deleteDependencies(AccountSP.getCurrentAccount().getAuthorization(), requestBody);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes baseRes = response.body();
                if (baseRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (baseRes.getCode() == 200) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void reinstallDependencies(@NonNull String requestId, @NonNull List<String> ids, @NonNull BaseCallback callback) {
        JsonArray jsonArray = new JsonArray();
        for (String id : ids) {
            jsonArray.add(id);
        }
        String json = jsonArray.toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(AccountSP.getCurrentAccount().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .reinstallDependencies(AccountSP.getCurrentAccount().getAuthorization(), requestBody);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                BaseRes baseRes = response.body();
                if (baseRes == null) {
                    if (response.code() == 401) {
                        callback.onFailure(RES_INVALID_AUTH);
                    } else {
                        callback.onFailure(RES_NO_BODY);
                    }

                } else {
                    if (baseRes.getCode() == 200) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(baseRes.getMessage());
                    }
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }


    public interface SystemCallback {
        void onSuccess(SystemRes systemRes);

        void onFailure(String msg);
    }

    public interface BaseCallback {
        void onSuccess();

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

    public interface GetEnvironmentsCallback {
        void onSuccess(EnvironmentRes res);

        void onFailure(String msg);
    }

    public interface GetDependenciesCallback {
        void onSuccess(DependenceRes res);

        void onFailure(String msg);
    }

    public interface EditEnvCallback {
        void onSuccess(Environment environment);

        void onFailure(String msg);
    }
}
