package auto.panel.net;

import androidx.annotation.NonNull;

import java.io.IOException;

import auto.panel.database.sp.PanelPreference;
import auto.panel.utils.LogUnit;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author: ASman
 * @date: 2023/12/20
 * @description:
 */
public class RetrofitInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        // 添加鉴权头
        Request modifiedRequest = originalRequest.newBuilder()
                .header("Authorization", PanelPreference.getAuthorization())
                .build();

        Response response = chain.proceed(modifiedRequest);

        LogUnit.log("RetrofitInterceptor");
        LogUnit.log("originalRequest:" + originalRequest.url().toString());
        return response;
    }

}
