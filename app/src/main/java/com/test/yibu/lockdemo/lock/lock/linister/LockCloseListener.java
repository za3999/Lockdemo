package com.test.yibu.lockdemo.lock.lock.linister;


import com.test.yibu.lockdemo.lock.bluetooth.BluetoothLeHelper;

/**
 * Created by zhengcf on 2017/8/25.
 */

public abstract class LockCloseListener {

    BluetoothLeHelper.GattAdapter adapter;

    /**
     * 关锁
     *
     * @param state true 成功，false 失败
     */
    public abstract void onLockClose(boolean state);

    public BluetoothLeHelper.GattAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BluetoothLeHelper.GattAdapter adapter) {
        this.adapter = adapter;
    }
}
