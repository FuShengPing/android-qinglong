package auto.panel.net;

import auto.panel.database.sp.PanelPreference;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: ASman
 * @date: 2023/12/20
 * @description:
 */
public class RetrofitFactory {
    private RetrofitFactory() {
    }

    public static <T> T buildWithAuthorization(final Class<T> service) {
        // 创建 OkHttpClient 实例，并添加拦截器
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new RetrofitInterceptor());
        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PanelPreference.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(service);
    }

    public static <T> T build(final Class<T> service) {
        // 创建 OkHttpClient 实例，并添加拦截器
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new RetrofitInterceptor());
        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PanelPreference.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(service);
    }
}
