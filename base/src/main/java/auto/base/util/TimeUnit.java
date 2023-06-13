package auto.base.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUnit {
    private static final
    SimpleDateFormat formatterA = new SimpleDateFormat("yyyy/M/d HH:mm:ss", Locale.CHINA);
    private static final
    SimpleDateFormat formatterB = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
    private static final
    SimpleDateFormat formatterC = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.CHINA);

    /**
     * @param timestamp 毫秒级时间戳
     * @return yyyy/M/d HH:mm:ss 格式日期
     */
    public static String formatTimeA(long timestamp) {
        return formatterA.format(new Date(timestamp));
    }

    /**
     * @param timestamp 毫秒级时间戳
     * @return yyyy/MM/dd HH:mm 格式日期
     */
    public static String formatTimeB(long timestamp) {
        return formatterB.format(new Date(timestamp));
    }

    /**
     * @return yyyy_MM_dd_HH_mm 当前格式日期
     */
    public static String formatCurrentTime() {
        return formatterC.format(new Date());
    }

}
