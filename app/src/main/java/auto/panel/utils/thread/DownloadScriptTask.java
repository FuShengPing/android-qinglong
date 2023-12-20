package auto.panel.utils.thread;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import auto.panel.bean.panel.PanelFile;
import auto.panel.net.panel.ApiController;
import auto.panel.utils.FileUtil;

/**
 * @author: ASman
 * @date: 2023/11/19
 * @description:
 */
public class DownloadScriptTask implements Runnable {
    PanelFile targetFile;
    DownloadScriptListener listener;

    public DownloadScriptTask(@NonNull PanelFile file, DownloadScriptListener listener) {
        this.targetFile = file;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (targetFile == null) {
            if (listener != null) {
                listener.onFinish(0, 0);
            }
            return;
        }

        // 构建下载任务
        List<PanelFile> tasks = new ArrayList<>();
        Stack<PanelFile> stack = new Stack<>();
        stack.add(targetFile);
        while (!stack.isEmpty()) {
            PanelFile panelFile = stack.pop();
            if (panelFile.isDir()) {
                stack.addAll(panelFile.getChildren());
            } else {
                tasks.add(panelFile);
            }
        }

        // 下载脚本
        int success = 0;
        for (int index = 0; index < tasks.size(); index++) {
            PanelFile panelFile = tasks.get(index);
            String content = ApiController.getScriptContentSync(panelFile.getTitle(), panelFile.getParentPath());
            if (content != null) {
                String filePath = FileUtil.getPathOfScript() + File.separator + panelFile.getParentPath();
                // 保存文件
                try {
                    FileUtil.save(filePath, panelFile.getTitle(), content);
                    success += 1;
                } catch (Exception e) {
                    ThreadPoolUtil.execute(new AppLogTask(e.getMessage()));
                }
            }
            if (listener != null) {
                listener.onProgress(index + 1, tasks.size());
            }
        }

        if (listener != null) {
            listener.onFinish(success, tasks.size());
        }
    }


    public interface DownloadScriptListener {
        void onProgress(int progress, int total);

        void onFinish(int success, int total);
    }
}
