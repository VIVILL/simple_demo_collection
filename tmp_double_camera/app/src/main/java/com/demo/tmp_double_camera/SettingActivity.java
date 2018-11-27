package com.demo.tmp_double_camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class SettingActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void onBackPressed() {
        Log.d("lixm","onBackPressed() ...");
        try {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

           // Process.killProcess(Process.myPid());
        } catch (Exception e) {
            Log.d("lixm","onBackPressed(), Exception : ",e);
        } finally {
            //System.exit(0);
        }
        super.onBackPressed();
    }
}
