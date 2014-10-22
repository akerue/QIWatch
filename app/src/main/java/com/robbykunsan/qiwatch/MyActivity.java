package com.robbykunsan.qiwatch;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;

public class MyActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        //stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
        //    @Override
        //    public void onLayoutInflated(WatchViewStub stub) {
        //        mTextView = (TextView) stub.findViewById(R.id.text);
//            }
        //});
    }
    public void boyBtn_onClick(View view) {
        Intent i = new Intent(this, com.robbykunsan.qiwatch.BoyActivity.class);
        startActivity(i);
    }

    public void girlBtn_onClick(View view) {
        Intent i = new Intent(this, com.robbykunsan.qiwatch.GirlActivity.class);
        startActivity(i);
    }
}
