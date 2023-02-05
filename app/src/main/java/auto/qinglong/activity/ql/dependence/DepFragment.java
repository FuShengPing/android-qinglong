package auto.qinglong.activity.ql.dependence;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.bean.ql.QLDependence;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.popup.EditWindow;
import auto.qinglong.views.popup.EditWindowItem;
import auto.qinglong.views.popup.MiniMoreItem;
import auto.qinglong.views.popup.MiniMoreWindow;
import auto.qinglong.views.popup.PopupWindowBuilder;


public class DepFragment extends BaseFragment {
    public static String TAG = "DepFragment";

    enum BarType {NAV, ACTION}

    private final String TYPE_NODEJS = "nodejs";
    private final String TYPE_PYTHON = "python3";
    private final String TYPE_LINUX = "linux";

    private PagerFragment mCurrentFragment;
    private PagerAdapter mPagerAdapter;
    private MenuClickListener mMenuClickListener;

    private RelativeLayout ui_bar;
    private LinearLayout ui_nav_bar;
    private LinearLayout ui_action_bar;
    private CheckBox ui_action_bar_check;
    private LinearLayout ui_action_bar_delete;
    private ImageView ui_action_bar_back;
    private ImageView ui_menu;
    private ImageView ui_more;

    private ViewPager2 ui_page;
    private TabLayout ui_page_tab;

    private EditWindow ui_pop_edit;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dep, null);

        ui_bar = view.findViewById(R.id.dep_top_bar);
        ui_nav_bar = view.findViewById(R.id.dep_nav_bar);

        ui_action_bar = view.findViewById(R.id.dep_action_bar);
        ui_action_bar_back = view.findViewById(R.id.dep_action_bar_back);
        ui_action_bar_delete = view.findViewById(R.id.dep_action_bar_delete);
        ui_action_bar_check = view.findViewById(R.id.dep_action_bar_select_all);

        ui_page = view.findViewById(R.id.view_page);
        ui_page_tab = view.findViewById(R.id.page_tab);
        ui_menu = view.findViewById(R.id.dep_nav_bar_menu);
        ui_more = view.findViewById(R.id.dep_nav_bar_more);

        init();

        return view;
    }

    @Override
    public void init() {
        //导航栏回调
        ui_menu.setOnClickListener(v -> mMenuClickListener.onMenuClick());

        //弹窗-更多
        ui_more.setOnClickListener(v -> showPopWindowMiniMore());

        //操作栏-返回
        ui_action_bar_back.setOnClickListener(v -> showBar(BarType.NAV));

        //操作栏-全选
        ui_action_bar_check.setOnCheckedChangeListener((buttonView, isChecked) -> mCurrentFragment.setAllItemCheck(isChecked));

        //操作栏-删除
        ui_action_bar_delete.setOnClickListener(v -> {
            List<String> ids = mCurrentFragment.getCheckedItemIds();
            if (ids != null && ids.size() > 0) {
                netDeleteDependence(ids);
            } else {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
            }
        });


        mPagerAdapter = new PagerAdapter(requireActivity());//界面适配器
        mPagerAdapter.setPagerActionListener(() -> {
            showBar(BarType.ACTION);//进入操作栏
        });

        ui_page.setAdapter(mPagerAdapter);
        ui_page.setUserInputEnabled(false);//禁用用户左右滑动页面
        ui_page.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //如果处于操作栏则切换至导航栏
                if (ui_action_bar.getVisibility() == View.VISIBLE) {
                    showBar(BarType.NAV);
                    mCurrentFragment.setCheckState(false);
                }
                mCurrentFragment = mPagerAdapter.getCurrentFragment(position);
            }
        });

        //设置界面联动
        TabLayoutMediator mediator = new TabLayoutMediator(ui_page_tab, ui_page, (tab, position) -> {
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
        });
        mediator.attach();

    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.mMenuClickListener = menuClickListener;
    }

    @Override
    public boolean onBackPressed() {
        if (ui_action_bar.getVisibility() == View.VISIBLE) {
            ui_action_bar_back.performClick();
            return true;
        } else {
            return false;
        }
    }

    public void netAddDependence(List<QLDependence> dependencies) {
        QLApiController.addDependencies(getNetRequestID(), dependencies, new QLApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                ui_pop_edit.dismiss();
                //刷新数据
                mPagerAdapter.getCurrentFragment(ui_page.getCurrentItem()).refreshData();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });

    }

    public void netDeleteDependence(List<String> ids) {
        QLApiController.deleteDependencies(getNetRequestID(), ids, new QLApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                showBar(BarType.NAV);
                //刷新数据
                mPagerAdapter.getCurrentFragment(ui_page.getCurrentItem()).refreshData();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    private void showPopWindowEdit() {
        ui_pop_edit = new EditWindow("新建依赖", "取消", "确定");
        ui_pop_edit.setMaxHeight(WindowUnit.getWindowHeightPix(requireContext()) / 3);
        String type = mPagerAdapter.getCurrentFragment(ui_page.getCurrentItem()).getType();
        ui_pop_edit.addItem(new EditWindowItem("type", type, "类型", null, false, false));
        ui_pop_edit.addItem(new EditWindowItem("name", null, "名称", "请输入依赖名称"));
        ui_pop_edit.setActionListener(new EditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String type = map.get("type");
                String name = map.get("name");

                if (TextUnit.isEmpty(name)) {
                    ToastUnit.showShort("请输入依赖名称");
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
        PopupWindowBuilder.buildEditWindow(requireActivity(), ui_pop_edit);
    }

    public void showPopWindowMiniMore() {
        MiniMoreWindow miniMoreWindow = new MiniMoreWindow();
        miniMoreWindow.setTargetView(ui_bar);
        miniMoreWindow.setGravity(Gravity.END);
        miniMoreWindow.addItem(new MiniMoreItem("add", "新建依赖", R.drawable.ic_gray_add));
        miniMoreWindow.addItem(new MiniMoreItem("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));
        miniMoreWindow.setOnActionListener(key -> {
            if (key.equals("add")) {
                showPopWindowEdit();
            } else {
                showBar(BarType.ACTION);
            }
            return true;
        });

        PopupWindowBuilder.buildMiniMoreWindow(requireActivity(), miniMoreWindow);
    }

    public void showBar(BarType barType) {
        if (barType == BarType.NAV) {
            ui_action_bar.setVisibility(View.INVISIBLE);
            mCurrentFragment.setCheckState(false);
            ui_action_bar_check.setChecked(false);
            ui_nav_bar.setVisibility(View.VISIBLE);
        } else {
            ui_nav_bar.setVisibility(View.INVISIBLE);
            ui_action_bar_check.setChecked(false);
            mCurrentFragment.setCheckState(true);
            ui_action_bar.setVisibility(View.VISIBLE);
        }
    }


}