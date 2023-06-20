package auto.qinglong.activity.ql.dependence;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.ql.CodeWebActivity;
import auto.qinglong.bean.ql.QLDependence;
import auto.qinglong.network.http.NetManager;
import auto.qinglong.network.http.QLApiController;
import auto.base.util.ToastUnit;

public class DepFragment extends BaseFragment {
    private String type;

    private DepItemAdapter depItemAdapter;
    private PagerAdapter.PagerActionListener pagerActionListener;

    private SmartRefreshLayout ui_refresh;
    private RecyclerView ui_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dep_pager, container, false);

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
        depItemAdapter = new DepItemAdapter(requireContext());
        ui_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        Objects.requireNonNull(ui_recycler.getItemAnimator()).setChangeDuration(0);
        ui_recycler.setAdapter(depItemAdapter);

        depItemAdapter.setItemInterface(new DepItemAdapter.ItemActionListener() {
            @Override
            public void onDetail(QLDependence dependence, int position) {
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_DEPENDENCE);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, dependence.getName());
                intent.putExtra(CodeWebActivity.EXTRA_DEPENDENCE_ID, dependence.getId());

                startActivity(intent);
            }

            @Override
            public void onReinstall(QLDependence dependence, int position) {
                List<String> ids = new ArrayList<>();
                ids.add(dependence.getId());
                netReinstallDependencies(ids);
            }
        });

        ui_refresh.setOnRefreshListener(refreshLayout -> netGetDependencies());
    }

    private void initData() {
        if (initDataFlag || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ui_refresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                netGetDependencies();
            }
        }, 1000);
    }

    public void refreshData() {
        this.netGetDependencies();
    }

    public List<String> getCheckedItemIds() {
        List<String> ids = new ArrayList<>();
        for (QLDependence dependence : depItemAdapter.getCheckedItems()) {
            ids.add(dependence.getId());
        }
        return ids;
    }

    public void setPagerActionListener(PagerAdapter.PagerActionListener pagerActionListener) {
        this.pagerActionListener = pagerActionListener;
    }

    public void setCheckState(boolean checkState) {
        depItemAdapter.setCheckState(checkState);
    }

    public void setAllItemCheck(boolean isChecked) {
        if (depItemAdapter.getCheckState()) {
            depItemAdapter.setAllChecked(isChecked);
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    private void netGetDependencies() {
        QLApiController.getDependencies(getNetRequestID(), null, this.type, new QLApiController.NetGetDependenciesCallback() {
            @Override
            public void onSuccess(List<QLDependence> dependencies) {
                depItemAdapter.setData(dependencies);
                initDataFlag = true;
                ToastUnit.showShort(getString(R.string.tip_load_success_header) + dependencies.size());
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.tip_load_failure_header) + msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (ui_refresh.isRefreshing()) {
                    ui_refresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    private void netReinstallDependencies(List<String> ids) {
        QLApiController.reinstallDependencies(getNetRequestID(), ids, new QLApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                netGetDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.tip_reinstall_failure_header) + msg);
                netGetDependencies();
            }
        });
    }
}
