package com.qizhu.rili.service;

import android.os.Environment;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * 下载最新版本APK的线程
 */
public class DownloadAPKThread extends Thread {
    private Handler mHandler;
    private String apkUrl;
    private String versionName;
    private static AppContext mContext;

    private static float percentCount = 0;
    private static float intervalCount = 1.0f;
    private static NotificationCompat.Builder mNotificationBuilder;

    //终止标记
    private static boolean interceptFlag = false;
    //是否正在下载
    public static boolean isDownloading = false;

    private String savePath;
    private String apkFilePath;
    private String tmpFilePath;
    private boolean isNotify = true;
    //5.3 add 下载其他应用时的标记和下载的名字
    private boolean isExternalApp = false;
    private String externalAppName;

    public DownloadAPKThread(AppContext context, String apkUrl, String versionName, boolean isNotify) {
        mContext = context;
        this.apkUrl = apkUrl;
        this.versionName = versionName;
        this.isNotify = isNotify;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public static void setInterceptDownload(boolean interceptDownload) {
        interceptFlag = interceptDownload;
    }

    @Override
    public void run() {
        LogUtils.d("apkUrl is " + apkUrl);
        if (TextUtils.isEmpty(apkUrl)) {
            //url地址为空时返回下载失败的连接
            sendEmptyMsg(YSRLService.MSG_DOWN_ERROR);
            return;
        }
        try {
            String apkName;
            //根据头部不同来区分下载的应用
            if (versionName.startsWith(AppConfig.CONFIG_EXTERNAL_APP_TAG)) {
                apkName = versionName + ".apk";
                isExternalApp = true;
                externalAppName = versionName.substring(AppConfig.CONFIG_EXTERNAL_APP_TAG.length());
            } else {
                apkName = "ysrl_" + versionName + ".apk";
            }
            String tmpApk = apkName + ".tmp";
            //判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ysrl/";
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                apkFilePath = savePath + apkName;
                tmpFilePath = savePath + tmpApk;
                LogUtils.d("apkFilePath is " + apkFilePath);
            }

            //没有挂载SD卡，无法下载文件
            if (TextUtils.isEmpty(apkFilePath)) {
                sendEmptyMsg(YSRLService.MSG_DOWN_NOSDCARD);
                return;
            }

            File ApkFile = new File(apkFilePath);

            //是否已下载更新文件
            if (ApkFile.exists()) {
                ApkFile.delete();
            }

            //输出临时下载文件
            File tmpFile = new File(tmpFilePath);
            FileOutputStream fos = new FileOutputStream(tmpFile);

            URL url = new URL(apkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();

            //显示文件大小格式：2个小数点显示
            DecimalFormat df = new DecimalFormat("0.00");
            //进度条下面显示的总文件大小
            String apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
            LogUtils.d("apkFileSize is " + apkFileSize);

            int count = 0;
            byte buf[] = new byte[1024];

            interceptFlag = false;
            //创建不同的应下载通知
            if (isExternalApp) {
                mNotificationBuilder = YSRLNotifyManager.generateExternalAppDownloadNotification(mContext, externalAppName);
            } else {
                mNotificationBuilder = YSRLNotifyManager.generateDownloadNotification(mContext);
            }
            isDownloading = true;
            percentCount = 0;
            do {
                int numread = is.read(buf);
                count += numread;
                if (isNotify) {
                    //进度条下面显示的当前下载文件大小
                    String tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    //当前进度值
                    LogUtils.d("tmpFile size is " + tmpFileSize);
                    setProgress(count, length, tmpFileSize, apkFileSize);
                }

                if (numread <= 0) {
                    //下载完成 - 将临时下载文件转成APK文件
                    if (tmpFile.renameTo(ApkFile)) {
                        LogUtils.d("Notification 下载APK completed! start install apk!");
                        //通知安装
                        setDownloadCompleted(apkFilePath);
                    }
                    break;
                }
                fos.write(buf, 0, numread);
            } while (!interceptFlag);//点击取消就停止下载
            //正在下载的标志位
            isDownloading = false;
            //被取消
            if (interceptFlag) {
                LogUtils.d("Notification 下载APK intercepted!");
                cancleNotification();
                sendEmptyMsg(YSRLService.MSG_DOWN_CANCEL);
            }

            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            isDownloading = false;
            cancleNotification();
            e.printStackTrace();
        } catch (IOException e) {
            isDownloading = false;
            cancleNotification();
            e.printStackTrace();
        }
    }

    private void sendEmptyMsg(int what) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(what);
        }
    }

    /**
     * 显示进度
     */
    private void setProgress(int count, int totalCount, String tmpFileSize, String apkFileSize) {
        if (mNotificationBuilder != null) {
            LogUtils.d("Notification 下载APK progress, count = " + count + ", totalCount = " + totalCount + ", tmpFileSize = " + tmpFileSize + ", apkFileSize = " + apkFileSize);
            if (percentCount == 0
                    || (count * 100.0f / totalCount) - intervalCount > percentCount) {
                percentCount += intervalCount;
                if (isExternalApp) {
                    mNotificationBuilder.setContentTitle("正在下载" + externalAppName + ", 请稍候~");
                    mNotificationBuilder.setContentText((int) (count * 100.0f / totalCount) + "%" + "    " + tmpFileSize + "/" + apkFileSize);
                } else {
                    mNotificationBuilder.setContentTitle("正在下载最新版本, 请稍候~");
                    mNotificationBuilder.setContentText((int) (count * 100.0f / totalCount) + "%" + "    " + tmpFileSize + "/" + apkFileSize);
                }
                mNotificationBuilder.setProgress(totalCount, count, false);
                YSRLNotifyManager.notify(YSRLNotifyManager.NOTIFY_ID_DOWNLOAD, mNotificationBuilder.build());
            }
        }
    }

    /**
     * 下载完成更新通知栏
     *
     * @param apkFilePath    下载apk的路径
     */
    private void setDownloadCompleted(String apkFilePath) {
        if (mNotificationBuilder != null && isNotify) {
            //提示用户安装
            YSRLService.installApk(mContext, apkFilePath);
            cancleNotification();
        }
    }

    /**
     * 取消通知
     */
    public void cancleNotification() {
        YSRLNotifyManager.cancel(YSRLNotifyManager.NOTIFY_ID_DOWNLOAD);
    }
}
