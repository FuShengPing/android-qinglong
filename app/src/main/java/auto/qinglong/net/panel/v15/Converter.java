package auto.qinglong.net.panel.v15;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import auto.base.util.TimeUnit;
import auto.qinglong.bean.panel.File;
import auto.qinglong.bean.views.Task;
import auto.qinglong.utils.CronUnit;

/**
 * @author wsfsp4
 * @version 2023.07.06
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
            if (object.getLast_running_time() >= 60) {
                task.setLastRunningTime(String.format(Locale.CHINA, "%d分%d秒", object.getLast_running_time() / 60, object.getLast_running_time() % 60));
            } else if (object.getLast_running_time() > 0) {
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
        List<File> files = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return files;
        }

        for (LogFileListRes.FileObject object : objects) {
            File logFile = new File();
            logFile.setTitle(object.getTitle());
            logFile.setDir(object.isDir());
            logFile.setParent("");
            logFile.setPath(object.getTitle());
            logFile.setCreateTime(TimeUnit.formatDatetimeA(object.getMtime()));

            if (object.isDir()) {
                List<File> children = new ArrayList<>();
                for (LogFileListRes.FileObject childObject : object.getChildren()) {
                    File childFile = new File();
                    childFile.setDir(false);
                    childFile.setTitle(childObject.getTitle());
                    childFile.setParent(object.getTitle());
                    childFile.setPath(object.getTitle() + "/" + childObject.getTitle());
                    childFile.setCreateTime(TimeUnit.formatDatetimeA(childObject.getMtime()));
                    children.add(childFile);
                }
                logFile.setChildren(children);
            }
            files.add(logFile);
        }

        return files;
    }

    public static List<File> convertScriptFiles(List<ScriptFileListRes.FileObject> objects) {
        List<File> files = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return files;
        }

        for (ScriptFileListRes.FileObject object : objects) {
            File file = new File();
            file.setTitle(object.getTitle());
            file.setParent("");
            file.setDir(object.isDir());
            file.setCreateTime(TimeUnit.formatDatetimeA((long) object.getMtime()));
            file.setPath(object.getTitle());

            if (object.isDir()) {
                file.setChildren(buildChildren(file.getPath(), object.getChildren()));
            }

            files.add(file);
        }
        return files;
    }

    private static List<File> buildChildren(String parent, List<ScriptFileListRes.FileObject> objects) {
        List<File> children = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return children;
        }

        for (ScriptFileListRes.FileObject object : objects) {
            File childFile = new File();
            childFile.setTitle(object.getTitle());
            childFile.setParent(parent);
            childFile.setDir(object.isDir());
            childFile.setCreateTime(TimeUnit.formatDatetimeA((long) object.getMtime()));
            childFile.setPath(parent + "/" + object.getTitle());

            if (object.isDir()) {
                childFile.setChildren(buildChildren(childFile.getPath(), object.getChildren()));
            }

            children.add(childFile);
        }
        return children;
    }
}
