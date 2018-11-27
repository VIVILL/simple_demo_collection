package com.demo.tmp_double_camera;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 相机工具类，使用SDK的方法打开相机
 *
 * @author lixm
 */
public class CameraUtil implements SurfaceHolder.Callback {
    private static final String TAG = "CameraUtil";
    public static int WIDTH = 640; // 要保存图片的分辨率
    public static int HEIGHT = 480;

    private Camera lightCamera;
    private Camera infraredCamera;
    private int lightIndex;
    private int infraredIndex;
    private PreviewCallback lightPreviewCallback;
    private PreviewCallback infraredPreviewCallback;
    private SurfaceView lightSurfaceView;
    private SurfaceView infraredSurfaceView;

    public void setLightPreviewCallback(PreviewCallback lightPreviewCallback) {
        this.lightPreviewCallback = lightPreviewCallback;
    }

    public void setInfraredPreviewCallback(PreviewCallback infraredPreviewCallback) {
        this.infraredPreviewCallback = infraredPreviewCallback;
    }

    public void setLightSurfaceView(SurfaceView lightSurfaceView) {
        this.lightSurfaceView = lightSurfaceView;
        this.lightSurfaceView.getHolder().addCallback(this);
    }

    public void setInfraredSurfaceView(SurfaceView infraredSurfaceView) {
        this.infraredSurfaceView = infraredSurfaceView;
    }


    public void setLightIndex(int lightIndex) {
        this.lightIndex = lightIndex;
    }

    public void setInfraredIndex(int infraredIndex) {
        this.infraredIndex = infraredIndex;
    }

    private void openLightCamera() {
        try {//此try是为了捕获setCamParam()的异常
            if (lightCamera == null) {
                int cameraCount = Camera.getNumberOfCameras();
                Log.d(TAG, "cameraCount = " + cameraCount);
                if (cameraCount > lightIndex) {
                    lightCamera = Camera.open(lightIndex);
                    Log.d(TAG, "CAMERA_FACING,index = " + lightIndex);
                } else {
                    Log.d(TAG, "no camera");
                }
                if (lightCamera != null) {
                    setCamParam(WIDTH, HEIGHT, lightCamera);
                }
                if (lightCamera != null) {
                    if (lightPreviewCallback != null) {
                        lightCamera.setPreviewCallback(lightPreviewCallback);
                    }
//                    try {
//                        lightCamera.setPreviewDisplay(lightSurfaceView.getHolder());
//                    } catch (IOException e) {
//                        Log.d(TAG, "surfaceCreated IOException = ", e);
//                    }
                    lightCamera.startPreview();
                }
            }
        } catch (Exception e) {
//            ToastUtils.showLong("openLightCamera error");
            e.printStackTrace();
            throw e;
        }
    }

    private void openInfraredCamera() {
        try {//此try是为了捕获setCamParam()的异常
            if (infraredCamera == null) {
                int cameraCount = Camera.getNumberOfCameras();
                Log.d(TAG, "cameraCount = " + cameraCount);
                if (cameraCount > infraredIndex) {
                    infraredCamera = Camera.open(infraredIndex);
                    Log.d(TAG, "CAMERA_FACING,index = " + infraredIndex);
                } else {
                    Log.d(TAG, "no camera");
                }
                if (infraredCamera != null) {
                    setCamParam(WIDTH, HEIGHT, infraredCamera);
                }
                if (infraredCamera != null) {
                    if (infraredPreviewCallback != null) {
                        infraredCamera.setPreviewCallback(infraredPreviewCallback);
                    }
//                    try {
//                        infraredCamera.setPreviewDisplay(infraredSurfaceView.getHolder());
//                    } catch (IOException e) {
//                        Log.d(TAG, "surfaceCreated IOException = ", e);
//                    }
                    infraredCamera.startPreview();
                }
            }
        } catch (Exception e) {
//            ToastUtils.showLong("openInfraredCamera error");
            e.printStackTrace();
            throw e;
        }
    }


    public void openCamera(int width,int heigth) {
        WIDTH = width;
        HEIGHT = heigth;
        try {
            openLightCamera();
            openInfraredCamera();
        } catch (Exception e) {
            e.printStackTrace();
//            retry();
        }
    }


