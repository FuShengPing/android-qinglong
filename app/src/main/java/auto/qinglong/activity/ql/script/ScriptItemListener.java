package auto.qinglong.activity.ql.script;

import auto.qinglong.bean.ql.QLScript;

public interface ScriptItemListener {
    void onEdit(QLScript QLScript);
    void onAction(QLScript QLScript);
}
