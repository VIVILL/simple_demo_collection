package com.example.administrator.reboot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmMainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "AlarmMainActivity";
    private TextView switch_in;
    private TextView mInTime;
    private RelativeLayout setInTime;
    private AlarmsSetting alarmsSetting;
    private LineGridView inGridview;
    private WeekGridAdpter inGridAdapter;
    private Button mSaveSettingButton;
    private Button mRebootTestButton;
    private TextView mNextRebootTimeTextView;
    private int selectedDatePosition;

    private RelativeLayout mCycleRelativeLayout;
    private Spinner mCycleSpinner;
    private boolean mIsSettingEffected;//是否生效过一次  默认false  生效一次后，ture    ，每次save setting后置false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_alarm_main);
        initView();
    }

    private void initView() {
        alarmsSetting = new AlarmsSetting(this);
        switch_in = (TextView)findViewById(R.id.switch_in);
        switch_in.setOnClickListener(this);
        switch_in.setSelected(alarmsSetting.isInEnble()?true:false);


        setInTime = (RelativeLayout)findViewById(R.id.set_in_time);
        setInTime.setOnClickListener(this);

        mInTime = (TextView)findViewById(R.id.alarm_in_set_time);
        setTime(alarmsSetting.getInHour(), alarmsSetting.getInMinutes(), AlarmsSetting.ALARM_SETTING_TYPE_IN);

        inGridview = (LineGridView) findViewById(R.id.in_gridview);
        inGridAdapter = new WeekGridAdpter(this,alarmsSetting,AlarmsSetting.ALARM_SETTING_TYPE_IN);
        Log.d(TAG,"getInPosition is : " + alarmsSetting.getInPosition());
        selectedDatePosition = alarmsSetting.getInPosition();
        inGridAdapter.setSelection(alarmsSetting.getInPosition());
        inGridview.setAdapter(inGridAdapter);
        inGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.d(TAG,"position is : " + position);
                selectedDatePosition = position;
                alarmsSetting.setInPosition(selectedDatePosition);
                Log.d(TAG,"setInPosition is : " + selectedDatePosition);

                inGridAdapter.setSelection(position); //传值更新
                inGridAdapter.notifyDataSetChanged(); //每一次点击通知adapter重新渲染
            }
        });



        mNextRebootTimeTextView = (TextView) findViewById(R.id.next_reboot_time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分");
        AlarmOpreation.enableAlert(AlarmMainActivity.this, AlarmsSetting.ALARM_SETTING_TYPE_IN, alarmsSetting);

        if (alarmsSetting.getNextAlarm()<System.currentTimeMillis()){
            mNextRebootTimeTextView.setText("请配置重启时间（请确保1、重启开关已打开2、时间和日期已选择）" + "    当前设置重启时间 ：" +formatter.format(new Date(alarmsSetting.getNextAlarm())) );
        }else {
            mNextRebootTimeTextView.setText("下次重启时间：" + formatter.format(new Date(alarmsSetting.getNextAlarm())));
        }
        mSaveSettingButton = (Button) findViewById(R.id.save_setting_button);
        mSaveSettingButton.setOnClickListener(this);
        mRebootTestButton = (Button) findViewById(R.id.reboot_test_button);
        mRebootTestButton.setOnClickListener(this);

        mCycleRelativeLayout = (RelativeLayout) findViewById(R.id.alarm_cycle_rl);
        if (alarmsSetting.getInCycle() ==0){
            alarmsSetting.setInCycle(1);
        }

        mCycleSpinner = (Spinner)   findViewById(R.id.alarm_cycle_spinner);
        mCycleSpinner.setSelection( alarmsSetting.getInCycle()-1,true);
        mCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] alarmCycle =getResources().getStringArray(R.array.cycle);
                int number= Integer.parseInt(alarmCycle[position]);
                Log.d(TAG,"cycle num is : " + number + " , position is : " + position);
                alarmsSetting.setInCycle(number);//存储cylce

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }





    private int mHour;
    private int mMinute;

    public void setTime(int hour,int minute,int type){
        mHour = hour;
        mMinute = minute;
        String mHour =""+hour;
        String mMinute =""+minute;
        if(hour/10==0) mHour = "0"+mHour;
        if(minute/10==0) mMinute = "0"+mMinute;
        if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN)
             mInTime.setText(mHour+":" + mMinute);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_in:
                if(v.isSelected()) {
                    alarmsSetting.setInEnble(false);
                    v.setSelected(false);
                    AlarmOpreation.cancelAlert(AlarmMainActivity.this, AlarmsSetting.ALARM_SETTING_TYPE_IN);
                }else {
                    alarmsSetting.setInEnble(true);
                    v.setSelected(true);
                    AlarmOpreation.enableAlert(AlarmMainActivity.this, AlarmsSetting.ALARM_SETTING_TYPE_IN, alarmsSetting);
                }
                break;

            case R.id.set_in_time:
                showTimePickerDialog(AlarmsSetting.ALARM_SETTING_TYPE_IN);
                break;

            case R.id.save_setting_button:
                alarmsSetting.setSettingEffected(false);
                alarmsSetting.setInHour(mHour);
                alarmsSetting.setInMinutes(mMinute);
                selectedDatePosition = alarmsSetting.getInPosition();
                switch (selectedDatePosition){
                    case 0:
                        alarmsSetting.setInDays(1);
                        break;
                    case 1:
                        alarmsSetting.setInDays(2);
                        break;
                    case 2:
                        alarmsSetting.setInDays(3);
                        break;
                    case 3:
                        alarmsSetting.setInDays(4);
                        break;
                    case 4:
                        alarmsSetting.setInDays(5);
                        break;
                    case 5:
                        alarmsSetting.setInDays(6);
                        break;
                    case 6:
                        alarmsSetting.setInDays(7);
                        break;
                }



                AlarmOpreation.cancelAlert(AlarmMainActivity.this,AlarmsSetting.ALARM_SETTING_TYPE_IN);
                AlarmOpreation.enableAlert(AlarmMainActivity.this,AlarmsSetting.ALARM_SETTING_TYPE_IN,alarmsSetting);
                //下次重启时间
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分");
                Log.d(TAG, "alarmsSetting.getNextAlarm()" + formatter.format(new Date(alarmsSetting.getNextAlarm())));
                if (alarmsSetting.getNextAlarm()<System.currentTimeMillis()){
                    mNextRebootTimeTextView.setText("请配置重启时间（请确保1、重启开关已打开2、时间和日期已选择）" + "   当前设置重启时间 ：" +formatter.format(new Date(alarmsSetting.getNextAlarm())) );
                }else {
                    mNextRebootTimeTextView.setText("下次重启时间：" + formatter.format(new Date(alarmsSetting.getNextAlarm())));
                }
                break;
            case R.id.reboot_test_button:
                reboot();
                break;
        }
    }

    public void showTimePickerDialog(final int type){
        TimePickerFragment  timePicker = new TimePickerFragment();
        if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN) {
            timePicker.setTime(alarmsSetting.getInHour(),alarmsSetting.getInMinutes());
        }else{
            timePicker.setTime(alarmsSetting.getOutHour(), alarmsSetting.getOutMinutes());
        }
        timePicker.show(getFragmentManager(),"timePicker" );
        timePicker.setOnSelectListener(new TimePickerFragment.OnSelectListener() {
            @Override
            public void getValue(int hourOfDay, int minute) {
                setTime(hourOfDay, minute, type);
                AlarmOpreation.cancelAlert(AlarmMainActivity.this,type);
                AlarmOpreation.enableAlert(AlarmMainActivity.this,type,alarmsSetting);
            }
        });
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
