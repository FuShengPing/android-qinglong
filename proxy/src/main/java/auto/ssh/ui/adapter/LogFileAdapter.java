package auto.ssh.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import auto.ssh.R;
import auto.ssh.bean.File;

/**
 * @author wsfsp4
 * @version 2023.07.29
 */
public class LogFileAdapter extends RecyclerView.Adapter<LogFileAdapter.MyViewHolder> {
    private Context context;
    private List<File> data;
    private OnItemActionListener listener;

    public LogFileAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.proxy_item_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File file = data.get(position);

        holder.uiTitle.setText(file.getName());

        if (listener != null) {
            holder.uiTitle.setOnClickListener(v -> listener.onClick(file));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public List<File> getData() {
        return data;
    }

    public void setData(List<File> data) {
        this.data = data;
    }

    public OnItemActionListener getListener() {
        return listener;
    }

    public void setListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView uiTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uiTitle = itemView.findViewById(R.id.proxy_item_file_title);
        }
    }

    public interface OnItemActionListener {
        void onClick(File file);
    }
}
