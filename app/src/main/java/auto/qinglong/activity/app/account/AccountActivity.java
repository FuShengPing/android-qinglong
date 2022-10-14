package auto.qinglong.activity.app.account;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;

public class AccountActivity extends BaseActivity {
    AccountItemAdapter accountItemAdapter;


    ImageView layout_back;
    RecyclerView layout_recycler;
    SmartRefreshLayout layout_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        layout_back = findViewById(R.id.bar_back);
        layout_recycler = findViewById(R.id.recyclerView);
        layout_refresh = findViewById(R.id.refreshLayout);

        init();
    }

    @Override
    protected void init() {
        accountItemAdapter = new AccountItemAdapter(getBaseContext());
        layout_recycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
        layout_recycler.setAdapter(accountItemAdapter);
        layout_back.setOnClickListener(v -> finish());

    }

}