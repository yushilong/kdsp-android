package com.qizhu.rili.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.EventItem;
import com.qizhu.rili.ui.activity.MainActivity;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UIUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by lindow on 15/6/29.
 * 应用服务类
 */
public class YSRLService extends Service {
    private static final String TAG = "YSRLService--> ";

    private static long mCancelTime;             //进行取消操作的时间
    public static boolean mNeedNotifyed;        //是否需要通知

    public static final String ACTION_CLEAR_CACHE = "com.qizhu.service.CLEAR_CACHE";                    //手动清除缓存
    public static final String ACTION_CLEAR_CACHE_ONTIME = "com.qizhu.service.CLEAR_CACHE_ON_TIME";     //自动清除缓存
    public static final String ACTION_DOWNLOAD_APK = "com.qizhu.service.DOWNLOAD_APK";                  //下载apk
    public static final String ACTION_INSTALL_APK = "com.qizhu.service.INSTALL_APK";                    //安装apk
    public static final String ACTION_ALARM = "com.qizhu.service.ALARM";                                //用于客户端自身的一些提醒操作

    public static final String EXTRA_IS_STOP = "extra_is_stop";
    public static final String EXTRA_APK_URL = "extra_apk_url";
    public static final String EXTRA_VERSION_NAME = "extra_version_name";
    public static final String EXTRA_ALARM_ORIGIN = "extra_alarm_origin";               //自身提醒的来源，用于定制不同的提醒
    public static final String EXTRA_IS_NOTIFY = "extra_is_notify";

    public static final int MSG_HANDLE_SUCCESS = 1;         //处理成功
    public static final int MSG_HANDLE_CANCEL = 2;          //取消
    public static final int MSG_DOWN_NOSDCARD = 3;          //无SD卡
    public static final int MSG_DOWN_ERROR = 4;             //下载失败
    public static final int MSG_DOWN_CANCEL = 5;            //下载取消

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HANDLE_SUCCESS:
                    UIUtils.toastMsg("清理缓存成功");
                    BroadcastUtils.sendClearCacheSuccessBroadcast();
                    break;
                case MSG_HANDLE_CANCEL:
                    UIUtils.toastMsg("清理缓存被终止");
                    break;
                case MSG_DOWN_CANCEL:
                    UIUtils.toastMsg("已取消下载");
                    break;
                case MSG_DOWN_NOSDCARD:
                    UIUtils.toastMsg("无法下载安装文件，请检查SD卡是否挂载");
                    break;
                case MSG_DOWN_ERROR:
                    UIUtils.toastMsg("更新失败！");
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG + "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("YSRLService onStartCommand!");

        //根据Intent启动相应的线程
        startThreadByIntent(intent);

