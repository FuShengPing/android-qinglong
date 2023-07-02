package auto.qinglong.ui.activity.panel.task;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import auto.base.util.LogUnit;
import auto.base.util.TextUnit;
import auto.base.util.ToastUnit;
import auto.base.util.VibratorUtil;
import auto.base.util.WindowUnit;
import auto.base.view.popup.PopEditObject;
import auto.base.view.popup.PopEditWindow;
import auto.base.view.popup.PopMenuObject;
import auto.base.view.popup.PopMenuWindow;
import auto.base.view.popup.PopProgressWindow;
import auto.base.view.popup.PopupWindowBuilder;
import auto.qinglong.R;
import auto.qinglong.bean.views.Task;
import auto.qinglong.database.sp.PanelPreference;
import auto.qinglong.net.panel.ApiController;
import auto.qinglong.ui.BaseFragment;
import auto.qinglong.ui.activity.panel.CodeWebActivity;
import auto.qinglong.utils.CronUnit;

public class TaskFragment extends BaseFragment {
    public static String TAG = "TaskFragment";

    private String mCurrentSearchValue;
    private MenuClickListener mMenuClickListener;
    private TaskAdapter mAdapter;
    private boolean init = false;

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
    private RecyclerView ui_recycler;
    private SmartRefreshLayout ui_refresh;

    private PopEditWindow ui_pop_edit;
    private PopProgressWindow ui_pop_progress;

