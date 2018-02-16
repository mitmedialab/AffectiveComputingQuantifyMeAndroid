package edu.mit.media.mysnapshot.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.mit.media.mysnapshot.R;

public class IntroActivity extends AppCompatActivity {

    public static final String LOGTAG = IntroActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        checkPermissions();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 56;

    void checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                android.support.v4.app.ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {

                // No explanation needed, we can request the permission.

                android.support.v4.app.ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                checkPermissions();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}