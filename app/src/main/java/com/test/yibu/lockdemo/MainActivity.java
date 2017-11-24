package com.test.yibu.lockdemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.permissionlib.util.AndroidMPermissionHelper;
import com.test.yibu.lockdemo.bean.LockMessage;
import com.test.yibu.lockdemo.lock.lock.LockConstant;
import com.test.yibu.lockdemo.lock.lock.LockManager;
import com.test.yibu.lockdemo.lock.lock.linister.LockCloseListener;
import com.test.yibu.lockdemo.lock.lock.linister.OpenLockListener;
import com.test.yibu.lockdemo.util.DateTimeUtil;
import com.test.yibu.lockdemo.util.LogHelper;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements AMapLocationListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.tv_cycling_time)
    TextView tvCyclingTime;
    @BindView(R.id.scan_btn)
    View vScanBtn;
    @BindView(R.id.cycling_layout)
    View vCycling;

    LockManager lockManager;
    Dialog loadingDialog;
    Timer timer;
    Long startTime;
    Animation inAnim, outAnim;
    AMap aMap;
    public AMapLocationClient mLocationClient;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        outAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        initMap(savedInstanceState);
        initLocationClient();
        AndroidMPermissionHelper.checkPermission(this, new AndroidMPermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted() {
                mLocationClient.startLocation();
            }

            @Override
            public void onDenied() {
                Toast.makeText(MainActivity.this, "位置权限失败", Toast.LENGTH_LONG).show();
            }
        }, AndroidMPermissionHelper.PERMISSION_LOCATION);
    }

    @OnClick({R.id.scan_btn, R.id.stop_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_btn:
                checkScanPermission();
                break;
            case R.id.stop_tv:
                endCycling();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
//            String result = data.getStringExtra("result");
//            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            openLock();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        LogHelper.w(TAG, "onLocationChanged aMapLocation:" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude());
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 17.5f);
                aMap.animateCamera(cameraUpdate);
                mLocationClient.stopLocation();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                LogHelper.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    private void initMap(@Nullable Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        UiSettings settings = aMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true); // 是否显示定位按钮
        settings.setLogoBottomMargin(-50);//隐藏logo
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        locationStyle.radiusFillColor(Color.parseColor("#1A007ef7"));
        locationStyle.strokeWidth(0f);
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon_position_ic));
        aMap.setMyLocationStyle(locationStyle);
        aMap.setMyLocationEnabled(true);
    }

    private void initLocationClient() {
        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationCacheEnable(true);
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        mLocationOption.setGpsFirst(true);
        mLocationClient.setLocationOption(mLocationOption);
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
        loadingDialog = ProgressDialog.show(this, "", getString(R.string.str_opening), true, false);
        final LockMessage lockMessage = new LockMessage("3C:A3:08:08:C5:E7", LockConstant.INIT_PWD, LockConstant.INIT_KEY);
        loadingDialog.show();
        lockManager = LockManager.getInstance(this);
        lockManager.openLock(lockMessage, new OpenLockListener() {
            @Override
            public void onOpenLock(boolean success, String message) {
                loadingDialog.dismiss();
                if (success) {
                    startCycling();
                    monitorClose(lockMessage);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.str_open_lock_fail, message), Toast.LENGTH_LONG).show();
                    if (LockConstant.LOCK_TYPE_ALREADY_OPEN.equals(message)) {
                        monitorClose(lockMessage);
                    } else {
                        lockManager.cancelOption();
                    }
                }
            }
        }, 1000 * 25l);
    }

    private void monitorClose(LockMessage lockMessage) {
        lockManager.monitoringLockClose(lockMessage, new LockCloseListener() {
            @Override
            public void onLockClose(boolean state) {
                Toast.makeText(MainActivity.this, getString(R.string.str_close_lock_success), Toast.LENGTH_LONG).show();
                endCycling();
            }
        });
    }


    private void startCycling() {
        vScanBtn.setVisibility(View.GONE);
        vCycling.setVisibility(View.VISIBLE);
        vCycling.setAnimation(inAnim);
        inAnim.start();
        startTime = System.currentTimeMillis();
        timer = new Timer();
        startTime = 0l;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCyclingTime.setText(DateTimeUtil.formatLong2TimeStr(startTime));
                        startTime += 1000;
                    }
                });
            }
        }, 0, 1000);
    }

    private void endCycling() {
        vScanBtn.setVisibility(View.VISIBLE);
        vCycling.setVisibility(View.GONE);
        vCycling.setAnimation(outAnim);
        outAnim.start();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
