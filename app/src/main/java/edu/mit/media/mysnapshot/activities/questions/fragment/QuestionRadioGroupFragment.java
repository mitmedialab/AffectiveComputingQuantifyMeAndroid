package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.ColoredRadioGroup;

public class QuestionRadioGroupFragment extends QuestionFragment<Integer> {

    public ColoredRadioGroup group;

    public QuestionRadioGroupFragment() {
    }

    String minLabel = "", maxLabel = "";
    Integer minColor = null, maxColor = null, numRadios = 7;

    @Override
    protected void initViews(ViewGroup root) {

        group = (ColoredRadioGroup) root.findViewById(R.id.radiogroup);

        if (minColor == null) {
            minColor = getResources().getColor(R.color.radio_red);
        }
        if (maxColor == null) {
            maxColor = getResources().getColor(R.color.radio_green);
        }
        group.setStyle(minColor, maxColor, numRadios);

        ((TextView) root.findViewById(R.id.left)).setText(minLabel);
        ((TextView) root.findViewById(R.id.right)).setText(maxLabel);

        if (value != null) {
            group.checkButton(value);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                value = group.getSelectedIndex();
                listener.onSelected(value);
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_radiogroup;
    }

    public QuestionRadioGroupFragment init(String minLabel, String maxLabel) {
        this.minLabel = minLabel;
        this.maxLabel = maxLabel;
        return this;
    }

    public QuestionRadioGroupFragment init(String minLabel, String maxLabel, int minColor, int maxColor, int numRadios) {
        init(minLabel, maxLabel);
        this.minColor = minColor;
        this.maxColor = maxColor;
        this.numRadios = numRadios;
        return this;
    }


}
