package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.SPUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 3/30/16.
 * 求签分类的activity
 */
public class PrayActivity extends BaseActivity {
    private ImageView mLoveTip;
    private ImageView mWealthTip;
    private ImageView mCareerTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pray_lay);
        initUI();
        getShakingStatus();
    }

    private void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.shake);
        mLoveTip = (ImageView) findViewById(R.id.love_tip);
        mWealthTip = (ImageView) findViewById(R.id.wealth_tip);
        mCareerTip = (ImageView) findViewById(R.id.career_tip);

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        findViewById(R.id.right_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                PrayListActivity.goToPage(PrayActivity.this);
                KDSPApiController.getInstance().addStatistics(StatisticsConstant.SOURCE_POCKET, StatisticsConstant.TYPE_PAGE, StatisticsConstant.subType_YAOYIYAO_RECORD, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {

                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {

                    }
                });
            }
        });

        findViewById(R.id.pray_love).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (DateUtils.isToday(SPUtils.getLongValue(YSRLConstants.PRAY_LOVE_TIME_32 + AppContext.userId))) {
                    showTip(1);
                } else {
                    ShakeActivity.goToPage(PrayActivity.this, 1);
                }
            }
        });
        findViewById(R.id.pray_career).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (DateUtils.isToday(SPUtils.getLongValue(YSRLConstants.PRAY_CAREER_TIME_32 + AppContext.userId))) {
                    showTip(2);
                } else {
                    ShakeActivity.goToPage(PrayActivity.this, 2);
                }
            }
        });
        findViewById(R.id.pray_wealth).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (DateUtils.isToday(SPUtils.getLongValue(YSRLConstants.PRAY_WEALTH_TIME_32 + AppContext.userId))) {
                    showTip(3);
                } else {
                    ShakeActivity.goToPage(PrayActivity.this, 3);
                }
            }
        });
    }

    private void getShakingStatus() {
        KDSPApiController.getInstance().flushShaking(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                //若为0则未摇，反之则已经摇过签了
                if (response.optInt("marriage") != 0) {
                    SPUtils.putLongValue(YSRLConstants.PRAY_LOVE_TIME_32 + AppContext.userId, System.currentTimeMillis());
                }
                if (response.optInt("business") != 0) {
                    SPUtils.putLongValue(YSRLConstants.PRAY_CAREER_TIME_32 + AppContext.userId, System.currentTimeMillis());
                }
                if (response.optInt("fortune") != 0) {
                    SPUtils.putLongValue(YSRLConstants.PRAY_WEALTH_TIME_32 + AppContext.userId, System.currentTimeMillis());
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }

    private void showTip(int type) {
        switch (type) {
            case 1:
                mLoveTip.setVisibility(View.VISIBLE);
                findViewById(R.id.pray_love).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoveTip.setVisibility(View.GONE);
                    }
                }, 3000);
                break;
            case 2:
                mCareerTip.setVisibility(View.VISIBLE);
                findViewById(R.id.pray_love).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCareerTip.setVisibility(View.GONE);
                    }
                }, 3000);
                break;
            case 3:
                mWealthTip.setVisibility(View.VISIBLE);
                findViewById(R.id.pray_love).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWealthTip.setVisibility(View.GONE);
                    }
                }, 3000);
                break;
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, PrayActivity.class);
        context.startActivity(intent);
    }
}
