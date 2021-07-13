package com.qizhu.rili.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.qizhu.rili.AppManager;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 * Created by lindow
 * 异常处理类，捕获未知异常防止程序进程终止
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //单例引用，一个应用程序里面只需要一个UncaughtExceptionHandler实例
    private static CrashHandler instance;
    private Context context;

    private CrashHandler() {
    }

    //同步方法，以免单例多线程环境下出现异常
    public synchronized static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    //初始化，把当前对象设置成UncaughtExceptionHandler处理器
    public void init(Context ctx) {
        context = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {  //当有未处理的异常发生时，就会来到这里。。
        LogUtils.d("uncaughtException, thread: " + thread
                + " name: " + thread.getName() + " id: " + thread.getId() + "exception: "
                + ex);
        long threadId = thread.getId();
        LogUtils.d("Thread.getName()=" + thread.getName() + " id=" + threadId + " state="
                + thread.getState());
//        String threadName = thread.getName();

        if (LogUtils.DEBUG) {
            //将日志信息写入文件
            String info = null;
            ByteArrayOutputStream baos = null;
            PrintStream printStream = null;
            try {
                baos = new ByteArrayOutputStream();
                printStream = new PrintStream(baos);
                ex.printStackTrace(printStream);
                byte[] data = baos.toByteArray();
                info = new String(data);
                data = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (printStream != null) {
                        printStream.close();
                    }
                    if (baos != null) {
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtils.d("Error[" + info + "]");
                //将错误log保存
                File fileErrorLog = null;
                try {
                    fileErrorLog = new File(FileUtils.getUserLogDirPath() + FileUtils.getTempFileName() + ".log");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // write to ysrl/logs/{date}.log
                write2ErrorLog(fileErrorLog, info);
            }
        }
        // 对于非UI线程可显示出提示界面，如果是UI线程抛的异常则界面卡死直到ANR
        if (threadId != 1) {

//                Intent intent = new Intent(context, ActErrorReport.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("error", info);
//                intent.putExtra("by", "uehandler");
//                context.startActivity(intent);
        } else {

        }
        //清除fresco的空间
        Fresco.shutDown();
        // kill App Progress,在结束进程之前，确保结束所有的activity，这样的话释放系统里面的binder，再次进入直接重新初始化
        AppManager.getAppManager().finishAllActivity();
        //保存友盟统计数据
        MobclickAgent.onKillProcess(context);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     *
     * @param file
     * @param content
     */
    private void write2ErrorLog(File file, String content) {
        FileOutputStream fos = null;
        try {
            if (file.exists()) {
                // 清空之前的记录
                file.delete();
            } else {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 收集用户设备参数信息
     */
    private void collectDeviceInfo() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
//                infos.put("versionName", versionName);
//                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
//                infos.put(field.getName(), field.get(null).toString());
                LogUtils.d(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                LogUtils.e("an error occured when collect crash info", e);
            }
        }
    }
}
