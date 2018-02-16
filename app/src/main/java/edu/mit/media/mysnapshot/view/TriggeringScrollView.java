package edu.mit.media.mysnapshot.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class TriggeringScrollView extends ScrollView {

    public interface ScrollViewTriggerListener {
        void onHitBottom();
    }

    ScrollViewTriggerListener listener;

    public TriggeringScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewTriggerListener getListener() {
        return listener;
    }

    public void setListener(ScrollViewTriggerListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        checkScroll();

        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        checkScroll();

    }

    void checkScroll() {
        View view = getChildAt(getChildCount()-1);

        if (view.getHeight() == 0) {
            return; // probably not initialized yet
        }

        int diff = (view.getBottom()-(getHeight()+getScrollY()));

        if( diff <= 0 )
        {
            if (listener != null) {
                listener.onHitBottom();
            }
        }
    }
}
