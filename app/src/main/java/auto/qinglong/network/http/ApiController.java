package auto.qinglong.network.http;

import androidx.annotation.NonNull;

import java.util.List;

import auto.qinglong.bean.app.Link;
import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.WebRule;
import auto.qinglong.bean.app.network.BaseRes;
import auto.qinglong.bean.ql.QLEnvironment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {
    private static final String ERROR_NO_BODY = "响应异常";
    public static final String API_GET_VERSION = "ApiController_getVersion";
    public static final String API_GET_PROJECT = "ApiController_getProject";
    public static final String API_GET_REMOTE_ENVS = "ApiController_getRemoteEnvironments";
    public static final String API_GET_REMOTE_RULES = "ApiController_getRemoteWebRules";

    public static void getVersion(@NonNull String requestId, @NonNull VersionCallback callback) {
        Call<Version> call = new Retrofit.Builder()
                .baseUrl(Api.URL_VERSION_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getVersion();

        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                RequestManager.finishCall(requestId);
                Version version = response.body();
                if (version != null) {
                    callback.onSuccess(version);
                } else {
                    callback.onFailure(ERROR_NO_BODY);
                }
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                RequestManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getProject(@NonNull String requestId) {
        Call<ResponseBody> call = new Retrofit.Builder()
                .baseUrl(Api.URL_VERSION_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getProject();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                RequestManager.finishCall(requestId);
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getRemoteEnvironments(@NonNull String requestId, @NonNull String baseUrl, @NonNull String path, @NonNull NetRemoteEnvCallback callback) {
        Call<List<QLEnvironment>> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getRemoteEnvironments(path);

        call.enqueue(new Callback<List<QLEnvironment>>() {
            @Override
            public void onResponse(Call<List<QLEnvironment>> call, Response<List<QLEnvironment>> response) {
                RequestManager.finishCall(requestId);
                List<QLEnvironment> res = response.body();
                callback.onSuccess(res);
            }

            @Override
            public void onFailure(Call<List<QLEnvironment>> call, Throwable t) {
                RequestManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getRemoteWebRules(@NonNull String requestId, @NonNull String baseUrl, @NonNull String path, @NonNull NetRemoteWebRuleCallback callback) {
        Call<List<WebRule>> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getRemoteWebRules(path);

        call.enqueue(new Callback<List<WebRule>>() {
            @Override
            public void onResponse(Call<List<WebRule>> call, Response<List<WebRule>> response) {
                RequestManager.finishCall(requestId);
                List<WebRule> res = response.body();
                callback.onSuccess(res);
            }

            @Override
            public void onFailure(Call<List<WebRule>> call, Throwable t) {
                RequestManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getLinks(@NonNull String requestId, @NonNull String baseUrl, @NonNull String path, @NonNull NetLinkCallback callback) {
        Call<List<Link>> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getLinks(path);

        call.enqueue(new Callback<List<Link>>() {
            @Override
            public void onResponse(Call<List<Link>> call, Response<List<Link>> response) {
                RequestManager.finishCall(requestId);
                List<Link> res = response.body();
                callback.onSuccess(res);
            }

            @Override
            public void onFailure(Call<List<Link>> call, Throwable t) {
                RequestManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        RequestManager.addCall(call, requestId);
    }


    public interface VersionCallback {
        void onSuccess(Version version);

        void onFailure(String msg);
    }

    public interface NetBaseCallback {
        void onSuccess(BaseRes baseRes);

        void onFailure(String msg);
    }

    public interface NetRemoteEnvCallback {
        void onSuccess(List<QLEnvironment> environments);

        void onFailure(String msg);
    }

    public interface NetRemoteWebRuleCallback {
        void onSuccess(List<WebRule> rules);

        void onFailure(String msg);
    }

    public interface NetLinkCallback {
        void onSuccess(List<Link> links);

        void onFailure(String msg);
    }
}
