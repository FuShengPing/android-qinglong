package auto.panel.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.baidu.mobstat.StatService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import auto.base.ui.popup.MenuItem;
import auto.base.ui.popup.MenuPopupWindow;
import auto.base.ui.popup.PopupWindowBuilder;
import auto.base.util.LogUnit;
import auto.panel.R;
import auto.panel.ui.adapter.PanelDependencePagerAdapter;

@SuppressLint("InflateParams")
public class PanelDependencePagerFragment extends BaseFragment {
    public static String TAG = "PanelDependenceFragment";
    public static String NAME = "依赖管理";

    private PanelDependenceFragment mCurrentFragment;
    private PanelDependencePagerAdapter mPagerAdapter;
    private MenuClickListener mMenuClickListener;

    private LinearLayout uiNavBar;
    private LinearLayout uiActionBar;
    private CheckBox uiActionBarCheck;
    private LinearLayout uiActionBarDelete;
    private ImageView uiActionBarBack;
    private ImageView uiMenu;
    private ImageView uiMore;

    private ViewPager2 uiPage;
    private TabLayout uiPageTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_dep, null);

        uiNavBar = view.findViewById(R.id.dep_nav_bar);

        uiActionBar = view.findViewById(R.id.dep_action_bar);
        uiActionBarBack = view.findViewById(R.id.dep_action_bar_back);
        uiActionBarDelete = view.findViewById(R.id.dep_action_bar_delete);
        uiActionBarCheck = view.findViewById(R.id.dep_action_bar_select_all);

        uiPage = view.findViewById(R.id.view_page);
        uiPageTab = view.findViewById(R.id.page_tab);
        uiMenu = view.findViewById(R.id.dep_nav_bar_menu);
        uiMore = view.findViewById(R.id.dep_nav_bar_more);

        mPagerAdapter = new PanelDependencePagerAdapter(requireActivity());
        uiPage.setAdapter(mPagerAdapter);
        uiPage.setUserInputEnabled(false);//禁用用户左右滑动页面

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(requireContext(), NAME);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            StatService.onPageEnd(requireContext(), NAME);
        } else {
            StatService.onPageStart(requireContext(), NAME);
        }
    }

    @Override
    public boolean onDispatchBackKey() {
        if (uiActionBar.getVisibility() == View.VISIBLE) {
            uiActionBarBack.performClick();
            return true;
        } else {
            return false;
        }
    }

    private void onActionBarOpen() {
        uiNavBar.setVisibility(View.INVISIBLE);
        mCurrentFragment.onCheckStateChange(true);
        uiActionBar.setVisibility(View.VISIBLE);
    }

    private void onActionBarClose() {
        uiActionBar.setVisibility(View.INVISIBLE);
        mCurrentFragment.onCheckStateChange(false);
        uiActionBarCheck.setChecked(false);
        uiNavBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void init() {
        //导航栏回调
        uiMenu.setOnClickListener(v -> mMenuClickListener.onMenuClick());

        //弹窗-更多
        uiMore.setOnClickListener(this::showPopWindowMenu);

        //操作栏-返回
        uiActionBarBack.setOnClickListener(v -> onActionBarClose());

        //操作栏-全选
        uiActionBarCheck.setOnCheckedChangeListener((buttonView, isChecked) -> mCurrentFragment.onSelectAllChange(isChecked));

        //操作栏-删除
        uiActionBarDelete.setOnClickListener(v -> mCurrentFragment.onDeleteClick());

        //页面切换监听
        uiPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (uiActionBar.getVisibility() == View.VISIBLE) {
                    onActionBarClose();
                }
                mCurrentFragment = mPagerAdapter.getCurrentFragment(position);
            }
        });

        //设置界面联动
        new TabLayoutMediator(uiPageTab, uiPage, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("NodeJs");
                    break;
                case 1:
                    tab.setText("Python3");
                    break;
                case 2:
                    tab.setText("Linux");
                    break;
            }
        }).attach();
    }

    @Override
    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.mMenuClickListener = mMenuClickListener;
    }

    private void showPopWindowMenu(View view) {
        MenuPopupWindow popMenuWindow = new MenuPopupWindow(view);
        popMenuWindow.addItem(new MenuItem("add", "新建依赖", R.drawable.ic_gray_add));
        popMenuWindow.addItem(new MenuItem("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));

        popMenuWindow.setOnActionListener(key -> {
            if (key.equals("add")) {
                mCurrentFragment.onAddClick();
            } else if (key.equals("mulAction")) {
                onActionBarOpen();
            }
            return true;
        });

        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }
}