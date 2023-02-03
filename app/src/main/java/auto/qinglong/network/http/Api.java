package auto.qinglong.network.http;

import java.util.List;

import auto.qinglong.bean.app.Link;
import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.WebRule;
import auto.qinglong.bean.ql.QLEnvironment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * 应用接口.
 */
public interface Api {
    /**
     * The constant URL_VERSION_BASE.
     */
    String URL_VERSION_BASE = "https://gitee.com/";

    /**
     * 获取版本信息.
     *
     * @return the version
     */
    @GET("wsfsp4/QingLong/raw/master/version.json")
    Call<Version> getVersion();

    /**
     * 获取项目信息.
     *
     * @return the project
     */
    @GET("wsfsp4/QingLong")
    Call<ResponseBody> getProject();

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

    /**
     * 获取推送.
     *
     * @param url the url
     * @return the links
     */
    @GET
    Call<List<Link>> getLinks(@Url String url);
}
