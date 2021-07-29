package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.fragment.StarViewPagerFragment;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;

/**
 * Created by lindow on 10/20/15.
 * 星宿的activity
 */
public class StarActivity extends BaseActivity {
    private TextView mStarTip;              //星宿
    private TextView mStellarName;          //类别名

    private ImageView mImage1;          //导航
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;
    private ImageView mImage5;

    private int mUserSex;               //性别
    private DateTime mDateTime = new DateTime();         //分析的日期
    private boolean mIsMine;            //是否是个人分析
    private int mChildTab;              //子tab
    private int mCurrentPosition;       //当前position
    private StarViewPagerFragment mStarViewPagerfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.star_lay);
        initUI();
    }

    private void initUI() {
        mStarTip = (TextView) findViewById(R.id.star_tip);
        mStellarName = (TextView) findViewById(R.id.stellar_name);
        mImage1 = (ImageView) findViewById(R.id.image1);
        mImage2 = (ImageView) findViewById(R.id.image2);
        mImage3 = (ImageView) findViewById(R.id.image3);
        mImage4 = (ImageView) findViewById(R.id.image4);
        mImage5 = (ImageView) findViewById(R.id.image5);

        View mContentLay = findViewById(R.id.content_lay);
        MethodCompat.setBackground(mContentLay, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(StarActivity.this, R.drawable.analysis_board_bg)));

        findViewById(R.id.to_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStarViewPagerfragment != null && mCurrentPosition > 0) {
                    mStarViewPagerfragment.setCurrentFragment(mCurrentPosition - 1, true);
                }
            }
        });
        findViewById(R.id.to_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStarViewPagerfragment != null && mCurrentPosition < 4) {
                    mStarViewPagerfragment.setCurrentFragment(mCurrentPosition + 1, true);
                }
            }
        });

        Intent intent = getIntent();
        mUserSex = intent.getIntExtra(IntentExtraConfig.EXTRA_USER_SEX, 0);
        mDateTime = intent.getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        mIsMine = intent.getBooleanExtra(IntentExtraConfig.EXTRA_IS_MINE, false);
        mChildTab = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);

        initViewPagerFragment();
        changeIndicator(mChildTab);

        mStarTip.setText(CalendarCore.getStellarSub(mDateTime));

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.share_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OperUtils.mSmallCat = OperUtils.SMALL_CAT_OTHER;
                OperUtils.mKeyCat = OperUtils.KEY_CAT_FRIEND_STAR;
                ShareActivity.goToMiniShare(StarActivity.this, ShareUtils.getShareTitle(mIsMine ? ShareUtils.Share_Type_STAR_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, ""),
                        ShareUtils.getShareContent(mIsMine ? ShareUtils.Share_Type_STAR_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, ""),
                        getShareUrl(), "", mIsMine ? ShareUtils.Share_Type_STAR_SELF : ShareUtils.Share_Type_ANALYSIS_FRIENDS, mIsMine ? StatisticsConstant.subType_Fenxixiaohuoban : StatisticsConstant.subType_Wodexingxiu
                ,"pages/pocket/calculate/buddy/buddy_detail?birthday="+ mDateTime+ "&sex=" + mUserSex + "&key="+"stellar");
            }
        });
    }

    private void initViewPagerFragment() {
        mStarViewPagerfragment = StarViewPagerFragment.newInstance(mDateTime, mChildTab);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mStarViewPagerfragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void changeIndicator(int position) {
        switch (position) {
            case 0:
                mStellarName.setText(R.string.stellar_character);
                mImage1.setImageResource(R.drawable.circle_purple);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                break;
            case 1:
                mStellarName.setText(R.string.stellar_health);
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_purple);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                break;
            case 2:
                mStellarName.setText(R.string.stellar_career);
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_purple);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                break;
            case 3:
                mStellarName.setText(R.string.stellar_fortune);
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_purple);
                mImage5.setImageResource(R.drawable.circle_gray30);
                break;
            case 4:
                mStellarName.setText(R.string.stellar_love);
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_purple);
                break;
        }
        mCurrentPosition = position;
    }

    private String getShareUrl() {
        if (mIsMine) {
            return AppConfig.API_BASE + "app/shareExt/myStars" + "?userId=" + AppContext.userId + "&shareType=2";
        } else {
            return AppConfig.API_BASE + "app/shareExt/analysisFriendXingxiu" + "?birthday=" + DateUtils.getWebTimeFormatDate(mDateTime.getDate()) + "&shareType=10";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //不保存activity状态防止viewpager状态保存发生重影
    }

    /**
     * 跳转至星宿页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab) {
        goToPage(context, sex, dateTime, childTab, false);
    }

    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab, boolean isMine) {
        Intent intent = new Intent(context, StarActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_USER_SEX, sex);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, childTab);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, isMine);
        context.startActivity(intent);
    }
}
