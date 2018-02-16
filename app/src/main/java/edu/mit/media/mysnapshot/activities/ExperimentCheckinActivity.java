package edu.mit.media.mysnapshot.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.activities.questions.QuestionActivity;
import edu.mit.media.mysnapshot.activities.questions.QuestionListener;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionRadioGroupFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionSpinnerFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionTextActionButtonFragment;
import edu.mit.media.mysnapshot.backend.BackendAPI;
import edu.mit.media.mysnapshot.backend.BackendAPIFactory;
import edu.mit.media.mysnapshot.backend.Experiment;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

@SuppressWarnings("ValidFragment")
public class ExperimentCheckinActivity extends QuestionActivity {

    public static final String LOGTAG = ExperimentCheckinActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    QuestionTextActionButtonFragment textFragment;
    QuestionRadioGroupFragment didFollowDirections;
    QuestionRadioGroupFragment happy, stress, productivity;
    QuestionSpinnerFragment leisure;

    Experiment.ExperimentType experimentType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_experiment_config;
    }

    @Override
    protected void initFragments(List<Fragment> fragments, List<Drawable> icons) {

        Experiment experiment = Experiment.getCurrentExperiment(this);
        if (experiment == null) {
            return;
        }

        experimentType = experiment.getType();

        initText(fragments);
        initDidFollowDirections(fragments);
        initLeisure(fragments);
        initHappy(fragments);
        initStress(fragments);
        initProductivity(fragments);

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
    }


    void initText(List<Fragment> fragments) {
        textFragment = new QuestionTextActionButtonFragment();
        textFragment.setLayout(new QuestionFragment.Layout(experimentType.iconId, "Daily Check In"));
        textFragment.init("We're going to ask you a couple quick questions about your day, then we'll let you know what you should do for your experiment.\n\nYou only need to check in once a day!",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.jawbone.up");
                        if (launchIntent != null) {
                            startActivity(launchIntent); //null pointer check in case package name was not found
                        }
                    }
                });
        textFragment.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete(true);
            }
        });
        fragments.add(textFragment);
    }

    void initDidFollowDirections(List<Fragment> fragments) {
        didFollowDirections = new QuestionRadioGroupFragment();
        didFollowDirections.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_self_effectiveness, "How did you do with following the experiment's instructions?"));
        didFollowDirections.init("Poor", "Great");
        didFollowDirections.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(didFollowDirections);
    }



    void initProductivity(List<Fragment> fragments) {

        productivity = new QuestionRadioGroupFragment();
        productivity.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_productivity, "How productive were you in the past 24 hours?"));
        productivity.init("Not at all", "Extremely");
        productivity.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(productivity);
    }

    void initStress(List<Fragment> fragments) {
        stress = new QuestionRadioGroupFragment();
        stress.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_stress, "How stressed were you in the past 24 hours?"));
        stress.init("Not at all", "Extremely", getResources().getColor(R.color.radio_green), getResources().getColor(R.color.radio_red), 7);
        stress.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(stress);
    }

    void initHappy(List<Fragment> fragments) {
        happy = new QuestionRadioGroupFragment();
        happy.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_happiness, "How happy were you in the past 24 hours?"));
        happy.init("Not at all", "Extremely");
        happy.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(happy);
    }

    void initLeisure(List<Fragment> fragments) {
        leisure = new QuestionSpinnerFragment();
        leisure.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_leisure, "How much leisure time did you have in the past 24 hours?"));
        leisure.init( R.array.leisurevalues, R.array.leisurelabels, "Please Select an Option");
        leisure.setListener(new QuestionListener<String>() {

            @Override
            public void onSelected(String value) {
                onPageComplete();
            }
        });
        fragments.add(leisure);
    }



    @Override
    protected boolean loadInitialData() {

        return false;

    }

    @Override
    public void onFinish() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Generating daily instructions...");
        dialog.setCancelable(false);
        dialog.setIcon(android.R.drawable.ic_menu_upload);
        dialog.show();

        Experiment experiment = Experiment.getCurrentExperiment(this);

        final Experiment.Checkin checkin = new Experiment.Checkin(experiment.key,
                didFollowDirections.getValue(),
                happy.getValue(),
                stress.getValue(),
                productivity.getValue(),
                Integer.parseInt(leisure.getValue()));

        String version = "version not found";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        BackendAPIFactory.getAPI(this).experimentCheckin(
                checkin.experimentKey,
                checkin.didFollowInstructions,
                checkin.happy,
                checkin.stress,
                checkin.productivity,
                checkin.leisure,
                version
        )
                .enqueue(new Callback<BackendAPI.CheckinResponse>() {

                    @Override
                    public void onResponse(Response<BackendAPI.CheckinResponse> response, Retrofit retrofit) {
                        dialog.dismiss();
                        if (response.isSuccess() && response.body() != null && response.body().success) {

                            onSaveSuccess(response.body(), checkin);

                        } else {
                            onFailure(null);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "An error happened! Please try again!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

    }

    void onSaveSuccess(BackendAPI.CheckinResponse response, Experiment.Checkin checkin) {

        checkin.result = response.result;
        checkin.save(this);

        Experiment experiment = Experiment.getCurrentExperiment(this);
        experiment.setAsCurrentExperiment(this);
        experiment.currentStage = checkin.result.currentStage;
        experiment.result = checkin.result;
        experiment.setAsCurrentExperiment(this);

        if (response.result.isComplete) {
            ExperimentCompleteActivity.startActivity(this);
        } else {
            ExperimentInstructionsActivity.startActivity(this);
        }

        finish();
        overridePendingTransition(0, 0);
    }



}