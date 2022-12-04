package auto.qinglong.activity.plugin.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import auto.qinglong.R;
import auto.qinglong.bean.app.WebRule;

public class RuleItemAdapter extends RecyclerView.Adapter<RuleItemAdapter.MyViewHolder> {
    public static final String TAG = "RuleItemAdapter";
    private List<WebRule> data;
    private OnActionListener actionListener;
    private Context context;

    public RuleItemAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plugin_web_rule, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WebRule webRule = data.get(position);
        holder.ui_env.setText(webRule.getEnvName());
        holder.ui_name.setText(webRule.getName());
        holder.ui_url.setText(webRule.getUrl());
        holder.ui_target.setText(webRule.getTarget());
        holder.ui_main.setText(webRule.getMain());
        holder.ui_join.setText(webRule.getJoinChar());
        holder.ui_check.setChecked(webRule.isChecked());

        holder.ui_check.setOnCheckedChangeListener((buttonView, isChecked) -> actionListener.onCheck(isChecked, holder.getLayoutPosition(), webRule.getId()));

        holder.itemView.setOnClickListener(v -> holder.ui_check.setChecked(!holder.ui_check.isChecked()));

        holder.itemView.setOnLongClickListener(v -> {
            actionListener.onAction(v, holder.getLayoutPosition(), webRule);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<WebRule> getData() {
        return data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<WebRule> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public OnActionListener getActionListener() {
        return actionListener;
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView ui_env;
        protected TextView ui_name;
        protected TextView ui_url;
        protected TextView ui_target;
        protected TextView ui_main;
        protected TextView ui_join;
        protected CheckBox ui_check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ui_env = itemView.findViewById(R.id.web_rule_item_env);
            ui_name = itemView.findViewById(R.id.web_rule_item_name);
            ui_url = itemView.findViewById(R.id.web_rule_item_url);
            ui_target = itemView.findViewById(R.id.web_rule_item_target);
            ui_main = itemView.findViewById(R.id.web_rule_item_main);
            ui_join = itemView.findViewById(R.id.web_rule_item_join);
            ui_check = itemView.findViewById(R.id.web_rule_item_check);
        }
    }

    public interface OnActionListener {
        void onCheck(boolean isChecked, int position, int id);

        void onAction(View view, int position, WebRule rule);
    }
}
