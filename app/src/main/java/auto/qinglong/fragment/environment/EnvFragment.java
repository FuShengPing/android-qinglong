package auto.qinglong.fragment.env;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.BaseActivity;
import auto.qinglong.api.ApiController;
import auto.qinglong.api.object.Environment;
import auto.qinglong.api.res.EnvironmentRes;
import auto.qinglong.fragment.BaseFragment;
import auto.qinglong.fragment.FragmentInterFace;
import auto.qinglong.fragment.MenuClickInterface;
import auto.qinglong.tools.CallManager;
import auto.qinglong.tools.ToastUnit;
import auto.qinglong.tools.WindowUnit;

public class EnvFragment extends BaseFragment implements FragmentInterFace {
    public static String TAG = "EnvFragment";
    private String currentSearchValue = "";
    private MenuClickInterface menuClickInterface;
    private EnvItemAdapter envItemAdapter;
    private boolean isSuccess = false;

    enum QueryType {QUERY, OTHER}

    enum BarType {NAV, SEARCH, ACTIONS}

    private LinearLayout layout_root;
    private RelativeLayout layout_bar;
    private LinearLayout layout_bar_nav;
    private ImageView layout_nav_menu;
    private ImageView layout_nav_search;
    private ImageView layout_nav_more;
    private LinearLayout layout_bar_search;
    private ImageView layout_search_back;
    private EditText layout_search_value;
    private ImageView layout_search_confirm;
    private LinearLayout layout_bar_actions;
    private ImageView layout_actions_back;
    private CheckBox layout_actions_select;
    private LinearLayout layout_actions_enable;
    private LinearLayout layout_actions_disable;
    private LinearLayout layout_actions_delete;

    private PopupWindow popupWindowMore;
    private PopupWindow popupWindowEdit;
    private TextView layout_edit_type;
    private TextView layout_edit_name;
    private TextView layout_edit_value;
    private TextView layout_edit_remark;
    private Button layout_edit_save;

