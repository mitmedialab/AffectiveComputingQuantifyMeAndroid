package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import edu.mit.media.mysnapshot.R;

public abstract class QuestionEditTextFragment extends QuestionFragment<String> {

    View button;
    EditText editText;

    public QuestionEditTextFragment() {
    }


    @Override
    protected void initViews(ViewGroup root) {

        button = root.findViewById(R.id.button);
        editText = (EditText) root.findViewById(R.id.edittext);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            listener.onSelected(editText.getText().toString());
            }
        });

    }

    @Override
    public String getValue() {
        return editText.getText().toString();
    }

    @Override
    public void setValue(String string) {
        editText.setText(string);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
