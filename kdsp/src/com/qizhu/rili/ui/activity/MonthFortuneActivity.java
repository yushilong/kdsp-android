package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lindow on 9/28/16.
 * 月运
 */
public class MonthFortuneActivity extends BaseActivity {
    private View mMonthFortuneLay;                      //月运势
    private TextView mMonthLoveImage;                   //感情
    private TextView mMonthCareerImage;                 //事业
    private TextView mMonthMoneyImage;                  //财运
    private TextView mMonthLoveLevel;                   //感情
    private TextView mMonthCareerLevel;                 //事业
    private TextView mMonthMoneyLevel;                  //财运
    private TextView mMonthLove;                        //感情
    private TextView mMonthCareer;                      //事业
    private TextView mMonthMoney;                       //财运

    private SpannableStringBuilder mMonthLoveText = new SpannableStringBuilder();       //感情正文
    private SpannableStringBuilder mMonthCareerText = new SpannableStringBuilder();     //事业正文
    private SpannableStringBuilder mMonthMoneyText = new SpannableStringBuilder();      //财运正文

    private int mLoveLevel;                             //感情
    private int mCareerLevel;                           //事业
    private int mMoneyLevel;                            //财运

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_fortune_lay);
        initView();
        getMonthFortune();
    }

    private void initView() {
        mMonthFortuneLay = findViewById(R.id.month_fortune);
        mMonthLoveImage = (TextView) findViewById(R.id.month_love_img);
        mMonthCareerImage = (TextView) findViewById(R.id.month_career_img);
        mMonthMoneyImage = (TextView) findViewById(R.id.month_money_img);
        mMonthLoveLevel = (TextView) findViewById(R.id.month_love_level);
        mMonthCareerLevel = (TextView) findViewById(R.id.month_career_level);
        mMonthMoneyLevel = (TextView) findViewById(R.id.month_money_level);
        mMonthLove = (TextView) findViewById(R.id.month_love);
        mMonthCareer = (TextView) findViewById(R.id.month_career);
        mMonthMoney = (TextView) findViewById(R.id.month_money);

        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.month_fortune);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
    }

    private void getMonthFortune() {
        showLoadingDialog();
        KDSPApiController.getInstance().findLuckymonth(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    final JSONArray jsonArray = response.optJSONArray("list");
                    mMonthLoveText.clear();
                    mMonthCareerText.clear();
                    mMonthMoneyText.clear();
                    mMonthLoveText.append("感情:").append(jsonArray.optJSONObject(0).optString("description")).append("\n");
                    mMonthCareerText.append("事业:").append(jsonArray.optJSONObject(1).optString("description")).append("\n");
                    mMonthMoneyText.append("财运:").append(jsonArray.optJSONObject(2).optString("description")).append("\n");
                    mMonthLoveText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(MonthFortuneActivity.this, R.color.pink3)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mMonthLoveText.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mMonthCareerText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(MonthFortuneActivity.this, R.color.pink3)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mMonthCareerText.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mMonthMoneyText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(MonthFortuneActivity.this, R.color.pink3)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mMonthMoneyText.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mLoveLevel = StringUtils.getCountofChar(jsonArray.optJSONObject(0).optString("name"), '★');
                    mCareerLevel = StringUtils.getCountofChar(jsonArray.optJSONObject(1).optString("name"), '★');
                    mMoneyLevel = StringUtils.getCountofChar(jsonArray.optJSONObject(2).optString("name"), '★');
                    MonthFortuneActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dismissLoadingDialog();
                                mMonthFortuneLay.setVisibility(View.VISIBLE);
                                switch (mLoveLevel) {
                                    case 1:
                                        mMonthLoveImage.setBackgroundResource(R.drawable.level1);
                                        mMonthLoveLevel.setText("★");
                                        break;
                                    case 2:
                                        mMonthLoveImage.setBackgroundResource(R.drawable.level2);
                                        mMonthLoveLevel.setText("★★");
                                        break;
                                    case 3:
                                        mMonthLoveImage.setBackgroundResource(R.drawable.level3);
                                        mMonthLoveLevel.setText("★★★");
                                        break;
                                    case 4:
                                        mMonthLoveImage.setBackgroundResource(R.drawable.level4);
                                        mMonthLoveLevel.setText("★★★★");
                                        break;
                                    case 5:
                                        mMonthLoveImage.setBackgroundResource(R.drawable.level5);
                                        mMonthLoveLevel.setText("★★★★★");
                                        break;
                                }
                                switch (mCareerLevel) {
                                    case 1:
                                        mMonthCareerImage.setBackgroundResource(R.drawable.level1);
                                        mMonthCareerLevel.setText("★");
                                        break;
                                    case 2:
                                        mMonthCareerImage.setBackgroundResource(R.drawable.level2);
                                        mMonthCareerLevel.setText("★★");
                                        break;
                                    case 3:
                                        mMonthCareerImage.setBackgroundResource(R.drawable.level3);
                                        mMonthCareerLevel.setText("★★★");
                                        break;
                                    case 4:
                                        mMonthCareerImage.setBackgroundResource(R.drawable.level4);
                                        mMonthCareerLevel.setText("★★★★");
                                        break;
                                    case 5:
                                        mMonthCareerImage.setBackgroundResource(R.drawable.level5);
                                        mMonthCareerLevel.setText("★★★★★");
                                        break;
                                }
                                switch (mMoneyLevel) {
                                    case 1:
                                        mMonthMoneyImage.setBackgroundResource(R.drawable.level1);
                                        mMonthMoneyLevel.setText("★");
                                        break;
                                    case 2:
                                        mMonthMoneyImage.setBackgroundResource(R.drawable.level2);
                                        mMonthMoneyLevel.setText("★★");
                                        break;
                                    case 3:
                                        mMonthMoneyImage.setBackgroundResource(R.drawable.level3);
                                        mMonthMoneyLevel.setText("★★★");
                                        break;
                                    case 4:
                                        mMonthMoneyImage.setBackgroundResource(R.drawable.level4);
                                        mMonthMoneyLevel.setText("★★★★");
                                        break;
                                    case 5:
                                        mMonthMoneyImage.setBackgroundResource(R.drawable.level5);
                                        mMonthMoneyLevel.setText("★★★★★");
                                        break;
                                }
                                mMonthLove.setText(mMonthLoveText);
                                mMonthCareer.setText(mMonthCareerText);
                                mMonthMoney.setText(mMonthMoneyText);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                mMonthFortuneLay.setVisibility(View.GONE);
                findViewById(R.id.bad_lay).setVisibility(View.VISIBLE);
                findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        getMonthFortune();
                    }
                });
            }
        });
    }

    /**
     * 跳转至页面
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MonthFortuneActivity.class);
        context.startActivity(intent);
    }
}
