package auto.panel.net.panel.v15;

import auto.panel.net.panel.BaseRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * @author wsfsp4
 * @version 2023.07.06
 */
public interface Api {
    @GET("api/crons")
    Call<TasksRes> getTasks(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("page") int page, @Query("pageSize") int size);

    @GET("api/envs")
    Call<EnvironmentsRes> getEnvironments(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    @GET("api/scripts")
    Call<ScriptFilesRes> getScriptFiles(@Header("Authorization") String authorization);

    @GET("api/logs")
    Call<LogFilesRes> getLogFiles(@Header("Authorization") String authorization);

    @GET("api/dependencies")
    Call<DependenciesRes> getDependencies(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("type") String type);

    @HTTP(method = "DELETE", path = "api/dependencies/force", hasBody = true)
    Call<BaseRes> deleteDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET("api/system/config")
    Call<SystemConfigRes> getSystemConfig(@Header("Authorization") String authorization);

    @PUT("api/system/config")
    Call<BaseRes> updateSystemConfig(@Header("Authorization") String authorization, @Body RequestBody body);
}
