package auto.qinglong.net.panel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public interface Api {
    @GET("api/system")
    Call<SystemInfoRes> getSystemInfo();

    @POST("api/user/login")
    Call<LoginRes> login(@Body RequestBody body);

    @GET
    Call<FileContentRes> getFileContent(@Url String url, @Header("Authorization") String authorization);

    @PUT("api/scripts")
    Call<BaseRes> updateScript(@Header("Authorization") String authorization, @Body RequestBody body);

    @POST("api/configs/save")
    Call<BaseRes> updateConfig(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET("api/system/log/remove")
    Call<SystemLogConfigRes> getSystemLogConfig(@Header("Authorization") String authorization);
}
