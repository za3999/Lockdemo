package cn.marno.kkqrcode;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;

/**
 * Create by Marno on 2016/12/6 14:47
 * Function：处理二维码识别的异步任务
 * Desc：
 */
class QRDecodeTask extends AsyncTask<Void, Void, String> {
    private Camera mCamera;
    private byte[] mData;
    private Delegate mDelegate;

    public QRDecodeTask(Camera camera, byte[] data, Delegate delegate) {
        mCamera = camera;
        mData = data;
        mDelegate = delegate;
    }

    public QRDecodeTask perform() {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
        return this;
    }

    public void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mDelegate = null;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                Camera.Size size = parameters.getPreviewSize();
                int width = size.width;
                int height = size.height;

                byte[] rotatedData = new byte[mData.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        rotatedData[x * height + height - y - 1] = mData[x + y * width];
                    }
                }
                int tmp = width;
                width = height;
                height = tmp;

                try {
                    if (mDelegate == null) {
                        return null;
                    }
                    return mDelegate.processData(rotatedData, width, height, false);
                } catch (Exception e1) {
                    try {
                        return mDelegate.processData(rotatedData, width, height, true);
                    } catch (Exception e2) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public interface Delegate {
        String processData(byte[] data, int width, int height, boolean isRetry);
    }
}
