package auto.panel.ui.activity.panel.environment;

/**
 * @author wsfsp4
 * @version 2023.03.01
 */
public interface ItemMoveCallback {
    void onItemMove(int from, int to);

    void onItemMoveStart();

    void onItemMoveEnd(int start,int from, int to);
}
