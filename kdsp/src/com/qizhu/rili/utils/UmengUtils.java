package com.qizhu.rili.utils;

import android.content.Context;

import com.qizhu.rili.AppConfig;
import com.umeng.analytics.MobclickAgent;


/**
 * 友盟相关工具类
 */
public class UmengUtils {
    public static final String APP_KEY = "23345386";

    public static final String SYSTEM_TRIBE_CONVERSATION = "sysTribe";
    public static final String SYSTEM_FRIEND_REQ_CONVERSATION = "sysfrdreq";

    //友盟自定义事件ID(以USER为后缀的只统计一次)
    private static final String EVENT_NEW_USER = "NEW_USER"; //打开APP用户数
    private static final String EVENT_REGISTER_USER = "REGISTER_USER"; //成功注册用户数
    private static final String EVENT_LOGOUT_COUNT = "LOGOUT_COUNT"; //用户登出的次数

    public static final boolean IS_PRODUCT = AppConfig.API_BASE.equals(AppConfig.API_BASE_PRODUCT);    //判断是否为线上版本

    /**
     * 设置友盟Debug标志位
     *
     * @param debug
     */
    public static void setUmengDebug(boolean debug) {
        MethodCompat.setUmengDebug(debug);
    }

    /**
     * Activity重新获得焦点时调用
     *
     * @param context
     */
    public static void onActivityResume(Context context) {
        MobclickAgent.onResume(context);
    }

    /**
     * Activity失去焦点时调用
     *
     * @param context
     */
    public static void onActivityPause(Context context) {
        MobclickAgent.onPause(context);
    }


    /**
     * 打开APP用户数
     *
     * @param context
     */
    public static void onEventNewUser(Context context) {
        if (IS_PRODUCT) {
            onEventUser(context, EVENT_NEW_USER);
        }
    }

    /**
     * 成功注册用户数
     *
     * @param context
     */
    public static void onEventRegisterUser(Context context) {
        if (IS_PRODUCT) {
            onEventUser(context, EVENT_REGISTER_USER);
        }
    }

    /**
     * 用户登出的次数
     *
     * @param context
     */
    public static void onEventLogoutCount(Context context) {
        if (IS_PRODUCT) {
            MobclickAgent.onEvent(context, EVENT_LOGOUT_COUNT);
        }
    }

    /**
     * 统计相关用户数，只统计一次
     *
     * @param context
     * @param eventId
     */
    private static void onEventUser(Context context, String eventId) {
        if (IS_PRODUCT) {
            boolean hasAddStatistics = SPUtils.getBoolleanValue(eventId, false);
            if (!hasAddStatistics) { //只统计一次
                MobclickAgent.onEvent(context, eventId);
                SPUtils.putBoolleanValue(eventId, true);
            }
        }
    }

    /**
     * 自定义错误
     *
     * @param context
     * @param error    错误内容
     */
    public static void reportError(Context context, String error) {
        if (IS_PRODUCT) {
            MobclickAgent.reportError(context, error);
        }
    }
}
