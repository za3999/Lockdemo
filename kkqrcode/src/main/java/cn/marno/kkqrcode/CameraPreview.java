package cn.marno.kkqrcode;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * 实时预览控件
 */
 class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getSimpleName();
    private Camera mCamera;
    private boolean mPreviewing = true;
    private boolean mAutoFocus = true;
    private boolean mSurfaceCreated = false;
    private boolean mIsFlashLightOpen = false;
    private CameraOptions mCameraOptions;

    public CameraPreview(Context context) {
        super(context);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mCameraOptions = new CameraOptions(getContext());
            mCameraOptions.initFromCameraParameters(mCamera);

            getHolder().addCallback(this);
            if (mPreviewing) {
                requestLayout();
            } else {
                showCameraPreview();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        stopCameraPreview();

        post(new Runnable() {
            public void run() {
                showCameraPreview();
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = false;
        stopCameraPreview();
    }

    /**
     * 显示相机预览
     */
    public void showCameraPreview() {
        if (mCamera != null) {
            try {
                mPreviewing = true;
                mCamera.setPreviewDisplay(getHolder());

                mCameraOptions.setDesiredCameraParameters(mCamera);
                mCamera.startPreview();
                if (mAutoFocus) {
                    mCamera.autoFocus(autoFocusCB);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    /**
     * 停止相机预览
     */
    public void stopCameraPreview() {
        if (mCamera != null) {
            try {
                removeCallbacks(doAutoFocus);

                mPreviewing = false;
                mCamera.cancelAutoFocus();
                mCamera.setOneShotPreviewCallback(null);
                mCamera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    /**
     * 打开闪光灯
     */
    public void openFlashlight() {
        if (flashLightAvaliable()) {
            mCameraOptions.openFlashlight(mCamera);
            mIsFlashLightOpen = true;
        }
    }

    /**
     * 关闭闪光灯
     */
    public void closeFlashlight() {
        if (flashLightAvaliable()) {
            mCameraOptions.closeFlashlight(mCamera);
            mIsFlashLightOpen = false;
        }
    }

    /**
     * 打开或关闭闪光灯
     */
    public void toggleFlashlight() {
        if (isFlashLightOpen()) closeFlashlight();
        else openFlashlight();
    }

    /**
     * 判断闪光灯是否开启
     *
     * @return
     */
    public boolean isFlashLightOpen() {
        return mIsFlashLightOpen;
    }

    /**
     * 判断闪光灯是否可用
     *
     * @return
     */
    private boolean flashLightAvaliable() {
        return mCamera != null &&
                mPreviewing &&
                mSurfaceCreated &&
                getContext().getPackageManager()
                        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (mCamera != null && mPreviewing && mAutoFocus && mSurfaceCreated) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            postDelayed(doAutoFocus, 1000);
        }
    };

}