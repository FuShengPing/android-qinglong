package auto.qinglong.fragment.dep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import auto.qinglong.R;
import auto.qinglong.api.ApiController;
import auto.qinglong.api.res.DependenceRes;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.tools.LogUnit;
import auto.qinglong.tools.ToastUnit;

public class PagerFragment extends BaseFragment {
    private String type;
    private boolean isLoadSuccess = false;

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

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoadSuccess) {
            getDependencies("");
        }
    }

    private void init() {
        layout_swipe.setColorSchemeColors(getResources().getColor(R.color.theme_color_shadow, null));
        layout_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDependencies("");
            }
        });
    }


    private void getDependencies(String searchValue) {
        ApiController.getDependencies(getClassName(), searchValue, this.type, new ApiController.GetDependenciesCallback() {
            @Override
            public void onSuccess(DependenceRes res) {
                LogUnit.log("onSuccess");
                LogUnit.log(res.getData().size());
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void addDependence() {

    }

    private void deleteDependencies() {

    }

    public void setType(String type) {
        this.type = type;
    }

}
