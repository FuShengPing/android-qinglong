package auto.panel.ui.activity.panel.setting;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.List;
import java.util.Objects;

import auto.base.util.ToastUnit;
import auto.panel.R;
import auto.panel.bean.panel.LoginLog;
import auto.panel.database.sp.PanelPreference;
import auto.panel.ui.BaseFragment;


public class LoginLogFragment extends BaseFragment {
    private LoginLogItemAdapter itemAdapter;

    private RecyclerView uiRecycler;
    private SmartRefreshLayout uiRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_setting_login_log, container, false);

        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiRecycler = view.findViewById(R.id.recycler_view);

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void init() {
        itemAdapter = new LoginLogItemAdapter(getContext());
        uiRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        uiRecycler.setAdapter(itemAdapter);
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);

        uiRefresh.setOnRefreshListener(refreshLayout -> getLoginLogs());
    }

    private void initData() {
        if (init) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getLoginLogs();
            }
        }, 1000);
    }

    private void getLoginLogs() {
        auto.panel.net.panel.ApiController.getLoginLogs(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), new auto.panel.net.panel.ApiController.LoginLogListCallBack() {
            @Override
            public void onSuccess(List<LoginLog> loginLogs) {
                itemAdapter.setData(loginLogs);
                init = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (uiRefresh.isRefreshing()) {
                    uiRefresh.finishRefresh(isSuccess);
                }
            }
        });
    }
}