package edu.mit.media.mysnapshot.activities.fragments;

import android.app.Activity;
import android.app.DialogFragment;

import edu.mit.media.mysnapshot.R;


public class FailedStageFragment extends NewStageFragment {

    public FailedStageFragment() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_failed_stage;
    }

    protected static final String FRAGMENT_TAG = "qgDASVIJGWNRvsn04wmg0esidv";

    public static void showDialog(Activity activity) {
        FailedStageFragment fragment = new FailedStageFragment();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        fragment.setCancelable(false);

        fragment.show(activity.getFragmentManager().beginTransaction(), FRAGMENT_TAG);
    }

}