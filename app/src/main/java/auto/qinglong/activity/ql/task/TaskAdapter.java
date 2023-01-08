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
        holder.layout_name.setText("[" + qlTask.getIndex() + "] " + qlTask.getName());
        holder.layout_command.setText(qlTask.getCommand());
        holder.layout_schedule.setText(qlTask.getSchedule());
        //运行状态
        if (qlTask.getTaskState() == QLTaskState.RUNNING) {
            holder.layout_state.setText("运行中");
            holder.layout_state.setTextColor(context.getColor(R.color.theme_color_shadow));
            holder.layout_action.setImageResource(R.drawable.ic_pause);
        } else if (qlTask.getTaskState() == QLTaskState.LIMIT) {
            holder.layout_state.setText("禁止中");
            holder.layout_state.setTextColor(context.getColor(R.color.text_color_red));
            holder.layout_action.setImageResource(R.drawable.ic_start);
        } else {
            holder.layout_state.setText("空闲中");
            holder.layout_state.setTextColor(context.getColor(R.color.text_color_49));
            holder.layout_action.setImageResource(R.drawable.ic_start);
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
        holder.layout_last_run_time.setText(str);

        //上次运行时间
        if (qlTask.getLast_execution_time() > 0) {
            str = TimeUnit.formatTimeA(qlTask.getLast_execution_time() * 1000);
        } else {
            str = "--";
        }
        holder.layout_last_execution_time.setText(str);

        //下次运行时间(判断定时规则是否合法)
        if (CronUnit.isValid(qlTask.getSchedule())) {
            holder.layout_next_execution_time.setText(CronUnit.nextExecutionTime(qlTask.getSchedule()));
        } else {
            holder.layout_next_execution_time.setText("--");
        }

        //复选框
        if (checkState) {
            holder.layout_check.setChecked(dataCheckState[position]);
            holder.layout_check.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
            holder.layout_check.setVisibility(View.VISIBLE);
        } else {
            holder.layout_check.setVisibility(View.GONE);
        }

        //顶置
        if (qlTask.getIsPinned() == 1) {
            holder.layout_pinned.setVisibility(View.VISIBLE);
        } else {
            holder.layout_pinned.setVisibility(View.GONE);
        }

        //监听
        if (itemActionListener == null) {
            return;
        }

        holder.layout_action.setOnClickListener(v -> {
            if (this.checkState) {
                holder.layout_check.setChecked(!holder.layout_check.isChecked());
            } else if (qlTask.getTaskState() != QLTaskState.RUNNING) {
                itemActionListener.onRun(qlTask);
            } else {
                itemActionListener.onStop(qlTask);
            }
        });

        holder.layout_name.setOnClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onLog(qlTask);
            } else {
                holder.layout_check.setChecked(!holder.layout_check.isChecked());
            }
        });

        holder.layout_name.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onMulAction(qlTask, holder.getAdapterPosition());
            }
            return true;
        });

        holder.layout_detail.setOnClickListener(v -> {
            if (this.checkState) {
                holder.layout_check.setChecked(!holder.layout_check.isChecked());
            }
        });

        holder.layout_detail.setOnLongClickListener(v -> {
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
    public TextView layout_name;
    public LinearLayout layout_detail;
    public TextView layout_command;
    public TextView layout_schedule;
    public TextView layout_state;
    public CheckBox layout_check;
    public ImageView layout_action;
    public ImageView layout_pinned;
    public TextView layout_last_run_time;
    public TextView layout_last_execution_time;
    public TextView layout_next_execution_time;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        layout_name = itemView.findViewById(R.id.task_name);
        layout_detail = itemView.findViewById(R.id.task_detail);
        layout_command = itemView.findViewById(R.id.task_command);
        layout_schedule = itemView.findViewById(R.id.task_schedule);
        layout_state = itemView.findViewById(R.id.task_state);
        layout_action = itemView.findViewById(R.id.task_action_run);
        layout_check = itemView.findViewById(R.id.task_check);
        layout_pinned = itemView.findViewById(R.id.task_pinned);
        layout_last_run_time = itemView.findViewById(R.id.task_last_running_time);
        layout_last_execution_time = itemView.findViewById(R.id.task_last_execution_time);
        layout_next_execution_time = itemView.findViewById(R.id.task_next_execution_time);
    }
}
