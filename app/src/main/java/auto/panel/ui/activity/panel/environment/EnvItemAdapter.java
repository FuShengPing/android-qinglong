package auto.panel.ui.activity.panel.environment;

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

import auto.panel.R;
import auto.panel.bean.panel.Environment;
import auto.panel.bean.panel.MoveInfo;

public class EnvItemAdapter extends RecyclerView.Adapter<EnvItemAdapter.MyViewHolder> implements ItemMoveCallback {
    public static final String TAG = "EnvItemAdapter";

    private final Context context;
    private List<Environment> data;
    private ItemActionListener itemActionListener;
    private boolean checkState;
    private Boolean[] dataCheckState;

    private final int colorBlue;
    private final int colorRed;

    public EnvItemAdapter(@NonNull Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.checkState = false;

        this.colorBlue = context.getColor(R.color.theme_blue_shadow);
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
        Environment environment = this.data.get(position);
        holder.uiName.setText(environment.getName());
        holder.uiValue.setText(environment.getValue());
        holder.uiTime.setText(environment.getTime());

        if (environment.getRemark() == null || environment.getRemark().isEmpty()) {
            holder.uiRemark.setText("--");
        } else {
            holder.uiRemark.setText(environment.getRemark());
        }

        holder.uiStatus.setText(environment.getStatus());

        if (environment.getStatusCode() == Environment.STATUS_ENABLE) {
            holder.uiStatus.setTextColor(colorBlue);
        } else {
            holder.uiStatus.setTextColor(colorRed);
        }

        if (this.checkState) {
            holder.uiCheck.setChecked(this.dataCheckState[position]);
            holder.uiCheck.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getBindingAdapterPosition()] = isChecked);
            holder.uiCheck.setVisibility(View.VISIBLE);
        } else {
            holder.uiCheck.setVisibility(View.GONE);
        }

        holder.uiBody.setOnClickListener(v -> {
            if (this.checkState) {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
            }
        });

        holder.uiBody.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onEdit(environment);
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (this.checkState) {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
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
//        VibratorUtil.vibrate(context, VibratorUtil.VIBRATE_SHORT);
    }

    @Override
    public void onItemMoveEnd(int start, int from, int to) {
//        if (start != to) {
//            MoveInfo moveInfo = new MoveInfo(data.get(to), from, data.get(from), to);
//            itemActionListener.onMove(moveInfo);
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Environment> data) {
        this.data = data;
        if (data != null && data.size() > 0) {
            this.dataCheckState = new Boolean[this.data.size()];
            Arrays.fill(this.dataCheckState, false);
        }
        notifyDataSetChanged();
    }

    public List<Environment> getData() {
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

    public List<Environment> getSelectedItems() {
        List<Environment> environments = new ArrayList<>();
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
        void onEdit(Environment environment);

        void onMove(MoveInfo info);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox uiCheck;
        LinearLayout uiBody;
        TextView uiName;
        TextView uiValue;
        TextView uiRemark;
        TextView uiStatus;
        TextView uiTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uiCheck = itemView.findViewById(R.id.env_check);
            uiName = itemView.findViewById(R.id.env_name);
            uiBody = itemView.findViewById(R.id.item_env_body);
            uiValue = itemView.findViewById(R.id.env_value);
            uiStatus = itemView.findViewById(R.id.env_status);
            uiRemark = itemView.findViewById(R.id.env_remark);
            uiTime = itemView.findViewById(R.id.env_create_time);
        }
    }
}



