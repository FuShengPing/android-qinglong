package auto.qinglong.module.app.account;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import auto.qinglong.R;
import auto.qinglong.module.BaseActivity;
import auto.qinglong.tools.LogUnit;

public class AccountActivity extends BaseActivity {

    ImageView layout_back;
    RecyclerView layout_recycler;
    SmartRefreshLayout layout_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        layout_back = findViewById(R.id.bar_back);
        layout_recycler = findViewById(R.id.recycler_view);
        layout_refresh = findViewById(R.id.refreshLayout);

        init();
    }

    @Override
    protected void init() {
        layout_back.setOnClickListener(v -> finish());


        layout_refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                LogUnit.log("onLoadMore");
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                LogUnit.log("onRefresh");
            }
        });

    }

    @Override
    protected void initWindow() {

    }
}