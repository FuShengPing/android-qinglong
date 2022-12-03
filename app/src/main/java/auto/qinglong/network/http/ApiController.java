package auto.qinglong.network.http;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;

import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.network.BaseRes;
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
                Version version = response.body();
                if (version != null) {
                    callback.onSuccess(version);
                } else {
                    callback.onFailure(ERROR_NO_BODY);
                }
                RequestManager.finishCall(requestId);
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                RequestManager.finishCall(requestId);
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
                BaseRes baseRes = response.body();
                if (baseRes != null) {
                    callback.onSuccess(baseRes);
                } else {
                    callback.onFailure(ERROR_NO_BODY);
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

    public interface VersionCallback {
        void onSuccess(Version version);

        void onFailure(String msg);
    }

    public interface BaseCallback {
        void onSuccess(BaseRes baseRes);

        void onFailure(String msg);
    }
}
