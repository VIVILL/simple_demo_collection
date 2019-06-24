/*
 *  autor：OrandNot
 *  email：orandnot@qq.com
 *  time: 2016 - 1 - 14
 *
 */

package com.example.administrator.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver  extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private AlarmsSetting alarmsSetting;
    @Override
    public void onReceive(Context context, Intent intent) {
        alarmsSetting = new AlarmsSetting(context);
        int type = intent.getIntExtra("type",0);
        Log.e("#######################", "getRecevier_ACtion" + intent.getAction());

        //如果已经设置闹钟w不可用，先拦截
        if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN && !alarmsSetting.isInEnble()){
            return ;
        }else if(type==AlarmsSetting.ALARM_SETTING_TYPE_OUT && !alarmsSetting.isOutEnble()){
            return;
        }

        if(intent.getAction().equals(AlarmsSetting.ALARM_ALERT_ACTION) && type !=0) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分ss秒SSS毫秒");
            Log.e("###########此次闹钟#######", "alarmsSetting.getNextAlarm()" + formatter.format(new Date(alarmsSetting.getNextAlarm())));
            Log.e("###########当前系统时间###", "System.currentTimeMillis()" + formatter.format(new Date(System.currentTimeMillis())));


            alarmsSetting.setSettingEffected(true);
            Log.d(TAG,"alarmsSetting setSettingEffected true ");
            alarmsSetting.setRebootTime(System.currentTimeMillis());
            Log.e(TAG, "alarmsSetting getRebootTime() : " + formatter.format(new Date(alarmsSetting.getRebootTime())));
            reboot();

        }else{
            AlarmOpreation.cancelAlert(context,  AlarmsSetting.ALARM_SETTING_TYPE_IN);
            AlarmOpreation.enableAlert(context,  AlarmsSetting.ALARM_SETTING_TYPE_IN, new AlarmsSetting(context));

        }

    }


    private void reboot(){
            Log.d(TAG,"reboot Android  ");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");//24小时制
            Date date = new Date();
            date.setTime(System.currentTimeMillis());
            Log.d(TAG,"reboot Time is : "  + simpleDateFormat.format(date));
            try {
                Log.d(TAG,"before su");
                Process proc = Runtime.getRuntime().exec(new String[]{"reboot"}); // "su","-c",
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
