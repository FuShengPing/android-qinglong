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
import auto.qinglong.bean.ql.network.QLDependenceRes;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.network.http.RequestManager;
import auto.qinglong.utils.ToastUnit;

public class PagerFragment extends BaseFragment {
    private String type;

    private DepItemAdapter depItemAdapter;
    private PagerAdapter.PagerActionListener pagerActionListener;

    private SmartRefreshLayout ui_refresh;
    private RecyclerView ui_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_dep_pager, container, false);

        ui_refresh = view.findViewById(R.id.refreshLayout);
        ui_recycler = view.findViewById(R.id.recyclerView);

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
        //适配器
        depItemAdapter = new DepItemAdapter(requireContext());
        ui_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        Objects.requireNonNull(ui_recycler.getItemAnimator()).setChangeDuration(0);
        ui_recycler.setAdapter(depItemAdapter);

        depItemAdapter.setItemInterface(new DepItemAdapter.ItemActionListener() {
            @Override
            public void onMulAction(QLDependence dependence, int position) {
                depItemAdapter.setCheckState(true, -1);
                pagerActionListener.onMulAction();
            }

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

        //刷新控件//
        //初始设置处于刷新状态
        ui_refresh.autoRefreshAnimationOnly();
        ui_refresh.setOnRefreshListener(refreshLayout -> netGetDependencies());
    }

    private void initData() {
        if (!initDataFlag && !RequestManager.isRequesting(getNetRequestID())) {
            new Handler().postDelayed(() -> {
                if (isVisible()) {
                    netGetDependencies();
                }
            }, 1000);
        }
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
        depItemAdapter.setCheckState(checkState, -1);
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
        QLApiController.getDependencies(getNetRequestID(), "", this.type, new QLApiController.NetGetDependenciesCallback() {
            @Override
            public void onSuccess(QLDependenceRes res) {
                depItemAdapter.setData(res.getData());
                initDataFlag = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                this.onEnd(false);
            }

            protected void onEnd(boolean isSuccess) {
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
                ToastUnit.showShort("请求失败：" + msg);
                //该接口发送请求成功 但可能会出现响应时间超时问题
                netGetDependencies();
            }
        });
    }
}
