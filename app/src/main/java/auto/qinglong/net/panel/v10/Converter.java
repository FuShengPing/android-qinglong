package auto.qinglong.net.panel.v10;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import auto.base.util.TimeUnit;
import auto.qinglong.bean.panel.File;
import auto.qinglong.bean.views.Task;
import auto.qinglong.utils.CronUnit;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class Converter {
    public static List<Task> convertTasks(List<TaskListRes.TaskObject> objects) {
        List<Task> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }
        for (TaskListRes.TaskObject object : objects) {
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
            result.add(task);
        }
        return result;
    }

    public static List<File> convertLogFiles(List<LogFileListRes.FileObject> objects) {
        List<File> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }
        for (LogFileListRes.FileObject object : objects) {
            File logFile = new File();
            logFile.setTitle(object.getName());
            logFile.setDir(object.isDir());
            logFile.setPath(object.getName());
            if (object.isDir()) {
                List<File> children = new ArrayList<>();
                for (String name : object.getFiles()) {
                    File child = new File();
                    child.setDir(false);
                    child.setTitle(name);
                    child.setParent(object.getName());
                    child.setPath(object.getName() + "/" + name);
                    children.add(child);
                }
                logFile.setChildren(children);
            }
            result.add(logFile);
        }
        return result;
    }

    public static List<File> convertScriptFiles(List<ScriptFileListRes.FileObject> objects) {
        List<File> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }
        for (ScriptFileListRes.FileObject object : objects) {
            File file = new File();
            file.setTitle(object.getTitle());
            file.setDir(object.isDir());
            file.setCreateTime(TimeUnit.formatDatetimeA((long) object.getMtime()));
            file.setPath(object.getTitle());
            if (object.isDir()) {
                List<File> children = new ArrayList<>();
                for (ScriptFileListRes.FileObject childObject : object.getChildren()) {
                    File childFile = new File();
                    childFile.setDir(false);
                    childFile.setTitle(childObject.getTitle());
                    childFile.setParent(object.getTitle());
                    childFile.setCreateTime(TimeUnit.formatDatetimeA((long) childObject.getMtime()));
                    childFile.setPath(object.getTitle() + "/" + childObject.getTitle());
                    children.add(childFile);
                }
                file.setChildren(children);
            }
            result.add(file);
        }
        return result;
    }
}
