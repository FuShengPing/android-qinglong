package auto.panel.ui.activity.panel.environment;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import auto.base.ui.popup.MenuPopupObject;
import auto.base.util.TextUnit;
import auto.base.util.TimeUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.base.ui.popup.LocalFileAdapter;
import auto.base.ui.popup.EditPopupObject;
import auto.base.ui.popup.EditPopupWindow;
import auto.base.ui.popup.ListPopupWindow;
import auto.base.ui.popup.MenuPopupWindow;
import auto.base.ui.popup.ProgressPopupWindow;
import auto.base.ui.popup.PopupWindowBuilder;
import auto.panel.R;
import auto.panel.bean.panel.Environment;
import auto.panel.bean.panel.MoveInfo;
import auto.panel.bean.panel.QLEnvironment;
import auto.panel.database.sp.PanelPreference;
import auto.panel.net.NetManager;
import auto.panel.net.panel.v10.ApiController;
import auto.panel.ui.adapter.EnvironmentItemAdapter;
import auto.panel.ui.fragment.BaseFragment;
import auto.panel.utils.FileUtil;

public class EnvironmentFragment extends BaseFragment {
    public static String TAG = "EnvironmentFragment";
    private String mCurrentSearchValue;
    private MenuClickListener mMenuClickListener;
    private EnvironmentItemAdapter mAdapter;

    private LinearLayout uiBarNav;
    private ImageView uiNavMenu;
    private ImageView uiNavSearch;
    private ImageView uiNavMore;
    private LinearLayout uiBarSearch;
    private ImageView uiSearchBack;
    private EditText uiSearchValue;
    private ImageView uiSearchConfirm;
    private LinearLayout uiBarActions;
    private ImageView uiActionBack;
    private CheckBox uiActionsSelect;
    private LinearLayout uiActionsEnable;
    private LinearLayout uiActionsDisable;
    private LinearLayout uiActionsDelete;

    private RecyclerView uiRecycler;
    private SmartRefreshLayout uiRefresh;

