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
    Call<TaskListRes> getTasks(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("page") int page, @Query("pageSize") int size);

    @GET("api/scripts")
    Call<ScriptFileListRes> getScriptFiles(@Header("Authorization") String authorization);

    /**
     * 获取日志列表.
     *
     * @param authorization the authorization
     * @return the logs
     */
    @GET("api/logs")
    Call<LogFileListRes> getLogFiles(@Header("Authorization") String authorization);
}
