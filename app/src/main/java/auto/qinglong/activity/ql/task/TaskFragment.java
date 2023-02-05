package auto.qinglong.activity.ql.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.ql.CodeWebActivity;
import auto.qinglong.activity.ql.LocalFileAdapter;
import auto.qinglong.bean.ql.QLTask;
import auto.qinglong.bean.ql.network.QLTasksRes;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.network.http.RequestManager;
import auto.qinglong.utils.CronUnit;
import auto.qinglong.utils.FileUtil;
import auto.qinglong.utils.LogUnit;
import auto.qinglong.utils.TextUnit;
import auto.qinglong.utils.TimeUnit;
import auto.qinglong.utils.ToastUnit;
import auto.qinglong.utils.WindowUnit;
import auto.qinglong.views.popup.EditWindow;
import auto.qinglong.views.popup.EditWindowItem;
import auto.qinglong.views.popup.ListWindow;
import auto.qinglong.views.popup.MiniMoreItem;
import auto.qinglong.views.popup.MiniMoreWindow;
import auto.qinglong.views.popup.PopupWindowBuilder;
import auto.qinglong.views.popup.ProgressWindow;

public class TaskFragment extends BaseFragment {
    public static String TAG = "TaskFragment";

    private String mCurrentSearchValue = "";
    private MenuClickListener mMenuClickListener;
    private TaskAdapter mTaskAdapter;

    //主导航栏
    private LinearLayout ui_bar_main;
    private ImageView ui_nav_menu;
    private ImageView ui_nav_search;
    private ImageView ui_nav_more;
    //搜索导航栏
    private LinearLayout ui_bar_search;
    private ImageView ui_search_back;
    private ImageView ui_search_confirm;
    private EditText ui_search_value;
    //操作导航栏
    private LinearLayout ui_bar_actions;
    private ImageView ui_actions_back;
    private CheckBox ui_actions_select;
    private HorizontalScrollView ui_actions_scroll;
    private LinearLayout ui_actions_run;
    private LinearLayout ui_actions_stop;
    private LinearLayout ui_actions_pin;
    private LinearLayout ui_actions_unpin;
    private LinearLayout ui_actions_enable;
    private LinearLayout ui_actions_disable;
    private LinearLayout ui_actions_delete;
    //布局控件
    private LinearLayout ui_root;
    private RelativeLayout ui_bar;
    private RecyclerView ui_recycler;
    private SmartRefreshLayout ui_refresh;

    private EditWindow ui_pop_edit;
    private ProgressWindow ui_pop_progress;

