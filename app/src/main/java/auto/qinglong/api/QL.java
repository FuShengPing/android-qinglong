package auto.qinglong.api;

import auto.qinglong.api.res.BaseRes;
import auto.qinglong.api.res.EditEnvRes;
import auto.qinglong.api.res.EditTaskRes;
import auto.qinglong.api.res.EnvRes;
import auto.qinglong.api.res.LoginRes;
import auto.qinglong.api.res.ScriptRes;
import auto.qinglong.api.res.SystemRes;
import auto.qinglong.api.res.TasksRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface QL {
    //登录
    @POST("api/user/login")
    Call<LoginRes> login(@Body RequestBody body);

    //查询系统信息
    @GET("api/system")
    Call<SystemRes> system(@Header("Authorization") String authorization);

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
    Call<EnvRes> getEnvs(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    //更新环境变量
    @PUT("api/envs")
    Call<EditEnvRes> updateEnv(@Header("Authorization") String authorization, @Body RequestBody body);

    //新建环境变量
    @POST("api/envs")
    Call<EnvRes> addEnvs(@Header("Authorization") String authorization, @Body RequestBody body);

    //删除环境变量
    @HTTP(method = "DELETE", path = "api/envs", hasBody = true)
    Call<BaseRes> deleteEnvs(@Header("Authorization") String authorization, @Body RequestBody body);

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

    //读取日志信息
    @GET
    Call<BaseRes> getLogDetail(@Url String url, @Header("Authorization") String authorization);

    @PUT("api/scripts")
    Call<BaseRes> saveScript(@Header("Authorization") String authorization, @Body RequestBody body);
}
