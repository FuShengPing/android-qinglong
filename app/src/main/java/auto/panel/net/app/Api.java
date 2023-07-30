package auto.panel.net.app;

import auto.panel.bean.app.Config;
import auto.panel.bean.app.Extensions;
import auto.panel.bean.app.Version;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * 应用接口.
 */
public interface Api {
    String URL_BASE_TENCENT = "https://service-m1nufffu-1306746806.gz.apigw.tencentcs.com";

    @GET("panel/version")
    Call<Version> getVersion(@Header("uid") String uid);

    @GET("panel/config")
    Call<Config> getConfig(@Header("uid") String uid);

    @GET("panel/extensions")
    Call<Extensions> getExtensions(@Header("uid") String uid);
}