    private RecyclerView layout_recycler;
    private SwipeRefreshLayout layout_swipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_env, null);

        layout_root = view.findViewById(R.id.root);
        layout_bar = view.findViewById(R.id.env_bar);
        layout_bar_nav = view.findViewById(R.id.env_bar_nav);
        layout_nav_menu = view.findViewById(R.id.env_menu);
        layout_nav_search = view.findViewById(R.id.env_search);
        layout_nav_more = view.findViewById(R.id.env_more);
        layout_bar_search = view.findViewById(R.id.env_bar_search);
        layout_search_back = view.findViewById(R.id.env_bar_search_back);
        layout_search_value = view.findViewById(R.id.env_bar_search_value);
        layout_search_confirm = view.findViewById(R.id.env_bar_search_confirm);
        layout_bar_actions = view.findViewById(R.id.env_bar_actions);
        layout_actions_back = view.findViewById(R.id.env_bar_actions_back);
        layout_actions_select = view.findViewById(R.id.env_bar_actions_select_all);
        layout_actions_enable = view.findViewById(R.id.env_bar_actions_enable);
        layout_actions_disable = view.findViewById(R.id.env_bar_actions_disable);
        layout_actions_delete = view.findViewById(R.id.env_bar_actions_delete);

        layout_swipe = view.findViewById(R.id.env_swipe);
        layout_recycler = view.findViewById(R.id.env_recycler);
        envItemAdapter = new EnvItemAdapter(requireContext());
        layout_recycler.setAdapter(envItemAdapter);
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);

        init();

        return view;
    }

    @Override
    public void init() {
        envItemAdapter.setItemInterface(new ItemInterface() {
            @Override
            public void onEdit(Environment environment, int position) {
                showPopWindowEdit(environment);
            }

            @Override
            public void onActions(Environment environment, int position) {
                if (!envItemAdapter.isCheckState()) {
                    showBar(BarType.ACTIONS);
                }
            }
        });

        //导航栏
        layout_nav_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClickInterface.onMenuClick();
            }
        });

        //下拉刷新
        layout_swipe.setColorSchemeColors(requireContext().getColor(R.color.theme_color));
        layout_swipe.setRefreshing(true);
        layout_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEnvs(currentSearchValue, QueryType.QUERY);
            }
        });

        //更多
        layout_nav_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindowMore();
            }
        });

        //搜索栏进入
        layout_nav_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_search_value.setText(currentSearchValue);
                showBar(BarType.SEARCH);
            }
        });

        //搜索栏确定
        layout_search_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = layout_search_value.getText().toString().trim();
                if (!value.isEmpty()) {
                    currentSearchValue = value;
                    WindowUnit.hideKeyboard(layout_search_value);
                    getEnvs(currentSearchValue, QueryType.OTHER);
                }
            }
        });

        //搜索栏返回
        layout_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBar(BarType.NAV);
            }
        });

        //动作栏返回
        layout_actions_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBar(BarType.NAV);
            }
        });

        //全选
        layout_actions_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                envItemAdapter.setAllChecked(isChecked);
            }
        });

        //删除
        layout_actions_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                List<Environment> environments = envItemAdapter.getSelectedItems();
                if (environments.size() == 0) {
                    ToastUnit.showShort(getContext(), "至少选择一项");
                    return;
                }

                List<String> ids = new ArrayList<>();
                for (Environment environment : environments) {
                    ids.add(environment.get_id());
                }
                deleteEnvs(ids);
            }
        });

        //禁用
        layout_actions_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                List<Environment> environments = envItemAdapter.getSelectedItems();
                if (environments.size() == 0) {
                    ToastUnit.showShort(getContext(), "至少选择一项");
                    return;
                }

                List<String> ids = new ArrayList<>();
                for (Environment environment : environments) {
                    ids.add(environment.get_id());
                }
                disableEnvs(ids);
            }
        });

        //启用
        layout_actions_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }
                List<Environment> environments = envItemAdapter.getSelectedItems();
                if (environments.size() == 0) {
                    ToastUnit.showShort(getContext(), "至少选择一项");
                    return;
                }

                List<String> ids = new ArrayList<>();
                for (Environment environment : environments) {
                    ids.add(environment.get_id());
                }
                enableEnvs(ids);
            }
        });

    }

    private void getEnvs(String searchValue, QueryType queryType) {
        ApiController.getEnvironments(getClassName(), searchValue, new ApiController.GetEnvironmentsCallback() {
            @Override
            public void onSuccess(EnvironmentRes res) {
                isSuccess = true;
                envItemAdapter.setData(res.getData());
                if (queryType == QueryType.QUERY) {
                    ToastUnit.showShort(requireContext(), "加载成功");
                }
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "加载失败：" + msg);
                if (layout_swipe.isRefreshing()) {
                    layout_swipe.setRefreshing(false);
                }
            }
        });
    }

    public void updateEnv(Environment environment) {
        ApiController.updateEnvironment(getClassName(), environment, new ApiController.EditEnvCallback() {
            @Override
            public void onSuccess(Environment data) {
                popupWindowEdit.dismiss();
                ToastUnit.showShort(requireContext(), "更新成功");
                getEnvs(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "更新失败：" + msg);
            }
        });
    }

    public void addEnvs(List<Environment> environments) {
        ApiController.addEnvironment(getClassName(), environments, new ApiController.GetEnvironmentsCallback() {
            @Override
            public void onSuccess(EnvironmentRes res) {
                popupWindowEdit.dismiss();
                ToastUnit.showShort(requireContext(), "新建成功");
                getEnvs(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "新建失败：" + msg);
            }
        });
    }

    public void deleteEnvs(List<String> ids) {
        ApiController.deleteEnvironments(getClassName(), ids, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String msg) {
                layout_actions_back.performClick();
                ToastUnit.showShort(requireContext(), "删除成功");
                getEnvs(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "删除失败：" + msg);
            }
        });
    }

    public void enableEnvs(List<String> ids) {
        ApiController.enableEnvironments(getClassName(), ids, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String msg) {
                layout_actions_back.performClick();
                ToastUnit.showShort(requireContext(), "启用成功");
                getEnvs(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "启用失败：" + msg);
            }
        });

    }

    public void disableEnvs(List<String> ids) {
        ApiController.disableEnvironments(getClassName(), ids, new ApiController.BaseCallback() {
            @Override
            public void onSuccess(String msg) {
                layout_actions_back.performClick();
                ToastUnit.showShort(requireContext(), "禁用成功");
                getEnvs(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(requireContext(), "禁用失败");
            }
        });
    }

    public void showPopWindowMore() {
        if (popupWindowMore == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_fg_env_more, null, false);
            LinearLayout layout_add = view.findViewById(R.id.pop_fg_env_more_add);
            LinearLayout layout_actions = view.findViewById(R.id.pop_fg_env_more_actions);

            popupWindowMore = new PopupWindow(getContext());
            popupWindowMore.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowMore.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowMore.setContentView(view);
            popupWindowMore.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindowMore.setOutsideTouchable(true);
            popupWindowMore.setFocusable(true);

            layout_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowMore.dismiss();
                    showPopWindowEdit(null);
                }
            });

            layout_actions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowMore.dismiss();
                    showBar(BarType.ACTIONS);
                }
            });
        }
        popupWindowMore.showAsDropDown(layout_bar, 0, 0, Gravity.END);
    }

    public void showPopWindowEdit(Environment environment) {
        if (popupWindowEdit == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_fg_env_edit, null, false);

            layout_edit_type = view.findViewById(R.id.env_edit_type);
            layout_edit_name = view.findViewById(R.id.env_edit_name);
            layout_edit_value = view.findViewById(R.id.env_edit_value);
            layout_edit_remark = view.findViewById(R.id.env_edit_remark);
            Button layout_edit_cancel = view.findViewById(R.id.env_edit_cancel);
            layout_edit_save = view.findViewById(R.id.env_edit_save);

            popupWindowEdit = new PopupWindow(getContext());
            popupWindowEdit.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowEdit.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowEdit.setContentView(view);
            popupWindowEdit.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindowEdit.setOutsideTouchable(true);
            popupWindowEdit.setFocusable(true);
            popupWindowEdit.setAnimationStyle(R.style.anim_fg_task_pop_edit);

            layout_edit_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowEdit.dismiss();
                }
            });

            popupWindowEdit.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    BaseActivity baseActivity = (BaseActivity) getActivity();
                    baseActivity.setBackgroundAlpha(1.0f);
                }
            });
        }

        if (environment == null) {
            layout_edit_type.setText("新建变量");
            layout_edit_name.setText(null);
            layout_edit_value.setText(null);
            layout_edit_remark.setText(null);
        } else {
            layout_edit_type.setText("编辑变量");
            layout_edit_name.setText(environment.getName());
            layout_edit_value.setText(environment.getValue());
            layout_edit_remark.setText(environment.getRemarks());
        }

        layout_edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallManager.isRequesting(getClassName())) {
                    return;
                }

                WindowUnit.hideKeyboard(layout_root);

                String name = layout_edit_name.getText().toString().trim();
                String value = layout_edit_value.getText().toString().trim();
                String remarks = layout_edit_remark.getText().toString().trim();

                if (name.isEmpty()) {
                    ToastUnit.showShort(requireContext(), "变量名称不能为空");
                    return;
                }

                if (value.isEmpty()) {
                    ToastUnit.showShort(requireContext(), "变量值不能为空");
                    return;
                }

                List<Environment> environments = new ArrayList<>();
                Environment newEnv;
                newEnv = new Environment();
                newEnv.setName(name);
                newEnv.setValue(value);
                newEnv.setRemarks(remarks);
                environments.add(newEnv);
                if (environment == null) {
                    addEnvs(environments);
                } else {
                    newEnv.set_id(environment.get_id());
                    updateEnv(newEnv);
                }
            }
        });

        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setBackgroundAlpha(0.5f);
        popupWindowEdit.showAtLocation(layout_root, Gravity.CENTER, 0, 0);
    }

    public void showBar(BarType barType) {
        if (layout_bar_search.getVisibility() == View.VISIBLE) {
            WindowUnit.hideKeyboard(layout_root);
            layout_bar_search.setVisibility(View.INVISIBLE);
            currentSearchValue = "";
        }

        if (layout_bar_actions.getVisibility() == View.VISIBLE) {
            layout_bar_actions.setVisibility(View.INVISIBLE);
            envItemAdapter.setCheckState(false, -1);
            layout_actions_select.setChecked(false);
        }

        layout_bar_nav.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            layout_bar_nav.setVisibility(View.VISIBLE);
        } else if (barType == BarType.SEARCH) {
            layout_bar_search.setVisibility(View.VISIBLE);
        } else {
            layout_actions_select.setChecked(false);
            envItemAdapter.setCheckState(true, -1);
            layout_bar_actions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        if (!isSuccess && !CallManager.isRequesting(getClassName())) {
            getEnvs(currentSearchValue, QueryType.QUERY);
        }
        super.onResume();
    }

    @Override
    public void setMenuClickInterface(MenuClickInterface menuClickInterface) {
        this.menuClickInterface = menuClickInterface;
    }

    @Override
    public boolean onBackPressed() {
        if (layout_bar_search.getVisibility() == View.VISIBLE) {
            showBar(BarType.NAV);
            return true;
        } else if (layout_bar_actions.getVisibility() == View.VISIBLE) {
            showBar(BarType.NAV);
            return true;
        } else {
            return false;
        }
    }
}