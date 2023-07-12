package auto.qinglong.net.panel;

import retrofit2.Call;

/**
 * @author wsfsp4
 * @version 2023.07.11
 */
public class Handler {
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
            callBack.onFailure(res.getMessage());
        }
        return true;
    }

    public static void handleRequestError(Call<?> call, Throwable t, ApiController.BaseCallBack callBack) {
        if (!call.isCanceled()) {
            callBack.onFailure(t.getLocalizedMessage());
        }
    }
}
