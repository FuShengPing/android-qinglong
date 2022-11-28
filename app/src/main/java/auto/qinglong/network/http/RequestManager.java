package auto.qinglong.network.http;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class RequestManager {
    private static Map<String, Call<?>> callMap = new HashMap<>();

    /**
     * 记录页面的网络请求
     * @param call 网络请求
     * @param id    页面ID
     */
    public static void addCall(Call<?> call, String id) {
        callMap.put(id, call);
    }

    /**
     * 请求页面的网络请求
     * @param id 页面ID
     */
    public static void cancelCall(String id) {
        Call<?> call = callMap.get(id);
        if (call != null) {
            call.cancel();
            callMap.remove(id);
        }
    }

    /**
     * 页面的网络请求结束后移除请求
     * @param id 页面ID
     */
    public static  void finishCall(String id){
        callMap.remove(id);
    }

    /**
     * @param id 页面ID
     * @return  页面是否有网络请求正在进行
     */
    public static boolean isRequesting(String id){
        if(callMap.get(id)==null){
            return false;
        }else{
            return true;
        }
    }

}
