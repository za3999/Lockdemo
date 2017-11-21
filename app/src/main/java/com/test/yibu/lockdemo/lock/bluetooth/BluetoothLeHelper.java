package com.test.yibu.lockdemo.lock.bluetooth;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.permissionlib.util.AndroidMPermissionHelper;
import com.test.yibu.lockdemo.util.LogHelper;
import com.test.yibu.lockdemo.util.StringUtil;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 蓝牙功能辅助类
 * Created by zhengcf on 2017/7/21.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeHelper {

    private final static String TAG = BluetoothLeHelper.class.getSimpleName();
    private boolean mScanning;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeHelper instance;
    private BluetoothGatt mBluetoothGatt;
    List<SoftReference<GattAdapter>> adapterRefList = new CopyOnWriteArrayList<>();
    private int mConnectionState = BluetoothConstant.STATE_DISCONNECTED;
    Handler handler = new Handler(Looper.getMainLooper());

    private BluetoothLeHelper(Context context) {
        this.context = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个蓝牙适配器(API必须在以上android4.3或以上和版本)
            try {
                final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                if (bluetoothManager != null) {
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized static BluetoothLeHelper getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothLeHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void addGattAdapter(GattAdapter gattAdapter) {
        Log.d(TAG, "addGattAdapter:" + gattAdapter);
        adapterRefList.add(new SoftReference(gattAdapter));
    }

    public void removeGattAdapter(GattAdapter gattAdapter) {

        for (int i = adapterRefList.size() - 1; i >= 0; i--) {
            SoftReference<GattAdapter> adapterRef = adapterRefList.get(i);
            if (adapterRef.get() == null || adapterRef.get() == gattAdapter) {
                adapterRefList.remove(adapterRef);
                Log.d(TAG, "removeGattAdapter:" + adapterRef);
                break;
            }
        }
        Log.d(TAG, "adapter size:" + adapterRefList.size());
    }

    /**
     * 获取连接状态
     *
     * @return
     */
    public int getConnectionState() {
        return mConnectionState;
    }

    /**
     * 是否支持蓝牙ble功能
     *
     * @return
     */
    public boolean isBluetoothLeSupport() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false;
        }
        if (mBluetoothAdapter == null) {
            return false;
        }
        //检查是否支持BLE
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 蓝牙是否可用
     *
     * @return
     */
    public boolean isBluetoothEnable() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * 是否正在扫描中
     *
     * @return
     */
    public boolean isScanning() {
        return mScanning;
    }

    /**
     * 启动蓝牙设备
     *
     * @param activity
     * @param requestCode
     */
    public void startBluetooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 开始扫描
     *
     * @param mLeScanCallback
     * @param scantPeriod
     */
    public void startLeScan(final ScanCallBackListener mLeScanCallback, final long scantPeriod) {
        mScanning = true;
        AndroidMPermissionHelper.checkPermission(context, new AndroidMPermissionHelper.PermissionCallBack() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onGranted() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mScanning) {
                            stopLeScan(mLeScanCallback);
                            mLeScanCallback.onScanTimeout();
                        }
                    }
                }, scantPeriod);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }

            @Override
            public void onDenied() {
                Toast.makeText(context, "获取位置权限失败", Toast.LENGTH_LONG).show();
                mScanning = false;
            }
        }, Manifest.permission.ACCESS_COARSE_LOCATION);

    }

    /**
     * 结束扫描
     *
     * @param mLeScanCallback
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopLeScan(BluetoothAdapter.LeScanCallback mLeScanCallback) {
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }


    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connect(final String address) {
        if (mConnectionState == BluetoothConstant.STATE_DISCONNECTED) {
            if (mBluetoothAdapter == null || !StringUtil.isMacAddress(address)) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                return false;
            }

            // Previously connected device.  Try to reconnect.
//        if (this.address != null && address.equals(this.address)
//                && mBluetoothGatt != null) {
//            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
//            if (mBluetoothGatt.connect()) {
//                mConnectionState = BluetoothConstant.STATE_CONNECTING;
//                return true;
//            } else {
//                return false;
//            }
//        }

            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                Log.w(TAG, "Device not found.  Unable to connect.");
                return false;
            }
            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
            Log.d(TAG, "Trying to create a new connection.");
            mConnectionState = BluetoothConstant.STATE_CONNECTING;
            return true;
        } else {
            return false;
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        mConnectionState = BluetoothConstant.STATE_DISCONNECTED;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        disconnect();
        close();
        adapterRefList.clear();
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        return mBluetoothGatt.readCharacteristic(characteristic);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean readCharacteristic(UUID serverUUID, UUID readUUID) {
        boolean isSuccess = false;
        BluetoothGattService gattService = getGattService(serverUUID);
        if (gattService != null) {
            BluetoothGattCharacteristic bluetoothGattCharacteristic = gattService.getCharacteristic(readUUID);
            if (bluetoothGattCharacteristic != null) {
                isSuccess = readCharacteristic(bluetoothGattCharacteristic);
            }
        }
        return isSuccess;
    }

    /**
     * 写数据
     *
     * @param serverUUID
     * @param writeDataUUID
     * @param value
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean writeCharacteristic(UUID serverUUID, UUID writeDataUUID, byte[] value) {
        boolean isSuccess = false;
        if (value != null) {
            BluetoothGattService gattService = getGattService(serverUUID);
            if (gattService != null) {
                BluetoothGattCharacteristic bluetoothGattCharacteristic = gattService.getCharacteristic(writeDataUUID);
                if (bluetoothGattCharacteristic != null) {
                    isSuccess = writeCharacteristic(bluetoothGattCharacteristic, value);
                }
            }
        }
        return isSuccess;
    }

    /**
     * 写数据
     *
     * @param characteristic
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        characteristic.setValue(value);
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }


    /**
     * 配置监听状态
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean configCharacteristicNotification(UUID serverUUID, UUID readDataUUID, boolean enable) {
        boolean isSuccess = false;
        BluetoothGattService gattService = getGattService(serverUUID);
        if (gattService != null) {
            BluetoothGattCharacteristic readCharacteristic = gattService.getCharacteristic(readDataUUID);
            if (readCharacteristic != null) {
                BluetoothGattDescriptor descriptor = readCharacteristic.getDescriptor
                        (BluetoothConstant.CLIENT_CHARACTERISTIC_CONFIG);
                isSuccess = setCharacteristicNotification(readCharacteristic, descriptor, enable);
            }
        }
        return isSuccess;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                  BluetoothGattDescriptor descriptor, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || descriptor == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        return mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                 UUID clientConfigUUID, boolean enabled) {
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigUUID);
        return setCharacteristicNotification(characteristic, descriptor, enabled);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothGattService getGattService(UUID uuid) {
        if (mBluetoothGatt == null || uuid == null) {
            return null;
        }
        return mBluetoothGatt.getService(uuid);
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d(TAG, "Connected to GATT server.");
                        mConnectionState = BluetoothConstant.STATE_CONNECTED;

                        for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                            GattAdapter gattListener = gattListenerRef.get();
                            if (gattListener != null) {
                                gattListener.onConnected();
                            }
                            if (mBluetoothGatt != null) {
                                mBluetoothGatt.discoverServices();
                            }
                        }

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d(TAG, "Disconnected from GATT server.");
                        mConnectionState = BluetoothConstant.STATE_DISCONNECTED;
                        for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                            GattAdapter gattListener = gattListenerRef.get();
                            if (gattListener != null) {
                                gattListener.onDisconnected();
                            }
                        }
                    }
                }
            });

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, final int status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "mGattCallback onServicesDiscovered received: " + status);
                    for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                        GattAdapter gattListener = gattListenerRef.get();
                        LogHelper.d(TAG, "onServicesDiscovered gattListener: " + gattListener);
                        if (status == BluetoothGatt.GATT_SUCCESS && gattListener != null) {
                            gattListener.onServicesDiscovered();
                        }
                    }
                }
            });
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "mGattCallback onCharacteristicWrite");
                    for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                        GattAdapter gattListener = gattListenerRef.get();
                        if (gattListener != null) {
                            gattListener.onCharacteristicWrite(characteristic, status);
                        }
                    }
                }
            });
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LogHelper.i(TAG, "mGattCallback onCharacteristicRead");
                    for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                        GattAdapter gattListener = gattListenerRef.get();
                        if (gattListener != null)
                            gattListener.onCharacteristicRead(characteristic, status);
                    }
                }
            });
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LogHelper.i(TAG, "mGattCallback onCharacteristicChanged");
                    for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                        GattAdapter gattListener = gattListenerRef.get();
                        LogHelper.i(TAG, "onCharacteristicChanged " + gattListener);
                        if (gattListener != null) {
                            gattListener.onCharacteristicChanged(characteristic);
                        }
                    }
                }
            });

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LogHelper.i(TAG, "mGattCallback onDescriptorWrite");
                    for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                        GattAdapter gattListener = gattListenerRef.get();
                        if (gattListener != null) {
                            gattListener.onDescriptorWrite(descriptor, status);
                        }
                    }
                }
            });
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, final int status) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LogHelper.i(TAG, "mGattCallback onReadRemoteRssi");
                    for (SoftReference<GattAdapter> gattListenerRef : adapterRefList) {
                        GattAdapter gattListener = gattListenerRef.get();
                        if (gattListener != null) {
                            gattListener.onReadRssi(rssi, status);
                        }
                    }
                }
            });
        }
    };

    public interface ScanCallBackListener extends BluetoothAdapter.LeScanCallback {

        void onScanTimeout();
    }


    /**
     * 缺省适配器
     */
    public static abstract class GattAdapter {

        public void onConnected() {
            LogHelper.d(TAG, "onConnected");
        }

        public void onDisconnected() {
            LogHelper.d(TAG, "onDisconnected");
        }

        public void onServicesDiscovered() {
            LogHelper.d(TAG, "onServicesDiscovered");
        }

        public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {
            LogHelper.d(TAG, "onWriteGattCharacteristic");
        }

        public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
            LogHelper.d(TAG, "onCharacteristicRead");
        }

        public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
            LogHelper.d(TAG, "onCharacteristicChanged");
        }

        public void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status) {
            LogHelper.d(TAG, "onDescriptorWrite");
        }

        public void onReadRssi(int rssi, int status) {
            LogHelper.d(TAG, "onReadRssi");
        }
    }
}
