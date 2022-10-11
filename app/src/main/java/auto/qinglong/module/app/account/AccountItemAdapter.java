package auto.qinglong.module.app.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import auto.qinglong.R;

public class AccountItemAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Context context;
    private OnItemActionListener itemActionListener;

    public AccountItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }


    public void setItemActionListener(OnItemActionListener itemActionListener) {
        this.itemActionListener = itemActionListener;
    }

    public interface OnItemActionListener {
        void onClick();

        void onDelete();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
