package com.qizhu.rili.ui.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.lyf.yflibrary.Permission;
import com.example.lyf.yflibrary.PermissionResult;
import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.StartupImage;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.db.KDSPStartupImageDBHandler;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.YSRLNavigationByUrlUtils;
import com.qizhu.rili.widget.TimeTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * 闪屏activity
 */
public class AppStart extends BaseActivity {
    private View         mStartLay;                 //开机启动全局view
    private ImageView    mStartImage;          //启动界面图片
    private View         mSolganLay;                //开机solgan的布局
    private View         mAnimImage;                //动画的view
    private TimeTextView mCountDown;        //倒计时

    private boolean hasStartImage = false;              //是否有启动图
    private boolean mShouldGo     = true;                   //是否应该跳转
    private StartupImage       startupImage;                  //启动图
    private List<StartupImage> mStartupImages;
    private int      mDisplayTime        = 2000;                    //显示时间
    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_lay);
        mStartLay = findViewById(R.id.start_lay);
        mStartImage = (ImageView) findViewById(R.id.start_img);
        mSolganLay = findViewById(R.id.solgan_lay);
        mAnimImage = findViewById(R.id.anim_image);
        mCountDown = (TimeTextView) findViewById(R.id.countdown);
        setInitFlag(false);
        Permission.checkPermisson(this, REQUEST_PERMISSIONS, new PermissionResult() {

            @Override
            public void success() {

            }

            @Override
            public void fail() {

            }
        });

        LogUtils.d("----- 启动页 appstart  userId ：" + AppContext.userId);
        if (!TextUtils.isEmpty(AppContext.userId)) {
            KDSPApiController.getInstance().initSysConfig(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (response != null) {
                        JSONArray startupImageJson = response.optJSONArray("startupImages");

                        if (startupImageJson != null) {
                            mStartupImages = StartupImage.parseListFromJSON(startupImageJson);
                            if (mStartupImages.size() > 0) {
                                startupImage = mStartupImages.get(0);
                            }

                        }

//                        StartupImage startupImage1 = new StartupImage();
//                        startupImage1.showTimes = 1000;
//                        startupImage1.stId = "f257fa9e56c49fdd0156c4a65e610000";
////                        startupImage1.imageUrl = "/2015/12/18/2d5d0690-9992-450c-b0e0-40d5b250b48c.png";
//                        startupImage1.imageUrl = "/2016/08/26/bad17fbd-21f1-4efc-9caf-eb6947fa0ace.png";
//                        startupImage1.startTime = DateUtils.getIntFromDateString("2016-08-18 10:22:30");
//                        startupImage1.endTime = DateUtils.getIntFromDateString("2019-05-15 14:32:04");
//                        List<StartupImage> startupImageList = new ArrayList<>();
//                        mStartupImages = startupImageList;
//
//                        startupImageList.add(startupImage1);
//                        startupImage = startupImage1;
//                        LogUtils.d("----- 启动页 appstart  Success");
                        //一个activity resume之后，调用init成功则将标记位重置，防止重复调用
                        setInitFlag(false);
                        AppContext.threadPoolExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (startupImage != null) {


                                    if (startupImage.bytes == null) {
                                        startupImage.bytes = ImageUtils.getBytesFromUrl(AppConfig.API_IMG_URL + startupImage.imageUrl, AppContext.mScreenWidth, AppContext.mScreenHeight);
                                    }
                                }
                            }
                        });

                        AppStart.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //init之后触发
                                doAfterInit();
                            }
                        });


                    }
                }

                @Override
                public void handleAPIFailureMessage(final Throwable error, final String reqCode) {
                    AppStart.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.d("----- 启动页 appstart  Fail" + error.toString() + reqCode);

                            doAfterInit();
                        }
                    });

                }
            });
        } else {
            LogUtils.d("----- 启动页 appstart  userId null");
            doAfterInit();
        }
    }

    @Override
    protected void onStart() {
        //AppStart不调用init接口

        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("appstart onResume!");

        //设置动画

    }

    /**
     * 跳转页面
     */
    private void goToApp() {
        if (mShouldGo) {
            //判断是否第一次登陆
            LogUtils.d("---> AppStart1 goToApp" + !SPUtils.getBoolleanValue(YSRLConstants.HAS_ENTER_INFO));
            if (AppContext.baseContext.isFirstOpen()) {
                SPUtils.putBoolleanValue(YSRLConstants.FIRST_ENTER, false);
                GuideActivity.goToPage(AppStart.this);
            } else {
                //启动主页的时候必须调用init接口
                if (AppContext.isInVaildUser()) {
                    LoginActivity.goToPage(AppStart.this, true);
                } else if (!SPUtils.getBoolleanValue(YSRLConstants.HAS_ENTER_INFO)) {
                    SetInfoActivity.goToPage(AppStart.this);
                } else {
                    setInitFlag(true);
                    MainActivity.goToPage(AppStart.this);
                }
            }
            mShouldGo = false;
            finish();
        }
    }


    @Override
    public void doAfterInit() {
        super.doAfterInit();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.app_icon_scale_out);

        //设置动画监听
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LogUtils.d("启动页 in animation start!");


                //获取当前符合规范的启动图
                StartupImage tempStartupImage = KDSPStartupImageDBHandler.getNowImage();
                if (tempStartupImage != null && startupImage != null) {
                    if (tempStartupImage.stId.equals(startupImage.stId))
                        startupImage.showTimes = tempStartupImage.showTimes;
                }

                if (mStartupImages != null) {


                    if (mStartupImages.size() > 0) {
                        KDSPStartupImageDBHandler.insertImageListAfterDelete(mStartupImages);
                    } else {
                        KDSPStartupImageDBHandler.deleteImage();
                    }
                }
                LogUtils.d("----- 启动页 appstart");
                //是否有海报图,只有海报图不为空，且有图片地址，且显示次数大于0才显示
                hasStartImage = startupImage != null && !TextUtils.isEmpty(startupImage.imageUrl) && (startupImage.showTimes > 0);
                LogUtils.d("----- 启动页 appstart" + hasStartImage);


            }

            @Override
            public void onAnimationEnd(Animation animation) {

                LogUtils.d("启动页 onAnimationEnd userId = " + AppContext.userId + ", session = " + AppContext.session
                        + ", AppContext isOnCreated = " + AppContext.isOnCreated );
                if (hasStartImage && startupImage != null) {

                    Drawable mStartupDrawable = getStartImg(startupImage.bytes);

                    mDisplayTime = startupImage.duration;
                    //若动画时间明显不合规则，则设置为默认时间防止意外
                    if (mDisplayTime <= 0 || mDisplayTime > 10000) {
                        mDisplayTime = 2000;
                    }
                    //如果获取到的启动图不为空，那么显示启动图，否则直接跳转
                    if (mStartupDrawable != null) {
                        if (!TextUtils.isEmpty(startupImage.colorVal)) {
                            try {
                                mStartLay.setBackgroundColor(Color.parseColor(startupImage.colorVal));
                            } catch (IllegalArgumentException e) {
                                LogUtils.d("AppStart setBackgroundColor error" + e);
                                mStartLay.setBackgroundColor(ContextCompat.getColor(AppStart.this, R.color.purple1));
                            }
                        } else {
                            mStartLay.setBackgroundColor(ContextCompat.getColor(AppStart.this, R.color.purple1));
                        }
                        MethodCompat.setBackground(mStartImage, mStartupDrawable);
//                        mStartImage.setImageDrawable(mStartupDrawable);
                        if (!TextUtils.isEmpty(startupImage.linkUrl)) {
                            mStartImage.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    LogUtils.d("total uri is %s", startupImage.linkUrl);
                                    YSRLNavigationByUrlUtils.navigate(startupImage.linkUrl, AppStart.this, true);
                                    mShouldGo = false;
                                    finish();
                                }
                            });
                        }
                        mStartImage.setVisibility(View.VISIBLE);
                        mSolganLay.setVisibility(View.GONE);
                        //启动图的时候，隐藏首发logo
