package auto.qinglong.bean.ql;

import java.util.Locale;

import auto.qinglong.utils.CronUnit;
import auto.base.util.TimeUnit;

public class QLTask implements Comparable<QLTask> {
    /* 接口属性 */
    private String _id;
    private String name;
    private String command;
    private String schedule;
    private String saved;
    private String timestamp;
    private int status;
    private int isSystem;
    private String pid;
    private int isDisabled;
    private int isPinned;
    private String log_path;
    private int last_running_time;
    private long last_execution_time;
    private String createdAt;
    private String updatedAt;
    /* 自定义属性 */
    private int mIndex;//序号
    private QLTaskState mState;//状态
    private String mFormatName;//格式化的名称
    private String mFormatLastExecutionTime;//格式化的上次运行时间
    private String mFormatLastRunningTime;//格式化的上次运行时长
    private String mFormatNextExecutionTime;//格式化的下次运行时间
    private String mLastLogPath;//最后的日志地址

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
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

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }

    public int isPinned() {
        return isPinned;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    /**
     * 获取任务最新日志地址
     *
     * @return 地址
     */
    public String getLastLogPath() {
        if (mLastLogPath == null) {
            mLastLogPath = "api/crons/" + _id + "/log";
        }
        return mLastLogPath;
    }

    public String getFormatName() {
        if (mFormatName == null) {
            mFormatName = String.format(Locale.CHINA, "[%d] %s", mIndex, name);
        }
        return mFormatName;
    }

    public String getFormatLastExecutionTime() {
        if (mFormatLastExecutionTime == null) {
            if (last_execution_time > 0) {
                mFormatLastExecutionTime = TimeUnit.formatDatetimeA(last_execution_time * 1000);
            } else {
                mFormatLastExecutionTime = "--";
            }
        }
        return mFormatLastExecutionTime;
    }

    public String getFormatLastRunningTime() {
        if (mFormatLastRunningTime == null) {
            if (last_running_time >= 60) {
                mFormatLastRunningTime = String.format(Locale.CHINA, "%d分%d秒", last_running_time / 60, last_running_time % 60);
            } else if (last_running_time > 0) {
                mFormatLastRunningTime = String.format(Locale.CHINA, "%d秒", last_running_time);
            } else {
                mFormatLastRunningTime = "--";
            }
        }
        return mFormatLastRunningTime;
    }

    public String getFormatNextExecutionTime() {
        if (mFormatNextExecutionTime == null) {
            mFormatNextExecutionTime = CronUnit.nextExecutionTime(schedule, "--");
        }
        return mFormatNextExecutionTime;
    }

    public QLTaskState getTaskState() {
        if (mState == null) {
            if (this.status == 0) {
                mState = QLTaskState.RUNNING;
            } else if (this.status == 3) {
                mState = QLTaskState.WAITING;
            } else if (this.isDisabled == 1) {
                mState = QLTaskState.LIMIT;
            } else {
                mState = QLTaskState.FREE;
            }
        }
        return mState;
    }

    /**
     * 排序，运行>队列>顶置>空闲>禁止
     *
     * @return -1,0,1
     */
    @Override
    public int compareTo(QLTask o) {
        if (this.getTaskState() == o.getTaskState()) {
            return o.isPinned() - this.isPinned;
        } else if (this.getTaskState() == QLTaskState.RUNNING && o.getTaskState() == QLTaskState.WAITING) {
            return -1;
        } else if (this.getTaskState() == QLTaskState.WAITING && o.getTaskState() == QLTaskState.RUNNING) {
            return 1;
        } else if ((this.getTaskState() == QLTaskState.RUNNING || this.getTaskState() == QLTaskState.WAITING) && (o.getTaskState() == QLTaskState.LIMIT || o.getTaskState() == QLTaskState.FREE)) {
            return -1;
        } else if ((this.getTaskState() == QLTaskState.LIMIT || this.getTaskState() == QLTaskState.FREE) && (o.getTaskState() == QLTaskState.RUNNING || o.getTaskState() == QLTaskState.WAITING)) {
            return 1;
        } else {
            return o.isPinned() - this.isPinned;
        }
    }

}
