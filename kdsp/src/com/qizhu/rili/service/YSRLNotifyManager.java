package com.qizhu.rili.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.ui.activity.MainActivity;
import com.qizhu.rili.ui.activity.NewsAgentActivity;

/**
 * 通知栏管理
 */
public class YSRLNotifyManager {
    private static NotificationManager mNotifyManager;

    public static int NOTIFY_ID_DOWNLOAD = 19890111;            //下载消息
    public static int NOTIFY_ID_CLEAR_CACHE = 19890314;         //清除缓存通知
    public static int NOTIFY_ID_ALARM_BY_SELF = 19890412;       //自身提醒消息
    public static int NOTIFY_ID_ALARM_BY_PUSH = 19890419;       //推送提醒消息
    private static String CHANNEL_ID = "01";

    /**
     * 初始化消息管理器
     */
    public static NotificationManager getInstance(Context context) {
        if (mNotifyManager == null) {
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                CharSequence name = "my_channel";
                String Description = "channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
                mNotifyManager.createNotificationChannel(mChannel);
            }

        }
        return mNotifyManager;
    }

    /**
     * 生成消息的通知
     */
    public static Notification generateDailyNotice(Context context, String link, String content, int badgeNumberAndroid) {
        Intent intent = new Intent(context, NewsAgentActivity.class);
        intent.putExtra(NewsAgentActivity.EXTRA_LINK, link);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, badgeNumberAndroid, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        return new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setTicker(content)
                .setContentTitle("您有新消息")
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

    }

    /**
     * 生成本地提醒消息的通知
     */
    public static Notification generateAlarmNotification(Context context, String TipText, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setTicker(TipText)
                .setContentTitle("您有新消息")
                .setContentText(TipText)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * 生成本地提醒消息的通知
     * 同时只存在一条通知，所以用FLAG_UPDATE_CURRENT更新
     */
    public static Notification generateAlarmNotification(Context context, String title, String TipText, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setTicker(TipText)
                .setContentTitle(title)
                .setContentText(TipText)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * 生成清理缓存的通知，有进度条，因此返回builder对象
     */
    public static NotificationCompat.Builder generateClearCacheNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, buildClearCacheIntent(context, false), 0);
        PendingIntent deleteIntent = PendingIntent.getService(context, 0, buildClearCacheIntent(context, true), 0);
        return new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setTicker("清理缓存")
                .setContentTitle("清理缓存")
                .setWhen(System.currentTimeMillis())
                .setContentText("0%")
                .setContentIntent(contentIntent)
                .setDeleteIntent(deleteIntent);

    }

    /**
     * 生成下载的通知,由于下载通知需要生成进度条，因此返回builder对象
     */
    public static NotificationCompat.Builder generateDownloadNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getService(context, 0, buildDownloadIntent(context, true), 0);
        PendingIntent deleteIntent = PendingIntent.getService(context, 0, buildDownloadIntent(context, true), 0);
        return new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setTicker("下载新版本")
                .setContentTitle("正在下载新版本")
                .setWhen(System.currentTimeMillis())
                .setContentText("0%")
                .setContentIntent(contentIntent)
                .setDeleteIntent(deleteIntent);
    }

    /**
     * 下载其他应用的通知,有进度条，因此返回builder对象
     */
    public static NotificationCompat.Builder generateExternalAppDownloadNotification(Context context, String appName) {
        PendingIntent contentIntent = PendingIntent.getService(context, 0, buildDownloadIntent(context, true), 0);
        PendingIntent deleteIntent = PendingIntent.getService(context, 0, buildDownloadIntent(context, true), 0);
        return new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.extra_app_icon)
                .setTicker("开始下载" + appName)
                .setContentTitle("正在下载" + appName)
                .setWhen(System.currentTimeMillis())
                .setContentText("0%")
                .setContentIntent(contentIntent)
                .setDeleteIntent(deleteIntent);
    }

    /**
     * 构建clearCache的intent
     *
     * @param context     上下文
     * @param isStopCache 是否停止
     */
    private static Intent buildClearCacheIntent(Context context, boolean isStopCache) {
        Intent intent;
        if (!isStopCache) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.INIT_TAB_EXTRA, MainActivity.POS_MINE);
        } else {
            intent = new Intent(context, YSRLService.class);
            intent.setAction(YSRLService.ACTION_CLEAR_CACHE);
            intent.putExtra(YSRLService.EXTRA_IS_STOP, isStopCache);
        }
        return intent;
    }

    /**
     * 构建download的intent
     *
     * @param context 上下文
     * @param isStop  是否停止
     */
    private static Intent buildDownloadIntent(Context context, boolean isStop) {
        Intent intent;
        if (!isStop) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.INIT_TAB_EXTRA, MainActivity.POS_MINE);
        } else {
            intent = new Intent(context, YSRLService.class);
            intent.setAction(YSRLService.ACTION_DOWNLOAD_APK);
            intent.putExtra(YSRLService.EXTRA_IS_STOP, isStop);
        }
        return intent;
    }

    /**
     * 显示指定的通知
     *
     * @param id           通知id
     * @param notification 通知
     */
    public static void notify(int id, Notification notification) {
        getInstance(AppContext.baseContext).notify(id, notification);
    }

    /**
     * 取消显示指定的通知
     *
     * @param id 通知id
     */
    public static void cancel(int id) {
        getInstance(AppContext.baseContext).cancel(id);
    }

    /**
     * 取消显示的所有通知
     */
    public static void cancelAll() {
        //清除我们应用的所有通知
        getInstance(AppContext.baseContext).cancelAll();
    }
}
