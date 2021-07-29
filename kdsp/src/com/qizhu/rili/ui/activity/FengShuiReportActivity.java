package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.widget.TimeTextView;

/**
 * 风水报告
 */
public class FengShuiReportActivity extends BaseActivity implements View.OnClickListener {
    TimeTextView   mTimeTv;
    RelativeLayout mTimeRl;
    private String         mItemId;                 //问题id
    private String         mUrl;                 //风水结果
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fengshui_report_activity);
        mItemId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        mUrl = getIntent().getStringExtra(IntentExtraConfig.EXTRA_WEB_URL);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    protected void initUI() {
        mTimeTv = findViewById(R.id.time_tv);
        mTimeRl = findViewById(R.id.time_rl);
        TextView mTitle = (TextView) findViewById(R.id.title_txt);

        mTitle.setText(R.string.fengshui);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.go_back).setOnClickListener(this);
        findViewById(R.id.see_tv).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mTimeRl.setVisibility(View.GONE);
            mTimeTv.setTimes(DateUtils.convertTimeToDHMS(5), TimeTextView.TYPE_CODE);
            countDownTimer = new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeTv.ComputeTime(TimeTextView.TYPE_CODE);
                }

                @Override
                public void onFinish() {
                    YSRLWebActivity.goToPage(FengShuiReportActivity.this, mUrl);
                    finish();
                }
            };
            countDownTimer.start();

        }
    }

    public static void goToPage(Context context, String itemId, String url) {
        Intent intent = new Intent(context, FengShuiReportActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_WEB_URL, url);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
                finish();
                break;
            case R.id.see_tv:
                AugurySubmitActivity.goToPage(FengShuiReportActivity.this, mItemId);
                break;
        }
    }

}
