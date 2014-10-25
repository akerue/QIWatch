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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Robbykunsan on 2014/10/18.
 */
public class GirlActivity extends Activity{
    private static final String TAG = "Receive";
    private static final String SERVICE_NAME = "BTHello";
    private static final String SERIAL_PORT_SERVICE_ID = "00001101-0000-1000-8000-00805F9B34FB";
    private static final UUID SERVICE_ID = UUID.fromString(SERIAL_PORT_SERVICE_ID);
    AcceptThread thread;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girl);
        Button ng_btn = (Button)findViewById(R.id.ng);
        setBtnListner(ng_btn, R.id.ng);
        Button money_btn = (Button)findViewById(R.id.treat);
        setBtnListner(money_btn, R.id.treat);
        Button educ_btn = (Button)findViewById(R.id.welcome);
        setBtnListner(educ_btn, R.id.welcome);
        Boolean setResult = useScanMode();
        if (setResult) {
            Log.d(TAG, "Success to set scan mode");
        } else {
            Log.e(TAG, "SHIT!!!!");
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (thread != null) {
            thread.cancel();
        }
    }

    private Boolean useScanMode() {
        Method[] methods = mBluetoothAdapter.getClass().getDeclaredMethods();
        for (Method method: methods) {
            if (method.getName().equals("setScanMode")) {
                try {
                    Boolean result = (Boolean) method.invoke(mBluetoothAdapter, new Object[]{new Integer(23), new Integer(3600)});
                    return result;
                } catch (IllegalArgumentException e) {
                    //呼び出し：引数が異なる
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    //呼び出し：アクセス違反、保護されている
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    //ターゲットとなるメソッド自身の例外処理
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void setBtnListner(Button btn, final int btn_id) {
        btn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setSelected(btn_id);
                        if (thread != null) {
                            thread.cancel();
                        }
                        thread = new AcceptThread();
                        thread.start();
                    }
                }
        );
    }

    private void setSelected(int id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int prev_id = sharedPreferences.getInt("ID", -1);
        Boolean boy_flag = sharedPreferences.getBoolean("boy_flag", true);
        if (prev_id != -1 && !boy_flag) {
            Button prev_btn = (Button) findViewById(prev_id);
            prev_btn.setBackgroundColor(Color.DKGRAY);
        }
        setStatus(id);
        Button btn = (Button)findViewById(id);
        btn.setBackgroundColor(Color.GRAY);
    }

    private void setStatus(int id) {
        Map<Integer, Integer> girlColors = new HashMap<Integer, Integer>();
        girlColors.put(R.id.ng, Color.BLUE);
        girlColors.put(R.id.treat, Color.YELLOW);
        girlColors.put(R.id.welcome, Color.RED);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GirlActivity.this);
        Editor editor = sharedPreferences.edit();

        editor.putBoolean("boy_flag", false);
        editor.putInt("ID", id);
        editor.putInt("Status", girlColors.get(id));
        editor.commit();
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;

        public AcceptThread() {
            Log.d(TAG, "Thread is created");
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_ID);
            } catch (IOException e) { }
        }

        public void run() {
            BluetoothSocket socket = null;
            Log.d(TAG, "Thread run");
            while (true) {
                try {
                    Log.d(TAG, "Waiting for connection request");
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Fail to accept.", e);
                    break;
                }
                Log.d(TAG, "A connection was accepted.");
                if (socket != null) {
                    connect(socket);
                    Intent i = new Intent(GirlActivity.this, com.robbykunsan.qiwatch.ModeActivity.class);
                    startActivity(i);
                    break;
                }
                Log.d(TAG, "The session was closed. Listen again.");
            }
            Log.d(TAG, "Thread is finished");
        }

        private void connect(BluetoothSocket socket) {

            try {
                Log.d(TAG, "Connection established.");
                socket.close();

            } catch (IOException e) {
                Log.e(TAG, "Something bad happened!", e);
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                serverSocket.close();
                Log.d(TAG, "The server socket is closed.");
            } catch (IOException e) { }
        }
    }
}
