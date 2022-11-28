package auto.qinglong.activity.ql.dependence;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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

import auto.qinglong.R;
import auto.qinglong.bean.ql.QLDependence;
import auto.qinglong.network.http.ApiController;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;


public class DepFragment extends BaseFragment {
    public static String TAG = "DepFragment";

    enum BarType {NAV, ACTION}

    private final String type_nodejs = "nodejs";
    private final String type_python = "python3";
    private final String type_linux = "linux";

    private PagerFragment currentFragment;
    private PagerAdapter pagerAdapter;
    private MenuClickListener menuClickListener;

    private RelativeLayout layout_bar;
    private LinearLayout layout_nav_bar;
    private LinearLayout layout_action_bar;
    private CheckBox layout_action_bar_check;
    private LinearLayout layout_action_bar_delete;
    private ImageView layout_action_bar_back;

    private ViewPager2 layout_page;
    private TabLayout layout_page_tab;
    private ImageView layout_menu;
    private ImageView layout_more;

    private PopupWindow popupWindowMore;
    private PopupWindow popupWindowEdit;
    private TextView layout_edit_type;
    private TextView layout_edit_name;
    private Button layout_edit_confirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_dep, null);

        //进行控件变量初始化
        layout_bar = view.findViewById(R.id.dep_top_bar);
        layout_nav_bar = view.findViewById(R.id.dep_nav_bar);

        layout_action_bar = view.findViewById(R.id.dep_action_bar);
        layout_action_bar_back = view.findViewById(R.id.dep_action_bar_back);
        layout_action_bar_delete = view.findViewById(R.id.dep_action_bar_delete);
        layout_action_bar_check = view.findViewById(R.id.dep_action_bar_select_all);

        layout_page = view.findViewById(R.id.dep_page);
        layout_page_tab = view.findViewById(R.id.dep_page_tab);
        layout_menu = view.findViewById(R.id.dep_nav_bar_menu);
        layout_more = view.findViewById(R.id.dep_nav_bar_more);

        init();

        return view;
    }

    @Override
    public void init() {
        //导航栏回调
        layout_menu.setOnClickListener(v -> menuClickListener.onMenuClick());

        //弹窗-更多
        layout_more.setOnClickListener(v -> showPopWindowMore());

        //操作栏-返回
        layout_action_bar_back.setOnClickListener(v -> showBar(BarType.NAV));

        //操作栏-全选
        layout_action_bar_check.setOnCheckedChangeListener((buttonView, isChecked) -> currentFragment.setAllItemCheck(isChecked));

        //操作栏-删除
        layout_action_bar_delete.setOnClickListener(v -> {
            List<String> ids = currentFragment.getCheckedItemIds();
            if (ids != null && ids.size() > 0) {
                deleteDependence(ids);
            } else {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
            }
        });

        //设置界面适配器
        pagerAdapter = new PagerAdapter(requireActivity());
        pagerAdapter.setPagerActionListener(() -> {
            //进入操作栏
            showBar(BarType.ACTION);
        });

        layout_page.setAdapter(pagerAdapter);
        layout_page.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //如果处于操作栏则切换至导航栏
                if (layout_action_bar.getVisibility() == View.VISIBLE) {
                    showBar(BarType.NAV);
                    currentFragment.setCheckState(false);
                }
                currentFragment = pagerAdapter.getCurrentFragment(position);
            }
        });

        //设置界面联动
        TabLayoutMediator mediator = new TabLayoutMediator(layout_page_tab, layout_page, (tab, position) -> {
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
            showPopWindowEdit();
        });

        layout_action.setOnClickListener(v -> {
            popupWindowMore.dismiss();
            currentFragment.setCheckState(true);
            showBar(BarType.ACTION);
        });
    }

    private void initPopWindowEdit() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_fg_dep_edit, null, false);
        TextView layout_title = view.findViewById(R.id.pop_title);
        Button layout_edit_cancel = view.findViewById(R.id.pop_edit_cancel);
        layout_edit_type = view.findViewById(R.id.pop_edit_dep_type);
        layout_edit_name = view.findViewById(R.id.pop_edit_dep_name);
        layout_edit_confirm = view.findViewById(R.id.pop_edit_confirm);

        layout_title.setText("新建依赖");

        popupWindowEdit = new PopupWindow(getContext());
        popupWindowEdit.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowEdit.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowEdit.setContentView(view);
        popupWindowEdit.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowEdit.setOutsideTouchable(true);
        popupWindowEdit.setFocusable(true);

        layout_edit_cancel.setOnClickListener(v -> popupWindowEdit.dismiss());

        popupWindowEdit.setOnDismissListener(() -> WindowUnit.setBackgroundAlpha(requireActivity(), 1.0f));

    }

    private void showPopWindowEdit() {
        if (popupWindowEdit == null) {
            initPopWindowEdit();
        }

        //设置依赖类型
        layout_edit_type.setHint(pagerAdapter.getCurrentFragment(layout_page.getCurrentItem()).getType());

        layout_edit_confirm.setOnClickListener(v -> {
            String type = layout_edit_type.getHint().toString();
            String name = layout_edit_name.getText().toString().trim();

            if (name.isEmpty()) {
                ToastUnit.showShort("请输入依赖名称");
                return;
            }

            List<QLDependence> dependencies = new ArrayList<>();
            QLDependence dependence = new QLDependence();
            dependence.setName(name);
            if (type.equals(type_nodejs)) {
                dependence.setType(0);
            } else if (type.equals(type_python)) {
                dependence.setType(1);
            } else {
                dependence.setType(2);
            }
            dependencies.add(dependence);

            addDependence(dependencies);
        });

        WindowUnit.setBackgroundAlpha(requireActivity(), 0.5f);
        popupWindowEdit.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    public void showPopWindowMore() {
        if (popupWindowMore == null) {
            initPopWindowMore();
        }
        popupWindowMore.showAsDropDown(layout_bar, 0, 0, Gravity.END);
    }

    public void addDependence(List<QLDependence> dependencies) {
        ApiController.addDependencies(getNetRequestID(), dependencies, new ApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                popupWindowEdit.dismiss();
                //刷新数据
                pagerAdapter.getCurrentFragment(layout_page.getCurrentItem()).refreshData();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });

    }

    public void deleteDependence(List<String> ids) {
        ApiController.deleteDependencies(getNetRequestID(), ids, new ApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                showBar(BarType.NAV);
                //刷新数据
                pagerAdapter.getCurrentFragment(layout_page.getCurrentItem()).refreshData();
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(msg);
            }
        });
    }

    public void showBar(BarType barType) {
        if (layout_action_bar.getVisibility() == View.VISIBLE) {
            layout_action_bar.setVisibility(View.INVISIBLE);
            currentFragment.setCheckState(false);
            layout_action_bar_check.setChecked(false);
        }

        layout_nav_bar.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            layout_nav_bar.setVisibility(View.VISIBLE);
        } else {
            layout_action_bar_check.setChecked(false);
            layout_action_bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    @Override
    public boolean onBackPressed() {
        if (layout_action_bar.getVisibility() == View.VISIBLE) {
            layout_action_bar_back.performClick();
            return true;
        } else {
            return false;
        }
    }
}