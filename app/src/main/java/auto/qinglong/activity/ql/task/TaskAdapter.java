package auto.qinglong.activity.ql.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.bean.ql.QLTask;
import auto.qinglong.bean.ql.QLTaskState;
import auto.qinglong.utils.CronUnit;
import auto.qinglong.utils.TimeUnit;

public class TaskAdapter extends RecyclerView.Adapter<MyViewHolder> {
    public static final String TAG = "TaskAdapter";

    Context context;
    private ItemActionListener itemActionListener;
    private List<QLTask> data;
    private boolean checkState;
    private boolean[] dataCheckState;

    public TaskAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.checkState = false;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QLTask qlTask = data.get(position);
        holder.ui_name.setText("[" + qlTask.getIndex() + "] " + qlTask.getName());
        holder.ui_command.setText(qlTask.getCommand());
        holder.ui_schedule.setText(qlTask.getSchedule());
        //运行状态
        if (qlTask.getTaskState() == QLTaskState.RUNNING) {
            holder.ui_state.setText("运行中");
            holder.ui_state.setTextColor(context.getColor(R.color.theme_color_shadow));
            holder.ui_action.setImageResource(R.drawable.ic_pause);
        } else if (qlTask.getTaskState() == QLTaskState.WAITING) {
            holder.ui_state.setText("队列中");
            holder.ui_state.setTextColor(context.getColor(R.color.theme_color_shadow));
            holder.ui_action.setImageResource(R.drawable.ic_pause);
        } else if (qlTask.getTaskState() == QLTaskState.LIMIT) {
            holder.ui_state.setText("禁止中");
            holder.ui_state.setTextColor(context.getColor(R.color.text_color_red));
            holder.ui_action.setImageResource(R.drawable.ic_start);
        } else {
            holder.ui_state.setText("空闲中");
            holder.ui_state.setTextColor(context.getColor(R.color.text_color_49));
            holder.ui_action.setImageResource(R.drawable.ic_start);
        }

        //上次运行时长
        @SuppressLint("DefaultLocale") String str;
        if (qlTask.getLast_running_time() >= 60) {
            str = String.format("%d分%d秒", qlTask.getLast_running_time() / 60, qlTask.getLast_running_time() % 60);
        } else if (qlTask.getLast_running_time() > 0) {
            str = String.format("%d秒", qlTask.getLast_running_time());
        } else {
            str = "--";
        }
        holder.ui_last_run_time.setText(str);

        //上次运行时间
        if (qlTask.getLast_execution_time() > 0) {
            str = TimeUnit.formatTimeA(qlTask.getLast_execution_time() * 1000);
        } else {
            str = "--";
        }
        holder.ui_last_execution_time.setText(str);

        //下次运行时间(判断定时规则是否合法)
        if (CronUnit.isValid(qlTask.getSchedule())) {
            holder.ui_next_execution_time.setText(CronUnit.nextExecutionTime(qlTask.getSchedule()));
        } else {
            holder.ui_next_execution_time.setText("--");
        }

        //复选框
        if (checkState) {
            holder.ui_check.setChecked(dataCheckState[position]);
            holder.ui_check.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
            holder.ui_check.setVisibility(View.VISIBLE);
        } else {
            holder.ui_check.setVisibility(View.GONE);
        }

        //顶置
        if (qlTask.getIsPinned() == 1) {
            holder.ui_pinned.setVisibility(View.VISIBLE);
        } else {
            holder.ui_pinned.setVisibility(View.GONE);
        }

        //监听
        if (itemActionListener == null) {
            return;
        }

        holder.ui_action.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            } else if (qlTask.getTaskState() == QLTaskState.LIMIT || qlTask.getTaskState() == QLTaskState.FREE) {
                itemActionListener.onRun(qlTask);
            } else {
                itemActionListener.onStop(qlTask);
            }
        });

        holder.ui_name.setOnClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onLog(qlTask);
            } else {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.ui_name.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onMulAction(qlTask, holder.getAdapterPosition());
            }
            return true;
        });

        holder.ui_detail.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.ui_detail.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onEdit(qlTask);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<QLTask> data) {
        this.data.clear();
        this.data = data;
        this.dataCheckState = new boolean[data.size()];
        notifyDataSetChanged();
    }

    public List<QLTask> getData() {
        return this.data;
    }

    public void setTaskInterface(ItemActionListener itemActionListener) {
        this.itemActionListener = itemActionListener;
    }

    public boolean getCheckState() {
        return checkState;
    }

    public void setCheckState(boolean checkState, int position) {
        this.checkState = checkState;
        Arrays.fill(this.dataCheckState, false);
        if (checkState && position > -1) {
            this.dataCheckState[position] = true;
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    public void selectAll(boolean isSelected) {
        if (this.checkState) {
            Arrays.fill(this.dataCheckState, isSelected);
            notifyItemRangeChanged(0, this.data.size());
        }
    }

    public List<QLTask> getCheckedItems() {
        List<QLTask> QLTasks = new ArrayList<>();
        if (dataCheckState != null) {
            for (int k = 0; k < dataCheckState.length; k++) {
                if (dataCheckState[k]) {
                    QLTasks.add(this.data.get(k));
                }
            }
        }
        return QLTasks;
    }

    public interface ItemActionListener {
        void onLog(QLTask QLTask);

        void onStop(QLTask QLTask);

        void onRun(QLTask QLTask);

        void onEdit(QLTask QLTask);

        void onMulAction(QLTask QLTask, int position);
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView ui_name;
    public LinearLayout ui_detail;
    public TextView ui_command;
    public TextView ui_schedule;
    public TextView ui_state;
    public CheckBox ui_check;
    public ImageView ui_action;
    public ImageView ui_pinned;
    public TextView ui_last_run_time;
    public TextView ui_last_execution_time;
    public TextView ui_next_execution_time;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        ui_name = itemView.findViewById(R.id.task_name);
        ui_detail = itemView.findViewById(R.id.task_detail);
        ui_command = itemView.findViewById(R.id.task_command);
        ui_schedule = itemView.findViewById(R.id.task_schedule);
        ui_state = itemView.findViewById(R.id.task_state);
        ui_action = itemView.findViewById(R.id.task_action_run);
        ui_check = itemView.findViewById(R.id.task_check);
        ui_pinned = itemView.findViewById(R.id.task_pinned);
        ui_last_run_time = itemView.findViewById(R.id.task_last_running_time);
        ui_last_execution_time = itemView.findViewById(R.id.task_last_execution_time);
        ui_next_execution_time = itemView.findViewById(R.id.task_next_execution_time);
    }
}
