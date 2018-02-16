package edu.mit.media.mysnapshot.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.backend.Experiment;

public class ExperimentIntroActivity extends PermissionCheckingAppCompatActivity {

    public static final String LOGTAG = ExperimentIntroActivity.class.getName();

    public static final String EXPERIMENT_TYPE_EXTRA = "OUNAEGUONEGouanAENUGAE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String typeName = getIntent().getExtras().getString(EXPERIMENT_TYPE_EXTRA);
        final Experiment.ExperimentType experimentType = Experiment.ExperimentType.getType(typeName);

        setContentView(experimentType.introLayout);

        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperimentConfigActivity.startActivity(ExperimentIntroActivity.this, experimentType);
                finish();
            }
        });


    }

    public static void startActivity(Context context, Experiment.ExperimentType experimentType) {
        Intent intent = new Intent(context, ExperimentIntroActivity.class);
        intent.putExtra(EXPERIMENT_TYPE_EXTRA, experimentType.typeKey);
        context.startActivity(intent);
    }


}