    private enum BarType {NAV, SEARCH, MUL_ACTION}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, null);

        ui_root = view.findViewById(R.id.root);
        ui_bar = view.findViewById(R.id.task_bar);
        ui_bar_main = view.findViewById(R.id.task_bar_nav);
        ui_nav_search = view.findViewById(R.id.task_bar_nav_search);
        ui_nav_more = view.findViewById(R.id.task_bar_nav_more);
        ui_nav_menu = view.findViewById(R.id.task_bar_nav_menu);

        ui_bar_search = view.findViewById(R.id.task_bar_search);
        ui_search_back = view.findViewById(R.id.task_bar_search_back);
        ui_search_value = view.findViewById(R.id.task_bar_search_value);
        ui_search_confirm = view.findViewById(R.id.task_bar_search_confirm);

        ui_bar_actions = view.findViewById(R.id.task_bar_actions);
        ui_actions_select = view.findViewById(R.id.task_bar_actions_select_all);
        ui_actions_scroll = view.findViewById(R.id.task_bar_actions_scroll);
        ui_actions_back = view.findViewById(R.id.task_bar_actions_back);
        ui_actions_run = view.findViewById(R.id.task_bar_actions_run);
        ui_actions_stop = view.findViewById(R.id.task_bar_actions_stop);
        ui_actions_pin = view.findViewById(R.id.task_bar_actions_pinned);
        ui_actions_unpin = view.findViewById(R.id.task_bar_actions_unpinned);
        ui_actions_enable = view.findViewById(R.id.task_bar_actions_enable);
        ui_actions_disable = view.findViewById(R.id.task_bar_actions_disable);
        ui_actions_delete = view.findViewById(R.id.task_bar_actions_delete);

        ui_refresh = view.findViewById(R.id.refresh_layout);
        ui_recycler = view.findViewById(R.id.recycler_view);

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
    public boolean onBackPressed() {
        if (ui_bar_actions.getVisibility() == View.VISIBLE) {
            changeBar(BarType.NAV);
            return true;
        } else if (ui_bar_search.getVisibility() == View.VISIBLE) {
            changeBar(BarType.NAV);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void init() {
        //item容器配置
        mTaskAdapter = new TaskAdapter(getContext());
        //取消更新动画，避免刷新闪烁
        Objects.requireNonNull(ui_recycler.getItemAnimator()).setChangeDuration(0);
        ui_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        ui_recycler.setAdapter(mTaskAdapter);

        //列表item操作接口
        mTaskAdapter.setTaskInterface(new TaskAdapter.ItemActionListener() {
            @Override
            public void onLog(QLTask task) {
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_LOG);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, task.getName());
                intent.putExtra(CodeWebActivity.EXTRA_LOG_PATH, task.getLogPath());
                startActivity(intent);
            }

            @Override
            public void onStop(QLTask QLTask) {
                if (RequestManager.isRequesting(getNetRequestID())) {
                    return;
                }
                List<String> ids = new ArrayList<>();
                ids.add(QLTask.getId());
                netStopTasks(ids, false);
            }

            @Override
            public void onRun(QLTask QLTask) {
                List<String> ids = new ArrayList<>();
                ids.add(QLTask.getId());
                netRunTasks(ids, false);
            }

            @Override
            public void onEdit(QLTask QLTask) {
                showPopWindowEdit(QLTask);
            }

            @Override
            public void onMulAction() {
                changeBar(BarType.MUL_ACTION);
            }
        });

        ui_refresh.setOnRefreshListener(refreshLayout -> netGetTasks(mCurrentSearchValue, true));

        //导航点击监听
        ui_nav_menu.setOnClickListener(v -> {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick();
            }
        });

        //搜索按键监听
        ui_nav_search.setOnClickListener(v -> {
            ui_search_value.setText(mCurrentSearchValue);
            changeBar(BarType.SEARCH);
        });

        //搜索返回按键监听
        ui_search_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //搜索确定监听
        ui_search_confirm.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }
            ToastUnit.showShort(getString(R.string.tip_searching));
            mCurrentSearchValue = ui_search_value.getText().toString();
            WindowUnit.hideKeyboard(ui_search_value);
            netGetTasks(mCurrentSearchValue, true);
        });

        //更多操作按键监听
        ui_nav_more.setOnClickListener(v -> showPopWindowMiniMore());

        //批量操作返回
        ui_actions_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //全选监听
        ui_actions_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mTaskAdapter.getCheckState()) {
                mTaskAdapter.selectAll(isChecked);
            }
        });

        //执行
        ui_actions_run.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<QLTask> QLTasks = mTaskAdapter.getCheckedItems();
                if (QLTasks.size() == 0) {
                    ToastUnit.showShort(getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (QLTask QLTask : QLTasks) {
                        ids.add(QLTask.getId());
                    }
                    netRunTasks(ids, true);
                }
            }
        });

        //停止
        ui_actions_stop.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<QLTask> QLTasks = mTaskAdapter.getCheckedItems();
                if (QLTasks.size() == 0) {
                    ToastUnit.showShort(getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (QLTask QLTask : QLTasks) {
                        ids.add(QLTask.getId());
                    }
                    netStopTasks(ids, true);
                }
            }
        });

        //顶置
        ui_actions_pin.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<QLTask> QLTasks = mTaskAdapter.getCheckedItems();
                if (QLTasks.size() == 0) {
                    ToastUnit.showShort(getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (QLTask QLTask : QLTasks) {
                        ids.add(QLTask.getId());
                    }
                    netPinTasks(ids);
                }
            }
        });

        //取消顶置
        ui_actions_unpin.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<QLTask> QLTasks = mTaskAdapter.getCheckedItems();
                if (QLTasks.size() == 0) {
                    ToastUnit.showShort(getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (QLTask QLTask : QLTasks) {
                        ids.add(QLTask.getId());
                    }
                    netUnpinTasks(ids);
                }
            }
        });

        //启用
        ui_actions_enable.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<QLTask> QLTasks = mTaskAdapter.getCheckedItems();
                if (QLTasks.size() == 0) {
                    ToastUnit.showShort(getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (QLTask QLTask : QLTasks) {
                        ids.add(QLTask.getId());
                    }
                    netEnableTasks(ids);
                }
            }
        });

        //禁用
        ui_actions_disable.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<QLTask> QLTasks = mTaskAdapter.getCheckedItems();
                if (QLTasks.size() == 0) {
                    ToastUnit.showShort(getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (QLTask QLTask : QLTasks) {
                        ids.add(QLTask.getId());
                    }
                    netDisableTasks(ids);
                }
            }
        });

        //删除
        ui_actions_delete.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<QLTask> QLTasks = mTaskAdapter.getCheckedItems();
                if (QLTasks.size() == 0) {
                    ToastUnit.showShort(getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (QLTask QLTask : QLTasks) {
                        ids.add(QLTask.getId());
                    }
                    netDeleteTasks(ids);
                }
            }
        });
    }

    @Override
    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.mMenuClickListener = menuClickListener;
    }

    private void initData() {
        if (initDataFlag || RequestManager.isRequesting(this.getNetRequestID())) {
            return;
        }
        ui_refresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                netGetTasks(mCurrentSearchValue, true);
            }
        }, 1000);
    }

    private void showPopWindowMiniMore() {
        MiniMoreWindow miniMoreWindow = new MiniMoreWindow();
        miniMoreWindow.setTargetView(ui_bar);
        miniMoreWindow.setGravity(Gravity.END);
        miniMoreWindow.addItem(new MiniMoreItem("add", "新建任务", R.drawable.ic_gray_add));
        miniMoreWindow.addItem(new MiniMoreItem("localAdd", "本地导入", R.drawable.ic_gray_file));
        miniMoreWindow.addItem(new MiniMoreItem("backup", "任务备份", R.drawable.ic_gray_backup));
        miniMoreWindow.addItem(new MiniMoreItem("deleteMul", "任务去重", R.drawable.ic_gray_delete));
        miniMoreWindow.addItem(new MiniMoreItem("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));
        miniMoreWindow.setOnActionListener(key -> {
            switch (key) {
                case "add":
                    showPopWindowEdit(null);
                    break;
                case "localAdd":
                    localAddData();
                    break;
                case "backup":
                    showPopWindowBackupEdit();
                    break;
                case "deleteMul":
                    compareAndDeleteData();
                    break;
                default:
                    changeBar(BarType.MUL_ACTION);
            }
            return true;
        });
        PopupWindowBuilder.buildMiniMoreWindow(requireActivity(), miniMoreWindow);
    }

    private void showPopWindowEdit(QLTask qlTask) {
        ui_pop_edit = new EditWindow("新建任务", "取消", "确定");
        EditWindowItem itemName = new EditWindowItem("name", null, "名称", "请输入任务名称");
        EditWindowItem itemCommand = new EditWindowItem("command", null, "命令", "请输入要执行的命令");
        EditWindowItem itemSchedule = new EditWindowItem("schedule", null, "定时规则", "秒(可选) 分 时 天 月 周");

        if (qlTask != null) {
            ui_pop_edit.setTitle("编辑任务");
            itemName.setValue(qlTask.getName());
            itemCommand.setValue(qlTask.getCommand());
            itemSchedule.setValue(qlTask.getSchedule());
        }

        ui_pop_edit.addItem(itemName);
        ui_pop_edit.addItem(itemCommand);
        ui_pop_edit.addItem(itemSchedule);
        ui_pop_edit.setActionListener(new EditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String name = map.get("name");
                String command = map.get("command");
                String schedule = map.get("schedule");

                if (TextUnit.isEmpty(name)) {
                    ToastUnit.showShort(getString(R.string.tip_empty_task_name));
                    return false;
                }
                if (TextUnit.isEmpty(command)) {
                    ToastUnit.showShort(getString(R.string.tip_empty_command));
                    return false;
                }
                if (!CronUnit.isValid(schedule)) {
                    ToastUnit.showShort(getString(R.string.tip_invalid_schedule));
                    return false;
                }

                WindowUnit.hideKeyboard(ui_pop_edit.getView());

                QLTask newQLTask = new QLTask();
                if (qlTask == null) {
                    newQLTask.setName(name);
                    newQLTask.setCommand(command);
                    newQLTask.setSchedule(schedule);
                    netAddTask(newQLTask);
                } else {
                    newQLTask.setName(name);
                    newQLTask.setCommand(command);
                    newQLTask.setSchedule(schedule);
                    newQLTask.setId(qlTask.getId());
                    netEditTask(newQLTask);
                }

                return false;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), ui_pop_edit);
    }

    private void showPopWindowBackupEdit() {
        ui_pop_edit = new EditWindow("任务备份", "取消", "确定");
        EditWindowItem itemName = new EditWindowItem("file_name", null, "文件名", "选填");

        ui_pop_edit.addItem(itemName);

        ui_pop_edit.setActionListener(new EditWindow.OnActionListener() {
            @Override
            public boolean onConfirm(Map<String, String> map) {
                String fileName = map.get("file_name");
                WindowUnit.hideKeyboard(ui_pop_edit.getView());
                backupData(fileName);
                return true;
            }

            @Override
            public boolean onCancel() {
                return true;
            }
        });

        PopupWindowBuilder.buildEditWindow(requireActivity(), ui_pop_edit);
    }

    private void changeBar(BarType barType) {
        if (ui_bar_search.getVisibility() == View.VISIBLE) {
            WindowUnit.hideKeyboard(ui_root);
            ui_bar_search.setVisibility(View.INVISIBLE);
            mCurrentSearchValue = "";
        }

        if (ui_bar_actions.getVisibility() == View.VISIBLE) {
            ui_bar_actions.setVisibility(View.INVISIBLE);
            mTaskAdapter.setCheckState(false);
            ui_actions_select.setChecked(false);
        }

        ui_bar_main.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            ui_bar_main.setVisibility(View.VISIBLE);
        } else if (barType == BarType.SEARCH) {
            ui_bar_search.setVisibility(View.VISIBLE);
        } else {
            ui_actions_scroll.scrollTo(0, 0);
            mTaskAdapter.setCheckState(true);
            ui_bar_actions.setVisibility(View.VISIBLE);
        }
    }

    private void compareAndDeleteData() {
        List<String> ids = new ArrayList<>();
        Set<String> set = new HashSet<>();
        List<QLTask> tasks = this.mTaskAdapter.getData();
        for (QLTask task : tasks) {
            String key = task.getCommand();
            if (set.contains(key)) {
                ids.add(task.getId());
            } else {
                set.add(key);
            }
        }
        if (ids.size() == 0) {
            ToastUnit.showShort("无重复任务");
        } else {
            netDeleteTasks(ids);
        }
    }

    private void localAddData() {
        if (FileUtil.isNeedRequestPermission()) {
            ToastUnit.showShort("请授予应用读写存储权限");
            FileUtil.requestPermission(requireActivity());
            return;
        }

        List<File> files = FileUtil.getFiles(FileUtil.getTaskPath(), (dir, name) -> name.endsWith(".json"));
        if (files.size() == 0) {
            ToastUnit.showShort("无本地备份数据");
            return;
        }

        ListWindow<LocalFileAdapter> listWindow = new ListWindow<>("选择文件");
        LocalFileAdapter fileAdapter = new LocalFileAdapter(getContext());
        fileAdapter.setData(files);
        listWindow.setAdapter(fileAdapter);

        PopupWindow popupWindow = PopupWindowBuilder.buildListWindow(requireActivity(), listWindow);

        fileAdapter.setListener(file -> {
            try {
                popupWindow.dismiss();
                if (ui_pop_progress == null) {
                    ui_pop_progress = PopupWindowBuilder.buildProgressWindow(requireActivity(), null);
                }
                ui_pop_progress.setTextAndShow("加载文件中...");
                BufferedReader bufferedInputStream = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedInputStream.readLine()) != null) {
                    stringBuilder.append(line);
                }

                ui_pop_progress.setTextAndShow("解析文件中...");
                Type type = new TypeToken<List<QLTask>>() {
                }.getType();
                List<QLTask> tasks = new Gson().fromJson(stringBuilder.toString(), type);

                netMulAddTask(tasks);
            } catch (Exception e) {
                ToastUnit.showShort("导入失败：" + e.getLocalizedMessage());
            }
        });
    }

    private void backupData(String fileName) {
        if (FileUtil.isNeedRequestPermission()) {
            ToastUnit.showShort("请授予应用读写存储权限");
            FileUtil.requestPermission(requireActivity());
            return;
        }

        List<QLTask> tasks = mTaskAdapter.getData();
        if (tasks == null || tasks.size() == 0) {
            ToastUnit.showShort("数据为空,无需备份");
            return;
        }

        JsonArray jsonArray = new JsonArray();
        for (QLTask task : tasks) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", task.getName());
            jsonObject.addProperty("command", task.getCommand());
            jsonObject.addProperty("schedule", task.getSchedule());
            jsonArray.add(jsonObject);
        }

        if (fileName == null) {
            fileName = TimeUnit.formatCurrentTime() + ".json";
        } else {
            fileName += ".json";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String content = gson.toJson(jsonArray);
        try {
            boolean result = FileUtil.save(FileUtil.getTaskPath(), fileName, content);
            if (result) {
                ToastUnit.showShort("备份成功：" + fileName);
            } else {
                ToastUnit.showShort("备份失败");
            }
        } catch (Exception e) {
            ToastUnit.showShort("备份失败：" + e.getMessage());
        }

    }

    private void netGetTasks(String searchValue, boolean needTip) {
        QLApiController.getTasks(getNetRequestID(), searchValue, new QLApiController.NetGetTasksCallback() {
            @Override
            public void onSuccess(QLTasksRes res) {
                initDataFlag = true;
                List<QLTask> data = res.getData();
                Collections.sort(data);
                for (int k = 0; k < data.size(); k++) {
                    data.get(k).setIndex(k + 1);
                }
                mTaskAdapter.setData(data);
                if (needTip) {
                    ToastUnit.showShort("加载成功：" + data.size());
                }
                ui_refresh.finishRefresh(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                ui_refresh.finishRefresh(false);
            }
        });
    }

    private void netRunTasks(List<String> ids, boolean isFromBar) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.runTasks(getNetRequestID(), ids, new QLApiController.NetRunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (isFromBar && ui_bar_actions.getVisibility() == View.VISIBLE) {
                    ui_actions_back.performClick();
                }
                ToastUnit.showShort("执行成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("执行失败：" + msg);
            }
        });

    }

    private void netStopTasks(List<String> ids, boolean isFromBar) {
        QLApiController.stopTasks(getNetRequestID(), ids, new QLApiController.NetRunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (isFromBar && ui_bar_actions.getVisibility() == View.VISIBLE) {
                    ui_actions_back.performClick();
                }
                ToastUnit.showShort("终止成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("终止失败：" + msg);
            }
        });
    }

    private void netEnableTasks(List<String> ids) {
        QLApiController.enableTasks(getNetRequestID(), ids, new QLApiController.NetRunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (ui_actions_back.getVisibility() == View.VISIBLE) {
                    ui_actions_back.performClick();
                }
                ToastUnit.showShort("启用成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("启用失败：" + msg);
            }
        });
    }

    private void netDisableTasks(List<String> ids) {
        QLApiController.disableTasks(getNetRequestID(), ids, new QLApiController.NetRunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (ui_actions_back.getVisibility() == View.VISIBLE) {
                    ui_actions_back.performClick();
                }
                ToastUnit.showShort("禁用成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("禁用失败：" + msg);
            }
        });
    }

    private void netPinTasks(List<String> ids) {
        QLApiController.pinTasks(getNetRequestID(), ids, new QLApiController.NetRunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (ui_actions_back.getVisibility() == View.VISIBLE) {
                    ui_actions_back.performClick();
                }
                ToastUnit.showShort("顶置成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getString(R.string.action_pin_failure) + msg);
            }
        });
    }

    private void netUnpinTasks(List<String> ids) {
        QLApiController.unpinTasks(getNetRequestID(), ids, new QLApiController.NetRunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (ui_actions_back.getVisibility() == View.VISIBLE) {
                    ui_actions_back.performClick();
                }
                ToastUnit.showShort("取消顶置成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("取消顶置失败：" + msg);
            }
        });
    }

    private void netDeleteTasks(List<String> ids) {
        QLApiController.deleteTasks(getNetRequestID(), ids, new QLApiController.NetBaseCallback() {
            @Override
            public void onSuccess() {
                if (ui_actions_back.getVisibility() == View.VISIBLE) {
                    ui_actions_back.performClick();
                }
                ToastUnit.showShort("删除成功：" + ids.size());
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("删除失败：" + msg);
            }
        });
    }

    private void netEditTask(QLTask task) {
        QLApiController.editTask(getNetRequestID(), task, new QLApiController.NetEditTaskCallback() {
            @Override
            public void onSuccess(QLTask QLTask) {
                ui_pop_edit.dismiss();
                ToastUnit.showShort("编辑成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("编辑失败：" + msg);
            }
        });
    }

    private void netAddTask(QLTask task) {
        QLApiController.addTask(getNetRequestID(), task, new QLApiController.NetEditTaskCallback() {
            @Override
            public void onSuccess(QLTask QLTask) {
                ui_pop_edit.dismiss();
                ToastUnit.showShort("新建任务成功");
                netGetTasks(mCurrentSearchValue, false);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建任务失败：" + msg);
            }
        });
    }

    private void netMulAddTask(List<QLTask> tasks) {
        new Thread(() -> {
            final boolean[] isEnd = {false};

            for (int k = 0; k < tasks.size(); k++) {
                ui_pop_progress.setText("导入任务中 " + k + "/" + tasks.size());
                QLApiController.addTask(getNetRequestID(), tasks.get(k), new QLApiController.NetEditTaskCallback() {
                    @Override
                    public void onSuccess(QLTask QLTask) {
                        isEnd[0] = true;
                    }

                    @Override
                    public void onFailure(String msg) {
                        isEnd[0] = true;
                        LogUnit.log(TAG, msg);
                    }
                });
                while (!isEnd[0]) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            ui_pop_progress.dismiss();
            netGetTasks(mCurrentSearchValue, true);
        }).start();
    }


}