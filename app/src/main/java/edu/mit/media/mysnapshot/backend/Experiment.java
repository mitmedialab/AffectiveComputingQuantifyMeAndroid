package edu.mit.media.mysnapshot.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import edu.mit.media.mysnapshot.R;

public class Experiment {

    public static abstract class ExperimentType {
        public final String typeKey, name;
        public final int introLayout, iconId;

        private static final Map<String, ExperimentType> types = new HashMap<>();

        protected ExperimentType(String typeKey, String name, int introLayout, int iconId) {
            this.typeKey = typeKey;
            this.name = name;
            this.introLayout = introLayout;
            this.iconId = iconId;

            types.put(typeKey, this);
        }

        public static ExperimentType getType(String typeName) {
            ExperimentType t = types.get(typeName);
            return t != null ? t : leisureHappy;
        }

        public abstract String formatInstruction(float value);
        public abstract String formatTarget(float target);
        public abstract String formatResult(float value);
    }

    public static final ExperimentType leisureHappy = new ExperimentType("leisurehappiness",
            "How does my leisure time affect my happiness?",
            R.layout.experiment_intro_leisurehappy,
            R.drawable.icon_experiment_leisurehappiness) {

        public String formatResult(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "Try to get around " + df.format(value / 60.0f) + " hours of leisure time each day";
        }

        public String formatInstruction(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "Try to take " + df.format(value / 60.0f) + " hours of leisure time today";
        }

        public String formatTarget(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(value / 60.0f) + " Hours";
        }

    };
    public static final ExperimentType sleepVariabilityStress = new ExperimentType("sleepvariabilitystress",
            "How do inconsistent bedtimes affect my stress level?",
            R.layout.experiment_intro_sleepvariabilitystress,
            R.drawable.icon_experiment_sleepdurationproductivity) {

        public String formatResult(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "Try to go to sleep within " + df.format(value) + " minutes each day";
        }

        public String formatInstruction(float value) {
            return "Try to go to sleep at " + formatTarget(value) + " today";
        }

        public String formatTarget(float target) {
            while (target < 0) {
                target += 24*60;
            }
            target = target % (24*60);
            DateTime dt = new DateTime().withTimeAtStartOfDay().withHourOfDay((int)target/60).withMinuteOfHour((int)target%60);
            DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mma");
            return dt.toString(fmt);
        }

    };
    public static final ExperimentType sleepDurationProductivity = new ExperimentType("sleepdurationproductivity",
            "How does my nightly sleep affect my productivity?",
            R.layout.experiment_intro_sleepdurationproductivity,
            R.drawable.icon_experiment_sleepvariabilitystress) {

        public String formatInstruction(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "Try to sleep " + df.format(value/60.0) + " hours tonight";
        }

        public String formatResult(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "Try to sleep " + df.format(value/60.0) + " hours each night";
        }

        public String formatTarget(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(value / 60.0f) + " Hours";
        }


    };
    public static final ExperimentType stepsSleepEfficiency = new ExperimentType("stepssleepefficiency",
            "How does my activity level affect my sleep efficiency?",
            R.layout.experiment_intro_stepssleepefficiency,
            R.drawable.icon_experiment_stepssleepefficiency) {

        public String formatResult(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "Try to walk " + df.format(value) + " steps every day";
        }
        public String formatInstruction(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "Try to walk " + df.format(value) + " steps today";
        }

        public String formatTarget(float value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(value) + " Steps";
        }


    };


    public String typeKey;
    public DateTime start;
    public String key;
    public int selfEfficacy, appEfficacy, experimentEfficacy;
    public int currentStage = 0;

    public BackendAPI.CheckinResponse.ExperimentCompleteResult result;

    public Experiment() {
    }

    public Experiment(ExperimentType experimentType,
                      int selfEfficacy,
                      int appEfficacy,
                      int experimentEfficacy) {
        this.typeKey = experimentType.typeKey;
        this.start = DateTime.now();
        this.selfEfficacy = selfEfficacy;
        this.appEfficacy = appEfficacy;
        this.experimentEfficacy = experimentEfficacy;
    }

    public ExperimentType getType() {
        return ExperimentType.getType(typeKey);
    }

    public boolean isFinished() {
        return result != null && result.isComplete;
    }



    public static final String CURRENT_EXPERIMENT_PREF = "ADGHIAGDAGDHIAGDAGD";

    static final Gson gson = Converters.registerDateTime(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).create();

    public static Experiment getCurrentExperiment(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(CURRENT_EXPERIMENT_PREF, null);
        if (json != null) {
            try {
                return gson.fromJson(json, Experiment.class);
            } catch (Exception e) {

            }
        }
        return null;
    }

    public void setAsCurrentExperiment(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = gson.toJson(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_EXPERIMENT_PREF, json);
        editor.commit();
    }

    public static void deleteCurrentExperiment(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(CURRENT_EXPERIMENT_PREF);
        editor.commit();
    }


    public static class Checkin {

        public static final String LAST_CHECKIN_PREF = "UADGU)NQTEAGD*S)MQTESFD";

        public DateTime time;
        public String experimentKey;
        public int didFollowInstructions, happy, stress, productivity, leisure;

        public BackendAPI.CheckinResponse.CheckinResult result;

        public Checkin() {

        }

        public Checkin(String experimentKey, int didFollowInstructions, int happy, int stress, int productivity, int leisure) {
            this.time = DateTime.now();
            this.experimentKey = experimentKey;
            this.didFollowInstructions = didFollowInstructions;
            this.happy = happy;
            this.stress = stress;
            this.productivity = productivity;
            this.leisure = leisure;
        }

        public void save(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String json = gson.toJson(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LAST_CHECKIN_PREF, json);
            editor.commit();
        }

        public static Checkin loadLastCheckin(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String json = prefs.getString(LAST_CHECKIN_PREF, null);
            if (json != null) {
                try {
                    return gson.fromJson(json, Checkin.class);
                } catch (Exception e) {

                }
            }
            return null;
        }

        public static boolean hadCheckinToday(Context context) {
            Experiment experiment = getCurrentExperiment(context);
            if (experiment == null) {
                return true;
            }
            DateTime now = DateTime.now();

            if (sameDate(experiment.start, now)) {
                // we don't want to ask for a checkin on the first day it's created.
                return true;
            }

            Checkin lastCheckin = loadLastCheckin(context);
            if (lastCheckin == null) {
                return false;
            }
            return sameDate(now, lastCheckin.time);
        }

        static public boolean sameDate ( DateTime dt1 , DateTime dt2 )
        {
            LocalDate ld1 = new LocalDate( dt1 );
            // LocalDate determination depends on the time zone.
            // So be sure the date-time values are adjusted to the same time zone.
            LocalDate ld2 = new LocalDate( dt2.withZone( dt1.getZone() ) );
            return ld1.equals( ld2 );
        }
    }

}
