package auto.qinglong.network.http;

import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.network.BaseRes;
import auto.qinglong.bean.app.network.EnvironmentRes;
import auto.qinglong.bean.app.network.WebRuleRes;
import auto.qinglong.bean.ql.network.QLEnvironmentRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Api {
    String URL_VERSION_BASE = "https://gitee.com/";
    String URL_LOG_REPORT_BASE = "https://service-m1nufffu-1306746806.gz.apigw.tencentcs.com:443/";

    @GET("wsfsp4/QingLong/raw/master/version.json")
    Call<Version> getVersion();

    @POST("qinglong/log/report")
    Call<BaseRes> logReport(@Body RequestBody body);

    @GET
    Call<WebRuleRes> getRemoteWebRules(@Url String url);

    @GET
    Call<EnvironmentRes> getRemoteEnvironments(@Url String url);
}
