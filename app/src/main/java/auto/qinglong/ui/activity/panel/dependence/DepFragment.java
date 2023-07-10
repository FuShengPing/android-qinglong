package auto.qinglong.ui.activity.panel.dependence;

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

import auto.base.util.ToastUnit;
import auto.qinglong.R;
import auto.qinglong.bean.panel.Dependence;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.net.NetManager;
import auto.qinglong.net.panel.ApiController;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.ui.activity.panel.CodeWebActivity;

public class DepFragment extends BaseFragment {
    private String type;

    private DepItemAdapter itemAdapter;
    private PagerAdapter.PagerActionListener pagerActionListener;

    private SmartRefreshLayout uiRefresh;
    private RecyclerView uiRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dep_pager, container, false);

        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiRecycler = view.findViewById(R.id.recycler_view);

        itemAdapter = new DepItemAdapter(requireContext());
        uiRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        uiRecycler.setAdapter(itemAdapter);
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public void onDeleteClick() {
        List<Object> ids = getCheckedItemIds();
        if (ids.size() > 0) {
            netDeleteDependence(ids);
        } else {
            ToastUnit.showShort(getString(R.string.tip_empty_select));
        }
    }

    public void onSelectAllChange(boolean isChecked) {
        if (itemAdapter.getCheckState()) {
            itemAdapter.setAllChecked(isChecked);
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    protected void init() {
        itemAdapter.setItemInterface(new DepItemAdapter.ItemActionListener() {
            @Override
            public void onDetail(Dependence dependence, int position) {
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_DEPENDENCE);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, dependence.getTitle());
                intent.putExtra(CodeWebActivity.EXTRA_DEPENDENCE_ID, String.valueOf(dependence.getKey()));

                startActivity(intent);
            }

            @Override
            public void onReinstall(Dependence dependence, int position) {
                List<Object> ids = new ArrayList<>();
                ids.add(dependence.getKey());
                reinstallDependencies(ids);
            }
        });

        uiRefresh.setOnRefreshListener(refreshLayout -> getDependencies());
    }

    private void initData() {
        if (init || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getDependencies();
            }
        }, 1000);
    }

    public void refreshData() {
        this.getDependencies();
    }

    private List<Object> getCheckedItemIds() {
        List<Object> ids = new ArrayList<>();
        for (Dependence dependence : itemAdapter.getCheckedItems()) {
            ids.add(dependence.getKey());
        }
        return ids;
    }

    public void setPagerActionListener(PagerAdapter.PagerActionListener pagerActionListener) {
        this.pagerActionListener = pagerActionListener;
    }

    public void setCheckState(boolean checkState) {
        itemAdapter.setCheckState(checkState);
    }

    private void getDependencies() {
        auto.qinglong.net.panel.ApiController.getDependencies(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), "", this.type, new auto.qinglong.net.panel.ApiController.DependenceListCallBack() {
            @Override
            public void onSuccess(List<Dependence> dependencies) {
                itemAdapter.setData(dependencies);
                init = true;
                ToastUnit.showShort("加载成功：" + dependencies.size());
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                this.onEnd(false);
            }

            private void onEnd(boolean isSuccess) {
                if (uiRefresh.isRefreshing()) {
                    uiRefresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    private void reinstallDependencies(List<Object> keys) {
        ApiController.reinstallDependencies(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                getDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("重装失败：" + msg);
                getDependencies();
            }
        });
    }

    private void netDeleteDependence(List<Object> keys) {
        auto.qinglong.net.panel.ApiController.deleteDependencies(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                getDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("重装失败：" + msg);
                getDependencies();
            }
        });
    }
}
