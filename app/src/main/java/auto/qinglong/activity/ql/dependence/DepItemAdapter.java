package auto.qinglong.activity.ql.dependence;

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
import auto.qinglong.bean.ql.QLDependence;
import auto.qinglong.utils.TimeUnit;

public class DepItemAdapter extends RecyclerView.Adapter<DepItemAdapter.MyViewHolder> {
    List<QLDependence> data;
    private boolean checkState;
    private Boolean[] dataCheckState;

    private ItemActionListener itemActionListener;
    private final Context context;

    public DepItemAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.checkState = false;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dep, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QLDependence dependence = data.get(position);

        holder.ui_title.setText(dependence.getName());
        holder.ui_time.setText(TimeUnit.formatTimeA(dependence.getCreated()));

        if (dependence.getStatus() == 0) {
            holder.ui_status.setText("安装中");
            holder.ui_status.setTextColor(context.getColor(R.color.theme_color_shadow));
        } else if (dependence.getStatus() == 1) {
            holder.ui_status.setText("已安装");
            holder.ui_status.setTextColor(context.getColor(R.color.theme_color_shadow));
        } else if (dependence.getStatus() == 2) {
            holder.ui_status.setText("安装失败");
            holder.ui_status.setTextColor(context.getColor(R.color.text_color_red));
        } else if (dependence.getStatus() == 3) {
            holder.ui_status.setText("删除中");
            holder.ui_status.setTextColor(context.getColor(R.color.text_color_red));
        } else if (dependence.getStatus() == 5) {
            holder.ui_status.setText("卸载失败");
            holder.ui_status.setTextColor(context.getColor(R.color.text_color_red));
        } else {
            holder.ui_status.setText("未知");
            holder.ui_status.setTextColor(context.getColor(R.color.text_color_49));
        }

        //处于选择状态
        if (this.checkState) {
            holder.ui_check.setChecked(this.dataCheckState != null && this.dataCheckState[position]);
            holder.ui_check.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
            holder.ui_check.setVisibility(View.VISIBLE);
        } else {
            holder.ui_check.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

        holder.ui_title.setOnClickListener(v -> {
            if (this.checkState) {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            } else {
                itemActionListener.onDetail(dependence, holder.getAdapterPosition());
            }
        });

        holder.ui_title.setOnLongClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onMulAction(dependence, holder.getAdapterPosition());
            }
            return true;
        });

        holder.ui_bug.setOnClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onReinstall(dependence, holder.getAdapterPosition());
            } else {
                holder.ui_check.setChecked(!holder.ui_check.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<QLDependence> data) {
        this.data.clear();
        this.data.addAll(data);
        this.dataCheckState = new Boolean[this.data.size()];
        Arrays.fill(this.dataCheckState, false);
        notifyDataSetChanged();
    }

    public void setItemInterface(ItemActionListener itemActionListener) {
        this.itemActionListener = itemActionListener;
    }

    public boolean getCheckState() {
        return checkState;
    }

    /**
     * 设置是否进入选择状态
     */
    public void setCheckState(boolean isChecked, int position) {
        this.checkState = isChecked;
        Arrays.fill(this.dataCheckState, false);
        if (isChecked && position > -1) {
            this.dataCheckState[position] = true;
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * 全选或取消全选
     */
    public void setAllChecked(boolean isChecked) {
        if (this.checkState) {
            Arrays.fill(this.dataCheckState, isChecked);
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    /**
     * 获取被选中的item
     */
    public List<QLDependence> getCheckedItems() {
        List<QLDependence> dependencies = new ArrayList<>();
        if (this.dataCheckState != null) {
            for (int k = 0; k < this.dataCheckState.length; k++) {
                if (this.dataCheckState[k]) {
                    dependencies.add(this.data.get(k));
                }
            }
        }
        return dependencies;
    }

    public interface ItemActionListener {
        void onMulAction(QLDependence dependence, int position);

        void onDetail(QLDependence dependence, int position);

        void onReinstall(QLDependence dependence, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ui_title;
        public TextView ui_time;
        public TextView ui_status;
        public CheckBox ui_check;
        public ImageView ui_bug;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ui_title = itemView.findViewById(R.id.item_title);
            ui_time = itemView.findViewById(R.id.dep_item_time);
            ui_status = itemView.findViewById(R.id.dep_item_status);
            ui_check = itemView.findViewById(R.id.item_check);
            ui_bug = itemView.findViewById(R.id.dep_action_bug);

        }
    }
}




