package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.NoDefaultSpinner;

public class QuestionSpinnerFragment extends QuestionFragment<String> {

    public NoDefaultSpinner spinner;

    public QuestionSpinnerFragment() {
    }

    int valuesId = 0, labelsId = 0;
    String prompt = "";

    @Override
    protected void initViews(ViewGroup root) {

        spinner = (NoDefaultSpinner) root.findViewById(R.id.spinner);

        spinner.post(new Runnable() {
            public void run() {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        value = spinner.getSelectedItemValue();
                        listener.onSelected(value);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        spinner.setPrompt(prompt);

        spinner.setEntryValues(getResources().getStringArray(valuesId));

        String[] labels = getResources().getStringArray(labelsId);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, labels);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        if (value != null) {
            spinner.setSelectionToValue(value);
        }

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_spinner;
    }

    public void init(int valuesId, int labelsId, String prompt) {
        this.valuesId = valuesId;
        this.labelsId = labelsId;
        this.prompt = prompt;
    }

}
