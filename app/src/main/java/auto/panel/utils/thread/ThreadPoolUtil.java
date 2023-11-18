package auto.panel.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: ASman
 * @date: 2023/11/3
 * @description: 线程池工具类
 */
public class ThreadPoolUtil {
    // IO密集型线程池
    private static final ExecutorService instanceIO = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 1, TimeUnit.MINUTES, new SynchronousQueue<>());

    // CPU密集型线程池
    private static final ExecutorService cpuInstance = null;

    public static void executeIO(Runnable runnable) {
        try {
            instanceIO.execute(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}