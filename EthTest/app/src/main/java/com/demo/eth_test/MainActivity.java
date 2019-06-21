package com.demo.eth_test;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int MESSAGE_RESULT = 0;
    private static final int MESSAGE_RESULT_IMAGE = 1;

    private String mGateWayString;
    private String mIpString;
    private Button mGateWayButton;
    private Button mOnButton;
    private Button mOffButton;
    private TextView mResultString;
    private ImageView mResultImageView;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGateWayString =  getGateWay(this);
        myHandler=new MyHandler(this);
        mResultString = findViewById(R.id.result_text_view);
        mResultImageView = findViewById(R.id.result_image_view);
        mGateWayButton = findViewById(R.id.gate_way_button);
        mOnButton = findViewById(R.id.eth0_up_button);
        mOffButton = findViewById(R.id.eth0_down_button);
        mGateWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultImageView.setVisibility(View.VISIBLE);
                mResultImageView.setImageDrawable(getResources().getDrawable(R.drawable.testing));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PingNetEntity pingNetEntity=new PingNetEntity(mGateWayString,3,5,new StringBuffer());
                        pingNetEntity = PingNet.ping(pingNetEntity);
                        Log.d(TAG,"Ip = "+ pingNetEntity.getIp());
                        Log.d(TAG,"Ping Time = "+ pingNetEntity.getPingTime());
                        Log.d(TAG,"Ping result "+ pingNetEntity.isResult()+"");
                        String resultString = "Ping Time = " +  pingNetEntity.getPingTime() + " Ping result = " + pingNetEntity.isResult();
                        Log.d(TAG,"resultString "+ resultString);
                        myHandler.obtainMessage(MESSAGE_RESULT,resultString).sendToTarget();

                        boolean resultBoolean = pingNetEntity.isResult();
                        myHandler.obtainMessage(MESSAGE_RESULT_IMAGE,resultBoolean).sendToTarget();

                    }
                }).start();
            }
        });
        mOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String up = "ifconfig eth0 up";
                boolean result = ComUtility.RootCommand(up);
                Log.d(TAG,"up result is "+ result);
                Toast.makeText(MainActivity.this,"up result is "+ result,Toast.LENGTH_SHORT).show();
                myHandler.obtainMessage(MESSAGE_RESULT,"以太网已开启 ,ip = " + getIp(MainActivity.this)).sendToTarget();
                mResultImageView.setVisibility(View.INVISIBLE);

            }
        });
        mOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String down = "ifconfig eth0 down";
                boolean result = ComUtility.RootCommand(down);
                Log.d(TAG,"down result is "+ result);
                Toast.makeText(MainActivity.this,"down result is " + result ,Toast.LENGTH_SHORT).show();
                myHandler.obtainMessage(MESSAGE_RESULT,"以太网已关闭").sendToTarget();
                mResultImageView.setVisibility(View.INVISIBLE);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }



    private static class MyHandler extends Handler {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private final WeakReference<MainActivity> mActivty;
        private MyHandler(MainActivity activity){
            mActivty =new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity=mActivty.get();
            super.handleMessage(msg);
            if(activity!=null){
                //执行业务逻辑
                activity.upDateUI(msg);
            }
        }
    }

    public void upDateUI(Message msg){
        switch (msg.what){
            case MESSAGE_RESULT:
                String resultString = (String) msg.obj;
                mResultString.setText("测试结果：" + resultString);
                break;
            case MESSAGE_RESULT_IMAGE:
                boolean resultBoolean = (boolean) msg.obj;
                if (resultBoolean){
                    mResultImageView.setImageDrawable(getResources().getDrawable(R.drawable.ctid_success));
                }else {
                    mResultImageView.setImageDrawable(getResources().getDrawable(R.drawable.ctid_fail));

                }

                break;
            default:
                break;
        }
    }


    //获取ip
    public String getIp(Context context){
        String ipString = "";
        try {
            String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);
            Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");
            Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);
            Field mService = ethernetManagerClass.getDeclaredField("mService");
            // 设置访问权限
            mService.setAccessible(true);
            Object mServiceObject = mService.get(ethernetManager);
            Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");
            Method[] methods = iEthernetManagerClass.getDeclaredMethods();
            for (Method ms : methods) {
                String methodName = ms.getName();
                if("getIpAddress".equals(methodName)){  // IP地址
                    String ipAddr = (String)ms.invoke(mServiceObject);
                    ipString = ipAddr;
                    Log.d(TAG,"ipString = " + ipString);

                }
            }
        }catch (Exception e) {
            Log.d(TAG, "Exception : ",e);
        }
        return ipString;

    }



    //获取网关
    public String getGateWay(Context context){
        String gateWay = "";
        try {
            String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);
            Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");
            Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);
            Field mService = ethernetManagerClass.getDeclaredField("mService");
            // 设置访问权限
            mService.setAccessible(true);
            Object mServiceObject = mService.get(ethernetManager);
            Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");
            Method[] methods = iEthernetManagerClass.getDeclaredMethods();
            for (Method ms : methods) {
                String methodName = ms.getName();
                if("getGateway".equals(methodName)){   // 网关
                    String gate = (String)ms.invoke(mServiceObject);
                    gateWay = gate;
                    Log.d(TAG,"gateWay = " + gate);

                }
            }
        }catch (Exception e) {
            Log.d(TAG, "Exception : ",e);
        }
        return gateWay;

    }


        //  ifconfig eth0 down
    // ifconfig eth0 up



    /**
     * 获取静态IP的相关信息
     * @param context
     * @return
     */
    public Map<String,String> getIps(Context context){
        Map<String,String> ipMaps = new HashMap<String,String>();
        try {
            String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);
            Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");
            Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);
            Field mService = ethernetManagerClass.getDeclaredField("mService");
            // 设置访问权限
            mService.setAccessible(true);
            Object mServiceObject = mService.get(ethernetManager);
            Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");
            Method[] methods = iEthernetManagerClass.getDeclaredMethods();
            for (Method ms : methods) {
                String methodName = ms.getName();
                if("getGateway".equals(methodName)){   // 网关
                    String gate = (String)ms.invoke(mServiceObject);
                    Log.d(TAG,"gate = " + gate);
                    ipMaps.put("gateWay",gate);
                }else if("getNetmask".equals(methodName)){  // 子网掩码
                    String mask = (String)ms.invoke(mServiceObject);
                    ipMaps.put("maskAddress",mask);
                }else if("getIpAddress".equals(methodName)){  // IP地址
                    String ipAddr = (String)ms.invoke(mServiceObject);
                    ipMaps.put("ipAddress",ipAddr);
                }else if("getDns".equals(methodName)){  // DNS(注意解析)
                    String dnss = (String)ms.invoke(mServiceObject);
                    String []arrDns = dnss.split("\\,");
                    String dns = null;
                    if(arrDns != null){
                        dns = arrDns[0];
                        ipMaps.put("dns",dns);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception : ",e);
        }
        return ipMaps;
    }

}
