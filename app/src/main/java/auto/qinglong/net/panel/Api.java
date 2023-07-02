package auto.qinglong.net.panel;

import auto.qinglong.net.SystemLogConfigRes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * @author wsfsp4
 * @version 2023.07.02
 */
public interface Api {
    @GET("api/system")
    Call<SystemInfoRes> getSystemInfo();

    @GET("api/system/log/remove")
    Call<SystemLogConfigRes> getSystemLogConfig(@Header("Authorization") String authorization);
}
