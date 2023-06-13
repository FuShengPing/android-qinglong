package auto.qinglong.activity.ql.environment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.bean.ql.MoveInfo;
import auto.qinglong.bean.ql.QLEnvironment;
import auto.qinglong.utils.VibratorUtil;

public class EnvItemAdapter extends RecyclerView.Adapter<EnvItemAdapter.MyViewHolder> implements ItemMoveCallback {
    public static final String TAG = "EnvItemAdapter";

    private final Context context;
    private List<QLEnvironment> data;
    private ItemActionListener itemActionListener;
    private boolean checkState;
    private Boolean[] dataCheckState;

    private final int colorBlue;
    private final int colorRed;

    public EnvItemAdapter(@NonNull Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.checkState = false;

        this.colorBlue = context.getColor(R.color.theme_blue_color_shadow);
        this.colorRed = context.getColor(R.color.text_color_red);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_env, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QLEnvironment environment = this.data.get(position);
        holder.ui_name.setText(environment.getFormatName());
        holder.ui_value.setText(environment.getValue());
        holder.ui_createAt.setText(environment.getFormatCreated());

        if (environment.getRemarks() == null || environment.getRemarks().isEmpty()) {
            holder.ui_remark.setText("--");
        } else {
            holder.ui_remark.setText(environment.getRemarks());
        }

        if (environment.getStatus() == 0) {
            holder.ui_status.setTextColor(colorBlue);
            holder.ui_status.setText("已启用");
        } else {
            holder.ui_status.setTextColor(colorRed);
            holder.ui_status.setText("已禁用");
        }

        if (this.checkState) {
            holder.ui_check.setChecked(this.dataCheckState[position]);
            holder.ui_check.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getBindingAdapterPosition()] = isChecked);
            holder.ui_check.setVisibility(View.VISIBLE);
        } else {
            holder.ui_check.setVisibility(View.GONE);
        }

        holder.ui_body.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.ui_body.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onEdit(environment);
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : this.data.size();
    }

    @Override
    public void onItemMove(int from, int to) {
        Collections.swap(data, from, to);
        notifyItemMoved(from, to);
    }

    @Override
    public void onItemMoveStart() {
        VibratorUtil.vibrate(context, VibratorUtil.VIBRATE_SHORT);
    }

    @Override
    public void onItemMoveEnd(int start, int from, int to) {
        if (start != to) {
            MoveInfo moveInfo = new MoveInfo(data.get(to), from, data.get(from), to);
            itemActionListener.onMove(moveInfo);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<QLEnvironment> data) {
        this.data = data;
        if (data != null && data.size() > 0) {
            this.dataCheckState = new Boolean[this.data.size()];
            Arrays.fill(this.dataCheckState, false);
        }
        notifyDataSetChanged();
    }

    public List<QLEnvironment> getData() {
        return this.data;
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
        void onEdit(QLEnvironment environment);

        void onMove(MoveInfo info);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox ui_check;
        TextView ui_name;
        LinearLayout ui_body;
        TextView ui_value;
        TextView ui_remark;
        TextView ui_status;
        TextView ui_createAt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ui_check = itemView.findViewById(R.id.env_check);
            ui_name = itemView.findViewById(R.id.env_name);
            ui_body = itemView.findViewById(R.id.item_env_body);
            ui_value = itemView.findViewById(R.id.env_value);
            ui_status = itemView.findViewById(R.id.env_status);
            ui_remark = itemView.findViewById(R.id.env_remark);
            ui_createAt = itemView.findViewById(R.id.env_create_time);
        }
    }

}



