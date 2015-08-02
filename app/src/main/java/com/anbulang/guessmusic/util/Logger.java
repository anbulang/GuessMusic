package com.anbulang.guessmusic.util;

import android.util.Log;

/**
 * 日志工具类
 * Created by Chaucer on 2015/7/10.
 */
public class Logger {
    public static final boolean DEBUG = true;

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (DEBUG) {
            Log.i(tag, message);
        }
    }
}
