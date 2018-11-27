package com.demo.tmp_double_camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.USBCamera.USBCamCtrl;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    ImageView iv1,iv2;
    ImageButton ibSettings;
    //    public static final int DEFAULT_WIDTH = 640;
//    public static final int DEFAULT_HEIGHT = 480;
    public static final int DEFAULT_WIDTH = 480;
    public static final int DEFAULT_HEIGHT = 640;
    private static Bitmap bmp_ir = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);//ARGB_8888);
    private static Bitmap bmp_light = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);//ARGB_8888);
    //用于保存将yuv数据转成argb数据
    private byte[] graybuffer = new byte[DEFAULT_WIDTH * DEFAULT_HEIGHT * 4];
    private byte[] rgbbuffer = new byte[DEFAULT_WIDTH * DEFAULT_HEIGHT * 4];
    private CameraUtil cameraUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("lixm","onCreate() ...");

        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        ibSettings = (ImageButton) findViewById(R.id.setting);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
            }
        });

        cameraUtil = new CameraUtil();
        if (CameraUtil.isVideo0ColorCamera()) {
            cameraUtil.setLightIndex(Camera.CameraInfo.CAMERA_FACING_BACK);
            cameraUtil.setInfraredIndex(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            cameraUtil.setLightIndex(Camera.CameraInfo.CAMERA_FACING_FRONT);
            cameraUtil.setInfraredIndex(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        cameraUtil.setLightPreviewCallback(previewCallback1);
        cameraUtil.setInfraredPreviewCallback(previewCallback2);
        cameraUtil.openCamera(DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

    private Camera.PreviewCallback previewCallback1 = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            //Log.d("lixm","previewCallback1 , camera = " + camera + ", data = " + data);
            USBCamCtrl.UVCYuvtoRgb(DEFAULT_WIDTH, DEFAULT_HEIGHT, data, rgbbuffer);
            ByteBuffer buffer = ByteBuffer.wrap(rgbbuffer);
            bmp_light.copyPixelsFromBuffer(buffer);
            iv1.setImageBitmap(bmp_light);
        }
    };

    private Camera.PreviewCallback previewCallback2 = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            USBCamCtrl.UVCYuvtoRgb(DEFAULT_WIDTH, DEFAULT_HEIGHT, data, graybuffer);
            ByteBuffer buffer = ByteBuffer.wrap(graybuffer);
            bmp_ir.copyPixelsFromBuffer(buffer);
            iv2.setImageBitmap(bmp_ir);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lixm","onDestroy() ...");
        if(cameraUtil != null){
            cameraUtil.stopCamera();
        }
    }
}
