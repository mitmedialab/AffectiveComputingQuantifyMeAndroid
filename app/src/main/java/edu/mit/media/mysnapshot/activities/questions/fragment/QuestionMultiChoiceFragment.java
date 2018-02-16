package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.SelectableIconMultiSelectGroup;

public abstract class QuestionMultiChoiceFragment extends QuestionFragment<List<String>> {

    public SelectableIconMultiSelectGroup group;

    public QuestionMultiChoiceFragment() {
        value = new ArrayList<>();
    }


    @Override
    protected void initViews(ViewGroup root) {

        group = (SelectableIconMultiSelectGroup) root.findViewById(R.id.choices);

        group.setListener(new SelectableIconMultiSelectGroup.SelectableIconListener() {
            @Override
            public void onToggled(List<String> val) {
                value = group.getAllSelectedValues();
                if (listener != null) {
                    listener.onSelected(val);
                }
            }
        });

        if (value != null) {
            group.setValues(value);
        }

        View button = root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDataSave(group.getValue());
            }
        });

        if (! isBuildingData()) {
            button.setVisibility(View.GONE);
        }

    }





}
