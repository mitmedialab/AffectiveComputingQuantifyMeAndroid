package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.SelectableIcon;
import edu.mit.media.mysnapshot.view.SelectableIconGroup;

public class QuestionChoiceFragment extends QuestionFragment<String> {

    public SelectableIconGroup group;

    List<SelectableIcon.IconChoice> choices = new ArrayList<>();

    public QuestionChoiceFragment() {
    }

    @Override
    protected void initViews(ViewGroup root) {

        group = (SelectableIconGroup) root.findViewById(R.id.choices);

        for (SelectableIcon.IconChoice choice : choices) {
            group.addChoice(choice);
        }

        group.setListener(new SelectableIconGroup.SelectableIconListener() {
            @Override
            public void onSelected(String val) {
                value = group.getValue();
                listener.onSelected(val);
            }
        });

        if (value != null) {
            group.setValue(value);
        }

    }

    public int getLayoutId() {
        return R.layout.fragment_question_choice;
    }

    public QuestionChoiceFragment addChoice(SelectableIcon.IconChoice choice) {
        choices.add(choice);
        return this;
    }

}
