package auto.ssh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import auto.base.util.Logger;
import auto.ssh.R;
import auto.ssh.ui.adapter.LogFileAdapter;

public class LogActivity extends BaseActivity {
    private View uiExit;
    private RecyclerView uiRecyclerView;

    private LogFileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_activity_log);

        uiExit = findViewById(R.id.exit);
        uiRecyclerView = findViewById(R.id.proxy_log_recyclerview);

        adapter = new LogFileAdapter(self);
        uiRecyclerView.setAdapter(adapter);
        uiRecyclerView.setLayoutManager(new LinearLayoutManager(self, RecyclerView.VERTICAL, false));
        Objects.requireNonNull(uiRecyclerView.getItemAnimator()).setChangeDuration(0);

        init();
    }

    private void init() {
        // 退出
        uiExit.setOnClickListener(v -> finish());

        adapter.setData(getFiles());

        adapter.setListener(file -> {
            Intent intent = new Intent(self, LogDetailActivity.class);
            intent.putExtra(LogDetailActivity.EXTRA_PATH, file.getPath());

            startActivity(intent);
        });
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
                    result.add(new auto.ssh.bean.File(file.getName(), file.getAbsolutePath()));
                }
            }
        }

        return result;
    }
}