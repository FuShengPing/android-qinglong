package auto.qinglong.activity.ql.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;

public class SettingFragment extends BaseFragment {
    public static String TAG = "SettingFragment";

    private MenuClickListener menuClickListener;
    private PagerAdapter mPagerAdapter;

    private ImageView ui_menu;
    private TabLayout ui_tab;
    private ViewPager2 ui_page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_setting, null);

        ui_menu = view.findViewById(R.id.action_nav_bar_menu);
        ui_tab = view.findViewById(R.id.page_tab);
        ui_page = view.findViewById(R.id.view_page);

        init();

        return view;
    }

    @Override
    public void init() {
        ui_menu.setOnClickListener(v -> menuClickListener.onMenuClick());

        //设置界面适配器
        mPagerAdapter = new PagerAdapter(requireActivity());

        ui_page.setAdapter(mPagerAdapter);
        ui_page.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
            }
        });

        //设置界面联动
        new TabLayoutMediator(ui_tab, ui_page, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("登录日志");
                    break;
                case 1:
                    tab.setText("应用设置");
                    break;
                case 2:
                    tab.setText("常规设置");
                    break;
            }
        }).attach();
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }
}