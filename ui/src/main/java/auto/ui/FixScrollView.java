package auto.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 支持设置最大高度的滚动控件 主要用于PopupWindow中.
 *
 * @author wsfsp4
 * @version 2023.06.01
 */
public class FixScrollView extends ScrollView {
    public static final String TAG = "MyScrollView";
    private int maxHeight = -1;

    public FixScrollView(Context context) {
        super(context);
    }

    public FixScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FixScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (maxHeight > 0 && height > maxHeight) {
            setMeasuredDimension(width, maxHeight);
        }
    }

    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }
}
