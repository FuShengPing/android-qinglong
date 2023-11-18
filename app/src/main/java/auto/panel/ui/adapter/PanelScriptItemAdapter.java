package auto.panel.ui.adapter;

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
import auto.panel.bean.panel.PanelFile;

public class PanelScriptItemAdapter extends RecyclerView.Adapter<PanelScriptItemAdapter.MyViewHolder> {
    public static final String TAG = "PanelScriptItemAdapter";

    private final Context context;
    private List<PanelFile> data;
    private ItemActionListener itemActionListener;

    public PanelScriptItemAdapter(@NonNull Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.panel_recycle_item_file, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PanelFile file = data.get(position);

        holder.ui_title.setText(file.getTitle());

        if (file.isDir()) {
            holder.ui_num.setText(file.getChildren().size() + " 项");
        } else {
            holder.ui_num.setText(null);
        }

        if (file.isDir()) {
            holder.ui_image.setImageResource(R.drawable.ic_blue_folder);
        } else if (file.getTitle().endsWith(".js")) {
            holder.ui_image.setImageResource(R.mipmap.ic_file_js);
        } else if (file.getTitle().endsWith(".py")) {
            holder.ui_image.setImageResource(R.mipmap.ic_file_py);
        } else if (file.getTitle().endsWith(".json")) {
            holder.ui_image.setImageResource(R.mipmap.ic_file_json);
        } else {
            holder.ui_image.setImageResource(R.mipmap.ic_file_unknow);
        }

        holder.ui_mtime.setText(file.getCreateTime());

        holder.itemView.setOnClickListener(v -> itemActionListener.onEdit(file));

        holder.itemView.setOnLongClickListener(v -> {
            itemActionListener.onMenu(v, file, holder.getAbsoluteAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<PanelFile> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setScriptInterface(ItemActionListener itemActionListener) {
        this.itemActionListener = itemActionListener;
    }

    public interface ItemActionListener {
        void onEdit(PanelFile file);

        void onMenu(View view, PanelFile file, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ui_image;
        public TextView ui_title;
        public TextView ui_num;
        public TextView ui_mtime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ui_image = itemView.findViewById(R.id.item_file_image);
            ui_title = itemView.findViewById(R.id.item_file_title);
            ui_num = itemView.findViewById(R.id.item_file_num);
            ui_mtime = itemView.findViewById(R.id.item_file_time);
        }
    }

}


