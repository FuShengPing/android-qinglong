package auto.qinglong.fragment.dependence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.api.object.Dependence;
import auto.qinglong.tools.TimeUnit;

public class DepItemAdapter extends RecyclerView.Adapter<MyViewHolder> {
    List<Dependence> data;
    private boolean checkState;
    private Boolean[] dataCheckState;

    ItemInterface itemInterface;
    Context context;

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
        Dependence dependence = data.get(position);

        holder.layout_title.setText(dependence.getName());
        holder.layout_time.setText(TimeUnit.formatTimeA(dependence.getCreated()));

        if (dependence.getStatus() == 0) {
            holder.layout_status.setText("安装中");
            holder.layout_status.setTextColor(context.getColor(R.color.text_color_49));
        } else if (dependence.getStatus() == 1) {
            holder.layout_status.setText("已安装");
            holder.layout_status.setTextColor(context.getColor(R.color.theme_color_shadow));
        } else if (dependence.getStatus() == 2) {
            holder.layout_status.setText("安装失败");
            holder.layout_status.setTextColor(context.getColor(R.color.text_color_red));
        }

        holder.layout_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemInterface.onDetail(dependence, holder.getAdapterPosition());
            }
        });

        holder.layout_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemInterface.onAction(dependence, holder.getAdapterPosition());
                return true;
            }
        });

        holder.layout_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemInterface.onReinstall(dependence, holder.getAdapterPosition());
            }
        });

        //处于选择状态
        if (this.checkState) {
            holder.layout_check.setChecked(this.dataCheckState != null && this.dataCheckState[position]);
            holder.layout_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    dataCheckState[holder.getAdapterPosition()] = isChecked;
                }
            });
            holder.layout_check.setVisibility(View.VISIBLE);
        } else {
            holder.layout_check.setVisibility(View.GONE);
        }


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

    public void setItemInterface(ItemInterface itemInterface) {
        this.itemInterface = itemInterface;
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
}

class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView layout_title;
    public TextView layout_time;
    public TextView layout_status;
    public CheckBox layout_check;
    public ImageView layout_bug;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        layout_title = itemView.findViewById(R.id.item_title);
        layout_time = itemView.findViewById(R.id.dep_item_time);
        layout_status = itemView.findViewById(R.id.dep_item_status);
        layout_check = itemView.findViewById(R.id.item_check);
        layout_bug = itemView.findViewById(R.id.dep_action_bug);

    }
}

interface ItemInterface {
    void onAction(Dependence dependence, int position);

    void onDetail(Dependence dependence, int position);

    void onReinstall(Dependence dependence, int position);
}
