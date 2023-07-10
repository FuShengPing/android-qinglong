package auto.qinglong.ui.activity.panel.dependence;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.base.view.popup.PopEditObject;
import auto.base.view.popup.PopEditWindow;
import auto.base.view.popup.PopMenuObject;
import auto.base.view.popup.PopMenuWindow;
import auto.base.view.popup.PopupWindowBuilder;
import auto.qinglong.R;
import auto.qinglong.bean.panel.QLDependence;
import auto.qinglong.net.panel.v10.ApiController;
import auto.qinglong.ui.BaseFragment;


public class DepPagerFragment extends BaseFragment {
    public static String TAG = "DepFragment";
    private final String TYPE_NODEJS = "nodejs";
    private final String TYPE_PYTHON = "python3";
    private final String TYPE_LINUX = "linux";

    private DepFragment mCurrentFragment;
    private PagerAdapter mPagerAdapter;
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

    private PopEditWindow uiPopEdit;

    enum BarType {NAV, ACTION}

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dep, null);

        uiNavBar = view.findViewById(R.id.dep_nav_bar);

        uiActionBar = view.findViewById(R.id.dep_action_bar);
        uiActionBarBack = view.findViewById(R.id.dep_action_bar_back);
        uiActionBarDelete = view.findViewById(R.id.dep_action_bar_delete);
        uiActionBarCheck = view.findViewById(R.id.dep_action_bar_select_all);

        uiPage = view.findViewById(R.id.view_page);
        uiPageTab = view.findViewById(R.id.page_tab);
        uiMenu = view.findViewById(R.id.dep_nav_bar_menu);
        uiMore = view.findViewById(R.id.dep_nav_bar_more);

        init();

        return view;
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

    @Override
    public void init() {
        //导航栏回调
        uiMenu.setOnClickListener(v -> mMenuClickListener.onMenuClick());

        //弹窗-更多
        uiMore.setOnClickListener(this::showPopWindowMenu);

        //操作栏-返回
        uiActionBarBack.setOnClickListener(v -> showBar(BarType.NAV));

        //操作栏-全选
        uiActionBarCheck.setOnCheckedChangeListener((buttonView, isChecked) -> mCurrentFragment.onSelectAllChange(isChecked));

        //操作栏-删除
        uiActionBarDelete.setOnClickListener(v -> mCurrentFragment.onDeleteClick());

        mPagerAdapter = new PagerAdapter(requireActivity());//界面适配器
        mPagerAdapter.setPagerActionListener(() -> {
            showBar(BarType.ACTION);//进入操作栏
        });

        uiPage.setAdapter(mPagerAdapter);
        //禁用用户左右滑动页面
        uiPage.setUserInputEnabled(false);
        uiPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //如果处于操作栏则切换至导航栏
                if (uiActionBar.getVisibility() == View.VISIBLE) {
                    showBar(BarType.NAV);
                    mCurrentFragment.setCheckState(false);
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

    private void showPopWindowEdit() {
        uiPopEdit = new PopEditWindow("新建依赖", "取消", "确定");
        uiPopEdit.setMaxHeight(WindowUnit.getWindowHeightPix(requireContext()) / 3);
        String type = mPagerAdapter.getCurrentFragment(uiPage.getCurrentItem()).getType();
        uiPopEdit.addItem(new PopEditObject("type", type, "类型", null, false, false));
        uiPopEdit.addItem(new PopEditObject("name", null, "名称", "请输入依赖名称"));
        uiPopEdit.setActionListener(new PopEditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String type = map.get("type");
                String name = map.get("name");

                if (TextUnit.isEmpty(name)) {
                    ToastUnit.showShort(getString(R.string.tip_empty_dependence_name));
                    return false;
                }

                List<QLDependence> dependencies = new ArrayList<>();
                QLDependence dependence = new QLDependence();
                dependence.setName(name);
                if (TYPE_NODEJS.equals(type)) {
                    dependence.setType(0);
                } else if (TYPE_PYTHON.equals(type)) {
                    dependence.setType(1);
                } else {
                    dependence.setType(2);
                }
                dependencies.add(dependence);

                netAddDependence(dependencies);
                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });
        PopupWindowBuilder.buildEditWindow(requireActivity(), uiPopEdit);
    }

    private void showPopWindowMenu(View view) {
        PopMenuWindow popMenuWindow = new PopMenuWindow(view, Gravity.END);
        popMenuWindow.addItem(new PopMenuObject("add", "新建依赖", R.drawable.ic_gray_add));
        popMenuWindow.addItem(new PopMenuObject("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));
        popMenuWindow.setOnActionListener(key -> {
            if (key.equals("add")) {
                showPopWindowEdit();
            } else {
                showBar(BarType.ACTION);
            }
            return true;
        });

        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showBar(BarType barType) {
        if (barType == BarType.NAV) {
            uiActionBar.setVisibility(View.INVISIBLE);
            mCurrentFragment.setCheckState(false);
            uiActionBarCheck.setChecked(false);
            uiNavBar.setVisibility(View.VISIBLE);
        } else {
            uiNavBar.setVisibility(View.INVISIBLE);
            uiActionBarCheck.setChecked(false);
            mCurrentFragment.setCheckState(true);
            uiActionBar.setVisibility(View.VISIBLE);
        }
    }

    private void netAddDependence(List<QLDependence> dependencies) {
        ApiController.addDependencies(getNetRequestID(), dependencies, new ApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                uiPopEdit.dismiss();
                mPagerAdapter.getCurrentFragment(uiPage.getCurrentItem()).refreshData();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });

    }
}