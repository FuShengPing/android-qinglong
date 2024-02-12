package auto.panel.net.panel;

import auto.panel.net.panel.v15.SystemConfigRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * @author wsfsp4
 * @date 2023.07.02
 */
public interface Api {
    @GET("api/system")
    Call<SystemInfoRes> getSystemInfo();

    @PUT("api/user/init")
    Call<BaseRes> initAccount(@Body RequestBody body);

    @POST("api/user/login")
    Call<LoginRes> login(@Body RequestBody body);

    @PUT("api/user/two-factor/login")
    Call<LoginRes> twoFactorLogin(@Body RequestBody body);

    @PUT("api/crons/run")
    Call<BaseRes> runTasks(@Body RequestBody body);

    @PUT("api/crons/stop")
    Call<BaseRes> stopTasks( @Body RequestBody body);

    @PUT("api/crons/enable")
    Call<BaseRes> enableTasks( @Body RequestBody body);

    @PUT("api/crons/disable")
    Call<BaseRes> disableTasks( @Body RequestBody body);

    @PUT("api/crons/pin")
    Call<BaseRes> pinTasks( @Body RequestBody body);

    @PUT("api/crons/unpin")
    Call<BaseRes> unpinTasks(@Body RequestBody body);

    @POST("api/crons")
    Call<BaseRes> addTask( @Body RequestBody body);

    @PUT("api/crons")
    Call<BaseRes> updateTask( @Body RequestBody body);

    @HTTP(method = "DELETE", path = "api/crons", hasBody = true)
    Call<BaseRes> deleteTasks( @Body RequestBody body);

    @PUT("api/envs/enable")
    Call<BaseRes> enableEnvironments( @Body RequestBody body);

    @PUT("api/envs/disable")
    Call<BaseRes> disableEnvironments( @Body RequestBody body);

    @PUT("api/envs")
    Call<BaseRes> updateEnvironment( @Body RequestBody body);

    @POST("api/envs")
    Call<BaseRes> addEnvironments( @Body RequestBody body);

    @HTTP(method = "DELETE", path = "api/envs", hasBody = true)
    Call<BaseRes> deleteEnvironments( @Body RequestBody body);

    @POST("api/dependencies")
    Call<BaseRes> addDependencies( @Body RequestBody body);

    @PUT("api/dependencies/reinstall")
    Call<BaseRes> reinstallDependencies( @Body RequestBody body);

    @GET
    Call<DependenceLogRes> getDependenceLog(@Url String url);

    @POST("api/configs/save")
    Call<BaseRes> updateConfigContent(@Body RequestBody body);

    @PUT("api/scripts")
    Call<BaseRes> updateScript(@Body RequestBody body);

    @HTTP(method = "DELETE", path = "api/scripts", hasBody = true)
    Call<BaseRes> deleteScript( @Body RequestBody body);

    @GET("api/user/login-log")
    Call<LoginLogsRes> getLoginLogs();

    @GET
    Call<FileContentRes> getFileContent(@Url String url);

    @PUT("api/user")
    Call<BaseRes> updateAccount(@Body RequestBody body);

    @GET("api/system/config")
    Call<SystemConfigRes> getSystemConfig();

    @GET("api/system/config")
    Call<SystemConfigRes> checkToken(@Header("Authorization") String token);
}
