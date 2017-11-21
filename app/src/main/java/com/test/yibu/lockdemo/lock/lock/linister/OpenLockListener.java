package com.test.yibu.lockdemo.lock.lock.linister;


import com.test.yibu.lockdemo.lock.bluetooth.BluetoothLeHelper;

/**
 * Created by zhengcf on 2017/8/25.
 */

public abstract class OpenLockListener {

    /**
     * 电量值
     */
    private int electric = -1;

    private boolean isTimeOut = false;

    BluetoothLeHelper.GattAdapter adapter;

    public int getElectric() {
        return electric;
    }

    public void setElectric(int electric) {
        this.electric = electric;
    }

    /**
     * @param success true 成功，false 失败
     * @param message 消息
     */
    public abstract void onOpenLock(boolean success, String message);

    public BluetoothLeHelper.GattAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BluetoothLeHelper.GattAdapter adapter) {
        this.adapter = adapter;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean timeOut) {
        isTimeOut = timeOut;
    }
}
