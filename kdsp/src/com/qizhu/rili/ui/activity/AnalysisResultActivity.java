package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.fragment.LifeViewPagerFragment;
import com.qizhu.rili.ui.fragment.StarViewPagerFragment;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ShareUtils;

/**
 * 分析结果页
 */
public class AnalysisResultActivity extends BaseActivity {
    private TextView mDisposition;          //性格
    private TextView mStars;                //你的星宿
    private ImageView mShare;               //分享

    private int mUserSex;               //性别
    private DateTime mDateTime;         //分析的日期
    private boolean mIsMine;            //是否是个人分析
    private int mChildTab;              //子tab
    private int mCurrentTab;            //子tab的选中tab页
    private LifeViewPagerFragment mLifeViewPagerFragment;           //命格
    private StarViewPagerFragment mStarViewPagerfragment;           //星宿

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.analysis_result);
        initUI();
    }

    protected void initUI() {
        mDisposition = (TextView) findViewById(R.id.disposition);
        mStars = (TextView) findViewById(R.id.star);
        mShare = (ImageView) findViewById(R.id.share_btn);

        Intent intent = getIntent();
        mUserSex = intent.getIntExtra(IntentExtraConfig.EXTRA_USER_SEX, 0);
        mDateTime = intent.getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        mIsMine = intent.getBooleanExtra(IntentExtraConfig.EXTRA_IS_MINE, false);
        mChildTab = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);
        mCurrentTab = intent.getIntExtra(IntentExtraConfig.EXTRA_GROUP_ID, 0);

        setChooseTab(mChildTab);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        mDisposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChooseTab(0);
            }
        });
        mStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChooseTab(1);
            }
        });
        mShare.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (0 == mChildTab) {
                    ShareActivity.goToShare(AnalysisResultActivity.this, ShareUtils.getShareTitle(mIsMine ? ShareUtils.Share_Type_ANALYSIS_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, ""),
                            ShareUtils.getShareContent(mIsMine ? ShareUtils.Share_Type_ANALYSIS_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, ""),
                            getShareUrl(), "", mIsMine ? ShareUtils.Share_Type_ANALYSIS_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, StatisticsConstant.subType_Fenxixiaohuoban);
                } else {
                    ShareActivity.goToShare(AnalysisResultActivity.this, ShareUtils.getShareTitle(mIsMine ? ShareUtils.Share_Type_STAR_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, ""),
                            ShareUtils.getShareContent(mIsMine ? ShareUtils.Share_Type_STAR_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, ""),
                            getShareUrl(), "", mIsMine ? ShareUtils.Share_Type_STAR_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, StatisticsConstant.subType_Fenxixiaohuoban);
                }
            }
        });
    }

    /**
     * 子页面切换
     */
    private void setChooseTab(int childTab) {
        if (0 == childTab) {
            mDisposition.setTextColor(ContextCompat.getColor(this, R.color.purple));
            mDisposition.setBackgroundResource(R.drawable.left_selected);
            mStars.setTextColor(ContextCompat.getColor(this, R.color.white));
            mStars.setBackgroundResource(R.drawable.right_unselected);
            if (mLifeViewPagerFragment == null) {
                mLifeViewPagerFragment = LifeViewPagerFragment.newInstance(mDateTime, mCurrentTab, mIsMine);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (mStarViewPagerfragment != null) {
                fragmentTransaction.hide(mStarViewPagerfragment);
                if (mLifeViewPagerFragment.isAdded()) {
                    fragmentTransaction.show(mLifeViewPagerFragment);
                } else {
                    fragmentTransaction.add(R.id.body_fragment, mLifeViewPagerFragment);
                }
            } else {
                fragmentTransaction.add(R.id.body_fragment, mLifeViewPagerFragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            mDisposition.setTextColor(ContextCompat.getColor(this, R.color.white));
            mDisposition.setBackgroundResource(R.drawable.left_unselected);
            mStars.setTextColor(ContextCompat.getColor(this, R.color.purple));
            mStars.setBackgroundResource(R.drawable.right_selected);
            if (mStarViewPagerfragment == null) {
                mStarViewPagerfragment = StarViewPagerFragment.newInstance(mDateTime, 0);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (mLifeViewPagerFragment != null) {
                fragmentTransaction.hide(mLifeViewPagerFragment);
                if (mStarViewPagerfragment.isAdded()) {
                    fragmentTransaction.show(mStarViewPagerfragment);
                } else {
                    fragmentTransaction.add(R.id.body_fragment, mStarViewPagerfragment);
                }
            } else {
                fragmentTransaction.add(R.id.body_fragment, mStarViewPagerfragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
        mChildTab = childTab;
    }

    private String getShareUrl() {
        if (0 == mChildTab) {
            if (mIsMine) {
                return ShareUtils.BASE_SHARE_URL + "?userId=" + AppContext.userId + "&shareType=1";
            } else {
                return ShareUtils.BASE_SHARE_URL + "?friendBirth=" + DateUtils.getWebTimeFormatDate(mDateTime.getDate()) + "&shareType=9";
            }
        } else {
            if (mIsMine) {
                return ShareUtils.BASE_SHARE_URL + "?userId=" + AppContext.userId + "&shareType=2";
            } else {
                return ShareUtils.BASE_SHARE_URL + "?friendBirth=" + DateUtils.getWebTimeFormatDate(mDateTime.getDate()) + "&shareType=10";
            }
        }
    }

    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab) {
        goToPage(context, sex, dateTime, childTab, false);
    }

    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab, int currentTab) {
        Intent intent = new Intent(context, AnalysisResultActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_USER_SEX, sex);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, childTab);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, currentTab);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, false);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab, boolean isMine) {
        Intent intent = new Intent(context, AnalysisResultActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_USER_SEX, sex);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, childTab);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, isMine);
        context.startActivity(intent);
    }
}
