package auto.qinglong.net.app;

import auto.qinglong.bean.app.Version;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 应用接口.
 */
public interface Api {
    /**
     * The constant URL_VERSION_BASE.
     */
    String URL_BASE_GIT = "https://gitee.com/";
    String URL_BASE_TENCENT = "https://service-m1nufffu-1306746806.gz.apigw.tencentcs.com:443";

    /**
     * 获取项目信息.
     *
     * @return the project
     */
    @GET("wsfsp4/QingLong")
    Call<ResponseBody> getProject();

    /**
     * 获取版本信息.
     *
     * @return the version
     */
    @GET("version")
    Call<Version> getVersion(@Query("uid") String uid);

}
