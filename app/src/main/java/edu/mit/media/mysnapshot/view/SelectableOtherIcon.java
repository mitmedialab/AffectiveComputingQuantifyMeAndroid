package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

public class SelectableOtherIcon extends SelectableIcon {

    String defaultValue;

    public SelectableOtherIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultValue = choice.labelText;
        setSelected(false);
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            showDialog();
        } else {
            setValue(defaultValue);
            super.setSelected(selected);
        }
    }

    private int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final EditText edittext = new EditText(getContext());
        edittext.setLines(1);
        edittext.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        edittext.setHint("Enter your option here");
        alert.setTitle("Custom Option");

        FrameLayout editParent = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dp2px(50));
        int xMargin = dp2px(30), yMargin = dp2px(30);
        params.setMargins(xMargin, yMargin, xMargin, yMargin);
        edittext.setLayoutParams(params);
        editParent.addView(edittext);

        alert.setView(editParent);
        alert.setCancelable(false);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = edittext.getText().toString();
                if (value.length() > 0) {
                    setValue(value);
                    SelectableOtherIcon.super.setSelected(true);
                } else {
                    setValue(defaultValue);
                }
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                setValue(defaultValue);
            }
        });

        alert.show();
    }

    void setValue(String val) {
        choice.value = val;
        label.setText(choice.value);
    }


}
