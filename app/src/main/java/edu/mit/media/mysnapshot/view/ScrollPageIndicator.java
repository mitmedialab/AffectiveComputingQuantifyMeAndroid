package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flyco.pageindicator.anim.base.IndicatorBaseAnimator;
import com.flyco.pageindicator.indicator.base.PageIndicator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.mysnapshot.R;

public class ScrollPageIndicator extends LinearLayout implements PageIndicator {
    private Context context;
    private ViewPager vp;
    private RelativeLayout rl_parent;
    private ImageView selectView;
    private ArrayList<ImageView> indicatorViews = new ArrayList<>();
    private int totalPages;

    private int currentItem;
    private int lastItem;
    private int indicatorWidth;
    private int indicatorHeight;
    private int indicatorGap;
    private int cornerRadius;
    private Drawable selectDrawable;
    private Drawable unSelectDrawable;
    private Drawable extraDrawable;
    private int strokeWidth;
    private int strokeColor;
    private boolean isSnap;

    public int activePages;

    private Class<? extends IndicatorBaseAnimator> selectAnimClass;
    private Class<? extends IndicatorBaseAnimator> unselectAnimClass;

    List<Drawable> iconDrawables = new ArrayList<>();
    int selectColor, unselectColor, extraColor;
    int padding, selectPadding, unselectPadding, extraPadding;
    boolean selectedInForeground = true;

    public ScrollPageIndicator(Context context) {
        this(context, null);
    }

    public ScrollPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setClipChildren(false);
        setClipToPadding(false);

        rl_parent = new RelativeLayout(context);
        rl_parent.setClipChildren(false);
        rl_parent.setClipToPadding(false);
        addView(rl_parent);

        setGravity(Gravity.CENTER);
        TypedArray ta = context.obtainStyledAttributes(attrs, com.flyco.pageindicator.R.styleable.FlycoPageIndicaor);
        indicatorWidth = ta.getDimensionPixelSize(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_width, dp2px(6));
        indicatorHeight = ta.getDimensionPixelSize(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_height, dp2px(6));
        indicatorGap = ta.getDimensionPixelSize(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_gap, dp2px(8));
        cornerRadius = ta.getDimensionPixelSize(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_cornerRadius, dp2px(3));
        strokeWidth = ta.getDimensionPixelSize(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_strokeWidth, dp2px(0));
        strokeColor = ta.getColor(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_strokeColor, Color.parseColor("#ffffff"));
        isSnap = ta.getBoolean(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_isSnap, false);

