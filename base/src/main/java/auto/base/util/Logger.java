package auto.base.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wsfsp4
 * @version 2023.06.21
 */
public class Logger extends LogFileUtil {

    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARN = 2;
    public static final int LEVEL_ERROR = 3;
    public static final int LEVEL_NONE = 4;

    private static int level = 0;

    public static void setLevel(int level) {
        Logger.level = level;
    }

    public static void debug(String str, Exception e) {
        if (level > LEVEL_DEBUG) {
            return;
        }
        if (e == null) {
            writeLog("[DEBUG]\t" + str);
        } else {
            writeLog("[DEBUG]\t" + str + " " + e.getMessage());
        }
    }

    public static void info(String str, Exception e) {
        if (level > LEVEL_INFO) {
            return;
        }
        if (e == null) {
            writeLog("[INFO]\t" + str);
        } else {
            writeLog("[INFO]\t" + str + " " + e.getMessage());
        }
    }

    public static void warn(String str, Exception e) {
        if (level > LEVEL_WARN) {
            return;
        }
        if (e == null) {
            writeLog("[WARN]\t" + str);
        } else {
            writeLog("[WARN]\t" + str + " " + e.getMessage());
        }
    }

    public static void error(String str, Exception e) {
        if (level > LEVEL_ERROR) {
            return;
        }
        if (e == null) {
            writeLog("[ERROR]\t" + str);
        } else {
            writeLog("[ERROR]\t" + str + " " + e.getMessage());
        }

    }

    public static List<LogLevel> getLevels() {
        List<LogLevel> levels = new ArrayList<>();
        levels.add(new LogLevel("调试", Logger.LEVEL_DEBUG));
        levels.add(new LogLevel("消息", Logger.LEVEL_INFO));
        levels.add(new LogLevel("警告", Logger.LEVEL_WARN));
        levels.add(new LogLevel("错误", Logger.LEVEL_ERROR));
        levels.add(new LogLevel("关闭", Logger.LEVEL_NONE));
        return levels;
    }

    public static String getLevelName(int level) {
        switch (level) {
            case LEVEL_DEBUG:
                return "调试";
            case LEVEL_INFO:
                return "信息";
            case LEVEL_WARN:
                return "警告";
            case LEVEL_ERROR:
                return "错误";
            default:
                return "关闭";
        }
    }

    public static class LogLevel {
        private String name;
        private int value;

        public LogLevel(String name, int level) {
            this.name = name;
            this.value = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
