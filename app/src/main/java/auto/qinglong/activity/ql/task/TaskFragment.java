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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.ql.log.LogDetailActivity;
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
import auto.qinglong.views.popup.MiniMoreItem;
import auto.qinglong.views.popup.MiniMoreWindow;
import auto.qinglong.views.popup.PopupWindowBuilder;

public class TaskFragment extends BaseFragment {
    public static String TAG = "TaskFragment";

    private String mCurrentSearchValue = "";
    private MenuClickListener mMenuClickListener;
    private TaskAdapter mTaskAdapter;

    private enum BarType {NAV, SEARCH, MUL_ACTION}

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_task, null);

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

        ui_refresh = view.findViewById(R.id.refreshLayout);
        ui_recycler = view.findViewById(R.id.recyclerView);

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
        ui_recycler.getItemAnimator().setChangeDuration(0);
        ui_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        ui_recycler.setAdapter(mTaskAdapter);

        //列表item操作接口
        mTaskAdapter.setTaskInterface(new TaskAdapter.ItemActionListener() {
            @Override
            public void onLog(QLTask QLTask) {
                Intent intent = new Intent(getContext(), LogDetailActivity.class);
                intent.putExtra(LogDetailActivity.ExtraName, QLTask.getName());
                intent.putExtra(LogDetailActivity.ExtraPath, QLTask.getLogPath());
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
            public void onMulAction(QLTask QLTask, int position) {
                changeBar(BarType.MUL_ACTION);
            }
        });

        //刷新控件//
        //初始设置处于刷新状态
        ui_refresh.autoRefreshAnimationOnly();
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
        if (loadSuccessFlag || RequestManager.isRequesting(this.getNetRequestID())) {
            return;
        }
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
        miniMoreWindow.addItem(new MiniMoreItem("add", "新建任务", R.drawable.ic_add_gray));
        miniMoreWindow.addItem(new MiniMoreItem("localAdd", "本地导入", R.drawable.ic_file_gray));
        miniMoreWindow.addItem(new MiniMoreItem("backup", "任务备份", R.drawable.ic_backup_gray));
        miniMoreWindow.addItem(new MiniMoreItem("mulAction", "批量操作", R.drawable.ic_mul_action_gray));
        miniMoreWindow.setOnActionListener(key -> {
            switch (key) {
                case "add":
                    showPopWindowEdit(null);
                    break;
                case "localAdd":
                    localAddData();
                    break;
                case "backup":
                    backupData();
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

                WindowUnit.hideKeyboard(ui_root);

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

    private void changeBar(BarType barType) {
        if (ui_bar_search.getVisibility() == View.VISIBLE) {
            WindowUnit.hideKeyboard(ui_root);
            ui_bar_search.setVisibility(View.INVISIBLE);
            mCurrentSearchValue = "";
        }

        if (ui_bar_actions.getVisibility() == View.VISIBLE) {
            ui_bar_actions.setVisibility(View.INVISIBLE);
            mTaskAdapter.setCheckState(false, -1);
            ui_actions_select.setChecked(false);
        }

        ui_bar_main.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            ui_bar_main.setVisibility(View.VISIBLE);
        } else if (barType == BarType.SEARCH) {
            ui_bar_search.setVisibility(View.VISIBLE);
        } else {
            ui_actions_scroll.scrollTo(0, 0);
            mTaskAdapter.setCheckState(true, -1);
            ui_bar_actions.setVisibility(View.VISIBLE);
        }
    }

    private void localAddData() {
        List<File> files = FileUtil.getFiles(FileUtil.getTaskPath(), (dir, name) -> name.endsWith(".json"));
        if (files.size() == 0) {
            ToastUnit.showShort("无本地备份数据");
            return;
        }
        for (File file : files) {
            LogUnit.log(file.getName());
        }
    }

    private void backupData() {
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

        String fileName = TimeUnit.formatCurrentTime() + ".json";
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
                loadSuccessFlag = true;
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

    private void netEditTask(QLTask QLTask) {
        QLApiController.editTask(getNetRequestID(), QLTask, new QLApiController.NetEditTaskCallback() {
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

    private void netAddTask(QLTask QLTask) {
        QLApiController.addTask(getNetRequestID(), QLTask, new QLApiController.NetEditTaskCallback() {
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


}