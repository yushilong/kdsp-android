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
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.fragment.FeelingsViewPagerFragment;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.ShareUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lindow on 10/29/15.
 * 感情世界的activity
 */
public class FeelingsActivity extends BaseActivity {
    private TextView mFeelingsName;     //类别名
    private ImageView mImage1;          //导航
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;
    private ImageView mImage5;
    private ImageView mImage6;
    private ImageView mImage7;
    private ImageView mImage8;

    private int mUserSex;               //性别
    private DateTime mDateTime;         //分析的日期
    private boolean mIsMine;            //是否是个人分析
    private int mChildTab;              //子tab
    private int mSize;                  //总个数
    private int mCurrentPosition;       //当前position
    private ArrayList<Character> tempList = new ArrayList<Character>();
    private FeelingsViewPagerFragment mFeelingsViewPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feelings_lay);
        initUI();
    }

    private void initUI() {
        mFeelingsName = (TextView) findViewById(R.id.feelings_name);
        mImage1 = (ImageView) findViewById(R.id.image1);
        mImage2 = (ImageView) findViewById(R.id.image2);
        mImage3 = (ImageView) findViewById(R.id.image3);
        mImage4 = (ImageView) findViewById(R.id.image4);
        mImage5 = (ImageView) findViewById(R.id.image5);
        mImage6 = (ImageView) findViewById(R.id.image6);
        mImage7 = (ImageView) findViewById(R.id.image7);
        mImage8 = (ImageView) findViewById(R.id.image8);
        View mContentLay = findViewById(R.id.content_lay);
        MethodCompat.setBackground(mContentLay, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(FeelingsActivity.this, R.drawable.analysis_board_bg)));

        findViewById(R.id.to_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeelingsViewPagerFragment != null && mCurrentPosition > 0) {
                    mFeelingsViewPagerFragment.setCurrentFragment(mCurrentPosition - 1, true);
                }
            }
        });
        findViewById(R.id.to_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeelingsViewPagerFragment != null && mCurrentPosition < mSize - 1) {
                    mFeelingsViewPagerFragment.setCurrentFragment(mCurrentPosition + 1, true);
                }
            }
        });

        Intent intent = getIntent();
        mUserSex = intent.getIntExtra(IntentExtraConfig.EXTRA_USER_SEX, 0);
        mDateTime = intent.getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        mIsMine = intent.getBooleanExtra(IntentExtraConfig.EXTRA_IS_MINE, false);
        mChildTab = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);

        initViewPagerFragment();
        setIndicator();
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
                ShareActivity.goToShare(FeelingsActivity.this, ShareUtils.getShareTitle(mIsMine ? ShareUtils.Share_Type_MY_FEELINGS : ShareUtils.Share_Type_FRIENDS_FEELINGS, ""),
                        ShareUtils.getShareContent(mIsMine ? ShareUtils.Share_Type_MY_FEELINGS : ShareUtils.Share_Type_FRIENDS_FEELINGS, ""),
                        getShareUrl(), "", mIsMine ? ShareUtils.Share_Type_MY_FEELINGS : ShareUtils.Share_Type_FRIENDS_FEELINGS, mIsMine ? StatisticsConstant.subType_Fenxixiaohuoban : StatisticsConstant.subType_Wodetianfu);
            }
        });
    }

    private void initViewPagerFragment() {
        mFeelingsViewPagerFragment = FeelingsViewPagerFragment.newInstance(mDateTime, mChildTab);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mFeelingsViewPagerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void setIndicator() {
        tempList.clear();
        char[] charArray = mDateTime.toDayInt().toCharArray();
        Arrays.sort(charArray);
        for (char temp : charArray) {
            if (!tempList.contains(temp) && temp != '0') {
                tempList.add(temp);
            }
        }
        mSize = tempList.size();
        switch (mSize) {
            case 1:
                mImage2.setVisibility(View.GONE);
                mImage3.setVisibility(View.GONE);
                mImage4.setVisibility(View.GONE);
                mImage5.setVisibility(View.GONE);
                mImage6.setVisibility(View.GONE);
                mImage7.setVisibility(View.GONE);
                mImage8.setVisibility(View.GONE);
                break;
            case 2:
                mImage3.setVisibility(View.GONE);
                mImage4.setVisibility(View.GONE);
                mImage5.setVisibility(View.GONE);
                mImage6.setVisibility(View.GONE);
                mImage7.setVisibility(View.GONE);
                mImage8.setVisibility(View.GONE);
                break;
            case 3:
                mImage4.setVisibility(View.GONE);
                mImage5.setVisibility(View.GONE);
                mImage6.setVisibility(View.GONE);
                mImage7.setVisibility(View.GONE);
                mImage8.setVisibility(View.GONE);
                break;
            case 4:
                mImage5.setVisibility(View.GONE);
                mImage6.setVisibility(View.GONE);
                mImage7.setVisibility(View.GONE);
                mImage8.setVisibility(View.GONE);
                break;
            case 5:
                mImage6.setVisibility(View.GONE);
                mImage7.setVisibility(View.GONE);
                mImage8.setVisibility(View.GONE);
                break;
            case 6:
                mImage7.setVisibility(View.GONE);
                mImage8.setVisibility(View.GONE);
                break;
            case 7:
                mImage8.setVisibility(View.GONE);
                break;
        }
    }

    public void changeIndicator(int position) {
        switch (position) {
            case 0:
                mImage1.setImageResource(R.drawable.circle_purple);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                mImage6.setImageResource(R.drawable.circle_gray30);
                mImage7.setImageResource(R.drawable.circle_gray30);
                mImage8.setImageResource(R.drawable.circle_gray30);
                break;
            case 1:
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_purple);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                mImage6.setImageResource(R.drawable.circle_gray30);
                mImage7.setImageResource(R.drawable.circle_gray30);
                mImage8.setImageResource(R.drawable.circle_gray30);
                break;
            case 2:
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_purple);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                mImage6.setImageResource(R.drawable.circle_gray30);
                mImage7.setImageResource(R.drawable.circle_gray30);
                mImage8.setImageResource(R.drawable.circle_gray30);
                break;
            case 3:
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_purple);
                mImage5.setImageResource(R.drawable.circle_gray30);
                mImage6.setImageResource(R.drawable.circle_gray30);
                mImage7.setImageResource(R.drawable.circle_gray30);
                mImage8.setImageResource(R.drawable.circle_gray30);
                break;
            case 4:
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_purple);
                mImage6.setImageResource(R.drawable.circle_gray30);
                mImage7.setImageResource(R.drawable.circle_gray30);
                mImage8.setImageResource(R.drawable.circle_gray30);
                break;
            case 5:
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                mImage6.setImageResource(R.drawable.circle_purple);
                mImage7.setImageResource(R.drawable.circle_gray30);
                mImage8.setImageResource(R.drawable.circle_gray30);
                break;
            case 6:
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                mImage6.setImageResource(R.drawable.circle_gray30);
                mImage7.setImageResource(R.drawable.circle_purple);
                mImage8.setImageResource(R.drawable.circle_gray30);
                break;
            case 7:
                mImage1.setImageResource(R.drawable.circle_gray30);
                mImage2.setImageResource(R.drawable.circle_gray30);
                mImage3.setImageResource(R.drawable.circle_gray30);
                mImage4.setImageResource(R.drawable.circle_gray30);
                mImage5.setImageResource(R.drawable.circle_gray30);
                mImage6.setImageResource(R.drawable.circle_gray30);
                mImage7.setImageResource(R.drawable.circle_gray30);
                mImage8.setImageResource(R.drawable.circle_purple);
                break;
        }
        mCurrentPosition = position;
        char mNum = tempList.get(position);
        switch (mNum) {
            case '1':
                mFeelingsName.setText(R.string.feelings_expression);
                break;
            case '2':
                mFeelingsName.setText(R.string.feelings_intuition);
                break;
            case '3':
                mFeelingsName.setText(R.string.feelings_thinking);
                break;
            case '4':
                mFeelingsName.setText(R.string.feelings_activity);
                break;
            case '5':
                mFeelingsName.setText(R.string.feelings_firmness);
                break;
            case '6':
                mFeelingsName.setText(R.string.feelings_self_value);
                break;
            case '7':
                mFeelingsName.setText(R.string.feelings_love_lorn_treat);
                break;
            case '8':
                mFeelingsName.setText(R.string.feelings_intelligence);
                break;
            case '9':
                mFeelingsName.setText(R.string.feelings_considerate);
                break;
        }
    }

    private String getShareUrl() {
        if (mIsMine) {
            return AppConfig.API_BASE + "app/shareExt/myTalent" + "?userId=" + AppContext.userId + "&shareType=11";
        } else {
            return AppConfig.API_BASE + "app/shareExt/analysisFriendTianfu" + "?birthday=" + DateUtils.getWebTimeFormatDate(mDateTime.getDate()) + "&shareType=13";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    /**
     * 跳转至命格页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab) {
        goToPage(context, sex, dateTime, childTab, false);
    }

    public static void goToPage(Context context, int sex, DateTime dateTime, int childTab, boolean isMine) {
        Intent intent = new Intent(context, FeelingsActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_USER_SEX, sex);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, childTab);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, isMine);
        context.startActivity(intent);
    }
}
