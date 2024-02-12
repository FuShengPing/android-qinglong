package auto.panel.ui.activity;

import static auto.panel.ui.activity.LoginActivity.MIN_VERSION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import auto.base.ui.popup.PopupWindowBuilder;
import auto.base.ui.popup.ProgressPopupWindow;
import auto.panel.R;
import auto.panel.bean.app.Account;
import auto.panel.bean.panel.PanelAccount;
import auto.panel.bean.panel.PanelSystemInfo;
import auto.panel.database.db.AccountDataSource;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.ApiController;
import auto.panel.ui.adapter.AccountAdapter;
import auto.panel.utils.ActivityUtils;
import auto.panel.utils.ToastUnit;

public class AccountActivity extends BaseActivity {
    public static final String EXTRA_HIDE_ACTION_ADD = "hide_action_add";
    private boolean mHideActionAdd = false;
    private AccountAdapter mAdapter;
    private View uiBarBack;
    private View uiBarAdd;
    private RecyclerView uiRecycler;

    private ProgressPopupWindow uiPopProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mHideActionAdd = getIntent().getBooleanExtra(EXTRA_HIDE_ACTION_ADD, false);

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
    protected void onDestroy() {
        super.onDestroy();
        if (uiPopProgress != null) {
            uiPopProgress.dismiss();
        }
    }

    @Override
    protected void init() {
        uiBarBack.setOnClickListener(v -> finish());

        if (mHideActionAdd) {
            uiBarAdd.setVisibility(View.GONE);
        } else {
            uiBarAdd.setVisibility(View.VISIBLE);
            uiBarAdd.setOnClickListener(v -> ActivityUtils.clearAndStartActivity(mActivity, LoginActivity.class));
        }

        mAdapter.setListener(new AccountAdapter.ActionListener() {
            @Override
            public void onDelete(Account account, int position) {
                String address = account.getAddress();
                String currentAddress = PanelPreference.getAddress();

                AccountDataSource source = new AccountDataSource(mActivity);
                source.deleteAccount(address);
                source.close();
                mAdapter.removeItem(position);

                //当前账号
                if (address.equals(currentAddress)) {
                    PanelPreference.setAddress(null);
                    PanelPreference.setAuthorization(null);
                    toLoginActivity(new PanelAccount(address, account.getUsername(), account.getPassword()));
                }
            }

            @Override
            public void onClick(Account account, int position) {
                buildPopWindowProgress();
                uiPopProgress.setTextAndShow("登录中");
                PanelAccount panelAccount = new PanelAccount(account.getAddress(), account.getUsername(), account.getPassword());
                netQuerySystemInfo(panelAccount, account.getToken());
            }
        });

        AccountDataSource source = new AccountDataSource(this);
        List<Account> accounts = source.getAllAccounts();
        source.close();
        mAdapter.setData(accounts);
    }

    private void buildPopWindowProgress() {
        if (uiPopProgress == null) {
            uiPopProgress = PopupWindowBuilder.buildProgressWindow(this, () -> NetManager.cancelAllCall(getNetRequestID()));
        }
    }

    private void dismissProgress() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            uiPopProgress.dismiss();
        }
    }

    private void toLoginActivity(PanelAccount account) {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_ADDRESS, account.getAddress());
        intent.putExtra(LoginActivity.EXTRA_USERNAME, account.getUsername());
        intent.putExtra(LoginActivity.EXTRA_PASSWORD, account.getPassword());
        ActivityUtils.clearAndStartActivity(mActivity, intent);
    }

    private void updateAccount(Account account) {
        PanelPreference.setAddress(account.getAddress());
        PanelPreference.setAuthorization(account.getToken());
        AccountDataSource source = new AccountDataSource(mContext);
        source.updateAccount(account);
        source.close();
    }

    private void netQuerySystemInfo(PanelAccount account, String token) {
        auto.panel.net.panel.ApiController.getSystemInfo(account.getBaseUrl(), new ApiController.SystemInfoCallBack() {
            @Override
            public void onSuccess(PanelSystemInfo system) {
                account.setVersion(system.getVersion());
                if (system.getVersion().compareTo(MIN_VERSION) < 0) {
                    ToastUnit.showShort("仅支持" + MIN_VERSION + "及以上版本");
                    dismissProgress();
                } else if (!system.isInitialized()) {
                    toLoginActivity(account);
                } else {
                    netCheckAccountToken(account, token);
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissProgress();
                ToastUnit.showShort(msg);
            }
        });
    }

    private void netCheckAccountToken(PanelAccount panelAccount, String token) {
        auto.panel.net.panel.ApiController.checkAccountToken(panelAccount.getBaseUrl(), token, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                Account account = new Account(panelAccount.getAddress(), panelAccount.getUsername(), panelAccount.getPassword(), token);
                updateAccount(account);
                ActivityUtils.clearAndStartActivity(mActivity, HomeActivity.class);
            }

            @Override
            public void onFailure(String msg) {
                netLogin(panelAccount);
            }
        });
    }

    private void netLogin(PanelAccount panelAccount) {
        auto.panel.net.panel.ApiController.login(panelAccount.getBaseUrl(), panelAccount, new auto.panel.net.panel.ApiController.LoginCallBack() {
            @Override
            public void onSuccess(String token) {
                Account account = new Account(panelAccount.getAddress(), panelAccount.getUsername(), panelAccount.getPassword(), token);
                updateAccount(account);
                ActivityUtils.clearAndStartActivity(mActivity, HomeActivity.class);
            }

            @Override
            public void onFailure(String msg) {
                toLoginActivity(panelAccount);
            }
        });
    }
}