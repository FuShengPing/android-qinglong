package auto.ssh.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;

import auto.ssh.R;

public class LogDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PATH = "path";

    private View uiExit;
    private TextView uiText;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_activity_log_detail);

        path = getIntent().getStringExtra(EXTRA_PATH);

        uiExit = findViewById(R.id.exit);
        uiText = findViewById(R.id.proxy_log_detail_text);

        init();
    }

    private void init() {
        // 退出
        uiExit.setOnClickListener(v -> finish());

        // 读取文件内容
        try {
            StringBuilder builder = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            uiText.setText(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}