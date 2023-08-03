package auto.panel.ui.adapter;

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

import auto.panel.R;
import auto.panel.bean.panel.Dependence;

public class PanelDependenceItemAdapter extends RecyclerView.Adapter<PanelDependenceItemAdapter.MyViewHolder> {
    public static String TAG = "PanelDependenceItemAdapter";

    private final Context context;
    private ItemActionListener itemActionListener;
    private final List<Dependence> data;
    private boolean checkState;
    private Boolean[] dataCheckState;

    private final int colorBlue;
    private final int colorRed;
    private final int colorGray;

    public PanelDependenceItemAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.checkState = false;

        this.colorBlue = context.getColor(R.color.theme_blue_shadow);
        this.colorRed = context.getColor(R.color.text_color_red);
        this.colorGray = context.getColor(R.color.text_color_49);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.panel_recycle_item_dep, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Dependence dependence = data.get(position);

        holder.uiTitle.setText(dependence.getTitle());
        holder.uiTime.setText(dependence.getCreateTime());
        holder.uiStatus.setText(dependence.getStatus());

        if (dependence.getStatusCode() == Dependence.STATUS_INSTALLING) {
            holder.uiStatus.setTextColor(colorBlue);
        } else if (dependence.getStatusCode() == Dependence.STATUS_INSTALLED) {
            holder.uiStatus.setTextColor(colorBlue);
        } else if (dependence.getStatusCode() == Dependence.STATUS_INSTALL_FAILURE) {
            holder.uiStatus.setTextColor(colorRed);
        } else if (dependence.getStatusCode() == Dependence.STATUS_UNINSTALLING) {
            holder.uiStatus.setTextColor(colorRed);
        } else if (dependence.getStatusCode() == Dependence.STATUS_UNINSTALL_FAILURE) {
            holder.uiStatus.setTextColor(colorRed);
        } else {
            holder.uiStatus.setTextColor(colorGray);
        }

        //处于选择状态
        if (this.checkState) {
            holder.uiCheck.setChecked(this.dataCheckState != null && this.dataCheckState[position]);
            holder.uiCheck.setOnCheckedChangeListener((buttonView, isChecked) -> dataCheckState[holder.getAdapterPosition()] = isChecked);
            holder.uiCheck.setVisibility(View.VISIBLE);
        } else {
            holder.uiCheck.setVisibility(View.GONE);
        }

        holder.uiTitle.setOnClickListener(v -> {
            if (this.checkState) {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
            } else {
                itemActionListener.onDetail(dependence, holder.getAdapterPosition());
            }
        });

        holder.uiBug.setOnClickListener(v -> {
            if (!this.checkState) {
                itemActionListener.onReinstall(dependence, holder.getAdapterPosition());
            } else {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (this.checkState) {
                holder.uiCheck.setChecked(!holder.uiCheck.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Dependence> data) {
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
    public void setCheckState(boolean isChecked) {
        this.checkState = isChecked;
        Arrays.fill(this.dataCheckState, false);
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
    public List<Dependence> getCheckedItems() {
        List<Dependence> dependencies = new ArrayList<>();
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
        void onDetail(Dependence dependence, int position);

        void onReinstall(Dependence dependence, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView uiTitle;
        public TextView uiTime;
        public TextView uiStatus;
        public CheckBox uiCheck;
        public ImageView uiBug;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uiTitle = itemView.findViewById(R.id.item_title);
            uiTime = itemView.findViewById(R.id.dep_item_time);
            uiStatus = itemView.findViewById(R.id.dep_item_status);
            uiCheck = itemView.findViewById(R.id.item_check);
            uiBug = itemView.findViewById(R.id.dep_action_bug);
        }
    }
}




