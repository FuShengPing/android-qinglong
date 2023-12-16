package auto.panel.net.panel;

import com.baidu.mobstat.StatService;

import auto.panel.MyApplication;
import auto.panel.utils.thread.AppLogTask;
import auto.panel.utils.thread.ThreadPoolUtil;
import retrofit2.Call;

/**
 * @author wsfsp4
 * @version 2023.07.11
 */
public class NetHandler {
    private static final String ERROR_NO_BODY = "响应异常";
    private static final String ERROR_INVALID_AUTH = "登录信息失效";

    public static boolean handleResponse(int statusCode, BaseRes res, ApiController.BaseCallBack callBack) {
        if (res != null && res.getCode() == 200) {
            return false;
        }

        if (statusCode == 401) {
            callBack.onFailure(ERROR_INVALID_AUTH);
        } else if (res == null) {
            callBack.onFailure(ERROR_NO_BODY + statusCode);
        } else {
            ThreadPoolUtil.execute(new AppLogTask(res.getMessage()));
            callBack.onFailure(res.getMessage());
        }
        return true;
    }

    public static void handleRequestError(Call<?> call, Throwable t, ApiController.BaseCallBack callBack) {
        ThreadPoolUtil.execute(new AppLogTask(t.getMessage()));
        StatService.recordException(MyApplication.getInstance(), t);
        if (!call.isCanceled()) {
            callBack.onFailure(t.getLocalizedMessage());
        }
    }
}
