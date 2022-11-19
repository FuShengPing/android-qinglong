package auto.qinglong.activity.app.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import auto.qinglong.R;

public class AccountItemAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Context context;
    private OnItemActionListener itemActionListener;

    private List<Account> data;

    private boolean isDeleteState = false;

    public AccountItemAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Account account = data.get(position);

        holder.layout_txt_username.setText(account.getUsername());
        holder.layout_txt_address.setText(account.getAddress());


        if (isDeleteState) {//处于删除状态
            holder.layout_img_selected.setVisibility(View.GONE);
            holder.layout_img_delete.setVisibility(View.VISIBLE);
            holder.layout_img_delete.setOnClickListener(v -> itemActionListener.onDelete(account, holder.getBindingAdapterPosition()));
        } else {
            holder.layout_img_delete.setVisibility(View.GONE);
            if (account.isCurrent()) {
                holder.layout_img_selected.setVisibility(View.VISIBLE);
            } else {
                holder.layout_img_selected.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(v -> itemActionListener.onClick(account, holder.getBindingAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Account> data) {
        this.data.clear();
        if (data != null) {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setDeleteState(boolean flag) {
        if (this.isDeleteState == flag) {
            return;
        }
        this.isDeleteState = flag;
        notifyItemRangeChanged(0, data.size());
    }

    public boolean getDeleteState() {
        return this.isDeleteState;
    }

    public void setItemActionListener(OnItemActionListener itemActionListener) {
        this.itemActionListener = itemActionListener;
    }

    public interface OnItemActionListener {
        void onClick(Account account, int position);

        void onDelete(Account account, int position);
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    protected ImageView layout_img_delete;
    protected ImageView layout_img_selected;
    protected TextView layout_txt_username;
    protected TextView layout_txt_address;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        layout_img_delete = itemView.findViewById(R.id.item_delete);
        layout_img_selected = itemView.findViewById(R.id.item_selected);
        layout_txt_address = itemView.findViewById(R.id.txt_address);
        layout_txt_username = itemView.findViewById(R.id.txt_username);
    }
}
