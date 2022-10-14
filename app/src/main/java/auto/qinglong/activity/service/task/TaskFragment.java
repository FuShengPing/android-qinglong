package auto.qinglong.activity.service.task;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import auto.qinglong.R;
import auto.qinglong.activity.service.log.LogActivity;
import auto.qinglong.network.ApiController;
import auto.qinglong.network.response.TasksRes;
import auto.qinglong.activity.BaseFragment;
import auto.qinglong.tools.ToastUnit;
import auto.qinglong.tools.WindowUnit;
import auto.qinglong.network.RequestManager;

public class TaskFragment extends BaseFragment{
    public static String TAG = "TaskFragment";

    private String currentSearchValue = "";
    private MenuClickListener menuClickListener;
    private TaskAdapter taskAdapter;

    //弹窗编辑框
    private PopupWindow popupWindowMore;
    private PopupWindow popupWindowEdit;
    private TextView layout_edit_type;
    private TextView layout_edit_name;
    private TextView layout_edit_command;
    private TextView layout_edit_schedule;
    private Button layout_edit_save;
    //初始导航栏
    private LinearLayout layout_bar_nav;
    private ImageView layout_nav_menu;
    private ImageView layout_nav_search;
    private ImageView layout_nav_more;
    //搜索导航栏控件
    private LinearLayout layout_bar_search;
    private ImageView layout_search_back;
    private ImageView layout_search_confirm;
    private EditText layout_search_value;
    //批量操作导航栏控件
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

    private enum QueryType {QUERY, SEARCH, OTHER}

    private enum BarType {NAV, SEARCH, ACTIONS}

