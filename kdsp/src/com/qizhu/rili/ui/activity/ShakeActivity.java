package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.ShakingSign;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 3/30/16.
 * 摇签的activity
 */
public class ShakeActivity extends BaseActivity {
    private int mMode = 1;                      //模式，1为月老灵签，2为事业灵签, 3为财神灵签
    private ImageView mSticksContainer;         //签筒
    private TextView mSticks;                   //签

    private SensorManager mSensorManager;                   //定义sensor管理器
    private Sensor accelSensor;                             //重力加速度传感器
    private SensorEventListener eventListener;              //传感器监听器
    private float oriticalShakeValue = 18.5f;               //加速度临界值

    private ShakingSign mShakingSign;           //签文
    private SoundPool mSoundPool;               //用来播放较短的声音
    private Animation mAnim;                    //摇一摇动画
    private boolean mHasEndAnim;                //动画已经结束
    private boolean isShakeing;                 //是否正在摇动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_lay);
        initUI();
        initSensor();
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                initSoundPool();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager != null && accelSensor != null) {
            mSensorManager.registerListener(eventListener, accelSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //解绑传感器
        if (mSensorManager != null && accelSensor != null) {
            mSensorManager.unregisterListener(eventListener);
        }
    }

    private void initUI() {
        mMode = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE, 1);
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        mSticksContainer = (ImageView) findViewById(R.id.sticks_container);
        mSticks = (TextView) findViewById(R.id.pray_sticks);
        TextView mTitle = (TextView) findViewById(R.id.title_txt);

        switch (mMode) {
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

        mAnim = AnimationUtils.loadAnimation(ShakeActivity.this, R.anim.shake_anim);
        mAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mHasEndAnim = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHasEndAnim = true;
                isShakeing = false;
                //动画结束执行刷新签文
                if (mShakingSign != null) {
                    refreshUI();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mSticksContainer.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                shakeSticks();
            }
        });
    }

    /**
     * 初始化传感器
     */
    private void initSensor() {
        //获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //加速度传感器
        accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (!isShakeing) {
                    int sensorType = event.sensor.getType();
                    //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
                    float[] values = event.values;
                    if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                        if (Math.abs(values[0]) > oriticalShakeValue || Math.abs(values[1]) > oriticalShakeValue) {
                            shakeSticks();
                        }
                    }
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //当传感器精度改变时回调该方法，Do nothing.
            }
        };
    }

    private void initSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.load(this, R.raw.shake_sticks, 1);
    }

    private void shakeSticks() {
        isShakeing = true;
        long clickTime = 0;
        switch (mMode) {
            case 1:
                clickTime = SPUtils.getLongValue(YSRLConstants.PRAY_LOVE_TIME_32 + AppContext.userId);
                break;
            case 2:
                clickTime = SPUtils.getLongValue(YSRLConstants.PRAY_CAREER_TIME_32 + AppContext.userId);
                break;
            case 3:
                clickTime = SPUtils.getLongValue(YSRLConstants.PRAY_WEALTH_TIME_32 + AppContext.userId);
                break;
        }
        if (DateUtils.isToday(clickTime)) {
            UIUtils.toastMsg("今天已经求过此签啦");
        } else {
            mSticksContainer.startAnimation(mAnim);
            playShakeMusic();
            int signCount = (int) (Math.random() * (1 == mMode ? 101 : 100) + 1);
            KDSPApiController.getInstance().askShaking(mMode, signCount, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    switch (mMode) {
                        case 1:
                            SPUtils.putLongValue(YSRLConstants.PRAY_LOVE_TIME_32 + AppContext.userId, System.currentTimeMillis());
                            break;
                        case 2:
                            SPUtils.putLongValue(YSRLConstants.PRAY_CAREER_TIME_32 + AppContext.userId, System.currentTimeMillis());
                            break;
                        case 3:
                            SPUtils.putLongValue(YSRLConstants.PRAY_WEALTH_TIME_32 + AppContext.userId, System.currentTimeMillis());
                            break;
                    }
                    mShakingSign = ShakingSign.parseObjectFromJSON(response.optJSONObject("shaking"));
                    //摇一摇动画结束之后才刷新签文
                    if (mHasEndAnim) {
                        ShakeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshUI();
                            }
                        });
                    }
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    showFailureMessage(error);
                }
            });
        }
    }

    /**
     * 播放摇一摇声音
     */
    private void playShakeMusic() {
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

    private void refreshUI() {
        if (!TextUtils.isEmpty(mShakingSign.name)) {
            UIUtils.setVerticalTxt(mSticks, mShakingSign.name);
            mSticks.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.sticks_anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    PrayDetailActivity.goToPage(ShakeActivity.this, mShakingSign);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mSticks.startAnimation(animation);
        }
    }

    public static void goToPage(Context context, int mode) {
        Intent intent = new Intent(context, ShakeActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mode);
        context.startActivity(intent);
    }
}
