package auto.qinglong.net.panel;

import auto.qinglong.net.panel.v10.SystemConfigRes;
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

    /**
     * 终止任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/stop")
    Call<BaseRes> stopTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 启用任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/enable")
    Call<BaseRes> enableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 禁用任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/disable")
    Call<BaseRes> disableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 顶置任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/pin")
    Call<BaseRes> pinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 顶置任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/unpin")
    Call<BaseRes> unpinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 新建任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @POST("api/crons")
    Call<BaseRes> createTask(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 编辑任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons")
    Call<BaseRes> updateTask(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 删除任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @HTTP(method = "DELETE", path = "api/crons", hasBody = true)
    Call<BaseRes> deleteTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/scripts")
    Call<BaseRes> updateScript(@Header("Authorization") String authorization, @Body RequestBody body);

    @HTTP(method = "DELETE", path = "api/scripts", hasBody = true)
    Call<BaseRes> deleteScript(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET
    Call<DependenceLogRes> getDependenceLog(@Url String url, @Header("Authorization") String authorization);

    @GET
    Call<FileContentRes> getFileContent(@Url String url, @Header("Authorization") String authorization);

    @POST("api/configs/save")
    Call<BaseRes> updateConfigContent(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET("api/system/log/remove")
    Call<SystemConfigRes> getSystemConfig(@Header("Authorization") String authorization);
}
