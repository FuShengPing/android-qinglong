package auto.panel.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mobstat.StatService;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.List;
import java.util.Objects;

import auto.panel.R;
import auto.panel.bean.panel.PanelLoginLog;
import auto.panel.ui.adapter.PanelLoginLogItemAdapter;
import auto.panel.utils.ToastUnit;


public class PanelSettingLoginLogFragment extends BaseFragment {
    public static String TAG = "PanelLoginLogFragment";
    public static String NAME = "系统设置-登录日志";

    private PanelLoginLogItemAdapter itemAdapter;

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
        StatService.onPageStart(requireContext(), NAME);
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            StatService.onPageEnd(requireContext(), NAME);
        } else {
            StatService.onPageStart(requireContext(), NAME);
            initData();
        }
    }

    @Override
    protected void init() {
        itemAdapter = new PanelLoginLogItemAdapter(getContext());
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
        auto.panel.net.panel.ApiController.getLoginLogs(new auto.panel.net.panel.ApiController.LoginLogListCallBack() {
            @Override
            public void onSuccess(List<PanelLoginLog> loginLogs) {
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