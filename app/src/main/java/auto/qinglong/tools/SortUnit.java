package auto.qinglong.tools;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auto.qinglong.api.object.Script;
import auto.qinglong.api.object.Task;
import auto.qinglong.api.object.TaskState;

public class SortUnit {

    public static List<Task> sortTasks(@NonNull List<Task> fromData) {
        List<Task> runningData = new ArrayList<>();
        List<Task> pinnedData = new ArrayList<>();
        List<Task> freeData = new ArrayList<>();
        List<Task> limitData = new ArrayList<>();

        for (Task task : fromData) {
            if (task.getTaskState() == TaskState.RUNNING) {
                runningData.add(task);
            } else if (task.getIsPinned() == 1) {
                pinnedData.add(task);
            } else if (task.getTaskState() == TaskState.FREE) {
                freeData.add(task);
            } else {
                limitData.add(task);
            }
        }
        fromData.clear();
        fromData.addAll(runningData);
        fromData.addAll(pinnedData);
        fromData.addAll(freeData);
        fromData.addAll(limitData);
        return fromData;
    }

    public static List<Script> sortScript(@NonNull List<Script> fromData) {
        List<Script> folderData = new ArrayList<>();
        List<Script> fileData = new ArrayList<>();
        Map<String, Script> map = new HashMap<>();
        List<String> order = new ArrayList<>();

        for (Script script : fromData) {
            if (script.getChildren() != null) {
                order.add(script.getTitle());
                map.put(script.getTitle(), script);
            }
        }
        Collections.sort(order);
        for (String title : order) {
            folderData.add(map.get(title));
        }
        map.clear();
        order.clear();


        for (Script script : fromData) {
            if (script.getChildren() == null) {
                order.add(script.getTitle());
                map.put(script.getTitle(), script);
            }
        }
        Collections.sort(order);
        for (String title : order) {
            fileData.add(map.get(title));
        }
        map.clear();
        order.clear();

        fromData.clear();
        fromData.addAll(folderData);
        fromData.addAll(fileData);
        return fromData;
    }
}
