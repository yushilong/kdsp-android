package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.ShakingSign;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 3/30/16.
 * 签文的activity
 */
public class PrayDetailActivity extends BaseActivity {
    private TextView mName;                     //名称
    private TextView mAsk;                      //所求
    private TextView mWord;                     //签文
    private TextView mSolution;                 //解签
    private ImageView mLikeSign;                //盖章
    private ImageView mLike;                    //准
    private ImageView mDislike;                 //不准

    private ShakingSign mSign;                  //签文
    private SoundPool mSoundPool;               //用来播放较短的声音

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pray_detail_lay);
        initUI();
        initSoundPool();
    }

    private void initUI() {
        mSign = getIntent().getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        TextView mTitle = (TextView) findViewById(R.id.title_txt);

        mName = (TextView) findViewById(R.id.name);
        mAsk = (TextView) findViewById(R.id.ask);
        mWord = (TextView) findViewById(R.id.word);
        mSolution = (TextView) findViewById(R.id.solution);
        mLikeSign = (ImageView) findViewById(R.id.like_sign);
        mLike = (ImageView) findViewById(R.id.like);
        mDislike = (ImageView) findViewById(R.id.dislike);

        if (mSign != null) {
            switch (mSign.type) {
                case 1:
                    mTitle.setText("月老灵签");
                    break;
                case 2:
                    mTitle.setText("事业灵签");
                    break;
                case 3:
                    mTitle.setText("财神灵签");
                    break;
            }

            mName.setText(mSign.name);
            mAsk.setText(mSign.askSth);
            if (1 == mSign.type) {
                mWord.setText(mSign.mean);
                mSolution.setText(mSign.fate + "\n" + mSign.marriage + "\n" + mSign.fateDegree + "\n" + mSign.happinessDegree + "\n" + mSign.confessionDegree);
            } else {
                mWord.setText(mSign.word);
                mSolution.setText(mSign.solution);
            }

            if (1 == mSign.isLike) {
                mLike.setImageResource(R.drawable.like);
                mDislike.setImageResource(R.drawable.to_dislike);
                mLikeSign.setImageResource(R.drawable.like_sign);
                mLikeSign.setVisibility(View.VISIBLE);
            } else if (2 == mSign.isLike) {
                mLike.setImageResource(R.drawable.to_like);
                mDislike.setImageResource(R.drawable.dislike);
                mLikeSign.setImageResource(R.drawable.dislike_sign);
                mLikeSign.setVisibility(View.VISIBLE);
            } else {
                mLike.setImageResource(R.drawable.to_like);
                mDislike.setImageResource(R.drawable.to_dislike);
                mLikeSign.setVisibility(View.INVISIBLE);
            }

            mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSign.isLike != 1) {
                        KDSPApiController.getInstance().isLike(mSign.shakId, 1, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                PrayDetailActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String userId = "";
                                        if(AppContext.mUser != null){
                                            userId = AppContext.mUser.userId;
                                        }
                                        mLike.setImageResource(R.drawable.like);
                                        mDislike.setImageResource(R.drawable.to_dislike);
                                        mLikeSign.setImageResource(R.drawable.like_sign);
                                        mLikeSign.setVisibility(View.VISIBLE);
                                        mLikeSign.startAnimation(AnimationUtils.loadAnimation(PrayDetailActivity.this, R.anim.seal_anim));
                                        playSealMusic();

                                        OperUtils.mSmallCat = OperUtils.SMALL_CAT_SHAKE;
                                        OperUtils.mKeyCat = mSign.shakId;
                                        ShareActivity.goToMiniShare(PrayDetailActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_PRAY_STICKS, ""),
                                                ShareUtils.getShareContent(ShareUtils.Share_Type_PRAY_STICKS, ""),
                                                getShareUrl(), "http://pt.qi-zhu.com/@/shaking.jpg", ShareUtils.Share_Type_PRAY_STICKS, StatisticsConstant.subType_YAOYIYAO
                                        ,"pages/pocket/calculate/stick/stick_answer?shakId=" + mSign.shakId + "&userId=" + userId);
                                    }
                                });
                                mSign.isLike = 1;
                                BroadcastUtils.sendLikeSignBroadcast(mSign.shakId, 1);
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                            }
                        });
                    }
                }
            });
            mDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSign.isLike != 2) {
                        KDSPApiController.getInstance().isLike(mSign.shakId, 2, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                PrayDetailActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLike.setImageResource(R.drawable.to_like);
                                        mDislike.setImageResource(R.drawable.dislike);
                                        mLikeSign.setImageResource(R.drawable.dislike_sign);
                                        mLikeSign.setVisibility(View.VISIBLE);
                                        mLikeSign.startAnimation(AnimationUtils.loadAnimation(PrayDetailActivity.this, R.anim.seal_anim));
                                        playSealMusic();
                                    }
                                });
                                mSign.isLike = 2;
                                BroadcastUtils.sendLikeSignBroadcast(mSign.shakId, 2);
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                            }
                        });
                    }
                }
            });
        }
    }

    private void initSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.load(this, R.raw.seal, 1);
    }

    /**
     * 播放盖章声音
     */
    private void playSealMusic() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);   // 音频管理
        float audioMaxVolum = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);     // 音效最大值
        float audioCurrentVolum = am.getStreamVolume(AudioManager.STREAM_MUSIC);    // 当前音效值
        float audioRatio = audioCurrentVolum / audioMaxVolum;
        if (mSoundPool != null) {
            mSoundPool.play(1,
                    audioRatio,     // 左声道音量
                    audioRatio,     // 右声道音量
                    1,              // 优先级 0最低
                    0,              // 循环播放次数
                    1);             // 回放速度，该值在0.5-2.0之间 1为正常速度
        }
    }

    private String getShareUrl() {
        return AppConfig.API_BASE + "app/shareExt/shakingShare" + "?userId=" + AppContext.userId + "&shakId=" + mSign.shakId;
    }

    public static void goToPage(Context context, ShakingSign sign) {
        Intent intent = new Intent(context, PrayDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, sign);
        context.startActivity(intent);
    }
}
