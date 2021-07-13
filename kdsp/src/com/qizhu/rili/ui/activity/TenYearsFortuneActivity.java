package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.DeleteOrderDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lindow on 20/04/2017.
 * 十年大运
 */

public class TenYearsFortuneActivity extends BaseActivity {
    private ImageView mScore1;
    private ImageView mScore2;
    private ImageView mScore3;
    private ImageView mScore4;
    private ImageView mScore5;
    private ImageView mScore6;
    private ImageView mScore7;
    private ImageView mScore8;
    private ImageView mScore9;
    private ImageView mScore10;
    private TextView mYear1;
    private TextView mYear2;
    private TextView mYear3;
    private TextView mYear4;
    private TextView mYear5;
    private TextView mYear6;
    private TextView mYear7;
    private TextView mYear8;
    private TextView mYear9;
    private TextView mYear10;
    private TextView mYearTitle1;
    private TextView mYearTitle2;
    private TextView mYearTitle3;
    private TextView mYearTitle4;
    private TextView mYearTitle5;
    private TextView mYearTitle6;
    private TextView mYearTitle7;
    private TextView mYearTitle8;
    private TextView mYearTitle9;
    private TextView mYearTitle10;
    private TextView mResult1;
    private TextView mResult2;
    private TextView mResult3;
    private TextView mResult4;
    private TextView mResult5;
    private TextView mResult6;
    private TextView mResult7;
    private TextView mResult8;
    private TextView mResult9;
    private TextView mResult10;
    private TextView mRightText;

    private String mItemId;                 //问题id
    private String mIoId;                   //当前的订单id
    private boolean mMode;                  //模式,false为下单，true为订单详情

