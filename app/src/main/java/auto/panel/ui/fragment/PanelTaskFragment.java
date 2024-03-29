package auto.panel.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import auto.base.ui.popup.EditItem;
import auto.base.ui.popup.EditPopupWindow;
import auto.base.ui.popup.ListPopupWindow;
import auto.base.ui.popup.LocalFileAdapter;
import auto.base.ui.popup.MenuItem;
import auto.base.ui.popup.MenuPopupWindow;
import auto.base.ui.popup.PopupWindowBuilder;
import auto.base.ui.popup.ProgressPopupWindow;
import auto.base.util.WindowUnit;
import auto.panel.R;
import auto.panel.bean.panel.PanelTask;
import auto.panel.net.panel.ApiController;
import auto.panel.ui.activity.TextEditorActivity;
import auto.panel.ui.adapter.PanelTaskItemAdapter;
import auto.panel.utils.CronUnit;
import auto.panel.utils.FileUtil;
import auto.panel.utils.TextUnit;
import auto.panel.utils.TimeUnit;
import auto.panel.utils.ToastUnit;

public class PanelTaskFragment extends BaseFragment {
    public static String TAG = "PanelTaskFragment";
    public static String NAME = "任务管理";

    private String mCurrentSearchValue;
    private MenuClickListener mMenuClickListener;
    private PanelTaskItemAdapter mAdapter;
    private boolean mHasMore = false;
    private int mPageNo = 1;
    private int mPageSize = 50;

    //主导航栏
    private LinearLayout uiBarNav;
    private ImageView uiNavMenu;
    private ImageView uiNavSearch;
    private ImageView uiNavMore;
    //搜索导航栏
    private LinearLayout uiBarSearch;
    private ImageView uiSearchBack;
    private ImageView uiSearchConfirm;
    private EditText uiSearchValue;
    //操作导航栏
    private LinearLayout uiBarActions;
    private ImageView uiActionsBack;
    private CheckBox uiActionsSelect;
    private HorizontalScrollView uiActionsScroll;
    private LinearLayout uiActionsRun;
    private LinearLayout uiActionsStop;
    private LinearLayout uiActionsPin;
    private LinearLayout uiActionsUnpin;
    private LinearLayout uiActionsEnable;
    private LinearLayout uiActionsDisable;
    private LinearLayout uiActionsDelete;
    //布局控件
    private RecyclerView uiRecycler;
    private SmartRefreshLayout uiRefresh;

