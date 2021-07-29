package com.qizhu.rili.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;

/**
 * 广播工具类
 */
public class BroadcastUtils {
    public static final String ACTION_FOLLOW_USER_BROADCAST = "action_follow_user_broadcast";       //关注发生变化
    public static final String ACTION_UPDATE_USER_DATA = "action_update_user_data";                 //用户信息更新
    public static final String ACTION_CLEAR_CACHE_BROADCAST = "action_clear_cache_broadcast";       //清除缓存
    public static final String ACTION_APPSTART_REDIRECT_BROADCAST = "app_start_redirect";           //应用进入跳转
    public static final String ACTION_COLLECT_DAILY_BROADCAST = "app_collect_daily";                //收藏日报
    public static final String ACTION_SIGN_LIKE_CHANGED = "action_sign_like_changed";               //签文状态更改
    public static final String ACTION_PAY_SUCCESS = "action_pay_success";                           //打赏成功
    public static final String ACTION_UNREAD_CHANGE = "action_unread_change";                       //未读数发生变化
    public static final String ACTION_LOGIN_SUCCESS = "action_login_success";                       //登录成功
    public static final String ACTION_REFRESH = "action_refresh_success";                           //刷新
    public static final String ACTION_VOICE_TIME = "action_voice_time";                           //语音时间
    public static final String ACTION_VOICE_POSITION = "action_voice_position";                     //语音时间
    public static final String ACTION_VOICE_STOP= "action_voice_stop";                              //语音停止
    public static final String ACTION_COLLECT_REFRESH= "action_collect_refresh";                    //收藏刷新

    private static LocalBroadcastManager localBroadcastManager;

    public static LocalBroadcastManager getInstance() {
        if (localBroadcastManager == null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(AppContext.baseContext);
        }
        return localBroadcastManager;
    }

    /**
     * 发送加关注/取消关注
     */
    public static void sendFollowUserBroadcast(String userId, boolean isFollow) {
        Intent intent = new Intent(ACTION_FOLLOW_USER_BROADCAST);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, userId);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, isFollow);
        getInstance().sendBroadcast(intent);
    }

    /**
     * 发送用户信息更改的广播
     */
    public static void sendUpdateUserDataBroadcast() {
        getInstance().sendBroadcast(new Intent(ACTION_UPDATE_USER_DATA));
    }

    /**
     * 发送清理缓存的广播
     */
    public static void sendClearCacheSuccessBroadcast() {
        getInstance().sendBroadcast(new Intent(ACTION_CLEAR_CACHE_BROADCAST));
    }

    /**
     * 发送未读数发生变化的广播
     */
    public static void sendUnreadChangeBroadcast() {
        getInstance().sendBroadcast(new Intent(ACTION_UNREAD_CHANGE));
    }

    /**
     * 发送通知相册更新内容的广播
     */
    public static void sendUpdateGalleryBroadcast() {
        /**
         * 注释: android 4.4以上 不允许发送Intent.ACTION_MEDIA_MOUNTED 可用MediaScannerConnection or ACTION_MEDIA_SCANNER_SCAN_FILE代替
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            AppContext.baseContext.sendBroadcast(new Intent
                    (Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } else {
            AppContext.baseContext.sendBroadcast(new Intent
                    (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    /**
     * 发送收藏/取消收藏
     */
    public static void sendCollectDailyBroadcast(String dailyId, boolean isCollect) {
        Intent intent = new Intent(ACTION_COLLECT_DAILY_BROADCAST);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, dailyId);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, isCollect);
        getInstance().sendBroadcast(intent);
    }

    /**
     * 发送收藏/取消收藏
     */
    public static void sendLikeSignBroadcast(String shakid, int islike) {
        Intent intent = new Intent(ACTION_SIGN_LIKE_CHANGED);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, shakid);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, islike);
        getInstance().sendBroadcast(intent);
    }

    /**
     * 支付成功
     */
    public static void sendPaySuccessBroadcast(String id) {
        Intent intent = new Intent(ACTION_PAY_SUCCESS);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, id);
        getInstance().sendBroadcast(intent);
    }

    /**
     * 登录成功
     */
    public static void sendLoginSuccessBroadcast(String json) {
        Intent intent = new Intent(ACTION_LOGIN_SUCCESS);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, json);
        getInstance().sendBroadcast(intent);
    }

    /**
     * 刷新
     */
    public static void sendRefreshBroadcast(String id) {
        Intent intent = new Intent(ACTION_REFRESH);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, id);
        getInstance().sendBroadcast(intent);
    }

    /**
     * 刷新
     */
    public static void sendVoiceTimeBroadcast(String action,int time) {
        Intent intent = new Intent(action);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, time);
        getInstance().sendBroadcast(intent);
    }

    /**
     * 刷新
     */
    public static void sendBroadcast(String action) {
        Intent intent = new Intent(action);
        getInstance().sendBroadcast(intent);
    }
}
