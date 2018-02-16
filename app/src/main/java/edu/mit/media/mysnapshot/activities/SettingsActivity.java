package edu.mit.media.mysnapshot.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;
import java.util.TimeZone;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.activities.fragments.CreditsFragment;
import edu.mit.media.mysnapshot.activities.questions.JawboneQuestionActivity;
import edu.mit.media.mysnapshot.activities.questions.QuestionListener;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionCheckboxFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionChoiceFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionDateFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionJawboneFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionNotificationFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionRadioGroupFragment;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionSpinnerFragment;
import edu.mit.media.mysnapshot.backend.BackendAPI;
import edu.mit.media.mysnapshot.backend.BackendAPIFactory;
import edu.mit.media.mysnapshot.view.SelectableIcon;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

@SuppressWarnings("ValidFragment")
public class SettingsActivity extends JawboneQuestionActivity {

    public static final String LOGTAG = SettingsActivity.class.getName();

    SharedPreferences sharedPreferences;

    public UserData userData = null;


    QuestionCheckboxFragment terms;
    QuestionJawboneFragment jawbone;
    QuestionNotificationFragment notification;
    QuestionDateFragment birthdate;
    QuestionSpinnerFragment race;
    QuestionChoiceFragment genders;
    QuestionRadioGroupFragment happy;
    QuestionRadioGroupFragment stress;
    QuestionSpinnerFragment activity;
    QuestionRadioGroupFragment sleepQuality;


    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initFragments(List<Fragment> fragments, List<Drawable> icons) {

        initTerms(fragments);
        initJawbone(fragments);
        initBirthdate(fragments);
        initRace(fragments);
        initGenders(fragments);
        initNotification(fragments);
        initHappy(fragments);
        initStress(fragments);
        initActivity(fragments);
        initSleepQuality(fragments);

        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreditsDialog();
            }
        });

    }

    @Override
    protected boolean loadInitialData() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String str = sharedPreferences.getString(USERDATAPREF, "");
        UserDataLoaded loaded = loadUserData(this);
        userData = loaded.userData;

        if (! loaded.existed) {
            View controls = findViewById(R.id.controls);
            controls.setVisibility(View.GONE);
        } else {
            View backbutton = findViewById(R.id.backbutton);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            View savebutton = findViewById(R.id.savebutton);
            savebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFinish();
                }
            });
        }

        return loaded.existed;
    }

    @Override
    public void onFinish() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Saving...");
        dialog.setCancelable(false);
        dialog.setIcon(android.R.drawable.ic_menu_save);
        dialog.show();

        getDataFromQuestions();

        String timezone = TimeZone.getDefault().getID();

        BackendAPIFactory.getAPI(this).setBiographicData(
                userData.jawboneAccess,
                userData.jawboneReset,
                userData.dobString,
                userData.race,
                userData.gender,
                userData.happy,
                userData.stress,
                userData.activity,
                userData.sleepQuality,
                timezone
                )
            .enqueue(new Callback<BackendAPI.SettingsResult>() {

                @Override
                public void onResponse(Response<BackendAPI.SettingsResult> response, Retrofit retrofit) {
                    dialog.dismiss();
                    if (response.isSuccess() && response.body() != null && response.body().success) {

                        onSaveSuccess(response.body());

                    } else {
                        onFailure(null);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(SettingsActivity.this, "An error happened! Please try again!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
        });

    }

    void onSaveSuccess(BackendAPI.SettingsResult result) {

        saveUserData();

        if (isBuildingData()) {
            Intent intent = new Intent(SettingsActivity.this, IntroThanksActivity.class);
            startActivity(intent);

            finish();
            overridePendingTransition(0, 0);
        } else {
            Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    void initGenders(List<Fragment> fragments) {
        genders = new QuestionChoiceFragment();
        genders.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_gender, "What's your gender?"));
        genders.addChoice(new SelectableIcon.IconChoice("Male", R.drawable.question_icon_male, "m", getResources().getColor(R.color.gender_male)))
                .addChoice(new SelectableIcon.IconChoice("Female", R.drawable.question_icon_female, "f", getResources().getColor(R.color.gender_female)));
        genders.setListener(new QuestionListener<String>() {

            @Override
            public void onSelected(String value) {
                onPageComplete();
            }
        });
        if (! isBuildingData()) {
            genders.setValue(userData.gender);
        }
        fragments.add(genders);
    }

    void initTerms(List<Fragment> fragments) {
        terms = new QuestionCheckboxFragment();
        terms.setLayout(new QuestionFragment.Layout(R.drawable.art_icon, "Terms and Conditions\nof Science!"));
        terms.setText(getResources().getString(R.string.terms_text));
        terms.setListener(new QuestionListener<Boolean>() {

            @Override
            public void onSelected(Boolean value) {
                if (value) {
                    onPageComplete();
                }
                if (value) {
                    terms.checkbox.setChecked(true);
                    terms.checkbox.setEnabled(false);
                    userData.acceptedTerms = value;
                }
            }
        });
        if (! isBuildingData()) {
            terms.setValue(userData.acceptedTerms);
        }
        fragments.add(terms);
    }

    void initNotification(List<Fragment> fragments) {
        notification = new QuestionNotificationFragment();
        notification.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_alarm, "Would you like daily notification reminders?"));
        notification.setListener(new QuestionListener<QuestionNotificationFragment.NotificationData>() {

            @Override
            public void onSelected(QuestionNotificationFragment.NotificationData value) {
                onPageComplete();
            }
            @Override
            public void onDataSave(QuestionNotificationFragment.NotificationData value) {
                onPageComplete();
                waitThenSlidePage();
            }

        });
        notification.setValue(userData.notificationData);
        fragments.add(notification);
    }

    void initBirthdate(List<Fragment> fragments) {
        birthdate = new QuestionDateFragment();
        birthdate.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_birthday, "When is your birthdate?"));
        birthdate.setListener(new QuestionListener<String>() {

            @Override
            public void onSelected(String value) {
                onPageComplete();
            }
        });
        if (! isBuildingData()) {
            birthdate.setValue(userData.dobString);
        }
        fragments.add(birthdate);
    }

    void initRace(List<Fragment> fragments) {

        race = new QuestionSpinnerFragment();
        race.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_race, "Which of these describes you the best?"));
        race.init( R.array.racevalues, R.array.races, "Please Select an Option");
        race.setListener(new QuestionListener<String>() {

            @Override
            public void onSelected(String value) {
                onPageComplete();
            }
        });
        if (! isBuildingData()) {
            race.setValue(userData.race);
        }
        fragments.add(race);
    }

    void initJawbone(List<Fragment> fragments) {
        jawbone = new QuestionJawboneFragment();
        jawbone.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_jawbone, "Please connect your Jawbone UP account."));
        jawbone.setListener(new QuestionListener<Boolean>() {

            @Override
            public void onSelected(Boolean value) {
                onPageComplete();
            }

            @Override
            public void onDataSave(Boolean value) {
                onPageComplete();
                waitThenSlidePage();
            }

        });
        if (! isBuildingData()) {
            jawbone.setValue(true);
            jawbone.accessToken = userData.jawboneAccess;
            jawbone.resetToken = userData.jawboneReset;

        }
        fragments.add(jawbone);
    }

    void initActivity(List<Fragment> fragments) {

        activity = new QuestionSpinnerFragment();
        activity.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_activity, "On average, how many hours are you active each day?"));
        activity.init( R.array.activityvalues, R.array.activity, "Please Select an Option");

        activity.setListener(new QuestionListener<String>() {

            @Override
            public void onSelected(String value) {
                onPageComplete();
            }
        });
        if (! isBuildingData()) {
            activity.setValue(userData.activity);
        }
        fragments.add(activity);
    }

    void initStress(List<Fragment> fragments) {
        stress = new QuestionRadioGroupFragment();
        stress.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_stress, "What is your average stress level?"));
        stress.init("Very Low", "Very High", getResources().getColor(R.color.radio_green), getResources().getColor(R.color.radio_red), 7);
        stress.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        if (! isBuildingData()) {
            stress.setValue(userData.stress);
        }
        fragments.add(stress);
    }

    void initHappy(List<Fragment> fragments) {
        happy = new QuestionRadioGroupFragment();
        happy.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_happiness, "What is your average happiness level?"));
        happy.init("Very Unhappy", "Very Happy");
        happy.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        if (! isBuildingData()) {
            happy.setValue(userData.happy);
        }
        fragments.add(happy);
    }

    void initSleepQuality(List<Fragment> fragments) {
        sleepQuality = new QuestionRadioGroupFragment();
        sleepQuality.setLayout(new QuestionFragment.Layout(R.drawable.icon_settings_sleep, "On average, how well do you sleep?"));
        sleepQuality.init("Terrible", "Great!");
        sleepQuality.setListener(new QuestionListener<Integer>() {

            @Override
            public void onSelected(Integer value) {
                onPageComplete();
            }
        });
        if (! isBuildingData()) {
            sleepQuality.setValue(userData.sleepQuality);
        }
        fragments.add(sleepQuality);
    }

    protected void getDataFromQuestions() {
        userData.acceptedTerms = terms.getValue();
        userData.jawboneAccess = jawbone.accessToken;
        userData.jawboneReset = jawbone.resetToken;
        userData.dobString = birthdate.getValue();
        userData.race = race.getValue();
        userData.gender = genders.getValue();
        userData.happy = happy.getValue();
        userData.stress = stress.getValue();
        userData.activity = activity.getValue();
        userData.sleepQuality = sleepQuality.getValue();
        userData.notificationData = notification.getValue();
    }

    protected void saveUserData() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (userData != null) {
            Gson gson = new Gson();
            editor.putString(USERDATAPREF, gson.toJson(userData));
        } else {
            editor.remove(USERDATAPREF);
        }
        editor.apply();
    }



    public static boolean hasSetUserData(SharedPreferences sharedPreferences) {
        return sharedPreferences.contains(USERDATAPREF);
    }

    @Override
    protected void onJawboneAuth(String accessToken, String resetToken) {
        jawbone.onJawboneAuth(accessToken, resetToken);
    }

    public static class UserData {
        public boolean acceptedTerms = false;
        public String gender, race;
        public String dobString;
        public int happy, stress, sleepQuality;
        public String jawboneAccess, jawboneReset;
        public String activity;

        public QuestionNotificationFragment.NotificationData notificationData;

        public UserData() {

        }

        public QuestionNotificationFragment.NotificationData getNotificationData() {
            return notificationData == null ? new QuestionNotificationFragment.NotificationData() : notificationData;
        }
    }

    public static final String USERDATAPREF = "userdataprefyo";

    public static class UserDataLoaded {
        public UserData userData;
        public boolean existed = true;
    }

    public static UserDataLoaded loadUserData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String str = sharedPreferences.getString(USERDATAPREF, "");
        boolean existed = true;

        Gson gson = new Gson();
        UserData userData;
        try {
            userData = gson.fromJson(str, UserData.class);
        } catch (Exception e) {
            userData = null;
        }

        if (userData == null) {
            userData = new UserData();
            existed = false;
        }
        UserDataLoaded loaded = new UserDataLoaded();
        loaded.existed = existed;
        loaded.userData = userData;

        return loaded;
    }

    private static final String DIALOG_TAG = "DialogFragment";

    void showCreditsDialog() {
        CreditsFragment fragment = new CreditsFragment();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        fragment.setCancelable(false);

        fragment.show(getFragmentManager().beginTransaction(), DIALOG_TAG);
    }




}
