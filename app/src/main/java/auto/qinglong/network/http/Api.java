package auto.qinglong.network.http;

import java.util.List;

import auto.qinglong.bean.app.Link;
import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.WebRule;
import auto.qinglong.bean.ql.QLEnvironment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    /**
     * 远程导入Web规则.
     *
     * @param url the url
     * @return the remote web rules
     */
    @GET
    Call<List<WebRule>> getRemoteWebRules(@Url String url);

    /**
     * 远程导入环境变量.
     *
     * @param url the url
     * @return the remote environments
     */
    @GET
    Call<List<QLEnvironment>> getRemoteEnvironments(@Url String url);
}
