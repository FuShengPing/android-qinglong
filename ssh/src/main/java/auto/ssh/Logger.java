package auto.ssh;

/**
 * @author wsfsp4
 * @version 2023.06.21
 */
public class Logger {
    private static final String PROJECT = "proxy";
    private static final int LEVEL = 0;
    private static final int LEVEL_DEBUG = 0;
    private static final int LEVEL_INFO = 1;
    private static final int LEVEL_WARN = 2;
    private static final int LEVEL_ERROR = 3;


    public static void debug(String str, Exception e) {
        if (LEVEL > LEVEL_DEBUG) {
            return;
        }
        if (e == null) {
            LogFileUtil.writeLogToFile(PROJECT, "[DEBUG]" + str);
        } else {
            LogFileUtil.writeLogToFile(PROJECT, "[DEBUG]" + str + " " + e.getMessage());
        }
    }

    public static void info(String str, Exception e) {
        if (LEVEL > LEVEL_INFO) {
            return;
        }
        if (e == null) {
            LogFileUtil.writeLogToFile(PROJECT, "[INFO]" + str);
        } else {
            LogFileUtil.writeLogToFile(PROJECT, "[INFO]" + str + " " + e.getMessage());
        }
    }

    public static void warn(String str, Exception e) {
        if (LEVEL > LEVEL_WARN) {
            return;
        }
        if (e == null) {
            LogFileUtil.writeLogToFile(PROJECT, "[WARN]" + str);
        } else {
            LogFileUtil.writeLogToFile(PROJECT, "[WARN]" + str + " " + e.getMessage());
        }
    }

    public static void error(String str, Exception e) {
        if (LEVEL > LEVEL_ERROR) {
            return;
        }
        if (e == null) {
            LogFileUtil.writeLogToFile(PROJECT, "[ERROR]" + str);
        } else {
            LogFileUtil.writeLogToFile(PROJECT, "[ERROR]" + str + " " + e.getMessage());
        }

    }
}
