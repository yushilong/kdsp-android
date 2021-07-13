package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.ui.activity.CalendarActivity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lindow on 2/22/16.
 * 月运
 */
public class MonthFortuneFragment extends BaseFragment {
    private TextView mFortuneName1;                 //运势1
    private TextView mFortuneName2;                 //运势2
    private TextView mFortuneName3;                 //运势3
    private TextView mFortuneText1;                 //运势详细1
    private TextView mFortuneText2;                 //运势详细2
    private TextView mFortuneText3;                 //运势详细3
    private TextView mGoodDays;                     //我大吉大利的日子
    private TextView mBadDays;                      //我夹紧尾巴的日子
    private TextView mNextTab;          //下一个tab

    public static MonthFortuneFragment newInstance(int position) {
        MonthFortuneFragment fragment = new MonthFortuneFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(IntentExtraConfig.EXTRA_POSITION, position);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        getLucky();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.month_lucky, container, false);
    }

    protected void initUI() {
        TextView mDate = (TextView) mMainLay.findViewById(R.id.date);
        mFortuneName1 = (TextView) mMainLay.findViewById(R.id.fortune_name1);
        mFortuneName2 = (TextView) mMainLay.findViewById(R.id.fortune_name2);
        mFortuneName3 = (TextView) mMainLay.findViewById(R.id.fortune_name3);
        mFortuneText1 = (TextView) mMainLay.findViewById(R.id.fortune_text1);
        mFortuneText2 = (TextView) mMainLay.findViewById(R.id.fortune_text2);
        mFortuneText3 = (TextView) mMainLay.findViewById(R.id.fortune_text3);
        mGoodDays = (TextView) mMainLay.findViewById(R.id.good_days);
        mBadDays = (TextView) mMainLay.findViewById(R.id.bad_days);
        mNextTab = (TextView) mMainLay.findViewById(R.id.next_tab);

        DateTime dateTime = new DateTime();
        DateTime mBirDateTime;
        int sex;
        if (AppContext.mUser != null) {
            mBirDateTime = new DateTime(AppContext.mUser.birthTime);
            sex = AppContext.mUser.userSex;
        } else {
            mBirDateTime = new DateTime();
            sex = 2;
        }
        int month = dateTime.month;
        if (month != 11) {
            mDate.setText((month + 1) + ".01~" + (month + 2) + ".01>");
        } else {
            mDate.setText("12.01~1.01>");
        }
        mDate.setOnClickListener(new View.OnClickListener() {
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

        StringBuilder mGoodDay = new StringBuilder();
        StringBuilder mBadDay = new StringBuilder();
        int monthCount = CalendarCore.daysInMonth(dateTime.year, dateTime.month);
        for (int day = 1; day <= monthCount; day++) {
            int flag = CalendarCore.getDayIsGoodDay(dateTime.year, dateTime.month, day, mBirDateTime, sex);
            if (flag == 1) {
                mGoodDay.append(day).append("、");
            } else if (flag == 0) {
                mBadDay.append(day).append("、");
            }
        }
        try {
            if (mGoodDay.length() > 0) {
                mGoodDay.deleteCharAt(mGoodDay.length() - 1);
                mGoodDay.append("日");
            }
            if (mBadDay.length() > 0) {
                mBadDay.deleteCharAt(mBadDay.length() - 1);
                mBadDay.append("日");
            }
            mGoodDays.setText(mGoodDay);
            mBadDays.setText(mBadDay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mNextTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HomeViewPagerFragment frag = (HomeViewPagerFragment) getParentFragment();
                    frag.setCurrentFragment(0, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getLucky() {
        KDSPApiController.getInstance().findLuckymonth(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    final JSONArray jsonArray = response.optJSONArray("list");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mFortuneName1.setText(jsonArray.optJSONObject(0).optString("name"));
                                mFortuneText1.setText(jsonArray.optJSONObject(0).optString("description"));
                                mFortuneName2.setText(jsonArray.optJSONObject(1).optString("name"));
                                mFortuneText2.setText(jsonArray.optJSONObject(1).optString("description"));
                                mFortuneName3.setText(jsonArray.optJSONObject(2).optString("name"));
                                mFortuneText3.setText(jsonArray.optJSONObject(2).optString("description"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }
}
