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
    private static final ExecutorService instance = new ThreadPoolExecutor(1,
            100,
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    public static void execute(Runnable runnable) {
        try {
            instance.execute(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}