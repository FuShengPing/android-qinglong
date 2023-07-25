package auto.base.util;

/**
 * @author wsfsp4
 * @version 2023.06.21
 */
public class Logger extends LogFileUtil {
    private static final String PROJECT = "proxy";
    private static final int LEVEL = 0;
    private static final int LEVEL_DEBUG = 0;
    private static final int LEVEL_INFO = 1;
    private static final int LEVEL_WARN = 2;
    private static final int LEVEL_ERROR = 3;
    private static final int LEVEL_NONE = 4;


    public static void debug(String str, Exception e) {
        if (LEVEL > LEVEL_DEBUG) {
            return;
        }
        if (e == null) {
            writeLog(PROJECT, "[DEBUG]\t" + str);
        } else {
            writeLog(PROJECT, "[DEBUG]\t" + str + " " + e.getMessage());
        }
    }

    public static void info(String str, Exception e) {
        if (LEVEL > LEVEL_INFO) {
            return;
        }
        if (e == null) {
            writeLog(PROJECT, "[INFO]\t" + str);
        } else {
            writeLog(PROJECT, "[INFO]\t" + str + " " + e.getMessage());
        }
    }

    public static void warn(String str, Exception e) {
        if (LEVEL > LEVEL_WARN) {
            return;
        }
        if (e == null) {
            writeLog(PROJECT, "[WARN]\t" + str);
        } else {
            writeLog(PROJECT, "[WARN]\t" + str + " " + e.getMessage());
        }
    }

    public static void error(String str, Exception e) {
        if (LEVEL > LEVEL_ERROR) {
            return;
        }
        if (e == null) {
            writeLog(PROJECT, "[ERROR]\t" + str);
        } else {
            writeLog(PROJECT, "[ERROR]\t" + str + " " + e.getMessage());
        }

    }
}
