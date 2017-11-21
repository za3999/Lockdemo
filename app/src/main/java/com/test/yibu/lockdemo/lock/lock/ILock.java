package com.test.yibu.lockdemo.lock.lock;

import android.bluetooth.BluetoothDevice;

import com.test.yibu.lockdemo.bean.LockMessage;
import com.test.yibu.lockdemo.lock.lock.linister.LockCloseListener;
import com.test.yibu.lockdemo.lock.lock.linister.OpenLockListener;


/**
 * Created by zhengcf on 2017/8/9.
 */

public interface ILock {

    boolean checkDevices(LockMessage message, BluetoothDevice device, byte[] scanRecord);

    void openLock(LockMessage message, OpenLockListener openLockListener);

    void monitoringLockClose(LockMessage message,LockCloseListener lockCloseListener);
}
