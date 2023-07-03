package auto.qinglong.ui.activity.panel.setting;

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

import auto.qinglong.R;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.bean.panel.QLLoginLog;
import auto.qinglong.net.NetManager;
import auto.qinglong.net.panel.v10.ApiController;
import auto.base.util.ToastUnit;


public class LoginLogFragment extends BaseFragment {

    private LoginLogItemAdapter itemAdapter;

    private RecyclerView ui_recycler;
    private SmartRefreshLayout ui_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_login_log, container, false);

        ui_refresh = view.findViewById(R.id.refresh_layout);
        ui_recycler = view.findViewById(R.id.recycler_view);

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

        Objects.requireNonNull(ui_recycler.getItemAnimator()).setChangeDuration(0);
        ui_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        ui_recycler.setAdapter(itemAdapter);

        ui_refresh.setOnRefreshListener(refreshLayout -> netGetLoginLogs());
    }

    private void initData() {
        if (init || NetManager.isRequesting(this.getNetRequestID())) {
            return;
        }
        ui_refresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                netGetLoginLogs();
            }
        }, 1000);
    }

    private void netGetLoginLogs() {
        ApiController.getLoginLogs(getNetRequestID(), new ApiController.NetGetLoginLogsCallback() {
            @Override
            public void onSuccess(List<QLLoginLog> logs) {
                itemAdapter.setData(logs);
                init = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (ui_refresh.isRefreshing()) {
                    ui_refresh.finishRefresh(isSuccess);
                }
            }
        });
    }
}