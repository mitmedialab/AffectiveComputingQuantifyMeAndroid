package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

import edu.mit.media.mysnapshot.R;

public class ViewPagerCustomDuration extends ViewPager {

    FixedSpeedScroller scroller;
    int scrollDuration = 700;

    public ViewPagerCustomDuration(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerCustomDuration, 0, 0);
        try {
            scrollDuration = ta.getInt(R.styleable.ViewPagerCustomDuration_duration, scrollDuration);
        } finally {
            ta.recycle();
        }

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new FixedSpeedScroller(getContext());
            // scroller.setFixedDuration(5000);
            mScroller.set(this, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        setDuration(scrollDuration);

    }

    public void setDuration(int duration) {
        scrollDuration = duration;
        scroller.setDuration(duration);
    }

    public class FixedSpeedScroller extends Scroller {

        protected int mDuration = 5000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setDuration(int duration) {
            mDuration = duration;
        }
    }


}
