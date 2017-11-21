/**
 * Created on Apr 9, 2012 5:26:27 PM
 */
package com.test.yibu.lockdemo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;


/**
 * Created by zhengcf on 2017/9/15.
 */
public class LogHelper {

    public static final int ALL = 1;

    public static final int VERBOSE = android.util.Log.VERBOSE;

    public static final int DEBUG = android.util.Log.DEBUG;

    public static final int INFO = android.util.Log.INFO;

    public static final int WARN = android.util.Log.WARN;

    public static final int ERROR = android.util.Log.ERROR;

    public static final int ASSERT = android.util.Log.ASSERT;

    private static final boolean isLog = true;

    private static boolean isDebug = true;

    private static int filter = VERBOSE;

    public static void setFilter(int level) {
        filter = level;
    }

    public static void setIsDebug(boolean isDebug) {
        LogHelper.isDebug = isDebug;
    }

    private static String formatMsg(String msg) {
        return "[" + android.os.Process.myUid() + "]" + msg;
    }

    public static int e(String tag, String msg) {
        if (isLogToFile()) {
            f(tag, msg);
        }
        return showLog(ERROR) ? android.util.Log.e(tag, formatMsg(msg)) : 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (isLogToFile()) {
            f(tag, msg, tr);
        }
        return showLog(ERROR) ? android.util.Log.e(tag, formatMsg(msg), tr) : 0;
    }

    public static int w(String tag, String msg) {
        if (isLogToFile()) {
            f(tag, msg);
        }
        return showLog(WARN) ? android.util.Log.w(tag, formatMsg(msg)) : 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (isLogToFile()) {
            f(tag, msg, tr);
        }
        return showLog(WARN) ? android.util.Log.w(tag, formatMsg(msg), tr) : 0;
    }

    public static int i(String tag, String msg) {
        if (isLogToFile()) {
            f(tag, msg);
        }
        return showLog(INFO) ? android.util.Log.i(tag, formatMsg(msg)) : 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (isLogToFile()) {
            f(tag, msg, tr);
        }
        return showLog(INFO) ? android.util.Log.i(tag, formatMsg(msg), tr) : 0;
    }

    public static int d(String tag, String msg) {
        if (isLogToFile()) {
            f(tag, msg);
        }
        return showLog(DEBUG) ? android.util.Log.d(tag, formatMsg(msg)) : 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (isLogToFile()) {
            f(tag, msg, tr);
        }
        return showLog(DEBUG) ? android.util.Log.d(tag, formatMsg(msg), tr) : 0;
    }

    public static int v(String tag, String msg) {
        if (isLogToFile()) {
            f(tag, msg);
        }
        return showLog(VERBOSE) ? android.util.Log.v(tag, formatMsg(msg)) : 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (isLogToFile()) {
            f(tag, msg, tr);
        }
        return showLog(VERBOSE) ? android.util.Log.v(tag, formatMsg(msg), tr) : 0;
    }

    public static int dLstr(String tag, String msg) {
        return showLog(DEBUG) ? logLd(tag, msg) : 0;
    }

    private static boolean showLog(int debugLevel) {
        return (isLog && isDebug && filter <= debugLevel);
    }

    private static int logLd(String tag, final String msg) {
        int result = 0;
        int LEN_MAX = 3000;
        int start = 0;
        int len = msg.length();
        int end = len > LEN_MAX ? LEN_MAX : len;
        int temp = 0;
        while (start < end) {
            result = android.util.Log.d(tag, msg.substring(start, end));
            start = end;
            temp = len - end;
            end += temp > LEN_MAX ? LEN_MAX : temp;
        }
        return result;
    }

    /**
     * 打印程序调用的Task信息
     *
     * @param str
     */
    public static void printStackTrace(String str) {
        StackTraceElement st[] = Thread.currentThread().getStackTrace();
        for (int i = 0; i < st.length; i++) {
            LogHelper.d(str, i + ":" + st[i]);
        }
    }

    /**
     * 打印程序调用的Task信息
     *
     * @param str
     * @param index StackTrace起始位置
     */
    public static void printStackTrace(String str, int index) {
        StackTraceElement st[] = Thread.currentThread().getStackTrace();
        if (index < st.length) {
            for (int i = index; i < st.length; i++) {
                LogHelper.d(str, i + ":" + st[i]);
            }
        } else {
            LogHelper.d(str, "index invalid");
        }
    }

