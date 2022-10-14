package auto.qinglong.activity.service.task;

public interface ItemInterface {
    void onLog(Task task);

    void onStop(Task task);

    void onRun(Task task);

    void onEdit(Task task);

    void onAction(Task task,int position);
}