    public void stopCamera() {
        if (lightCamera != null) {
            Log.d(TAG, "stopCamera: lightCamera");
//            lightCamera.stopPreview();
            lightCamera.setPreviewCallback(null);
            lightCamera.release();
            lightCamera = null;
        }
        if (infraredCamera != null) {
            Log.d(TAG, "stopCamera: infraredCamera");
//            infraredCamera.stopPreview();
            infraredCamera.setPreviewCallback(null);
            infraredCamera.release();
            infraredCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated holder = " + holder);
        //openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG, "surfaceChanged  ");
        if (lightCamera != null) {
            try {
                lightCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed  ");
    }


    private void setCamParam(int width, int height, Camera camera_) {
        Log.d(TAG, "setCamParam , width = " + width + ", height " + height);
        /* 获取摄像头支持的预览、保存参数 */
        try {
            Camera.Parameters paramsBefore = camera_.getParameters();
            if (paramsBefore == null) {
                return;
            }
            String strPrevSizesVals = paramsBefore.get("picture-size-values");
            int[] pictureSize = new int[2];
            if (strPrevSizesVals != null) {
                getSuitableWidth(strPrevSizesVals, width, height, pictureSize);
            }
            Log.d(TAG, "setPictureSize pictureSize[0] = " + pictureSize[0] + ", pictureSize[1] = " + pictureSize[1]);
            paramsBefore.setPictureSize(pictureSize[0], pictureSize[1]);

            /* 设置预览尺寸大小 */
            strPrevSizesVals = paramsBefore.get("preview-size-values");
            if (strPrevSizesVals != null) {
                getSuitableWidth(strPrevSizesVals, width, height, pictureSize);
            }
            Log.d(TAG, "setPreviewSize pictureSize[0] = " + pictureSize[0] + ", pictureSize[1] = " + pictureSize[1]);
            paramsBefore.setPreviewSize(pictureSize[0], pictureSize[1]);
            camera_.setParameters(paramsBefore);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "surfacecreate:setCamParam:error ");
            throw e;
        }
    }

    /**
     * 寻找合适的照片大小，与 needWidth最接近的分辨率 strSizesVals 摄像头支持的所有分辨率（字符串形式） needWidth
     * 希望设置的分辨率宽度 size_out 传去参数、当不支持期望的分辨率时，找到的与之最相近的分辨率
     * 匹配的方式更严格，但是更难接近所预期的分辨率
     */
    private void getSuitableWidth(String strSizesVals, int needWidth, int needHeight, int[] size_out) {
        int modeWidth = 0;
        int modeHeight = 0;
        int modeDiff = 1000;
        String tokens[] = strSizesVals.split(",");
        for (int i = 0; i < tokens.length; i++) {
            String tokens2[] = tokens[i].split("x");
            int tmpdiff = Math.abs(Integer.parseInt(tokens2[0]) - needWidth) * 10 + Math.abs(Integer.parseInt(tokens2[1]) - needHeight);
            if (modeDiff > tmpdiff) {
                modeDiff = tmpdiff;
                modeWidth = Integer.parseInt(tokens2[0]);
                modeHeight = Integer.parseInt(tokens2[1]);
            }
        }
        Log.d(TAG, "modeWidth = " + modeWidth + ", modeHeight = " + modeHeight);
        size_out[0] = modeWidth;
        size_out[1] = modeHeight;
    }

    private static String getPid0(int i) {
        String tmp = "1";
        try {
            FileReader fr = new FileReader("/sys/class/video4linux/video0/device/input/input" + i + "/id/product");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            Log.d("lixm0", "text = " + text);
            return text;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return tmp;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return tmp;
    }

    public static boolean isVideo0ColorCamera() {
        for (int i = 0; i <= 20; i++) {
            String value = getPid0(i);
            if (!TextUtils.isEmpty(value) && value.toLowerCase().contains("c")) {
                // 已经检测到： video0 就是可见光摄像头
                Log.d("lixm", "video0 is color camera.");
                return true;
            }
        }
        return false;
    }
}
