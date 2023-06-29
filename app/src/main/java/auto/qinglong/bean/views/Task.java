package auto.qinglong.bean.views;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class Task extends Base implements Comparable<Task> {
    public static final int STATE_FREE = 0;
    public static final int STATE_WAITING = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_LIMIT = 3;

    private String title;
    private String command;
    private String state;
    private int stateCode;
    private String schedule;
    private String lastExecuteTime;
    private String nextExecuteTime;
    private String lastRunningTime;
    private boolean isPinned;

    public Task(Object key) {
        this.key = key;
    }

    public Task(Object key, String title, String command, String state, int stateCode, String schedule, String lastExecuteTime, String nextExecuteTime, String lastRunningTime, boolean isPinned) {
        this.key = key;
        this.title = title;
        this.command = command;
        this.state = state;
        this.stateCode = stateCode;
        this.schedule = schedule;
        this.lastExecuteTime = lastExecuteTime;
        this.nextExecuteTime = nextExecuteTime;
        this.lastRunningTime = lastRunningTime;
        this.isPinned = isPinned;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(String lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public String getNextExecuteTime() {
        return nextExecuteTime;
    }

    public void setNextExecuteTime(String nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
    }

    public String getLastRunningTime() {
        return lastRunningTime;
    }

    public void setLastRunningTime(String lastRunningTime) {
        this.lastRunningTime = lastRunningTime;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    @Override
    public int compareTo(Task o) {
        if (this.stateCode == STATE_RUNNING && o.stateCode == STATE_WAITING) {
            return -1;
        } else if (this.stateCode == STATE_WAITING && o.stateCode == STATE_RUNNING) {
            return 1;
        } else if (this.isPinned && !o.isPinned) {
            return -1;
        } else if (!this.isPinned && o.isPinned) {
            return 1;
        }

        return o.stateCode - this.stateCode;
    }
}
