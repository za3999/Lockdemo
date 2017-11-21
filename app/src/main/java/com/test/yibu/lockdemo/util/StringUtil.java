package com.test.yibu.lockdemo.util;

import android.text.TextUtils;

/**
 * Created by zhengcf on 2017/11/20.
 */

public class StringUtil {


    public static boolean isMacAddress(String val) {
        String trueMacAddress = "([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}";
        // 这是真正的MAV地址；正则表达式；
        if (!TextUtils.isEmpty(val) && val.matches(trueMacAddress)) {
            return true;
        } else {
            return false;
        }
    }
}
