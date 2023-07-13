package auto.panel.net.panel;

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
 * @version 2023.07.02
 */
public interface Api {
    @GET("api/system")
    Call<SystemInfoRes> getSystemInfo();

    @POST("api/user/login")
    Call<LoginRes> login(@Body RequestBody body);

    @PUT("api/crons/run")
    Call<BaseRes> runTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/crons/stop")
    Call<BaseRes> stopTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/crons/enable")
    Call<BaseRes> enableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/crons/disable")
    Call<BaseRes> disableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/crons/pin")
    Call<BaseRes> pinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/crons/unpin")
    Call<BaseRes> unpinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @POST("api/crons")
    Call<BaseRes> addTask(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/crons")
    Call<BaseRes> updateTask(@Header("Authorization") String authorization, @Body RequestBody body);

    @HTTP(method = "DELETE", path = "api/crons", hasBody = true)
    Call<BaseRes> deleteTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/envs/enable")
    Call<BaseRes> enableEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/envs/disable")
    Call<BaseRes> disableEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/envs")
    Call<BaseRes> updateEnvironment(@Header("Authorization") String authorization, @Body RequestBody body);

    @POST("api/envs")
    Call<BaseRes> addEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    @HTTP(method = "DELETE", path = "api/envs", hasBody = true)
    Call<BaseRes> deleteEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    @POST("api/dependencies")
    Call<BaseRes> addDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/dependencies/reinstall")
    Call<BaseRes> reinstallDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET
    Call<DependenceLogRes> getDependenceLog(@Url String url, @Header("Authorization") String authorization);

    @POST("api/configs/save")
    Call<BaseRes> updateConfigContent(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/scripts")
    Call<BaseRes> updateScript(@Header("Authorization") String authorization, @Body RequestBody body);

    @HTTP(method = "DELETE", path = "api/scripts", hasBody = true)
    Call<BaseRes> deleteScript(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET("api/user/login-log")
    Call<LoginLogsRes> getLoginLogs(@Header("Authorization") String authorization);

    @GET
    Call<FileContentRes> getFileContent(@Url String url, @Header("Authorization") String authorization);

    @PUT("api/user")
    Call<BaseRes> updateAccount(@Header("Authorization") String authorization, @Body RequestBody body);
}
