package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.mysnapshot.R;

public class SelectableIconGroup extends GridLayout {

    public SelectableIcon selected = null;

    public static final boolean ALLOW_DESELECTION = false;

    SelectableIconListener listener;


    public interface SelectableIconListener {
        void onSelected(String value);
    }


    public SelectableIconGroup(Context context, AttributeSet attrs) {
        super(context, attrs, R.style.QuestionIconGroup);
    }

    public List<SelectableIcon> getChildren() {
        List<SelectableIcon> children = new ArrayList<>();
        int count = getChildCount();
        for(int i = 0 ; i < count ; i++){
            View child = getChildAt(i);
            if (child instanceof SelectableIcon) {
                children.add((SelectableIcon) child);
            }
        }
        return children;
    }


    public String getValue() {
        if (selected == null) {
            return null;
        }
        return selected.getValue();
    }

    public void setValue(String value) {
        if (value == null) {
            return;
        }
        for (SelectableIcon child : getChildren()) {
            if (value.equals(child.getValue())) {
                child.setSelected(true);
            }
        }
    }

    public void onChildSelected(SelectableIcon selectedChild) {
        selected = selectedChild;
        for (SelectableIcon child : getChildren()) {
            if (child != selectedChild) {
                child.setSelected(false);
            }
        }
        if (listener != null) {
            listener.onSelected(selected.getValue());
        }
    }

    public void onChildToggled(SelectableIcon selectedChild) {
    }


    public void onChildAppearsSelected(SelectableIcon selectedChild) {
        for (SelectableIcon child : getChildren()) {
            if (child != selectedChild) {
                child.appearsSelected = false;
            }
        }
    }

    public boolean childCanToggle(SelectableIcon child) {
        return false;
    }

    public void setListener(SelectableIconListener listener) {
        this.listener = listener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        if (selected != null) {
            bundle.putString("selected", selected.getValue());
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        String selected = null;
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            selected = bundle.getString("selected");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);

        setValue(selected);
    }

    public void addChoice(SelectableIcon.IconChoice choice) {
        SelectableIcon icon = new SelectableIcon(getContext(), null);
        this.addView(icon);
        icon.setChoice(choice);
    }


}
