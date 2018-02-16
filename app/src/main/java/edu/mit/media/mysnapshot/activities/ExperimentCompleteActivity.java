package edu.mit.media.mysnapshot.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.backend.Experiment;

public class ExperimentCompleteActivity extends PermissionCheckingAppCompatActivity {

    public static final String LOGTAG = ExperimentCompleteActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Experiment experiment = Experiment.getCurrentExperiment(this);

        if (experiment == null || ! experiment.isFinished()) {
            return;
        }

        Experiment.ExperimentType experimentType = experiment.getType();

        setContentView(R.layout.activity_experiment_complete);

        // ((ImageView) findViewById(R.id.icon)).setImageResource(experimentType.iconId);

        findViewById(R.id.choose_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Experiment.deleteCurrentExperiment(ExperimentCompleteActivity.this);
                startActivity(new Intent(ExperimentCompleteActivity.this, ExperimentChooseActivity.class));
                finish();
            }
        });


        TextView resultView = (TextView) findViewById(R.id.result);
        resultView.setText(experimentType.formatResult(experiment.result.resultValue));

        TextView confidenceView = (TextView) findViewById(R.id.confidence);
        confidenceView.setText("(With a " + (int)Math.round(experiment.result.resultConfidence * 100) + "% confidence)");

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExperimentCompleteActivity.this, SettingsActivity.class));
            }
        });
        findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExperimentCompleteActivity.this, HistoryActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        Experiment experiment = Experiment.getCurrentExperiment(this);
        if (experiment == null || ! experiment.isFinished()) {
            startActivity(new Intent(this, MainActivity.class));

            finish();
            overridePendingTransition(0, 0);
            return;
        }


    }


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ExperimentCompleteActivity.class);
        context.startActivity(intent);
    }

}