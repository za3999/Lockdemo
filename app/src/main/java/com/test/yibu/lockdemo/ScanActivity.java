package com.test.yibu.lockdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.permissionlib.util.AndroidMPermissionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import cn.marno.kkqrcode.ScannerView;

/**
 * Created by zhengcf on 2017/11/20.
 */

public class ScanActivity extends BaseActivity implements ScannerView.Delegate {

    @BindView(R.id.ut_scanner_view)
    ScannerView mScannerView;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_scan;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView.setDelegate(this);
        checkCameraPermission();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Intent mIntent = new Intent();
        mIntent.putExtra("result", result);
        setResult(RESULT_OK, mIntent);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开相机失败，请到设置中检查是否允许相机权限", Toast.LENGTH_LONG).show();
    }

    /**
     * 检查相机权限
     */
    private void checkCameraPermission() {
        AndroidMPermissionHelper.checkPermission(ScanActivity.this, new AndroidMPermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted() {
                mScannerView.startSpotAndShowRect();
            }

            @Override
            public void onDenied() {
                Toast.makeText(ScanActivity.this, R.string.str_no_permission, Toast.LENGTH_LONG).show();
                finish();
            }
        }, AndroidMPermissionHelper.PERMISSION_CAMERA);
    }

}
