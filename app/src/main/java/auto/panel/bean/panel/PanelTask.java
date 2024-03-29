package auto.panel.bean.panel;

/**
 * @author wsfsp4
 * @version 2023.06.29
 */
public class PanelTask implements Comparable<PanelTask> {
    public static final int STATE_RUNNING = 0;
    public static final int STATE_WAITING = 1;
    public static final int STATE_FREE = 2;
    public static final int STATE_LIMIT = 3;
    public static final int STATE_UNKOWN = 4;

    private Object key;
    private String name;
    private String command;
    private String state;
    private int stateCode;
    private String schedule;
    private String lastExecuteTime;
    private String lastRunningTime;
    private String nextExecuteTime;
    private boolean isPinned;

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private int getLevel() {
        if (this.isPinned) {
            return -1;
        } else {
            return this.stateCode;
        }
    }

    @Override
    public int compareTo(PanelTask o) {
        return this.getLevel() - o.getLevel();
    }
}
