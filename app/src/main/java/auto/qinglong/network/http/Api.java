package auto.qinglong.network.http;

import auto.qinglong.bean.app.Version;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Api {
    String URL_VERSION_BASE = "https://gitee.com/";
    String URL_LOG_REPORT_BASE = "";

    @GET("wsfsp4/QingLong/raw/master/version.json")
    Call<Version> getVersion();

    @POST
    Call<ResponseBody> logReport(@Url String url, @Body String body);
}
