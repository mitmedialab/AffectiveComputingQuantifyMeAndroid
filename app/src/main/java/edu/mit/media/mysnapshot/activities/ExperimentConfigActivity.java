package edu.mit.media.mysnapshot.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.util.List;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.activities.questions.QuestionActivity;
import edu.mit.media.mysnapshot.activities.questions.QuestionListener;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionRadioGroupFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionTextFragment;
import edu.mit.media.mysnapshot.backend.BackendAPI;
import edu.mit.media.mysnapshot.backend.BackendAPIFactory;
import edu.mit.media.mysnapshot.backend.Experiment;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

@SuppressWarnings("ValidFragment")
public class ExperimentConfigActivity extends QuestionActivity {

    public static final String LOGTAG = ExperimentConfigActivity.class.getName();
    public static final String EXPERIMENT_TYPE_EXTRA = "ADGHIOADGOUADGOUADG";

    QuestionTextFragment textFragment;
    QuestionRadioGroupFragment appEfficacy, experimentEfficacy, selfEfficacy;

    Experiment.ExperimentType experimentType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_experiment_config;
    }

    @Override
    protected void initFragments(List<Fragment> fragments, List<Drawable> icons) {

        String typeName = getIntent().getExtras().getString(EXPERIMENT_TYPE_EXTRA);
        experimentType = Experiment.ExperimentType.getType(typeName);

        initText(fragments, icons);
        initAppEfficacy(fragments, icons);
        initExperimentEfficacy(fragments, icons);
        initSelfEfficacy(fragments, icons);
    }

    void initText(List<Fragment> fragments, List<Drawable> icons) {
        textFragment = new QuestionTextFragment();
        textFragment.setLayout(new QuestionFragment.Layout(experimentType.iconId, "Configuration"));
        textFragment.init("First, we need to ask you some questions to help us make your experiment.");
        textFragment.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(textFragment);
    }

    void initAppEfficacy(List<Fragment> fragments, List<Drawable> icons) {
        appEfficacy = new QuestionRadioGroupFragment();
        appEfficacy.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_app_effectiveness, "How effective do you think this app will be in helping you run this experiment?"));
        appEfficacy.init("Poor", "Great");
        appEfficacy.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(appEfficacy);
    }

    void initExperimentEfficacy(List<Fragment> fragments, List<Drawable> icons) {
        experimentEfficacy = new QuestionRadioGroupFragment();
        experimentEfficacy.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_chart, "How effective do you think this experiment will be in getting concrete results?"));
        experimentEfficacy.init("Poor", "Great");
        experimentEfficacy.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(experimentEfficacy);
    }

    void initSelfEfficacy(List<Fragment> fragments, List<Drawable> icons) {
        selfEfficacy = new QuestionRadioGroupFragment();
        selfEfficacy.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_self_effectiveness, "How effective do you think you will be in carrying out the experiment?"));
        selfEfficacy.init("Poor", "Great");
        selfEfficacy.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        fragments.add(selfEfficacy);
    }

    @Override
    protected boolean loadInitialData() {

        return false;

    }

    @Override
    public void onFinish() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Starting Experiment...");
        dialog.setIcon(android.R.drawable.ic_menu_upload);
        dialog.show();

        final Experiment experiment = new Experiment(experimentType,
                selfEfficacy.getValue(),
                appEfficacy.getValue(),
                experimentEfficacy.getValue());


        BackendAPIFactory.getAPI(this).startExperiment(
                experiment.typeKey,
                experiment.selfEfficacy,
                experiment.appEfficacy,
                experiment.experimentEfficacy
        )
                .enqueue(new Callback<BackendAPI.CheckinResponse>() {

                    @Override
                    public void onResponse(Response<BackendAPI.CheckinResponse> response, Retrofit retrofit) {
                        dialog.dismiss();
                        if (response.isSuccess() && response.body() != null && response.body().success) {

                            onSaveSuccess(response.body(), experiment);

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

    void onSaveSuccess(BackendAPI.CheckinResponse result, Experiment experiment) {

        experiment.key = result.key;
        experiment.setAsCurrentExperiment(this);

        startActivity(new Intent(this, ExperimentCreatedActivity.class));
        finish();
        overridePendingTransition(0, 0);
    }


    public static void startActivity(Context context, Experiment.ExperimentType experimentType) {
        Intent intent = new Intent(context, ExperimentConfigActivity.class);
        intent.putExtra(EXPERIMENT_TYPE_EXTRA, experimentType.typeKey);
        context.startActivity(intent);
    }



}