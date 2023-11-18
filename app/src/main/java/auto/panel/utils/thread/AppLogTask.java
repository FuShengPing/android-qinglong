package auto.panel.utils.thread;

import auto.base.util.TimeUnit;
import auto.panel.utils.FileUtil;

/**
 * @author: ASman
 * @date: 2023/11/14
 * @description:
 */
public class AppLogTask implements Runnable {
    Throwable throwable;
    String content;
    boolean kill;

    public AppLogTask(Throwable throwable) {
        this.throwable = throwable;
    }

    public AppLogTask(Throwable throwable, boolean kill) {
        this.throwable = throwable;
        this.kill = kill;
    }

    public AppLogTask(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        try {
            if (this.throwable == null && this.content == null) {
                return;
            }

            String fileName = String.format("app-%s.log", TimeUnit.formatDate());
            String filePath = FileUtil.getAppLogPath();

            if (this.throwable != null) {
                FileUtil.save(filePath, fileName, buildContentOfStack(throwable), true);
            } else {
                FileUtil.save(filePath, fileName, buildContent(content), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.kill) {
                // 结束应用
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
    }

    public static String buildContentOfStack(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append("[time]\n");
        sb.append(TimeUnit.formatDatetimeA());
        sb.append("\n[message]\n");
        sb.append(throwable.getMessage());
        sb.append("\n[stack]\n");
        // 添加异常调用栈
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append(stackTraceElement.toString()).append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    public static String buildContent(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("[time]\n");
        sb.append(TimeUnit.formatDatetimeA());
        sb.append("\n[message]\n");
        sb.append(content);
        sb.append("\n\n");
        return sb.toString();
    }
}
