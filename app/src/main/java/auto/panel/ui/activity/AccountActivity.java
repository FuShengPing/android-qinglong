package auto.panel.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import auto.panel.R;
import auto.panel.bean.app.Account;
import auto.panel.database.db.AccountDataSource;
import auto.panel.ui.adapter.AccountAdapter;
import auto.panel.utils.ActivityUtils;

public class AccountActivity extends BaseActivity {
    private AccountAdapter mAdapter;
    private View uiBarBack;
    private View uiBarAdd;
    private RecyclerView uiRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        uiBarBack = findViewById(R.id.bar_back);
        uiBarAdd = findViewById(R.id.bar_add);
        uiRecycler = findViewById(R.id.recycler_view);

        mAdapter = new AccountAdapter(this);
        uiRecycler.setAdapter(mAdapter);
        uiRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //取消更新动画，避免刷新闪烁
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);

        init();
    }

    @Override
    protected void init() {
        uiBarBack.setOnClickListener(v -> finish());
        uiBarAdd.setOnClickListener(v -> {
            ActivityUtils.clearAndStartActivity(mActivity, LoginActivity.class);
        });

        AccountDataSource source = new AccountDataSource(this);
        List<Account> accounts = source.getAllAccounts();
        source.close();
        mAdapter.setData(accounts);
    }
}