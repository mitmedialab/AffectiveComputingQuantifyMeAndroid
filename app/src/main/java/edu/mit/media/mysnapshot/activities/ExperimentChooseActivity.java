package edu.mit.media.mysnapshot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.backend.Experiment;

public class ExperimentChooseActivity extends PermissionCheckingAppCompatActivity {

    public static final String LOGTAG = ExperimentChooseActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Experiment.getCurrentExperiment(this) != null) {
            startActivity(new Intent(this, ExperimentCheckinActivity.class));
            finish();
        }


        setContentView(R.layout.activity_experiment_choose);

        initButton(R.id.leisurehappiness, Experiment.leisureHappy);
        initButton(R.id.stepssleepefficiency, Experiment.stepsSleepEfficiency);
        initButton(R.id.sleepdurationproductivity, Experiment.sleepDurationProductivity);
        initButton(R.id.sleepvariabilitystress, Experiment.sleepVariabilityStress);

    }

    void initButton(int buttonId, final Experiment.ExperimentType experimentType) {
        View button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperimentIntroActivity.startActivity(ExperimentChooseActivity.this, experimentType);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        Experiment experiment = Experiment.getCurrentExperiment(this);

        if (experiment != null) {
            startActivity(new Intent(this, MainActivity.class));

            finish();
            overridePendingTransition(0, 0);
            return;
        }

    }


}