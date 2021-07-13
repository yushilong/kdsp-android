package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.Line;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.widget.LineNumView;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhouyue on 11/15/17.
 * 生命灵线的activity
 */
public class LifeNumberActivity extends BaseActivity {
    @BindView(R.id.go_back)
    ImageView      mGoBack;
    @BindView(R.id.title_txt)
    TextView       mTitleTxt;
    @BindView(R.id.life_number_tv)
    TextView       mLifeNumberTv;
    @BindView(R.id.click_see_tv)
    TextView       mClickSeeTv;
    @BindView(R.id.line_one_tv)
    TextView       mLineOneTv;
    @BindView(R.id.line_two_tv)
    TextView       mLineTwoTv;
    @BindView(R.id.line_three_tv)
    TextView       mLineThreeTv;
    @BindView(R.id.see_line_tv)
    TextView       mSeeLineTv;
    @BindView(R.id.life_number_view)
    LineNumView    mlifeNumberImage;
    @BindView(R.id.line_empty_rl)
    RelativeLayout mLineEmptyRl;
    @BindView(R.id.line_normal_ll)
    LinearLayout   mLineNormalLl;
    @BindView(R.id.line_four_tv)
    TextView       mLineFourTv;
    @BindView(R.id.normal_sl)
    ScrollView     mNormalSl;
    private ArrayList<Line> mLines = new ArrayList<>();
    private int    lineNumber;
    private String mColor;
    private String mDesc;
    private String mSymbol;
    private String mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_number_activity);
        ButterKnife.bind(this);
        initUI();
        getData();
    }

    private void initUI() {
        mTitleTxt.setText(R.string.life_line);
        findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getData();
            }
        });
    }

    private void getData() {
        showLoadingDialog();
        String birthday = new DateTime().toServerMinString();
        if (AppContext.mUser != null) {
            birthday = new DateTime(AppContext.mUser.birthTime).toServerMinString();
        }
        KDSPApiController.getInstance().getLifeNumData(birthday, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                JSONObject lifeData = response.optJSONObject("lifeData");
                JSONObject lifeNumData = lifeData.optJSONObject("lifeNumData");
                mLines = Line.parseListFromJSON(lifeData.optJSONArray("lines"));
                mColor = lifeNumData.optString("color");
                mDesc = lifeNumData.optString("desc");
                mSymbol = lifeNumData.optString("symbol");
                mTitle = lifeNumData.optString("title");
                lineNumber = lifeNumData.optInt("lifeNum");

                LifeNumberActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        refreshUI();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                mNormalSl.setVisibility(View.GONE);
                findViewById(R.id.request_bad).setVisibility(View.VISIBLE);
                dismissLoadingDialog();
                showFailureMessage(error);
            }
        });
    }


    private void refreshUI() {
        mNormalSl.setVisibility(View.VISIBLE);
        findViewById(R.id.request_bad).setVisibility(View.GONE);
        if (mLines.isEmpty()) {
            mLineEmptyRl.setVisibility(View.VISIBLE);
            mLineNormalLl.setVisibility(View.GONE);

        } else {
            mlifeNumberImage.setData(mLines);
            mLineEmptyRl.setVisibility(View.GONE);
            mLineNormalLl.setVisibility(View.VISIBLE);
            for (int i=0;i<mLines.size();i++){
                if(i==0){
                    mLineOneTv.setText(mLines.get(0).line + mLines.get(0).lineName);
                    mLineOneTv.setVisibility(View.VISIBLE);
                }
                if(i==1){
                    mLineTwoTv.setText(mLines.get(1).line + mLines.get(1).lineName);
                    mLineTwoTv.setVisibility(View.VISIBLE);
                }
                if(i==2){
                    mLineThreeTv.setText(mLines.get(2).line + mLines.get(2).lineName);
                    mLineThreeTv.setVisibility(View.VISIBLE);
                }

            }

            if (mLines.size() > 3) {
                mLineFourTv.setVisibility(View.VISIBLE);
            } else {
                mLineFourTv.setVisibility(View.GONE);
            }
        }

        mLifeNumberTv.setText("" + lineNumber);
    }


    /**
     * 跳转至命格页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, LifeNumberActivity.class);
        context.startActivity(intent);
    }


    @OnClick({R.id.go_back, R.id.click_see_tv, R.id.see_line_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_back:
                goBack();
                break;
            case R.id.click_see_tv:
                LifeNumberResultActivity.goToPage(LifeNumberActivity.this, 1, mTitle, lineNumber, mColor, mSymbol, mDesc, null,null);
                break;
            case R.id.see_line_tv:
                LifeNumberResultActivity.goToPage(LifeNumberActivity.this, 2, "", 0, "", "", "", mLines,null);
                break;
        }
    }
}
