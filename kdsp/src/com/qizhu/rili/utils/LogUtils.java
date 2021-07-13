package com.qizhu.rili.utils;

import android.util.Log;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.BuildConfig;

/**
 * Log工具类
 */
public class LogUtils {
    public static boolean DEBUG = BuildConfig.LOG_DEBUG;

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(AppConfig.TAG, buildMessage(false, msg));
        }
    }

    public static void dStack(String formatStr, Object... values) {
        if (DEBUG) {
            Log.d(AppConfig.TAG, buildMessage(true, String.format(formatStr, values)));
        }
    }

    public static void d(String formatStr, Object... values) {
        if (DEBUG) {
            Log.d(AppConfig.TAG, buildMessage(false, String.format(formatStr, values)));
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(AppConfig.TAG, buildMessage(false, msg));
        }
    }

    public static void e(String msg, Throwable error) {
        if (DEBUG) {
            Log.e(AppConfig.TAG, buildMessage(false, msg), error);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(AppConfig.TAG, buildMessage(false, msg));
        }
    }

    protected static String buildMessage(boolean logStack, String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append(caller.getClassName())
                .append(".")
                .append(caller.getMethodName())
                .append("(): [")
                .append(caller.getLineNumber())
                .append("]")
                .append(msg).
                append("\n");

        if (logStack) {
            for (StackTraceElement ste : stackTraceElements) {
                sb.append(ste).append("\n");
            }
        }
        return sb.toString();
    }
}