    /**
     * 打印程序调用的Task信息
     *
     * @param str
     * @param begin StackTrace起始位置
     * @param end   StackTrace结束位置
     */
    public static void printStackTrace(String str, int begin, int end) {
        int localEnd = end;
        StackTraceElement st[] = Thread.currentThread().getStackTrace();
        if (begin < st.length) {
            localEnd = localEnd < st.length ? ++localEnd : st.length;
            for (int i = begin; i < localEnd; i++) {
                LogHelper.d(str, i + ":" + st[i]);
            }
        } else {
            LogHelper.d(str, "index invalid");
        }
    }

    final static byte SPACE[] = " ".getBytes();
    final static byte NEWLINE[] = "\n".getBytes();
    final static byte COLON[] = ":".getBytes();
    final static long THREE_DAY = 3 * 24 * 60 * 60 * 1000L;
    final static long FILESIZE_15M = 15 * 1024 * 1024L;
    @SuppressLint("SimpleDateFormat")
    final static DateFormat longDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final static Object longDateFormatLock = new Object();

    synchronized public static void f(final String tag, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OutputStream os;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    os = openLogFile();
                } else {
                    os = null;
                }
                if (os == null) {
                    return;
                }
                try {
                    writeLogFile(os, tag, msg);
                } catch (IOException e) {
                } finally {
                    try {
                        os.close();
                    } catch (IOException e) {
                    }
                }
            }
        }).start();
    }

    synchronized public static void f(String tag, String msg, Throwable tr) {
        f(tag, msg + '\n' + android.util.Log.getStackTraceString(tr));
    }

    private static File buildLogFile(final String fileName) {
        return new File(Environment.getExternalStorageDirectory(), fileName);
    }

    private static OutputStream openLogFile() {
        return openLogFile("appstore.log");
    }

    private static OutputStream openLogFile(final String fileName) {
        return openLogFile(buildLogFile(fileName));
    }

    private static OutputStream openLogFile(final File f) {
        if (f.exists() && (System.currentTimeMillis() - f.lastModified() > THREE_DAY || f.length() > FILESIZE_15M)) {
            if (!f.delete()) {
                android.util.Log.e("LOG", "Fail to delete file:" + f.getAbsolutePath());
            }
        }

        try {
            return new FileOutputStream(f, true);
        } catch (FileNotFoundException e) {
        }

        return null;
    }

    public static String dateToString(Date date) {
        synchronized (longDateFormatLock) {
            return longDateFormat.format(date);
        }
    }

    /**
     * 取当前位置的堆栈信息
     *
     * @return
     */
    public static String getStack() {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        StringBuilder bulder = new StringBuilder();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
                bulder.append(stackElements[i].getClassName());
                bulder.append(stackElements[i].getFileName());
                bulder.append(stackElements[i].getLineNumber());
                bulder.append(stackElements[i].getMethodName());
                bulder.append("\n");
            }
        }
        String stack = bulder.toString();
        d("Stack", "stack:" + stack);
        return stack;
    }

    private static void writeLogFile(final OutputStream os, final String tag, final String msg) throws IOException {
        if (!TextUtils.isEmpty(tag)) {
            os.write(dateToString(new Date()).getBytes("UTF-8"));
            os.write(SPACE);

            os.write(tag.getBytes("UTF-8"));
            os.write(COLON);
        }

        if (!TextUtils.isEmpty(msg)) {
            os.write(msg.getBytes("UTF-8"));
            os.write(NEWLINE);
        }
    }

    private static final String LOGFILE_PREFERENCE_NAME = "logtofile_pref";
    private static final String LOGFILE_KEY_NAME = "logtofile";
    private static boolean logToFileInited = false;
    private static boolean logToFile = false;

    synchronized public static void initToLogFile(Context context) {
        if (!logToFileInited) {
            logToFileInited = true;
            SharedPreferences settingSp = context.getSharedPreferences(LOGFILE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            if (settingSp != null) {
                logToFile = settingSp.getBoolean(LOGFILE_KEY_NAME, false);
            }
        }
    }

    synchronized public static boolean isLogToFile() {
        return logToFile;
    }

    synchronized public static void setLogToFile(Context context, boolean enable) {
        logToFile = enable;
        SharedPreferences settingSp = context.getSharedPreferences(LOGFILE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (settingSp != null) {
            Editor settingEditor = settingSp.edit();
            settingEditor.putBoolean(LOGFILE_KEY_NAME, enable);
            settingEditor.commit();
        }
    }
}
