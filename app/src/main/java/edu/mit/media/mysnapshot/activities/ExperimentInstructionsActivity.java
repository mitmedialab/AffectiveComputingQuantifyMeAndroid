package edu.mit.media.mysnapshot.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.activities.fragments.FailedStageFragment;
import edu.mit.media.mysnapshot.activities.fragments.NewStageFragment;
import edu.mit.media.mysnapshot.backend.BackendAPI;
import edu.mit.media.mysnapshot.backend.BackendAPIFactory;
import edu.mit.media.mysnapshot.backend.Experiment;
import edu.mit.media.mysnapshot.view.FontTextView;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ExperimentInstructionsActivity extends PermissionCheckingAppCompatActivity {

    public static final String LOGTAG = ExperimentInstructionsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Experiment experiment = Experiment.getCurrentExperiment(this);
        if (experiment == null) {
            startActivity(new Intent(this, MainActivity.class));

            finish();
            overridePendingTransition(0, 0);
            return;
        }

        if (experiment.isFinished()) {
            ExperimentCompleteActivity.startActivity(this);

            finish();
            overridePendingTransition(0, 0);
            return;
        }

        initViews();

    }

    private void initViews() {
        Experiment.Checkin checkin = Experiment.Checkin.loadLastCheckin(this);

        if (checkin == null) {
            setContentView(R.layout.activity_experiment_first_day);

            findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ExperimentInstructionsActivity.this, SettingsActivity.class));
                }
            });
            findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ExperimentInstructionsActivity.this, HistoryActivity.class));
                }
            });

            return;
        }

        Experiment experiment = Experiment.getCurrentExperiment(this);
        Experiment.ExperimentType experimentType = experiment.getType();

        setContentView(R.layout.activity_experiment_instructions);

        if (checkin.result.restartedStage) {
            FailedStageFragment.showDialog(this);
            checkin.result.restartedStage = false;
            checkin.save(this);
        } else if (checkin.result.newStage || MainActivity.FORCE_NEW_STAGE_DIALOG) {
            NewStageFragment.showDialog(this);
            checkin.result.newStage = false;
            checkin.save(this);
        }

        TextView questionView = (TextView) findViewById(R.id.question);
        questionView.setText(experimentType.name);

        if (checkin.result.currentStage == 0) {
            findViewById(R.id.target_container).setVisibility(View.GONE);
        }

        TextView stageView = (TextView) findViewById(R.id.stage);
        stageView.setText("Stage " + (checkin.result.currentStage + 1));

        ImageView iconView = (ImageView) findViewById(R.id.icon);
        iconView.setImageDrawable(getResources().getDrawable(experimentType.iconId));

        TextView instructionsView = (TextView) findViewById(R.id.stage_instructions);
        String[] instructions = getResources().getStringArray(R.array.stage_instructions);
        instructionsView.setText(instructions[checkin.result.currentStage]);

        TextView targetView = (TextView) findViewById(R.id.target);
        if (checkin.result.target != null) {
            targetView.setText(experimentType.formatInstruction(checkin.result.target));
        } else {
            targetView.setText("");
        }

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExperimentInstructionsActivity.this, SettingsActivity.class));
            }
        });
        findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExperimentInstructionsActivity.this, HistoryActivity.class));
            }
        });
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshInstructions();
            }
        });

        LinearLayout inputGrid = (LinearLayout) findViewById(R.id.progressgrid);
        inputGrid.removeAllViews();

        for (int i = 0; i < 7; i++) {
            Float input = null;
            if (i < checkin.result.stageInputs.size()) {
                input = checkin.result.stageInputs.get(i);
            }
            FontTextView v = new FontTextView(this, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            v.setLayoutParams(params);
            v.setPadding(0, 10, 0, 10);
            v.setTypeFaceName(FontTextView.RALEWAY);
            v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            v.setBackgroundColor(((ColorDrawable)findViewById(R.id.bg).getBackground()).getColor());
            v.setGravity(Gravity.CENTER);
            if (input != null) {
                v.setText(experimentType.formatTarget(input));
            } else {
                v.setText("-");
            }
            inputGrid.addView(v);

            if (i == 3) {
                inputGrid = (LinearLayout) findViewById(R.id.progressgrid_secondrow);
                inputGrid.removeAllViews();
            }
        }
        FontTextView v = new FontTextView(this, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        v.setLayoutParams(params);
        v.setBackgroundColor(((ColorDrawable)findViewById(R.id.bg).getBackground()).getColor());
        inputGrid.addView(v);
    }


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ExperimentInstructionsActivity.class);
        context.startActivity(intent);
    }

    public void refreshInstructions() {
       final RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        findViewById(R.id.refresh).startAnimation(rotateAnimation);

        Experiment experiment = Experiment.getCurrentExperiment(this);

        BackendAPIFactory.getAPI(this).refreshInstructions(experiment.key).enqueue(new Callback<BackendAPI.CheckinResponse>() {
            @Override
            public void onResponse(Response<BackendAPI.CheckinResponse> response, Retrofit retrofit) {
                if (response != null && response.body() != null && response.body().success) {
                    Experiment.Checkin checkin = Experiment.Checkin.loadLastCheckin(getApplicationContext());
                    checkin.result = response.body().result;
                    checkin.save(getApplicationContext());
                    initViews();
                } else {
                    onFailure(null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                rotateAnimation.reset();
                rotateAnimation.cancel();
                Toast.makeText(getApplicationContext(), "Refresh failed!", Toast.LENGTH_LONG).show();
            }
        });

    }


}