package edu.mit.media.mysnapshot.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class SelectableIconMultiSelectGroup extends SelectableIconGroup {

    public SelectableIcon selected = null;

    public static final boolean ALLOW_DESELECTION = true;

    SelectableIconListener listener;

    public interface SelectableIconListener {
        void onToggled(List<String> value);
    }


    public SelectableIconMultiSelectGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArrayList<String> getAllSelectedValues() {
        ArrayList<String> values = new ArrayList<>();
        for (SelectableIcon child : getChildren()) {
            if (child.isSelected()) {
                values.add(child.getValue());
            }
        }
        return values;
    }

    public void setValues(List<String> values) {
        for (SelectableIcon child : getChildren()) {
            if (values.contains(child.getValue())) {
                child.setSelected(true);
            }
        }
    }

    @Override
    public boolean childCanToggle(SelectableIcon child) {
        return true;
    }


    @Override
    public void onChildSelected(SelectableIcon selectedChild) {
        selected = selectedChild;
    }

    @Override
    public void onChildToggled(SelectableIcon selectedChild) {
        listener.onToggled(getAllSelectedValues());
    }


    @Override
    public void onChildAppearsSelected(SelectableIcon selectedChild) {
    }


    public void setListener(SelectableIconListener listener) {
        this.listener = listener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putStringArrayList("selected", getAllSelectedValues());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        List<String> selected = null;
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            selected = bundle.getStringArrayList("selected");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);

        if (selected != null) {
            for (String val : selected) {
                for (SelectableIcon child : getChildren()) {
                    if (val.equals(child.getValue())) {
                        child.setSelected(true);
                    }
                }
            }
        }
    }

}
