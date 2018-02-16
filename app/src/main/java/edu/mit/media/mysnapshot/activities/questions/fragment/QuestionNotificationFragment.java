package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.TimePickerView;

public class QuestionNotificationFragment extends QuestionFragment<QuestionNotificationFragment.NotificationData> {

    public static final String DEFAULT_TIME = "09:30";

    public static class NotificationData {
        public String notificationTime = DEFAULT_TIME;
        public boolean notificationSet = true;
    }

    AppCompatCheckBox notificationCheckbox;
    TimePickerView notificationTime;

    public QuestionNotificationFragment() {
    }

    @Override
    protected void initViews(ViewGroup root) {

        notificationCheckbox = (AppCompatCheckBox) root.findViewById(R.id.notificationCheckBox);
        notificationTime = (TimePickerView)root.findViewById(R.id.notificationTimePicker);

        notificationTime.setTime(parseDateString(value.notificationTime));
        notificationTime.setListener(new TimePickerView.TimePickerListener() {
            @Override
            public void onTimePicked(DateTime time) {
                value.notificationTime = encode(time);
            }
        });
        notificationCheckbox.setChecked(value.notificationSet);
        notificationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                value.notificationSet = isChecked;
            }
        });

        View button = root.findViewById(R.id.doneButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                value.notificationTime = encode(notificationTime.getTime());
                value.notificationSet = notificationCheckbox.isChecked();

                listener.onDataSave(value);
            }
        });

        if (! isBuildingData()) {
            button.setVisibility(View.GONE);
        }


    }

    @Override
    public void setValue(QuestionNotificationFragment.NotificationData value) {
        if (value == null) {
            value = new NotificationData();
        }
        if (value.notificationTime == null) {
            value.notificationTime = DEFAULT_TIME;
        }
        super.setValue(value);
    }

    public static DateTime parseDateString(String in) {
        if (in == null) {
            return null;
        }
        return DateTimeFormat.forPattern("HH:mm").parseDateTime(in);
    }

    public static String encode(DateTime in) {
        if (in == null) {
            return null;
        }
        return DateTimeFormat.forPattern("HH:mm").print(in);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_notification;
    }
}
