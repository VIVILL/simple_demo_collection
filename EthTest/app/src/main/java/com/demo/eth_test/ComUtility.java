package com.demo.eth_test;

import android.util.Log;

import java.io.DataOutputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by lei on 2017/8/21.
 */

public class ComUtility {
    private static final String TAG = "ComUtility";

    /** 执行控制台命令，参数为命令行字符串方式，申请Root控制权限*/
    public static boolean RootCommand(String command){
        Process process = null;
        DataOutputStream os = null;
        try{
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e){
            Log.e(TAG, "获取root权限失败： " + e.getMessage());
            return false;
        }
        Log.d(TAG, "获取root权限成功");
        return true;
    }
}
