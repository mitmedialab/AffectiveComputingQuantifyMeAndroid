package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.support.v7.widget.AppCompatCheckBox;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.TriggeringScrollView;

public class QuestionCheckboxFragment extends QuestionFragment<Boolean> {

    public AppCompatCheckBox checkbox;

    String text;

    public QuestionCheckboxFragment() {
    }

    @Override
    protected void initViews(ViewGroup root) {

        checkbox = (AppCompatCheckBox) root.findViewById(R.id.checkBox);

        if (value != null) {
            checkbox.setChecked(value);
        }

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                value = isChecked;
                listener.onSelected(isChecked);
            }
        });

        checkbox.setEnabled(false);
        checkbox.setSupportButtonTintList(getResources().getColorStateList(R.color.checkboxcolor));

        TriggeringScrollView scroll = (TriggeringScrollView) root.findViewById(R.id.scrollView);
        scroll.setListener(new TriggeringScrollView.ScrollViewTriggerListener() {
            @Override
            public void onHitBottom() {
                if (! checkbox.isChecked()) {
                    checkbox.setEnabled(true);
                }
            }
        });

        ((TextView) root.findViewById(R.id.termstext)).setText(text);
    }

    @Override
    public void setValue(Boolean value) {
        super.setValue(value);
        if (checkbox != null) {
            checkbox.setChecked(value);
        }
    }



    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_checkbox;
    }

    public void setText(String text) {
        this.text = text;
    }


}