//                        setStartingApp(false);
                        mCountDown.setVisibility(View.VISIBLE);
                        mCountDown.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                goToApp();
                            }
                        });
                        //onTick一开始就被触发，而且有时间损失，所以加上200ms的时间损失处理
                        CountDownTimer countDownTimer = new CountDownTimer(mDisplayTime + 200, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                LogUtils.d("onTick ---> " + millisUntilFinished);
                                StringBuilder sBuilder = new StringBuilder();
                                sBuilder.append("跳过（").append(millisUntilFinished / 1000).append("S").append("）");
                                mCountDown.setText(sBuilder);
                            }

                            @Override
                            public void onFinish() {
                                goToApp();
                            }
                        };
                        countDownTimer.start();
                        //更新数据库
                        AppContext.threadPoolExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                startupImage.showTimes--;
                                KDSPStartupImageDBHandler.updateShowTimes(startupImage);
                            }
                        });
                    } else {
                        goToApp();
                    }
                } else {
                    goToApp();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAnimImage.startAnimation(animation);

    }

    /**
     * 获取开机启动图片
     * 若发生OOM，则直接返回空，用的时候进行判断，若为空，则直接进入
     */
    private Drawable getStartImg(byte[] bytes) {
        LogUtils.d("---> AppStart getStartImg");
        Drawable drawable;
        try {
            Bitmap bitmap = ImageUtils.bytesToBitmap(bytes);

            if (bitmap != null) {
                drawable = new BitmapDrawable(getResources(), bitmap);
            } else {
                LogUtils.d("---> AppStart display default drawable");
                drawable = null;
            }
        } catch (Throwable ex) {
            drawable = null;
        }
        return drawable;
    }


}
