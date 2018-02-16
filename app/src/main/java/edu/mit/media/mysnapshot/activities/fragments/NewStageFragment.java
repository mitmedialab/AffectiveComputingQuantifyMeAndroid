package edu.mit.media.mysnapshot.activities.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.media.mysnapshot.R;


public class NewStageFragment extends DialogFragment {

    public NewStageFragment() {

    }

    View rootView;

    public int getLayoutId() {
        return R.layout.fragment_new_stage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(getLayoutId(), container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rootView.findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }


    protected static final String FRAGMENT_TAG = "QEGIg240ng4qEN*)NEQG8)Gr";

    public static void showDialog(Activity activity) {
        NewStageFragment fragment = new NewStageFragment();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        fragment.setCancelable(false);

        fragment.show(activity.getFragmentManager().beginTransaction(), FRAGMENT_TAG);
    }

}