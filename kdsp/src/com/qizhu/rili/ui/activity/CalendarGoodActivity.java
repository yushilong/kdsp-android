package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.listener.WheelChangeListener;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.ui.fragment.EventDaysViewPagerFragment;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.CircleMenuLayout;

import org.json.JSONObject;

/**
 * Created by lindow on 4/6/16.
 * 吉日搜索的fragment
 */
public class CalendarGoodActivity extends BaseActivity {
    private TextView         mTitle;                            //标题
    private ImageView        mStarImage;                       //星星
    private ImageView        mToLeft;                          //向左
    private ImageView        mToRight;                         //向右
    private TextView         mDateView;                         //日期
    private TextView         mEventTip;                         //事件
    private ImageView        mFortuneButton;                   //财富按钮
    private ImageView        mFeelingButton;                   //感情按钮
    private ImageView        mCareerButton;                    //事业按钮
    private ImageView        mCalendarCircle;                  //日历圆形
    private CircleMenuLayout mItemCircle;               //事件圆形菜单

    private String                     mStatisticSubType;                   //统计的subtype
    private String                     mActionTitle;                        //标题
    private int                        mActionIdx;                             //活动id
    private int                        mCurrentPosition;                       //当前的日期position
    private int                        mMode;                                  //吉日分类模式,1为财运，2为感情，3为事业
    private int                        mSelectIndex;                           //选中的index
    private boolean                    mShouldChange;                      //应该改变内容
    private EventDaysViewPagerFragment mEventDaysViewPagerFragment;     //轮滑viewpager

    private DateTime mNowDate = new DateTime();         //现在的日期
    private DateTime mCurDate = new DateTime();         //当前的日期
    private Animation mAlphaAnimation;                  //星星的渐变动画
    private String mWuxing[] = {"强金", "弱金", "强水", "弱水", "强木", "弱木", "强火", "弱火", "强土", "弱土"};

