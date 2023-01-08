package auto.qinglong.network.http;

import java.util.List;

import auto.qinglong.bean.app.Version;
import auto.qinglong.bean.app.WebRule;
import auto.qinglong.bean.app.network.BaseRes;
import auto.qinglong.bean.ql.QLEnvironment;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Api {
    String URL_VERSION_BASE = "https://gitee.com/";

    @GET("wsfsp4/QingLong/raw/master/version.json")
    Call<Version> getVersion();

    @GET("wsfsp4/QingLong")
    Call<ResponseBody> getProject();

    @GET
    Call<List<WebRule>> getRemoteWebRules(@Url String url);

    @GET
    Call<List<QLEnvironment>> getRemoteEnvironments(@Url String url);
}