        return START_STICKY;
    }

    /**
     * 根据Intent启动相应的线程
     */
    private void startThreadByIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            LogUtils.d(TAG + "intent action === " + action);

            if (ACTION_CLEAR_CACHE_ONTIME.equals(action)) {    //定时清理缓存

                LogUtils.d("定时清理缓存 start!");
                //清理缓存
                ClearCacheThread clearCacheThread = new ClearCacheThread(AppContext.baseContext);
                clearCacheThread.setRetainDay(7);  //删除7天前的缓存
                clearCacheThread.start();

            } else if (ACTION_CLEAR_CACHE.equals(action)) {       //手动清理缓存

                boolean isStopClear = intent.getBooleanExtra(EXTRA_IS_STOP, false);
                LogUtils.d("Notification 清理缓存 start, isStopClear = " + isStopClear);
                ClearCacheThread.setStopClearCache(isStopClear);

                if (!isStopClear) {
                    ClearCacheThread clearCacheThread = new ClearCacheThread(AppContext.baseContext);
                    clearCacheThread.setRetainDay(0);  //删除1天前的缓存
                    clearCacheThread.setHandler(handler);
                    clearCacheThread.setIsOnTimeClear(false);
                    clearCacheThread.start();
                }
                CookieSyncManager.createInstance(this);
                //删除cookie
                CookieManager.getInstance().removeAllCookie();
                CookieSyncManager.getInstance().sync();
                AppContext.threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //清除webview的cookie
                        SPUtils.putBoolleanValue(YSRLConstants.NEED_CLEAR_WEBVIEW_CACHE, true);
                        //清除cache目录
                        FileUtils.deleteDirectory(new File(FileUtils.getUserCacheDirPath()));
                        //通知更新相册
                        BroadcastUtils.sendUpdateGalleryBroadcast();
                    }
                });
            } else if (ACTION_DOWNLOAD_APK.equals(action)) {        //下载apk
                boolean isNotify = intent.getBooleanExtra(EXTRA_IS_NOTIFY, true);

                boolean isStop = intent.getBooleanExtra(EXTRA_IS_STOP, false);
                LogUtils.d("Notification 下载APK start, isStop = " + isStop);
                DownloadAPKThread.setInterceptDownload(isStop);

                if (!isStop) {
                    String apkUrl = intent.getStringExtra(EXTRA_APK_URL);
                    String versionName = intent.getStringExtra(EXTRA_VERSION_NAME);
                    DownloadAPKThread downloadThread = new DownloadAPKThread(AppContext.baseContext, apkUrl, versionName, isNotify);
                    downloadThread.setHandler(handler);
                    downloadThread.start();
                }

            } else if (ACTION_INSTALL_APK.equals(action)) {      //安装apk
                String apkFilePath = intent.getStringExtra("apkFilePath");
                if (!TextUtils.isEmpty(apkFilePath)) {
                    installApk(AppContext.baseContext, apkFilePath);
                }
            } else if (ACTION_ALARM.equals(action)) {               //实现自身的定时提醒
                EventItem item = intent.getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
                if (item != null) {
                    Intent newIntent = new Intent(this, MainActivity.class);

                    YSRLNotifyManager.notify(YSRLNotifyManager.NOTIFY_ID_ALARM_BY_SELF, YSRLNotifyManager.generateAlarmNotification(this, item.title, "", newIntent));
                }
            }
        }
    }

    /**
     * 启动闹钟消息提醒
     * 根据时间设置不同的requestCode来区分不同的提醒事件
     * 设置一个不重复的算法，将时间与事件的index拼凑从而形成一个唯一的requestCode来保证唯一
     *
     * @param context context
     */
    public static boolean startAlarm(Context context, EventItem item) {
        LogUtils.d("YSRLService startAlarm time = " + item.time);
        long time = item.time;
        int requestCode = (int) (time - new DateTime(2015, 0, 0, 0, 0, 0).getTime()) * 100 + item.index;

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, YSRLService.class);
        intent.setAction(YSRLService.ACTION_ALARM);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, item);
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        return true;
    }

    /**
     * 取消闹钟消息提醒
     *
     * @param context context
     */
    public static boolean stopAlarm(Context context, EventItem item) {
        long time = item.time;
        int requestCode = (int) (time - new DateTime(2015, 0, 0, 0, 0, 0).getTime()) * 100 + item.index;
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, YSRLService.class);
        intent.setAction(YSRLService.ACTION_ALARM);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, item);
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);

        return true;
    }

    /**
     * 下载APK
     */
    public static void startDownloadAPK(Context context, String apkUrl, String versionName, boolean isNotify) {
        if (!DownloadAPKThread.isDownloading) {
            if (isNotify) {
                UIUtils.toastMsg("开始下载 ...");
            }
            Intent intent = new Intent(context, YSRLService.class);
            intent.setAction(ACTION_DOWNLOAD_APK);
            intent.putExtra(EXTRA_IS_STOP, false);
            intent.putExtra(EXTRA_APK_URL, apkUrl);
            intent.putExtra(EXTRA_VERSION_NAME, versionName);
            intent.putExtra(EXTRA_IS_NOTIFY, isNotify);
            context.startService(intent);
        } else {
            UIUtils.toastMsg("正在下载中 ...");
        }
    }

    /**
     * 检查apk安装包是否可以安装
     */
    public static boolean isApkCanInstall(Context context, String apkFilePath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            return info != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查本地是否已下载
     */
    public static String checkLocalAppExsit(String appName) {
        String apkName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ysrl/" + appName + ".apk";
        File apkFile = new File(apkName);
        if (apkFile.exists() && apkFile.length() > 0) {
            return apkFile.getAbsolutePath();
        }
        return "";
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, String apkFilePath) {
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        if (!isApkCanInstall(context, apkFilePath)) {
            UIUtils.toastMsgByStringResource(R.string.install_fail_tips);
            apkfile.delete();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        LogUtils.d("YSRLService installApk  " );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".fileprovider",
                    apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            LogUtils.d("YSRLService installApk provider " );
        } else {
//             Uri uri = Uri.fromFile(apkfile);
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            LogUtils.d("YSRLService installApk file " );
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 启动后台每日清理缓存
     */
    public static void startClearCacheRemind(final Context context) {
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("YSRLService startClearCacheRemind");
                Intent intent = new Intent(context, YSRLService.class);
                intent.setAction(YSRLService.ACTION_CLEAR_CACHE_ONTIME);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

                AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                //随机到0:00到2:00
                Random random = new Random();
                int time = random.nextInt(2 * 60 * 60);
                //将传入的时间换成小时和分钟
                int mHour = DateUtils.getHourFromIntTime(time);
                int mMinute = DateUtils.getMinuteFromIntTime(time);
                //定时清理缓存，每天晚上0点到2点
                calendar.set(Calendar.HOUR_OF_DAY, mHour);
                calendar.set(Calendar.MINUTE, mMinute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                //当现在时间比定时的时间大"轮询的时间"以上，则将定时时间推迟到下一天
                if (System.currentTimeMillis() - calendar.getTimeInMillis() > AppConfig.INTERVAL_THREAD_SLEEP) {
                    calendar.add(Calendar.HOUR, 24);
                }

                // 设定每天在指定的时间运行alert
                manager.setRepeating(AlarmManager.RTC,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        });
    }

    /**
     * 手动清理缓存
     */
    public static void startClearCache(Context context) {
        UIUtils.toastMsg("开始清理缓存 ...");
        Intent intent = new Intent(context, YSRLService.class);
        intent.setAction(ACTION_CLEAR_CACHE);
        intent.putExtra(EXTRA_IS_STOP, false);
        context.startService(intent);
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG + "onDestroy");
        LogUtils.d("YSRLService onDestroy!");
        //服务关闭时，重新启动服务
        YSRLService.startClearCacheRemind(this);
    }
}
