package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.CalendarData;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSelectedDateItemChangedListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.ui.fragment.CalendarViewPagerFragment;
import com.qizhu.rili.ui.fragment.GoodDayViewPagerFragment;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;

/**
 * Created by lindow on 15/9/7.
 * 日历界面
 */
public class CalendarActivity extends BaseActivity {
    private TextView mSolarText;            //年月日
    private TextView mLunarText;            //月

    private DateTime mDateTime = new DateTime();             //时间
    private CalendarViewPagerFragment mCalendarViewPagerFragment;
    private GoodDayViewPagerFragment mGoodDayViewPagerFragment;
    private OnSelectedDateItemChangedListener mOnSelectedDateItemChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_lay);
        mDateTime = getIntent().getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        initView();
        initViewPagerFragment();
    }

    private void initView() {
        mSolarText = (TextView) findViewById(R.id.solar_text);
        mLunarText = (TextView) findViewById(R.id.lunar_text);
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.calendar);

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
                OperUtils.mKeyCat = OperUtils.KEY_CAT_CALENDAR;
                ShareActivity.goToShare(CalendarActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_CALENDAR, "日历"),
                        ShareUtils.getShareContent(ShareUtils.Share_Type_CALENDAR, ""), getShareUrl(), "http://api.ishenpo.com:8080/Fortune-Calendar/resource/app/appShare/appEdition1.8.7/calendarShare/images/ji.jpg", ShareUtils.Share_Type_CALENDAR, StatisticsConstant.subType_Enter_Calendar);
            }
        });
        findViewById(R.id.date_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(TimePickDialogFragment.newInstance(mDateTime, TimePickDialogFragment.PICK_DAY), "选择年月日");
            }
        });

        findViewById(R.id.prev_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentTab = CalendarCore.monthsFrom1901(mDateTime);
                if (currentTab - 1 >= 0) {
                    mCalendarViewPagerFragment.setCurrentFragment(currentTab - 1, true);
                }
            }
        });
        findViewById(R.id.next_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentTab = CalendarCore.monthsFrom1901(mDateTime);
                if (currentTab + 1 < CalendarCore.mMonths1901To2100) {
                    mCalendarViewPagerFragment.setCurrentFragment(currentTab + 1, true);
                }
            }
        });

        //不要设置多个监听器，这个activity内的统一由它处理即可
        mOnSelectedDateItemChangedListener = new OnSelectedDateItemChangedListener() {
            @Override
            public void onSelectedDateItemChanged(DateTime oldDate, DateTime newDate) {
                refreshUI();
                LogUtils.d("---> mOnSelectedDateItemChangedListener newDate = " + newDate);
                //新旧日期肯定不一样，那么如果是同年同月的话，只用修改日就行，否则要调整月视图
                if (mCalendarViewPagerFragment != null && mGoodDayViewPagerFragment != null) {
                    if (oldDate.year != newDate.year || oldDate.month != newDate.month) {
                        mCalendarViewPagerFragment.setCurrentFragment(CalendarCore.monthsFrom1901(newDate), false);
                    }
                    mGoodDayViewPagerFragment.setCurrentFragment(CalendarCore.daysFrom1901(newDate), false);
                    mCalendarViewPagerFragment.refreshUI();
                }
            }
        };
        CalendarData.getInstance().addOnSelectedDateItemChangedListener(mOnSelectedDateItemChangedListener);
    }

    private void initViewPagerFragment() {
        CalendarData.getInstance().setSelectedDateItem(mDateTime);
        mCalendarViewPagerFragment = CalendarViewPagerFragment.newInstance(CalendarCore.monthsFrom1901(mDateTime));
        mGoodDayViewPagerFragment = GoodDayViewPagerFragment.newInstance(CalendarCore.daysFrom1901(mDateTime));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mCalendarViewPagerFragment);
        fragmentTransaction.add(R.id.good_day_fragment, mGoodDayViewPagerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        try {
            mDateTime = CalendarData.getInstance().getSelectedDateItem();
            mSolarText.setText(mDateTime.toCHDayString());

            StringBuilder stringBuilder = new StringBuilder();
            DateTime lunar = ChinaDateUtil.solarToLunar(mDateTime);
            int lunarYear = lunar.year;
            int lunarMonth = lunar.month + 1;
            int lunarDay = lunar.day;
            stringBuilder.append(ChinaDateUtil.getLunarYearString(lunarYear)).append("年").append(" ");
            stringBuilder.append(ChinaDateUtil.getAnimalString(lunarYear)).append("年");
            stringBuilder.append(ChinaDateUtil.mMonthStr[lunarMonth]).append("月").append(ChinaDateUtil.getLunarDayString(lunarDay));
            mLunarText.setText(stringBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CalendarData.getInstance().removeOnSelectedDateItemChangedListener(mOnSelectedDateItemChangedListener);
    }

    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
        mDateTime = dateTime;
        CalendarData.getInstance().setSelectedDateItem(mDateTime);
    }

    private String getShareUrl() {
        if (AppContext.mUser != null) {
            return AppConfig.API_BASE + "app/shareExt/calendarShare" + "?userId=" + AppContext.userId + "&shareDate=" + mDateTime.toServerDayString() + "&birthday=" + DateUtils.getSimpleFormatDateFromDate(AppContext.mUser.birthTime) + "&sex=" + AppContext.mUser.userSex;
        } else {
            return AppConfig.API_BASE + "app/shareExt/calendarShare" + "?userId=" + AppContext.userId + "&shareDate=" + mDateTime.toServerDayString() + "&birthday=1990-1-1" + "&sex=1";
        }
    }


    /**
     * 跳转至引导页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context, DateTime dateTime) {
        Intent intent = new Intent(context, CalendarActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        context.startActivity(intent);
    }
}
