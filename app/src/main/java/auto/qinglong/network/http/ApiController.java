package auto.qinglong.network.http;

import androidx.annotation.NonNull;

import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.network.BaseRes;
import auto.qinglong.bean.app.network.EnvironmentRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {
    private static final String ERROR_NO_BODY = "响应异常";

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
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void logReport(@NonNull String requestId, @NonNull RequestBody body, @NonNull BaseCallback callback) {

        Call<BaseRes> call = new Retrofit.Builder()
                .baseUrl(Api.URL_LOG_REPORT_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .logReport(body);

        call.enqueue(new Callback<BaseRes>() {
            @Override
            public void onResponse(Call<BaseRes> call, Response<BaseRes> response) {
                RequestManager.finishCall(requestId);
                BaseRes baseRes = response.body();
                if (baseRes != null) {
                    callback.onSuccess(baseRes);
                } else {
                    callback.onFailure(ERROR_NO_BODY);
                }
            }

            @Override
            public void onFailure(Call<BaseRes> call, Throwable t) {
                RequestManager.finishCall(requestId);
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public static void getRemoteEnvironments(@NonNull String requestId, @NonNull String baseUrl, @NonNull String path, @NonNull RemoteEnvCallback callback) {
        Call<EnvironmentRes> call = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getRemoteEnvironments(path);

        call.enqueue(new Callback<EnvironmentRes>() {
            @Override
            public void onResponse(Call<EnvironmentRes> call, Response<EnvironmentRes> response) {
                RequestManager.finishCall(requestId);
                EnvironmentRes res = response.body();
                if (res != null) {
                    if (res.getCode() == 200) {
                        callback.onSuccess(res);
                    } else {
                        callback.onFailure(res.getMsg());
                    }
                } else {
                    callback.onFailure(ERROR_NO_BODY);
                }
            }

            @Override
            public void onFailure(Call<EnvironmentRes> call, Throwable t) {
                RequestManager.finishCall(requestId);
                callback.onFailure(t.getLocalizedMessage());
            }
        });

        RequestManager.addCall(call, requestId);
    }

    public interface VersionCallback {
        void onSuccess(Version version);

        void onFailure(String msg);
    }

    public interface BaseCallback {
        void onSuccess(BaseRes baseRes);

        void onFailure(String msg);
    }

    public interface RemoteEnvCallback {
        void onSuccess(EnvironmentRes res);

        void onFailure(String msg);
    }
}
