package auto.panel.net.panel.v10;

import auto.panel.net.panel.BaseRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 青龙面板接口.
 */
public interface Api {

    @GET("api/crons")
    Call<TasksRes> getTasks(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    @GET("api/envs")
    Call<EnvironmentsRes> getEnvironments(@Header("Authorization") String authorization, @Query("searchValue") String searchValue);

    @PUT("api/envs/{id}/move")
    Call<BaseRes> moveEnvironment(@Header("Authorization") String authorization, @Path("id") String id, @Body RequestBody body);

    @GET("api/scripts/files")
    Call<ScriptFilesRes> getScriptFiles(@Header("Authorization") String authorization);

    @PUT("api/scripts")
    Call<BaseRes> createScript(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET("api/dependencies")
    Call<DependenciesRes> getDependencies(@Header("Authorization") String authorization, @Query("searchValue") String searchValue, @Query("type") String type);

    @HTTP(method = "DELETE", path = "api/dependencies", hasBody = true)
    Call<BaseRes> deleteDependencies(@Header("Authorization") String authorization, @Body RequestBody body);

    @GET("api/logs")
    Call<LogFilesRes> getLogFiles(@Header("Authorization") String authorization);

    @GET("api/system/log/remove")
    Call<SystemConfigRes> getSystemConfig(@Header("Authorization") String authorization);

    @PUT("api/system/log/remove")
    Call<BaseRes> updateSystemConfig(@Header("Authorization") String authorization, @Body RequestBody body);
}
