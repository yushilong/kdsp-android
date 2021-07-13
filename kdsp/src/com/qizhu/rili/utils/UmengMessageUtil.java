package com.qizhu.rili.utils;

import android.content.Context;
import android.content.Intent;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.service.YSRLNotifyManager;
import com.qizhu.rili.ui.activity.NewsAgentActivity;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

/**
 * Created by lindow on 15/7/10.
 * 友盟的自定义消息处理
 */
public class UmengMessageUtil {
    /**
     * 通知的自定义处理
     * 此方法由于是BroadcastReceiver调用，因此启动activity需要加入Intent.FLAG_ACTIVITY_NEW_TASK,老规矩，使用NewsAgentActivity中转
     */
    public static void handlerNotification(final Context mContext, UMessage msg) {
        LogUtils.d("Umeng Push handlerNotification msg = " + msg);
        try {
            final JSONObject data = new JSONObject(msg.custom);
            LogUtils.d("Umeng Push handlerNotification data = " + data);
            int noReadCount = data.optInt("noReadCount");
            LogUtils.d("Umeng Push handlerNotification noReadCount = " + noReadCount);
            NewsAgentActivity.goToPage(mContext, data.optString("linkUrl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息的自定义处理
     */
    public static void handlerMessage(Context mContext, UMessage msg) {
        LogUtils.d("Umeng Push handlerMessage msg = " + msg);
        try {
            JSONObject data = new JSONObject(msg.custom);
            LogUtils.d("Umeng Push handlerMessage data = " + data);
            int noReadCount = data.optInt("noReadCount");
            LogUtils.d("Umeng Push handlerMessage noReadCount = " + noReadCount);
            Intent intent = new Intent(mContext, NewsAgentActivity.class);
            intent.putExtra(IntentExtraConfig.EXTRA_NO_READCOUNT, noReadCount);
            intent.putExtra(NewsAgentActivity.EXTRA_LINK, data.optString("linkUrl"));
            YSRLNotifyManager.notify(YSRLNotifyManager.NOTIFY_ID_ALARM_BY_PUSH,
                    YSRLNotifyManager.generateAlarmNotification(mContext, data.optString("title"), data.optString("message"), intent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
