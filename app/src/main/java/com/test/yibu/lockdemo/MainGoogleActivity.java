package com.test.yibu.lockdemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class MainGoogleActivity extends BaseActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainGoogleActivity";

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @BindView(R.id.map_view)
    MapView mMapView;
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

    private GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    public int getLayoutRes() {
        return R.layout.activity_google_main;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMap(savedInstanceState);
        inAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        outAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        AndroidMPermissionHelper.checkPermission(this, new AndroidMPermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted() {
                mMapView.getMapAsync(MainGoogleActivity.this);
            }

            @Override
            public void onDenied() {
                Toast.makeText(MainGoogleActivity.this, "位置权限失败", Toast.LENGTH_LONG).show();
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
            openLock();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LogHelper.d(TAG,"onMapReady");
        this.mGoogleMap = googleMap;
        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogHelper.d(TAG,"onConnected");
        Location mLastLocation = null;
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (mLastLocation != null) {
            //place marker at current position
            mGoogleMap.clear();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon_position_ic));
//            mGoogleMap.addMarker(markerOptions);
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        });
    }


    @Override
    public void onConnectionSuspended(int i) {
        LogHelper.d(TAG,"onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogHelper.d(TAG,"onConnectionFailed");
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private void initMap(@Nullable Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        buildGoogleApiClient();
    }

    private void checkScanPermission() {
        AndroidMPermissionHelper.checkPermission(this, new AndroidMPermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted() {
                startActivityForResult(new Intent(MainGoogleActivity.this, ScanActivity.class), 0);
            }

            @Override
            public void onDenied() {
                Toast.makeText(MainGoogleActivity.this, "获取相机权限失败", Toast.LENGTH_LONG).show();
            }
        }, AndroidMPermissionHelper.PERMISSION_CAMERA);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void openLock() {
        loadingDialog = ProgressDialog.show(this, "", getString(R.string.str_opening), true, false);
//        final LockMessage lockMessage = new LockMessage("3C:A3:08:08:C5:E7", LockConstant.INIT_PWD, LockConstant.INIT_KEY);
        final LockMessage lockMessage = new LockMessage("50:33:8B:F3:E6:0A", LockConstant.INIT_PWD, LockConstant.INIT_KEY);
//        final LockMessage lockMessage = new LockMessage("50:33:8B:F2:1C:F8", LockConstant.INIT_PWD, LockConstant.INIT_KEY);
//        final LockMessage lockMessage = new LockMessage("50:33:8B:F3:1F:F5", LockConstant.INIT_PWD, LockConstant.INIT_KEY);
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
                    Toast.makeText(MainGoogleActivity.this, getString(R.string.str_open_lock_fail, message), Toast.LENGTH_LONG).show();
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
                Toast.makeText(MainGoogleActivity.this, getString(R.string.str_close_lock_success), Toast.LENGTH_LONG).show();
                endCycling();
                lockManager.cancelOption();
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
