package auto.qinglong.activity.module.log;

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

public class LogAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<QLLog> data;
    private LogInterFace logInterFace;

    public LogAdapter(@NonNull Context context) {
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
        QLLog QLLog = data.get(position);

        holder.layout_title.setText(QLLog.getName());

        if (QLLog.isDir()) {
            holder.layout_num.setText(QLLog.getFiles().size() + " 项");
        } else {
            holder.layout_num.setText(null);
        }

        if (QLLog.isDir()) {
            holder.layout_image.setImageResource(R.drawable.ic_folder);
        } else {
            holder.layout_image.setImageResource(R.mipmap.ic_file_txt);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInterFace.onItemClick(QLLog);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<QLLog> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }


    public void setLogInterFace(LogInterFace logInterFace) {
        this.logInterFace = logInterFace;
    }
}

interface LogInterFace {
    void onItemClick(QLLog QLLog);
}

class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView layout_image;
    public TextView layout_title;
    public TextView layout_num;
    public TextView layout_time;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        layout_image = itemView.findViewById(R.id.item_file_image);
        layout_title = itemView.findViewById(R.id.item_file_title);
        layout_num = itemView.findViewById(R.id.item_file_num);
        layout_time = itemView.findViewById(R.id.item_file_time);
    }
}


