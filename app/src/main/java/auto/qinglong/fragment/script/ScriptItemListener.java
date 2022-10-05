package auto.qinglong.fragment.script;

import auto.qinglong.api.object.Script;

public interface ScriptItemListener {
    void onEdit(Script script);
    void onAction(Script script); 
}
