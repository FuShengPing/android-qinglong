package auto.panel.net.app;

import auto.panel.bean.app.Config;
import auto.panel.bean.app.Version;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * 应用接口.
 */
public interface Api {
    String URL_BASE_TENCENT = "https://gitee.com/wsfsp4/public-static-file/raw/master/qinglong/";

    @GET("version.json")
    Call<Version> getVersion(@Header("uid") String uid);

    @GET("config.json")
    Call<Config> getConfig(@Header("uid") String uid);
}