    private int[] mFortuneUnselectedImgs = new int[]{R.drawable.lifa_unselected, R.drawable.gouwu_unselected, R.drawable.bocai_unselected,
            R.drawable.lvxing_unselected, R.drawable.banqian_unselected};
    private int[] mFeelingUnselectedImgs = new int[]{R.drawable.hehao_unselected, R.drawable.yueme_unselected, R.drawable.yuehui_unselected,
            R.drawable.jiehun_unselected, R.drawable.biaobai_unselected};
    private int[] mCareerUnselectedImgs  = new int[]{R.drawable.qianyue_unselected, R.drawable.mianshi_unselected, R.drawable.tanshengyi_unselected,
            R.drawable.juhui_unselected, R.drawable.xuexi_unselected};
    private int[] mFortuneSelectedImgs   = new int[]{R.drawable.lifa_selected, R.drawable.gouwu_selected, R.drawable.bocai_selected,
            R.drawable.lvxing_selected, R.drawable.banqian_selected};
    private int[] mFeelingSelectedImgs   = new int[]{R.drawable.hehao_selected, R.drawable.yueme_selected, R.drawable.yuehui_selected,
            R.drawable.jiehun_selected, R.drawable.biaobai_selected};
    private int[] mCareerSelectedImgs    = new int[]{R.drawable.qianyue_selected, R.drawable.mianshi_selected, R.drawable.tanshengyi_selected,
            R.drawable.juhui_selected, R.drawable.xuexi_selected};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_good_day);
        initView();
        selectLucky(2, 5);
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.right_time_and_place);

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OperUtils.mSmallCat = OperUtils.SMALL_CAT_OTHER;
                OperUtils.mKeyCat = OperUtils.KEY_CAT_GOOD_DATE;
                int sex = 2;
                int index = 0;
                if (AppContext.mUser != null) {
                    sex = AppContext.mUser.userSex;
                    String wuxing = CalendarCore.getElementName(new DateTime(AppContext.mUser.birthTime));
                    index = getWuxingIndex(wuxing);
                }

                ShareActivity.goToMiniShare(CalendarGoodActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_GOOD_DAY, mActionTitle),
                        ShareUtils.getShareContent(ShareUtils.Share_Type_GOOD_DAY, ""), getShareUrl(), getShareImage(), ShareUtils.Share_Type_GOOD_DAY, mStatisticSubType, "/pages/pocket/calculate/day/day?five=" + index + "&sex=" + sex);
            }
        });

        mStarImage = (ImageView) findViewById(R.id.star);
        mToLeft = (ImageView) findViewById(R.id.to_left);
        mToRight = (ImageView) findViewById(R.id.to_right);
        mDateView = (TextView) findViewById(R.id.date);
        mEventTip = (TextView) findViewById(R.id.event_tip);

        mFortuneButton = (ImageView) findViewById(R.id.fortune_button);
        mFeelingButton = (ImageView) findViewById(R.id.feeling_button);
        mCareerButton = (ImageView) findViewById(R.id.career_button);
        mCalendarCircle = (ImageView) findViewById(R.id.calendar_circle);
        mItemCircle = (CircleMenuLayout) findViewById(R.id.item_circle);

        RelativeLayout.LayoutParams bottomParms = (RelativeLayout.LayoutParams) ((RelativeLayout) findViewById(R.id.bottom_lay)).getLayoutParams();
        bottomParms.height = (AppContext.getScreenHeight() - DisplayUtils.dip2px(115)) * 800 / 1334;

        RelativeLayout.LayoutParams popParms = (RelativeLayout.LayoutParams) ((ImageView) findViewById(R.id.pop)).getLayoutParams();
        popParms.height = (AppContext.getScreenHeight() - DisplayUtils.dip2px(115)) * 550 / 1334;

        mCalendarCircle.setBackgroundResource(R.drawable.calendar_circle);
        mStarImage.setBackgroundResource(R.drawable.star);

        int mCircleWidth = (int) (AppContext.getScreenWidth() * 2 / 1.41);
        mItemCircle.setWidth(mCircleWidth);
        //将item的宽度设置为屏幕宽度/5，因为只会显示5个,但是中间的需要放大，因此扩大每个item的范围
        mItemCircle.setItemWidthRadio((float) (AppContext.getScreenWidth() / 4) / mCircleWidth);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mItemCircle.getLayoutParams();
        int margin = -(mCircleWidth - AppContext.getScreenWidth()) / 2;
        layoutParams.setMargins(margin, 0, margin, -DisplayUtils.dip2px(25));
        mItemCircle.setRollerChangeListener(new WheelChangeListener() {
            @Override
            public void onSelectionChange(int selectedPosition) {
                mSelectIndex = selectedPosition;
                setSelectIndex(mMode, selectedPosition);
            }
        });
        mItemCircle.setMenuItemIcons(mFeelingUnselectedImgs, mFeelingSelectedImgs);

        mFortuneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLucky(1, 5);
            }
        });

        mFeelingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLucky(2, 5);
            }
        });

        mCareerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLucky(3, 5);
            }
        });

        mDateView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(TimePickDialogFragment.newInstance(mNowDate, TimePickDialogFragment.PICK_DAY, "择日"), "选择年月日");
            }
        });

        mToLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventDaysViewPagerFragment != null && mCurrentPosition > 0) {
                    mEventDaysViewPagerFragment.setCurrentFragment(mCurrentPosition - 1, true);
                }
            }
        });

        mToRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventDaysViewPagerFragment != null) {
                    mEventDaysViewPagerFragment.setCurrentFragment(mCurrentPosition + 1, true);
                }
            }
        });

        mAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.star_alpha);
        mStarImage.startAnimation(mAlphaAnimation);
    }

    /**
     * 选中吉日
     */
    private void selectLucky(int mode, int index) {
        mShouldChange = false;
        if (mMode != mode) {
            switch (mode) {
                case 1:
                    mFortuneButton.setImageResource(R.drawable.fortune_selected);
                    mItemCircle.setMenuItemIcons(mFortuneUnselectedImgs, mFortuneSelectedImgs);
                    break;
                case 2:
                    mFeelingButton.setImageResource(R.drawable.feeling_selected);
                    mItemCircle.setMenuItemIcons(mFeelingUnselectedImgs, mFeelingSelectedImgs);
                    break;
                case 3:
                    mCareerButton.setImageResource(R.drawable.career_selected);
                    mItemCircle.setMenuItemIcons(mCareerUnselectedImgs, mCareerSelectedImgs);
                    break;
            }
            switch (mMode) {
                case 1:
                    mFortuneButton.setImageResource(R.drawable.fortune_unselect);
                    break;
                case 2:
                    mFeelingButton.setImageResource(R.drawable.feeling_unselect);
                    break;
                case 3:
                    mCareerButton.setImageResource(R.drawable.career_unselect);
                    break;
            }
            mMode = mode;
            mShouldChange = true;
        }

        if (mSelectIndex != index) {
            mSelectIndex = index;
        }

        if (mShouldChange) {
            setSelectIndex(mMode, mSelectIndex);
        }
    }


    private int getWuxingIndex(String wuxing) {
        int index = 0;
        for (int i = 0, len = mWuxing.length; i < len; i++) {

            if (mWuxing[i].equals(wuxing)) {
                return i;
            }

        }
        return index;
    }

    private void setSelectIndex(int mode, int index) {
        int i = index % 5;
        switch (mode) {
            case 1:
                switch (i) {
                    case 2:
                        mActionTitle = "赢钞票";
                        mActionIdx = 0;
                        mStatisticSubType = StatisticsConstant.subType_Bocai;
                        break;
                    case 1:
                        mActionTitle = "买买买";
                        mActionIdx = 3;
                        mStatisticSubType = StatisticsConstant.subType_Gouwu;
                        break;
                    case 0:
                        mActionTitle = "剪头发";
                        mActionIdx = 18;
                        mStatisticSubType = StatisticsConstant.subType_Lifa;
                        break;
                    case 4:
                        mActionTitle = "搬新家";
                        mActionIdx = 13;
                        mStatisticSubType = StatisticsConstant.subType_Banqian;
                        break;
                    case 3:
                        mActionTitle = "外出玩";
                        mActionIdx = 8;
                        mStatisticSubType = StatisticsConstant.subType_Lvxing;
                        break;
                }
                break;
            case 2:
                switch (i) {
                    case 2:
                        mActionTitle = "约个会";
                        mActionIdx = 5;
                        mStatisticSubType = StatisticsConstant.subType_Yuehui;
                        break;
                    case 1:
                        mActionTitle = "XXOO";
                        mActionIdx = 1;
                        mStatisticSubType = StatisticsConstant.subType_Yueme;
                        break;
                    case 0:
                        mActionTitle = "想和好";
                        mActionIdx = 4;
                        mStatisticSubType = StatisticsConstant.subType_Hehao;
                        break;
                    case 4:
                        mActionTitle = "诉爱意";
                        mActionIdx = 6;
                        mStatisticSubType = StatisticsConstant.subType_Biaobai;
                        break;
                    case 3:
                        mActionTitle = "领证啦";
                        mActionIdx = 7;
                        mStatisticSubType = StatisticsConstant.subType_Jiehun;
                        break;
                }
                break;
            case 3:
                switch (i) {
                    case 2:
                        mActionTitle = "谈生意";
                        mActionIdx = 9;
                        mStatisticSubType = StatisticsConstant.subType_Tanshengyi;
                        break;
                    case 1:
                        mActionTitle = "找工作";
                        mActionIdx = 15;
                        mStatisticSubType = StatisticsConstant.subType_Mianshi;
                        break;
                    case 0:
                        mActionTitle = "签合同";
                        mActionIdx = 10;
                        mStatisticSubType = StatisticsConstant.subType_Qianyue;
                        break;
                    case 4:
                        mActionTitle = "读读书";
                        mActionIdx = 16;
                        mStatisticSubType = StatisticsConstant.subType_Xuexi;
                        break;
                    case 3:
                        mActionTitle = "开趴体";
                        mActionIdx = 11;
                        mStatisticSubType = StatisticsConstant.subType_Juhui;
                        break;
                }
                break;
        }

        getContent();
        KDSPApiController.getInstance().addStatistics(StatisticsConstant.SOURCE_LUCKY_SEARCH, StatisticsConstant.TYPE_PAGE, mStatisticSubType, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {

            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }

    private void getContent() {
        if (mEventDaysViewPagerFragment == null) {
            mEventDaysViewPagerFragment = EventDaysViewPagerFragment.newInstance(mNowDate, mActionIdx);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.body_fragment, mEventDaysViewPagerFragment);
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            mEventDaysViewPagerFragment.setActionIndex(mActionIdx);
        }
        if (mNowDate.year == mCurDate.year && mNowDate.month == mCurDate.month) {
            mTitle.setText("本月我的好日子");
            mDateView.setText("本月");
        } else {
            mTitle.setText((mNowDate.month + 1) + "月我的好日子");
            mDateView.setText(mNowDate.toCHMonthString());
        }
        mEventTip.setText(mActionTitle);
    }

    //设置选中
    public void setPosition(int position, DateTime dateTime) {
        mCurrentPosition = position;
        mNowDate = dateTime;
        if (mNowDate.year == mCurDate.year && mNowDate.month == mCurDate.month) {
            mTitle.setText("本月我的好日子");
            mDateView.setText("本月");
        } else {
            mTitle.setText((mNowDate.month + 1) + "月我的好日子");
            mDateView.setText(mNowDate.toCHMonthString());
        }
    }

    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
        if (dateTime.year > mCurDate.year || (dateTime.year == mCurDate.year && dateTime.month >= mCurDate.month)) {
            mNowDate = dateTime;
            if (mEventDaysViewPagerFragment != null) {
                mEventDaysViewPagerFragment.setCurrentFragment(CalendarCore.monthsFrom1901(mNowDate) - CalendarCore.monthsFrom1901(new DateTime()), false);
            }
        } else {
            UIUtils.toastMsgByStringResource(R.string.must_after_now);
        }
    }

    private String getShareUrl() {
        int iamgeIndex = getIamgeIndex();
        if (AppContext.mUser != null) {
            return AppConfig.API_BASE + "app/shareExt/whitedayShare21" + "?userId=" + AppContext.userId + "&years=" + mNowDate.toMonthString() + "&imgIndex=" + iamgeIndex + "&eventIndex=" + mActionIdx + "&birthday=" + DateUtils.getSimpleFormatDateFromDate(AppContext.mUser.birthTime) + "&sex=" + AppContext.mUser.userSex;
        } else {
            return AppConfig.API_BASE + "app/shareExt/whitedayShare21" + "?userId=" + AppContext.userId + "&years=" + mNowDate.toMonthString() + "&imgIndex=" + iamgeIndex + "&eventIndex=" + mActionIdx + "&birthday=1990-1-1" + "&sex=1";
        }
    }

    private int getIamgeIndex() {
        int i = 1;
        switch (mSelectIndex % 5) {
            case 0:
                i = 3;
                break;
            case 1:
                i = 2;
                break;
            case 2:
                i = 1;
                break;
            case 3:
                i = 5;
                break;
            case 4:
                i = 4;
                break;
        }
        return i + (mMode - 1) * 5;
    }

    private String getShareImage() {
        int iamgeIndex = getIamgeIndex();
        return "http://pt.qi-zhu.com/resource/app/appShare/appEdition2.1.0/whitedayShare/images/starsImg/" + iamgeIndex + ".png";
    }

    /**
     * 跳转至主页面
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, CalendarGoodActivity.class);
        context.startActivity(intent);
    }
}
