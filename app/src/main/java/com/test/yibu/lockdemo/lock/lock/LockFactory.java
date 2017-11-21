package com.test.yibu.lockdemo.lock.lock;

import android.content.Context;

import com.test.yibu.lockdemo.bean.LockMessage;

/**
 * Created by zhengcf on 2017/8/9.
 */

public class LockFactory {

    public static ILock getLockInstance(Context context,LockMessage lockMessage) {
        ILock iLock = null;
        if (lockMessage.getType() == 0) {
            iLock = new LockDemoImpl(context);
        } 
        return iLock;
    }
}
