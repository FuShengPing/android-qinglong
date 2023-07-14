package auto.panel.bean.panel;

/**
 * @author wsfsp4
 * @version 2023.03.02
 */
public class MoveInfo {
    private final QLEnvironment fromObject;
    private final QLEnvironment toObject;
    private final int fromIndex;
    private final int toIndex;

    public MoveInfo(QLEnvironment fromObejct, int fromIndex, QLEnvironment toObject, int toIndex) {
        this.fromObject = fromObejct;
        this.fromIndex = fromIndex;
        this.toObject = toObject;
        this.toIndex = toIndex;
    }

    public QLEnvironment getFromObject() {
        return fromObject;
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