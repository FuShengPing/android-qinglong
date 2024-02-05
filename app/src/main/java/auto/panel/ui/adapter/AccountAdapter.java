package auto.panel.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import auto.panel.R;

/**
 * @author: ASman
 * @date: 2024/2/5
 * @description:
 */
public class AccountAdapter extends RecyclerView.Adapter<PanelTaskItemAdapter.MyViewHolder> {
    Context context;

    public AccountAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public PanelTaskItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_account, parent, false);
        return new PanelTaskItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanelTaskItemAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
