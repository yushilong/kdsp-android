package com.qizhu.rili.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;

import com.qizhu.rili.AppContext;
import com.umeng.analytics.MobclickAgent;

/**
 * Android各版本的兼容方法
 */
public class MethodCompat {

    /**
     * 检测当前版本是否支持方法
     *
     * @param versionCode 检测版本
     */
    public static boolean isCompatible(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    /**
     * 设置view的背景图
     */
    public static void setBackground(View view, Drawable bg) {
        if (isCompatible(Build.VERSION_CODES.JELLY_BEAN)) {
            view.setBackground(bg);
        } else {
            view.setBackgroundDrawable(bg);
        }
    }

    public static void setLayerType(ImageView img, int layerType) {
        if (img != null) {
            if (isCompatible(Build.VERSION_CODES.HONEYCOMB)) {
                img.setLayerType(layerType, null);
            }
        }
    }

    /**
     * 设置view的旋转
     */
    public static void setScale(View view, float scale) {
        if (view != null) {
            if (isCompatible(Build.VERSION_CODES.HONEYCOMB)) {
                view.setRotation(scale);
            } else {
                ScaleAnimation animation = new ScaleAnimation(0.0f, scale, 0.0f, scale,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(0);//设置动画持续时间
                animation.setFillAfter(true);
                view.startAnimation(animation);
            }
        }
    }

    /**
     * 设置view的缩放
     */
    public static void setRotation(View view, float rotation) {
        if (view != null) {
            if (isCompatible(Build.VERSION_CODES.HONEYCOMB)) {
                view.setScaleX(rotation);
                view.setScaleY(rotation);
            } else {
                RotateAnimation anim = new RotateAnimation(0, rotation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(true);
                anim.setDuration(0);
                view.startAnimation(anim);
            }
        }
    }

    /**
     * 将内容复制到剪切板
     * 3.0以上是android.text.ClipboardManager的子类，android.content.ClipboardManager，2.3是android.text.ClipboardManager
     */
    public static void copyText(String text) {
        if (isCompatible(Build.VERSION_CODES.HONEYCOMB)) {
            ClipboardManager clipboardManager = (ClipboardManager) AppContext.baseContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        } else {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) AppContext.baseContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        }
    }

    /**
     * webview加载js
     * 4.4以下是webkit核心，采用loadurl即可，4.4及以上是chromium核心，采用evaluateJavascript方式加载，防止由于返回值问题的出错
     */
    public static void loadJS(WebView webview, String jsUrl) {
        LogUtils.d("load js jsurl = " + jsUrl);
        if (isCompatible(Build.VERSION_CODES.KITKAT)) {
            webview.evaluateJavascript(jsUrl, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    //此处可得到返回值，但是仍然为异步，所以目前web端全部采用向下兼容，调用本地方法传回返回值的方式
                    LogUtils.d("load js result = " + s);
                }
            });
        } else {
            webview.loadUrl(jsUrl);
        }
    }

    /**
     * 设置友盟Debug
     *
     * @param debug 是否开启debug
     */
    public static void setUmengDebug(boolean debug) {
        MobclickAgent.setDebugMode(debug);
    }

    public static int getIntFromBundle(Bundle bundle, String tag, int defaultVal) {
        int rtn = defaultVal;
        if (bundle != null && bundle.containsKey(tag)) {
            rtn = bundle.getInt(tag);
        }
        return rtn;
    }

    public static double getDoubleFromBundle(Bundle bundle, String tag, double defaultVal) {
        double rtn = defaultVal;
        if (bundle != null && bundle.containsKey(tag)) {
            rtn = bundle.getDouble(tag);
        }
        return rtn;
    }

    public static String getStringFromBundle(Bundle bundle, String tag, String defaultVal) {
        String rtn = defaultVal;
        if (bundle != null && bundle.containsKey(tag)) {
            rtn = bundle.getString(tag);
        }
        return rtn;
    }

    public static String getStringFromBundle(Bundle bundle, String tag) {
        String rtn = "";
        if (bundle != null && bundle.containsKey(tag)) {
            rtn = bundle.getString(tag);
        }
        return rtn;
    }

    public static boolean getBooleanFromBundle(Bundle bundle, String tag, boolean defaultVal) {
        boolean rtn = defaultVal;
        if (bundle != null && bundle.containsKey(tag)) {
            rtn = bundle.getBoolean(tag);
        }
        return rtn;
    }
}
