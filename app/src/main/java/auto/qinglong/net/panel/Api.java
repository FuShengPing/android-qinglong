package auto.qinglong.net.panel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public interface Api {
    @GET("api/system")
    Call<SystemInfoRes> getSystemInfo();

    @POST("api/user/login")
    Call<LoginRes> login(@Body RequestBody body);

    @GET("api/system/log/remove")
    Call<SystemLogConfigRes> getSystemLogConfig(@Header("Authorization") String authorization);
}
