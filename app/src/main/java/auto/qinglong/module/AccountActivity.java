package auto.qinglong.module;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import auto.qinglong.R;

public class AccountActivity extends BaseActivity {

    ImageView layout_back;
    RecyclerView layout_recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        layout_back = findViewById(R.id.bar_back);
        layout_recycler = findViewById(R.id.recycler_view);

        init();
    }

    @Override
    protected void init() {
        layout_back.setOnClickListener(v -> finish());
    }

    @Override
    protected void initWindow() {

    }
}