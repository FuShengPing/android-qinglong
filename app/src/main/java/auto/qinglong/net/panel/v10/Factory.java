package auto.qinglong.net.panel.v10;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import auto.base.util.TimeUnit;
import auto.qinglong.bean.views.Task;
import auto.qinglong.utils.CronUnit;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class Factory {
    public static List<Task> buildTasks(List<TaskObject> objects) {
        List<Task> results = new ArrayList<>();
        for (TaskObject object : objects) {
            Task task = new Task(object.getId());
            task.setTitle(object.getName());
            task.setPinned(object.getIsPinned() == 1);
            task.setCommand(object.getCommand());
            task.setSchedule(object.getSchedule());
            //任务下次执行时间
            task.setNextExecuteTime(CronUnit.nextExecutionTime(object.getSchedule(), "--"));
            //任务上次执行时间
            if (object.getLast_execution_time() > 0) {
                task.setLastExecuteTime(TimeUnit.formatDatetimeA(object.getLast_execution_time() * 1000));
            } else {
                task.setLastExecuteTime("--");
            }
            //任务执行时长
            if (object.getLast_running_time() > 0) {
                task.setLastRunningTime(String.format(Locale.CHINA, "%d秒", object.getLast_running_time()));
            } else {
                task.setLastRunningTime("--");
            }
            // 任务状态
            if (object.getStatus() == 0) {
                task.setState("运行中");
                task.setStateCode(Task.STATE_RUNNING);
            } else if (object.getStatus() == 3) {
                task.setState("等待中");
                task.setStateCode(Task.STATE_WAITING);
            } else if (object.getIsDisabled() == 1) {
                task.setState("已禁止");
                task.setStateCode(Task.STATE_LIMIT);
            } else {
                task.setState("空闲中");
                task.setStateCode(Task.STATE_FREE);
            }
            results.add(task);
        }
        return results;
    }
}
