package com.qizhu.rili.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.EventItem;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.ui.activity.CalendarActivity;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.widget.WeekLuckyItem;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 2/22/16.
 * 周运
 */
public class WeekFortuneFragment extends BaseFragment {
    private TextView mDateTip;
    private View mWeekLuckyLay;             //周运总布局
    private TextView mNextTab;              //下一个tab
    private WeekLuckyItem mSunday;
    private WeekLuckyItem mMonday;
    private WeekLuckyItem mTuesday;
    private WeekLuckyItem mWednesday;
    private WeekLuckyItem mThursday;
    private WeekLuckyItem mFriday;
    private WeekLuckyItem mSaturday;

    private DateTime mBirDateTime;      //出生时间
    private int mSex = 2;               //性别

    public static WeekFortuneFragment newInstance(int position) {
        WeekFortuneFragment fragment = new WeekFortuneFragment();
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
    public void onResume() {
        super.onResume();
        ViewTreeObserver viewTreeObserver = mWeekLuckyLay.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (MethodCompat.isCompatible(Build.VERSION_CODES.JELLY_BEAN)) {
                    mWeekLuckyLay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mWeekLuckyLay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int height = mWeekLuckyLay.getHeight();
                if (height != 0) {
                    mSunday.setHeight(height);
                    mMonday.setHeight(height);
                    mTuesday.setHeight(height);
                    mWednesday.setHeight(height);
                    mThursday.setHeight(height);
                    mFriday.setHeight(height);
                    mSaturday.setHeight(height);
                    refreshUI();
                }
            }
        });
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.week_lucky, container, false);
    }

    protected void initUI() {
        mDateTip = (TextView) mMainLay.findViewById(R.id.date);
        mWeekLuckyLay = mMainLay.findViewById(R.id.week_lucky_lay);
        mNextTab = (TextView) mMainLay.findViewById(R.id.next_tab);
        mSunday = (WeekLuckyItem) mMainLay.findViewById(R.id.sunday);
        mMonday = (WeekLuckyItem) mMainLay.findViewById(R.id.monday);
        mTuesday = (WeekLuckyItem) mMainLay.findViewById(R.id.tuesday);
        mWednesday = (WeekLuckyItem) mMainLay.findViewById(R.id.wednesday);
        mThursday = (WeekLuckyItem) mMainLay.findViewById(R.id.thursday);
        mFriday = (WeekLuckyItem) mMainLay.findViewById(R.id.friday);
        mSaturday = (WeekLuckyItem) mMainLay.findViewById(R.id.saturday);

        mNextTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HomeViewPagerFragment frag = (HomeViewPagerFragment) getParentFragment();
                    frag.setCurrentFragment(3, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshUI() {
        if (AppContext.mUser != null) {
            mBirDateTime = new DateTime(AppContext.mUser.birthTime);
            mSex = AppContext.mUser.userSex;
        } else {
            mBirDateTime = new DateTime();
        }

        StringBuilder stringBuilder = new StringBuilder();

        DateTime dateTime = new DateTime();
        int weekCount = CalendarCore.getWeekDay(dateTime);      //获取当天是星期几
        for (int i = 0; i < 7; i++) {
            DateTime dateTime1 = CalendarCore.shiftDays(dateTime, i - weekCount);
            refreshGodLay(dateTime1, i);
            if (0 == i) {
                stringBuilder.append(dateTime1.month + 1).append(".").append(dateTime1.day);
            }
            if (6 == i) {
                stringBuilder.append("~").append(dateTime1.month + 1).append(".").append(dateTime1.day);
            }
        }
        stringBuilder.append(">");

        mDateTip.setText(stringBuilder);

        mDateTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarActivity.goToPage(mActivity, new DateTime());
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
    }

    public void refreshGodLay(DateTime dateTime, int week) {
        ArrayList<String> mGoodDays = new ArrayList<String>();

        //判断今天是否适宜
        for (int i = 0; i < 20; i++) {
            //第一个是当前时间，第二个是生日
            if (CalendarCore.isGoodDay4Act(dateTime, mBirDateTime, i, mSex)) {
                mGoodDays.add(EventItem.ACTION_ARRAY[i]);
            }
        }

        if (mGoodDays.isEmpty()) {
            mGoodDays.add("诸事不宜");
        }

        String title = CalendarCore.getDayTitle(dateTime, mBirDateTime, AppContext.mUser != null && AppContext.mUser.userSex == User.BOY);
        switch (week) {
            case 0:
                mSunday.setTextArray(title, mGoodDays);
                break;
            case 1:
                mMonday.setTextArray(title, mGoodDays);
                break;
            case 2:
                mTuesday.setTextArray(title, mGoodDays);
                break;
            case 3:
                mWednesday.setTextArray(title, mGoodDays);
                break;
            case 4:
                mThursday.setTextArray(title, mGoodDays);
                break;
            case 5:
                mFriday.setTextArray(title, mGoodDays);
                break;
            case 6:
                mSaturday.setTextArray(title, mGoodDays);
                break;
        }
    }
}
