package com.qizhu.rili;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import androidx.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.pgyer.pgyersdk.PgyerSDKManager;
import com.pgyer.pgyersdk.pgyerenum.Features;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.db.YSRLDataBaseHelper;
import com.qizhu.rili.db.YSRLUserDBHandler;
import com.qizhu.rili.service.CrashHandler;
import com.qizhu.rili.service.YSRLNotifyManager;
import com.qizhu.rili.service.YSRLService;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.ui.activity.MainActivity;
import com.qizhu.rili.ui.dialog.PointDialogFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.LuckyAliasUtils;
import com.qizhu.rili.utils.MD5Utils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.SSOSinaUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.UmengMessageUtil;
import com.qizhu.rili.utils.WeixinUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.qizhu.rili.YSRLConstants.SERVICE_POLICY_AGREE;

/**
 * 应用全局上下文
 */
public class AppContext extends Application {
    public static final int LOAD_DATA_MESSAGE = 1;              //全局加载信息
    public static final int CUSTOM_TOAST_MESSAGE = 2;           //自定义Toast信息
    public static final int START_CLEAR_CACHE = 3;              //主线程startClearCacheRemind
    public static final int GET_UNREAD_COUNT = 4;               //获取未读消息

    public static AppContext baseContext;
    public static String userId = "";                       //当前的用户id
    public static String session;                           //当前的session
    public static User mUser = null;                        //当前用户
    public static YSRLDataBaseHelper mDBHelper = null;      //db helper
    public static HashMap<String, String> mSolarHoliday = new HashMap<String, String>();        //阳历的节日
    public static HashMap<String, String> mLunarHoliday = new HashMap<String, String>();        //阴历的节日
    public static HashMap<String, String> mLunarSpecialHoliday = new HashMap<String, String>();        //农历特殊日子


    public static boolean isOnCreated = false;      //判断AppContext是否走完了onCreate
    public static boolean hasNewVersion = false;    //是否有版本更新
    public static boolean isNeedUpdateUserData = true;  //是否需要更新个人主页界面数据 默认第一次需要更新

    public static String version = "";         //当前应用版本
    public static int versionCode;             //应用版本号
    public static String channel;              //渠道名
    public static String imei;                 //手机imei号
    public static String macAddress;           //手机wifi mac地址

    public static Handler mAppHandler;              //应用全局handler，处理全局事务

    public static int mScreenWidth;                 //全局记录屏幕的宽度
    public static int mScreenHeight;                //全局记录屏幕的高度

    public static int mUnReadWaiting;               //未读待回复消息
    public static int mUnReadReply;                 //未读已回复消息
    public static int mUnReadFeedback;              //未读反馈消息
    public static int mUnReadTestFont;              //测字反馈消息
    public static int mCartCount;                   //购物车数量
    public static int mWaitPay;                     //待付款
    public static int mWaitSend;                    //待发货
    public static int mAlreadySend;                 //待收货
    public static int mPointSum;                    //福豆
    public static int mAddressNum;                  //地址数
    public static String mFengShuiUrl;                  //风水地址

    private PushAgent mPushAgent;                   //推送agent

    public static String festivals[] = {
            "小寒",
            "大寒",
            "立春",
            "雨水",
            "惊蛰",
            "春分",
            "清明",
            "谷雨",
            "立夏",
            "小满",
            "芒种",
            "夏至",
            "小暑",
            "大暑",
            "立秋",
            "处暑",
            "白露",
            "秋分",
            "寒露",
            "霜降",
            "立冬",
            "小雪",
            "大雪",
            "冬至",
            "春节",//1.1
            "元宵",//1.15
            "头牙",//2.2
            "端午节",//5.5
            "七夕节",//7.7
            "中元节",//7.15
            "中秋节",//8.15
            "重阳节",//9.9
            "腊八节",//12.8
            "祭灶",//12.23
            "除夕",
    };
    public static String lunarFestivals[] = {
            "1.1",
            "1.9",
            "1.20",
            "2.15",
            "2.19",
            "2.21",
            "3.15",
            "3.16",
            "3.23",
            "4.4",
            "4.8",
            "4.28",
            "5.13",
            "6.3",
            "7.13",
            "7.18",
            "7.30",
            "8.22",
            "10.5",
            "11.17",

    };


    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 15, TimeUnit.SECONDS,
            new ArrayBlockingQueue(3), new DefaultThreadFactory("ysrl-"), new ThreadPoolExecutor.DiscardOldestPolicy());


    public void initAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

        if (shouldInit()) {
//            baseContext = this;

            Fresco.shutDown();
            Fresco.initialize(baseContext);

            //初始化捕获异常handler
            initCrashHandler();
            LogUtils.d("启动页: onCreate in AppContext start!");

            init();

            LogUtils.d("启动页: onCreate in AppContext end!");
        } else {
            Log.d(AppConfig.TAG, "onCreate in AppContext start for Umeng Push Service");
        }

        //友盟推送
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, "", "", UMConfigure.DEVICE_TYPE_PHONE, "9fb14d7f356db2abaaaaa77ac7715a17");
        initUmengPush();

//搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseContext = this;
        boolean isAgree = SPUtils.getBoolleanValue(SERVICE_POLICY_AGREE);
        if (isAgree) {
            initAll();
        }
    }

    /**
     * 友盟推送处于另一进程，因此AppContext会初始化2次，取本进程即可
     */
    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void doInit() {
        Looper.prepare();

        //获取版本号和渠道名
        getVersion();
        getAppChannel();

        LogUtils.d("启动页 init, APPCONTEXT VERSION = " + version + ", CHANNEL = " + channel);

        getHolidays();

        //初始化数据库
        initDb();

        //获取屏幕的宽高，以全局调用而无需每次调用api
        getScreenWidthAndHeight();

        //初始化用户信息
        initUser();

        //初始化表情
//        SmileyManager.init(baseContext);

        //注册appId到微信
        WeixinUtils.getInstance().regToWx(baseContext);
        //注册app到微博
        SSOSinaUtils.getInstance().regToSina(baseContext);

        //腾讯bugly
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(baseContext);
        strategy.setAppChannel(channel);    //设置渠道
        strategy.setAppReportDelay(5000);   //设置SDK处理延时，5秒
        CrashReport.initCrashReport(baseContext, "900009923", false, strategy);
        isOnCreated = true;
        Looper.loop();
    }

    /**
     * 初始化操作
     */
    private void init() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                LogUtils.d("启动页 init in AppContext!");
                doInit();
                return null;
            }
        }.execute();
    }

    private void initUmengPush() {

        mPushAgent = PushAgent.getInstance(this);

//        if(MiPushRegistar.checkDevice(this)){
//            MiPushRegistar.register(this, "2882303761517308008", "5281730839008");
//            LogUtils.d("Umeng Push MiPushRegistar");
//        }
////
//       HuaWeiRegister.register(this);

//        mPushAgent.setDebugMode(LogUtils.DEBUG);
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                LogUtils.d("Umeng Push deviceToken = " + deviceToken);
                LogUtils.d("Umeng Push getDeviceId = " + getDeviceId());
                //设置alias
                if (!TextUtils.isEmpty(deviceToken)) {
                    try {

//                        mPushAgent.deleteAlias(AppContext.userId, AppConfig.ALIAS_USER_ID, new UTrack.ICallBack(){
//
//                            @Override
//                            public void onMessage(boolean isSuccess, String message) {
//
//                                LogUtils.d("Umeng Push AppContext.mUser.userId remove = " + ", success = " + isSuccess);
//                            }
//
//                        });
                        mPushAgent.addAlias(AppContext.userId, AppConfig.ALIAS_USER_ID, new UTrack.ICallBack() {

                            @Override
                            public void onMessage(boolean b, String s) {
                                LogUtils.d("Umeng Push addAlias, alias = " + s + ", success = " + b);
                            }
                        });

                        LogUtils.d("Umeng Push AppContext.mUser.userId = " + AppContext.mUser.userId);
                    } catch (Exception e) {
                        LogUtils.d("Umeng Push error = " + e.toString());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtils.d("Umeng Push onFailure = " + s + "," + s1);
            }
        });


        //通知的自定义处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                LogUtils.d("Umeng Push UmengNotificationClickHandler msg = " + msg);
                UmengMessageUtil.handlerNotification(context, msg);
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //消息的自定义处理
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                LogUtils.d("Umeng Push messageHandler msg = " + msg);
                UTrack.getInstance(baseContext).trackMsgClick(msg);
                UmengMessageUtil.handlerMessage(context, msg);
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Fresco.shutDown();
        if (mDBHelper != null) {
            OpenHelperManager.releaseHelper();
            LogUtils.d("APPCONTEXT onTerminate!");
            mDBHelper = null;
        }
    }

    /**
     * 初始化异常处理，捕获未知异常防止程序停止运行
     */
    private void initCrashHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        //在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器
        handler.init(AppContext.baseContext);
    }

    public void getHolidays() {
        //阳历节日
        mSolarHoliday.put("1.1", "元旦");
        mSolarHoliday.put("2.14", "情人节");
        mSolarHoliday.put("3.8", "妇女节");
        mSolarHoliday.put("3.12", "植树节");
        mSolarHoliday.put("3.15", "消费者日");
        mSolarHoliday.put("4.1", "愚人节");
        mSolarHoliday.put("4.22", "地球日");
        mSolarHoliday.put("5.1", "劳动节");
        mSolarHoliday.put("5.4", "青年节");
        mSolarHoliday.put("5.12", "护士节");
        mSolarHoliday.put("6.1", "儿童节");
        mSolarHoliday.put("6.5", "环境日");
        mSolarHoliday.put("6.23", "奥林匹克日");
        mSolarHoliday.put("7.1", "建党节");
        mSolarHoliday.put("8.1", "建军节");
        mSolarHoliday.put("9.3", "抗战胜利日");
        mSolarHoliday.put("9.10", "教师节");
        mSolarHoliday.put("10.1", "国庆节");
        mSolarHoliday.put("12.1", "艾滋病日");

        //阴历节日
        mLunarHoliday.put("腊月初八", "腊八节");
        mLunarHoliday.put("腊月廿三", "祭灶");
        mLunarHoliday.put("正月初一", "春节");
        mLunarHoliday.put("正月十五", "元宵");
        mLunarHoliday.put("二月初二", "头牙");
        mLunarHoliday.put("五月初五", "端午节");
        mLunarHoliday.put("七月初七", "七夕节");
        mLunarHoliday.put("七月十五", "中元节");
        mLunarHoliday.put("八月十五", "中秋节");
        mLunarHoliday.put("九月初九", "重阳节");
        mLunarHoliday.put("十月初一", "寒衣节");
        mLunarHoliday.put("十月十五", "下元节");
        mLunarHoliday.put("十一月初五", "冬至");
        mLunarHoliday.put("除夕", "除夕");
        mLunarHoliday.put("小寒", "小寒");
        mLunarHoliday.put("大寒", "大寒");
        mLunarHoliday.put("立春", "立春");
        mLunarHoliday.put("雨水", "雨水");
        mLunarHoliday.put("惊蛰", "惊蛰");
        mLunarHoliday.put("春分", "春分");
        mLunarHoliday.put("清明", "清明");
        mLunarHoliday.put("谷雨", "谷雨");
        mLunarHoliday.put("立夏", "立夏");
        mLunarHoliday.put("小满", "小满");
        mLunarHoliday.put("芒种", "芒种");
        mLunarHoliday.put("夏至", "夏至");
        mLunarHoliday.put("小暑", "小暑");
        mLunarHoliday.put("大暑", "大暑");
        mLunarHoliday.put("立秋", "立秋");
        mLunarHoliday.put("处暑", "处暑");
        mLunarHoliday.put("白露", "白露");
        mLunarHoliday.put("秋分", "秋分");
        mLunarHoliday.put("寒露", "寒露");
        mLunarHoliday.put("霜降", "霜降");
        mLunarHoliday.put("立冬", "立冬");
        mLunarHoliday.put("小雪", "小雪");
        mLunarHoliday.put("大雪", "大雪");
        mLunarHoliday.put("冬至", "冬至");

        mLunarSpecialHoliday.put("正月初一", "1.1");
        mLunarSpecialHoliday.put("正月初九", "1.9");
        mLunarSpecialHoliday.put("正月二十", "1.20");
        mLunarSpecialHoliday.put("二月十五", "2.15");
        mLunarSpecialHoliday.put("二月十九", "2.19");
        mLunarSpecialHoliday.put("二月廿一", "2.21");
        mLunarSpecialHoliday.put("三月十五", "3.15");
        mLunarSpecialHoliday.put("三月十六", "3.16");
        mLunarSpecialHoliday.put("三月廿三", "3.23");
        mLunarSpecialHoliday.put("四月初四", "4.4");
        mLunarSpecialHoliday.put("四月初八", "4.8");
        mLunarSpecialHoliday.put("四月廿八", "4.28");
        mLunarSpecialHoliday.put("五月十三", "5.13");
        mLunarSpecialHoliday.put("六月初三", "6.3");
        mLunarSpecialHoliday.put("七月十三", "7.13");
        mLunarSpecialHoliday.put("七月十八", "7.18");
        mLunarSpecialHoliday.put("七月三十", "7.30");
        mLunarSpecialHoliday.put("十月初五", "10.5");
        mLunarSpecialHoliday.put("十一月十七", "11.17");
    }

    /**
     * 获取应用版本信息
     */
    private void getVersion() {
        PackageInfo packageInfo = getPackageInfo();
        if (packageInfo != null) {
            version = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        }
    }

    /**
     * 获取App安装包信息
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("package error", e);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    /**
     * 获取应用渠道应用
     */
    public void getAppChannel() {
        channel = "web";
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            channel = info.metaData.getString("UMENG_CHANNEL");
            //360和91被系统处理为int型，因此getString会返回为null，此时抓取int型转之
            if (TextUtils.isEmpty(channel)) {
                channel = String.valueOf(info.metaData.getInt("UMENG_CHANNEL"));
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("getAppChannel error", e);
        }
    }

    /**
     * 获取手机宽高信息
     */
    public void getScreenWidthAndHeight() {
        mScreenWidth = DisplayUtils.getDisplayWidthPixels(baseContext);
        mScreenHeight = DisplayUtils.getDisplayHeightPixels(baseContext);
        LogUtils.d("---> 手机屏幕宽 mScreenWidth = " + mScreenWidth + " 高 mScreenHeight =" + mScreenHeight);
    }

    /**
     * 获取手机的宽度
     */
    public static int getScreenWidth() {
        if (mScreenWidth <= 0) {
            mScreenWidth = DisplayUtils.getDisplayWidthPixels(baseContext);
        }
        return mScreenWidth;
    }

    /**
     * 获取手机的高度
     */
    public static int getScreenHeight() {
        if (mScreenHeight <= 0) {
            mScreenHeight = DisplayUtils.getDisplayHeightPixels(baseContext);
        }
        return mScreenHeight;
    }

    /**
     * 初始化本地数据库
     */
    private void initDb() {
        if (mDBHelper == null) {
            mDBHelper = OpenHelperManager.getHelper(this, YSRLDataBaseHelper.class);
        }
    }

    /**
     * 获取数据库帮助类
     */
    public static YSRLDataBaseHelper getDBHelper() {
        if (mDBHelper == null) {
            LogUtils.d(" ---------- 同步获取数据库");
            mDBHelper = OpenHelperManager.getHelper(baseContext, YSRLDataBaseHelper.class);
        }
        return mDBHelper;
    }

    /**
     * 获取device id
     * 如果获取失败，则返回空(极少量机型注册权限无效)
     */
    public static String getDeviceId() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) baseContext.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取device udid
     */
    public static String getDeviceUdId() {
        imei = "";
        imei = getDeviceId();
        //在wifi未开启状态下，仍然可以获取MAC地址
        macAddress = "";
        try {
            WifiManager wifiMgr = (WifiManager) baseContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
            if (null != info) {
                macAddress = info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtils.d("---> 手机 imei = " + imei + " wifi mac =" + macAddress);
        return MD5Utils.MD5(imei + macAddress);
    }

    public static Handler getAppHandler() {
        if (mAppHandler == null) {
            mAppHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case LOAD_DATA_MESSAGE:
                            removeMessages(LOAD_DATA_MESSAGE);
                            break;
                        case CUSTOM_TOAST_MESSAGE:
                            removeMessages(CUSTOM_TOAST_MESSAGE);
                            if (msg.obj != null) {
                                String toastMsg = String.valueOf(msg.obj);
                                UIUtils.toastMsgByHandler(toastMsg, msg.arg1);
                            }
                            break;
                        case START_CLEAR_CACHE:
                            removeMessages(START_CLEAR_CACHE);
                            YSRLService.startClearCacheRemind(baseContext);
                            break;
                        case GET_UNREAD_COUNT:
                            removeMessages(GET_UNREAD_COUNT);
                            getUnreadCount();
                            refreshUnread();
                            break;
                    }
                }
            };
        }
        return mAppHandler;
    }

    /**
     * 初始化用户信息
     * 匿名用户直接本地生成一个用户即可，但为了统计，要将其信息传到服务器
     */
    public void initUser() {
        //读取配置中的userId
        userId = SPUtils.getStringValue(AppConfig.CURRENT_USER_ID);
        LogUtils.d("启动页 initUser, CURRENT USER_ID initUser get: userId = " + userId);

        if (!TextUtils.isEmpty(userId)) {
            //先通过userId从数据库中取出用户数据，然后从server同步用户的最新数据
            LogUtils.d("initUser get user info from db ");
            mUser = YSRLUserDBHandler.getUser(userId);

            LogUtils.d("getUser (in AppContext initUser) from db, user = " + mUser);
            //按道理，有userId的情况并不会发生从数据库取出为null的情况，但是友盟上发现这个概率并不低
            if (mUser == null) {
                //如果当前用户为空，且已经联网，则从服务器获取用户信息，同步一次数据
                LogUtils.d("get user info from server ");
                KDSPApiController.getInstance().getUserInfoByUserId(new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        if (response != null) {
                            User user = User.parseObjectFromJSON(response.optJSONObject("user"));
                            doAfterLogin(user);
                        }
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        LogUtils.d("handleAPIFailureMessage initUser get user info from db ");
                        mUser = YSRLUserDBHandler.getUser(userId);
                        if (mUser == null) {
                            mUser = new User();
                            mUser.userId = userId;
                            mUser.userSex = SPUtils.getIntValue(YSRLConstants.USER_SEX, 1);
                            mUser.birthTime = DateUtils.toDate(SPUtils.getStringValue(YSRLConstants.USER_BIRTH));
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        new PgyerSDKManager.Init().setContext(this).setApiKey("a2d2e90d5345ab203a3debbd7c398ae6").setFrontJSToken("6951ceeb07147dd379c2de6b354f9c12").enable(Features.CHECK_UPDATE).start();
    }

    /**
     * 调用user.init接口之后动作集成
     */
    public static void doUserInit(final JSONObject data) {
        SPUtils.putStringValue(YSRLConstants.QINIU_TOKEN, data.optString("qiniuToken"));
        if (!TextUtils.isEmpty(userId)) {
            CrashReport.setUserId(userId);
        }
//        JSONArray startupImage = data.optJSONArray("startupImages");
//
//        if (startupImage != null) {
//
//            KDSPStartupImageDBHandler.insertImageListAfterDelete(StartupImage.parseListFromJSON(startupImage));
//        }else {
//
//            KDSPStartupImageDBHandler.deleteImage();
//        }

        //保存吉日别名列表版本号
        LuckyAliasUtils.updateVersion(AppContext.baseContext, data.optInt("luckydayAliasVersion"));
        //签到和看手相是否上线
        SPUtils.putIntValue(YSRLConstants.SIGN_ONLINE, data.optInt("isOnlinePalmAndSign"));
        int point = data.optInt("point");
        if (point > 0) {
            Activity activity = AppManager.getAppManager().currentActivity();
            if (activity instanceof BaseActivity) {
                ((BaseActivity) activity).showDialogFragment(PointDialogFragment.newInstance(point), "领取福豆对话框");
            }
        }
    }

    private static void getUnreadCount() {
        KDSPApiController.getInstance().getItemMsgCount(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mUnReadWaiting = response.optInt("replay_before");
                mUnReadReply = response.optInt("replay_after");
                mUnReadFeedback = response.optInt("feedback_count");
                mUnReadTestFont = response.optInt("test_font");
                mWaitPay = response.optInt("waitPay");
                mWaitSend = response.optInt("waitSend");
                mAlreadySend = response.optInt("alreadySend");
                //延迟60s之后继续请求
                mAppHandler.sendEmptyMessageDelayed(GET_UNREAD_COUNT, AppConfig.DELAY_GET_UNREAD);
                BroadcastUtils.sendUnreadChangeBroadcast();
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                //延迟60s之后继续请求
                mAppHandler.sendEmptyMessageDelayed(GET_UNREAD_COUNT, AppConfig.DELAY_GET_UNREAD);
            }
        });
    }

    private static void refreshUnread() {
        Activity activity = AppManager.getAppManager().currentActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).refreshMyUnread();
        }
    }

    /**
     * 判断用户是否第一次启动
     */
    public boolean isFirstOpen() {
        return SPUtils.getBoolleanValue(YSRLConstants.FIRST_ENTER, true);
    }

    /**
     * 是否是有效用户
     * 仅为userId不为空才是有效用户
     */
    public static boolean isInVaildUser() {
        return TextUtils.isEmpty(userId);
    }

    /**
     * 是否是正式用户
     * 即排除匿名用户,用户为空或者为匿名则返回
     */
    public static boolean isAnonymousUser() {
        return mUser == null || mUser.userState == 0;
    }

    /**
     * 登录后的处理
     *
     * @param user 用户
     */
    public static void doAfterLogin(User user) {
        if (user != null) {
            //更新当前的用户
            mUser = user;
            userId = user.userId;
            isNeedUpdateUserData = true;
            YSRLUserDBHandler.insertOrUpdateUser(user);
            mUser = YSRLUserDBHandler.getUser(userId);

            LogUtils.d("getUser (in AppContext initUser) from db, user = " + mUser);
            saveLoginConfig();
            //用户信息发生改变，发送广播
            BroadcastUtils.sendUpdateUserDataBroadcast();
        }
    }

    /**
     * 保存相关登录配置信息
     */
    private static void saveLoginConfig() {
        //存储当前登录的userId
        SPUtils.putStringValue(AppConfig.CURRENT_USER_ID, userId);
        //重置是否需要登录的标志位
        SPUtils.putBoolleanValue(AppConfig.CONFIG_NEED_LOGIN, false);
    }

    /**
     * 用户退出后，清空相关数据
     */
    public void logoutAndClear() {
        mPushAgent.deleteAlias(AppContext.userId, AppConfig.ALIAS_USER_ID, new UTrack.ICallBack() {

            @Override
            public void onMessage(boolean isSuccess, String message) {

                LogUtils.d("Umeng Push AppContext.mUser.userId remove = " + ", success = " + isSuccess);
            }

        });
        //清除当前用户的所有通知
        YSRLNotifyManager.cancelAll();
        //设置当前用户为空
        userId = "";
        mUser = null;
        LogUtils.d("CURRENT USER_ID logoutAndClear set: userId = 空");
        //存储当前userId为空字符串
        SPUtils.putStringValue(AppConfig.CURRENT_USER_ID, "");
        //用户手动登出时，存储当前需要登录的配置
        SPUtils.putBoolleanValue(AppConfig.CONFIG_NEED_LOGIN, true);
        //用户手动登出时，并没有生日信息
        SPUtils.putBoolSync(YSRLConstants.HAS_ENTER_INFO, false);

        CookieSyncManager.createInstance(baseContext);
        //删除cookie
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
        SPUtils.putBoolleanValue(YSRLConstants.NEED_CLEAR_WEBVIEW_CACHE, true);
        //清除未读提示
        clearUnread();
        //删除本地数据库中的关联数据
//        clearDBData();


    }

    private void clearUnread() {
        mUnReadWaiting = 0;               //未读待回复消息
        mUnReadReply = 0;                 //未读已回复消息
        mUnReadFeedback = 0;              //未读反馈消息
        mUnReadTestFont = 0;              //测字反馈消息
        mCartCount = 0;                   //购物车数量
        mWaitPay = 0;                     //待付款
        mWaitSend = 0;                    //待发货
        mAlreadySend = 0;                 //待收货
        mPointSum = 0;                    //福豆
    }

    // exit application
    public void exit() {
        AppManager.getAppManager().finishAllActivity();
    }

    /**
     * 检测网络是否可用
     * 奇葩的错误，oppo的一款机器竟然报无android.permission.ACCESS_NETWORK_STATE权限
     * 因此处理异常，若发生异常，默认有网络
     */
    public static boolean isNetworkConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) baseContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnectedOrConnecting();
        } catch (Exception e) {
            return true;
        }
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String threadNamePrefix) {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            //将多线程的优先级降低，提升响应速度
            t.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            return t;
        }
    }
}
