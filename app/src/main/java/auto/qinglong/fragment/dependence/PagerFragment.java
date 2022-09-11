package auto.qinglong.fragment.dep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import auto.qinglong.R;
import auto.qinglong.api.ApiController;
import auto.qinglong.api.object.Dependence;
import auto.qinglong.api.res.DependenceRes;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.tools.LogUnit;
import auto.qinglong.tools.ToastUnit;

public class PagerFragment extends BaseFragment {
    private String type;
    private boolean isLoadSuccess = false;

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
        if (!isLoadSuccess) {
            getDependencies();
        }
    }

    private void init() {
        //适配器
        depItemAdapter = new DepItemAdapter(requireContext());
        depItemAdapter.setItemInterface(new ItemInterface() {
            @Override
            public void onLongClick(Dependence dependence, int position) {
                LogUnit.log("onLongClick");
                //适配器未处于选择状态则进入
                if (!depItemAdapter.getCheckState()) {
                    depItemAdapter.setCheckState(true, position);
                    pagerInterface.onAction();
                }
            }

            @Override
            public void onClick(Dependence dependence, int position) {

            }
        });
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        layout_recycler.setAdapter(depItemAdapter);
        //刷新控件
        layout_swipe.setColorSchemeColors(getResources().getColor(R.color.theme_color_shadow, null));
        layout_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDependencies();
            }
        });
    }

    private void getDependencies() {

        ApiController.getDependencies(getClassName(), "", this.type, new ApiController.GetDependenciesCallback() {
            @Override
            public void onSuccess(DependenceRes res) {
                depItemAdapter.setData(res.getData());
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
                isLoadSuccess = true;
                ToastUnit.showShort("加载成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    public List<String> getCheckedItemIds() {
        return null;
    }

    public void setPagerInterface(PagerInterface pagerInterface) {
        this.pagerInterface = pagerInterface;
    }

    /**
     * 设置选择状态，由外部状态栏改变调用
     */
    public void setCheckState(boolean checkState) {
        depItemAdapter.setCheckState(checkState, -1);
    }

    public void setType(String type) {
        this.type = type;
    }

}
