package auto.qinglong.net.panel.v15;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * @author wsfsp4
 * @version 2023.07.06
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
    Call<TasksRes> getTasks(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("page") int page, @Query("pageSize") int size);

    @GET("api/scripts")
    Call<ScriptFilesRes> getScriptFiles(@Header("Authorization") String authorization);

    /**
     * 获取日志列表.
     *
     * @param authorization the authorization
     * @return the logs
     */
    @GET("api/logs")
    Call<LogFilesRes> getLogFiles(@Header("Authorization") String authorization);

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
     * 获取系统配置.
     *
     * @param authorization the authorization
     * @return the log remove
     */
    @GET("api/system/config")
    Call<SystemConfigRes> getSystemConfig(@Header("Authorization") String authorization);
}
