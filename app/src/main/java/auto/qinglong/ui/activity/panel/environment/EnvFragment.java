package auto.qinglong.ui.activity.panel.environment;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import auto.base.util.TextUnit;
import auto.base.util.TimeUnit;
import auto.base.util.ToastUnit;
import auto.base.util.WindowUnit;
import auto.base.view.popup.LocalFileAdapter;
import auto.base.view.popup.PopEditObject;
import auto.base.view.popup.PopEditWindow;
import auto.base.view.popup.PopListWindow;
import auto.base.view.popup.PopMenuObject;
import auto.base.view.popup.PopMenuWindow;
import auto.base.view.popup.PopProgressWindow;
import auto.base.view.popup.PopupWindowBuilder;
import auto.qinglong.R;
import auto.qinglong.bean.panel.MoveInfo;
import auto.qinglong.bean.panel.QLEnvironment;
import auto.qinglong.net.NetManager;
import auto.qinglong.net.panel.v10.ApiController;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.utils.FileUtil;
import auto.qinglong.utils.WebUnit;

public class EnvFragment extends BaseFragment {
    public static String TAG = "EnvFragment";
    private String mCurrentSearchValue;
    private MenuClickListener mMenuClickListener;
    private EnvItemAdapter mAdapter;

    private LinearLayout uiBarNav;
    private ImageView uiNavMenu;
    private ImageView uiNavSearch;
    private ImageView uiNavMore;
    private LinearLayout uiBarSearch;
    private ImageView uiSearchBack;
    private EditText uiSearchValue;
    private ImageView uiSearchConfirm;
    private LinearLayout uiBarActions;
    private ImageView uiActionsBack;
    private CheckBox uiActionsSelect;
    private LinearLayout uiActionsEnable;
    private LinearLayout uiActionsDisable;
    private LinearLayout uiActionsDelete;

    private RecyclerView uiRecycler;
    private SmartRefreshLayout uiRefresh;

