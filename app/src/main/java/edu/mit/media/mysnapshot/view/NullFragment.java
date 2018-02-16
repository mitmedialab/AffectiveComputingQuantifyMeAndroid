package edu.mit.media.mysnapshot.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.media.mysnapshot.R;

public class NullFragment extends Fragment {

    public NullFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_null, container, false);

    }
}
