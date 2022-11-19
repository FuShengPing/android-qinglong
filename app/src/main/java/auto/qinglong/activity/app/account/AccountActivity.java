package auto.qinglong.activity.app.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.List;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.database.db.AccountDBHelper;
import auto.qinglong.database.sp.AccountSP;

public class AccountActivity extends BaseActivity {
    AccountItemAdapter accountItemAdapter;

    ImageView layout_back;
    RecyclerView layout_recycler;
    SmartRefreshLayout layout_refresh;
    ImageView layout_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        layout_back = findViewById(R.id.bar_back);
        layout_recycler = findViewById(R.id.recyclerView);
        layout_refresh = findViewById(R.id.refreshLayout);
        layout_edit = findViewById(R.id.account_edit);

        init();
    }


    @Override
    protected void init() {
        accountItemAdapter = new AccountItemAdapter(getBaseContext());
        layout_recycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
        layout_recycler.setAdapter(accountItemAdapter);

        accountItemAdapter.setItemActionListener(new AccountItemAdapter.OnItemActionListener() {
            @Override
            public void onClick(Account account, int position) {

            }

            @Override
            public void onDelete(Account account, int position) {

            }
        });
 
        layout_back.setOnClickListener(v -> {
            if (accountItemAdapter.getDeleteState()) {
                accountItemAdapter.setDeleteState(false);
                layout_edit.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        });

        layout_edit.setOnClickListener(v -> {
            if (!accountItemAdapter.getDeleteState()) {
                accountItemAdapter.setDeleteState(true);
                layout_edit.setVisibility(View.INVISIBLE);
            }
        });

        initData();
    }

    private void initData() {
        int position = 0;
        Account currentAccount = AccountSP.getCurrentAccount();
        List<Account> accounts = AccountDBHelper.getAllAccount();
        //找出当前账号,移至首位
        if (currentAccount != null && accounts.size() > 0) {
            for (Account ac : accounts) {
                if (ac.getAddress().equals(currentAccount.getAddress())) {
                    break;
                }
                position += 1;
            }
            accounts.remove(position);
            accounts.add(0, currentAccount);
        }
        accountItemAdapter.setData(accounts);
    }

}