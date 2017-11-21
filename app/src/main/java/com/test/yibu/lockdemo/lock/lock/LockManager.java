package com.test.yibu.lockdemo.lock.lock;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.test.yibu.lockdemo.bean.LockMessage;
import com.test.yibu.lockdemo.lock.bluetooth.BluetoothLeHelper;
import com.test.yibu.lockdemo.lock.lock.linister.LockCloseListener;
import com.test.yibu.lockdemo.lock.lock.linister.OpenLockListener;


/**
 * Created by zhengcf on 2017/8/9.
 */

public class LockManager {

    private static final String TAG = "LockManager";

    private ILock iLock;
    private Context context;

    private LockManager(Context context) {
        this.context = context;
    }

    public static LockManager getInstance(final Context context) {
        return new LockManager(context);
    }

    public void openLock(final LockMessage lockMessage, final OpenLockListener listener, long openPeriod) {
        openLock(lockMessage, false, listener, 0, openPeriod);
    }

    public void openLock(final LockMessage lockMessage, boolean needScan, final OpenLockListener listener, long
            scanPeriod, long openPeriod) {
        if (!BluetoothLeHelper.getInstance(context).isBluetoothLeSupport()) {
            listener.onOpenLock(false, LockConstant.LOCK_ERROR_TYPE_NOT_SUPPORT);
        } else if (!BluetoothLeHelper.getInstance(context).isBluetoothEnable()) {
            listener.onOpenLock(false, LockConstant.LOCK_ERROR_TYPE_BLUETOOTH_NOT_OPEN);
        } else {
            BluetoothLeHelper.getInstance(context).release();
            iLock = LockFactory.getLockInstance(context, lockMessage);
            if (needScan) {
                BluetoothLeHelper.getInstance(context).startLeScan(new BluetoothLeHelper.ScanCallBackListener() {
                    @Override
                    public void onScanTimeout() {
                        listener.onOpenLock(false, LockConstant.LOCK_ERROR_TYPE_SCAN_TIME_OUT);
                    }

                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        if (iLock.checkDevices(lockMessage, device, scanRecord)) {
                            BluetoothLeHelper.getInstance(context).stopLeScan(this);
                            iLock.openLock(lockMessage, listener);
                        }
                    }
                }, scanPeriod);
            } else {
                iLock.openLock(lockMessage, listener);
            }
        }
    }

    public void monitoringLockClose(final LockMessage lockMessage, final LockCloseListener listener) {
        monitoringLockClose(lockMessage, false, listener, 0l);
    }

    private void monitoringLockClose(final LockMessage lockMessage, boolean needScan, final LockCloseListener listener, long
            scanPeriod) {
        if (!BluetoothLeHelper.getInstance(context).isBluetoothLeSupport()) {
            listener.onLockClose(false);
        } else if (!BluetoothLeHelper.getInstance(context).isBluetoothEnable()) {
            listener.onLockClose(false);
        } else {
            iLock = LockFactory.getLockInstance(context, lockMessage);
            iLock.monitoringLockClose(lockMessage, listener);
            if (needScan) {
                BluetoothLeHelper.getInstance(context).startLeScan(new BluetoothLeHelper.ScanCallBackListener() {
                    @Override
                    public void onScanTimeout() {
                        listener.onLockClose(false);
                    }

                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        if (iLock.checkDevices(lockMessage, device, scanRecord)) {
                            BluetoothLeHelper.getInstance(context).stopLeScan(this);
                            iLock.monitoringLockClose(lockMessage, listener);
                        }
                    }
                }, scanPeriod);
            } else {
                iLock.monitoringLockClose(lockMessage, listener);
            }
        }
    }

    public void cancelOption() {
        BluetoothLeHelper.getInstance(context).release();
    }
}
