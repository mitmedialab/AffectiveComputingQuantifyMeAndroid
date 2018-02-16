package edu.mit.media.mysnapshot;


import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.HttpSender;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import edu.mit.media.mysnapshot.backend.BackendAPIFactory;


public class MyApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        // LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        String baseURL = BackendAPIFactory.baseURL(base);

        String acraUser = base.getResources().getString(R.string.ACRA_USER);
        String acraPassword = base.getResources().getString(R.string.ACRA_PASSWORD);

        HashMap<String, String> headers = new HashMap<>();
        URL host = null;
        try {
            host = new URL(baseURL);
            headers.put("HTTP_HOST", host.getHost());
        } catch (MalformedURLException e) {
        }

        try {

            final ACRAConfiguration config = new ConfigurationBuilder(this)
                    .setFormUri(baseURL + "acra/report/")
                    .setReportType(HttpSender.Type.JSON)
                    .setFormUriBasicAuthLogin(acraUser)
                    .setFormUriBasicAuthPassword(acraPassword)
                    .setHttpHeaders(headers)
                    .build();

            // The following line triggers the initialization of ACRA
            ACRA.init(this, config);

        } catch (ACRAConfigurationException e) {
            e.printStackTrace();
        }

    }
}
