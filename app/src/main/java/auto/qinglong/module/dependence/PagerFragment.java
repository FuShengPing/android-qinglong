package auto.qinglong.module.dependence;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.net.ApiController;
import auto.qinglong.net.res.DependenceRes;
import auto.qinglong.module.BaseFragment;
import auto.qinglong.net.CallManager;
import auto.qinglong.tools.ToastUnit;

public class PagerFragment extends BaseFragment {
    private String type;

    private DepItemAdapter depItemAdapter;
    private PagerInterface pagerInterface;

    private SwipeRefreshLayout layout_swipe;
    private RecyclerView layout_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_dep_pager, container, false);

        layout_swipe = view.findViewById(R.id.dep_page_fg_swipe);
        layout_recycler = view.findViewById(R.id.dep_page_fg_recycler);

        init();

        return view;
    }

    /**
     * viewPager中切换fragment会触发该回调 而fragmentLayout则不会
     */
    @Override
    public void onResume() {
        super.onResume();
        //延迟进行首次加载
        if (!haveFirstSuccess) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //如果视图可视则进行加载
                    if (isVisible()) {
                        getDependencies();
                    }
                }
            }, 1000);
        }
    }

    private void init() {
        //适配器
        depItemAdapter = new DepItemAdapter(requireContext());
        depItemAdapter.setItemInterface(new ItemInterface() {
            @Override
            public void onAction(Dependence dependence, int position) {
                //适配器未处于选择状态则进入
                if (!depItemAdapter.getCheckState()) {
                    depItemAdapter.setCheckState(true, position);
                    pagerInterface.onAction();
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
                getDependencies();
            }
        });

        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        layout_recycler.setAdapter(depItemAdapter);

        //刷新控件
        layout_swipe.setColorSchemeColors(getResources().getColor(R.color.theme_color_shadow, null));
        layout_swipe.setOnRefreshListener(this::getDependencies);
    }

    private void getDependencies() {
        ApiController.getDependencies(getClassName(), "", this.type, new ApiController.GetDependenciesCallback() {
            @Override
            public void onSuccess(DependenceRes res) {
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
                depItemAdapter.setData(res.getData());
                haveFirstSuccess = true;
                ToastUnit.showShort("加载成功");
            }

            @Override
            public void onFailure(String msg) {
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
                ToastUnit.showShort(msg);
            }
        });
    }

    private void reinstallDependencies(List<String> ids) {
        if (CallManager.isRequesting(getClassName())) {
            return;
        }
        ApiController.reinstallDependencies(getClassName(), ids, new ApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                getDependencies();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("请求失败：" + msg);
                //该接口发送请求成功 但出现响应时间超时问题
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
    public void setPagerInterface(PagerInterface pagerInterface) {
        this.pagerInterface = pagerInterface;
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
}
