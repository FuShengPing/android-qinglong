package auto.qinglong.net.panel.v10;

import auto.qinglong.bean.panel.network.QLEnvEditRes;
import auto.qinglong.bean.panel.network.QLEnvironmentRes;
import auto.qinglong.net.panel.BaseRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 青龙面板接口.
 */
public interface Api {

    /**
     * 查询任务列表.
     *
     * @param authorization the authorization
     * @param searchValue   the search value
     * @return the tasks
     */
    @GET("api/crons")
    Call<TasksRes> getTasks(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    @GET("api/envs")
    Call<QLEnvironmentRes> getEnvironments(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    /**
     * 更新环境变量.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/envs")
    Call<QLEnvEditRes> updateEnvironment(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 新建环境变量.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @POST("api/envs")
    Call<QLEnvironmentRes> addEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 删除环境变量.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @HTTP(method = "DELETE", path = "api/envs", hasBody = true)
    Call<BaseRes> deleteEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 启用环境变量.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/envs/enable")
    Call<BaseRes> enableEnv(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 禁用环境变量.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/envs/disable")
    Call<BaseRes> disableEnv(@Header("Authorization") String authorization, @Body RequestBody body);

    @PUT("api/envs/{id}/move")
    Call<BaseRes> moveEnv(@Header("Authorization") String authorization, @Path("id") String id, @Body RequestBody body);

    /**
     * 读取脚本列表.
     *
     * @param authorization the authorization
     * @return the scripts
     */
    @GET("api/scripts/files")
    Call<ScriptFilesRes> getScriptFiles(@Header("Authorization") String authorization);

    /**
     * 新建脚本.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/scripts")
    Call<BaseRes> createScript(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 获取依赖列表.
     *
     * @param authorization the authorization
     * @param searchValue   the search value
     * @param type          the type
     * @return the dependencies
     */
    @GET("api/dependencies")
    Call<DependenciesRes> getDependencies(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("type") String type);

    /**
     * 获取日志列表.
     *
     * @param authorization the authorization
     * @return the logs
     */
    @GET("api/logs")
    Call<LogFilesRes> getLogFiles(@Header("Authorization") String authorization);

    /**
     * 获取系统配置.
     *
     * @param authorization the authorization
     * @return the log remove
     */
    @GET("api/system/log/remove")
    Call<SystemConfigRes> getSystemConfig(@Header("Authorization") String authorization);

    /**
     * 更新系统配置.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/system/log/remove")
    Call<BaseRes> updateSystemConfig(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 更新账号密码.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/user")
    Call<BaseRes> updateUserInfo(@Header("Authorization") String authorization, @Body RequestBody body);
}
