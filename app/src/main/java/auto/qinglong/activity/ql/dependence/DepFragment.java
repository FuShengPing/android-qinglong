package auto.qinglong.activity.ql.dependence;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import auto.qinglong.R;
import auto.qinglong.bean.ql.QLDependence;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.popup.EditWindow;
import auto.qinglong.views.popup.EditWindowItem;
import auto.qinglong.views.popup.PopupWindowManager;


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

    private PopupWindow popupWindowMore;
    private PopupWindow popupWindowEdit;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_dep, null);

        //进行控件变量初始化
        ui_bar = view.findViewById(R.id.dep_top_bar);
        ui_nav_bar = view.findViewById(R.id.dep_nav_bar);

        ui_action_bar = view.findViewById(R.id.dep_action_bar);
        ui_action_bar_back = view.findViewById(R.id.dep_action_bar_back);
        ui_action_bar_delete = view.findViewById(R.id.dep_action_bar_delete);
        ui_action_bar_check = view.findViewById(R.id.dep_action_bar_select_all);

        ui_page = view.findViewById(R.id.dep_page);
        ui_page_tab = view.findViewById(R.id.dep_page_tab);
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
        ui_more.setOnClickListener(v -> showPopWindowMenu());

        //操作栏-返回
        ui_action_bar_back.setOnClickListener(v -> showBar(BarType.NAV));

        //操作栏-全选
        ui_action_bar_check.setOnCheckedChangeListener((buttonView, isChecked) -> mCurrentFragment.setAllItemCheck(isChecked));

        //操作栏-删除
        ui_action_bar_delete.setOnClickListener(v -> {
            List<String> ids = mCurrentFragment.getCheckedItemIds();
            if (ids != null && ids.size() > 0) {
                deleteDependence(ids);
            } else {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
            }
        });

        //设置界面适配器
        mPagerAdapter = new PagerAdapter(requireActivity());
        mPagerAdapter.setPagerActionListener(() -> {
            //进入操作栏
            showBar(BarType.ACTION);
        });

        ui_page.setAdapter(mPagerAdapter);
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

    private void initPopWindowMore() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_fg_more, null, false);
        LinearLayout layout_add = view.findViewById(R.id.pop_fg_more_add);
        LinearLayout layout_action = view.findViewById(R.id.pop_fg_more_action);
        TextView layout_add_text = view.findViewById(R.id.pop_fg_more_add_text);
        TextView layout_action_text = view.findViewById(R.id.pop_fg_more_action_text);
        layout_add_text.setText("新建依赖");
        layout_action_text.setText(getString(R.string.mul_action));

        popupWindowMore = new PopupWindow(getContext());
        popupWindowMore.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMore.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowMore.setContentView(view);
        popupWindowMore.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowMore.setOutsideTouchable(true);
        popupWindowMore.setFocusable(true);

        layout_add.setOnClickListener(v -> {
            popupWindowMore.dismiss();
            showEditWindow();
        });

        layout_action.setOnClickListener(v -> {
            popupWindowMore.dismiss();
            mCurrentFragment.setCheckState(true);
            showBar(BarType.ACTION);
        });
    }

    private void showEditWindow() {
        EditWindow editWindow = new EditWindow("新建依赖", "取消", "确定");
        editWindow.setMaxHeight(WindowUnit.getWindowHeightPix() / 3);
        String type = mPagerAdapter.getCurrentFragment(ui_page.getCurrentItem()).getType();
        editWindow.addItem(new EditWindowItem("type", type, "类型", null, false, false));
        editWindow.addItem(new EditWindowItem("name", null, "名称", "请输入依赖名称"));
        editWindow.setActionListener(new EditWindow.OnActionListener() {
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

                addDependence(dependencies);
                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });
        popupWindowEdit = PopupWindowManager.buildEditWindow(requireActivity(), editWindow);
    }

    public void showPopWindowMenu() {
        if (popupWindowMore == null) {
            initPopWindowMore();
        }
        popupWindowMore.showAsDropDown(ui_bar, 0, 0, Gravity.END);
    }

    public void addDependence(List<QLDependence> dependencies) {
        QLApiController.addDependencies(getNetRequestID(), dependencies, new QLApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                if (popupWindowEdit != null && popupWindowEdit.isShowing()) {
                    popupWindowEdit.dismiss();
                }
                //刷新数据
                mPagerAdapter.getCurrentFragment(ui_page.getCurrentItem()).refreshData();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });

    }

    public void deleteDependence(List<String> ids) {
        QLApiController.deleteDependencies(getNetRequestID(), ids, new QLApiController.BaseCallback() {
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

    public void showBar(BarType barType) {
        if (ui_action_bar.getVisibility() == View.VISIBLE) {
            ui_action_bar.setVisibility(View.INVISIBLE);
            mCurrentFragment.setCheckState(false);
            ui_action_bar_check.setChecked(false);
        }

        ui_nav_bar.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            ui_nav_bar.setVisibility(View.VISIBLE);
        } else {
            ui_action_bar_check.setChecked(false);
            ui_action_bar.setVisibility(View.VISIBLE);
        }
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
}