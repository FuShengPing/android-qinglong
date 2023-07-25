package auto.panel.ui.activity.panel.setting;

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

import auto.panel.R;
import auto.panel.bean.panel.LoginLog;

public class LoginLogItemAdapter extends RecyclerView.Adapter<LoginLogItemAdapter.MyViewHolder> {
    List<LoginLog> data;

    private final Context context;

    public LoginLogItemAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.panel_recycle_item_login_log, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LoginLog loginLog = data.get(position);

        holder.uiTitle.setText("[" + (position + 1) + "]");
        holder.uiTime.setText(loginLog.getTime());
        holder.uiAddress.setText(loginLog.getAddress());
        holder.uiIp.setText(loginLog.getIp());
        holder.uiPlatform.setText(loginLog.getPlatform());
        holder.uiStatus.setText(loginLog.getStatus());
        if (loginLog.getStatusCode() == LoginLog.STATUS_SUCCESS) {
            holder.uiStatus.setTextColor(context.getColor(R.color.theme_blue_shadow));
        } else {
            holder.uiStatus.setTextColor(context.getColor(R.color.text_color_red));
        }
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<LoginLog> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView uiTitle;
        public TextView uiTime;
        public TextView uiAddress;
        public TextView uiIp;
        public TextView uiPlatform;
        public TextView uiStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uiTitle = itemView.findViewById(R.id.login_log_title);
            uiTime = itemView.findViewById(R.id.login_log_time);
            uiAddress = itemView.findViewById(R.id.login_log_address);
            uiIp = itemView.findViewById(R.id.login_log_ip);
            uiPlatform = itemView.findViewById(R.id.login_log_platform);
            uiStatus = itemView.findViewById(R.id.login_log_status);

        }
    }
}




