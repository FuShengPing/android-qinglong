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
        holder.uiTitle.setText(task.getTitle());
        holder.uiCommand.setText(task.getCommand());
        holder.uiSchedule.setText(task.getSchedule());
        holder.uiLastRunTime.setText(task.getLastRunningTime());
        holder.uiLastExecutionTime.setText(task.getLastExecuteTime());
        holder.uiNextExecutionTime.setText(task.getNextExecuteTime());
        holder.uiState.setText(task.getState());

        if (task.getStateCode() == Task.STATE_RUNNING) {
            holder.uiState.setTextColor(colorBlue);
            holder.uiAction.setImageResource(R.drawable.ic_blue_pause);
        } else if (task.getStateCode() == Task.STATE_WAITING) {
            holder.uiState.setTextColor(colorBlue);
            holder.uiAction.setImageResource(R.drawable.ic_blue_pause);
        } else if (task.getStateCode() == Task.STATE_LIMIT) {
            holder.uiState.setTextColor(colorRed);
            holder.uiAction.setImageResource(R.drawable.ic_blue_start);
        } else {
            holder.uiState.setTextColor(colorGray);
            holder.uiAction.setImageResource(R.drawable.ic_blue_start);
        }

        if (this.onCheck) {
            holder.uiAction.setVisibility(View.INVISIBLE);
        } else {
            holder.uiAction.setVisibility(View.VISIBLE);
        }

        if (task.isPinned()) {
            holder.uiPinned.setVisibility(View.VISIBLE);
        } else {
            holder.uiPinned.setVisibility(View.GONE);
        }

        //复选框
        if (onCheck) {
            holder.uiCheck.setChecked(dataCheckState[position]);
            holder.uiCheck.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
            holder.uiCheck.setVisibility(View.VISIBLE);
        } else {
            holder.uiCheck.setVisibility(View.GONE);
        }

        holder.uiTitle.setOnClickListener(v -> {
            if (this.onCheck) {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
            } else {
                actionListener.onLog(task);
            }
        });

        holder.uiTitle.setOnLongClickListener(v -> {
            if (!this.onCheck) {
                actionListener.onScript(task);
            }
            return true;
        });

        holder.uiAction.setOnClickListener(v -> {
            if (this.onCheck) {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
            } else if (task.getStateCode() == Task.STATE_LIMIT || task.getStateCode() == Task.STATE_FREE) {
                actionListener.onRun(task);
            } else {
                actionListener.onStop(task);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (this.onCheck) {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
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

    public void setCheckState(boolean onCheck) {
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
        public TextView uiTitle;
        public TextView uiCommand;
        public TextView uiSchedule;
        public TextView uiState;
        public CheckBox uiCheck;
        public ImageView uiAction;
        public ImageView uiPinned;
        public TextView uiLastRunTime;
        public TextView uiLastExecutionTime;
        public TextView uiNextExecutionTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uiTitle = itemView.findViewById(R.id.task_title);
            uiCommand = itemView.findViewById(R.id.task_command);
            uiSchedule = itemView.findViewById(R.id.task_schedule);
            uiState = itemView.findViewById(R.id.task_state);
            uiAction = itemView.findViewById(R.id.task_action_run);
            uiCheck = itemView.findViewById(R.id.task_check);
            uiPinned = itemView.findViewById(R.id.task_pinned);
            uiLastRunTime = itemView.findViewById(R.id.task_last_running_time);
            uiLastExecutionTime = itemView.findViewById(R.id.task_last_execution_time);
            uiNextExecutionTime = itemView.findViewById(R.id.task_next_execution_time);
        }
    }
}


