package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.qizhu.rili.ui.fragment.ShadowViewPagerFragment;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.StringUtils;

/**
 * Created by lindow on 10/29/15.
 * 上一世的影子
 */
public class ShadowActivity extends BaseActivity {
    private TextView mLifeName;
    private ImageView mImage1;          //导航
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;

    private int mUserSex;               //性别
    private DateTime mDateTime;         //分析的日期
    private boolean mIsMine;            //是否是个人分析
    private int mChildTab;              //子tab
    private int mCurrentPosition;       //当前position
    private int mOneInt;                    //日期转的int值里1的个数
    private ShadowViewPagerFragment mShadowViewPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shadow_lay);
        initUI();
    }

    private void initUI() {
        mLifeName = (TextView) findViewById(R.id.life_name);
        mImage1 = (ImageView) findViewById(R.id.image1);
        mImage2 = (ImageView) findViewById(R.id.image2);
        mImage3 = (ImageView) findViewById(R.id.image3);
        mImage4 = (ImageView) findViewById(R.id.image4);

        View mContentLay = findViewById(R.id.content_lay);
        MethodCompat.setBackground(mContentLay, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(ShadowActivity.this, R.drawable.analysis_board_bg)));

        findViewById(R.id.to_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShadowViewPagerFragment != null && mCurrentPosition > 0) {
                    mShadowViewPagerFragment.setCurrentFragment(mCurrentPosition - 1, true);
                }
            }
        });
        findViewById(R.id.to_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShadowViewPagerFragment != null && mCurrentPosition < 3) {
                    mShadowViewPagerFragment.setCurrentFragment(mCurrentPosition + 1, true);
                }
            }
        });

        Intent intent = getIntent();
        mUserSex = intent.getIntExtra(IntentExtraConfig.EXTRA_USER_SEX, 0);
        mDateTime = intent.getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        mIsMine = intent.getBooleanExtra(IntentExtraConfig.EXTRA_IS_MINE, false);
        mChildTab = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);

        mOneInt = StringUtils.getCountofChar(mDateTime.toDayInt(), '1');

        initViewPagerFragment();
        changeIndicator(mChildTab);

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
                OperUtils.mKeyCat = OperUtils.KEY_CAT_FRIEND_SHADOW;
                ShareActivity.goToMiniShare(ShadowActivity.this, ShareUtils.getShareTitle(mIsMine ? ShareUtils.Share_Type_MY_SHADOW : ShareUtils.Share_Type_FRIENDS_SHADOW, ""),
                        ShareUtils.getShareContent(mIsMine ? ShareUtils.Share_Type_MY_SHADOW : ShareUtils.Share_Type_FRIENDS_SHADOW, ""),
                        getShareUrl(), "", mIsMine ? ShareUtils.Share_Type_MY_SHADOW : ShareUtils.Share_Type_FRIENDS_SHADOW, mIsMine ? StatisticsConstant.subType_Fenxixiaohuoban : StatisticsConstant.subType_Wodeyingzi
                ,"pages/pocket/calculate/buddy/buddy_detail?birthday="+ mDateTime+ "&sex=" + mUserSex + "&key="+"shadow");
            }
        });
    }

    private void initViewPagerFragment() {
        mShadowViewPagerFragment = ShadowViewPagerFragment.newInstance(mDateTime, mUserSex, mChildTab);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mShadowViewPagerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void changeIndicator(int position) {
        switch (position) {
            case 0:
                mLifeName.setText("TA的真性情");
                mImage1.setImageResource(R.drawable.circle_purple);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                break;
            case 1:
                mLifeName.setText(CalendarCore.getLastWorldTitle(mOneInt, mUserSex));
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_purple);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                break;
            case 2:
                mLifeName.setText(R.string.feelings_word);
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_purple);
                mImage4.setImageResource(R.drawable.circle_gray30);
                break;
            case 3:
                mLifeName.setText(R.string.tips_for_you);
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_purple);
                break;
        }
        mCurrentPosition = position;
    }

    private String getShareUrl() {
        if (mIsMine) {
            return AppConfig.API_BASE + "app/shareExt/myShadow" + "?userId=" + AppContext.userId + "&shareType=12";
        } else {
            return AppConfig.API_BASE + "app/shareExt/analysisFriendYingzi" + "?birthday=" + DateUtils.getWebTimeFormatDate(mDateTime.getDate()) + "&sex=" + mUserSex + "&shareType=14";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //不保存activity状态防止viewpager状态保存发生重影
    }

    /**
     * 跳转至影子页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab) {
        goToPage(context, sex, dateTime, childTab, false);
    }

    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab, boolean isMine) {
        Intent intent = new Intent(context, ShadowActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_USER_SEX, sex);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, childTab);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, isMine);
        context.startActivity(intent);
    }
}
