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
import com.qizhu.rili.ui.fragment.LifeTimeViewPagerFragment;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;

/**
 * Created by lindow on 2/21/16.
 * 我的一生的activity
 */
public class LifeTimeActivity extends BaseActivity {
    private TextView mLifeName;
    private ImageView mImage1;          //导航
    private ImageView mImage2;
    private ImageView mImage3;

    private int mUserSex;               //性别
    private DateTime mDateTime = new DateTime();         //分析的日期
    private boolean mIsMine;            //是否是个人分析
    private int mChildTab;              //子tab
    private int mCurrentPosition;       //当前position
    private String mElement;            //我的五行
    private LifeTimeViewPagerFragment mLifeTimeViewPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lifetime_lay);
        initUI();
    }

    private void initUI() {
        mLifeName = (TextView) findViewById(R.id.life_name);
        mImage1 = (ImageView) findViewById(R.id.image1);
        mImage2 = (ImageView) findViewById(R.id.image2);
        mImage3 = (ImageView) findViewById(R.id.image3);

        View mContentLay = findViewById(R.id.content_lay);
        MethodCompat.setBackground(mContentLay, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(LifeTimeActivity.this, R.drawable.analysis_board_bg)));

        findViewById(R.id.to_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLifeTimeViewPagerFragment != null && mCurrentPosition > 0) {
                    mLifeTimeViewPagerFragment.setCurrentFragment(mCurrentPosition - 1, true);
                }
            }
        });
        findViewById(R.id.to_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLifeTimeViewPagerFragment != null && mCurrentPosition < 3) {
                    mLifeTimeViewPagerFragment.setCurrentFragment(mCurrentPosition + 1, true);
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
                OperUtils.mKeyCat = OperUtils.KEY_CAT_FRIEND_LIFE;
                ShareActivity.goToMiniShare(LifeTimeActivity.this, ShareUtils.getShareTitle(mIsMine ? ShareUtils.Share_Type_MY_LIFETIME : ShareUtils.Share_Type_FRIENDS_LIFETIME, ""),
                        ShareUtils.getShareContent(mIsMine ? ShareUtils.Share_Type_MY_LIFETIME : ShareUtils.Share_Type_FRIENDS_LIFETIME, ""),
                        getShareUrl(), "", mIsMine ? ShareUtils.Share_Type_MY_LIFETIME : ShareUtils.Share_Type_FRIENDS_LIFETIME, mIsMine ? StatisticsConstant.subType_Fenxixiaohuoban : StatisticsConstant.subType_Wodeyisheng
                ,"pages/pocket/calculate/buddy/buddy_detail?birthday="+ mDateTime+ "&sex=" + mUserSex + "&key="+"mylife");
            }
        });

        TextView mTitle = (TextView) findViewById(R.id.title);
        if (mIsMine) {
            mTitle.setText("我的一生");
        } else {
            mTitle.setText("TA的一生");
        }
    }

    private void initViewPagerFragment() {
        mElement = CalendarCore.getElementName(mDateTime);
        mLifeTimeViewPagerFragment = LifeTimeViewPagerFragment.newInstance(mElement, mUserSex, mChildTab);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mLifeTimeViewPagerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void changeIndicator(int position) {
        switch (position) {
            case 0:
                mLifeName.setText("TA一生的属性&颜色");
                mImage1.setImageResource(R.drawable.circle_purple);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                break;
            case 1:
                mLifeName.setText("一生最适合他的职业");
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_purple);
                mImage3.setImageResource(R.drawable.circle_gray30);
                break;
            case 2:
                mLifeName.setText("吃货须知");
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_purple);
                break;
        }
        mCurrentPosition = position;
    }

    private String getShareUrl() {
        if (mIsMine) {
            return AppConfig.API_BASE + "app/shareExt/myLifetime" + "?userId=" + AppContext.userId + "&shareType=16";
        } else {
            return AppConfig.API_BASE + "app/shareExt/juniorPartnerLifetime" + "?birthday=" + DateUtils.getWebTimeFormatDate(mDateTime.getDate()) + "&sex=" + mUserSex + "&shareType=17";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //不保存activity状态防止viewpager状态保存发生重影
    }

    /**
     * 跳转至一生页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab) {
        goToPage(context, sex, dateTime, childTab, false);
    }

    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab, boolean isMine) {
        Intent intent = new Intent(context, LifeTimeActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_USER_SEX, sex);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, childTab);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, isMine);
        context.startActivity(intent);
    }
}
