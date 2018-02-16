package edu.mit.media.mysnapshot.activities.questions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jawbone.upplatformsdk.api.response.OauthAccessTokenResponse;
import com.jawbone.upplatformsdk.oauth.OauthUtils;
import com.jawbone.upplatformsdk.oauth.OauthWebViewActivity;
import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

public abstract class JawboneQuestionActivity extends QuestionActivity {

    private static final String TAG = JawboneQuestionActivity.class.getSimpleName();


    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";

    private static final int OAUTH_REQUEST_CODE = 25;

    private static final String OAUTH_CALLBACK_URL = "http://localhost/helloup?";

    protected List<UpPlatformSdkConstants.UpPlatformAuthScope> authScope;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set required levels of permissions here, for demonstration purpose
        // we are requesting all permissions
        authScope  = new ArrayList<UpPlatformSdkConstants.UpPlatformAuthScope>();
        authScope.add(UpPlatformSdkConstants.UpPlatformAuthScope.ALL);

    }

    private Intent getIntentForWebView() {
        Uri.Builder builder = OauthUtils.setOauthParameters(CLIENT_ID, OAUTH_CALLBACK_URL, authScope);

        Intent intent = new Intent(OauthWebViewActivity.class.getName());
        intent.putExtra(UpPlatformSdkConstants.AUTH_URI, builder.build());
        return intent;
    }

    public void authJawbone() {
        Intent intent = getIntentForWebView();
        startActivityForResult(intent, OAUTH_REQUEST_CODE);
    }


    private Callback accessTokenRequestListener = new Callback<OauthAccessTokenResponse>() {

        @Override
        public void onResponse(Response<OauthAccessTokenResponse> response, Retrofit retrofit) {
            OauthAccessTokenResponse result = response.body();
            if (result.access_token != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JawboneQuestionActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(UpPlatformSdkConstants.UP_PLATFORM_ACCESS_TOKEN, result.access_token);
                editor.putString(UpPlatformSdkConstants.UP_PLATFORM_REFRESH_TOKEN, result.refresh_token);
                editor.commit();

                Log.e(TAG, "accessToken:" + result.access_token);

                onJawboneAuth(result.access_token, result.refresh_token);

            } else {
                Log.e(TAG, "accessToken not returned by Oauth call, exiting...");
            }
        }

        @Override
        public void onFailure(Throwable retrofitError) {
            Log.e(TAG, "failed to get accessToken:" + retrofitError.getMessage());
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == OAUTH_REQUEST_CODE) {
                String code = data.getStringExtra(UpPlatformSdkConstants.ACCESS_CODE);
                if (code != null) {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://jawbone.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RestApiInterface rest = retrofit.create(RestApiInterface.class);

                    rest.getAccessToken(
                            CLIENT_ID,
                            CLIENT_SECRET,
                            code).enqueue(accessTokenRequestListener);
                }
            }
        }

    }

    public interface RestApiInterface {
        @GET("/auth/oauth2/token?grant_type=authorization_code")
        Call<OauthAccessTokenResponse> getAccessToken(@Query("client_id") String var1, @Query("client_secret") String var2, @Query("code") String var3);
    }



    protected abstract void onJawboneAuth(String authToken, String resetToken);

    public static class JawboneAuthResult {
        public String accessToken, refreshToken;
    }

    public static JawboneAuthResult getAuthInfo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        JawboneAuthResult result = new JawboneAuthResult();

        result.accessToken = preferences.getString(UpPlatformSdkConstants.UP_PLATFORM_ACCESS_TOKEN, null);
        result.refreshToken = preferences.getString(UpPlatformSdkConstants.UP_PLATFORM_REFRESH_TOKEN, null);

        return result;
    }
}
