package auto.panel.net;

import androidx.annotation.NonNull;

import java.io.IOException;

import auto.panel.utils.LogUnit;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author: ASman
 * @date: 2023/12/20
 * @description:
 */
public class NetBaseInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        LogUnit.log("RetrofitInterceptor");
        LogUnit.log("originalRequest:" + originalRequest.url());

        Response response = chain.proceed(originalRequest);

        return response;
    }

}
