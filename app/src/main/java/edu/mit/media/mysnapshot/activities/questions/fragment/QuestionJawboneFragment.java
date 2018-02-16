package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.View;
import android.view.ViewGroup;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.activities.questions.JawboneQuestionActivity;

public class QuestionJawboneFragment extends QuestionFragment<Boolean> {

    View jawboneButton;
    public String accessToken, resetToken;

    public QuestionJawboneFragment() {
    }

    @Override
    protected void initViews(ViewGroup root) {

        jawboneButton = (View) root.findViewById(R.id.jawboneButton);
        jawboneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((JawboneQuestionActivity) getActivity()).authJawbone();
            }
        });

        View button = root.findViewById(R.id.doneButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onDataSave(value);
            }
        });

        if (isBuildingData()) {
            button.setVisibility(View.GONE);
        }

    }

    @Override
    public void setValue(Boolean value) {

        super.setValue(value);

    }

    public void onJawboneAuth(String accessToken, String resetToken) {
        setValue(true);
        listener.onDataSave(value);
        this.accessToken = accessToken;
        this.resetToken = resetToken;

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_jawbone;
    }



}
