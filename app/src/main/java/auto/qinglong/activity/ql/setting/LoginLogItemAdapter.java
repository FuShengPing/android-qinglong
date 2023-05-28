package auto.qinglong.activity.ql.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.bean.ql.QLLoginLog;
import auto.qinglong.utils.TimeUnit;

public class LoginLogItemAdapter extends RecyclerView.Adapter<LoginLogItemAdapter.MyViewHolder> {
    List<QLLoginLog> data;

    private final Context context;

    public LoginLogItemAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_login_log, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QLLoginLog loginLog = data.get(position);

        holder.ui_title.setText("[" + (position + 1) + "]");
        holder.ui_time.setText(TimeUnit.formatTimeA(loginLog.getTimestamp()));
        holder.ui_address.setText(loginLog.getAddress());
        holder.ui_ip.setText(loginLog.getIp());
        holder.ui_platform.setText(loginLog.getPlatform());
        if (loginLog.getStatus() == 0) {
            holder.ui_status.setText("成功");
            holder.ui_status.setTextColor(context.getColor(R.color.theme_color_shadow));
        } else {
            holder.ui_status.setText("失败");
            holder.ui_status.setTextColor(context.getColor(R.color.text_color_red));
        }
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<QLLoginLog> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ui_title;
        public TextView ui_time;
        public TextView ui_address;
        public TextView ui_ip;
        public TextView ui_platform;
        public TextView ui_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ui_title = itemView.findViewById(R.id.login_log_title);
            ui_time = itemView.findViewById(R.id.login_log_time);
            ui_address = itemView.findViewById(R.id.login_log_address);
            ui_ip = itemView.findViewById(R.id.login_log_ip);
            ui_platform = itemView.findViewById(R.id.login_log_platform);
            ui_status = itemView.findViewById(R.id.login_log_status);

        }
    }
}




