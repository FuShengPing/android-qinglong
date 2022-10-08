package auto.qinglong.module.server.script;

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
import auto.qinglong.tools.TimeUnit;

public class ScriptAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Script> data;
    private ScriptItemListener scriptInterface;

    public ScriptAdapter(@NonNull Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Script script = data.get(position);

        holder.layout_title.setText(script.getTitle());

        if (script.getChildren() == null) {
            holder.layout_num.setText(null);
        } else {
            holder.layout_num.setText(script.getChildren().size() + " 项");
        }

        if (script.getChildren() != null) {
            holder.layout_image.setImageResource(R.drawable.ic_folder);
        } else if (script.getTitle().matches(".*\\.(js)|(JS)$")) {
            holder.layout_image.setImageResource(R.mipmap.ic_file_js);
        } else if (script.getTitle().matches(".*\\.(py)|(PY)$")) {
            holder.layout_image.setImageResource(R.mipmap.ic_file_py);
        } else if (script.getTitle().matches(".*\\.(json)|(JSON)$")) {
            holder.layout_image.setImageResource(R.mipmap.ic_file_json);
        } else {
            holder.layout_image.setImageResource(R.mipmap.ic_file_unknow);
        }

        holder.layout_mtime.setText(TimeUnit.formatTimeB((long) script.getMtime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scriptInterface.onEdit(script);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scriptInterface.onAction(script);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Script> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }


    public void setScriptInterface(ScriptItemListener scriptInterface) {
        this.scriptInterface = scriptInterface;
    }

}


class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView layout_image;
    public TextView layout_title;
    public TextView layout_num;
    public TextView layout_mtime;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        layout_image = itemView.findViewById(R.id.item_file_image);
        layout_title = itemView.findViewById(R.id.item_file_title);
        layout_num = itemView.findViewById(R.id.item_file_num);
        layout_mtime = itemView.findViewById(R.id.item_file_time);
    }
}