    private EditPopupWindow uiPopEdit;
    private ProgressPopupWindow uiPopProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_env, null);

        uiBarNav = view.findViewById(R.id.env_bar_nav);
        uiNavMenu = view.findViewById(R.id.env_menu);
        uiNavSearch = view.findViewById(R.id.env_search);
        uiNavMore = view.findViewById(R.id.env_more);
        uiBarSearch = view.findViewById(R.id.env_bar_search);
        uiSearchBack = view.findViewById(R.id.env_bar_search_back);
        uiSearchValue = view.findViewById(R.id.env_bar_search_input);
        uiSearchConfirm = view.findViewById(R.id.env_bar_search_confirm);
        uiBarActions = view.findViewById(R.id.env_bar_actions);
        uiActionBack = view.findViewById(R.id.env_bar_actions_back);
        uiActionsSelect = view.findViewById(R.id.env_bar_actions_select_all);
        uiActionsEnable = view.findViewById(R.id.env_bar_actions_enable);
        uiActionsDisable = view.findViewById(R.id.env_bar_actions_disable);
        uiActionsDelete = view.findViewById(R.id.env_bar_actions_delete);

        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiRecycler = view.findViewById(R.id.recycler_view);

        mAdapter = new EnvironmentItemAdapter(requireContext());
        uiRecycler.setAdapter(mAdapter);
        uiRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        Objects.requireNonNull(uiRecycler.getItemAnimator()).setChangeDuration(0);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    @Override
    public boolean onDispatchBackKey() {
        if (uiBarSearch.getVisibility() == View.VISIBLE) {
            onSearchBarClose();
            return true;
        } else if (uiBarActions.getVisibility() == View.VISIBLE) {
            onActionBarClose();
            return true;
        } else {
            return false;
        }
    }

    private void onSearchBarOpen() {
        uiBarNav.setVisibility(View.INVISIBLE);
        uiBarSearch.setVisibility(View.VISIBLE);
    }

    private void onSearchBarClose() {
        uiBarSearch.setVisibility(View.INVISIBLE);
        mCurrentSearchValue = "";
        uiSearchValue.setText("");
        uiBarNav.setVisibility(View.VISIBLE);
    }

    private void onActionBarOpen() {
        uiBarNav.setVisibility(View.INVISIBLE);
        mAdapter.setCheckState(true);
        uiBarActions.setVisibility(View.VISIBLE);
    }

    private void onActionBarClose() {
        uiBarActions.setVisibility(View.INVISIBLE);
        uiActionsSelect.setChecked(false);
        mAdapter.setCheckState(false);
        uiBarNav.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onDispatchTouchEvent() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            return true;
        }
        return super.onDispatchTouchEvent();
    }

    @Override
    public void init() {
        //导航栏
        uiNavMenu.setOnClickListener(v -> mMenuClickListener.onMenuClick());

        //列表项目移动
//        ItemMoveHelper itemMoveHelper = new ItemMoveHelper(mAdapter);
//        new ItemTouchHelper(itemMoveHelper).attachToRecyclerView(uiRecycler);

        //列表操作接口
        mAdapter.setItemInterface(new EnvironmentItemAdapter.ItemActionListener() {
            @Override
            public void onEdit(Environment environment) {
                showPopWindowEdit(environment);
            }

            @Override
            public void onMove(MoveInfo info) {
                moveEnvironment(info);
            }
        });

        //刷新
        uiRefresh.setOnRefreshListener(refreshLayout -> {
            if (uiBarSearch.getVisibility() != View.VISIBLE) {
                mCurrentSearchValue = null;
            }
            getEnvironments(mCurrentSearchValue);
        });

        //更多操作
        uiNavMore.setOnClickListener(this::showPopWindowMenu);

        //搜索栏进入
        uiNavSearch.setOnClickListener(v -> onSearchBarOpen());

        //搜索栏返回
        uiSearchBack.setOnClickListener(v -> onSearchBarClose());

        //搜索栏确定
        uiSearchConfirm.setOnClickListener(v -> {
            mCurrentSearchValue = uiSearchValue.getText().toString().trim();
            WindowUnit.hideKeyboard(uiSearchValue);
            getEnvironments(mCurrentSearchValue);
        });

        //操作栏返回
        uiActionBack.setOnClickListener(v -> onActionBarClose());

        //全选
        uiActionsSelect.setOnCheckedChangeListener((buttonView, isChecked) -> mAdapter.setAllChecked(isChecked));

        //删除
        uiActionsDelete.setOnClickListener(v -> {
            List<Environment> environments = mAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<Object> ids = new ArrayList<>();
            for (Environment environment : environments) {
                ids.add(environment.getKey());
            }
            deleteEnvironments(ids);
        });

        //启用
        uiActionsEnable.setOnClickListener(v -> {
            if (NetManager.isRequesting(getNetRequestID())) {
                return;
            }
            List<Environment> environments = mAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<Object> ids = new ArrayList<>();
            for (Environment environment : environments) {
                ids.add(environment.getKey());
            }
            enableEnvironments(ids);
        });

        //禁用
        uiActionsDisable.setOnClickListener(v -> {
            List<Environment> environments = mAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<Object> ids = new ArrayList<>();
            for (Environment environment : environments) {
                ids.add(environment.getKey());
            }
            disableEnvironments(ids);
        });
    }

    private void initData() {
        if (init || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getEnvironments(mCurrentSearchValue);
            }
        }, 1000);
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.mMenuClickListener = menuClickListener;
    }

    private void showPopWindowMenu(View view) {
        MenuPopupWindow popMenuWindow = new MenuPopupWindow(view, Gravity.END);
        popMenuWindow.addItem(new MenuPopupObject("add", "新建变量", R.drawable.ic_gray_add));
        popMenuWindow.addItem(new MenuPopupObject("quickAdd", "快捷导入", R.drawable.ic_gray_flash_on));
        popMenuWindow.addItem(new MenuPopupObject("localAdd", "本地导入", R.drawable.ic_gray_file));
        popMenuWindow.addItem(new MenuPopupObject("backup", "变量备份", R.drawable.ic_gray_download));
        popMenuWindow.addItem(new MenuPopupObject("deleteMul", "变量去重", R.drawable.ic_gray_delete));
        popMenuWindow.addItem(new MenuPopupObject("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));
        popMenuWindow.setOnActionListener(key -> {
            switch (key) {
                case "add":
                    showPopWindowEdit(null);
                    break;
                case "quickAdd":
                    showPopWindowQuickImport();
                    break;
                case "localAdd":
                    showPopWindowSelectFile();
                    break;
                case "deleteMul":
                    deduplicationData();
                    break;
                case "mulAction":
                    onActionBarOpen();
                    break;
                case "backup":
                    showPopWindowBackup();
                    break;
                default:
                    break;
            }
            return true;
        });
        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showPopWindowEdit(Environment environment) {
        uiPopEdit = new EditPopupWindow("新建变量", "取消", "确定");
        EditPopupObject itemName = new EditPopupObject("name", null, "名称", "请输入变量名称");
        EditPopupObject itemValue = new EditPopupObject("value", null, "值", "请输入变量值");
        EditPopupObject itemRemark = new EditPopupObject("remark", null, "备注", "请输入备注(可选)");

        uiPopEdit.addItem(itemName);
        uiPopEdit.addItem(itemValue);
        uiPopEdit.addItem(itemRemark);

        if (environment != null) {
            uiPopEdit.setTitle("编辑变量");
            itemName.setValue(environment.getName());
            itemValue.setValue(environment.getValue());
            itemRemark.setValue(environment.getRemark());
        }

        uiPopEdit.setActionListener(new EditPopupWindow.OnActionListener() {
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

                WindowUnit.hideKeyboard(uiPopEdit.getView());

                List<Environment> environments = new ArrayList<>();
                Environment newEnv = new Environment();
                newEnv.setName(name);
                newEnv.setValue(value);
                newEnv.setRemark(remarks);

                if (environment == null) {
                    environments.add(newEnv);
                    addEnvironments(environments);
                } else {
                    newEnv.setKey(environment.getKey());
                    updateEnvironment(newEnv);
                }

                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), uiPopEdit);
    }

    private void showPopWindowQuickImport() {
        uiPopEdit = new EditPopupWindow("快捷导入", "取消", "确定");
        EditPopupObject itemValue = new EditPopupObject("values", null, "文本", "请输入文本");
        EditPopupObject itemRemark = new EditPopupObject("remark", null, "备注", "请输入备注(可选)");

        uiPopEdit.addItem(itemValue);
        uiPopEdit.addItem(itemRemark);
        uiPopEdit.setActionListener(new EditPopupWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String values = map.get("values");
                String remarks = map.get("remark");

                if (TextUnit.isEmpty(values)) {
                    ToastUnit.showShort("文本不能为空");
                    return false;
                }

                WindowUnit.hideKeyboard(uiPopEdit.getView());

                List<Environment> environments = Environment.parse(values, remarks);
                if (environments.size() == 0) {
                    ToastUnit.showShort("提取变量失败");
                } else {
                    addEnvironments(environments);
                }
                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), uiPopEdit);
    }

    private void showPopWindowBackup() {
        if (!FileUtil.checkStoragePermission()) {
            ToastUnit.showShort("请授予应用获取存储权限");
            FileUtil.requestStoragePermission(requireActivity());
            return;
        }

        uiPopEdit = new EditPopupWindow("变量备份", "取消", "确定");
        EditPopupObject itemName = new EditPopupObject("fileName", null, "文件名", "选填");

        uiPopEdit.addItem(itemName);

        uiPopEdit.setActionListener(new EditPopupWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String fileName = map.get("fileName");
                WindowUnit.hideKeyboard(uiPopEdit.getView());
                backupData(fileName);
                return true;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), uiPopEdit);
    }

    private void showPopWindowSelectFile() {
        if (!FileUtil.checkStoragePermission()) {
            ToastUnit.showShort("请授予应用读写存储权限");
            FileUtil.requestStoragePermission(requireActivity());
            return;
        }

        List<File> files = FileUtil.getFiles(FileUtil.getEnvironmentPath(), (dir, name) -> name.endsWith(".json"));
        if (files.size() == 0) {
            ToastUnit.showShort("无本地备份数据");
            return;
        }

        ListPopupWindow<LocalFileAdapter> listWindow = new ListPopupWindow<>("选择文件");
        LocalFileAdapter fileAdapter = new LocalFileAdapter(getContext());
        fileAdapter.setData(files);
        listWindow.setAdapter(fileAdapter);

        PopupWindow popupWindow = PopupWindowBuilder.buildListWindow(requireActivity(), listWindow);

        fileAdapter.setListener(file -> {
            popupWindow.dismiss();
            importData(file);
        });
    }

    private void dismissPopWindowEdit() {
        if (uiPopEdit != null) {
            uiPopEdit.dismiss();
        }
    }

    private void dismissPopWindowProgress() {
        if (uiPopProgress != null) {
            uiPopProgress.dismiss();
        }
    }

    private void backupData(String fileName) {
        List<Environment> environments = mAdapter.getData();
        if (environments == null || environments.size() == 0) {
            ToastUnit.showShort("数据为空,无需备份");
            return;
        }

        JsonArray jsonArray = new JsonArray();
        for (Environment environment : environments) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", environment.getName());
            jsonObject.addProperty("value", environment.getValue());
            jsonObject.addProperty("remark", environment.getRemark());
            jsonArray.add(jsonObject);
        }

        if (TextUnit.isFull(fileName)) {
            fileName += ".json";
        } else {
            fileName = TimeUnit.formatDatetimeC() + ".json";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String content = gson.toJson(jsonArray);

        try {
            boolean result = FileUtil.save(FileUtil.getEnvironmentPath(), fileName, content);
            if (result) {
                ToastUnit.showShort("备份成功：" + fileName);
            } else {
                ToastUnit.showShort("备份失败");
            }
        } catch (Exception e) {
            ToastUnit.showShort("备份失败：" + e.getMessage());
        }

    }

    private void importData(File file) {
        try {
            if (uiPopProgress == null) {
                uiPopProgress = PopupWindowBuilder.buildProgressWindow(requireActivity(), null);
            }

            uiPopProgress.setTextAndShow("加载文件中...");
            BufferedReader bufferedInputStream = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedInputStream.readLine()) != null) {
                stringBuilder.append(line);
            }

            uiPopProgress.setTextAndShow("解析文件中...");
            Environment[] environments = new Gson().fromJson(stringBuilder.toString(), Environment[].class);

            new Thread(() -> {
                uiPopProgress.setTextAndShow("导入中...");
                int index = 0;
                int success = 0;
                int total = environments.length;

                for (Environment environment : environments) {
                    boolean result = auto.panel.net.panel.ApiController.addEnvironmentSync(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), environment);
                    uiPopProgress.setTextAndShow("导入中... " + index + "/" + total);
                    index++;
                    if (result) {
                        success++;
                    }
                }

                dismissPopWindowProgress();
                ToastUnit.showShort("导入完成 " + success + "/" + total);
                getEnvironments(mCurrentSearchValue);
            }).start();
        } catch (Exception e) {
            dismissPopWindowProgress();
            ToastUnit.showShort("导入失败：" + e.getLocalizedMessage());
        }
    }

    private void deduplicationData() {
        List<Object> ids = new ArrayList<>();
        Set<Object> set = new HashSet<>();
        List<Environment> environments = this.mAdapter.getData();
        for (Environment environment : environments) {
            String key = environment.getName() + environment.getValue();
            if (set.contains(key)) {
                ids.add(environment.getKey());
            } else {
                set.add(key);
            }
        }
        if (ids.size() == 0) {
            ToastUnit.showShort("无重复变量");
        } else {
            deleteEnvironments(ids);
        }
    }

    private void getEnvironments(String searchValue) {
        auto.panel.net.panel.ApiController.getEnvironments(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), searchValue, new auto.panel.net.panel.ApiController.EnvironmentListCallBack() {
            @Override
            public void onSuccess(List<Environment> environments) {
                init = true;
                mAdapter.setData(environments);
                uiRefresh.finishRefresh(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                uiRefresh.finishRefresh(false);
            }
        });
    }

    private void enableEnvironments(List<Object> ids) {
        auto.panel.net.panel.ApiController.enableEnvironments(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), ids, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                uiActionBack.performClick();
                ToastUnit.showShort("启用成功");
                getEnvironments(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("启用失败：" + msg);
            }
        });
    }

    private void disableEnvironments(List<Object> ids) {
        auto.panel.net.panel.ApiController.disableEnvironments(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), ids, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                uiActionBack.performClick();
                ToastUnit.showShort("禁用成功");
                getEnvironments(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("禁用失败：" + msg);
            }
        });
    }

    private void addEnvironments(List<Environment> environments) {
        auto.panel.net.panel.ApiController.addEnvironments(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), environments, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                dismissPopWindowEdit();
                ToastUnit.showShort("新建成功");
                getEnvironments(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建失败：" + msg);
            }
        });
    }

    private void updateEnvironment(Environment environment) {
        auto.panel.net.panel.ApiController.updateEnvironment(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), environment, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                uiPopEdit.dismiss();
                ToastUnit.showShort("更新成功");
                getEnvironments(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("更新失败：" + msg);
            }
        });
    }

    private void deleteEnvironments(List<Object> ids) {
        auto.panel.net.panel.ApiController.deleteEnvironments(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), ids, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                uiActionBack.performClick();
                ToastUnit.showShort("删除成功");
                getEnvironments(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("删除失败：" + msg);
            }
        });
    }

    private void moveEnvironment(MoveInfo info) {
        QLEnvironment fromObject = info.getFromObject();
        QLEnvironment toObject = info.getToObject();
        int realFrom = info.getFromObject().getRealIndex();
        int realTo = info.getToObject().getRealIndex();
        ApiController.moveEnvironment(getNetRequestID(), info.getFromObject().getId(), realFrom, realTo, new ApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort(getString(R.string.tip_move_success));
                //交换真实序号
                fromObject.setRealIndex(realTo);
                toObject.setRealIndex(realFrom);
                //同名变量交换同名序号 注：调用notifyItemChanged更新会显示异常
                if (fromObject.getName().equals(toObject.getName())) {
                    int index = fromObject.getIndex();
                    fromObject.setIndex(toObject.getIndex());
                    fromObject.resetFormatName();
                    toObject.setIndex(index);
                    toObject.resetFormatName();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.tip_move_failure_header) + msg);
                mAdapter.onItemMove(info.getToIndex(), info.getFromIndex());
            }
        });
    }
}