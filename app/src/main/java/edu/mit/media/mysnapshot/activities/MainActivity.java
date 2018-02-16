package edu.mit.media.mysnapshot.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import edu.mit.media.mysnapshot.backend.Experiment;
import edu.mit.media.mysnapshot.notifications.AlarmReceiver;

public class MainActivity extends AppCompatActivity {

    public static final String LOGTAG = MainActivity.class.getName();

    public static final String CLICK_NOTIFICATION_ACTION = "ClickedonNotification,man";

    public static final boolean FORCE_ACCOUNT_SETUP = false, FORCE_CHOOSE_EXPERIMENT = false, FORCE_CHECKIN = true, FORCE_NEW_STAGE_DIALOG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (FORCE_ACCOUNT_SETUP) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(SettingsActivity.USERDATAPREF);
            editor.apply();
        }

        if (FORCE_CHOOSE_EXPERIMENT) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Experiment.CURRENT_EXPERIMENT_PREF);
            editor.apply();
        }

        if (FORCE_CHECKIN) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Experiment.Checkin.LAST_CHECKIN_PREF);
            editor.apply();
        }

        if (! SettingsActivity.hasSetUserData(sharedPreferences)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (Experiment.getCurrentExperiment(this) == null) {
            Intent intent = new Intent(this, ExperimentChooseActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (FORCE_CHECKIN || ! Experiment.Checkin.hadCheckinToday(this)) {
            Intent intent = new Intent(this, ExperimentCheckinActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Intent intent = new Intent(this, ExperimentInstructionsActivity.class);
        startActivity(intent);
        finish();
        return;

    }

    @Override
    protected void onNewIntent (Intent intent) {
        clearNotifications(intent);
    }

    public void clearNotifications(Intent intent) {
        if (CLICK_NOTIFICATION_ACTION.equals(intent.getAction())) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID);
        }
    }





}