package com.robbykunsan.qiwatch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robbykunsan on 2014/10/18.
 */
public class BoyActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boy);
        Button income_btn = (Button)findViewById(R.id.income);
        income_btn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setSelected(R.id.income);
                    }
                }
        );

        Button money_btn = (Button)findViewById(R.id.money);
        money_btn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setSelected(R.id.money);
                    }
                }
        );

        Button educ_btn = (Button)findViewById(R.id.educ);
        educ_btn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setSelected(R.id.educ);
                    }
                }
        );
    }

    private void setSelected(int id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int prev_id = sharedPreferences.getInt("ID", -1);
        Boolean boy_flag = sharedPreferences.getBoolean("boy_flag", true);
        if (prev_id != -1 && boy_flag) {
            Button prev_btn = (Button) findViewById(prev_id);
            prev_btn.setBackgroundColor(Color.DKGRAY);
        }
        setStatus(id);
        Button btn = (Button)findViewById(id);
        btn.setBackgroundColor(Color.GRAY);
       // Intent i = new Intent(BoyActivity.this, com.robbykunsan.qiwatch.ModeActivity.class);
 //       startActivity(i);
    }

    private void setStatus(int id) {
        Map<Integer, Integer> boyColors = new HashMap<Integer, Integer>();
        boyColors.put(R.id.money, Color.RED);
        boyColors.put(R.id.income, Color.YELLOW);
        boyColors.put(R.id.educ, Color.GREEN);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sharedPreferences.edit();

        editor.putBoolean("boy_flag", true);
        editor.putInt("ID", id);
        editor.putInt("Status", boyColors.get(id));
        editor.commit();
    }
}
