package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.LineDetail;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.MediaStateChangedListener;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.VoiceUtils;

import org.json.JSONObject;

/**
 * 某条线的详情
 */
public class LineDetailActivity extends BaseActivity {
    private ImageView mDish;                    //碟
    private ImageView mNeedle;                  //碟针
    private TextView mOneLineTitle;             //标题
    private TextView mOneLineDesc;              //描述
    private TextView mTitle;                    //标题

    private int mType;                          //支线类型
    private LineDetail mLineDetail;             //详情
    private Animation mDishAnim;                //碟盘的动画
    private Animation mNeedleAnim;              //碟针的动画
    private boolean mIsBranch;                  //是否是支线

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_detail_lay);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        mDish = (ImageView) findViewById(R.id.dish);
        mNeedle = (ImageView) findViewById(R.id.needle);
        mOneLineTitle = (TextView) findViewById(R.id.line_title);
        mOneLineDesc = (TextView) findViewById(R.id.line_desc);

        mTitle = (TextView) findViewById(R.id.title_txt);
        findViewById(R.id.play_lay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLineDetail != null && !TextUtils.isEmpty(mLineDetail.voiceUrl)) {
                    VoiceUtils.playVoice(mLineDetail.voiceUrl, new MediaStateChangedListener() {
                        @Override
                        public void onStart(String url) {
                            mDish.setImageResource(R.drawable.dish);
                            mDish.startAnimation(mDishAnim);
                            mNeedle.startAnimation(mNeedleAnim);
                        }

                        @Override
                        public void onPause(String url) {
                            mDish.setImageResource(R.drawable.dish_play);
                            mDishAnim.cancel();
                            mDish.clearAnimation();
                            mNeedle.startAnimation(AnimationUtils.loadAnimation(LineDetailActivity.this, R.anim.needle_reverse_anim));
                        }

                        @Override
                        public void onStop(String url) {
                            mDish.setImageResource(R.drawable.dish_play);
                            mDishAnim.cancel();
                            mDish.clearAnimation();
                            mNeedle.startAnimation(AnimationUtils.loadAnimation(LineDetailActivity.this, R.anim.needle_reverse_anim));
                        }

                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mDish.setImageResource(R.drawable.dish_play);
                            mDishAnim.cancel();
                            mDish.clearAnimation();
                            mNeedle.startAnimation(AnimationUtils.loadAnimation(LineDetailActivity.this, R.anim.needle_reverse_anim));
                        }
                    });
                }
            }
        });
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BranchLinesActivity.goToPage(LineDetailActivity.this, mType);
                VoiceUtils.releaseMedia();
            }
        });
        findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLineDetail != null) {
                    OperUtils.mSmallCat = OperUtils.SMALL_CAT_OTHER;
                    OperUtils.mKeyCat = OperUtils.KEY_CAT_PALM;
                    ShareActivity.goToShare(LineDetailActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_CESHOUXIANG, ""),
                            ShareUtils.getShareContent(ShareUtils.Share_Type_CESHOUXIANG, ""),
                            getShareUrl(), "", ShareUtils.Share_Type_CESHOUXIANG, StatisticsConstant.subType_RENYICE);
                    VoiceUtils.releaseMedia();
                }
            }
        });
        mDishAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        mNeedleAnim = AnimationUtils.loadAnimation(this, R.anim.needle_anim);

        mOneLineDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public boolean onClickBackBtnEvent() {
        //主线进入，那么回退到拍照页面
        if (!mIsBranch) {
            TakeHandsPhotoActivity.goToPage(LineDetailActivity.this, false);
            finish();
            return true;
        }
        return super.onClickBackBtnEvent();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        getData();
    }

    /**
     * 设置数据
     */
    private void getData() {
        Intent intent = getIntent();
        mLineDetail = intent.getParcelableExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT);
        mType = intent.getIntExtra(IntentExtraConfig.EXTRA_MODE, 2);
        if (mLineDetail != null) {
            mIsBranch = true;
            refreshUI();
            mTitle.setText(R.string.line_branch);
        } else {
            mIsBranch = false;

            String mStartPoint = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
            String mEndPoint = intent.getStringExtra(IntentExtraConfig.EXTRA_PARCEL);
            String mScreenSize = intent.getStringExtra(IntentExtraConfig.EXTRA_POSITION);
            switch (mType) {
                case 2:
                    mTitle.setText(R.string.line_one);
                    break;
                case 3:
                    mTitle.setText(R.string.line_two);
                    break;
                case 4:
                    mTitle.setText(R.string.line_three);
                    break;
                case 5:
                    mTitle.setText(R.string.line_four);
                    break;
                case 6:
                    mTitle.setText(R.string.line_five);
                    break;
            }
            showLoadingDialog();
            KDSPApiController.getInstance().addPalmMsg(mType, mStartPoint, mEndPoint, mScreenSize, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    mLineDetail = LineDetail.parseObjectFromJSON(response.optJSONObject("palmVoice"));
                    LineDetailActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            refreshUI();
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    dismissLoadingDialog();
                    showFailureMessage(error);
                }
            });
        }
    }

    private void refreshUI() {
        VoiceUtils.loadVoice(mLineDetail.voiceUrl);
        mOneLineTitle.setText(mLineDetail.subTitle);
        mOneLineDesc.setText(mLineDetail.fontContent);
    }

    private String getShareUrl() {
        if (mIsBranch) {
            return AppConfig.API_BASE + "app/shareExt/brandPalmShare" + "?userId=" + AppContext.userId + "&pbId=" + mLineDetail.pbId;
        } else {
            return AppConfig.API_BASE + "app/shareExt/testPalmResult" + "?userId=" + AppContext.userId + "&pvId=" + mLineDetail.pvId;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceUtils.releaseMedia();
    }

    public static void goToPage(Context context, int type, String startPoint, String endPoint, String screenSize) {
        Intent intent = new Intent(context, LineDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, type);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, startPoint);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, endPoint);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, screenSize);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, LineDetail detail, int type) {
        Intent intent = new Intent(context, LineDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT, detail);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, type);
        context.startActivity(intent);
    }
}