    private EditPopupWindow uiPopEdit;
    private ProgressPopupWindow uiPopProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.panel_fragment_task, null);

        uiBarNav = view.findViewById(R.id.task_bar_nav);
        uiNavMenu = view.findViewById(R.id.task_bar_nav_menu);
        uiNavSearch = view.findViewById(R.id.task_bar_nav_search);
        uiNavMore = view.findViewById(R.id.task_bar_nav_more);

        uiBarSearch = view.findViewById(R.id.task_bar_search);
        uiSearchBack = view.findViewById(R.id.task_bar_search_back);
        uiSearchValue = view.findViewById(R.id.task_bar_search_input);
        uiSearchConfirm = view.findViewById(R.id.task_bar_search_confirm);

        uiBarActions = view.findViewById(R.id.task_bar_actions);
        uiActionsSelect = view.findViewById(R.id.task_bar_actions_select_all);
        uiActionsScroll = view.findViewById(R.id.task_bar_actions_scroll);
        uiActionsBack = view.findViewById(R.id.task_bar_actions_back);
        uiActionsRun = view.findViewById(R.id.task_bar_actions_run);
        uiActionsStop = view.findViewById(R.id.task_bar_actions_stop);
        uiActionsPin = view.findViewById(R.id.task_bar_actions_pinned);
        uiActionsUnpin = view.findViewById(R.id.task_bar_actions_unpinned);
        uiActionsEnable = view.findViewById(R.id.task_bar_actions_enable);
        uiActionsDisable = view.findViewById(R.id.task_bar_actions_disable);
        uiActionsDelete = view.findViewById(R.id.task_bar_actions_delete);

        uiRefresh = view.findViewById(R.id.refresh_layout);
        uiRecycler = view.findViewById(R.id.recycler_view);

        mAdapter = new PanelTaskItemAdapter(requireContext());
        uiRecycler.setAdapter(mAdapter);
        uiRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //取消更新动画，避免刷新闪烁
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
        if (uiBarActions.getVisibility() == View.VISIBLE) {
            onActionBarClose();
            return true;
        } else if (uiBarSearch.getVisibility() == View.VISIBLE) {
            onSearchBarClose();
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
        mAdapter.setCheckState(false);
        uiActionsSelect.setChecked(false);
        uiActionsScroll.scrollTo(0, 0);
        uiBarNav.setVisibility(View.VISIBLE);
    }

    @Override
    public void init() {
        //唤起导航栏
        uiNavMenu.setOnClickListener(v -> {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick();
            }
        });

        //列表下拉刷新
        uiRefresh.setOnRefreshListener(refreshLayout -> {
            if (uiBarSearch.getVisibility() != View.VISIBLE) {
                mCurrentSearchValue = null;
            }
            getTasks(mCurrentSearchValue, 1);
        });

        //列表上拉加载
        uiRefresh.setOnLoadMoreListener(refreshLayout -> {
            if (mHasMore) {
                getTasks(mCurrentSearchValue, mPageNo + 1);
            } else {
                uiRefresh.finishLoadMore(true);
            }
        });

        //数据项操作监听
        mAdapter.setActionListener(new PanelTaskItemAdapter.ActionListener() {
            @Override
            public void onStop(PanelTask task) {
                List<Object> keys = new ArrayList<>();
                keys.add(task.getKey());
                stopTasks(keys);
            }

            @Override
            public void onRun(PanelTask task) {
                List<Object> keys = new ArrayList<>();
                keys.add(task.getKey());
                runTasks(keys);
            }

            @Override
            public void onEdit(PanelTask task) {
                showPopWindowEdit(task);
            }

            @Override
            public void onLog(PanelTask task) {
                Intent intent = new Intent(getContext(), TextEditorActivity.class);
                intent.putExtra(TextEditorActivity.EXTRA_TYPE, TextEditorActivity.TYPE_LOG);
                intent.putExtra(TextEditorActivity.EXTRA_TITLE, task.getName());
                intent.putExtra(TextEditorActivity.EXTRA_LOG_ID, String.valueOf(task.getKey()));
                startActivity(intent);
            }

            @Override
            public void onScript(PanelTask task) {
                if (!task.getCommand().matches("^task .*\\.((sh)|(py)|(js)|(ts))$")) {
                    return;
                }
                String[] path = task.getCommand().replace("task ", "").trim().split("/");
                String dir, fileName;
                if (path.length == 1) {
                    dir = "";
                    fileName = path[0].trim();
                } else if (path.length == 2) {
                    dir = path[0].trim();
                    fileName = path[1].trim();
                } else {
                    return;
                }
                Intent intent = new Intent(getContext(), TextEditorActivity.class);
                intent.putExtra(TextEditorActivity.EXTRA_SCRIPT_NAME, fileName);
                intent.putExtra(TextEditorActivity.EXTRA_SCRIPT_DIR, dir);
                intent.putExtra(TextEditorActivity.EXTRA_TITLE, fileName);
                intent.putExtra(TextEditorActivity.EXTRA_TYPE, TextEditorActivity.TYPE_SCRIPT);
                intent.putExtra(TextEditorActivity.EXTRA_CAN_EDIT, true);
                startActivity(intent);
            }
        });

        //更多操作
        uiNavMore.setOnClickListener(this::showPopWindowMenu);

        //搜索栏进入
        uiNavSearch.setOnClickListener(v -> onSearchBarOpen());

        //搜索栏确定
        uiSearchConfirm.setOnClickListener(v -> {
            mCurrentSearchValue = uiSearchValue.getText().toString().trim();
            WindowUnit.hideKeyboard(uiSearchValue);
            getTasks(mCurrentSearchValue, 1);
        });

        //搜索栏返回
        uiSearchBack.setOnClickListener(v -> onSearchBarClose());

        //操作栏返回
        uiActionsBack.setOnClickListener(v -> onActionBarClose());

        //操作-全选
        uiActionsSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mAdapter.selectAll(isChecked);
        });

        //操作-执行
        uiActionsRun.setOnClickListener(v -> {
            List<PanelTask> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (PanelTask task : tasks) {
                keys.add(task.getKey());
            }
            runTasks(keys);
        });

        //操作-停止
        uiActionsStop.setOnClickListener(v -> {
            List<PanelTask> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (PanelTask task : tasks) {
                keys.add(task.getKey());
            }
            stopTasks(keys);
        });

        //操作-顶置
        uiActionsPin.setOnClickListener(v -> {
            List<PanelTask> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (PanelTask task : tasks) {
                keys.add(task.getKey());
            }
            pinTasks(keys);
        });

        //操作-取消顶置
        uiActionsUnpin.setOnClickListener(v -> {
            List<PanelTask> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (PanelTask task : tasks) {
                keys.add(task.getKey());
            }
            unpinTasks(keys);
        });

        //操作-启用
        uiActionsEnable.setOnClickListener(v -> {
            List<PanelTask> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (PanelTask task : tasks) {
                keys.add(task.getKey());
            }
            enableTasks(keys);
        });

        //操作-禁用
        uiActionsDisable.setOnClickListener(v -> {
            List<PanelTask> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (PanelTask task : tasks) {
                keys.add(task.getKey());
            }
            disableTasks(keys);
        });

        //操作-删除
        uiActionsDelete.setOnClickListener(v -> {
            List<PanelTask> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (PanelTask task : tasks) {
                keys.add(task.getKey());
            }
            deleteTasks(keys);
        });
    }

    private void initData() {
        if (init) {
            return;
        }
        uiRefresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getTasks(mCurrentSearchValue, 1);
            }
        }, 1000);
    }

    @Override
    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.mMenuClickListener = mMenuClickListener;
    }

    private void showPopWindowMenu(View view) {
        MenuPopupWindow popMenuWindow = new MenuPopupWindow(view);
        popMenuWindow.addItem(new MenuItem("add", "新建任务", R.drawable.ic_gray_add));
        popMenuWindow.addItem(new MenuItem("localAdd", "本地导入", R.drawable.ic_gray_file));
        popMenuWindow.addItem(new MenuItem("backup", "任务备份", R.drawable.ic_gray_download));
        popMenuWindow.addItem(new MenuItem("deleteMul", "任务去重", R.drawable.ic_gray_delete));
        popMenuWindow.addItem(new MenuItem("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));
        popMenuWindow.setOnActionListener(key -> {
            switch (key) {
                case "add":
                    showPopWindowEdit(null);
                    break;
                case "localAdd":
                    showPopWindowSelectFile();
                    break;
                case "backup":
                    showPopWindowBackupEdit();
                    break;
                case "deleteMul":
                    deduplicationData();
                    break;
                default:
                    onActionBarOpen();
            }
            return true;
        });

        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showPopWindowEdit(PanelTask task) {
        uiPopEdit = new EditPopupWindow("新建任务", "取消", "确定");
        EditItem itemName = new EditItem("name", null, "名称", "请输入任务名称");
        EditItem itemCommand = new EditItem("command", null, "命令", "请输入要执行的命令");
        EditItem itemSchedule = new EditItem("schedule", null, "定时规则", "秒(可选) 分 时 天 月 周");

        uiPopEdit.addItem(itemName);
        uiPopEdit.addItem(itemCommand);
        uiPopEdit.addItem(itemSchedule);

        if (task != null) {
            uiPopEdit.setTitle("编辑任务");
            itemName.setValue(task.getName());
            itemCommand.setValue(task.getCommand());
            itemSchedule.setValue(task.getSchedule());
        }

        uiPopEdit.setActionListener(new EditPopupWindow.OnActionListener() {
            @Override
            public boolean onConfirm(@NonNull Map<String, String> map) {
                String name = map.get("name");
                String command = map.get("command");
                String schedule = map.get("schedule");

                if (TextUnit.isEmpty(name)) {
                    ToastUnit.showShort(getString(R.string.tip_empty_task_name));
                    return false;
                }
                if (TextUnit.isEmpty(command)) {
                    ToastUnit.showShort(getString(R.string.tip_empty_task_command));
                    return false;
                }
                if (!CronUnit.isValid(schedule)) {
                    ToastUnit.showShort(getString(R.string.tip_invalid_task_schedule));
                    return false;
                }

                WindowUnit.hideKeyboard(uiPopEdit.getView());

                PanelTask newTask = new PanelTask();
                newTask.setName(name);
                newTask.setCommand(command);
                newTask.setSchedule(schedule);

                if (task == null) {
                    addTask(newTask);
                } else {
                    newTask.setKey(task.getKey());
                    updateTask(newTask);
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

    private void showPopWindowBackupEdit() {
        if (!FileUtil.checkStoragePermission()) {
            ToastUnit.showShort("请授予应用读写存储权限");
            FileUtil.requestStoragePermission(requireActivity());
            return;
        }

        uiPopEdit = new EditPopupWindow("任务备份", "取消", "确定");
        EditItem itemName = new EditItem("fileName", null, "文件名", "选填");

        uiPopEdit.addItem(itemName);

        uiPopEdit.setActionListener(new EditPopupWindow.OnActionListener() {
            @Override
            public boolean onConfirm(@NonNull Map<String, String> map) {
                WindowUnit.hideKeyboard(uiPopEdit.getView());
                String fileName = map.get("fileName");
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

        List<File> files = FileUtil.getFiles(FileUtil.getPathOfTask(), (dir, name) -> name.endsWith(".json"));
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
        List<PanelTask> tasks = mAdapter.getData();
        if (tasks == null || tasks.size() == 0) {
            ToastUnit.showShort("数据为空,无需备份");
            return;
        }

        JsonArray jsonArray = new JsonArray();
        for (PanelTask task : tasks) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", task.getName());
            jsonObject.addProperty("command", task.getCommand());
            jsonObject.addProperty("schedule", task.getSchedule());
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
            boolean result = FileUtil.save(FileUtil.getPathOfTask(), fileName, content);
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
            PanelTask[] tasks = new Gson().fromJson(stringBuilder.toString(), PanelTask[].class);

            new Thread(() -> {
                uiPopProgress.setTextAndShow("导入中...");
                int index = 0;
                int success = 0;
                int total = tasks.length;

                for (PanelTask task : tasks) {
                    boolean result = ApiController.addTaskSync(task);
                    uiPopProgress.setTextAndShow("导入中... " + index + "/" + total);
                    index++;
                    if (result) {
                        success++;
                    }
                }

                dismissPopWindowProgress();
                ToastUnit.showShort("导入完成 " + success + "/" + total);
                getTasks(mCurrentSearchValue, 1);
            }).start();
        } catch (Exception e) {
            dismissPopWindowProgress();
            ToastUnit.showShort("导入失败：" + e.getLocalizedMessage());
        }
    }

    private void deduplicationData() {
        List<Object> ids = new ArrayList<>();
        Set<Object> set = new HashSet<>();
        List<PanelTask> tasks = this.mAdapter.getData();
        for (PanelTask task : tasks) {
            String key = task.getCommand().trim();
            if (set.contains(key)) {
                ids.add(task.getKey());
            } else {
                set.add(key);
            }
        }
        if (ids.size() == 0) {
            ToastUnit.showShort("无重复任务");
        } else {
            deleteTasks(ids);
        }
    }

    private void getTasks(String searchValue, int pageNo) {
        auto.panel.net.panel.ApiController.getTasks(searchValue, pageNo, mPageSize, new ApiController.TaskListCallBack() {
            @Override
            public void onSuccess(List<PanelTask> tasks) {
                Collections.sort(tasks);
                if (pageNo == 1) {
                    mAdapter.setData(tasks);
                    uiRefresh.finishRefresh(true);
                } else {
                    mAdapter.extendData(tasks);
                    uiRefresh.finishLoadMore(true);
                }
                mPageNo = pageNo;
                mHasMore = tasks.size() >= mPageSize;
                uiRefresh.setEnableLoadMore(mHasMore);
                init = true;
            }

            @Override
            public void onFailure(String msg) {
                uiRefresh.finishRefresh(false);
                uiRefresh.finishLoadMore(false);
                ToastUnit.showShort(msg);
            }
        });
    }

    private void runTasks(List<Object> keys) {
        auto.panel.net.panel.ApiController.runTasks(keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("执行成功");
                getTasks(mCurrentSearchValue, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("执行失败：" + msg);
            }
        });
    }

    private void stopTasks(List<Object> keys) {
        auto.panel.net.panel.ApiController.stopTasks(keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("终止成功");
                getTasks(mCurrentSearchValue, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("终止失败：" + msg);
            }
        });
    }

    private void enableTasks(List<Object> keys) {
        auto.panel.net.panel.ApiController.enableTasks(keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("启用成功");
                getTasks(mCurrentSearchValue, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("启用失败：" + msg);
            }
        });
    }

    private void disableTasks(List<Object> keys) {
        auto.panel.net.panel.ApiController.disableTasks(keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("禁用成功");
                getTasks(mCurrentSearchValue, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("禁用失败：" + msg);
            }
        });
    }

    private void pinTasks(List<Object> keys) {
        auto.panel.net.panel.ApiController.pinTasks(keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("顶置成功");
                getTasks(mCurrentSearchValue, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("顶置失败：" + msg);
            }
        });
    }

    private void unpinTasks(List<Object> keys) {
        ApiController.unpinTasks(keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("取消顶置成功");
                getTasks(mCurrentSearchValue, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("取消顶置失败：" + msg);
            }
        });
    }

    private void deleteTasks(List<Object> keys) {
        auto.panel.net.panel.ApiController.deleteTasks(keys, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("删除成功");
                getTasks(mCurrentSearchValue, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("删除失败：" + msg);
            }
        });
    }

    private void updateTask(PanelTask task) {
        auto.panel.net.panel.ApiController.updateTask(task, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                dismissPopWindowEdit();
                ToastUnit.showShort("编辑成功");
                getTasks(null, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("编辑失败：" + msg);
            }
        });
    }

    private void addTask(PanelTask task) {
        auto.panel.net.panel.ApiController.addTask(task, new auto.panel.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                dismissPopWindowEdit();
                ToastUnit.showShort("新建任务成功");
                getTasks(null, 1);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建任务失败：" + msg);
            }
        });
    }
}