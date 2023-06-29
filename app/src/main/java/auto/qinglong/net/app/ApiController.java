package auto.qinglong.net.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import auto.qinglong.bean.app.Link;
import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.WebRule;
import auto.qinglong.bean.panel.QLEnvironment;
import auto.qinglong.net.NetManager;
import auto.qinglong.utils.WebUnit;
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

    public static void getVersion(@NonNull String requestId, @Nullable String uid, @NonNull VersionCallback callback) {
        Call<Version> call = new Retrofit.Builder()
                .baseUrl(Api.URL_BASE_TENCENT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getVersion(uid);

        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(@NonNull Call<Version> call, @NonNull Response<Version> response) {
                NetManager.finishCall(requestId);
                Version version = response.body();
                if (version != null) {
                    callback.onSuccess(version);
                } else {
                    callback.onFailure(ERROR_NO_BODY);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Version> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getProject(@NonNull String requestId) {
        Call<ResponseBody> call = new Retrofit.Builder()
                .baseUrl(Api.URL_BASE_GIT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getProject();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                NetManager.finishCall(requestId);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getRemoteEnvironments(@NonNull String requestId, @NonNull String url, @NonNull NetRemoteEnvCallback callback) {
        String baseUrl = WebUnit.getHost(url) + "/";
        String path = WebUnit.getPath(url, "");

        Call<List<QLEnvironment>> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getRemoteEnvironments(path);

        call.enqueue(new Callback<List<QLEnvironment>>() {
            @Override
            public void onResponse(@NonNull Call<List<QLEnvironment>> call, @NonNull Response<List<QLEnvironment>> response) {
                NetManager.finishCall(requestId);
                List<QLEnvironment> res = response.body();
                callback.onSuccess(res);
            }

            @Override
            public void onFailure(@NonNull Call<List<QLEnvironment>> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
    }

    public static void getRemoteWebRules(@NonNull String requestId, @NonNull String url, @NonNull NetRemoteWebRuleCallback callback) {
        String baseUrl = WebUnit.getHost(url) + "/";
        String path = WebUnit.getPath(url, "");

        Call<List<WebRule>> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getRemoteWebRules(path);

        call.enqueue(new Callback<List<WebRule>>() {
            @Override
            public void onResponse(@NonNull Call<List<WebRule>> call, @NonNull Response<List<WebRule>> response) {
                NetManager.finishCall(requestId);
                List<WebRule> res = response.body();
                callback.onSuccess(res);
            }

            @Override
            public void onFailure(@NonNull Call<List<WebRule>> call, @NonNull Throwable t) {
                NetManager.finishCall(requestId);
                if (call.isCanceled()) {
                    return;
                }
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        NetManager.addCall(call, requestId);
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
