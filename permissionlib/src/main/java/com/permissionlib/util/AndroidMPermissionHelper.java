package com.permissionlib.util; /**
 *
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.ArrayList;

import com.permissionlib.R;

/**
 * 权限管理Helper类
 *
 * @author zhengcf on 2017/7/18.
 */
public final class AndroidMPermissionHelper {

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    public static final String SECURITY_RESULT_ACTION = "security_result_action";

    /**
     * 麦克风权限
     */
    public static final String PERMISSION_MICROPHONE = Manifest.permission.RECORD_AUDIO;

    /**
     * 传感器权限
     */
    public static final String PERMISSION_SENSORS = Manifest.permission.BODY_SENSORS;

    /**
     * 日历权限
     */
    public static final String PERMISSION_CALENDAR = Manifest.permission.WRITE_CALENDAR;

    /**
     * 访问摄像头权限组
     */
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    /**
     * 访问通讯录权限组
     */
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;

    /**
     * 读取电话状态权限组
     */
    public static final String PERMISSION_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    /**
     * 读取位置信息权限组
     */
    public static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    /**
     * 发送短信、访问短信权限组
     */
    public static final String PERMISSION_SMS = Manifest.permission.READ_SMS;

    /**
     * 使用外置存储权限组
     */

    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * 带回调的权限检查
     *
     * @param context
     * @param callBack
     * @param permissions
     */
    public static void checkPermission(Context context, PermissionCallBack callBack, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new RuntimeException("permissions is null");
        }

        if (isAllPermissionGranted(context, permissions)) {
            if (callBack != null) {
                callBack.onGranted();
            } else {
                Toast.makeText(context, context.getString(R.string.str_get_permission_success), Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (callBack != null) {
                    callBack.onDenied();
                } else {
                    Toast.makeText(context, context.getString(R.string.str_get_permission_fail), Toast.LENGTH_SHORT).show();
                }
            } else {
                showPermissionActivity(context, callBack, getNotGrantedPermission(context, permissions));
            }
        }
    }

    /**
     * 获取没有被授予的权限的列表
     *
     * @param permissions
     * @return
     */
    public static ArrayList<String> getNotGrantedPermission(String[] permissions, int[] grantResults) {
        ArrayList<String> permissionList = new ArrayList<String>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                permissionList.add(permissions[i]);
        }
        return permissionList;
    }

    /**
     * 检查是否所有权限都通过
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean isAllPermissionGranted(Context context, String... permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (context.checkCallingOrSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查权限返回结果
     *
     * @param grantResults
     * @return
     */
    public static boolean isAllPermissionGranted(int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示权限框
     *
     * @param context
     * @param callback
     * @param permissions
     */
    private static void showPermissionActivity(Context context, final PermissionCallBack callback,
                                               String... permissions) {
        final long code = System.currentTimeMillis();
        Intent intent = new Intent(context, AndroidMPermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("permissions", permissions);
        intent.putExtra("code", code);
        final LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        mLocalBroadcastManager.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getLongExtra("code", 0l) == code) {
                    if (callback != null) {
                        if (intent.getBooleanExtra("success", false)) {
                            callback.onGranted();
                        } else {
                            callback.onDenied();
                        }
                    }
                    mLocalBroadcastManager.unregisterReceiver(this);
                }
            }
        }, new IntentFilter(SECURITY_RESULT_ACTION));
        context.startActivity(intent);
    }

    /**
     * 获取没有权限的列表
     *
     * @param context
     * @param permissions
     * @return
     */
    @SuppressWarnings("unused")
    private static String[] getDeniedPermission(Context context, String... permissions) {
        ArrayList<String> permissionList = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            if (context.checkCallingOrSelfPermission(permissions[i]) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(permissions[i]);
            }
        }
        return permissionList.toArray(new String[permissionList.size()]);
    }

    /**
     * 获取没有被授予的权限的列表
     *
     * @param context
     * @param permissions
     * @return
     */
    private static String[] getNotGrantedPermission(Context context, String... permissions) {
        ArrayList<String> permissionList = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            if (context.checkCallingOrSelfPermission(permissions[i]) == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            permissionList.add(permissions[i]);
        }
        return permissionList.toArray(new String[permissionList.size()]);
    }

    public interface PermissionCallBack {

        void onGranted();

        void onDenied();

    }

}
