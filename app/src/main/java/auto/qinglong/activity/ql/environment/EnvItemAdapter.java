package auto.qinglong.activity.ql.environment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.bean.ql.QLEnvironment;
import auto.qinglong.utils.TimeUnit;

public class EnvItemAdapter extends RecyclerView.Adapter<EnvItemAdapter.MyViewHolder> {
    public static final String TAG = "EnvItemAdapter";

    private final Context context;
    private List<QLEnvironment> data;
    private ItemActionListener itemActionListener;
    private boolean checkState;
    private Boolean[] dataCheckState;

    public EnvItemAdapter(@NonNull Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.checkState = false;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_env, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QLEnvironment environment = this.data.get(position);
        holder.ui_name.setText(String.format("[%d] %s", environment.getIndex(), environment.getName()));
        holder.ui_value.setText(environment.getValue());

        if (this.checkState) {
            holder.ui_check.setChecked(this.dataCheckState[position]);
            holder.ui_check.setVisibility(View.VISIBLE);
        } else {
            holder.ui_check.setVisibility(View.GONE);
        }

        if (environment.getRemarks() == null || environment.getRemarks().isEmpty()) {
            holder.ui_remark.setText("--");
        } else {
            holder.ui_remark.setText(environment.getRemarks());
        }

        if (environment.getStatus() == 0) {
            holder.ui_status.setTextColor(context.getColor(R.color.theme_color_shadow));
            holder.ui_status.setText("已启用");
        } else {
            holder.ui_status.setTextColor(context.getColor(R.color.text_color_red));
            holder.ui_status.setText("已禁用");
        }

        holder.ui_createAt.setText(TimeUnit.formatTimeA(environment.getCreated()));

        holder.ui_name.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.ui_name.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onMulAction();
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onEdit(environment, holder.getAdapterPosition());
            }
            return true;
        });

        holder.ui_check.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : this.data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<QLEnvironment> data) {
        this.data.clear();
        this.data.addAll(data);
        this.dataCheckState = new Boolean[this.data.size()];
        Arrays.fill(this.dataCheckState, false);
        notifyDataSetChanged();
    }

    public List<QLEnvironment> getData() {
        return this.data;
    }

    public boolean isCheckState() {
        return checkState;
    }

    public void setCheckState(boolean checkState) {
        this.checkState = checkState;
        Arrays.fill(this.dataCheckState, false);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setAllChecked(boolean checked) {
        if (checkState) {
            Arrays.fill(this.dataCheckState, checked);
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public List<QLEnvironment> getSelectedItems() {
        List<QLEnvironment> environments = new ArrayList<>();
        for (int k = 0; k < this.dataCheckState.length; k++) {
            if (this.dataCheckState[k]) {
                environments.add(this.data.get(k));
            }
        }
        return environments;
    }

    public void setItemInterface(ItemActionListener itemActionListener) {
        this.itemActionListener = itemActionListener;
    }

    public interface ItemActionListener {
        void onEdit(QLEnvironment environment, int position);

        void onMulAction();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox ui_check;
        public TextView ui_name;
        public TextView ui_value;
        public TextView ui_remark;
        public TextView ui_status;
        public TextView ui_createAt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ui_check = itemView.findViewById(R.id.env_check);
            ui_name = itemView.findViewById(R.id.env_name);
            ui_value = itemView.findViewById(R.id.env_value);
            ui_status = itemView.findViewById(R.id.env_status);
            ui_remark = itemView.findViewById(R.id.env_remark);
            ui_createAt = itemView.findViewById(R.id.env_create_time);
        }
    }

}



