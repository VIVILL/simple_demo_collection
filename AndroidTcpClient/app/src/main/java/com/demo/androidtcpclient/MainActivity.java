package com.demo.androidtcpclient;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final String TAG="MainActivity";
    private Timer  mQueryModuleTimer;
    private TimerTask mTimerTask;
    private boolean isTimerCmdSending;
    private EditText edit_ip;
    private EditText edit_port;
    private EditText edit_send;
    private EditText edit_recv;
    private Button btn_connect;
    private Button btn_send;
    private boolean isConnected = false;
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    private String line;

    private int NET_UN_CONNECTED = 0;
    private int NET_ETHERNET = 1;
    private int NET_WIFI = 2;
    private int NET_ETHER_WIFI = 3;


    private boolean isEthInserted;//网线连接状态
    private boolean isWifi;

    private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) || intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                int netWorkStatus = isNetworkAvailable();
                Log.d(TAG,"netWorkStatus is : " + netWorkStatus);
                switch (netWorkStatus) {
                    case 3:
                        Log.d(TAG, "有线无线同时使用");
                        isEthInserted = true;
                        isWifi = true;
                        Toast.makeText(MainActivity.this,"有线无线同时使用",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Log.d(TAG, "有线");
                        isEthInserted = true;
                        isWifi = false;
                        Log.d(TAG,"inner mNetworkStateReceiver， isEthDetected is : " + isEthInserted);
                        Toast.makeText(MainActivity.this,"有线",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Log.d(TAG,"无线");
                        isEthInserted = false;
                        isWifi = true;
                        Toast.makeText(MainActivity.this,"无线",Toast.LENGTH_SHORT).show();
                        //当有线断掉的时候
                        disconnect();
                        break;
                    case 0:
                        Log.d(TAG, "无网络");
                        isEthInserted = false;
                        isWifi = false;
                        disconnect();
                        Toast.makeText(MainActivity.this,"无网络",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private int isNetworkAvailable() {
        ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiNetInfo = connectMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (ethNetInfo != null && wifiNetInfo != null  && ethNetInfo.isConnected()  && wifiNetInfo.isConnected() ){
            Log.d(TAG,"return NET_ETHER_WIFI");
            return NET_ETHER_WIFI;
        } else if (ethNetInfo != null && ethNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            Log.d(TAG,"return NET_ETHERNET");
            return NET_ETHERNET;//有线
        } else if (wifiNetInfo != null && wifiNetInfo.isConnected() && !ethNetInfo.isConnected()) {
            Log.d(TAG,"return NET_WIFI");
            return NET_WIFI;//wifi
        } else {
            Log.d(TAG,"return NET_UN_CONNECTED");
            return NET_UN_CONNECTED;
        }
    }

    /* 定义Handler对象 */
    private Handler handler = new Handler() {
        @Override
        /* 当有消息发送出来的时候就执行Handler的这个方法 */
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
              /* 更新UI */
            Log.d(TAG,"isTimerCmd is : " + isTimerCmdSending);
            if (!isTimerCmdSending){
                // edit_recv.append(line);
                edit_recv.setText(line);
                //当收到返回的数据的时候重新开启定时器防止tcp连接中断
                startTimer();
            }
           /* 调试输出 */
            Log.d(TAG, "handleMessage line is : " + line);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
         /* 初始化 */
        edit_ip = (EditText) findViewById(R.id.edit_ip);
        edit_port = (EditText) findViewById(R.id.edit_port);
        edit_send = (EditText) findViewById((R.id.edit_msgsend));
        edit_recv = (EditText) findViewById(R.id.edit_recv);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_send = (Button)findViewById(R.id.btn_send);
        /* 连接按钮 */
        btn_connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /* 连接按钮处理函数 */
                connect();
            }
        });
        /* 发送按钮 */
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止timer
                stopTimer();

                sendCmd(edit_send.getText().toString());
                edit_send.setText("");
            }
        });
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            /* 关闭socket */
            if(null != socket){
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
            }
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }
        /* 更新UI */
        btn_connect.setText("连接");
        edit_recv.setText("");
        stopTimer();
        unregisterReceiver(mNetworkStateReceiver);
    }


    /* 连接按钮处理函数：建立Socket连接 */
    @SuppressLint("HandlerLeak")
    public void connect() {
        //首先检测网线是否插入
        Log.d(TAG,"inner connect ， isEthDetected is : " + isEthInserted);
        if (isEthInserted){
            if(false == isConnected){
                new Thread() {
                    public void run(){
                        String IPAdr = edit_ip.getText().toString();
                        int PORT = Integer.parseInt(edit_port.getText().toString());
                        Log.d(TAG,"IPAdr is : " + IPAdr + " , PORT is : " +  PORT);
                        try {
                        /* 建立socket */
                            Log.d(TAG,"before socket");
                            socket = new Socket(IPAdr, PORT);
                            Log.d(TAG,"after socket");

                        /* 输出流 */
                            Log.d(TAG,"before new BufferedWriter");

                            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            Log.d(TAG,"after new BufferedWriter");
                        /* 输入流 */
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        /* 调试输出 */
                            Log.d(TAG, "输入输出流获取成功");
                            Log.d(TAG, "检测数据");
                        /* 读数据并更新UI */
                            char[] buf = new char[2048];
                            int i;
                            while((i= reader.read(buf,0,100))!=-1) {
                                line = new String(buf,0,i);
                                Message msg = handler.obtainMessage();
                                msg.obj = line;
                                handler.sendMessage(msg);
                                Log.d(TAG,"line is : " + line +" , send to handler");
                            }
                        } catch (UnknownHostException e){
                            //  Toast.makeText(MainActivity.this,"无法建立连接：）",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"无法建立连接");
                            e.printStackTrace();
                            isConnected = false;
                        }
                        catch (IOException e) {
                            //   Toast.makeText(MainActivity.this,"无法建立连接：）",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"无法建立连接");
                            e.printStackTrace();
                            isConnected = false;
                        }
                    }
                }.start();
                isConnected = true;
            /* 更新UI */
                btn_connect.setText("断开");
                Toast.makeText(MainActivity.this,"连接成功：）",Toast.LENGTH_SHORT).show();
            }else{
                disconnect();
            }
        }else {
            Toast.makeText(MainActivity.this,"未检测到有线网",Toast.LENGTH_SHORT).show();
        }

    }



    private void disconnect(){
        isConnected = false;
            /* 更新UI */
        btn_connect.setText("连接");
        edit_send.setText("");
        edit_recv.setText("");
            /* 关闭socket */
        try {
            if(null != socket){
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
            }
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }
        /* 更新UI */
        btn_connect.setText("连接");
        edit_recv.setText("");
        Toast.makeText(MainActivity.this,"连接已断开：）",Toast.LENGTH_SHORT).show();
    }

    /* 发送按钮处理函数：向输出流写数据 */
    public void sendCmd(String string) {
        try {
            /* 向输出流写数据 */
            Log.d(TAG,"before write , send string is : " + string);
            if (writer != null){
                writer.write(string);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void startTimer(){
        if (mQueryModuleTimer != null || mTimerTask != null){
            stopTimer();
        }
        if (mQueryModuleTimer == null) {
            mQueryModuleTimer = new Timer();
        }
        if (mTimerTask == null){
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isConnected){
                        Log.d(TAG,"isConnected is : " + isConnected);
                        sendCmd("00");
                    }
                    Log.d(TAG,"isConnected is : " + isConnected);
                }
            };
        }
        if (mQueryModuleTimer != null && mTimerTask != null){
            mQueryModuleTimer.schedule(mTimerTask,0,1000);
        }
    }

    private void stopTimer() {
        isTimerCmdSending = false;
        if (mQueryModuleTimer != null) {
            mQueryModuleTimer.cancel();
            mQueryModuleTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

}