    public TaskFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_task, null);

        layout_root = view.findViewById(R.id.root);

        layout_bar = view.findViewById(R.id.task_bar);
        layout_bar_nav = view.findViewById(R.id.task_bar_nav);
        layout_nav_search = view.findViewById(R.id.task_bar_nav_search);
        layout_nav_more = view.findViewById(R.id.task_bar_nav_more);
        layout_nav_menu = view.findViewById(R.id.task_bar_nav_menu);

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
        firstLoad();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            firstLoad();
        }
    }

    /***
     * 首次加载数据(直至成功加载一次) 页面转入可见时调用
     */
    private void firstLoad() {
        if (haveFirstSuccess || RequestManager.isRequesting(this.getNetRequestID())) {
            return;
        }
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                getTasks(currentSearchValue, QueryType.QUERY);
            }
        }, 1000);
    }

    @Override
    public void init() {
        //item容器配置
        taskAdapter = new TaskAdapter(getContext());
        //取消更新动画，避免刷新闪烁
        Objects.requireNonNull(layout_recycler.getItemAnimator()).setChangeDuration(0);
        layout_recycler.setAdapter(taskAdapter);
        layout_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //列表item操作接口
        taskAdapter.setTaskInterface(new ItemInterface() {
            @Override
            public void onLog(Task task) {
                Intent intent = new Intent(getContext(), LogActivity.class);
                intent.putExtra(LogActivity.ExtraName, task.getName());
                intent.putExtra(LogActivity.ExtraPath, task.getLogPath());
                startActivity(intent);
            }

            @Override
            public void onStop(Task task) {
                if (RequestManager.isRequesting(getNetRequestID())) {
                    return;
                }
                List<String> ids = new ArrayList<>();
                ids.add(task.get_id());
                stopTasks(ids, false);
            }

            @Override
            public void onRun(Task task) {
                List<String> ids = new ArrayList<>();
                ids.add(task.get_id());
                runTasks(ids, false);
            }

            @Override
            public void onEdit(Task task) {
                showPopWindowEdit(task);
            }

            @Override
            public void onAction(Task task, int position) {
                if (!taskAdapter.getCheckState()) {
                    layout_actions_select.setChecked(false);
                    showBar(BarType.ACTIONS);
                }
            }
        });

        //刷新控件//
        //初始设置处于刷新状态
        layout_refresh.autoRefreshAnimationOnly();
        layout_refresh.setOnRefreshListener(refreshLayout -> getTasks(currentSearchValue, QueryType.QUERY));
        
        //导航点击监听
        layout_nav_menu.setOnClickListener(v -> {
            if (menuClickListener != null) {
                menuClickListener.onMenuClick();
            }
        });

        //搜索按键监听
        layout_nav_search.setOnClickListener(v -> {
            layout_search_value.setText(currentSearchValue);
            showBar(BarType.SEARCH);
        });

        //搜索返回按键监听
        layout_search_back.setOnClickListener(v -> showBar(BarType.NAV));

        //搜索确定监听
        layout_search_confirm.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }
            ToastUnit.showShort(getContext(), getString(R.string.tip_searching));
            currentSearchValue = layout_search_value.getText().toString();
            WindowUnit.hideKeyboard(layout_search_value);
            getTasks(currentSearchValue, QueryType.SEARCH);
        });

        //更多操作按键监听
        layout_nav_more.setOnClickListener(v -> showPopWindowMore());

        //批量操作返回
        layout_actions_back.setOnClickListener(v -> showBar(BarType.NAV));

        //全选监听
        layout_actions_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (taskAdapter.getCheckState()) {
                taskAdapter.selectAll(isChecked);
            }
        });

        //执行
        layout_actions_run.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<Task> tasks = taskAdapter.getCheckedItems();
                if (tasks.size() == 0) {
                    ToastUnit.showShort(getContext(), getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Task task : tasks) {
                        ids.add(task.get_id());
                    }
                    runTasks(ids, true);
                }
            }
        });

        //停止
        layout_actions_stop.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<Task> tasks = taskAdapter.getCheckedItems();
                if (tasks.size() == 0) {
                    ToastUnit.showShort(getContext(), getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Task task : tasks) {
                        ids.add(task.get_id());
                    }
                    stopTasks(ids, true);
                }
            }
        });

        //顶置
        layout_actions_pin.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<Task> tasks = taskAdapter.getCheckedItems();
                if (tasks.size() == 0) {
                    ToastUnit.showShort(getContext(), getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Task task : tasks) {
                        ids.add(task.get_id());
                    }
                    pinTasks(ids);
                }
            }
        });

        //取消顶置
        layout_actions_unpin.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<Task> tasks = taskAdapter.getCheckedItems();
                if (tasks.size() == 0) {
                    ToastUnit.showShort(getContext(), getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Task task : tasks) {
                        ids.add(task.get_id());
                    }
                    unpinTasks(ids);
                }
            }
        });

        //启用
        layout_actions_enable.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<Task> tasks = taskAdapter.getCheckedItems();
                if (tasks.size() == 0) {
                    ToastUnit.showShort(getContext(), getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Task task : tasks) {
                        ids.add(task.get_id());
                    }
                    enableTasks(ids);
                }
            }
        });

        //禁用
        layout_actions_disable.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<Task> tasks = taskAdapter.getCheckedItems();
                if (tasks.size() == 0) {
                    ToastUnit.showShort(getContext(), getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Task task : tasks) {
                        ids.add(task.get_id());
                    }
                    disableTasks(ids);
                }
            }
        });

        //删除
        layout_actions_delete.setOnClickListener(v -> {
            if (!RequestManager.isRequesting(getNetRequestID())) {
                List<Task> tasks = taskAdapter.getCheckedItems();
                if (tasks.size() == 0) {
                    ToastUnit.showShort(getContext(), getString(R.string.tip_empty_select));
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Task task : tasks) {
                        ids.add(task.get_id());
                    }
                    deleteTasks(ids);
                }
            }
        });
    }

    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    public void getTasks(String searchValue, QueryType queryType) {
        ApiController.getTasks(getNetRequestID(), searchValue, new ApiController.GetTasksCallback() {
            @Override
            public void onSuccess(TasksRes data) {
                haveFirstSuccess = true;
                taskAdapter.setData(TaskFragment.sortTasks(data.getData()));
                if (queryType == QueryType.QUERY) {
                    ToastUnit.showShort(getContext(), "加载成功");
                } else if (queryType == QueryType.SEARCH) {
                    ToastUnit.showShort(getContext(), "搜索成功");
                }
                this.onEnd(true);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "加载失败：" + msg);
                this.onEnd(false);
            }

            public void onEnd(boolean isSuccess) {
                if (layout_refresh.isRefreshing()) {
                    layout_refresh.finishRefresh(isSuccess);
                }
            }
        });
    }

    public void runTasks(List<String> ids, boolean isFromBar) {
        if (RequestManager.isRequesting(getNetRequestID())) {
            return;
        }
        ApiController.runTasks(getNetRequestID(), ids, new ApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (isFromBar && layout_bar_actions.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
                }
                ToastUnit.showShort(getContext(), "执行成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "执行失败：" + msg);
            }
        });

    }

    public void stopTasks(List<String> ids, boolean isFromBar) {
        ApiController.stopTasks(getNetRequestID(), ids, new ApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (isFromBar && layout_bar_actions.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
                }
                ToastUnit.showShort(getContext(), "终止成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "终止失败：" + msg);
            }
        });
    }

    public void enableTasks(List<String> ids) {
        ApiController.enableTasks(getNetRequestID(), ids, new ApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
                }
                ToastUnit.showShort(getContext(), "启用成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "启用失败：" + msg);
            }
        });
    }

    public void disableTasks(List<String> ids) {
        ApiController.disableTasks(getNetRequestID(), ids, new ApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
                }
                ToastUnit.showShort(getContext(), "禁用成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "禁用失败：" + msg);
            }
        });
    }

    public void pinTasks(List<String> ids) {
        ApiController.pinTasks(getNetRequestID(), ids, new ApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
                }
                ToastUnit.showShort(getContext(), getString(R.string.action_pin_success));
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), getString(R.string.action_pin_failure) + msg);
            }
        });
    }

    public void unpinTasks(List<String> ids) {
        ApiController.unpinTasks(getNetRequestID(), ids, new ApiController.RunTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
                }
                ToastUnit.showShort(getContext(), "取消顶置成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "取消顶置失败：" + msg);
            }
        });
    }

    public void deleteTasks(List<String> ids) {
        ApiController.deleteTasks(getNetRequestID(), ids, new ApiController.BaseCallback() {
            @Override
            public void onSuccess() {
                if (layout_actions_back.getVisibility() == View.VISIBLE) {
                    layout_actions_back.performClick();
                }
                ToastUnit.showShort(getContext(), "删除成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "删除失败：" + msg);
            }
        });
    }

    public void editTask(Task task) {
        ApiController.editTask(getNetRequestID(), task, new ApiController.EditTaskCallback() {
            @Override
            public void onSuccess(Task task) {
                popupWindowEdit.dismiss();
                ToastUnit.showShort(getContext(), "编辑成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "编辑失败：" + msg);
            }
        });
    }

    public void addTask(Task task) {
        ApiController.addTask(getNetRequestID(), task, new ApiController.EditTaskCallback() {
            @Override
            public void onSuccess(Task task) {
                popupWindowEdit.dismiss();
                ToastUnit.showShort(getContext(), "新建任务成功");
                getTasks(currentSearchValue, QueryType.OTHER);
            }

            @Override
            public void onFailure(String msg) {
                ToastUnit.showShort(getContext(), "新建任务失败：" + msg);
            }
        });
    }

    public void showPopWindowMore() {
        if (popupWindowMore == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_fg_more, null, false);
            LinearLayout layout_add = view.findViewById(R.id.pop_fg_more_add);
            LinearLayout layout_action = view.findViewById(R.id.pop_fg_more_action);
            TextView layout_add_text = view.findViewById(R.id.pop_fg_more_add_text);
            TextView layout_action_text = view.findViewById(R.id.pop_fg_more_action_text);
            layout_add_text.setText(getString(R.string.action_new_task));
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
                showPopWindowEdit(null);
            });

            layout_action.setOnClickListener(v -> {
                popupWindowMore.dismiss();
                showBar(BarType.ACTIONS);
            });
        }

        popupWindowMore.showAsDropDown(layout_bar, 0, 0, Gravity.END);
    }

    public void showPopWindowEdit(Task task) {
        if (popupWindowEdit == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_fg_task_edit, null, false);

            layout_edit_type = view.findViewById(R.id.task_edit_type);
            layout_edit_name = view.findViewById(R.id.task_edit_name);
            layout_edit_command = view.findViewById(R.id.task_edit_command);
            layout_edit_schedule = view.findViewById(R.id.task_edit_schedule);
            layout_edit_save = view.findViewById(R.id.task_edit_save);
            Button layout_edit_cancel = view.findViewById(R.id.task_edit_cancel);

            popupWindowEdit = new PopupWindow(getContext());
            popupWindowEdit.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowEdit.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindowEdit.setContentView(view);
            popupWindowEdit.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindowEdit.setOutsideTouchable(true);
            popupWindowEdit.setFocusable(true);
            popupWindowEdit.setAnimationStyle(R.style.anim_fg_task_pop_edit);

            //关闭弹窗
            layout_edit_cancel.setOnClickListener(v -> popupWindowEdit.dismiss());

            //取消蒙层
            popupWindowEdit.setOnDismissListener(() -> WindowUnit.setBackgroundAlpha(requireActivity(), 1.0f));
        }

        if (task != null) {
            layout_edit_type.setText(getString(R.string.action_edit_task));
            layout_edit_name.setText(task.getName());
            layout_edit_command.setText(task.getCommand());
            layout_edit_schedule.setText(task.getSchedule());
        } else {
            layout_edit_type.setText(getString(R.string.action_new_task));
            layout_edit_name.setText(null);
            layout_edit_command.setText(null);
            layout_edit_schedule.setText(null);
        }

        layout_edit_save.setOnClickListener(v -> {
            if (RequestManager.isRequesting(getNetRequestID())) {
                return;
            }

            WindowUnit.hideKeyboard(layout_edit_name);

            String name = layout_edit_name.getText().toString().trim();
            String command = layout_edit_command.getText().toString().trim();
            String schedule = layout_edit_schedule.getText().toString().trim();

            if (name.isEmpty()) {
                ToastUnit.showShort(getContext(), getString(R.string.tip_empty_task_name));
                return;
            }
            if (command.isEmpty()) {
                ToastUnit.showShort(getContext(), getString(R.string.tip_empty_command));
                return;
            }
            if (!CronUnit.isValid(schedule)) {
                ToastUnit.showShort(getContext(), getString(R.string.tip_invalid_schedule));
                return;
            }

            Task newTask = new Task();
            if (task == null) {
                newTask.setName(name);
                newTask.setCommand(command);
                newTask.setSchedule(schedule);
                addTask(newTask);
            } else {
                newTask.setName(name);
                newTask.setCommand(command);
                newTask.setSchedule(schedule);
                editTask(newTask);
            }
        });

        WindowUnit.setBackgroundAlpha(requireActivity(), 0.5f);
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
            taskAdapter.setCheckState(false, -1);
            layout_actions_select.setChecked(false);
        }

        layout_bar_nav.setVisibility(View.INVISIBLE);

        if (barType == BarType.NAV) {
            layout_bar_nav.setVisibility(View.VISIBLE);
        } else if (barType == BarType.SEARCH) {
            layout_bar_search.setVisibility(View.VISIBLE);
        } else {
            layout_actions_scroll.scrollTo(0, 0);
            taskAdapter.setCheckState(true, -1);
            layout_bar_actions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (layout_bar_actions.getVisibility() == View.VISIBLE) {
            showBar(BarType.NAV);
            return true;
        } else if (layout_bar_search.getVisibility() == View.VISIBLE) {
            showBar(BarType.NAV);
            return true;
        } else {
            return false;
        }
    }

    public static List<Task> sortTasks(@NonNull List<Task> fromData) {
        List<Task> runningData = new ArrayList<>();
        List<Task> pinnedData = new ArrayList<>();
        List<Task> freeData = new ArrayList<>();
        List<Task> limitData = new ArrayList<>();

        for (Task task : fromData) {
            if (task.getTaskState() == TaskState.RUNNING) {
                runningData.add(task);
            } else if (task.getIsPinned() == 1) {
                pinnedData.add(task);
            } else if (task.getTaskState() == TaskState.FREE) {
                freeData.add(task);
            } else {
                limitData.add(task);
            }
        }
        fromData.clear();
        fromData.addAll(runningData);
        fromData.addAll(pinnedData);
        fromData.addAll(freeData);
        fromData.addAll(limitData);
        return fromData;
    }
}