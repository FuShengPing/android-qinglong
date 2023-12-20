package auto.panel.net.panel.v10;

import auto.panel.net.panel.BaseRes;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 青龙面板接口.
 */
public interface Api {

    @GET("api/crons")
    Call<TasksRes> getTasks(@Query("searchValue") String searchValue);

    @GET("api/envs")
    Call<EnvironmentsRes> getEnvironments(@Query("searchValue") String searchValue);

    @PUT("api/envs/{id}/move")
    Call<BaseRes> moveEnvironment(@Path("id") String id, @Body RequestBody body);

    @GET("api/scripts/files")
    Call<ScriptFilesRes> getScriptFiles();

    @PUT("api/scripts")
    Call<BaseRes> createScript( @Body RequestBody body);

    @GET("api/dependencies")
    Call<DependenciesRes> getDependencies(@Query("searchValue") String searchValue, @Query("type") String type);

    @HTTP(method = "DELETE", path = "api/dependencies", hasBody = true)
    Call<BaseRes> deleteDependencies( @Body RequestBody body);

    @GET("api/logs")
    Call<LogFilesRes> getLogFiles();

    @GET("api/system/log/remove")
    Call<SystemConfigRes> getSystemConfig();

    @PUT("api/system/log/remove")
    Call<BaseRes> updateSystemConfig(@Body RequestBody body);
}
