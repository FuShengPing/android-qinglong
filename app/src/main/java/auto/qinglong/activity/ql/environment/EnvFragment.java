package auto.qinglong.activity.ql.environment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import auto.qinglong.R;
import auto.qinglong.bean.ql.QLEnvironment;
import auto.qinglong.network.http.ApiController;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.bean.ql.network.QLEnvironmentRes;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.network.http.RequestManager;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WebUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.popup.EditWindow;
import auto.qinglong.views.popup.EditWindowItem;
import auto.qinglong.views.popup.MiniMoreItem;
import auto.qinglong.views.popup.MiniMoreWindow;
import auto.qinglong.views.popup.PopupWindowManager;

public class EnvFragment extends BaseFragment {
    public static String TAG = "EnvFragment";
    private String currentSearchValue = "";
    private MenuClickListener menuClickListener;
    private EnvItemAdapter envItemAdapter;

    enum QueryType {QUERY, OTHER}

    enum BarType {NAV, SEARCH, MUL_ACTION}

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

    private SmartRefreshLayout layout_refresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_env, null);

        layout_root = view.findViewById(R.id.root);
        layout_bar = view.findViewById(R.id.env_top_bar);
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

        layout_refresh = view.findViewById(R.id.refreshLayout);
        RecyclerView layout_recycler = view.findViewById(R.id.recyclerView);

        envItemAdapter = new EnvItemAdapter(requireContext());
        layout_recycler.setAdapter(envItemAdapter);
        layout_recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFirst();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadFirst();
        }
    }

    private void loadFirst() {
        if (loadSuccessFlag || RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                netGetEnvironments(currentSearchValue, true);
            }
        }, 1000);
    }

    @Override
    public void init() {
        envItemAdapter.setItemInterface(new EnvItemAdapter.ItemActionListener() {
            @Override
            public void onEdit(QLEnvironment environment, int position) {
                showPopWindowCommonEdit(environment);
            }

            @Override
            public void onMulAction(QLEnvironment environment, int position) {
                envItemAdapter.setCheckState(true, -1);
                changeBar(BarType.MUL_ACTION);
            }
        });

        //导航栏
        layout_nav_menu.setOnClickListener(v -> menuClickListener.onMenuClick());

        //刷新控件//
        //初始设置处于刷新状态
        layout_refresh.autoRefreshAnimationOnly();
        layout_refresh.setOnRefreshListener(refreshLayout -> netGetEnvironments(currentSearchValue, true));

        //更多操作
        layout_nav_more.setOnClickListener(v -> showPopWindowMiniMore());

        //搜索栏进入
        layout_nav_search.setOnClickListener(v -> {
            layout_search_value.setText(currentSearchValue);
            changeBar(BarType.SEARCH);
        });

        //搜索栏确定
        layout_search_confirm.setOnClickListener(v -> {
            String value = layout_search_value.getText().toString().trim();
            if (!value.isEmpty()) {
                currentSearchValue = value;
                WindowUnit.hideKeyboard(layout_search_value);
                netGetEnvironments(currentSearchValue, true);
            }
        });

        //搜索栏返回
        layout_search_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //动作栏返回
        layout_actions_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //全选
        layout_actions_select.setOnCheckedChangeListener((buttonView, isChecked) -> envItemAdapter.setAllChecked(isChecked));

        //删除
        layout_actions_delete.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }
            List<QLEnvironment> environments = envItemAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<String> ids = new ArrayList<>();
            for (QLEnvironment environment : environments) {
                ids.add(environment.get_id());
            }
            netDeleteEnvironments(ids);
        });

        //禁用
        layout_actions_disable.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }
            List<QLEnvironment> environments = envItemAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<String> ids = new ArrayList<>();
            for (QLEnvironment environment : environments) {
                ids.add(environment.get_id());
            }
            netDisableEnvironments(ids);
        });

        //启用
        layout_actions_enable.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }
            List<QLEnvironment> environments = envItemAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<String> ids = new ArrayList<>();
            for (QLEnvironment environment : environments) {
                ids.add(environment.get_id());
            }
            netEnableEnvironments(ids);
        });

    }

    private void netGetEnvironments(String searchValue, boolean needTip) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.getEnvironments(getNetRequestID(), searchValue, new QLApiController.GetEnvironmentsCallback() {
            @Override
            public void onSuccess(QLEnvironmentRes res) {
                loadSuccessFlag = true;
                if (needTip) {
                    ToastUnit.showShort("加载成功：" + res.getData().size());
                }
                sortAndSetData(res.getData());
                layout_refresh.finishRefresh(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                layout_refresh.finishRefresh(false);
            }
        });
    }

    public void netUpdateEnvironment(QLEnvironment environment) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.updateEnvironment(getNetRequestID(), environment, new QLApiController.EditEnvCallback() {
            @Override
            public void onSuccess(QLEnvironment data) {
                if (popupWindowEdit != null && popupWindowEdit.isShowing()) {
                    popupWindowEdit.dismiss();
                }
                ToastUnit.showShort("更新成功");
                netGetEnvironments(currentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("更新失败：" + msg);
            }
        });
    }

    public void netAddEnvironments(List<QLEnvironment> environments) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.addEnvironment(getNetRequestID(), environments, new QLApiController.GetEnvironmentsCallback() {
            @Override
            public void onSuccess(QLEnvironmentRes res) {
                if (popupWindowEdit != null && popupWindowEdit.isShowing()) {
                    popupWindowEdit.dismiss();
                }
                ToastUnit.showShort("新建成功：" + environments.size());
                netGetEnvironments(currentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建失败：" + msg);
            }
        });
    }

    public void netDeleteEnvironments(List<String> ids) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.deleteEnvironments(getNetRequestID(), ids, new QLApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                layout_actions_back.performClick();
                ToastUnit.showShort("删除成功：" + ids.size());
                netGetEnvironments(currentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("删除失败：" + msg);
            }
        });
    }

    public void netEnableEnvironments(List<String> ids) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.enableEnvironments(getNetRequestID(), ids, new QLApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                layout_actions_back.performClick();
                ToastUnit.showShort("启用成功");
                netGetEnvironments(currentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("启用失败：" + msg);
            }
        });

    }

    public void netDisableEnvironments(List<String> ids) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.disableEnvironments(getNetRequestID(), ids, new QLApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                layout_actions_back.performClick();
                ToastUnit.showShort("禁用成功");
                netGetEnvironments(currentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("禁用失败：" + msg);
            }
        });
    }

    public void netGetRemoteEnvironments(String baseUrl, String path) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.getRemoteEnvironments(getNetRequestID(), baseUrl, path, new ApiController.RemoteEnvCallback() {

            @Override
            public void onSuccess(List<QLEnvironment> environments) {
                LogUnit.log("size：" + environments.size());
//                netAddEnvironments(res.getEnvs());
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
            }
        });
    }

    public void sortAndSetData(List<QLEnvironment> data) {
        if (data.size() != 0) {
            Collections.sort(data);
            //设置序号
            int size = data.size();
            int current = 0;
            int index = 1;
            while (true) {
                data.get(current).setIndex(index);
                if (current < size - 1) {
                    if (data.get(current).getName().equals(data.get(current + 1).getName())) {
                        index += 1;
                    } else {
                        index = 1;
                    }
                } else {
                    break;
                }
                current += 1;
            }
        }
        envItemAdapter.setData(data);
    }

    public void compareAndDeleteData() {
        List<String> ids = new ArrayList<>();
        Set<String> set = new HashSet<>();
        List<QLEnvironment> qlEnvironments = this.envItemAdapter.getData();
        for (QLEnvironment qlEnvironment : qlEnvironments) {
            String key = qlEnvironment.getName() + qlEnvironment.getValue();
            if (set.contains(key)) {
                ids.add(qlEnvironment.get_id());
            } else {
                set.add(key);
            }
        }
        if (ids.size() == 0) {
            ToastUnit.showShort("无重复变量");
        } else {
            netDeleteEnvironments(ids);
        }
    }

    public void showPopWindowMiniMore() {
        MiniMoreWindow miniMoreWindow = new MiniMoreWindow();
        miniMoreWindow.addItem(new MiniMoreItem("add", "新建变量", R.drawable.ic_add_gray));
        miniMoreWindow.addItem(new MiniMoreItem("quickAdd", "快捷导入", R.drawable.ic_flash_on_gray));
        miniMoreWindow.addItem(new MiniMoreItem("remoteAdd", "远程导入", R.drawable.ic_cloud_download));
        miniMoreWindow.addItem(new MiniMoreItem("deleteMul", "变量去重", R.drawable.ic_delete_gray));
        miniMoreWindow.addItem(new MiniMoreItem("mulAction", "批量操作", R.drawable.ic_mul_action_gray));
        miniMoreWindow.setOnActionListener(key -> {
            switch (key) {
                case "add":
                    showPopWindowCommonEdit(null);
                    break;
                case "quickAdd":
                    showPopWindowQuickEdit();
                    break;
                case "remoteAdd":
                    showPopWindowRemoteEdit();
                    break;
                case "deleteMul":
                    compareAndDeleteData();
                    break;
                case "mulAction":
                    changeBar(BarType.MUL_ACTION);
                    break;
                default:
                    break;
            }
            return true;
        });
        PopupWindowManager.buildMiniMoreWindow(requireActivity(), miniMoreWindow, layout_bar, Gravity.END);
    }

    private void showPopWindowCommonEdit(QLEnvironment environment) {
        EditWindow editWindow = new EditWindow("新建变量", "取消", "确定");
        EditWindowItem itemName = new EditWindowItem("name", null, "名称", "请输入变量名称");
        EditWindowItem itemValue = new EditWindowItem("value", null, "值", "请输入变量值");
        EditWindowItem itemRemark = new EditWindowItem("remark", null, "备注", "请输入备注(可选)");

        if (environment != null) {
            editWindow.setTitle("编辑变量");
            itemName.setValue(environment.getName());
            itemValue.setValue(environment.getValue());
            itemRemark.setValue(environment.getRemarks());
        }

        editWindow.addItem(itemName);
        editWindow.addItem(itemValue);
        editWindow.addItem(itemRemark);
        editWindow.setActionListener(new EditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String name = map.get("name");
                String value = map.get("value");
                String remarks = map.get("remark");

                if (TextUnit.isEmpty(name)) {
                    ToastUnit.showShort("变量名称不能为空");
                    return false;
                }
                if (TextUnit.isEmpty(value)) {
                    ToastUnit.showShort("变量值不能为空");
                    return false;
                }

                WindowUnit.hideKeyboard(layout_root);

                List<QLEnvironment> environments = new ArrayList<>();
                QLEnvironment newEnv;
                newEnv = new QLEnvironment();
                newEnv.setName(name);
                newEnv.setValue(value);
                newEnv.setRemarks(remarks);
                environments.add(newEnv);
                if (environment == null) {
                    netAddEnvironments(environments);
                } else {
                    newEnv.set_id(environment.get_id());
                    netUpdateEnvironment(newEnv);
                }

                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        popupWindowEdit = PopupWindowManager.buildEditWindow(requireActivity(), editWindow);
    }

    private void showPopWindowQuickEdit() {
        EditWindow editWindow = new EditWindow("快捷导入", "取消", "确定");
        EditWindowItem itemValue = new EditWindowItem("values", null, "文本", "请输入文本");
        EditWindowItem itemRemark = new EditWindowItem("remark", null, "备注", "请输入备注(可选)");

        editWindow.addItem(itemValue);
        editWindow.addItem(itemRemark);
        editWindow.setActionListener(new EditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String values = map.get("values");
                String remarks = map.get("remark");

                if (TextUnit.isEmpty(values)) {
                    ToastUnit.showShort("文本不能为空");
                    return false;
                }

                WindowUnit.hideKeyboard(layout_root);

                List<QLEnvironment> environments = QLEnvironment.parseExport(values, remarks);
                if (environments.size() == 0) {
                    ToastUnit.showShort("提取变量失败");
                } else {
                    netAddEnvironments(environments);
                }
                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        popupWindowEdit = PopupWindowManager.buildEditWindow(requireActivity(), editWindow);
    }

    private void showPopWindowRemoteEdit() {
        EditWindow editWindow = new EditWindow("远程导入", "取消", "确定");
        EditWindowItem itemValue = new EditWindowItem("url", null, "链接", "请输入远程地址");
        editWindow.addItem(itemValue);
        editWindow.setActionListener(new EditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String url = map.get("url");

                if (TextUnit.isEmpty(url)) {
                    ToastUnit.showShort("地址不能为空");
                    return false;
                }

                if (WebUnit.isValidUrl(url)) {
                    WindowUnit.hideKeyboard(layout_actions_back);
                    String baseUrl = WebUnit.getHost(url) + "/";
                    String path = WebUnit.getPath(url, "");
                    netGetRemoteEnvironments(baseUrl, path);
                } else {
                    ToastUnit.showShort("地址不合法");
                }

                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        popupWindowEdit = PopupWindowManager.buildEditWindow(requireActivity(), editWindow);
    }

    public void changeBar(BarType barType) {
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
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    @Override
    public boolean onBackPressed() {
        if (layout_bar_search.getVisibility() == View.VISIBLE) {
            changeBar(BarType.NAV);
            return true;
        } else if (layout_bar_actions.getVisibility() == View.VISIBLE) {
            changeBar(BarType.NAV);
            return true;
        } else {
            return false;
        }
    }
}