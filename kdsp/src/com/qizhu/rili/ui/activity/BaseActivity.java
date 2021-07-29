package com.qizhu.rili.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.AppManager;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.ui.dialog.LoadingDialog;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UmengUtils;
import com.umeng.message.PushAgent;

import org.json.JSONObject;

import java.util.List;


public abstract class BaseActivity extends AppCompatActivity {
    protected LayoutInflater mInflater;
    protected Resources mResources;
    protected PushAgent mPushAgent;
    protected LoadingDialog mLoadingDialog;         //加载对话框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(this);
        mResources = getResources();
        try {
            mPushAgent = PushAgent.getInstance(this);
            mPushAgent.onAppStart();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppManager.getAppManager().addActivity(this);
    }

    protected void bind(){
    }

    @Override
    protected void onStart() {

        super.onStart();
        //获取init标志位，当需要init时，调用服务器init接口
        boolean needInit = getInitFlag();
        LogUtils.d("YSRL needInit onStart get --> " + needInit + ", this = " + this);
        if (needInit) {
            //调用服务器init接口
            userInit();
            //程序切到前台,调用轮询接口
            AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        UmengUtils.onActivityResume(this);
        if (OperUtils.mShouldOper) {
            OperUtils.mShouldOper = false;
            OperUtils.oper(OperUtils.BIG_CAT_SHARE, OperUtils.mSmallCat, OperUtils.mKeyCat);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // umeng
        UmengUtils.onActivityPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("BaseActivity 周期 onStop -> this = " + this);
                //当程序处于后台时，设置init标志位为true，否则设为false
                boolean isAppOnForeground = isAppOnForeground();
                boolean needInit = !isAppOnForeground;
                LogUtils.d("YSRL needInit onStop set --> " + needInit + ", this = " + this);
                setInitFlag(needInit);

                if (needInit) {
                    //YSRLService的handler必须在主线程初始化
                    AppContext.getAppHandler().sendEmptyMessage(AppContext.START_CLEAR_CACHE);
                    //程序切到后台,那么停止轮询未读消息接口
                    AppContext.getAppHandler().removeMessages(AppContext.GET_UNREAD_COUNT);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    /**
     * 调用服务器init接口
     */
    protected void userInit() {
        if (!TextUtils.isEmpty(AppContext.userId)) {
            KDSPApiController.getInstance().initSysConfig(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (response != null) {
                        AppContext.doUserInit(response);
                        //一个activity resume之后，调用init成功则将标记位重置，防止重复调用
                        setInitFlag(false);
                        BaseActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //init之后触发
                                doAfterInit();
                            }
                        });
                    }
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    BaseActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //init之后触发
                            doAfterInit();
                        }
                    });

                }
            });
        }else{
            doAfterInit();
        }
    }



    /**
     * 设置应用程序是否需要init，通知服务器统计数据
     *
     * @param needInit 是否需要再次init
     */
    public void setInitFlag(boolean needInit) {
        //保存标志位到SharedPreferences中
        SPUtils.putBoolleanValue(YSRLConstants.CONFIG_KEY_INIT, needInit);
    }

    /**
     * 获取应用程序是否需要调用init接口，通知服务器统计数据
     */
    protected boolean getInitFlag() {
        //从SharedPreferences中读取init标志位
        return SPUtils.getBoolleanValue(YSRLConstants.CONFIG_KEY_INIT);
    }

    /**
     * 程序是否在前台运行
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 监听返回按钮，实现onClickBackBtnEvent()方法
     */
    @Override
    public void onBackPressed() {
        try {
            if (onClickBackBtnEvent()) {
                return;
            }
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回退操作，若用户有拦截操作，则finish原来界面
     */
    public void goBack() {
        if (!onClickBackBtnEvent()) {
            finish();
        }
    }

    /**
     * 监听用户单击回退键后的操作,子类需复写此函数，默认是调用系统的处理
     * return true 则拦截系统返回操作且不销毁当前的activity
     */
    public boolean onClickBackBtnEvent() {
        //如果从通知进入，主页面不在堆栈，则回退到主页面
        if (AppManager.getAppManager().backToMain()) {
            MainActivity.goToPage(this);
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        AppManager.getAppManager().removeActivity(this);
    }

    public void showLoadingDialog() {
        dismissLoadingDialog();
        mLoadingDialog = new LoadingDialog(this);
        if (!isFinishing()) {
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing() && !isFinishing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    // 显示DialogFragment
    public void showDialogFragment(DialogFragment dialogFragment, String tag) {
        //友盟上报错http://www.umeng.com/apps/ed100012e15c89dff335cd45/error_types/570e80f4498ef856944a743c
        //fragment的状态保存会导致show的时候状态出错
        try {
            dialogFragment.show(getSupportFragmentManager().beginTransaction(), tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init之后的操作
     */
    public void doAfterInit() {
    }

    /**
     * 设置传输的数据，以泛型适应所有情况
     */
    public <T> void setExtraData(T t) {
    }

    public void setPickDateTime(DateTime dateTime, int mode) {

    }

    /**
     * 设置选择的照片路径
     */
    public void setImagePath(String path) {
    }

    /**
     * 分享微信成功
     */
    public void shareWxSucceed() {
        LogUtils.d("---> shared wx succeed");
    }

    /**
     * 分享微信失败
     */
    public void shareWxFailed() {
        LogUtils.d("---> shared wx failed");
    }

    /**
     * 微信支付成功
     */
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        LogUtils.d("---> pay wx succeed");
    }

    /**
     * 支付宝支付成功
     */
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        LogUtils.d("---> pay ali succeed");
    }

    public void setWindowStatusBarColor(int colorResId) {
        try {
            if (MethodCompat.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mResources.getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
