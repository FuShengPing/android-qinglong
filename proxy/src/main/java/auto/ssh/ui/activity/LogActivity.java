package auto.ssh.ui.activity;

import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import auto.base.util.LogUnit;
import auto.base.util.Logger;
import auto.ssh.R;

public class LogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_activity_log);

        init();
    }

    private void init() {
        getFiles();
    }

    private List<auto.ssh.bean.File> getFiles() {
        List<auto.ssh.bean.File> result = new ArrayList<>();

        String path = Logger.getLogFileDir();

        // 获取指定目录下的所有文件
        File directory = new File(path);

        if (!directory.exists()) {
            return result;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".log")) {
                    LogUnit.log(file.getName() + "\n" + file.getAbsolutePath());
                }
            }
        }

        return result;
    }
}