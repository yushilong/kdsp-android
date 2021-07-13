package com.qizhu.rili.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by lindow on 6/8/16.
 * 相机工具类
 */
public class CameraUtils {
    public static boolean checkCameraHardware(Context context) {
        // return this device has a camera
        return context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
