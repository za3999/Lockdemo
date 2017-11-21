package com.test.yibu.lockdemo;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.permissionlib.util.AndroidMPermissionActivity;
import com.permissionlib.util.AndroidMPermissionHelper;
import com.test.yibu.lockdemo.bean.LockMessage;
import com.test.yibu.lockdemo.lock.lock.LockConstant;
import com.test.yibu.lockdemo.lock.lock.LockManager;
import com.test.yibu.lockdemo.lock.lock.linister.LockCloseListener;
import com.test.yibu.lockdemo.lock.lock.linister.OpenLockListener;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.hint_tv)
    TextView hintTv;

    LockManager lockManager;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }


    @OnClick({R.id.scan_btn, R.id.cancel_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_btn:
                checkScanPermission();
                break;
            case R.id.cancel_btn:
                LockManager.getInstance(this).cancelOption();
                hintTv.setText("");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            openLock();
        }
    }

    private void checkScanPermission() {
        AndroidMPermissionHelper.checkPermission(this, new AndroidMPermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted() {
                startActivityForResult(new Intent(MainActivity.this, ScanActivity.class), 0);
            }

            @Override
            public void onDenied() {
                Toast.makeText(MainActivity.this, "获取相机权限失败", Toast.LENGTH_LONG).show();
            }
        }, AndroidMPermissionHelper.PERMISSION_CAMERA);
    }

    private void openLock() {
        final LockMessage lockMessage = new LockMessage("3C:A3:08:08:C5:E7", LockConstant.INIT_PWD, LockConstant.INIT_KEY);
        hintTv.setText(getString(R.string.str_opening));
        lockManager = LockManager.getInstance(this);
        lockManager.openLock(lockMessage, new OpenLockListener() {
            @Override
            public void onOpenLock(boolean success, String message) {
                if (success) {
                    hintTv.setText(getString(R.string.str_open_lock_success));
                    monitorClose(lockMessage);
                } else {
                    hintTv.setText(getString(R.string.str_open_lock_fail, message));
                    if (LockConstant.LOCK_TYPE_ALREADY_OPEN.equals(message)) {
                        monitorClose(lockMessage);
                    }
                }
            }
        }, 1000 * 20l);
    }

    private void monitorClose(LockMessage lockMessage) {
        lockManager.monitoringLockClose(lockMessage, new LockCloseListener() {
            @Override
            public void onLockClose(boolean state) {
                hintTv.setText(getString(R.string.str_close_lock_success));
            }
        });
    }
}
