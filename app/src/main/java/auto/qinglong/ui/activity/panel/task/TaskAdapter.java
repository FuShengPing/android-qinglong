package auto.qinglong.ui.activity.panel.task;

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
import auto.qinglong.bean.views.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    public static final String TAG = "TaskAdapter";

    Context context;
    private ActionListener actionListener;
    private List<Task> data;
    private boolean onCheck;
    private boolean[] dataCheckState;

    private final int colorBlue;
    private final int colorRed;
    private final int colorGray;

    public TaskAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.onCheck = false;
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
        Task task = data.get(position);
        holder.ui_title.setText(task.getTitle());
        holder.ui_command.setText(task.getCommand());
        holder.ui_schedule.setText(task.getSchedule());
        holder.ui_last_run_time.setText(task.getLastRunningTime());
        holder.ui_last_execution_time.setText(task.getLastExecuteTime());
        holder.ui_next_execution_time.setText(task.getNextExecuteTime());
        holder.ui_state.setText(task.getState());

        if (task.getStateCode() == Task.STATE_RUNNING) {
            holder.ui_state.setTextColor(colorBlue);
            holder.ui_action.setImageResource(R.drawable.ic_blue_pause);
        } else if (task.getStateCode() == Task.STATE_WAITING) {
            holder.ui_state.setTextColor(colorBlue);
            holder.ui_action.setImageResource(R.drawable.ic_blue_pause);
        } else if (task.getStateCode() == Task.STATE_LIMIT) {
            holder.ui_state.setTextColor(colorRed);
            holder.ui_action.setImageResource(R.drawable.ic_blue_start);
        } else {
            holder.ui_state.setTextColor(colorGray);
            holder.ui_action.setImageResource(R.drawable.ic_blue_start);
        }

        if (this.onCheck) {
            holder.ui_action.setVisibility(View.INVISIBLE);
        } else {
            holder.ui_action.setVisibility(View.VISIBLE);
        }

        if (task.isPinned()) {
            holder.ui_pinned.setVisibility(View.VISIBLE);
        } else {
            holder.ui_pinned.setVisibility(View.GONE);
        }

        //复选框
        if (onCheck) {
            holder.ui_check.setChecked(dataCheckState[position]);
            holder.ui_check.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
            holder.ui_check.setVisibility(View.VISIBLE);
        } else {
            holder.ui_check.setVisibility(View.GONE);
        }

        holder.ui_title.setOnClickListener(v -> {
            if (this.onCheck) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            } else {
                actionListener.onLog(task);
            }
        });

        holder.ui_title.setOnLongClickListener(v -> {
            if(!this.onCheck){
                actionListener.onScript(task);
            }
            return true;
        });

        holder.ui_action.setOnClickListener(v -> {
            if (this.onCheck) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            } else if (task.getStateCode() == Task.STATE_LIMIT || task.getStateCode() == Task.STATE_FREE) {
                actionListener.onRun(task);
            } else {
                actionListener.onStop(task);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (this.onCheck) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!this.onCheck) {
                actionListener.onEdit(task);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Task> data) {
        this.data.clear();
        this.data = data;
        this.dataCheckState = new boolean[data.size()];
        notifyDataSetChanged();
    }

    public List<Task> getData() {
        return this.data;
    }

    public void setActionListener(ActionListener itemActionListener) {
        this.actionListener = itemActionListener;
    }

    public boolean getOnCheck() {
        return onCheck;
    }

    public void setOnCheck(boolean onCheck) {
        this.onCheck = onCheck;
        Arrays.fill(this.dataCheckState, false);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void selectAll(boolean isSelected) {
        if (this.onCheck) {
            Arrays.fill(this.dataCheckState, isSelected);
            notifyItemRangeChanged(0, this.data.size());
        }
    }

    public List<Task> getCheckedItems() {
        List<Task> tasks = new ArrayList<>();
        if (dataCheckState != null) {
            for (int k = 0; k < dataCheckState.length; k++) {
                if (dataCheckState[k]) {
                    tasks.add(this.data.get(k));
                }
            }
        }
        return tasks;
    }

    public interface ActionListener {
        void onRun(Task task);

        void onStop(Task task);

        void onEdit(Task task);

        void onLog(Task task);

        void onScript(Task task);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ui_title;
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
            ui_title = itemView.findViewById(R.id.task_title);
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


