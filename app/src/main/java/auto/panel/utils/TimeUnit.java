package auto.panel.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUnit {
    private static final SimpleDateFormat utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
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
    public static String formatDatetimeA(long timestamp) {
        return datetimeFormatterA.format(new Date(timestamp));
    }

    /**
     * 格式化当前日期时间
     *
     * @return yyyy/M/d HH:mm:ss
     */
    public static String formatDatetimeA() {
        return datetimeFormatterA.format(new Date());
    }

    /**
     * 格式化指定日期时间
     *
     * @param timestamp 毫秒级时间戳
     * @return yyyy/MM/dd HH:mm
     */
    public static String formatDatetimeB(long timestamp) {
        return datetimeFormatterB.format(new Date(timestamp));
    }

    /**
     * 格式化当前日期时间
     *
     * @return yyyy/MM/dd HH:mm
     */
    public static String formatDatetimeB() {
        return datetimeFormatterB.format(new Date());
    }

    /**
     * 格式化当前日期时间
     *
     * @return yyyy_MM_dd_HH_mm
     */
    public static String formatDatetimeC() {
        return datetimeFormatterC.format(new Date());
    }

    /**
     * 格式化当前日期
     *
     * @return yyyy-MM-dd
     */
    public static String formatDate() {
        return dateFormatter.format(new Date());
    }

    /**
     * 格式化当前时间
     *
     * @return HH:mm
     */
    public static String formatTime() {
        return timeFormatter.format(new Date());
    }

    public static long utcToTimestamp(String utc) {
        try {
            // 将字符串解析为日期对象
            Date date = utcFormatter.parse(utc);

            // 获取时间戳（毫秒级）
            long timestamp = date.getTime();

            return timestamp;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
