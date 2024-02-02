package auto.panel.net;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    private static TrustManager[] trustManagers;
    private static SSLContext sslContext;

    static {
        try {
            trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            // Do nothing, trust all
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            // Do nothing, trust all
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RetrofitFactory() {
    }

    public static <T> T buildWithAuthorization(final Class<T> service) {
        // 创建 OkHttpClient 实例
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                .hostnameVerifier((hostname, session) -> true) // Disable hostname verification
                .addInterceptor(new NetBaseInterceptor()) // 基础拦截器
                .addInterceptor(new NetAuthInterceptor()); // 鉴权拦截器
        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PanelPreference.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(service);
    }

    public static <T> T build(final Class<T> service, String baseUrl) {
        // 创建 OkHttpClient 实例
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                .hostnameVerifier((hostname, session) -> true) // Disable hostname verification
                .addInterceptor(new NetBaseInterceptor()); // 基础拦截器
        // 创建 builder 实例
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build());
        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.baseUrl(baseUrl);
        } else {
            builder.baseUrl(PanelPreference.getBaseUrl());
        }
        return builder.build().create(service);
    }
}
