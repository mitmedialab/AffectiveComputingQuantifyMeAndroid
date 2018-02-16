package edu.mit.media.mysnapshot.activities.questions.fragment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.mit.media.mysnapshot.R;

public class QuestionDateFragment extends QuestionFragment<String> {

    View button;
    TextView dateTextView;
    Calendar date;

    public QuestionDateFragment() {
    }


    @Override
    protected void initViews(ViewGroup root) {

        button = root.findViewById(R.id.button);
        dateTextView = (TextView) root.findViewById(R.id.date);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        setDateText();

    }

    boolean shown = false;

    public void showDatePickerDialog() {
        if (shown) {
            return;
        }
        shown = true;

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

    public void setDate(Calendar date) {
        this.date = date;
        setDateText();
        if (listener != null) {
            listener.onSelected(getValue());
        }
    }


    public void setDateText() {
        if (dateTextView != null) {
            if (date == null) {
                dateTextView.setText("");
                dateTextView.setVisibility(View.GONE);
            } else {
                dateTextView.setText(new SimpleDateFormat("MMMM d\nyyyy").format(date.getTime()));
                dateTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public String getValue() {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    @Override
    public void setValue(String dateString) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
            setDate(cal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Calendar defaultCal = Calendar.getInstance();
            defaultCal.set(1970,0,1);
            final Calendar c = date == null ? defaultCal : date;
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            shown = false;
            setDate(new GregorianCalendar(year, month, day));
        }

        @Override
        public void onStop() {
            super.onStop();
            shown = false;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_question_date;
    }


}
