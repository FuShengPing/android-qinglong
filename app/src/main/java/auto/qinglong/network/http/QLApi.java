package auto.qinglong.network.http;

import auto.qinglong.bean.ql.network.QLBaseRes;
import auto.qinglong.bean.ql.network.QLConfigRes;
import auto.qinglong.bean.ql.network.QLDependenceRes;
import auto.qinglong.bean.ql.network.QLEditEnvRes;
import auto.qinglong.bean.ql.network.QLEditTaskRes;
import auto.qinglong.bean.ql.network.QLEnvironmentRes;
import auto.qinglong.bean.ql.network.QLLogRemoveRes;
import auto.qinglong.bean.ql.network.QLLogRes;
import auto.qinglong.bean.ql.network.QLLoginLogRes;
import auto.qinglong.bean.ql.network.QLLoginRes;
import auto.qinglong.bean.ql.network.QLScriptRes;
import auto.qinglong.bean.ql.network.QLSystemRes;
import auto.qinglong.bean.ql.network.QLTasksRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 青龙面板接口.
 */
public interface QLApi {
    /**
     * 登录.
     *
     * @param body the body
     * @return the call
     */
    @POST("api/user/login")
    Call<QLLoginRes> login(@Body RequestBody body);

    /**
     * 查询系统信息.
     *
     * @return the system info
     */
    @GET("api/system")
    Call<QLSystemRes> getSystemInfo();

    /**
     * 查询任务列表.
     *
     * @param authorization the authorization
     * @param searchValue   the search value
     * @return the tasks
     */
    @GET("api/crons")
    Call<QLTasksRes> getTasks(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    /**
     * 执行任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/run")
    Call<QLBaseRes> runTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 终止任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/stop")
    Call<QLBaseRes> stopTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 启用任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/enable")
    Call<QLBaseRes> enableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 禁用任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/disable")
    Call<QLBaseRes> disableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 顶置任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/pin")
    Call<QLBaseRes> pinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 顶置任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons/unpin")
    Call<QLBaseRes> unpinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 删除任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @HTTP(method = "DELETE", path = "api/crons", hasBody = true)
    Call<QLBaseRes> deleteTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 编辑任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/crons")
    Call<QLEditTaskRes> updateTask(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 新建任务.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @POST("api/crons")
    Call<QLEditTaskRes> addTask(@Header("Authorization") String authorization, @Body RequestBody body);

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
    Call<QLEditEnvRes> updateEnvironment(@Header("Authorization") String authorization, @Body RequestBody body);

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
    Call<QLBaseRes> deleteEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 启用环境变量.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/envs/enable")
    Call<QLBaseRes> enableEnv(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 禁用环境变量.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/envs/disable")
    Call<QLBaseRes> disableEnv(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 读取配置文件.
     *
     * @param authorization the authorization
     * @return the config
     */
    @GET("api/configs/config.sh")
    Call<QLConfigRes> getConfig(@Header("Authorization") String authorization);

    /**
     * 保存配置文件.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @POST("api/configs/save")
    Call<QLBaseRes> updateConfig(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 读取脚本列表.
     *
     * @param authorization the authorization
     * @return the scripts
     */
    @GET("api/scripts/files")
    Call<QLScriptRes> getScripts(@Header("Authorization") String authorization);

    /**
     * 获取脚本详细.
     *
     * @param url           the url
     * @param authorization the authorization
     * @return the script detail
     */
    @GET
    Call<QLBaseRes> getScriptDetail(@Url String url, @Header("Authorization") String authorization);

    /**
     * 保存脚本.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/scripts")
    Call<QLBaseRes> updateScript(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 获取依赖.
     *
     * @param authorization the authorization
     * @param searchValue   the search value
     * @param type          the type
     * @return the dependencies
     */
    @GET("api/dependencies")
    Call<QLDependenceRes> getDependencies(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("type") String type);

    /**
     * 新建依赖.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @POST("api/dependencies")
    Call<QLBaseRes> addDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 删除依赖.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @HTTP(method = "DELETE", path = "api/dependencies", hasBody = true)
    Call<QLBaseRes> deleteDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 重装依赖.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/dependencies/reinstall")
    Call<QLBaseRes> reinstallDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 获取日志列表.
     *
     * @param authorization the authorization
     * @return the logs
     */
    @GET("api/logs")
    Call<QLLogRes> getLogs(@Header("Authorization") String authorization);

    /**
     * 读取日志信息.
     *
     * @param url           the url
     * @param authorization the authorization
     * @return the log detail
     */
    @GET
    Call<QLBaseRes> getLogDetail(@Url String url, @Header("Authorization") String authorization);

    /**
     * 获取登录日志.
     *
     * @param authorization the authorization
     * @return the login logs
     */
    @GET("api/user/login-log")
    Call<QLLoginLogRes> getLoginLogs(@Header("Authorization") String authorization);

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
    Call<QLBaseRes> updateLogRemove(@Header("Authorization") String authorization, @Body RequestBody body);

    /**
     * 更新账号密码.
     *
     * @param authorization the authorization
     * @param body          the body
     * @return the call
     */
    @PUT("api/user")
    Call<QLBaseRes> updateUser(@Header("Authorization") String authorization, @Body RequestBody body);
}
