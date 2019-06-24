/*
 *  autor：OrandNot
 *  email：orandnot@qq.com
 *  time: 2016 - 1 - 14
 *
 */

package com.example.administrator.reboot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmOpreation {
    private static final String TAG = "AlarmOpreation";


    public static void cancelAlert(Context context, int type) {
//        Log.e("<<<<<<<<<<<<<<<<<", "cancelAlert");
        AlarmManager mAlarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmsSetting.ALARM_ALERT_ACTION);
        intent.putExtra("type", type);
        intent.setClass(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, type, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(pi);

    }

    public static void enableAlert(Context context, int type, AlarmsSetting alarmsSetting) {
//        Log.e("<<<<<<<<<<<<<<<<<", "enableAlert");
        if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN && !alarmsSetting.isInEnble()){
            return ;
        }else if(type==AlarmsSetting.ALARM_SETTING_TYPE_OUT && !alarmsSetting.isOutEnble()){
            return;
        }
        AlarmManager mAlarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        int hours = 0,minute=0,dayOfweek=0;
        if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN){
            hours = alarmsSetting.getInHour();
            minute=alarmsSetting.getInMinutes();
            dayOfweek = alarmsSetting.getInDays();

        }else if(type==AlarmsSetting.ALARM_SETTING_TYPE_OUT){
            hours = alarmsSetting.getOutHour();
            minute=alarmsSetting.getOutMinutes();
            dayOfweek=alarmsSetting.getOutDays();
        }
        Log.d(TAG, "before cacluteNextAlarm dayOfweek, hours , minute is : " + dayOfweek + " , " + hours +" , " + minute);
        //计算 下次重启的时间
       // Calendar mCalendar = cacluteNextAlarm(hours, minute, dayOfweek);
        Calendar mCalendar = cacluteNextAlarm(alarmsSetting);
        Log.d(TAG, "dayOfweek, hours , minute is : " + dayOfweek + " , " + hours +" , " + minute);
        //屏蔽 xx
        if (mCalendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Log.e("!!!!!!!!!!!","设置时间小于当前系统时间，在当前时间上加7天"+mCalendar.getTimeInMillis() + 7*24 *60*60*1000);
            Intent intent = new Intent(AlarmsSetting.ALARM_ALERT_ACTION);
            intent.putExtra("type", type);
            intent.setClass(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, type, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis() + 7*24 *60*60*1000, pi);
            alarmsSetting.setNextAlarm(mCalendar.getTimeInMillis() + 7*24 *60*60*1000);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分ss秒SSS毫秒");
            Log.e("###########闹钟时间#######", "alarmsSetting.getNextAlarm()" + formatter.format(new Date(alarmsSetting.getNextAlarm())));
            return;
        }
        Intent intent = new Intent(AlarmsSetting.ALARM_ALERT_ACTION);
        intent.putExtra("type", type);
        intent.setClass(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, type, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pi);
        alarmsSetting.setNextAlarm(mCalendar.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分ss秒SSS毫秒");
        Log.e("###########闹钟时间#######", "alarmsSetting.getNextAlarm()" + formatter.format(new Date(alarmsSetting.getNextAlarm())));
    }



   public static Calendar cacluteNextAlarm(AlarmsSetting alarmsSetting){
       //alarmsSetting
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.HOUR_OF_DAY,alarmsSetting.getInHour());
        mCalendar.set(Calendar.MINUTE, alarmsSetting.getInMinutes());

        //计算下一次重启时间  这里需要进行判断
       Log.d(TAG,"alarmsSetting isSettingEffected : " + alarmsSetting.isSettingEffected());
        if (alarmsSetting.isSettingEffected()){
            int differDays = getNextAlarmDifferDays(mCalendar,alarmsSetting,mCalendar.get(Calendar.DAY_OF_WEEK));
            int nextYear = getNextAlarmYear(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.DAY_OF_YEAR), mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR), differDays);
            int nextMonth = getNextAlarmMonth(mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.getActualMaximum(Calendar.DATE), differDays);
            int nextDay = getNextAlarmDay(mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.getActualMaximum(Calendar.DATE), differDays);
            Log.d(TAG,"differDays is : " + differDays + " ,nextYear is : " + nextYear + " ,nextMonth is : " + nextMonth
                    + " ,nextDay is : " + nextDay + " , hour is : " + alarmsSetting.getInHour() + " ,minute is : " +  alarmsSetting.getInMinutes());
            mCalendar.set(Calendar.YEAR,nextYear);
            mCalendar.set(Calendar.MONTH, nextMonth % 12);//月份从0开始
            mCalendar.set(Calendar.DAY_OF_MONTH, nextDay);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
        }else {
            //  将设置的时间存在Calendar中
            int differDays = getNextAlarmDifferDays(alarmsSetting.getInDays(),mCalendar.get(Calendar.DAY_OF_WEEK), mCalendar.getTimeInMillis());
            int nextYear = getNextAlarmYear(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.DAY_OF_YEAR), mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR), differDays);
            int nextMonth = getNextAlarmMonth(mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.getActualMaximum(Calendar.DATE), differDays);
            int nextDay = getNextAlarmDay(mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.getActualMaximum(Calendar.DATE), differDays);
            Log.d(TAG,"differDays is : " + differDays + " ,nextYear is : " + nextYear + " ,nextMonth is : " + nextMonth
                    + " ,nextDay is : " + nextDay + " , hour is : " + alarmsSetting.getInHour() + " ,minute is : " +  alarmsSetting.getInMinutes());
            mCalendar.set(Calendar.YEAR,nextYear);
            mCalendar.set(Calendar.MONTH, nextMonth % 12);//月份从0开始
            mCalendar.set(Calendar.DAY_OF_MONTH, nextDay);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
          //  alarmsSetting.setSettingEffected(true);
        }


       SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分ss秒SSS毫秒");
        Log.d(TAG,"mCalendar get YEAR" + mCalendar.get(Calendar.YEAR));
       Log.d(TAG,"###########Calendar时间#######" + formatter.format(new Date(mCalendar.getTimeInMillis())));
        return mCalendar;
    }

    public static String getDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"后7天==" + dft.format(endDate));
        return dft.format(endDate);
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        //同一年
        if(year1 != year2) {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        } else {//不同年
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }

    //获取下次闹钟相差的天数
    private static int getNextAlarmDifferDays(Calendar calendar,AlarmsSetting alarmsSetting, int currentDayOfWeek){
        // 需判断在此时间之前还是之后，然后对数据进行处理。
        // int nextDayOfWeek =  getNextDayOfWeek(data, currentDayOfWeek,timeInMills);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分ss秒SSS毫秒");
        Date  date2  = new Date(Calendar.getInstance().getTimeInMillis());//当前时间
        Log.d(TAG,"currentTimeMillis is : " + formatter.format(date2));
        Date  date  = new Date(alarmsSetting.getRebootTime());//上次有效重启时间
        Log.d(TAG,"getRebootTime is : " + formatter.format(date));
        int diffDay = differentDays(date,date2);
        Log.d(TAG,"当前日期据上次重启日期的差距： " + diffDay);
        int nextDayOfWeek = alarmsSetting.getInDays()  ;
        Log.d(TAG,"selected data day of week is : " + nextDayOfWeek + " , current system day of week is : " +currentDayOfWeek );
//        Log.d(TAG,"current day is : " + calendar.get(Calendar.DAY_OF_MONTH )+ " , current cost time is :");
        Log.d(TAG,"Current time is : " + System.currentTimeMillis() + " , alarmsSetting getRebootTime() : " + alarmsSetting.getRebootTime());
        //  currentTimeMillis - getRebootTime  -> calendar
      //  Log.d(TAG,"nextDayOfWeek is : " + (7*alarmsSetting.getInCycle()+nextDayOfWeek-currentDayOfWeek));// 14 + 5 -6
       // Log.d(TAG,"  b is ;" + (7*alarmsSetting.getInCycle()  - currentDayOfWeek + nextDayOfWeek));
        if(diffDay <= 7*alarmsSetting.getInCycle()){
            return  7*alarmsSetting.getInCycle() - diffDay;
        }else {
            return 7*(1+alarmsSetting.getInCycle()) - diffDay;
        }

      //  return currentDayOfWeek<=nextDayOfWeek?(7*alarmsSetting.getInCycle()+nextDayOfWeek-currentDayOfWeek):(7*alarmsSetting.getInCycle()  - currentDayOfWeek + nextDayOfWeek);
    //          return currentDayOfWeek<=nextDayOfWeek?(nextDayOfWeek-currentDayOfWeek):(7 - currentDayOfWeek + nextDayOfWeek);
    }


    //获取下次闹钟相差的天数
    private static int getNextAlarmDifferDays(int data, int currentDayOfWeek,long timeInMills){
        Log.d(TAG,"selected data day of week is : " + data + " , current system day of week is : " +currentDayOfWeek );
        // 需判断在此时间之前还是之后，然后对数据进行处理。
       // int nextDayOfWeek =  getNextDayOfWeek(data, currentDayOfWeek,timeInMills);
        int nextDayOfWeek = data ;
        Log.d(TAG,"nextDayOfWeek is : " + nextDayOfWeek);
        return currentDayOfWeek<=nextDayOfWeek?(nextDayOfWeek-currentDayOfWeek):(7 - currentDayOfWeek + nextDayOfWeek);
    }


    //考虑年进位的情况
    private static int getNextAlarmYear(int year,int dayOfYears, int actualMaximum, int differDays) {
        int temp = actualMaximum-dayOfYears-differDays;
        return temp >= 0?year:year+1;
    }

    //考虑月进位的情况
    private static int getNextAlarmMonth(int month,int dayOfMonth,int actualMaximum, int differDays) {
        int temp = actualMaximum-dayOfMonth-differDays;
        return temp >= 0?month:month+1;
    }

    //获取下次闹钟的day
    private static int getNextAlarmDay(int thisDayOfMonth, int actualMaximum, int differDays) {
        int temp = actualMaximum - thisDayOfMonth-differDays;
        if (temp<0){
            return thisDayOfMonth + differDays - actualMaximum;
        }
        return thisDayOfMonth + differDays;
    }

    //获取下次显示是星期几
    private static int getNextDayOfWeek(int data, int cWeek,long timeInMillis) {
        int tempBack = data >> cWeek - 1;
        int tempFront = data ;

        if(tempBack%2==1){
            if(System.currentTimeMillis()<timeInMillis)  return cWeek;
        }
        tempBack = tempBack>>1;
        int m=1,n=0;
        while (tempBack != 0) {
            if (tempBack % 2 == 1 ) return cWeek + m;
            tempBack = tempBack / 2;
            m++;
        }
        while(n<cWeek){
            if (tempFront % 2 == 1)  return n+1;
            tempFront =tempFront/2;
            n++;
        }
        return 0;
    }
}
