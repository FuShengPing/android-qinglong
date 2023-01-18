package auto.qinglong.activity.plugin.link;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;

public class PluginLinkActivity extends BaseActivity {
    public static final String TAG = "LinkActivity";

    private ImageView ui_back;
    private TextView ui_contribute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        ui_back = findViewById(R.id.common_bar_back);
        ui_contribute = findViewById(R.id.bar_contribute);

        init();
    }

    @Override
    protected void init() {
        ui_back.setOnClickListener(v -> finish());

        ui_contribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}