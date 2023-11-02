package auto.panel.net.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import auto.panel.bean.app.Config;
import auto.panel.bean.app.Extensions;
import auto.panel.bean.app.Version;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {
    private static final String ERROR_NO_BODY = "响应异常";

    public static void getVersion(@Nullable String uid, @NonNull VersionCallBack callBack) {
        Call<Version> call = new Retrofit.Builder()
                .baseUrl(Api.URL_BASE_TENCENT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getVersion(uid);

        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(@NonNull Call<Version> call, @NonNull Response<Version> response) {
                Version version = response.body();
                if (version != null) {
                    callBack.onSuccess(version);
                } else {
                    callBack.onFailure(ERROR_NO_BODY);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Version> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public static void getConfig(@Nullable String uid, @NonNull ConfigCallBack callBack) {
        Call<Config> call = new Retrofit.Builder()
                .baseUrl(Api.URL_BASE_TENCENT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api.class)
                .getConfig(uid);

        call.enqueue(new Callback<Config>() {
            @Override
            public void onResponse(@NonNull Call<Config> call, @NonNull Response<Config> response) {
                Config config = response.body();
                if (config != null) {
                    callBack.onSuccess(config);
                } else {
                    callBack.onFailure(ERROR_NO_BODY);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Config> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callBack.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public interface BaseCallBack {
        default void onSuccess() {
        }

        default void onFailure(String msg) {
        }
    }

    public interface VersionCallBack extends BaseCallBack {
        void onSuccess(Version version);
    }

    public interface ConfigCallBack extends BaseCallBack {
        void onSuccess(Config config);
    }
}
