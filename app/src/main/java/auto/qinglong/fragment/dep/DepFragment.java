package auto.qinglong.fragment.dep;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.FragmentInterFace;
import auto.qinglong.fragment.MenuClickInterface;


public class DepFragment extends BaseFragment implements FragmentInterFace {
    public static String TAG = "DepFragment";

    private MenuClickInterface menuClickInterface;

    private ViewPager2 layout_page;
    private TabLayout layout_page_tab;
    private ImageView layout_menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_dep, null);

        layout_page = view.findViewById(R.id.dep_page);
        layout_page_tab = view.findViewById(R.id.dep_page_tab);
        layout_menu = view.findViewById(R.id.dep_top_bar_menu);

        init();

        return view;
    }

    @Override
    public void init() {
        layout_menu.setOnClickListener(v -> menuClickInterface.onMenuClick());

        //设置界面适配器
        layout_page.setAdapter(new PagerAdapter(requireActivity()));
        //设置界面联动
        TabLayoutMediator mediator = new TabLayoutMediator(layout_page_tab, layout_page, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("NodeJs");
                    break;
                case 1:
                    tab.setText("Python");
                    break;
                case 2:
                    tab.setText("Linux");
                    break;
            }
        });
        mediator.attach();

    }

    @Override
    public void setMenuClickInterface(MenuClickInterface menuClickInterface) {
        this.menuClickInterface = menuClickInterface;
    }
}