    private PopEditWindow uiPopEdit;
    private PopProgressWindow uiPopProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_env, null);

        uiBarNav = view.findViewById(R.id.env_bar_nav);
        uiNavMenu = view.findViewById(R.id.env_menu);
        uiNavSearch = view.findViewById(R.id.env_search);
        uiNavMore = view.findViewById(R.id.env_more);
        uiBarSearch = view.findViewById(R.id.env_bar_search);
        uiSearchBack = view.findViewById(R.id.env_bar_search_back);
        uiSearchValue = view.findViewById(R.id.env_bar_search_input);
        uiSearchConfirm = view.findViewById(R.id.env_bar_search_confirm);
        uiBarActions = view.findViewById(R.id.env_bar_actions);
        uiActionsBack = view.findViewById(R.id.env_bar_actions_back);
        uiActionsSelect = view.findViewById(R.id.env_bar_actions_select_all);
        uiActionsEnable = view.findViewById(R.id.env_bar_actions_enable);
        uiActionsDisable = view.findViewById(R.id.env_bar_actions_disable);
        uiActionsDelete = view.findViewById(R.id.env_bar_actions_delete);

        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiRecycler = view.findViewById(R.id.recycler_view);

        mAdapter = new EnvItemAdapter(requireContext());
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
    public void init() {
        ItemMoveHelper itemMoveHelper = new ItemMoveHelper(mAdapter);
        new ItemTouchHelper(itemMoveHelper).attachToRecyclerView(uiRecycler);

        //导航栏
        uiNavMenu.setOnClickListener(v -> mMenuClickListener.onMenuClick());

        //列表操作接口
        mAdapter.setItemInterface(new EnvItemAdapter.ItemActionListener() {
            @Override
            public void onEdit(QLEnvironment environment) {
                showPopWindowCommonEdit(environment);
            }

            @Override
            public void onMove(MoveInfo info) {
                netMoveEnvironment(info);
            }
        });

        //刷新
        uiRefresh.setOnRefreshListener(refreshLayout -> {
            if (uiBarSearch.getVisibility() != View.VISIBLE) {
                mCurrentSearchValue = null;
            }
            netGetEnvironments(mCurrentSearchValue, true);
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
            netGetEnvironments(mCurrentSearchValue, true);
        });

        //操作栏返回
        uiActionsBack.setOnClickListener(v -> onActionBarClose());

        //全选
        uiActionsSelect.setOnCheckedChangeListener((buttonView, isChecked) -> mAdapter.setAllChecked(isChecked));

        //删除
        uiActionsDelete.setOnClickListener(v -> {
            if (NetManager.isRequesting(getNetRequestID())) {
                return;
            }
            List<QLEnvironment> environments = mAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<String> ids = new ArrayList<>();
            for (QLEnvironment environment : environments) {
                ids.add(environment.getId());
            }
            netDeleteEnvironments(ids);
        });

        //禁用
        uiActionsDisable.setOnClickListener(v -> {
            if (NetManager.isRequesting(getNetRequestID())) {
                return;
            }
            List<QLEnvironment> environments = mAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<String> ids = new ArrayList<>();
            for (QLEnvironment environment : environments) {
                ids.add(environment.getId());
            }
            netDisableEnvironments(ids);
        });

        //启用
        uiActionsEnable.setOnClickListener(v -> {
            if (NetManager.isRequesting(getNetRequestID())) {
                return;
            }
            List<QLEnvironment> environments = mAdapter.getSelectedItems();
            if (environments.size() == 0) {
                ToastUnit.showShort(getString(R.string.tip_empty_select));
                return;
            }

            List<String> ids = new ArrayList<>();
            for (QLEnvironment environment : environments) {
                ids.add(environment.getId());
            }
            netEnableEnvironments(ids);
        });

    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.mMenuClickListener = menuClickListener;
    }

    @Override
    public boolean onDispatchTouchEvent() {
        if (uiPopProgress != null && uiPopProgress.isShowing()) {
            return true;
        }
        return super.onDispatchTouchEvent();
    }

    private void initData() {
        if (init || NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                netGetEnvironments(mCurrentSearchValue, true);
            }
        }, 1000);
    }

    private void showPopWindowMenu(View view) {
        PopMenuWindow popMenuWindow = new PopMenuWindow(view, Gravity.END);
        popMenuWindow.addItem(new PopMenuObject("add", "新建变量", R.drawable.ic_gray_add));
        popMenuWindow.addItem(new PopMenuObject("quickAdd", "快捷导入", R.drawable.ic_gray_flash_on));
        popMenuWindow.addItem(new PopMenuObject("localAdd", "本地导入", R.drawable.ic_gray_file));
        popMenuWindow.addItem(new PopMenuObject("remoteAdd", "远程导入", R.drawable.ic_gray_upload));
        popMenuWindow.addItem(new PopMenuObject("backup", "变量备份", R.drawable.ic_gray_download));
        popMenuWindow.addItem(new PopMenuObject("deleteMul", "变量去重", R.drawable.ic_gray_delete));
        popMenuWindow.addItem(new PopMenuObject("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));
        popMenuWindow.setOnActionListener(key -> {
            switch (key) {
                case "add":
                    showPopWindowCommonEdit(null);
                    break;
                case "quickAdd":
                    showPopWindowQuickEdit();
                    break;
                case "localAdd":
                    importData();
                    break;
                case "remoteAdd":
                    showPopWindowRemoteEdit();
                    break;
                case "deleteMul":
                    deduplicationData();
                    break;
                case "mulAction":
                    onActionBarOpen();
                    break;
                case "backup":
                    showPopWindowBackupEdit();
                    break;
                default:
                    break;
            }
            return true;
        });
        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showPopWindowCommonEdit(QLEnvironment environment) {
        uiPopEdit = new PopEditWindow("新建变量", "取消", "确定");
        PopEditObject itemName = new PopEditObject("name", null, "名称", "请输入变量名称");
        PopEditObject itemValue = new PopEditObject("value", null, "值", "请输入变量值");
        PopEditObject itemRemark = new PopEditObject("remark", null, "备注", "请输入备注(可选)");

        if (environment != null) {
            uiPopEdit.setTitle("编辑变量");
            itemName.setValue(environment.getName());
            itemValue.setValue(environment.getValue());
            itemRemark.setValue(environment.getRemarks());
        }

        uiPopEdit.addItem(itemName);
        uiPopEdit.addItem(itemValue);
        uiPopEdit.addItem(itemRemark);
        uiPopEdit.setActionListener(new PopEditWindow.OnActionListener() {
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
                    newEnv.setId(environment.getId());
                    netUpdateEnvironment(newEnv);
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

    private void showPopWindowQuickEdit() {
        uiPopEdit = new PopEditWindow("快捷导入", "取消", "确定");
        PopEditObject itemValue = new PopEditObject("values", null, "文本", "请输入文本");
        PopEditObject itemRemark = new PopEditObject("remark", null, "备注", "请输入备注(可选)");

        uiPopEdit.addItem(itemValue);
        uiPopEdit.addItem(itemRemark);
        uiPopEdit.setActionListener(new PopEditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String values = map.get("values");
                String remarks = map.get("remark");

                if (TextUnit.isEmpty(values)) {
                    ToastUnit.showShort("文本不能为空");
                    return false;
                }

                WindowUnit.hideKeyboard(uiPopEdit.getView());

                List<QLEnvironment> environments = QLEnvironment.parse(values, remarks);
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

        PopupWindowBuilder.buildEditWindow(requireActivity(), uiPopEdit);
    }

    private void showPopWindowRemoteEdit() {
        uiPopEdit = new PopEditWindow("远程导入", "取消", "确定");
        PopEditObject itemValue = new PopEditObject("url", null, "链接", "请输入远程地址");
        uiPopEdit.addItem(itemValue);
        uiPopEdit.setActionListener(new PopEditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String url = map.get("url");

                if (WebUnit.isInvalid(url)) {
                    ToastUnit.showShort(getString(R.string.tip_invalid_url));
                    return false;
                }
                WindowUnit.hideKeyboard(uiPopEdit.getView());
                netGetRemoteEnvironments(url);

                return true;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), uiPopEdit);
    }

    private void showPopWindowBackupEdit() {
        uiPopEdit = new PopEditWindow("变量备份", "取消", "确定");
        PopEditObject itemName = new PopEditObject("file_name", null, "文件名", "选填");

        uiPopEdit.addItem(itemName);

        uiPopEdit.setActionListener(new PopEditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String fileName = map.get("file_name");
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

    private void sortAndSetData(List<QLEnvironment> data) {
        for (int k = 0; k < data.size(); k++) {
            data.get(k).setRealIndex(k);
        }
        if (data.size() != 0) {
            Collections.sort(data);
            //设置同名序号
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
        mAdapter.setData(data);
    }

    private void deduplicationData() {
        List<String> ids = new ArrayList<>();
        Set<String> set = new HashSet<>();
        List<QLEnvironment> qlEnvironments = this.mAdapter.getData();
        for (QLEnvironment qlEnvironment : qlEnvironments) {
            String key = qlEnvironment.getName() + qlEnvironment.getValue();
            if (set.contains(key)) {
                ids.add(qlEnvironment.getId());
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

    private void backupData(String fileName) {
        if (FileUtil.isNeedRequestPermission()) {
            ToastUnit.showShort("请授予应用获取存储权限");
            FileUtil.requestPermission(requireActivity());
            return;
        }

        List<QLEnvironment> environments = mAdapter.getData();
        if (environments == null || environments.size() == 0) {
            ToastUnit.showShort("数据为空,无需备份");
            return;
        }

        JsonArray jsonArray = new JsonArray();
        for (QLEnvironment environment : environments) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", environment.getName());
            jsonObject.addProperty("value", environment.getValue());
            jsonObject.addProperty("remarks", environment.getRemarks());
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
            boolean result = FileUtil.save(FileUtil.getEnvPath(), fileName, content);
            if (result) {
                ToastUnit.showShort("备份成功：" + fileName);
            } else {
                ToastUnit.showShort("备份失败");
            }
        } catch (Exception e) {
            ToastUnit.showShort("备份失败：" + e.getMessage());
        }

    }

    private void importData() {
        if (FileUtil.isNeedRequestPermission()) {
            ToastUnit.showShort("请授予应用读写存储权限");
            FileUtil.requestPermission(requireActivity());
            return;
        }

        List<File> files = FileUtil.getFiles(FileUtil.getEnvPath(), (dir, name) -> name.endsWith(".json"));
        if (files.size() == 0) {
            ToastUnit.showShort("无本地备份数据");
            return;
        }

        PopListWindow<LocalFileAdapter> listWindow = new PopListWindow<>("选择文件");
        LocalFileAdapter fileAdapter = new LocalFileAdapter(getContext());
        fileAdapter.setData(files);
        listWindow.setAdapter(fileAdapter);

        PopupWindow popupWindow = PopupWindowBuilder.buildListWindow(requireActivity(), listWindow);

        fileAdapter.setListener(file -> {
            try {
                popupWindow.dismiss();
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
                Type type = new TypeToken<List<QLEnvironment>>() {
                }.getType();
                List<QLEnvironment> environments = new Gson().fromJson(stringBuilder.toString(), type);
                uiPopProgress.setTextAndShow("导入变量中...");
                netAddEnvironments(environments);
            } catch (Exception e) {
                ToastUnit.showShort("导入失败：" + e.getLocalizedMessage());
            }
        });
    }

    private void netGetEnvironments(String searchValue, boolean needTip) {
        if (NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.getEnvironments(getNetRequestID(), searchValue, new ApiController.NetGetEnvironmentsCallback() {
            @Override
            public void onSuccess(List<QLEnvironment> environments) {
                init = true;
                if (needTip) {
                    ToastUnit.showShort("加载成功：" + environments.size());
                }
                sortAndSetData(environments);
                uiRefresh.finishRefresh(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                uiRefresh.finishRefresh(false);
            }
        });
    }

    private void netUpdateEnvironment(QLEnvironment environment) {
        if (NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.updateEnvironment(getNetRequestID(), environment, new ApiController.NetEditEnvCallback() {
            @Override
            public void onSuccess(QLEnvironment environment) {
                uiPopEdit.dismiss();
                ToastUnit.showShort("更新成功");
                netGetEnvironments(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("更新失败：" + msg);
            }
        });
    }

    private void netAddEnvironments(List<QLEnvironment> environments) {
        if (NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.addEnvironment(getNetRequestID(), environments, new ApiController.NetGetEnvironmentsCallback() {
            @Override
            public void onSuccess(List<QLEnvironment> qlEnvironments) {
                if (uiPopEdit != null) {
                    uiPopEdit.dismiss();
                }
                if (uiPopProgress != null) {
                    uiPopProgress.dismiss();
                }
                ToastUnit.showShort("新建成功：" + environments.size());
                netGetEnvironments(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建失败：" + msg);
            }
        });
    }

    private void netDeleteEnvironments(List<String> ids) {
        if (NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.deleteEnvironments(getNetRequestID(), ids, new ApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                uiActionsBack.performClick();
                ToastUnit.showShort("删除成功：" + ids.size());
                netGetEnvironments(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("删除失败：" + msg);
            }
        });
    }

    private void netEnableEnvironments(List<String> ids) {
        if (NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.enableEnvironments(getNetRequestID(), ids, new ApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                uiActionsBack.performClick();
                ToastUnit.showShort("启用成功");
                netGetEnvironments(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("启用失败：" + msg);
            }
        });

    }

    private void netDisableEnvironments(List<String> ids) {
        if (NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.disableEnvironments(getNetRequestID(), ids, new ApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                uiActionsBack.performClick();
                ToastUnit.showShort("禁用成功");
                netGetEnvironments(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("禁用失败：" + msg);
            }
        });
    }

    private void netGetRemoteEnvironments(String url) {
        if (NetManager.isRequesting(getNetRequestID())) {
            return;
        }
        auto.qinglong.net.app.ApiController.getRemoteEnvironments(getNetRequestID(), url, new auto.qinglong.net.app.ApiController.NetRemoteEnvCallback() {

            @Override
            public void onSuccess(List<QLEnvironment> environments) {
                if (environments.size() == 0) {
                    ToastUnit.showShort("变量为空");
                } else {
                    netAddEnvironments(environments);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
            }
        });
    }

    private void netMoveEnvironment(MoveInfo info) {
        QLEnvironment fromObject = info.getFromObejct();
        QLEnvironment toObject = info.getToObject();
        int realFrom = info.getFromObejct().getRealIndex();
        int realTo = info.getToObject().getRealIndex();
        ApiController.moveEnvironment(getNetRequestID(), info.getFromObejct().getId(), realFrom, realTo, new ApiController.NetBaseCallback() {
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