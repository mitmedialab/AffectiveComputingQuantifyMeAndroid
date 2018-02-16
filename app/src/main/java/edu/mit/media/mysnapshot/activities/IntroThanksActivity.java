package edu.mit.media.mysnapshot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.mit.media.mysnapshot.R;

public class IntroThanksActivity extends AppCompatActivity {

    public static final String LOGTAG = IntroThanksActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro_thanks);

        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroThanksActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}