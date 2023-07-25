package auto.panel.ui.activity.panel.log;

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

import auto.panel.R;
import auto.panel.bean.panel.File;

@SuppressLint("SetTextI18n")
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyViewHolder> {
    private final Context context;
    private final List<File> data;
    private ItemActionListener itemActionListener;

    public LogAdapter(@NonNull Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.panel_recycle_item_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File file = data.get(position);

        holder.uiTitle.setText(file.getTitle());

        if (file.isDir()) {
            holder.uiNum.setText(file.getChildren().size() + " 项");
        } else {
            holder.uiNum.setText(null);
        }

        if (file.isDir()) {
            holder.uiImage.setImageResource(R.drawable.ic_blue_folder);
        } else {
            holder.uiImage.setImageResource(R.mipmap.ic_file_txt);
        }

        holder.uiTime.setText(file.getCreateTime());

        holder.itemView.setOnClickListener(v -> itemActionListener.onClick(file));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<File> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }


    public void setItemActionListener(ItemActionListener itemActionListener) {
        this.itemActionListener = itemActionListener;
    }

    public interface ItemActionListener {
        void onClick(File file);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView uiImage;
        public TextView uiTitle;
        public TextView uiNum;
        public TextView uiTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uiImage = itemView.findViewById(R.id.item_file_image);
            uiTitle = itemView.findViewById(R.id.item_file_title);
            uiNum = itemView.findViewById(R.id.item_file_num);
            uiTime = itemView.findViewById(R.id.item_file_time);
        }
    }
}




