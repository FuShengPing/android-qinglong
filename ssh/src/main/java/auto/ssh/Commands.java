package auto.ssh;

import android.annotation.SuppressLint;

/**
 * @author wsfsp4
 * @version 2023.06.21
 */
@SuppressLint("DefaultLocale")
public class Commands {
    public static String checkPortCommand(int port) {
        return String.format("netstat -tunlp -t -l | grep  %1$d", port);
    }

    public static String killPid(int pid) {
        return String.format("kill -9 %1$d", pid);
    }

    public static String testProxyNet(int port, int maxTime) {
        return String.format("curl -x 127.0.0.1:%1%d --max-time %2%d --request GET 'https://ip.useragentinfo.com/json'", port, maxTime);
    }
}
