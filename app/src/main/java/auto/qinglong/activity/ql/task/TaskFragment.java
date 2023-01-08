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

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.activity.ql.log.LogDetailActivity;
import auto.qinglong.bean.ql.QLTask;
import auto.qinglong.bean.ql.network.QLTasksRes;
import auto.qinglong.network.http.QLApiController;
import auto.qinglong.network.http.RequestManager;
import auto.qinglong.utils.CronUnit;
import auto.qinglong.utils.TextUnit;
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

    //PopupWindow
    private EditWindow ui_pop_edit;
    //主导航栏
    private LinearLayout ui_bar_main;
    private ImageView ui_nav_menu;
    private ImageView ui_nav_search;
    private ImageView ui_nav_more;
    //搜索导航栏
    private LinearLayout layout_bar_search;
    private ImageView layout_search_back;
    private ImageView layout_search_confirm;
    private EditText layout_search_value;
    //操作导航栏
    private LinearLayout layout_bar_actions;
    private ImageView layout_actions_back;
    private CheckBox layout_actions_select;
    private HorizontalScrollView layout_actions_scroll;
    private LinearLayout layout_actions_run;
    private LinearLayout layout_actions_stop;
    private LinearLayout layout_actions_pin;
    private LinearLayout layout_actions_unpin;
    private LinearLayout layout_actions_enable;
    private LinearLayout layout_actions_disable;
    private LinearLayout layout_actions_delete;
    //布局控件
    private LinearLayout layout_root;
    private RelativeLayout layout_bar;
    private SmartRefreshLayout layout_refresh;
    private RecyclerView layout_recycler;

    private enum BarType {NAV, SEARCH, MUL_ACTION}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_task, null);

        layout_root = view.findViewById(R.id.root);

        layout_bar = view.findViewById(R.id.task_bar);
        ui_bar_main = view.findViewById(R.id.task_bar_nav);
        ui_nav_search = view.findViewById(R.id.task_bar_nav_search);
        ui_nav_more = view.findViewById(R.id.task_bar_nav_more);
        ui_nav_menu = view.findViewById(R.id.task_bar_nav_menu);

        layout_bar_search = view.findViewById(R.id.task_bar_search);
        layout_search_back = view.findViewById(R.id.task_bar_search_back);
        layout_search_value = view.findViewById(R.id.task_bar_search_value);
        layout_search_confirm = view.findViewById(R.id.task_bar_search_confirm);

        layout_bar_actions = view.findViewById(R.id.task_bar_actions);
        layout_actions_select = view.findViewById(R.id.task_bar_actions_select_all);
        layout_actions_scroll = view.findViewById(R.id.task_bar_actions_scroll);
        layout_actions_back = view.findViewById(R.id.task_bar_actions_back);
        layout_actions_run = view.findViewById(R.id.task_bar_actions_run);
        layout_actions_stop = view.findViewById(R.id.task_bar_actions_stop);
        layout_actions_pin = view.findViewById(R.id.task_bar_actions_pinned);
        layout_actions_unpin = view.findViewById(R.id.task_bar_actions_unpinned);
        layout_actions_enable = view.findViewById(R.id.task_bar_actions_enable);
        layout_actions_disable = view.findViewById(R.id.task_bar_actions_disable);
        layout_actions_delete = view.findViewById(R.id.task_bar_actions_delete);

        layout_refresh = view.findViewById(R.id.refreshLayout);
        layout_recycler = view.findViewById(R.id.recyclerView);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkFirstLoad();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            checkFirstLoad();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (layout_bar_actions.getVisibility() == View.VISIBLE) {
            changeBar(BarType.NAV);
            return true;
        } else if (layout_bar_search.getVisibility() == View.VISIBLE) {
            changeBar(BarType.NAV);
            return true;
        } else {
            return false;
        }
    }

    /***
     * 首次加载数据(直至成功加载一次) 页面转入可见时调用
     */
    private void checkFirstLoad() {
        if (loadSuccessFlag || RequestManager.isRequesting(this.getNetRequestID())) {
            return;
        }
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                netGetTasks(mCurrentSearchValue, true);
            }
        }, 1000);
    }

    @Override
    public void init() {
        //item容器配置
        mTaskAdapter = new TaskAdapter(getContext());
        //取消更新动画，避免刷新闪烁
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);
        layout_recycler.setAdapter(mTaskAdapter);
        layout_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

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
        layout_refresh.autoRefreshAnimationOnly();
        layout_refresh.setOnRefreshListener(refreshLayout -> netGetTasks(mCurrentSearchValue, true));

        //导航点击监听
        ui_nav_menu.setOnClickListener(v -> {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick();
            }
        });

        //搜索按键监听
        ui_nav_search.setOnClickListener(v -> {
            layout_search_value.setText(mCurrentSearchValue);
            changeBar(BarType.SEARCH);
        });

        //搜索返回按键监听
        layout_search_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //搜索确定监听
        layout_search_confirm.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }
            ToastUnit.showShort(getString(R.string.tip_searching));
            mCurrentSearchValue = layout_search_value.getText().toString();
            WindowUnit.hideKeyboard(layout_search_value);
            netGetTasks(mCurrentSearchValue, true);
        });

        //更多操作按键监听
        ui_nav_more.setOnClickListener(v -> showPopWindowMiniMore());

        //批量操作返回
        layout_actions_back.setOnClickListener(v -> changeBar(BarType.NAV));

        //全选监听
        layout_actions_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mTaskAdapter.getCheckState()) {
                mTaskAdapter.selectAll(isChecked);
            }
        });

        //执行
        layout_actions_run.setOnClickListener(v -> {
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
        layout_actions_stop.setOnClickListener(v -> {
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
        layout_actions_pin.setOnClickListener(v -> {
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
        layout_actions_unpin.setOnClickListener(v -> {
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
        layout_actions_enable.setOnClickListener(v -> {
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
        layout_actions_disable.setOnClickListener(v -> {
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
        layout_actions_delete.setOnClickListener(v -> {
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

    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.mMenuClickListener = menuClickListener;
    }

    public void showPopWindowMiniMore() {
        MiniMoreWindow miniMoreWindow = new MiniMoreWindow();
        miniMoreWindow.setTargetView(layout_bar);
        miniMoreWindow.setGravity(Gravity.END);
        miniMoreWindow.addItem(new MiniMoreItem("add", "新建任务", R.drawable.ic_add_gray));
        miniMoreWindow.addItem(new MiniMoreItem("mulAction", "批量操作", R.drawable.ic_mul_action_gray));
        miniMoreWindow.setOnActionListener(key -> {
            if (key.equals("add")) {
                showPopWindowEdit(null);
            } else {
                changeBar(BarType.MUL_ACTION);
            }
            return true;
        });
        PopupWindowBuilder.buildMiniMoreWindow(requireActivity(), miniMoreWindow);
    }

    public void showPopWindowEdit(QLTask qlTask) {
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

                WindowUnit.hideKeyboard(layout_root);

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

    public void changeBar(BarType barType) {
        if (layout_bar_search.getVisibility() == View.VISIBLE) {
            WindowUnit.hideKeyboard(layout_root);
            layout_bar_search.setVisibility(View.INVISIBLE);
            mCurrentSearchValue = "";
        }

        if (layout_bar_actions.getVisibility() == View.VISIBLE) {
            layout_bar_actions.setVisibility(View.INVISIBLE);
            mTaskAdapter.setCheckState(false, -1);
            layout_actions_select.setChecked(false);
        }

        ui_bar_main.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            ui_bar_main.setVisibility(View.VISIBLE);
        } else if (barType == BarType.SEARCH) {
            layout_bar_search.setVisibility(View.VISIBLE);
        } else {
            layout_actions_scroll.scrollTo(0, 0);
            mTaskAdapter.setCheckState(true, -1);
            layout_bar_actions.setVisibility(View.VISIBLE);
        }
    }

    public void netGetTasks(String searchValue, boolean needTip) {
        QLApiController.getTasks(getNetRequestID(), searchValue, new QLApiController.GetTasksCallback() {
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
                layout_refresh.finishRefresh(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort("加载失败：" + msg);
                layout_refresh.finishRefresh(false);
            }
        });
    }

    public void netRunTasks(List<String> ids, boolean isFromBar) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        QLApiController.runTasks(getNetRequestID(), ids, new QLApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (isFromBar && layout_bar_actions.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
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

    public void netStopTasks(List<String> ids, boolean isFromBar) {
        QLApiController.stopTasks(getNetRequestID(), ids, new QLApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (isFromBar && layout_bar_actions.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
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

    public void netEnableTasks(List<String> ids) {
        QLApiController.enableTasks(getNetRequestID(), ids, new QLApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
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

    public void netDisableTasks(List<String> ids) {
        QLApiController.disableTasks(getNetRequestID(), ids, new QLApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
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

    public void netPinTasks(List<String> ids) {
        QLApiController.pinTasks(getNetRequestID(), ids, new QLApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
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

    public void netUnpinTasks(List<String> ids) {
        QLApiController.unpinTasks(getNetRequestID(), ids, new QLApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
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

    public void netDeleteTasks(List<String> ids) {
        QLApiController.deleteTasks(getNetRequestID(), ids, new QLApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
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

    public void netEditTask(QLTask QLTask) {
        QLApiController.editTask(getNetRequestID(), QLTask, new QLApiController.EditTaskCallback() {
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

    public void netAddTask(QLTask QLTask) {
        QLApiController.addTask(getNetRequestID(), QLTask, new QLApiController.EditTaskCallback() {
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