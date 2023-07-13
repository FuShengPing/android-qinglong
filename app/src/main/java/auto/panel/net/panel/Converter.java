package auto.panel.net.panel;

import java.util.ArrayList;
import java.util.List;

import auto.base.util.TimeUnit;
import auto.panel.bean.panel.LoginLog;

/**
 * @author wsfsp4
 * @version 2023.07.10
 */
public class Converter {
    public static List<LoginLog> convertLoginLogs(List<LoginLogsRes.LoginLogObject> objects) {
        List<LoginLog> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }

        for (LoginLogsRes.LoginLogObject object : objects) {
            LoginLog loginLog = new LoginLog();
            loginLog.setAddress(object.getAddress());
            loginLog.setIp(object.getIp());
            loginLog.setPlatform(object.getPlatform());
            loginLog.setTime(TimeUnit.formatDatetimeA(object.getTimestamp()));
            loginLog.setStatusCode(object.getStatus());
            result.add(loginLog);
        }

        return result;
    }
}
