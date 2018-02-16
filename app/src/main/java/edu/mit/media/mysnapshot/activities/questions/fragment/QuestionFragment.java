package edu.mit.media.mysnapshot.activities.questions.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.activities.questions.QuestionActivity;
import edu.mit.media.mysnapshot.activities.questions.QuestionListener;

public abstract class QuestionFragment<T> extends android.support.v4.app.Fragment {


    public QuestionListener listener;
    public ViewGroup root;
    T value;

    Layout layout = new Layout(0, "");

    public static class Layout {
        int iconId;
        String question;

        public Layout(int iconId, String question) {
            this.iconId = iconId;
            this.question = question;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(getLayoutId(), container, false);

        TextView title = (TextView) root.findViewById(R.id.title);
        if (title != null) {
            title.setText(layout.question);
        }
        ImageView icon = (ImageView) root.findViewById(R.id.icon);
        if (icon != null) {
            icon.setImageResource(layout.iconId);
        }

        initViews(root);

        return root;

    }

    protected abstract void initViews(ViewGroup root);

    public void setListener(QuestionListener listener) {
        this.listener = listener;

    }

    public abstract int getLayoutId();

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    protected boolean isBuildingData() {
        return ((QuestionActivity)getActivity()).isBuildingData;
    }

    public QuestionFragment<T> setLayout(Layout layout) {
        this.layout = layout;
        return this;
    }

}
