package edu.mit.media.mysnapshot.activities.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.mit.media.mysnapshot.R;


public class CreditsFragment extends DialogFragment {

    public CreditsFragment() {

    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.dialog_credits, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rootView.findViewById(R.id.scrollcontent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView creditsView = (TextView) rootView.findViewById(R.id.iconsCredits);
        String[] credits = getResources().getStringArray(R.array.iconcredits);
        creditsView.setText(TextUtils.join("\n", credits));

        return rootView;
    }

}