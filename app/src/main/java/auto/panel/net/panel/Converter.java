package auto.panel.net.panel;

import java.util.ArrayList;
import java.util.List;

import auto.base.util.TimeUnit;
import auto.panel.bean.panel.PanelLoginLog;

/**
 * @author wsfsp4
 * @version 2023.07.10
 */
public class Converter {
    public static List<PanelLoginLog> convertLoginLogs(List<LoginLogsRes.LoginLogObject> objects) {
        List<PanelLoginLog> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }

        try {
            for (LoginLogsRes.LoginLogObject object : objects) {
                PanelLoginLog loginLog = new PanelLoginLog();
                loginLog.setAddress(object.getAddress());
                loginLog.setIp(object.getIp());
                loginLog.setPlatform(object.getPlatform());
                loginLog.setTime(TimeUnit.formatDatetimeA(object.getTimestamp()));
                loginLog.setStatusCode(object.getStatus());
                result.add(loginLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
