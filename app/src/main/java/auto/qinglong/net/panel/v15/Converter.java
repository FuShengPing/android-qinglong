package auto.qinglong.net.panel.v15;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import auto.base.util.LogUnit;
import auto.base.util.TimeUnit;
import auto.qinglong.bean.panel.Dependence;
import auto.qinglong.bean.panel.File;
import auto.qinglong.bean.panel.SystemConfig;
import auto.qinglong.bean.views.Task;
import auto.qinglong.utils.CronUnit;

/**
 * @author wsfsp4
 * @version 2023.07.06
 */
public class Converter {
    public static List<Task> convertTasks(List<TasksRes.TaskObject> objects) {
        List<Task> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }
        for (TasksRes.TaskObject object : objects) {
            Task task = new Task(object.getId());
            task.setTitle(object.getName());
            task.setPinned(object.getIsPinned() == 1);
            task.setCommand(object.getCommand());
            task.setSchedule(object.getSchedule());
            //任务上次执行时间
            if (object.getLastExecutionTime() > 0) {
                task.setLastExecuteTime(TimeUnit.formatDatetimeA(object.getLastExecutionTime() * 1000));
            } else {
                task.setLastExecuteTime("--");
            }
            //任务上次执行时长
            if (object.getLast_running_time() >= 60) {
                task.setLastRunningTime(String.format(Locale.CHINA, "%d分%d秒", object.getLast_running_time() / 60, object.getLast_running_time() % 60));
            } else if (object.getLast_running_time() > 0) {
                task.setLastRunningTime(String.format(Locale.CHINA, "%d秒", object.getLast_running_time()));
            } else {
                task.setLastRunningTime("--");
            }
            //任务下次执行时间
            task.setNextExecuteTime(CronUnit.nextExecutionTime(object.getSchedule(), "--"));
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

    public static List<File> convertLogFiles(List<LogFilesRes.FileObject> objects) {
        List<File> files = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return files;
        }

        for (LogFilesRes.FileObject object : objects) {
            File logFile = new File();
            logFile.setTitle(object.getTitle());
            logFile.setDir(object.isDir());
            logFile.setParent("");
            logFile.setPath(object.getTitle());
            logFile.setCreateTime(TimeUnit.formatDatetimeA(object.getMtime()));

            if (object.isDir()) {
                List<File> children = new ArrayList<>();
                for (LogFilesRes.FileObject childObject : object.getChildren()) {
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

    public static List<Dependence> convertDependencies(List<DependenciesRes.DependenceObject> objects) {
        List<Dependence> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }

        for (DependenciesRes.DependenceObject object : objects) {
            Dependence dependence = new Dependence();
            dependence.setKey(object.getId());
            dependence.setTitle(object.getName());
            dependence.setCreateTime(object.getCreateAt());
            dependence.setStatusCode(object.getStatus());
            if (object.getStatus() == Dependence.STATUS_INSTALLING) {
                dependence.setStatus("安装中");
            } else if (object.getStatus() == Dependence.STATUS_INSTALLED) {
                dependence.setStatus("已安装");
            } else if (object.getStatus() == Dependence.STATUS_INSTALL_FAILURE) {
                dependence.setStatus("安装失败");
            } else if (object.getStatus() == Dependence.STATUS_UNINSTALLING) {
                dependence.setStatus("卸载中");
            } else if (object.getStatus() == Dependence.STATUS_UNINSTALL_FAILURE) {
                dependence.setStatus("卸载失败");
            } else {
                dependence.setStatus("未知");
            }

            result.add(dependence);
        }

        return result;
    }

    public static List<File> convertScriptFiles(List<ScriptFilesRes.FileObject> objects) {
        List<File> files = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return files;
        }

        for (ScriptFilesRes.FileObject object : objects) {
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

    public static SystemConfig convertSystemConfig(SystemConfigRes.SystemConfigObject object) {
        SystemConfig config = new SystemConfig();
        LogUnit.log(object.getLogRemoveFrequency());
        LogUnit.log(object.getCronConcurrency());
        config.setLogRemoveFrequency(object.getLogRemoveFrequency());
        config.setCronConcurrency(object.getCronConcurrency());
        return config;
    }

    private static List<File> buildChildren(String parent, List<ScriptFilesRes.FileObject> objects) {
        List<File> children = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return children;
        }

        for (ScriptFilesRes.FileObject object : objects) {
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
