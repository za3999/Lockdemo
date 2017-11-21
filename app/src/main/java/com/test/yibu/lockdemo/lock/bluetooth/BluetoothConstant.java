package com.test.yibu.lockdemo.lock.bluetooth;

import java.util.UUID;

/**
 * Created by zhengcf on 2017/7/25.
 */

public class BluetoothConstant {

    /**
     * 扫描设备超时时间
     */
    public static final long SCAN_PERIOD = 10 * 1000L;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final String ACTION_BLE_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
}
