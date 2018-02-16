package edu.mit.media.mysnapshot.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import edu.mit.media.mysnapshot.R;


public class TimePickerView extends FrameLayout {

    public interface TimePickerListener {
        void onTimePicked(DateTime time);
    }


    View button, rootView;
    TextView timeTextView;
    DateTime time = null;
    DateTime defaultTime = null;
    TimePickerListener listener;

    boolean smallPicker = false;

    Context context;


    public TimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimePickerView, 0, 0);
        try {
            smallPicker = ta.getBoolean(R.styleable.TimePickerView_small_picker, false);
        } finally {
            ta.recycle();
        }

        rootView = inflate(getContext(), smallPicker ? R.layout.view_timepicker_small : R.layout.view_timepicker, null);
        addView(rootView);

        button = rootView.findViewById(R.id.button);
        timeTextView = (TextView) rootView.findViewById(R.id.time);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        setTimeText();

    }


    public void setListener(TimePickerListener listener) {
        this.listener = listener;
    }


    boolean shown = false;

    public void showTimePickerDialog() {
        if (shown) {
            return;
        }
        shown = true;

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(((Activity) getContext()).getFragmentManager(), "timePicker");
    }

    public void setTime(DateTime time) {
        if (time == null) {
            return;
        }
        this.time = time;
        setTimeText();
    }

    public DateTime getTime() {
        return time;
    }


    public void setTimeText() {
        if (timeTextView != null) {
            if (time == null) {
                timeTextView.setText("");
                timeTextView.setVisibility(View.GONE);
            } else {
                timeTextView.setText(DateTimeFormat.forPattern("h:mm a").print(time));
                timeTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            DateTime defaultDT = defaultTime == null ? new DateTime().withTime(0, 0, 0, 0) : defaultTime;
            final DateTime d = time == null ? defaultDT : time;

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, d.getHourOfDay(), d.getMinuteOfHour(), true);
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            shown = false;
            setTime(new DateTime().withTime(hour,minute,0,0));
            if (listener != null) {
                listener.onTimePicked(time);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            shown = false;
        }
    }

    public void setDefaultTime(DateTime defaultTime) {
        this.defaultTime = defaultTime;
    }
}
