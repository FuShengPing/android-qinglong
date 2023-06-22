package auto.base.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUnit {
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private static final SimpleDateFormat datetimeFormatterA = new SimpleDateFormat("yyyy/M/d HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat datetimeFormatterB = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
    private static final SimpleDateFormat datetimeFormatterC = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.CHINA);

    /**
     * 格式化指定时间
     *
     * @param timestamp 毫秒级时间戳
     * @return yyyy/M/d HH:mm:ss
     */
    public static String datetimeFormatTimeA(long timestamp) {
        return datetimeFormatterA.format(new Date(timestamp));
    }

    /**
     * 格式化当前时间
     *
     * @return yyyy/M/d HH:mm:ss
     */
    public static String datetimeFormatTimeA() {
        return datetimeFormatterA.format(new Date());
    }

    /**
     * 格式化指定时间
     *
     * @param timestamp 毫秒级时间戳
     * @return yyyy/MM/dd HH:mm
     */
    public static String datetimeFormatTimeB(long timestamp) {
        return datetimeFormatterB.format(new Date(timestamp));
    }

    /**
     * 格式化当前时间
     *
     * @return yyyy/MM/dd HH:mm
     */
    public static String datetimeFormatTimeB() {
        return datetimeFormatterB.format(new Date());
    }

    /**
     * 格式化当前时间
     *
     * @return yyyy_MM_dd_HH_mm
     */
    public static String datetimeFormatTimeC() {
        return datetimeFormatterC.format(new Date());
    }

}
