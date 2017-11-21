package com.permissionlib.util;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import com.permissionlib.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 权限拦截页面
 *
 * @author zhengcf on 2017/7/18.
 */
public class AndroidMPermissionActivity extends AppCompatActivity {

    String[] permissions;
    long code;
    AlertDialog alertDialog = null;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissions = getIntent().getStringArrayExtra("permissions");
        code = getIntent().getLongExtra("code", 0l);
        requestPermissions(permissions, AndroidMPermissionHelper.REQUEST_CODE_ASK_PERMISSIONS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alertDialog != null && !alertDialog.isShowing()) {
            onPermissionResult(AndroidMPermissionHelper.isAllPermissionGranted(this, permissions));
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (AndroidMPermissionHelper.REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            boolean success = AndroidMPermissionHelper.isAllPermissionGranted(grantResults);
            if (success) {
                onPermissionResult(success);
            } else {
                alertDialog = createPermissionDialog(AndroidMPermissionHelper.getNotGrantedPermission(permissions,
                        grantResults));
                alertDialog.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 当权限返回结果之后的处理
     *
     * @param success
     */
    private void onPermissionResult(boolean success) {
        Intent intent = new Intent(AndroidMPermissionHelper.SECURITY_RESULT_ACTION);
        intent.putExtra("success", success);
        intent.putExtra("code", code);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        finish();
    }

    /**
     * 弹出去设置权限提示框
     *
     * @return
     */
    private AlertDialog createPermissionDialog(ArrayList<String> permissions) {

        return new AlertDialog.Builder(this).setMessage(getString(R.string
                .str_setting_open_permission, getPermissionDialogContent(permissions))).setPositiveButton(R.string.str_go_setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocalUtil.openSystemAppManage(AndroidMPermissionActivity.this, getPackageName());
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onPermissionResult(false);
            }
        }).create();
    }

    /**
     * 获取权限组对应的文字
     *
     * @param permissions
     * @return
     */
    private String getPermissionDialogContent(ArrayList<String> permissions) {
        HashMap<String, String> permissionContent = initPermissionContent();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < permissions.size(); i++) {
            builder.append(permissionContent.get(permissions.get(i)));
            if (i < permissions.size() - 1) {
                builder.append(getString(R.string.str_and));
            }
        }
        return builder.toString();
    }

    /**
     * 初始化权限对应的提示文字
     */
    private HashMap<String, String> initPermissionContent() {
        HashMap<String, String> permissionContent = new HashMap<>();
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_MICROPHONE, getString(R.string.str_permission_microphone));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_SENSORS, getString(R.string.str_permission_sensors));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_CALENDAR, getString(R.string.str_permission_calendar));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_CAMERA, getString(R.string.str_permission_camera));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_GET_ACCOUNTS, getString(R.string.str_permission_get_accounts));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_PHONE_STATE, getString(R.string.str_permission_phone_state));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_LOCATION, getString(R.string.str_permission_location));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_SMS, getString(R.string.str_permission_sms));
        permissionContent.put(AndroidMPermissionHelper.PERMISSION_STORAGE, getString(R.string.str_permission_storage));
        return permissionContent;
    }
}
