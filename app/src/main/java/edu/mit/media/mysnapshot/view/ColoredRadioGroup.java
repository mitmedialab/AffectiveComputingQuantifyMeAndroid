package edu.mit.media.mysnapshot.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.mysnapshot.R;

public class ColoredRadioGroup extends RadioGroup {

    int numChildren = 0;
    int leftColor, rightColor;

    Context context;


    public ColoredRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColoredRadioGroup);
            if (ta != null) {

                leftColor = ta.getColor(R.styleable.ColoredRadioGroup_leftTint, Color.parseColor("#666666"));
                rightColor = ta.getColor(R.styleable.ColoredRadioGroup_rightTint, Color.parseColor("#666666"));

                numChildren = ta.getInt(R.styleable.ColoredRadioGroup_buttons, 7);
                ta.recycle();
            }
        }


        createChildren();
    }

    List<ValueRadioButton> children = new ArrayList<>();

    public void setStyle(int leftColor, int rightColor, int numChildren) {
        this.numChildren = numChildren;
        this.leftColor = leftColor;
        this.rightColor = rightColor;
        createChildren();
    }

    void createChildren() {
        this.removeAllViews();
        children.clear();

        for (int i = 0; i < numChildren; i++) {
            int color = getColor(i, numChildren);

            ValueRadioButton child = new ValueRadioButton(context);
            child.value = String.valueOf(i);
            child.setId(i);
            addView(child);
            children.add(child);
            CompoundButtonCompat.setButtonTintList(child, getStateListFromColor(color));

            if (i != numChildren - 1) {
                RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
                View spacer = new View(context);
                spacer.setLayoutParams(lp);
                addView(spacer);
            }


        }
    }

    public int getSelectedIndex() {
        ValueRadioButton radioButton = (ValueRadioButton) findViewById(getCheckedRadioButtonId());
        return children.indexOf(radioButton);
    }

    public void checkButton(int index) {
        children.get(index).setChecked(true);
    }

    static class RGBColor {
        public int red, green, blue;

        public RGBColor(int color) {
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);
        }
    }

    int getColor(int i, int total) {
        return getColor(i, total, leftColor, rightColor);
    }

    public static int getColor(int i, int total, int leftColor, int rightColor) {
        RGBColor left = new RGBColor(leftColor);
        RGBColor right = new RGBColor(rightColor);
        return Color.argb(255,lerp(i, total, left.red, right.red), lerp(i, total, left.green, right.green), lerp(i, total, left.blue, right.blue));
    }

    static int lerp(int percent, int total, int start, int finish) {
        return Math.round((float) start + (finish - start) * (float) percent / (float) total);
    }

    ColorStateList getStateListFromColor(int color) {
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        color,
                        color,
                }
        );
    }

}
