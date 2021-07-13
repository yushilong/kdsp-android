package com.qizhu.rili.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;

import com.qizhu.rili.AppContext;

import java.io.File;

/**
 * 显示工具类
 */
public class DisplayUtils {
    private static long availableMem = 0;
    private static long totalMem = 0;


    public static float dip2pxWithFloatRtn(float dpValue) {
        final float scale = AppContext.baseContext.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = AppContext.baseContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = AppContext.baseContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕高度（像素）
     */
    public static int getDisplayHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(像素)
     */
    public static int getDisplayWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getDensityDpi() {
        return AppContext.baseContext.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getColor(int color) {
        return ContextCompat.getColor(AppContext.baseContext, color);
    }

    public static boolean isHighDensity() {
        return getDensityDpi() >= 240;
    }

    //这个是手机内存的可用空间大小
    public static long getAvailableInternalMemorySize() {
        if (availableMem == 0) {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableMem = availableBlocks * blockSize;
        }
        return availableMem;
    }

    public static long getTotalInternalMemorySize() {
        if (totalMem == 0) {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalMem = totalBlocks * blockSize;
        }
        return totalMem;
    }

    // 获取状态栏高度
    public static int getStatusBarHeight() {
        return (int) (25 * AppContext.baseContext.getResources().getDisplayMetrics().density);
    }

    // 获取屏幕内容高度(除去状态栏)
    public static int getContentHeight() {
        return getDisplayHeightPixels(AppContext.baseContext) - getStatusBarHeight();
    }

}
