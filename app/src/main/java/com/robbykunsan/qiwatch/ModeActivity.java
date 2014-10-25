package com.robbykunsan.qiwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.RelativeLayout;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Robbykunsan on 2014/10/18.
 */
public class ModeActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int color = sharedPreferences.getInt("Status", Color.BLACK);
        final RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.modeLayout);
        rl.setBackgroundColor(color);
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(10);
    }
}
