package edu.mit.media.mysnapshot.view;


import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ValueRadioButton extends AppCompatRadioButton {

    public String value;

    public ValueRadioButton(Context context) {
        super(context);
    }

    public ValueRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ValueRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }
}