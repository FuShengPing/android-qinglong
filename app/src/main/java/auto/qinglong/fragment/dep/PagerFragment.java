package auto.qinglong.fragment.dep;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import auto.qinglong.R;
import auto.qinglong.fragment.BaseFragment;

public class PagerFragment extends BaseFragment {
    private String type;


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

    private void init() {
        layout_swipe.setColorSchemeColors(getResources().getColor(R.color.theme_color_shadow, null));
        layout_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeps();
            }
        });
    }

    public void setType(String type) {
        this.type = type;
    }

    private void getDeps() {

    }

    private void addDeps() {

    }


}
