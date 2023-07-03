package auto.qinglong.ui.activity.panel.log;

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
import auto.qinglong.bean.panel.File;

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
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_file, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File file = data.get(position);

        holder.ui_title.setText(file.getTitle());

        if (file.isDir()) {
            holder.ui_num.setText(file.getChildren().size() + " 项");
        } else {
            holder.ui_num.setText(null);
        }

        if (file.isDir()) {
            holder.ui_image.setImageResource(R.drawable.ic_blue_folder);
        } else {
            holder.ui_image.setImageResource(R.mipmap.ic_file_txt);
        }

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
        public ImageView ui_image;
        public TextView ui_title;
        public TextView ui_num;
        public TextView ui_time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ui_image = itemView.findViewById(R.id.item_file_image);
            ui_title = itemView.findViewById(R.id.item_file_title);
            ui_num = itemView.findViewById(R.id.item_file_num);
            ui_time = itemView.findViewById(R.id.item_file_time);
        }
    }
}




