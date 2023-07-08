package auto.qinglong.net.panel.v10;

import auto.qinglong.bean.panel.network.QLDependenciesRes;
import auto.qinglong.bean.panel.network.QLEnvEditRes;
import auto.qinglong.bean.panel.network.QLEnvironmentRes;
import auto.qinglong.bean.panel.network.QLLogRemoveRes;
import auto.qinglong.bean.panel.network.QLLoginLogsRes;
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

    /**
     * 获取环境变量.
     *
     * @param authorization the authorization
     * @param searchValue   the search value
     * @return the environments
     */
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
     * 删除脚本.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @HTTP(method = "DELETE", path = "api/scripts", hasBody = true)
    Call<BaseRes> deleteScript(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 获取依赖列表.
     *
     * @param authorization the authorization
     * @param searchValue   the search value
     * @param type          the type
     * @return the dependencies
     */
    @GET("api/dependencies")
    Call<QLDependenciesRes> getDependencies(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("type") String type);

    /**
     * 新建依赖.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @POST("api/dependencies")
    Call<BaseRes> addDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 删除依赖.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @HTTP(method = "DELETE", path = "api/dependencies", hasBody = true)
    Call<BaseRes> deleteDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 重装依赖.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/dependencies/reinstall")
    Call<BaseRes> reinstallDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 获取日志列表.
     *
     * @param authorization the authorization
     * @return the logs
     */
    @GET("api/logs")
    Call<LogFilesRes> getLogFiles(@Header("Authorization") String authorization);

    /**
     * 获取登录日志.
     *
     * @param authorization the authorization
     * @return the login logs
     */
    @GET("api/user/login-log")
    Call<QLLoginLogsRes> getLoginLogs(@Header("Authorization") String authorization);

    /**
     * 获取日志删除频率.
     *
     * @param authorization the authorization
     * @return the log remove
     */
    @GET("api/system/log/remove")
    Call<QLLogRemoveRes> getLogRemove(@Header("Authorization") String authorization);

    /**
     * 更新日志删除频率.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/system/log/remove")
    Call<BaseRes> updateLogRemove(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 更新账号密码.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/user")
    Call<BaseRes> updateUser(@Header("Authorization") String authorization, @Body RequestBody body);
}
