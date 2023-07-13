package auto.panel.net.panel;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author wsfsp4
 * @version 2023.07.13
 */
public class ResponseInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        // 执行网络请求
        Response response = chain.proceed(chain.request());

        return response;
    }
}