    private HashMap<String, Integer> mScoreMap = new HashMap<>();       //分数
    private HashMap<String, Integer> mTitleMap = new HashMap<>();       //标题
    private ArrayList<String> mYears = new ArrayList<>();               //年
    private ArrayList<Integer> mScores = new ArrayList<>();             //每年的分数
    private ArrayList<String> mLiuYears = new ArrayList<>();            //流年
    private ArrayList<String> mYearsTitles = new ArrayList<>();         //年的标题
    private ArrayList<String> mResults = new ArrayList<>();             //结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_years_fortune_lay);
        getIntentExtra();
        initView();
        getData();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        mItemId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mIoId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mMode = intent.getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mRightText = (TextView) findViewById(R.id.right_text);
        mTitle.setText(R.string.ten_years_fortune);
        mRightText.setText(R.string.delete);
        mRightText.setClickable(false);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        mRightText.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(DeleteOrderDialogFragment.newInstance(DeleteOrderDialogFragment.DEL, mIoId), "删除订单");
            }
        });


        mScore1 = (ImageView) findViewById(R.id.score1);
        mScore2 = (ImageView) findViewById(R.id.score2);
        mScore3 = (ImageView) findViewById(R.id.score3);
        mScore4 = (ImageView) findViewById(R.id.score4);
        mScore5 = (ImageView) findViewById(R.id.score5);
        mScore6 = (ImageView) findViewById(R.id.score6);
        mScore7 = (ImageView) findViewById(R.id.score7);
        mScore8 = (ImageView) findViewById(R.id.score8);
        mScore9 = (ImageView) findViewById(R.id.score9);
        mScore10 = (ImageView) findViewById(R.id.score10);
        mYear1 = (TextView) findViewById(R.id.year1);
        mYear2 = (TextView) findViewById(R.id.year2);
        mYear3 = (TextView) findViewById(R.id.year3);
        mYear4 = (TextView) findViewById(R.id.year4);
        mYear5 = (TextView) findViewById(R.id.year5);
        mYear6 = (TextView) findViewById(R.id.year6);
        mYear7 = (TextView) findViewById(R.id.year7);
        mYear8 = (TextView) findViewById(R.id.year8);
        mYear9 = (TextView) findViewById(R.id.year9);
        mYear10 = (TextView) findViewById(R.id.year10);
        mYearTitle1 = (TextView) findViewById(R.id.year_title1);
        mYearTitle2 = (TextView) findViewById(R.id.year_title2);
        mYearTitle3 = (TextView) findViewById(R.id.year_title3);
        mYearTitle4 = (TextView) findViewById(R.id.year_title4);
        mYearTitle5 = (TextView) findViewById(R.id.year_title5);
        mYearTitle6 = (TextView) findViewById(R.id.year_title6);
        mYearTitle7 = (TextView) findViewById(R.id.year_title7);
        mYearTitle8 = (TextView) findViewById(R.id.year_title8);
        mYearTitle9 = (TextView) findViewById(R.id.year_title9);
        mYearTitle10 = (TextView) findViewById(R.id.year_title10);
        mResult1 = (TextView) findViewById(R.id.result1);
        mResult2 = (TextView) findViewById(R.id.result2);
        mResult3 = (TextView) findViewById(R.id.result3);
        mResult4 = (TextView) findViewById(R.id.result4);
        mResult5 = (TextView) findViewById(R.id.result5);
        mResult6 = (TextView) findViewById(R.id.result6);
        mResult7 = (TextView) findViewById(R.id.result7);
        mResult8 = (TextView) findViewById(R.id.result8);
        mResult9 = (TextView) findViewById(R.id.result9);
        mResult10 = (TextView) findViewById(R.id.result10);

        findViewById(R.id.augury).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AugurySubmitActivity.goToPage(TenYearsFortuneActivity.this, "", mItemId, "", "", 3);
            }
        });

        initScoreMap();
    }

    private void initScoreMap() {
        mScoreMap.put("食神", R.drawable.score_shishen);
        mScoreMap.put("偏财", R.drawable.score_piancai);
        mScoreMap.put("正财", R.drawable.score_zhengcai);
        mScoreMap.put("伤官", R.drawable.score_shangguan);
        mScoreMap.put("劫财", R.drawable.score_jiecai);
        mScoreMap.put("偏官", R.drawable.score_qisha);
        mScoreMap.put("偏印", R.drawable.score_pianyin);
        mScoreMap.put("比肩", R.drawable.score_bijian);
        mScoreMap.put("正官", R.drawable.score_zhengguan);
        mScoreMap.put("正印", R.drawable.score_zhengyin);

        mTitleMap.put("食神", R.drawable.shishen);
        mTitleMap.put("偏财", R.drawable.piancai);
        mTitleMap.put("正财", R.drawable.zhengcai);
        mTitleMap.put("伤官", R.drawable.shangguan);
        mTitleMap.put("劫财", R.drawable.jiecai);
        mTitleMap.put("偏官", R.drawable.qisha);
        mTitleMap.put("偏印", R.drawable.pianyin);
        mTitleMap.put("比肩", R.drawable.bijian);
        mTitleMap.put("正官", R.drawable.zhengguan);
        mTitleMap.put("正印", R.drawable.zhengyin);
    }

    private void getData() {
        showLoadingDialog();
        if (mMode) {
            mRightText.setVisibility(View.VISIBLE);
            KDSPApiController.getInstance().getFortuneAnswer(mIoId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    JSONArray scores = response.optJSONArray("scores");
                    for (int i = 0; i < scores.length(); i++) {
                        mScores.add(scores.optInt(i));
                    }
                    JSONArray liuYears = response.optJSONArray("liuYears");
                    for (int i = 0; i < liuYears.length(); i++) {
                        mLiuYears.add(liuYears.optString(i));
                    }
                    JSONArray years = response.optJSONArray("years");
                    for (int i = 0; i < years.length(); i++) {
                        mYears.add(years.optString(i));
                    }
                    JSONArray yearsTitles = response.optJSONArray("yearsTitles");
                    for (int i = 0; i < yearsTitles.length(); i++) {
                        mYearsTitles.add(yearsTitles.optString(i));
                    }
                    JSONArray results = response.optJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        mResults.add(results.optString(i));
                    }
                    TenYearsFortuneActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUI();
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    showFailureMessage(error);
                    dismissLoadingDialog();
                }
            });
        } else {
            mRightText.setVisibility(View.GONE);
            KDSPApiController.getInstance().getFortuneScore(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    JSONArray scores = response.optJSONArray("scores");
                    for (int i = 0; i < scores.length(); i++) {
                        mScores.add(scores.optInt(i));
                    }
                    JSONArray liuYears = response.optJSONArray("liuYears");
                    for (int i = 0; i < liuYears.length(); i++) {
                        mLiuYears.add(liuYears.optString(i));
                    }
                    JSONArray years = response.optJSONArray("years");
                    for (int i = 0; i < years.length(); i++) {
                        mYears.add(years.optString(i));
                    }
                    TenYearsFortuneActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUI();
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    showFailureMessage(error);
                    dismissLoadingDialog();
                }
            });
        }
    }

    private void refreshUI() {
        dismissLoadingDialog();
        try {
            mScore1.setImageResource(mScoreMap.get(mLiuYears.get(0)));
            mScore2.setImageResource(mScoreMap.get(mLiuYears.get(1)));
            mScore3.setImageResource(mScoreMap.get(mLiuYears.get(2)));
            mScore4.setImageResource(mScoreMap.get(mLiuYears.get(3)));
            mScore5.setImageResource(mScoreMap.get(mLiuYears.get(4)));
            mScore6.setImageResource(mScoreMap.get(mLiuYears.get(5)));
            mScore7.setImageResource(mScoreMap.get(mLiuYears.get(6)));
            mScore8.setImageResource(mScoreMap.get(mLiuYears.get(7)));
            mScore9.setImageResource(mScoreMap.get(mLiuYears.get(8)));
            mScore10.setImageResource(mScoreMap.get(mLiuYears.get(9)));

            UIUtils.setMargins(mScore1, DisplayUtils.dip2px(54 - 10), DisplayUtils.dip2px((5 - mScores.get(0)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore2, DisplayUtils.dip2px(54 * 2 - 10), DisplayUtils.dip2px((5 - mScores.get(1)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore3, DisplayUtils.dip2px(54 * 3 - 10), DisplayUtils.dip2px((5 - mScores.get(2)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore4, DisplayUtils.dip2px(54 * 4 - 10), DisplayUtils.dip2px((5 - mScores.get(3)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore5, DisplayUtils.dip2px(54 * 5 - 10), DisplayUtils.dip2px((5 - mScores.get(4)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore6, DisplayUtils.dip2px(54 * 6 - 10), DisplayUtils.dip2px((5 - mScores.get(5)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore7, DisplayUtils.dip2px(54 * 7 - 10), DisplayUtils.dip2px((5 - mScores.get(6)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore8, DisplayUtils.dip2px(54 * 8 - 10), DisplayUtils.dip2px((5 - mScores.get(7)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore9, DisplayUtils.dip2px(54 * 9 - 10), DisplayUtils.dip2px((5 - mScores.get(8)) * 36 + 35 - 10), 0, 0);
            UIUtils.setMargins(mScore10, DisplayUtils.dip2px(54 * 10 - 10), DisplayUtils.dip2px((5 - mScores.get(9)) * 36 + 35 - 10), 0, 0);

            mYear1.setText(mYears.get(0));
            mYear2.setText(mYears.get(1));
            mYear3.setText(mYears.get(2));
            mYear4.setText(mYears.get(3));
            mYear5.setText(mYears.get(4));
            mYear6.setText(mYears.get(5));
            mYear7.setText(mYears.get(6));
            mYear8.setText(mYears.get(7));
            mYear9.setText(mYears.get(8));
            mYear10.setText(mYears.get(9));

            if (mMode) {
                findViewById(R.id.fortune_tip).setVisibility(View.GONE);
                findViewById(R.id.augury).setVisibility(View.GONE);
                mYearTitle1.setVisibility(View.VISIBLE);
                mYearTitle2.setVisibility(View.VISIBLE);
                mYearTitle3.setVisibility(View.VISIBLE);
                mYearTitle4.setVisibility(View.VISIBLE);
                mYearTitle5.setVisibility(View.VISIBLE);
                mYearTitle6.setVisibility(View.VISIBLE);
                mYearTitle7.setVisibility(View.VISIBLE);
                mYearTitle8.setVisibility(View.VISIBLE);
                mYearTitle9.setVisibility(View.VISIBLE);
                mYearTitle10.setVisibility(View.VISIBLE);
                mResult1.setVisibility(View.VISIBLE);
                mResult2.setVisibility(View.VISIBLE);
                mResult3.setVisibility(View.VISIBLE);
                mResult4.setVisibility(View.VISIBLE);
                mResult5.setVisibility(View.VISIBLE);
                mResult6.setVisibility(View.VISIBLE);
                mResult7.setVisibility(View.VISIBLE);
                mResult8.setVisibility(View.VISIBLE);
                mResult9.setVisibility(View.VISIBLE);
                mResult10.setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.fortune_tip).setVisibility(View.VISIBLE);
                findViewById(R.id.augury).setVisibility(View.VISIBLE);
            }

            mYearTitle1.setText(mYearsTitles.get(0));
            mYearTitle1.setBackgroundResource(mTitleMap.get(mLiuYears.get(0)));
            mYearTitle2.setText(mYearsTitles.get(1));
            mYearTitle2.setBackgroundResource(mTitleMap.get(mLiuYears.get(1)));
            mYearTitle3.setText(mYearsTitles.get(2));
            mYearTitle3.setBackgroundResource(mTitleMap.get(mLiuYears.get(2)));
            mYearTitle4.setText(mYearsTitles.get(3));
            mYearTitle4.setBackgroundResource(mTitleMap.get(mLiuYears.get(3)));
            mYearTitle5.setText(mYearsTitles.get(4));
            mYearTitle5.setBackgroundResource(mTitleMap.get(mLiuYears.get(4)));
            mYearTitle6.setText(mYearsTitles.get(5));
            mYearTitle6.setBackgroundResource(mTitleMap.get(mLiuYears.get(5)));
            mYearTitle7.setText(mYearsTitles.get(6));
            mYearTitle7.setBackgroundResource(mTitleMap.get(mLiuYears.get(6)));
            mYearTitle8.setText(mYearsTitles.get(7));
            mYearTitle8.setBackgroundResource(mTitleMap.get(mLiuYears.get(7)));
            mYearTitle9.setText(mYearsTitles.get(8));
            mYearTitle9.setBackgroundResource(mTitleMap.get(mLiuYears.get(8)));
            mYearTitle10.setText(mYearsTitles.get(9));
            mYearTitle10.setBackgroundResource(mTitleMap.get(mLiuYears.get(9)));

            mResult1.setText(mResults.get(0));
            mResult2.setText(mResults.get(1));
            mResult3.setText(mResults.get(2));
            mResult4.setText(mResults.get(3));
            mResult5.setText(mResults.get(4));
            mResult6.setText(mResults.get(5));
            mResult7.setText(mResults.get(6));
            mResult8.setText(mResults.get(7));
            mResult9.setText(mResults.get(8));
            mResult10.setText(mResults.get(9));

            mRightText.setClickable(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void goToPage(Context context, String id) {
        Intent intent = new Intent(context, TenYearsFortuneActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, id);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String id, boolean mode) {
        Intent intent = new Intent(context, TenYearsFortuneActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, id);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mode);
        context.startActivity(intent);
    }
}