        selectColor = ta.getColor(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_selectColor, Color.parseColor("#ffffff"));
        unselectColor = ta.getColor(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_unselectColor, Color.parseColor("#88ffffff"));
        int selectRes = ta.getResourceId(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_selectRes, 0);
        int unselectRes = ta.getResourceId(com.flyco.pageindicator.R.styleable.FlycoPageIndicaor_fpi_unselectRes, 0);
        ta.recycle();

        ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollPageIndicator);

        padding = ta.getDimensionPixelSize(R.styleable.ScrollPageIndicator_tpi_padding, 0);
        extraPadding = ta.getDimensionPixelSize(R.styleable.ScrollPageIndicator_tpi_padding, padding);
        selectPadding = ta.getDimensionPixelSize(R.styleable.ScrollPageIndicator_tpi_selectedPadding, padding);
        unselectPadding = ta.getDimensionPixelSize(R.styleable.ScrollPageIndicator_tpi_unselectedPadding, padding);

        selectedInForeground = ta.getBoolean(R.styleable.ScrollPageIndicator_tpi_selectedInForeground, true);

        extraColor = ta.getColor(R.styleable.ScrollPageIndicator_tpi_extraColor, Color.parseColor("#88ffffff"));
        int extraRes = ta.getResourceId(R.styleable.ScrollPageIndicator_tpi_extraRes, unselectRes);

        ta.recycle();

        if (selectRes != 0) {
            this.selectDrawable = getResources().getDrawable(selectRes);
        } else {
            this.selectDrawable = null;
        }

        if (unselectRes != 0) {
            this.unSelectDrawable = getResources().getDrawable(unselectRes);
        } else {
            this.unSelectDrawable = getDrawable(cornerRadius);
        }

        if (extraRes != 0) {
            this.extraDrawable = getResources().getDrawable(extraRes);
        } else {
            this.extraDrawable = getDrawable(cornerRadius);
        }
    }

    /** call before setViewPager. set indicator width, unit dp, default 6dp */
    public ScrollPageIndicator setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = dp2px(indicatorWidth);
        return this;
    }

    /** call before setViewPager. set indicator height, unit dp, default 6dp */
    public ScrollPageIndicator setIndicatorHeight(float indicatorHeight) {
        this.indicatorHeight = dp2px(indicatorHeight);
        return this;
    }

    /** call before setViewPager. set gap between two indicators, unit dp, default 6dp */
    public ScrollPageIndicator setIndicatorGap(float indicatorGap) {
        this.indicatorGap = dp2px(indicatorGap);
        return this;
    }

    /** call before setViewPager. set indicator select color, default "#ffffff" "#88ffffff" */
    public ScrollPageIndicator setIndicatorSelectColor(int selectColor, int unselectColor) {
        this.selectColor = selectColor;
        this.unselectColor = unselectColor;
        return this;
    }

    /** call before setViewPager. set indicator corner raduis, unit dp, default 3dp */
    public ScrollPageIndicator setCornerRadius(float cornerRadius) {
        this.cornerRadius = dp2px(cornerRadius);
        return this;
    }

    /** call before setViewPager. set width of the stroke used to draw the indicators, unit dp, default 0dp */
    public ScrollPageIndicator setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    /** call before setViewPager. set color of the stroke used to draw the indicators. default "#ffffff" */
    public ScrollPageIndicator setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        return this;
    }

    /** call before setViewPager. Whether or not the selected indicator snaps to the indicators. default false */
    public ScrollPageIndicator setIsSnap(boolean isSnap) {
        this.isSnap = isSnap;
        return this;
    }

    /** call before setViewPager. set indicator select anim. only valid when isSnap is true */
    public ScrollPageIndicator setSelectAnimClass(Class<? extends IndicatorBaseAnimator> selectAnimClass) {
        this.selectAnimClass = selectAnimClass;
        return this;
    }

    /** call before setViewPager. set indicator unselect anim. only valid when isSnap is true */
    public ScrollPageIndicator setUnselectAnimClass(Class<? extends IndicatorBaseAnimator> unselectAnimClass) {
        this.unselectAnimClass = unselectAnimClass;
        return this;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public int getIndicatorWidth() {
        return indicatorWidth;
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public int getIndicatorGap() {
        return indicatorGap;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public boolean isSnap() {
        return isSnap;
    }

    @Override
    public void setCurrentItem(int item) {
        if (isValid()) {
            vp.setCurrentItem(item);
        }
    }

    @Override
    public void setViewPager(ViewPager vp) {
        setViewPager(vp, vp.getAdapter().getCount());
    }

    DataSetObserver observer;

    public void setViewPager(final ViewPager vp, int realCount) {
        this.vp = vp;


        if (isValid()) {
            if (observer != null) {
                vp.getAdapter().unregisterDataSetObserver(observer);
            }
            totalPages = realCount;
            vp.removeOnPageChangeListener(this);
            vp.addOnPageChangeListener(this);

            observer = new DataSetObserver() {
                @Override
                public void onChanged() {
                    activePages = vp.getAdapter().getCount();
                    createIndicators();
                }
            };
            vp.getAdapter().registerDataSetObserver(observer);
            observer.onChanged();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!isSnap && selectView != null) {
            /**
             * position:当前View的位置
             * positionOffset:当前View的偏移量比例.[0,1)
             */
            int selectedItem = position;
            if (positionOffset > .5f && position < indicatorViews.size() - 1) {
                selectedItem += 1;
            }

            this.currentItem = position;

            for (int i = 0; i < indicatorViews.size(); i++) {
                boolean isExtra = i >= activePages;
                boolean isSelected = selectedItem == i;


                ImageView iv = indicatorViews.get(i);
                if (isExtra) {
                    iv.setColorFilter(extraColor);
                } else {
                    iv.setColorFilter(isSelected ? selectColor : unselectColor);
                }
            }

            float tranlationX = (indicatorWidth + indicatorGap) * (currentItem + positionOffset);
            ViewHelper.setTranslationX(selectView, tranlationX);

//            RelativeLayout.LayoutParams lp = new  RelativeLayout.LayoutParams(indicatorWidth,
//                    indicatorHeight);
//            lp.leftMargin = (int) tranlationX;
//            selectView.setLayoutParams(lp);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    private void animSwitch(int position) {
        try {
//            Log.d(TAG, "position--->" + position);
//            Log.d(TAG, "lastPositon--->" + lastPositon);
            if (selectAnimClass != null) {
                if (position == lastItem) {
                    selectAnimClass.newInstance().playOn(indicatorViews.get(position));
                } else {
                    selectAnimClass.newInstance().playOn(indicatorViews.get(position));
                    if (unselectAnimClass == null) {
                        selectAnimClass.newInstance().interpolator(new ReverseInterpolator()).playOn(indicatorViews.get(lastItem));
                    } else {
                        unselectAnimClass.newInstance().playOn(indicatorViews.get(lastItem));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private abstract class IndexedOnClickListener implements OnClickListener {
        public int index;

        public IndexedOnClickListener(int index) {
            this.index = index;
        }
    }

    public void onImageClicked(int index) {
        if (index < activePages) {
            vp.setCurrentItem(index, true);
        }
    }

    public void playSoundEffect(int soundConstant) {
    }

    private void createIndicators() {
        if (totalPages <= 0) {
            return;
        }

        indicatorViews.clear();
        rl_parent.removeAllViews();

        if (! selectedInForeground) {
            createSelectIndicator();
        }

        LinearLayout ll_unselect_views = new LinearLayout(context);
        rl_parent.addView(ll_unselect_views);

        for (int i = 0; i < totalPages; i++) {
            ImageView iv = new ImageView(context);
            iv.setOnClickListener(new IndexedOnClickListener(i) {
                @Override
                public void onClick(View view) {
                    onImageClicked(index);
                }
            });
            boolean isExtra = i >= activePages;
            boolean isSelected = currentItem == i;
            Drawable overrideDrawable = null;
            if (i < iconDrawables.size() && iconDrawables.get(i) != null) {
                overrideDrawable = iconDrawables.get(i);
            }
            if (isExtra) {
                iv.setImageDrawable(overrideDrawable == null ? extraDrawable : overrideDrawable);
                iv.setColorFilter(extraColor);
                iv.setPadding(extraPadding, extraPadding, extraPadding, extraPadding);
            } else {
                iv.setColorFilter(isSelected ? selectColor : unselectColor);
                // iv.setColorFilter(unselectColor);
                if (isSelected && isSnap) {
                    iv.setImageDrawable(selectDrawable);
                    iv.setPadding(selectPadding, selectPadding, selectPadding, selectPadding);
                } else {
                    iv.setImageDrawable(overrideDrawable == null ? unSelectDrawable.getConstantState().newDrawable().mutate() : overrideDrawable);
                    iv.setPadding(unselectPadding, unselectPadding, unselectPadding, unselectPadding);
                }
            }
            LayoutParams lp = new LayoutParams(indicatorWidth,
                    indicatorHeight);
            lp.leftMargin = i == 0 ? 0 : indicatorGap;
            ll_unselect_views.addView(iv, lp);
            indicatorViews.add(iv);
        }

        if (selectedInForeground) {
            createSelectIndicator();
        }

        animSwitch(currentItem);
    }

    private void createSelectIndicator() {
        if (!isSnap) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(indicatorWidth,
                    indicatorHeight);
            // lp.leftMargin = (indicatorWidth + indicatorGap) * currentItem;
            selectView = new ImageView(context);
            selectView.setImageDrawable(selectDrawable);
            selectView.setColorFilter(selectColor, PorterDuff.Mode.MULTIPLY);
            selectView.setPadding(selectPadding, selectPadding, selectPadding, selectPadding);
            selectView.setScaleType(ImageView.ScaleType.FIT_XY);
            rl_parent.addView(selectView, lp);

            float tranlationX = (indicatorWidth + indicatorGap) * currentItem;
            ViewHelper.setTranslationX(selectView, tranlationX);
        }
    }

    private boolean isValid() {
        if (vp == null) {
            throw new IllegalStateException("ViewPager can not be NULL!");
        }

        if (vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager adapter can not be NULL!");
        }

        return true;
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    private GradientDrawable getDrawable(float raduis) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(raduis);
        drawable.setStroke(strokeWidth, Color.WHITE);
        drawable.setColor(Color.WHITE);

        return drawable;
    }


    private int dp2px(float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("currentItem", currentItem);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            currentItem = bundle.getInt("currentItem");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    public List<Drawable> getIconDrawables() {
        return iconDrawables;
    }

    public void setIconDrawables(List<Drawable> iconDrawables) {
        this.iconDrawables = iconDrawables;
        createIndicators();
    }
}
