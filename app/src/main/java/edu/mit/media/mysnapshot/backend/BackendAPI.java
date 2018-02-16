package edu.mit.media.mysnapshot.backend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface BackendAPI {

    String APP_KEY = "";

    class AuthToken {

        @Expose
        public String token;

        public AuthToken() {

        }

    }

    class SuccessCheck {
        public SuccessCheck() {

        }

        @Expose
        public boolean success;
    }

    @FormUrlEncoded
    @POST("/obtain_token/")
    Call<AuthToken> obtainToken(@Field("email") String email,
                                @Field("uuid") String uuid);




    class SettingsResult extends SuccessCheck {

        @Expose
        @SerializedName("had_user_data")
        public boolean hadUserData;

    }

    @FormUrlEncoded
    @POST("/set_user_data/")
    Call<SettingsResult> setBiographicData(
            @Field("jawbone_access") String jawboneAccess,
            @Field("jawbone_reset") String jawboneReset,
            @Field("date_of_birth") String dateOfBirth,
            @Field("race") String race,
            @Field("gender") String gender,
            @Field("happy") Integer happy,
            @Field("stress") Integer stress,
            @Field("activity") String activity,
            @Field("sleep_quality") Integer sleepQuality,
            @Field("timezone") String timezone
            );


    class CheckinResponse extends SuccessCheck {

        public static class ExperimentCompleteResult {

            public static class StageResult {
                @Expose
                int stage = -1;

                @Expose
                float target = -1, output = -1, min = -1, max = -1;

                @Expose
                List<Float> inputs = null, outputs = null;
            }

            @Expose
            @SerializedName("is_complete")
            public boolean isComplete = false;

            @Expose
            @SerializedName("result_value")
            public float resultValue = 0;

            @Expose
            @SerializedName("result_confidence")
            public float resultConfidence = 0;

            @Expose
            @SerializedName("stage_results")
            public List<StageResult> stageResults = new ArrayList<StageResult>();

        }

        public static class CheckinResult extends ExperimentCompleteResult {
            @Expose
            @SerializedName("new_stage")
            public boolean newStage = false;

            @Expose
            @SerializedName("ended_early")
            public boolean endedEarly = false;

            @Expose
            @SerializedName("restarted_stage")
            public boolean restartedStage = false;

            @Expose
            @SerializedName("current_stage")
            public int currentStage;

            @Expose
            @SerializedName("stage_inputs")
            public List<Float> stageInputs;

            @Expose
            @SerializedName("stage_outputs")
            public List<Float> stageOutputs;

            @Expose
            public Float target;

            @Expose
            public int day;
        }

        @Expose
        public String key;

        @Expose
        public CheckinResult result;
    }

    @FormUrlEncoded
    @POST("/start_experiment/")
    Call<CheckinResponse> startExperiment(
            @Field("type") String experimentType,
            @Field("self_efficacy") Integer selfEfficacy,
            @Field("app_efficacy") Integer appEfficacy,
            @Field("experiment_efficacy") Integer experimentEfficacy
    );

    @FormUrlEncoded
    @POST("/experiment_checkin/")
    Call<CheckinResponse> experimentCheckin(
            @Field("experiment_key") String experimentKey,
            @Field("did_follow_instructions") Integer didFollowInstructions,
            @Field("happy") Integer happy,
            @Field("stress") Integer stress,
            @Field("productivity") Integer productivity,
            @Field("leisure_time") Integer leisureTime,
            @Field("app_version") String appVersion
    );

    @GET("/refresh_instructions/")
    Call<CheckinResponse> refreshInstructions(
            @Query("experiment_key") String experimentKey
    );

    class ExperimentResults extends SuccessCheck {

        public static class ExperimentResult {
            @Expose
            public String type, key;

            @Expose
            public int days;

            @Expose
            @SerializedName("start_time")
            public DateTime startTime;

            @Expose
            @SerializedName("end_time")
            public DateTime endTime;

            @Expose
            @SerializedName("is_cancelled")
            public boolean isCancelled;

            @Expose
            @SerializedName("is_active")
            public boolean isActive;

            @Expose
            @SerializedName("result_value")
            public float resultValue;

            @Expose
            @SerializedName("result_confidence")
            public float resultConfidence;

        }

        @Expose
        public List<ExperimentResult> experiments;

    }


    @GET("/get_experiments/")
    Call<ExperimentResults> getExperiments();

    @FormUrlEncoded
    @POST("/cancel_experiment/")
    Call<ExperimentResults> cancelExperiment(
            @Field("experiment_key") String experimentKey,
            @Field("reason") String reason
    );

}
