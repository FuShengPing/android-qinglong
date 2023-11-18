package auto.panel.utils.thread;

/**
 * @author: ASman
 * @date: 2023/11/10
 * @description: 导入环境变量任务
 */
public class ImportEnvironmentTask {


    public interface ImportResultListener {
        void onStart();

        void onProgress(int progress, int total);

        void onFinished(int success, int total);

        void onFail(String msg);
    }
}
