package auto.qinglong.ui.activity.ql.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.bean.ql.QLTask;
import auto.qinglong.bean.ql.QLTaskState;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    public static final String TAG = "TaskAdapter";

    Context context;
    private ItemActionListener itemActionListener;
    private List<QLTask> data;
    private boolean checkState;
    private boolean[] dataCheckState;

    private final int colorBlue;
    private final int colorRed;
    private final int colorGray;

    public TaskAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.checkState = false;
        this.colorBlue = context.getColor(R.color.theme_blue_color_shadow);
        this.colorRed = context.getColor(R.color.text_color_red);
        this.colorGray = context.getColor(R.color.text_color_49);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QLTask task = data.get(position);
        holder.ui_name.setText(task.getFormatName());
        holder.ui_command.setText(task.getCommand());
        holder.ui_schedule.setText(task.getSchedule());
        holder.ui_last_run_time.setText(task.getFormatLastRunningTime());
        holder.ui_last_execution_time.setText(task.getFormatLastExecutionTime());
        holder.ui_next_execution_time.setText(task.getFormatNextExecutionTime());
        //运行状态
        if (task.getTaskState() == QLTaskState.RUNNING) {
            holder.ui_state.setText("运行中");
            holder.ui_state.setTextColor(colorBlue);
            holder.ui_action.setImageResource(R.drawable.ic_blue_pause);
        } else if (task.getTaskState() == QLTaskState.WAITING) {
            holder.ui_state.setText("队列中");
            holder.ui_state.setTextColor(colorBlue);
            holder.ui_action.setImageResource(R.drawable.ic_blue_pause);
        } else if (task.getTaskState() == QLTaskState.LIMIT) {
            holder.ui_state.setText("禁止中");
            holder.ui_state.setTextColor(colorRed);
            holder.ui_action.setImageResource(R.drawable.ic_blue_start);
        } else {
            holder.ui_state.setText("空闲中");
            holder.ui_state.setTextColor(colorGray);
            holder.ui_action.setImageResource(R.drawable.ic_blue_start);
        }

        //顶置
        if (task.isPinned() == 1) {
            holder.ui_pinned.setVisibility(View.VISIBLE);
        } else {
            holder.ui_pinned.setVisibility(View.GONE);
        }

        //复选框
        if (checkState) {
            holder.ui_check.setChecked(dataCheckState[position]);
            holder.ui_check.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
            holder.ui_check.setVisibility(View.VISIBLE);
        } else {
            holder.ui_check.setVisibility(View.GONE);
        }

        holder.ui_name.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            } else {
                itemActionListener.onLog(task);
            }
        });

        holder.ui_name.setOnLongClickListener(v -> {
            if (!this.checkState && task.getCommand().startsWith("task ")) {
                String[] path = task.getCommand().replace("task", "").split("/");
                if (path.length == 1) {
                    itemActionListener.onScript("", path[0].trim());
                } else if (path.length == 2) {
                    itemActionListener.onScript(path[0].trim(), path[1].trim());
                }
            }
            return true;
        });

        holder.ui_action.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            } else if (task.getTaskState() == QLTaskState.LIMIT || task.getTaskState() == QLTaskState.FREE) {
                itemActionListener.onRun(task);
            } else {
                itemActionListener.onStop(task);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onEdit(task);
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

    public void setCheckState(boolean checkState) {
        this.checkState = checkState;
        Arrays.fill(this.dataCheckState, false);
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
        void onLog(QLTask task);

        void onStop(QLTask task);

        void onRun(QLTask task);

        void onEdit(QLTask task);

        void onScript(String parent, String fileName);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ui_name;
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
}


