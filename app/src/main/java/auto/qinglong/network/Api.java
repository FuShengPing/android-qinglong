package auto.qinglong.network;

import auto.qinglong.network.response.BaseRes;
import auto.qinglong.network.response.DependenceRes;
import auto.qinglong.network.response.EditEnvRes;
import auto.qinglong.network.response.EditTaskRes;
import auto.qinglong.network.response.EnvironmentRes;
import auto.qinglong.network.response.LogRes;
import auto.qinglong.network.response.LoginRes;
import auto.qinglong.network.response.ScriptRes;
import auto.qinglong.network.response.SystemRes;
import auto.qinglong.network.response.TasksRes;
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

public interface Api {
    //登录
    @POST("api/user/login")
    Call<LoginRes> login(@Body RequestBody body);

    //查询系统信息
    @GET("api/system")
    Call<SystemRes> getSystemInfo();

    //查询任务列表
    @GET("api/crons")
    Call<TasksRes> getTasks(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    //执行任务
    @PUT("api/crons/run")
    Call<BaseRes> runTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    //终止任务
    @PUT("api/crons/stop")
    Call<BaseRes> stopTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    //启用任务
    @PUT("api/crons/enable")
    Call<BaseRes> enableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    //禁用任务
    @PUT("api/crons/disable")
    Call<BaseRes> disableTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    //顶置任务
    @PUT("api/crons/pin")
    Call<BaseRes> pinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    //顶置任务
    @PUT("api/crons/unpin")
    Call<BaseRes> unpinTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    //删除任务
    @HTTP(method = "DELETE", path = "api/crons", hasBody = true)
    Call<BaseRes> deleteTasks(@Header("Authorization") String authorization, @Body RequestBody body);

    //编辑任务
    @PUT("api/crons")
    Call<EditTaskRes> editTask(@Header("Authorization") String authorization, @Body RequestBody body);

    //新建任务
    @POST("api/crons")
    Call<EditTaskRes> addTask(@Header("Authorization") String authorization, @Body RequestBody body);

    //获取环境变量
    @GET("api/envs")
    Call<EnvironmentRes> getEnvironments(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    //更新环境变量
    @PUT("api/envs")
    Call<EditEnvRes> updateEnvironment(@Header("Authorization") String authorization, @Body RequestBody body);

    //新建环境变量
    @POST("api/envs")
    Call<EnvironmentRes> addEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    //删除环境变量
    @HTTP(method = "DELETE", path = "api/envs", hasBody = true)
    Call<BaseRes> deleteEnvironments(@Header("Authorization") String authorization, @Body RequestBody body);

    //启用环境变量
    @PUT("api/envs/enable")
    Call<BaseRes> enableEnv(@Header("Authorization") String authorization, @Body RequestBody body);

    //禁用环境变量
    @PUT("api/envs/disable")
    Call<BaseRes> disableEnv(@Header("Authorization") String authorization, @Body RequestBody body);

    //读取配置文件
    @GET("api/configs/config.sh")
    Call<BaseRes> getConfig(@Header("Authorization") String authorization);

    //保存配置文件
    @POST("api/configs/save")
    Call<BaseRes> saveConfig(@Header("Authorization") String authorization, @Body RequestBody body);

    //读取脚本列表
    @GET("api/scripts/files")
    Call<ScriptRes> getScripts(@Header("Authorization") String authorization);

    //获取脚本详细
    @GET
    Call<BaseRes> getScriptDetail(@Url String url, @Header("Authorization") String authorization);

    //保存脚本
    @PUT("api/scripts")
    Call<BaseRes> saveScript(@Header("Authorization") String authorization, @Body RequestBody body);

    //获取依赖
    @GET("api/dependencies")
    Call<DependenceRes> getDependencies(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("type") String type);

    //新建依赖
    @POST("api/dependencies")
    Call<BaseRes> addDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    //删除依赖
    @HTTP(method = "DELETE", path = "api/dependencies", hasBody = true)
    Call<BaseRes> deleteDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    //重装依赖
    @PUT("api/dependencies/reinstall?t=1662824091642")
    Call<BaseRes> reinstallDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    //获取日志列表
    @GET("api/logs")
    Call<LogRes> getLogs(@Header("Authorization") String authorization);

    //读取日志信息
    @GET
    Call<BaseRes> getLogDetail(@Url String url, @Header("Authorization") String authorization);


}
