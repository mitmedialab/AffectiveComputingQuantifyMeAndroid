package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.view.ViewGroup;

import org.joda.time.DateTime;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.TimePickerView;

public abstract class QuestionTimeFragment extends QuestionFragment<String> {

    TimePickerView timePicker;

    DateTime defaultTime;


    public QuestionTimeFragment() {
    }


    @Override
    protected void initViews(ViewGroup root) {

        timePicker = (TimePickerView) root.findViewById(R.id.timePicker);
        timePicker.setListener(new TimePickerView.TimePickerListener() {
            @Override
            public void onTimePicked(DateTime time) {
                if (listener != null) {
                    listener.onSelected(getValue());
                }
            }
        });
        timePicker.setTime(QuestionNotificationFragment.parseDateString(getValue()));
        timePicker.setDefaultTime(defaultTime);


    }

    public void setTime(DateTime time) {
        timePicker.setTime(time);
    }


    @Override
    public String getValue() {
        DateTime time = timePicker.getTime();
        return QuestionNotificationFragment.encode(time);
    }

    @Override
    public void setValue(String dateString) {
        super.setValue(dateString);
    }

    public void setDefaultTime(DateTime defaultTime) {
        this.defaultTime = defaultTime;
    }



}
