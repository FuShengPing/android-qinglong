package auto.panel.utils.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import auto.panel.bean.panel.PanelEnvironment;
import auto.panel.utils.FileUtil;

/**
 * @author: ASman
 * @date: 2023/11/10
 * @description: 备份环境变量任务
 */
public class BackupEnvironmentTask implements Runnable {
    private BackupResultListener listener;
    private List<PanelEnvironment> environments;
    private String filePath;
    private String fileName;

    public BackupEnvironmentTask(List<PanelEnvironment> environments, String filePath, String fileName, BackupResultListener listener) {
        this.listener = listener;
        this.environments = environments;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        JsonArray jsonArray = new JsonArray();
        for (PanelEnvironment environment : environments) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", environment.getName());
            jsonObject.addProperty("value", environment.getValue());
            jsonObject.addProperty("remark", environment.getRemark());
            jsonArray.add(jsonObject);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String content = gson.toJson(jsonArray);

        try {
            boolean result = FileUtil.save(filePath, fileName, content);
            if (listener != null) {
                if (result) {
                    listener.onSuccess();
                } else {
                    listener.onFail("");
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFail(e.getMessage());
            }
        }
    }

    public interface BackupResultListener {
        void onSuccess();

        void onFail(String msg);
    }
}