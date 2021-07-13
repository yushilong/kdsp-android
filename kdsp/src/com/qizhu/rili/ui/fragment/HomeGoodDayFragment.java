package com.qizhu.rili.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.EventItem;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.db.LuckyAliasDBHandler;
import com.qizhu.rili.ui.activity.CalendarActivity;
import com.qizhu.rili.utils.CalendarUtil;
import com.qizhu.rili.utils.LogUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 15/9/6.
 * 主页的幸运日
 */
public class HomeGoodDayFragment extends BaseFragment {
    private TextView mTip;              //提示
    private TextView mDay;              //日期
    private TextView mWeek;             //星期
    private TextView mDetailTxt;        //幸运日详细
    private TextView mLuckyColor;       //幸运色
    private TextView mLuckyNum;         //幸运数字
    private TextView mLocationText;     //吉位
    private TextView mGoodText;         //今日宜的详细
    private TextView mNextTab;          //下一个tab

    private DateTime mDateTime;         //当前时间
    private DateTime mBirDateTime;      //出生时间
    private int mSex = 2;               //性别
    private int mNowDateTab = 1;
    private int mCurrentTab = mNowDateTab;      //当前tab,默认定位到当天

    public static HomeGoodDayFragment newInstance(int position) {
        HomeGoodDayFragment fragment = new HomeGoodDayFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(IntentExtraConfig.EXTRA_POSITION, position);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.home_good_day_lay, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppContext.mUser != null) {
            mBirDateTime = new DateTime(AppContext.mUser.birthTime);
            mSex = AppContext.mUser.userSex;

            SpannableStringBuilder mBuilder2;
            if (0 == mCurrentTab) {
                mTip.setText("今日是我の" + CalendarCore.getDayTitle(mDateTime, mBirDateTime, AppContext.mUser.userSex == User.BOY));

                mBuilder2 = new SpannableStringBuilder("\t\t\t今日大事件：" + CalendarCore.getDayDetail(mDateTime, mBirDateTime, AppContext.mUser.userSex == User.BOY));
                mNextTab.setText("明日");
            } else {
                mTip.setText("明日是我の" + CalendarCore.getDayTitle(mDateTime, mBirDateTime, AppContext.mUser.userSex == User.BOY));

                mBuilder2 = new SpannableStringBuilder("\t\t\t明日大事件：" + CalendarCore.getDayDetail(mDateTime, mBirDateTime, AppContext.mUser.userSex == User.BOY));
                mNextTab.setText("本周");
            }

            mBuilder2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.pink3)), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mDetailTxt.setText(mBuilder2);

            //适宜颜色
            SpannableStringBuilder mColorBuilder = new SpannableStringBuilder("幸运色：" + CalendarCore.getDayColor(mDateTime, mBirDateTime));
            mColorBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.pink3)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mLuckyColor.setText(mColorBuilder);
            //幸运数字
            SpannableStringBuilder mNumberBuilder = new SpannableStringBuilder("幸运数：" + CalendarUtil.getLuckyNum(CalendarCore.getElementName(mBirDateTime), mDateTime.day));
            mNumberBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.pink3)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mLuckyNum.setText(mNumberBuilder);
            //今日吉位
            SpannableStringBuilder mPositionBuilder = new SpannableStringBuilder("幸运位：" + CalendarCore.getPosition(mDateTime, mBirDateTime));
            mPositionBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.pink3)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mLocationText.setText(mPositionBuilder);
        }

        //算出 宜和今日提醒
        refreshGodLay();
    }

    protected void initUI() {
        if (getArguments() != null) {
            mCurrentTab = getArguments().getInt(IntentExtraConfig.EXTRA_POSITION, mNowDateTab);
        }
        LogUtils.d("---> mCurrentTab = " + mCurrentTab);
        mDateTime = CalendarCore.shiftDays(new DateTime(), mCurrentTab);
        mTip = (TextView) mMainLay.findViewById(R.id.tip);
        mDay = (TextView) mMainLay.findViewById(R.id.day);
        mWeek = (TextView) mMainLay.findViewById(R.id.week);
        mDetailTxt = (TextView) mMainLay.findViewById(R.id.good_day_detail_text);
        mNextTab = (TextView) mMainLay.findViewById(R.id.next_tab);

        mLuckyColor = (TextView) mMainLay.findViewById(R.id.color_text);
        mLuckyNum = (TextView) mMainLay.findViewById(R.id.number_text);

        mDay.setText(mDateTime.toYearString());
        mWeek.setText(CalendarCore.getWeekDayString(mDateTime));

        mMainLay.findViewById(R.id.date_lay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarActivity.goToPage(mActivity, mDateTime);
                KDSPApiController.getInstance().addStatistics(StatisticsConstant.SOURCE_MAIN,
                        StatisticsConstant.TYPE_PAGE, StatisticsConstant.subType_Enter_Calendar, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {

                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                            }
                        });
            }
        });
        mLocationText = (TextView) mMainLay.findViewById(R.id.location_text);
        mGoodText = (TextView) mMainLay.findViewById(R.id.good_text);

        mNextTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HomeViewPagerFragment frag = (HomeViewPagerFragment) getParentFragment();
                    if (mCurrentTab < 2) {
                        frag.setCurrentFragment(mCurrentTab + 1, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 填充适宜的数据
     */
    public void refreshGodLay() {
        if (AppContext.mUser != null) {
            //适宜下标
            List<Integer> list = new ArrayList<Integer>();

            //判断今天是否适宜
            for (int i = 0; i < 20; i++) {
                //第一个是当前时间，第二个是生日
                if (CalendarCore.isGoodDay4Act(mDateTime, mBirDateTime, i, mSex)) {
                    list.add(i);
                }
            }
            int size = list.size();
            int day = mDateTime.day;
            if (size > 0) {
                StringBuilder mGoodBuilder = new StringBuilder();
                for (int i : list) {
                    String[] good = LuckyAliasDBHandler.getArrayOfname(EventItem.ACTION_ARRAY[i]);
                    if (good != null && good.length > 0) {
                        mGoodBuilder.append(good[day % good.length]).append(".");
                    } else {
                        mGoodBuilder.append(EventItem.ACTION_ARRAY[i]).append(".");
                    }
                }
                SpannableStringBuilder mBuilder = new SpannableStringBuilder("今日宜：" + mGoodBuilder);
                mBuilder.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.pink3)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mGoodText.setText(mBuilder);
            } else {
                SpannableStringBuilder mBuilder = new SpannableStringBuilder("今日宜：暂无");
                mBuilder.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.pink3)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mGoodText.setText(mBuilder);
            }
        }
    }
}
