package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.widget.FitWidthImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 生肖大运
 */
public class AnimalsLuckActivity extends BaseActivity {

    @BindView(R.id.animals_tv)
    TextView          mAnimalsTv;
    @BindView(R.id.key_word_tv)
    TextView          mKeyWordTv;
    @BindView(R.id.too_old_location_tv)
    TextView          mTooOldLocationTv;
    @BindView(R.id.luck_stars_tv)
    TextView          mLuckStarsTv;
    @BindView(R.id.bad_stars_tv)
    TextView          mBadStarsTv;
    @BindView(R.id.color_luck_tv)
    TextView          mColorLuckTv;
    @BindView(R.id.title_total_tv)
    TextView          mTitleTotalTv;
    @BindView(R.id.content_total_tv)
    TextView          mContentTotalTv;
    @BindView(R.id.content_total_image)
    FitWidthImageView mContentTotalImage;
    @BindView(R.id.content_total_goods_tv)
    TextView          mContentTotalGoodsTv;
    @BindView(R.id.content_total_goods_btn)
    TextView          mContentTotalGoodsBtn;
    @BindView(R.id.title_fortune_tv)
    TextView          mTitleFortuneTv;
    @BindView(R.id.content_fortune_tv)
    TextView          mContentFortuneTv;
    @BindView(R.id.title_career_tv)
    TextView          mTitleCareerTv;
    @BindView(R.id.content_career_tv)
    TextView          mContentCareerTv;
    @BindView(R.id.title_love_tv)
    TextView          mTitleLoveTv;
    @BindView(R.id.content_love_tv)
    TextView          mContentLoveTv;
    @BindView(R.id.normal_lay)
    LinearLayout      mNormalLay;
    @BindView(R.id.img_bg)
    ImageView         mImgBg;
    @BindView(R.id.bad_txt)
    TextView          mBadTxt;
    @BindView(R.id.reload)
    ImageView         mReload;
    @BindView(R.id.bad_lay)
    View  mBadLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animals_activity);
        ButterKnife.bind(this);
        initUI();
        getData();
    }


    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.animals_luck);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.share_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MarraigeShareActivity.goToPage(AnimalsLuckActivity.this);
            }
        });


    }

    private void getData() {
        showLoadingDialog();
//        KDSPApiController.getInstance().getItemInfoByItemId(mItemId, new KDSPHttpCallBack() {
//            @Override
//            public void handleAPISuccessMessage(JSONObject response) {
//
//
//                AnimalsLuckActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissLoadingDialog();
//
//                        refreshUI();
//                    }
//                });
//            }
//
//            @Override
//            public void handleAPIFailureMessage(Throwable error, String reqCode) {
//                showFailureMessage(error);
//                mNormalLay.setVisibility(View.GONE);
//                mBadLay.setVisibility(View.VISIBLE);
//            }
//        });
    }


    private void refreshUI() {

    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, AnimalsLuckActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.content_total_goods_btn, R.id.reload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.content_total_goods_btn:
                break;
            case R.id.reload:
                break;
        }
    }

    @OnClick(R.id.confirm_tv)
    public void onViewClicked() {
    }
}
