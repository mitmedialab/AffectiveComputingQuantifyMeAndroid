package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.mit.media.mysnapshot.R;

public class QuestionTextFragment extends QuestionFragment<Void> {

    View button;

    String text;

    public QuestionTextFragment() {
    }

    @Override
    protected void initViews(ViewGroup root) {

        TextView textView = (TextView) root.findViewById(R.id.text);
        textView.setText(text);

        button = root.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            listener.onSelected(null);
            }
        });

    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public void setValue(Void v) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_text;
    }

    public void init(String text) {
        this.text = text;
    }

}
