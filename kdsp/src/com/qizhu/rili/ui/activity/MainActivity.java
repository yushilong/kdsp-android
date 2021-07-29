package com.qizhu.rili.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pgyer.pgyersdk.PgyerSDKManager;
import com.pgyer.pgyersdk.callback.CheckoutVersionCallBack;
import com.pgyer.pgyersdk.model.CheckSoftModel;
import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.AppManager;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Coupons;
import com.qizhu.rili.bean.SoftUpdate;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.service.YSRLService;
import com.qizhu.rili.ui.dialog.CouponsGetDialogFragment;
import com.qizhu.rili.ui.dialog.PayDialogFragment;
import com.qizhu.rili.ui.dialog.UpdateVersionDialogFragment;
import com.qizhu.rili.ui.fragment.BaseFragment;
import com.qizhu.rili.ui.fragment.BaseViewPagerFragment;
import com.qizhu.rili.ui.fragment.HomeFragment;
import com.qizhu.rili.ui.fragment.InferringFragment;
import com.qizhu.rili.ui.fragment.MyFragment;
import com.qizhu.rili.ui.fragment.PocketFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.CustomTabHost;
import com.umeng.message.UTrack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends BaseActivity {
    public static final int POS_HOME = 0;                       //主页
    public static final int POS_POCKET = 3;                     //口袋
    public static final int POS_MINE = 4;                       //我的主页
    public static final int POS_INFERRING = 2;                  //好运城
    public static final String TAB_HOME = "tab_home";           //主页
    public static final String TAB_KNOWFORTUNE = "tab_knowfortune";           //知运
    public static final String TAB_INFERRING = "tab_inferring"; //占卜
    public static final String TAB_POCKET = "tab_pocket";       //口袋
    public static final String TAB_MINE = "tab_mine";           //我的

    public static final int FIRST_CHILD_POS = 0;    //当前页面的第一个fragment
    public static final int SECOND_CHILD_POS = 1;   //当前页面的第二个fragment

    public static final String INIT_TAB_EXTRA = "init_tab_extras";                      //参数--属于哪个tab
    public static final String INIT_TAB_CHILD_EXTRA = "init_tab_child_extras";          //参数--属于逛街的哪个子tab

    private CustomTabHost mTabHost;                         //底部tab栏
    private TabWidget tabWidget;                            //底部tab
    private ImageView mPocketUnread;                        //口袋页面未读信息
    private ImageView mMineUnread;                          //我的个人未读信息

    private SoftUpdate softUpdate;                          //软件更新信息
    private String             mCurrentTag  = "tab_home"  ;                           //当前fragment的Tag
    private ArrayList<Coupons> mCouponsList = new ArrayList<>();

    //未读数发生变化的广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_UNREAD_CHANGE.equals(action)) {
                refreshUnread();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_lay);
        AppManager.getAppManager().setMainActivity(this);

        try {
            mTabHost = (CustomTabHost) findViewById(android.R.id.tabhost);
            tabWidget = (TabWidget) findViewById(android.R.id.tabs);
            if (MethodCompat.isCompatible(Build.VERSION_CODES.HONEYCOMB)) {
                tabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
            }

            mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

            mTabHost.addTab(mTabHost.newTabSpec(TAB_HOME).
                            setIndicator(getTabIndicator(mResources.getString(R.string.home), R.drawable.tab_indicator_home)),
                    HomeFragment.class, null);

//            mTabHost.addTab(mTabHost.newTabSpec(TAB_KNOWFORTUNE).
//                            setIndicator(getTabIndicator(mResources.getString(R.string.know_fortune), R.drawable.tab_indicator_know_fortune)),
//                    KnowFortuneFragment.class, null);

            mTabHost.addTab(mTabHost.newTabSpec(TAB_INFERRING).
                            setIndicator(getTabIndicator(mResources.getString(R.string.custom_fortune), R.drawable.tab_indicator_inferring)),
                    InferringFragment.class, null);

            mTabHost.addTab(mTabHost.newTabSpec(TAB_POCKET).
                            setIndicator(getTabIndicator(mResources.getString(R.string.pocket), R.drawable.tab_indicator_pocket)),
                    PocketFragment.class, null);

            mTabHost.addTab(mTabHost.newTabSpec(TAB_MINE).
                            setIndicator(getTabIndicator(mResources.getString(R.string.mine), R.drawable.tab_indicator_mine)),
                    MyFragment.class, null);

            mTabHost.setOnTabChangedListener(buildTabChangeListener());
            if (savedInstanceState != null) {
                mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
            } else {
                initTab();
            }

//            if (SPUtils.getBoolleanValue(YSRLConstants.SHOW_MAIN_GUIDE, true)) {
//                SPUtils.putBoolleanValue(YSRLConstants.SHOW_MAIN_GUIDE, false);
//                findViewById(R.id.guide_bg).setVisibility(View.VISIBLE);
//                MethodCompat.setBackground(findViewById(R.id.guide_bg), new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(this, R.drawable.main_guide)));
//                findViewById(R.id.guide_bg).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        findViewById(R.id.guide_bg).setVisibility(View.GONE);
//                    }
//                });
//            }
            CheckVersion();
            getCoupons();
            BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_UNREAD_CHANGE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                PgyerSDKManager.checkSoftwareUpdate(MainActivity.this, new CheckoutVersionCallBack() {
                    @Override
                    public void onSuccess(CheckSoftModel checkSoftModel) {
                        Log.e("PgyerSDKManager",new Gson().toJson(checkSoftModel));
                    }

                    @Override
                    public void onFail(String s) {
                        Log.e("PgyerSDKManager",s);
                    }
                });
            }
        },1000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d("---> MainActivity onNewIntent");
        setIntent(intent);
        initTab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUnread();
    }

    public void CheckVersion() {
        KDSPApiController.getInstance().findSoftUpdate(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    ArrayList<SoftUpdate> softUpdates = SoftUpdate.parseListFromJSON(response.optJSONArray("softUpdates"));
                    if (softUpdates.size() > 0) {
                        softUpdate = softUpdates.get(0);
                        //服务器版本比当前版本新，则启动更新提示
                        if (softUpdate.versionCode > AppContext.versionCode) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String tip = softUpdate.description.replace("<br>", "\n");
                                        showDialogFragment(UpdateVersionDialogFragment.newInstance(tip), "版本更新");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
            }
        });
    }



    public void getCoupons() {
        KDSPApiController.getInstance().findNoActivationCoupon(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    mCouponsList = Coupons.parseListFromJSON(response.optJSONArray("coupons"));

//                        Coupons coupons = new Coupons();
//                    for (int i=1;i<3;i++){
//                        coupons.price = i*100;
//                        coupons.couponName = "ddd";
//                        mCouponsList .add(coupons);
//                    }

                    Collections.reverse(mCouponsList);
                    if(mCouponsList.size() > 0){
                        for(Coupons coupons : mCouponsList){
                            showDialogFragment(CouponsGetDialogFragment.newInstance(coupons.couponName,coupons.mcId,coupons.price/100), "领取优惠券");
                        }
                    }
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
            }
        });
    }

    private void initTab() {
        int tempTab = getIntent().getIntExtra(INIT_TAB_EXTRA, POS_HOME);

        mTabHost.setCurrentTab(tempTab);
        int tempChildTab = getIntent().getIntExtra(INIT_TAB_CHILD_EXTRA, FIRST_CHILD_POS);
        LogUtils.d("---> tempTab = " + tempTab);

        try {
            BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());
            if (fragment != null ) {
                ((BaseViewPagerFragment) fragment).setCurrentFragment(tempChildTab, false);
                LogUtils.d("---> BaseViewPagerFragment = " + tempTab);
            }


        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("change child fragment error!");
        }
    }

    public View getTabIndicator(String tabName, int imgResId) {

        View tabIndicator = mInflater.inflate(R.layout.tab_indicator, tabWidget, false);

        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tab_indicator_img);
        icon.setImageResource(imgResId);
        TextView name = (TextView) tabIndicator.findViewById(R.id.tab_indicator_name);
        name.setText(tabName);
        if (tabName.equals(mResources.getString(R.string.mine))) {
            mMineUnread = (ImageView) tabIndicator.findViewById(R.id.tab_indicator_unread_img);
        } else if (tabName.equals(mResources.getString(R.string.pocket))) {
            mPocketUnread = (ImageView) tabIndicator.findViewById(R.id.tab_indicator_unread_img);
        }

        return tabIndicator;
    }

    FragmentTabHost.OnTabChangeListener buildTabChangeListener() {
        return new FragmentTabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mCurrentTag = tabId;
                LogUtils.d("onTabChanged " + tabId);
            }
        };
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                UIUtils.toastMsg("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                AppContext.baseContext.exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public <T> void setExtraData(T t) {
        if ("ok".equals(t) && softUpdate != null) {
            YSRLService.startDownloadAPK(MainActivity.this, softUpdate.appUrl, softUpdate.version, true);
        }
    }

    /**
     * 调用user.init接口之后下载开机启动图
     */
    @Override
    public void doAfterInit() {
        //设置alias
        try {
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
        ImageUtils.loadStartImg();
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            InferringFragment.mNeedRefresh = true;
            RenewalsSuccessActivity.goToPage(MainActivity.this, "续费成功", 3);
        } else {
            showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
        }
    }

    @Override
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            InferringFragment.mNeedRefresh = true;
            RenewalsSuccessActivity.goToPage(MainActivity.this, "续费成功", 3);
        } else {
            showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
        }
    }

    /**
     * 刷新未读数
     */
    private void refreshUnread() {
        if (AppContext.mUnReadWaiting + AppContext.mUnReadReply + AppContext.mUnReadFeedback + AppContext.mWaitPay + AppContext.mWaitSend + AppContext.mAlreadySend > 0) {
            mMineUnread.setVisibility(View.VISIBLE);
        } else {
            mMineUnread.setVisibility(View.GONE);
        }
        if (AppContext.mUnReadTestFont > 0) {
            mPocketUnread.setVisibility(View.VISIBLE);
        } else {
            mPocketUnread.setVisibility(View.GONE);
        }
    }

    public void refreshMyUnread() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());
        if (mTabHost.getCurrentTab() == POS_MINE && frag instanceof MyFragment) {
            ((MyFragment) frag).refreshUnread();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("MAIN ACTIVITY onDestroy ");
        AppManager.getAppManager().clearMainActivity();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("tag",mCurrentTag);
    }

    /**
     * 跳转至主页面
     */
    public static void goToPage(Context context) {
        goToPage(context, -1);
    }

    /**
     * 跳转至主页面的相应tab页
     */
    public static void goToPage(Context context, int initTab) {
        Intent intent = new Intent(context, MainActivity.class);
        if (initTab != -1) {
            intent.putExtra(INIT_TAB_EXTRA, initTab);
        }
        context.startActivity(intent);
    }

    public static void goToPage(Context context, int initTab, int childPos) {
        Intent intent = new Intent(context, MainActivity.class);
        if (initTab != -1) {
            intent.putExtra(INIT_TAB_EXTRA, initTab);
            intent.putExtra(INIT_TAB_CHILD_EXTRA, childPos);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
