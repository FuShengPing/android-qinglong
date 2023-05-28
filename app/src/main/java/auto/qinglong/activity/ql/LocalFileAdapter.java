package auto.qinglong.activity.ql;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import auto.qinglong.R;

public class LocalFileAdapter extends RecyclerView.Adapter<LocalFileAdapter.MyViewHolder> {
    private final Context context;
    private List<File> data;
    private OnActionListener listener;

    public LocalFileAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.pop_item_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File file = data.get(position);
        holder.ui_text.setText(file.getName());
        if (this.listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onSelect(file));
        }

    }

    @Override
    public int getItemCount() {
        return this.data != null ? this.data.size() : 0;
    }


    public List<File> getData() {
        return data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<File> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public OnActionListener getListener() {
        return listener;
    }

    public void setListener(OnActionListener listener) {
        this.listener = listener;
    }

    public interface OnActionListener {
        void onSelect(File file);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView ui_text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ui_text = itemView.findViewById(R.id.text);
        }
    }
}



