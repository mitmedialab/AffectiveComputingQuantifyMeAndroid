package edu.mit.media.mysnapshot.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Calendar;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.activities.MainActivity;
import edu.mit.media.mysnapshot.activities.SettingsActivity;
import edu.mit.media.mysnapshot.activities.questions.fragment.QuestionNotificationFragment;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String LOGTAG = "AlarmReceiver";

    public static final int NOTIFICATION_ID = 4257245;

    public static final String CREATE_NOTIFICATION_ACTION = "edu.mit.media.mysnapshot.makenotificationsplz";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOGTAG,"Got broadcast: " + intent.getAction());

        if (CREATE_NOTIFICATION_ACTION.equals(intent.getAction())) {
            createNotification(context);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            setRecurringAlarm(context);
        }
    }

    public void createNotification(Context context) {

        Log.d(LOGTAG, "Creating notification");

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.art_icon)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle(context.getResources().getString(R.string.notification_title))
                        .setContentText(context.getResources().getString(R.string.notification_content));

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(CREATE_NOTIFICATION_ACTION);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        500000,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotifyMgr.notify(NOTIFICATION_ID, notification);
    }


    public static void setRecurringAlarm(Context context) {

        SettingsActivity.UserData userData = SettingsActivity.loadUserData(context).userData;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(CREATE_NOTIFICATION_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                64346357, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        if (! userData.getNotificationData().notificationSet || userData.getNotificationData().notificationTime == null) {
            alarms.cancel(pendingIntent);
            return;
        }

        DateTime notificationTime = QuestionNotificationFragment.parseDateString(userData.getNotificationData().notificationTime);

        Calendar updateTime = Calendar.getInstance();
        long currentTime = updateTime.getTimeInMillis();
        updateTime.set(Calendar.HOUR_OF_DAY, notificationTime.getHourOfDay());
        updateTime.set(Calendar.MINUTE, notificationTime.getMinuteOfHour());
        updateTime.set(Calendar.SECOND, 0);
        updateTime.set(Calendar.MILLISECOND, 0);

        if (updateTime.getTimeInMillis() <= currentTime) {
            updateTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        Log.d(LOGTAG, "Creating pending intent for: " + updateTime);

        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}