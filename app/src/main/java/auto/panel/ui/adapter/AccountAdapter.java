package auto.panel.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import auto.panel.R;
import auto.panel.bean.app.Account;
import auto.panel.utils.NetUnit;

/**
 * @author: ASman
 * @date: 2024/2/5
 * @description:
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {
    Context context;
    ActionListener listener;
    List<Account> data;

    public AccountAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_account, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Account account = data.get(position);

        holder.uiUsername.setText(account.getUsername());
        holder.uiAddress.setText(NetUnit.getHost(account.getAddress()));
        holder.uiVersion.setText(account.getVersion());
        holder.uiDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(account, position);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(account, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<Account> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public interface ActionListener {
        void onDelete(Account account, int position);

        void onClick(Account account, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView uiUsername;
        protected TextView uiAddress;
        protected TextView uiVersion;
        protected View uiDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uiUsername = itemView.findViewById(R.id.item_account_username);
            uiVersion = itemView.findViewById(R.id.item_account_version);
            uiAddress = itemView.findViewById(R.id.item_account_address);
            uiDelete = itemView.findViewById(R.id.item_account_delete);
        }
    }
}


