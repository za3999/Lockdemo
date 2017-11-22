package com.test.yibu.lockdemo.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marno on 2017/7/17/15:19
 * Function：
 * Desc：
 */
public class DateTimeUtil {


    /**
     * 将毫秒转化成 00：00：00 格式的时间字符串
     *
     * @param l 毫秒
     * @return 00：00：00 格式化后的时间字符串
     */
    public static String formatLong2TimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        second = l.intValue() / 1000;

        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return (getTwoLength(hour) + ":" + getTwoLength(minute) + ":" + getTwoLength(second));
    }

    private static String getTwoLength(final int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }


}
