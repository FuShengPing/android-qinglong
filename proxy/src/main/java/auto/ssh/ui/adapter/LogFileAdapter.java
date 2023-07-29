package auto.ssh.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import auto.ssh.bean.File;

/**
 * @author wsfsp4
 * @version 2023.07.29
 */
public class LogFileAdapter extends RecyclerView.Adapter<LogFileAdapter.MyViewHolder> {
    private Context context;
    private List<File> data;

    public LogFileAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public List<File> getData() {
        return data;
    }

    public void setData(List<File> data) {
        this.data = data;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
