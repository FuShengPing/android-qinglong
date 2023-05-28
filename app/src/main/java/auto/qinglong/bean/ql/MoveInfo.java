package auto.qinglong.bean.ql;

/**
 * @author wsfsp4
 * @version 2023.03.02
 */
public class MoveInfo {
    private QLEnvironment fromObejct;
    private QLEnvironment toObject;
    private int fromIndex;
    private int toIndex;

    public MoveInfo(QLEnvironment fromObejct, int fromIndex, QLEnvironment toObject, int toIndex) {
        this.fromObejct = fromObejct;
        this.fromIndex = fromIndex;
        this.toObject = toObject;
        this.toIndex = toIndex;
    }

    public QLEnvironment getFromObejct() {
        return fromObejct;
    }

    public QLEnvironment getToObject() {
        return toObject;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }
}
