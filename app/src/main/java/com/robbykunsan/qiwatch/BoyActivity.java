package com.robbykunsan.qiwatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.graphics.Color;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;

/**
 * Created by Robbykunsan on 2014/10/18.
 */
public class BoyActivity extends Activity{
    public static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice mDevice;
    private BroadcastReceiver bReceiver;
    ReceiveThread thread;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String TAG = "Client";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boy);
        Button income_btn = (Button)findViewById(R.id.income);
        setBtnListner(income_btn, R.id.income);
        Button money_btn = (Button)findViewById(R.id.money);
        setBtnListner(money_btn, R.id.money);
        Button educ_btn = (Button)findViewById(R.id.educ);
        setBtnListner(educ_btn, R.id.educ);
        setReceiver();
    }

    private void setBtnListner(Button btn, final int btn_id) {
        btn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setSelected(btn_id);
                        resetReceiver();
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

    private void setReceiver(){
        Log.d(TAG, "set receiver");
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Receive intent");
                String action = intent.getAction();

                //デバイスを見つけたら
                if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Log.i(TAG, "Detect bluetooth");
                    BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 可能ならQIWatchであるかどうか確かめたいけど
                    if (thread != null) {
                        thread.cancel();
                    }
                    thread = new ReceiveThread(mDevice);
                    thread.start();
                    Intent i = new Intent(BoyActivity.this, com.robbykunsan.qiwatch.ModeActivity.class);
                    startActivity(i);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private void resetReceiver() {
        if (bReceiver != null) {
            unregisterReceiver(bReceiver);
        }
        if (thread != null) {
            thread.cancel();
        }
        setReceiver();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(bReceiver);
        if (thread != null) {
            thread.cancel();
        }
    }

    private class ReceiveThread extends Thread {
        private final BluetoothSocket clientSocket;

        public ReceiveThread(BluetoothDevice device){
            Log.i(TAG, "create thread");
            BluetoothSocket tmpSock = null;

            try{
                tmpSock = device.createRfcommSocketToServiceRecord(TECHBOOSTER_BTSAMPLE_UUID);
            }catch(IOException e){
                e.printStackTrace();
            }
            clientSocket = tmpSock;
        }

        public void run(){
            Log.i(TAG, "run thread");
            try{
                //サーバー側に接続要求
                Log.i(TAG, "send connect request");
                clientSocket.connect();
            }catch(IOException e){
                Log.e(TAG, "failed to connect");
                try {
                    clientSocket.close();
                } catch (IOException closeException) {
                    e.printStackTrace();
                }
                return;
            }

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //接続完了時の処理
            //ReadWriteModel rw = new ReadWriteModel(mContext, clientSocket, myNumber);
            //rw.start();
        }

        public void cancel() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