    private enum BarType {NAV, SEARCH, MUL_ACTION}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, null);

        ui_root = view.findViewById(R.id.root);
        ui_bar_main = view.findViewById(R.id.task_bar_nav);
        ui_nav_menu = view.findViewById(R.id.task_bar_nav_menu);
        ui_nav_search = view.findViewById(R.id.task_bar_nav_search);
        ui_nav_more = view.findViewById(R.id.task_bar_nav_more);

        ui_bar_search = view.findViewById(R.id.task_bar_search);
        ui_search_back = view.findViewById(R.id.task_bar_search_back);
        ui_search_value = view.findViewById(R.id.task_bar_search_input);
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
    public boolean onDispatchBackKey() {
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
        mAdapter = new TaskAdapter(requireContext());
        ui_recycler.setAdapter(mAdapter);
        ui_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //取消更新动画，避免刷新闪烁
        Objects.requireNonNull(ui_recycler.getItemAnimator()).setChangeDuration(0);

        //唤起导航栏
        ui_nav_menu.setOnClickListener(v -> {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick();
            }
        });

        //下拉刷新列表
        ui_refresh.setOnRefreshListener(refreshLayout -> {
            if (ui_bar_search.getVisibility() != View.VISIBLE) {
                mCurrentSearchValue = null;
            }
            getTasks(mCurrentSearchValue);
        });

        //数据项操作监听
        mAdapter.setActionListener(new TaskAdapter.ActionListener() {
            @Override
            public void onStop(Task task) {
                List<Object> keys = new ArrayList<>();
                keys.add(task.getKey());
                stopTasks(keys);
            }

            @Override
            public void onRun(Task task) {
                List<Object> keys = new ArrayList<>();
                keys.add(task.getKey());
                runTasks(keys);
            }

            @Override
            public void onEdit(Task task) {
                showPopWindowEdit(task);
            }

            @Override
            public void onLog(Task task) {
                String path = "api/crons/" + task.getKey() + "/log";
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_LOG);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, task.getTitle());
                intent.putExtra(CodeWebActivity.EXTRA_LOG_PATH, path);
                startActivity(intent);
            }

            @Override
            public void onScript(Task task) {
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
                VibratorUtil.vibrate(requireContext(), VibratorUtil.VIBRATE_SHORT);
                Intent intent = new Intent(getContext(), CodeWebActivity.class);
                intent.putExtra(CodeWebActivity.EXTRA_SCRIPT_NAME, fileName);
                intent.putExtra(CodeWebActivity.EXTRA_SCRIPT_DIR, dir);
                intent.putExtra(CodeWebActivity.EXTRA_TITLE, fileName);
                intent.putExtra(CodeWebActivity.EXTRA_TYPE, CodeWebActivity.TYPE_SCRIPT);
                intent.putExtra(CodeWebActivity.EXTRA_CAN_EDIT, true);
                startActivity(intent);
            }
        });

        //更多操作
        ui_nav_more.setOnClickListener(this::showPopWindowMenu);

        //搜索栏进入
        ui_nav_search.setOnClickListener(v -> changeBar(BarType.SEARCH));

        //搜索栏返回
        ui_search_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //搜索栏确定
        ui_search_confirm.setOnClickListener(v -> {
            mCurrentSearchValue = ui_search_value.getText().toString().trim();
            WindowUnit.hideKeyboard(ui_search_value);
            getTasks(mCurrentSearchValue);
        });

        //操作栏返回
        ui_actions_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //全选
        ui_actions_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mAdapter.selectAll(isChecked);
        });

        //执行
        ui_actions_run.setOnClickListener(v -> {
            List<Task> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (Task task : tasks) {
                keys.add(task.getKey());
            }
            runTasks(keys);
        });

        //停止
        ui_actions_stop.setOnClickListener(v -> {
            List<Task> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (Task task : tasks) {
                keys.add(task.getKey());
            }
            stopTasks(keys);
        });

        //顶置
        ui_actions_pin.setOnClickListener(v -> {
            List<Task> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (Task task : tasks) {
                keys.add(task.getKey());
            }
            pinTasks(keys);
        });

        //取消顶置
        ui_actions_unpin.setOnClickListener(v -> {
            List<Task> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (Task task : tasks) {
                keys.add(task.getKey());
            }
            unpinTasks(keys);
        });

        //启用
        ui_actions_enable.setOnClickListener(v -> {
            List<Task> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (Task task : tasks) {
                keys.add(task.getKey());
            }
            enableTasks(keys);
        });

        //禁用
        ui_actions_disable.setOnClickListener(v -> {
            List<Task> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (Task task : tasks) {
                keys.add(task.getKey());
            }
            disableTasks(keys);
        });

        //删除
        ui_actions_delete.setOnClickListener(v -> {
            List<Task> tasks = mAdapter.getCheckedItems();
            if (tasks.size() == 0) {
                return;
            }
            List<Object> keys = new ArrayList<>();

            for (Task task : tasks) {
                keys.add(task.getKey());
            }
            deleteTasks(keys);
        });
    }

    @Override
    public void setMenuClickListener(MenuClickListener mMenuClickListener) {
        this.mMenuClickListener = mMenuClickListener;
    }

    private void initData() {
        if (init) {
            return;
        }
        ui_refresh.autoRefreshAnimationOnly();
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getTasks(mCurrentSearchValue);
            }
        }, 1000);
    }

    private void showPopWindowMenu(View view) {
        PopMenuWindow popMenuWindow = new PopMenuWindow(view, Gravity.END);
        popMenuWindow.addItem(new PopMenuObject("add", "新建任务", R.drawable.ic_gray_add));
//        popMenuWindow.addItem(new PopMenuObject("localAdd", "本地导入", R.drawable.ic_gray_file));
//        popMenuWindow.addItem(new PopMenuObject("backup", "任务备份", R.drawable.ic_gray_download));
//        popMenuWindow.addItem(new PopMenuObject("deleteMul", "任务去重", R.drawable.ic_gray_delete));
        popMenuWindow.addItem(new PopMenuObject("mulAction", "批量操作", R.drawable.ic_gray_mul_setting));
        popMenuWindow.setOnActionListener(key -> {
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
        PopupWindowBuilder.buildMenuWindow(requireActivity(), popMenuWindow);
    }

    private void showPopWindowEdit(Task task) {
        ui_pop_edit = new PopEditWindow("新建任务", "取消", "确定");
        PopEditObject itemName = new PopEditObject("name", null, "名称", "请输入任务名称");
        PopEditObject itemCommand = new PopEditObject("command", null, "命令", "请输入要执行的命令");
        PopEditObject itemSchedule = new PopEditObject("schedule", null, "定时规则", "秒(可选) 分 时 天 月 周");

        if (task != null) {
            ui_pop_edit.setTitle("编辑任务");
            itemName.setValue(task.getTitle());
            itemCommand.setValue(task.getCommand());
            itemSchedule.setValue(task.getSchedule());
        }

        ui_pop_edit.addItem(itemName);
        ui_pop_edit.addItem(itemCommand);
        ui_pop_edit.addItem(itemSchedule);
        ui_pop_edit.setActionListener(new PopEditWindow.OnActionListener() {
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
                    ToastUnit.showShort(getString(R.string.tip_empty_task_command));
                    return false;
                }
                if (!CronUnit.isValid(schedule)) {
                    ToastUnit.showShort(getString(R.string.tip_invalid_task_schedule));
                    return false;
                }

                WindowUnit.hideKeyboard(ui_pop_edit.getView());

                Task newTask = new Task(null);
                if (task == null) {
                    newTask.setTitle(name);
                    newTask.setCommand(command);
                    newTask.setSchedule(schedule);
                    createTask(newTask);
                } else {
                    newTask.setKey(task.getKey());
                    newTask.setTitle(name);
                    newTask.setCommand(command);
                    newTask.setSchedule(schedule);
                    updateTask(newTask);
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
        ui_pop_edit = new PopEditWindow("任务备份", "取消", "确定");
        PopEditObject itemName = new PopEditObject("file_name", null, "文件名", "选填");

        ui_pop_edit.addItem(itemName);

        ui_pop_edit.setActionListener(new PopEditWindow.OnActionListener() {
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
        } else if (ui_bar_actions.getVisibility() == View.VISIBLE) {
            ui_bar_actions.setVisibility(View.INVISIBLE);
            mAdapter.setOnCheck(false);
            ui_actions_select.setChecked(false);
        }

        ui_bar_main.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            ui_bar_main.setVisibility(View.VISIBLE);
        } else if (barType == BarType.SEARCH) {
            ui_search_value.setText(mCurrentSearchValue);
            ui_bar_search.setVisibility(View.VISIBLE);
        } else {
            ui_actions_scroll.scrollTo(0, 0);
            mAdapter.setOnCheck(true);
            ui_bar_actions.setVisibility(View.VISIBLE);
        }
    }

    private void compareAndDeleteData() {
//        List<String> ids = new ArrayList<>();
//        Set<String> set = new HashSet<>();
//        List<QLTask> tasks = this.mAdapter.getData();
//        for (QLTask task : tasks) {
//            String key = task.getCommand();
//            if (set.contains(key)) {
//                ids.add(task.getId());
//            } else {
//                set.add(key);
//            }
//        }
//        if (ids.size() == 0) {
//            ToastUnit.showShort("无重复任务");
//        } else {
//            netDeleteTasks(ids);
//        }
    }

    private void localAddData() {
//        if (FileUtil.isNeedRequestPermission()) {
//            ToastUnit.showShort("请授予应用读写存储权限");
//            FileUtil.requestPermission(requireActivity());
//            return;
//        }
//
//        List<File> files = FileUtil.getFiles(FileUtil.getTaskPath(), (dir, name) -> name.endsWith(".json"));
//        if (files.size() == 0) {
//            ToastUnit.showShort("无本地备份数据");
//            return;
//        }
//
//        PopListWindow<LocalFileAdapter> listWindow = new PopListWindow<>("选择文件");
//        LocalFileAdapter fileAdapter = new LocalFileAdapter(getContext());
//        fileAdapter.setData(files);
//        listWindow.setAdapter(fileAdapter);
//
//        PopupWindow popupWindow = PopupWindowBuilder.buildListWindow(requireActivity(), listWindow);
//
//        fileAdapter.setListener(file -> {
//            try {
//                popupWindow.dismiss();
//                if (ui_pop_progress == null) {
//                    ui_pop_progress = PopupWindowBuilder.buildProgressWindow(requireActivity(), null);
//                }
//                ui_pop_progress.setTextAndShow("加载文件中...");
//                BufferedReader bufferedInputStream = new BufferedReader(new FileReader(file));
//                StringBuilder stringBuilder = new StringBuilder();
//                String line;
//                while ((line = bufferedInputStream.readLine()) != null) {
//                    stringBuilder.append(line);
//                }
//
//                ui_pop_progress.setTextAndShow("解析文件中...");
//                Type type = new TypeToken<List<QLTask>>() {
//                }.getType();
//                List<QLTask> tasks = new Gson().fromJson(stringBuilder.toString(), type);
//
//                netMulAddTask(tasks);
//            } catch (Exception e) {
//                ToastUnit.showShort("导入失败：" + e.getLocalizedMessage());
//            }
//        });
    }

    private void backupData(String fileName) {
//        if (FileUtil.isNeedRequestPermission()) {
//            ToastUnit.showShort("请授予应用读写存储权限");
//            FileUtil.requestPermission(requireActivity());
//            return;
//        }
//
//        List<QLTask> tasks = mAdapter.getData();
//        if (tasks == null || tasks.size() == 0) {
//            ToastUnit.showShort("数据为空,无需备份");
//            return;
//        }
//
//        JsonArray jsonArray = new JsonArray();
//        for (QLTask task : tasks) {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("name", task.getName());
//            jsonObject.addProperty("command", task.getCommand());
//            jsonObject.addProperty("schedule", task.getSchedule());
//            jsonArray.add(jsonObject);
//        }
//
//        if (TextUnit.isFull(fileName)) {
//            fileName += ".json";
//        } else {
//            fileName = TimeUnit.formatDatetimeC() + ".json";
//        }
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String content = gson.toJson(jsonArray);
//        try {
//            boolean result = FileUtil.save(FileUtil.getTaskPath(), fileName, content);
//            if (result) {
//                ToastUnit.showShort("备份成功：" + fileName);
//            } else {
//                ToastUnit.showShort("备份失败");
//            }
//        } catch (Exception e) {
//            ToastUnit.showShort("备份失败：" + e.getMessage());
//        }

    }

    private void getTasks(String searchValue) {
        auto.qinglong.net.panel.ApiController.getTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), searchValue, new auto.qinglong.net.panel.ApiController.TaskCallBack() {
            @Override
            public void onSuccess(List<Task> tasks) {
                Collections.sort(tasks);
                mAdapter.setData(tasks);
                ui_refresh.finishRefresh(true);
                init = true;
            }

            @Override
            public void onFailure(String msg) {
                ui_refresh.finishRefresh(false);
                LogUnit.log(msg);
            }
        });
    }

    private void runTasks(List<Object> keys) {
        auto.qinglong.net.panel.ApiController.runTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("执行成功");
                getTasks(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("执行失败：" + msg);
                LogUnit.log(msg);
            }
        });
    }

    private void stopTasks(List<Object> keys) {
        auto.qinglong.net.panel.ApiController.stopTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("终止成功");
                getTasks(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("终止失败：" + msg);
                LogUnit.log(msg);
            }
        });
    }

    private void enableTasks(List<Object> keys) {
        auto.qinglong.net.panel.ApiController.enableTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("启用成功");
                getTasks(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("启用失败：" + msg);
            }
        });
    }

    private void disableTasks(List<Object> keys) {
        auto.qinglong.net.panel.ApiController.disableTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("禁用成功");
                getTasks(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("禁用失败：" + msg);
            }
        });
    }

    private void pinTasks(List<Object> keys) {
        auto.qinglong.net.panel.ApiController.pinTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("顶置成功");
                getTasks(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("顶置失败：" + msg);
            }
        });
    }

    private void unpinTasks(List<Object> keys) {
        ApiController.unpinTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("取消顶置成功");
                getTasks(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("取消顶置失败：" + msg);
            }
        });
    }

    private void deleteTasks(List<Object> keys) {
        auto.qinglong.net.panel.ApiController.deleteTasks(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), keys, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ToastUnit.showShort("删除成功");
                getTasks(mCurrentSearchValue);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("删除失败：" + msg);
            }
        });

    }

    private void updateTask(Task task) {
        auto.qinglong.net.panel.ApiController.updateTask(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), task, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ui_pop_edit.dismiss();
                ToastUnit.showShort("编辑成功");
                getTasks(null);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("编辑失败：" + msg);
            }
        });
    }

    private void createTask(Task task) {
        auto.qinglong.net.panel.ApiController.createTask(PanelPreference.getBaseUrl(), PanelPreference.getAuthorization(), task, new auto.qinglong.net.panel.ApiController.BaseCallBack() {
            @Override
            public void onSuccess() {
                ui_pop_edit.dismiss();
                ToastUnit.showShort("新建任务成功");
                getTasks(null);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("新建任务失败：" + msg);
            }
        });
    }

    private void netMulAddTask(List<Task> tasks) {
//        new Thread(() -> {
//            final boolean[] isEnd = {false};
//
//            for (int k = 0; k < tasks.size(); k++) {
//                ui_pop_progress.setText("导入任务中 " + k + "/" + tasks.size());
//                ApiController.addTask(getNetRequestID(), tasks.get(k), new ApiController.NetEditTaskCallback() {
//                    @Override
//                    public void onSuccess(QLTask QLTask) {
//                        isEnd[0] = true;
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//                        isEnd[0] = true;
//                        LogUnit.log(TAG, msg);
//                    }
//                });
//                while (!isEnd[0]) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            ui_pop_progress.dismiss();
////            netGetTasks(mCurrentSearchValue, true);
//        }).start();
    }


}