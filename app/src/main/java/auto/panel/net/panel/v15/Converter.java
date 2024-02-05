package auto.panel.net.panel.v15;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import auto.panel.bean.panel.PanelDependence;
import auto.panel.bean.panel.PanelEnvironment;
import auto.panel.bean.panel.PanelFile;
import auto.panel.bean.panel.PanelSystemConfig;
import auto.panel.bean.panel.PanelTask;
import auto.panel.utils.CronUnit;
import auto.panel.utils.TimeUnit;

/**
 * @author wsfsp4
 * @version 2023.07.06
 */
public class Converter {
    public static List<PanelTask> convertTasks(List<TasksRes.TaskObject> objects) {
        List<PanelTask> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }

        try {
            for (TasksRes.TaskObject object : objects) {
                PanelTask task = new PanelTask();
                task.setKey(object.getId());
                task.setName(object.getName());
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
                    task.setStateCode(PanelTask.STATE_RUNNING);
                } else if (object.getIsDisabled() == 1) {
                    task.setState("已禁止");
                    task.setStateCode(PanelTask.STATE_LIMIT);
                } else if (object.getStatus() == 0.5f || object.getStatus() == 3) {
                    task.setState("队列中");
                    task.setStateCode(PanelTask.STATE_WAITING);
                } else {
                    task.setState("空闲中");
                    task.setStateCode(PanelTask.STATE_FREE);
                }
                result.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<PanelEnvironment> convertEnvironments(List<EnvironmentsRes.EnvironmentObject> objects) {
        List<PanelEnvironment> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }

        try {
            for (EnvironmentsRes.EnvironmentObject object : objects) {
                PanelEnvironment environment = new PanelEnvironment();
                environment.setKey(object.getId());
                environment.setName(object.getName());
                environment.setValue(object.getValue());
                environment.setRemark(object.getRemarks());
                environment.setPosition(object.getPosition());
                environment.setStatusCode(object.getStatus());
                long timestamp = TimeUnit.utcToTimestamp(object.getUpdatedAt()) + 8 * 60 * 60 * 1000;
                environment.setTime(TimeUnit.formatDatetimeA(timestamp));
                result.add(environment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<PanelFile> convertLogFiles(List<LogFilesRes.FileObject> objects) {
        List<PanelFile> files = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return files;
        }

        try {
            for (LogFilesRes.FileObject object : objects) {
                PanelFile logFile = new PanelFile();
                logFile.setTitle(object.getTitle());
                logFile.setDir(object.isDir());
                logFile.setParentPath("");
                logFile.setPath(object.getTitle());
                logFile.setTime(TimeUnit.formatDatetimeA(object.getMtime()));

                if (object.isDir()) {
                    List<PanelFile> children = new ArrayList<>();
                    for (LogFilesRes.FileObject childObject : object.getChildren()) {
                        PanelFile childFile = new PanelFile();
                        childFile.setDir(false);
                        childFile.setTitle(childObject.getTitle());
                        childFile.setParentPath(object.getTitle());
                        childFile.setPath(object.getTitle() + "/" + childObject.getTitle());
                        childFile.setTime(TimeUnit.formatDatetimeA(childObject.getMtime()));
                        children.add(childFile);
                    }
                    logFile.setChildren(children);
                }
                files.add(logFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    public static List<PanelDependence> convertDependencies(List<DependenciesRes.DependenceObject> objects) {
        List<PanelDependence> result = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return result;
        }

        try {
            for (DependenciesRes.DependenceObject object : objects) {
                PanelDependence dependence = new PanelDependence();
                dependence.setKey(object.getId());
                dependence.setTitle(object.getName());
                long timestamp = TimeUnit.utcToTimestamp(object.getCreatedAt()) + 8 * 60 * 60 * 1000;
                dependence.setCreateTime(TimeUnit.formatDatetimeA(timestamp));
                dependence.setStatusCode(object.getStatus());
                result.add(dependence);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<PanelFile> convertScriptFiles(List<ScriptFilesRes.FileObject> objects) {
        List<PanelFile> files = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return files;
        }

        try {
            for (ScriptFilesRes.FileObject object : objects) {
                PanelFile file = new PanelFile();
                file.setTitle(object.getTitle());
                file.setParentPath("");
                file.setDir(object.isDir());
                file.setTime(TimeUnit.formatDatetimeA((long) object.getMtime()));
                file.setPath(object.getTitle());

                if (object.isDir()) {
                    file.setChildren(buildChildren(file.getPath(), object.getChildren()));
                }

                files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    public static PanelSystemConfig convertSystemConfig(SystemConfigRes.SystemConfigObject object) {
        PanelSystemConfig config = new PanelSystemConfig();

        try {
            config.setLogRemoveFrequency(object.getLogRemoveFrequency());
            config.setCronConcurrency(object.getCronConcurrency());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    private static List<PanelFile> buildChildren(String parent, List<ScriptFilesRes.FileObject> objects) {
        List<PanelFile> children = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return children;
        }

        try {
            for (ScriptFilesRes.FileObject object : objects) {
                PanelFile childFile = new PanelFile();
                childFile.setTitle(object.getTitle());
                childFile.setParentPath(parent);
                childFile.setDir(object.isDir());
                childFile.setTime(TimeUnit.formatDatetimeA((long) object.getMtime()));
                childFile.setPath(parent + "/" + object.getTitle());

                if (object.isDir()) {
                    childFile.setChildren(buildChildren(childFile.getPath(), object.getChildren()));
                }

                children.add(childFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return children;
    }
}
