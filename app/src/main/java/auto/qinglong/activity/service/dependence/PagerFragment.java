package auto.qinglong.activity.service.dependence;

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

import auto.qinglong.R;
import auto.qinglong.network.ApiController;
import auto.qinglong.network.response.DependenceRes;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.network.RequestManager;
import auto.qinglong.tools.ToastUnit;

public class PagerFragment extends BaseFragment {
    private String type;

    private DepItemAdapter depItemAdapter;
    private PagerActionListener pagerActionListener;

    private SmartRefreshLayout layout_refresh;
    private RecyclerView layout_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_dep_pager, container, false);

        layout_refresh = view.findViewById(R.id.refreshLayout);
        layout_recycler = view.findViewById(R.id.recyclerView);

        init();

        return view;
    }

    /**
     * viewPager中切换fragment会触发该回调 而fragmentLayout则不会
     */
    @Override
    public void onResume() {
        super.onResume();
        firstLoad();
    }

    private void firstLoad() {
        if (!haveFirstSuccess && !RequestManager.isRequesting(getNetRequestID())) {
            new Handler().postDelayed(() -> {
                if (isVisible()) {
                    getDependencies();
                }
            }, 1000);
        }
    }

    @Override
    protected void init() {
        //适配器
        depItemAdapter = new DepItemAdapter(requireContext());
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        layout_recycler.setAdapter(depItemAdapter);

        depItemAdapter.setItemInterface(new DepItemAdapter.ItemActionListener() {
            @Override
            public void onAction(Dependence dependence, int position) {
                if (!depItemAdapter.getCheckState()) {
                    depItemAdapter.setCheckState(true, position);
                    pagerActionListener.onAction();
                }
            }

            @Override
            public void onDetail(Dependence dependence, int position) {

            }

            @Override
            public void onReinstall(Dependence dependence, int position) {
                List<String> ids = new ArrayList<>();
                ids.add(dependence.get_id());
                reinstallDependencies(ids);
            }
        });

        //刷新控件//
        //初始设置处于刷新状态
        layout_refresh.autoRefreshAnimationOnly();
        layout_refresh.setOnRefreshListener(refreshLayout -> getDependencies());
    }

    private void getDependencies() {
        ApiController.getDependencies(getNetRequestID(), "", this.type, new ApiController.GetDependenciesCallback() {
            @Override
            public void onSuccess(DependenceRes res) {
                depItemAdapter.setData(res.getData());
                haveFirstSuccess = true;
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                this.onEnd(false);
            }

            protected void onEnd(boolean isSuccess) {
                if (layout_refresh.isRefreshing()) {
                    layout_refresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    private void reinstallDependencies(List<String> ids) {
        ApiController.reinstallDependencies(getNetRequestID(), ids, new ApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                getDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("请求失败：" + msg);
                //该接口发送请求成功 但可能会出现响应时间超时问题
                getDependencies();
            }
        });
    }

    /**
     * 刷新数据 由外部调用
     */
    public void refreshData() {
        this.getDependencies();
    }

    /**
     * 获取被选择的item ID
     */
    public List<String> getCheckedItemIds() {
        List<String> ids = new ArrayList<>();
        for (Dependence dependence : depItemAdapter.getCheckedItems()) {
            ids.add(dependence.get_id());
        }
        return ids;
    }

    /**
     * 设置外部回调接口
     */
    public void setPagerActionListener(PagerActionListener pagerActionListener) {
        this.pagerActionListener = pagerActionListener;
    }

    /**
     * 设置选择状态，由外部状态栏改变调用
     */
    public void setCheckState(boolean checkState) {
        depItemAdapter.setCheckState(checkState, -1);
    }

    /**
     * 设置item全选
     */
    public void setAllItemCheck(boolean isChecked) {
        if (depItemAdapter.getCheckState()) {
            depItemAdapter.setAllChecked(isChecked);
        }
    }

    /**
     * 设置依赖类型
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public interface PagerActionListener {
        void onAction();
    }
}
