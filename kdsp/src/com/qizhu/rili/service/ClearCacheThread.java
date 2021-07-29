package com.qizhu.rili.service;

import android.os.Handler;
import androidx.core.app.NotificationCompat;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.LogUtils;

import java.io.File;
import java.util.Calendar;

/**
 * 清理缓存的线程
 */
public class ClearCacheThread extends Thread {
    private int mRetainDay = 1;
    private Handler mHandler;
    private boolean isOnTime = true;
    private static AppContext mContext;

    private static boolean sStopClear = false;  //是否停止清理缓存
    private static float percentCount = 0;
    private static int count = 0;
    private static int totalCount;
    private static float intervalCount = 0.5f;
    private static NotificationCompat.Builder mNotificationBuilder;

    public ClearCacheThread(AppContext context) {
        mContext = context;
    }

    public static void setStopClearCache(boolean stopClear) {
        sStopClear = stopClear;
    }

    public void setRetainDay(int mRetainDay) {
        this.mRetainDay = mRetainDay;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void setIsOnTimeClear(boolean isOnTimeClear) {
        this.isOnTime = isOnTimeClear;
    }

    @Override
    public void run() {
        try {
            sStopClear = false;
            percentCount = 0;
            count = 0;

            final File cacheDir = new File(FileUtils.getImageCacheDirPath());
            File[] files = cacheDir.listFiles();
            if (files != null) {
                totalCount = files.length;
                if (!isOnTime) {
                    mNotificationBuilder = YSRLNotifyManager.generateClearCacheNotification(mContext);
                }
                //保留最近mRetainDay天的文件
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 0 - mRetainDay);
                long retainTime = calendar.getTimeInMillis();//ms

                for (File file : files) {
                    count++;
                    if (!isOnTime) {
                        setProgress();
                    }

                    boolean toDelete = file.lastModified() < retainTime;
                    if (file.exists() && toDelete) {
                        LogUtils.d("清理缓存 delete files %s ", file.getAbsolutePath());
                        if (file.isDirectory()) {
                            FileUtils.deleteDirectory(file);
                        } else {
                            file.delete();
                        }
                    } else {
                        LogUtils.d("清理缓存 not delete file %s", file.getAbsolutePath());
                    }

                    if (sStopClear) {
                        break;
                    }
                }
                cancleNotification();
                //发送清理成功或取消的消息
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(sStopClear ? YSRLService.MSG_HANDLE_CANCEL : YSRLService.MSG_HANDLE_SUCCESS);
                }
            }
        } catch (Exception e) {
            LogUtils.d("清理缓存 缓存文件不存在");
            cancleNotification();
        }
    }

    /**
     * 显示进度
     */
    private void setProgress() {
        if (mNotificationBuilder != null) {
            if (percentCount == 0
                    || (count * 100.0f / totalCount) - intervalCount > percentCount) {
                percentCount += intervalCount;
                mNotificationBuilder.setContentTitle("清理缓存中...");
                mNotificationBuilder.setContentText((int) (count * 100.0f / totalCount) + "%");
                mNotificationBuilder.setProgress(totalCount, count, false);
                YSRLNotifyManager.notify(YSRLNotifyManager.NOTIFY_ID_CLEAR_CACHE, mNotificationBuilder.build());
            }
        }
    }

    /**
     * 取消通知
     */
    public void cancleNotification() {
        YSRLNotifyManager.cancel(YSRLNotifyManager.NOTIFY_ID_CLEAR_CACHE);
    }
}
