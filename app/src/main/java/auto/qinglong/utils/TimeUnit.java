package auto.qinglong.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUnit {
    private static @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatterA = new SimpleDateFormat("yyyy/M/d HH:mm:ss");
    private static @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatterB = new SimpleDateFormat("yyyy/MM/dd HH:mm");

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

    public static long getMilliTimestamp() {
        return new Date().getTime();
    }
}
