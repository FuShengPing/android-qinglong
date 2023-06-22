package auto.qinglong.activity.ql.script;

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
import auto.qinglong.bean.ql.QLScript;
import auto.base.util.TimeUnit;

public class ScriptAdapter extends RecyclerView.Adapter<ScriptAdapter.MyViewHolder> {
    public static final String TAG = "ScriptAdapter";

    private final Context context;
    private List<QLScript> data;
    private ItemActionListener itemActionListener;

    public ScriptAdapter(@NonNull Context context) {
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
        QLScript qlScript = data.get(position);

        holder.ui_title.setText(qlScript.getTitle());

        if (qlScript.isDirectory()) {
            holder.ui_num.setText(qlScript.getChildren().size() + " 项");
        } else {
            holder.ui_num.setText(null);
        }

        if (qlScript.isDirectory()) {
            holder.ui_image.setImageResource(R.drawable.ic_blue_folder);
        } else if (qlScript.getType() == QLScript.Type.JavaScript) {
            holder.ui_image.setImageResource(R.mipmap.ic_file_js);
        } else if (qlScript.getType() == QLScript.Type.Python) {
            holder.ui_image.setImageResource(R.mipmap.ic_file_py);
        } else if (qlScript.getType() == QLScript.Type.Json) {
            holder.ui_image.setImageResource(R.mipmap.ic_file_json);
        } else {
            holder.ui_image.setImageResource(R.mipmap.ic_file_unknow);
        }

        holder.ui_mtime.setText(TimeUnit.datetimeFormatTimeB((long) qlScript.getMtime()));

        holder.itemView.setOnClickListener(v -> itemActionListener.onEdit(qlScript));

        holder.itemView.setOnLongClickListener(v -> {
            itemActionListener.onMenu(v, qlScript, holder.getAbsoluteAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<QLScript> data) {
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
        void onEdit(QLScript QLScript);

        void onMenu(View view, QLScript QLScript, int position);
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


