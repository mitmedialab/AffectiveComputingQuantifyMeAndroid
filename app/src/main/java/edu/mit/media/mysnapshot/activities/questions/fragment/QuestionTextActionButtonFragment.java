package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.View;
import android.view.ViewGroup;

import edu.mit.media.mysnapshot.R;

public class QuestionTextActionButtonFragment extends QuestionTextFragment {

    public View actionButton;
    View.OnClickListener listener;

    String text;

    public QuestionTextActionButtonFragment() {
    }

    @Override
    protected void initViews(ViewGroup root) {

        super.initViews(root);

        actionButton = root.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(listener);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_text_action;
    }

    public void init(String text, View.OnClickListener onClickListener) {
        super.init(text);
        this.listener = onClickListener;
    }

}
