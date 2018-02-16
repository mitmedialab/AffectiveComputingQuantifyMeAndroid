package edu.mit.media.mysnapshot.backend;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.acra.ACRA;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import edu.mit.media.mysnapshot.R;
import okio.Buffer;
import okio.BufferedSource;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class BackendAPIFactory {

    public static final String LOGTAG = BackendAPIFactory.class.getName();


    static BackendAPI api = null;

    static String token;

    public static final String TOKEN_PREF_KEY = "edu.mit.media.mysnapshot.authtoken";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        return dateFormat;
    }

    public static String formatDate(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return getDateFormat().format(cal.getTime());
    }

    public static Calendar parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(getDateFormat().parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static BackendAPI getAPI(Context context) {
        if (api != null) {
            return api;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        token = prefs.getString(TOKEN_PREF_KEY, "");

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new AuthInterceptor(context));
        okHttpClient.interceptors().add(new AcraInterceptor(context));


        Gson gson = getGson();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        BackendAPI restService = retrofit.create(BackendAPI.class);

        api = restService;

        return api;

    }

    public static String baseURL(Context context) {
        return context.getResources().getString(R.string.BASE_URL);
    }

    public static Gson getGson() {

        Gson gson = Converters.registerDateTime(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).excludeFieldsWithoutExposeAnnotation().create();
        return gson;
    }

    public static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        } return account;
    }

    public static String getUUID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public static class AuthInterceptor implements Interceptor {

        Context context;

        public AuthInterceptor(Context context) {
            super();
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            Request authorizedRequest = request.newBuilder()
                .addHeader("X-APPKEY", BackendAPI.APP_KEY)
                .addHeader("Authorization", "Token " + token)
                .build();

            // try the request
            Response response = chain.proceed(authorizedRequest);

            if (response.code() == 401) {

                obtainToken();

                // create a new request and modify it accordingly using the new token
                Request newRequest = request.newBuilder()
                        .addHeader("X-APPKEY", BackendAPI.APP_KEY)
                        .addHeader("Authorization", "Token " + token)
                        .build();

                // retry the request
                return chain.proceed(newRequest);
            }

            // otherwise just pass the original response on
            return response;
        }

        public void obtainToken() throws IOException {
            Interceptor requestInterceptor;
            requestInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();

                    request = request.newBuilder()
                            .addHeader("X-APPKEY", BackendAPI.APP_KEY)
                            .build();

                    // try the request
                    Response response = chain.proceed(request);
                    return response;
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.interceptors().add(requestInterceptor);


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.BASE_URL))
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            BackendAPI restService = retrofit.create(BackendAPI.class);

            retrofit.Response<BackendAPI.AuthToken> tokenResponse = restService.obtainToken(getEmail(context), getUUID(context)).execute();

            if (tokenResponse.isSuccess()) {

                token = tokenResponse.body().token;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(TOKEN_PREF_KEY, token);
                editor.apply();

            }
        }

    }

    public static class AcraInterceptor implements Interceptor {

        Context context;

        public AcraInterceptor(Context context) {
            super();
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            Response response = null;
            try {
                response = chain.proceed(request);
            } catch (Exception error) {
                ACRA.getErrorReporter().handleSilentException(error);
            }

            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));

            boolean success = false;

            if (responseBodyString != null) {
                try {
                    BackendAPI.SuccessCheck result = getGson().fromJson(responseBodyString, BackendAPI.SuccessCheck.class);
                    success = result.success;
                } catch (Exception e) {
                    success = true;
                }
            }

            if ((response.code() != 401 && response.code() != 200) || ! success) {

                ACRA.getErrorReporter().handleSilentException(new Exception(response.toString()));
            }

            // otherwise just pass the original response on
            return response;
        }
    }


    }
