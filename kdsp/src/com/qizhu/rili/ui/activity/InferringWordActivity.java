package com.qizhu.rili.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Divination;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.MediaStateChangedListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.VoiceUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 6/20/16.
 * 测字的activity
 */
public class InferringWordActivity extends BaseActivity {
    private View      mEnterLay;                 //输入测字
    private View      mResultLay;                //结果布局
    private ImageView mShare;               //分享
    private ImageView mUnreadIv;               //小红点
    private View      mPlayLay;                  //播放
    private ImageView mVoice;               //语音
    private EditText  mEnterText;            //测字
    private View      mRewardLay;                //打赏布局
    private ImageView mReward;              //打赏按键
    private TextView  mHasReward;            //已打赏

    private int mIsFonted = 0;              //今天是否可以测字，0为可以，1为否
    private Divination mDivination;         //占卜
    private String mWord = "";              //测字

    private AnimationDrawable mAnimationDrawable;   //动画
    private boolean mIsNotEnd = false;      //语音处于未结束状态
    private boolean mSetRead  = true;        //是否设置已读

    //支付结果的广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_PAY_SUCCESS.equals(action)) {
                String mId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
                if (mDivination != null && mDivination.dtId.equals(mId)) {
                    mDivination.isPay = 1;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inferring_word_lay);
        initUI();
        flushFont();
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_PAY_SUCCESS));
        mSetRead = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_MODE, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDivination != null) {
            if (0 == mDivination.isPay) {
                findViewById(R.id.reward_tip).setVisibility(View.VISIBLE);
                mHasReward.setVisibility(View.GONE);
            } else {
                findViewById(R.id.reward_tip).setVisibility(View.GONE);
                mHasReward.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceUtils.releaseMedia();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    private void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.inferring_word);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        mEnterLay = findViewById(R.id.enter_lay);
        mResultLay = findViewById(R.id.result_lay);

        mShare = (ImageView) findViewById(R.id.share_btn);
        mShare.setVisibility(View.GONE);

        mEnterText = (EditText) findViewById(R.id.enter_text);
        mPlayLay = findViewById(R.id.play_lay);
        mVoice = (ImageView) findViewById(R.id.voice_play);

        mRewardLay = findViewById(R.id.reward_lay);
        mReward = (ImageView) findViewById(R.id.reward);
        mUnreadIv = (ImageView) findViewById(R.id.unread);
        mHasReward = (TextView) findViewById(R.id.has_reward);

    }

    /**
     * 刷新测字状态
     */
    private void flushFont() {
        showLoadingDialog();
        KDSPApiController.getInstance().flushFont(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mIsFonted = response.optInt("isFonted");
                mDivination = Divination.parseObjectFromJSON(response.optJSONObject("div"));
                InferringWordActivity.this.runOnUiThread(new Runnable() {
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

    private void refreshUI() {
        if (0 == mIsFonted) {
            mEnterLay.setVisibility(View.VISIBLE);
            mResultLay.setVisibility(View.GONE);
            if (mDivination != null) {
                TextView mWordTv = (TextView) findViewById(R.id.word);
                mWordTv.setText(mDivination.word);
            }
            mEnterText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        mEnterText.setHint("");
                        UIUtils.showSoftKeyboard(InferringWordActivity.this, mEnterText);
                    } else {
                        mEnterText.setHint("缘");
                    }
                }
            });
            findViewById(R.id.text_confirm).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mWord = mEnterText.getText().toString();
                    if (TextUtils.isEmpty(mWord)) {
                        UIUtils.toastMsg("未填写要测的字~");
                        return;
                    }

                    int len = mWord.length();
                    if (len > 1) {
                        UIUtils.toastMsg("只能输入1个字哦~");
                        return;
                    }

                    if (!StringUtils.isChinese(mWord.charAt(0))) {
                        UIUtils.toastMsg("只能输入汉字哦~");
                        return;
                    }

                    showLoadingDialog();
                    KDSPApiController.getInstance().addFontMsg(mWord, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            mIsFonted = 1;
                            mDivination = new Divination();
                            mDivination.dtId = response.optString("dtId");
                            mDivination.isAnswer = 1;
                            mDivination.word = mWord;
                            Divination answer = new Divination();
                            answer.content = response.optString("voicePath");
                            mDivination.answers.add(answer);
                            InferringWordActivity.this.runOnUiThread(new Runnable() {
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
            });
        } else {
            if (mDivination.isAnswer == 1) {
                mUnreadIv.setVisibility(View.VISIBLE);
            }

            mEnterLay.setVisibility(View.GONE);
            mResultLay.setVisibility(View.VISIBLE);
            if (mDivination != null) {
                TextView mWordTv = (TextView) findViewById(R.id.word);
                mWordTv.setText(mDivination.word);
                if (0 == mDivination.isAnswer) {
                    mPlayLay.setVisibility(View.GONE);
                    findViewById(R.id.wait_reply).setVisibility(View.VISIBLE);
                    mRewardLay.setVisibility(View.INVISIBLE);
                } else {
                    mPlayLay.setVisibility(View.VISIBLE);
                    findViewById(R.id.wait_reply).setVisibility(View.INVISIBLE);
                    mRewardLay.setVisibility(View.VISIBLE);
                    final Divination mAnswer = mDivination.answers.get(0);
                    VoiceUtils.loadVoice(mAnswer.content);
                    mPlayLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            VoiceUtils.playVoice(mAnswer.content, new MediaStateChangedListener() {
                                @Override
                                public void onStart(String url) {
                                    mIsNotEnd = true;
                                    mVoice.setImageResource(R.drawable.voice_anim);
                                    mAnimationDrawable = (AnimationDrawable) mVoice.getDrawable();
                                    mAnimationDrawable.start();
                                    if (mSetRead) {
                                        KDSPApiController.getInstance().changeFontStatus(mDivination.dtId, new KDSPHttpCallBack() {
                                            @Override
                                            public void handleAPISuccessMessage(JSONObject response) {
                                                AppContext.mUnReadTestFont = 0;
                                                InferringWordActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mUnreadIv.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onPause(String url) {
                                    mAnimationDrawable.stop();
                                    mVoice.setImageResource(R.drawable.voice3);
                                }

                                @Override
                                public void onStop(String url) {
                                    mAnimationDrawable.stop();
                                    mVoice.setImageResource(R.drawable.voice3);
                                }

                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mIsNotEnd = false;
                                    mAnimationDrawable.stop();
                                    mVoice.setImageResource(R.drawable.voice3);
                                }
                            });
                        }
                    });

                    //跳转到支付页面
                    mReward.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            VoiceUtils.stopMedia();
                            RewardActivity.goToPage(InferringWordActivity.this, mDivination.dtId);
                        }
                    });

                    if (0 == mDivination.isPay) {
                        findViewById(R.id.reward_tip).setVisibility(View.VISIBLE);
                        mHasReward.setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.reward_tip).setVisibility(View.GONE);
                        mHasReward.setVisibility(View.VISIBLE);
                    }

                    mShare.setVisibility(View.VISIBLE);
                    mShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShareActivity.goToMiniShare(InferringWordActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_CEZI, ""),
                                    ShareUtils.getShareContent(ShareUtils.Share_Type_CEZI, ""),
                                    getShareUrl(), "", ShareUtils.Share_Type_CEZI, StatisticsConstant.subType_RENYICE, "pages/pocket/calculate/word/word_answer?word=" + mWord);
                            VoiceUtils.releaseMedia();
                        }
                    });
                }
            }
        }
    }

    private void showVoiceTip() {
        findViewById(R.id.voice_tip_lay).setVisibility(View.VISIBLE);
        findViewById(R.id.cancel_voice_tip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.goon_voice_tip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.voice_tip_lay).setVisibility(View.GONE);
            }
        });
    }

    private String getShareUrl() {
        return AppConfig.API_BASE + "app/shareExt/testFontAndPalmResult" + "?userId=" + AppContext.userId + "&dtId=" + mDivination.dtId;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            //点击空白位置 隐藏软键盘
            UIUtils.hideSoftKeyboard(InferringWordActivity.this, focusView);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onClickBackBtnEvent() {
        if (mIsNotEnd) {
            showVoiceTip();
            return true;
        }
        return super.onClickBackBtnEvent();
    }

    public static void goToPage(Context context) {
        goToPage(context, true);
    }

    /**
     * @param setRead 是否设置已读,若推送进入,则不设置,默认为true,推送传false
     */
    public static void goToPage(Context context, boolean setRead) {
        Intent intent = new Intent(context, InferringWordActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, setRead);
        context.startActivity(intent);
    }
}
