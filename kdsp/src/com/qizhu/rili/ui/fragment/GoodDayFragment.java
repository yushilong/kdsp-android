package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.EventItem;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.db.LuckyAliasDBHandler;
import com.qizhu.rili.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 15/9/8.
 * 幸运日的fragment
 */
public class GoodDayFragment extends BaseFragment {
    private TextView mDayTitle;             //幸运日
    private TextView mLuckyColor;           //幸运色
    private TextView mLuckyNum;             //幸运数字
    private TextView mLocationText;         //幸运数字
    private DateTime mDateTime;             //传入的日期
    private TextView mDaySummary;           //幸运日说明

    public static GoodDayFragment newInstance(DateTime dateTime) {
        GoodDayFragment fragment = new GoodDayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDateTime = getArguments().getParcelable(IntentExtraConfig.EXTRA_PARCEL);
        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.good_item_lay, container, false);
    }

    protected void initView() {
        mDayTitle = (TextView) mMainLay.findViewById(R.id.day_title);
        mLocationText = (TextView) mMainLay.findViewById(R.id.location_text);
        mLuckyColor = (TextView) mMainLay.findViewById(R.id.color_text);
        mLuckyNum = (TextView) mMainLay.findViewById(R.id.number_text);
        mDaySummary = (TextView) mMainLay.findViewById(R.id.day_summary);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        if (AppContext.mUser != null) {
            DateTime mBirDateTime = new DateTime(AppContext.mUser.birthTime);
            int mSex = AppContext.mUser.userSex;
            mDayTitle.setText(CalendarCore.getDayTitle(mDateTime, mBirDateTime, AppContext.mUser.userSex == User.BOY));

            //适宜颜色
            mLuckyColor.setText("幸运色：" + CalendarCore.getDayColor(mDateTime, mBirDateTime));
            //幸运数字
            mLuckyNum.setText("幸运数：" + CalendarUtil.getLuckyNum(CalendarCore.getElementName(mBirDateTime), mDateTime.day));
            //今日吉位
            mLocationText.setText("幸运位：" + CalendarCore.getPosition(mDateTime, mBirDateTime));

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
                mDaySummary.setText("今日宜：" + mGoodBuilder);
            } else {
                mDaySummary.setText("今日宜：暂无");
            }
        }
    }
}
