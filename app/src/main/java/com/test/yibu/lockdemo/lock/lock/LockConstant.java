package com.test.yibu.lockdemo.lock.lock;

/**
 * Created by zhengcf on 2017/8/24.
 */

public class LockConstant {

    public static final byte[] INIT_PWD = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
    public static final byte[] INIT_KEY = {32, 87, 47, 82, 54, 75, 63, 71, 48, 80, 65, 88, 17, 99, 45, 43};

    public static final int LOCK_STATUS_UNKNOWN = 0;

    public static final int LOCK_STATUS_OPEN = 1;

    public static final int LOCK_STATUS_CLOSE = 2;

    public static final String LOCK_ERROR_TYPE_NOT_SUPPORT = "not support";

    public static final String LOCK_ERROR_TYPE_OPEN_FAIL = "open fail";

    public static final String LOCK_ERROR_TYPE_DISCONNECTED = "gatt disconnected";

    public static final String LOCK_ERROR_TYPE_BLUETOOTH_NOT_OPEN = "bluetooth not open";

    public static final String LOCK_ERROR_TYPE_BICYCLE_ERROR = "bicycle error";

    public static final String LOCK_TYPE_SUCCESS = "success";

    public static final String LOCK_ERROR_TYPE_TIME_OUT = "time out";

    public static final String LOCK_ERROR_TYPE_SCAN_TIME_OUT = "scan time out";

    public static final String LOCK_ERROR_TYPE_READ_ERROR = "read status error";

    public static final String LOCK_ERROR_TYPE_WRITE_ERROR = "write error";

    public static final String LOCK_TYPE_ALREADY_OPEN = "already open";
}
