package com.nazeer.ratinglib;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.nazeer.ratingmanager.RatingManager;

public class MainActivity extends AppCompatActivity {
    RatingManager ratingManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ratingManager=new RatingManager(this,3);
        ratingManager.setRatingRunnable(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Do whatEver action you want And make sure you handle holding back your self ", Toast.LENGTH_SHORT).show();
                ratingManager.holdBack();
            }
        });
    }

    public void triggerRunnable(View view) {
        ratingManager.triggerRateEvent(RatingManager.RatingMode.USE_PROVIDED_RUNNABLE);
    }

    public void triggerDialog(View view) {
        ratingManager.triggerRateEvent(RatingManager.RatingMode.USE_DEFAULT_DIALOG);
    }

    public void reset(View view) {
       ratingManager.reset();
        Toast.makeText(MainActivity.this, "reset Success", Toast.LENGTH_SHORT).show();
    }

    public void forceShowRating(View view) {
        ratingManager.triggerRateEvent(RatingManager.RatingMode.USE_DEFAULT_DIALOG,true);
    }
}
