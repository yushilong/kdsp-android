package com.qizhu.rili.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.CalendarData;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;

/**
 * Created by lindow on 15/9/7.
 * 月历
 */
public class MonthGridAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private Resources mResources;
    private int mYear;
    private int mMonth;
    private int mWeek;
    private int mDays;
    private int mRows;                  //行数
    private int mCount = 0;             //调用position为0的次数
    private int mHeight;                //布局的高度
    private int mActionIdx;             //宜的index
    private DateTime mBirthDateTime;    //出生生日
    private int mSex = 2;               //性别
    private DateTime mNowDateTime;      //今天的日期
    private String mTitle;              //吉日的标题
    private MonthHolder mSelectHolder;  //选中的view

    public MonthGridAdapter(Context mContext, int year, int month, int days, int week) {
        this(mContext, year, month, days, week, -1, "");
    }

    public MonthGridAdapter(Context mContext, int year, int month, int days, int week, int action, String title) {
        this.mContext = mContext;
        mYear = year;
        mMonth = month;
        mDays = days;
        mWeek = week;
        mInflater = LayoutInflater.from(mContext);
        mResources = mContext.getResources();
        //行数为总天数加上第一天开始的星期(不用减1，因为星期从0开始)除以7,如果余数不为0,则加上1行
        int allDay = mDays + mWeek;
        if (allDay % 7 == 0) {
            mRows = allDay / 7;
        } else {
            mRows = (allDay / 7 + 1);
        }
        mActionIdx = action;
        if (AppContext.mUser != null) {
            mBirthDateTime = new DateTime(AppContext.mUser.birthTime);
            mSex = AppContext.mUser.userSex;
        } else {
            mBirthDateTime = new DateTime();
        }
        mTitle = title;
        mNowDateTime = new DateTime(true);
    }

    @Override
    public int getCount() {
        return mRows * 7;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final MonthHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.calendar_day_item_lay, null);
            holder = new MonthHolder();
            holder.mItemLay = convertView.findViewById(R.id.item_lay);
            holder.mSelected = (ImageView) convertView.findViewById(R.id.selected_image);
            holder.mSolarTxt = (TextView) convertView.findViewById(R.id.solar_date_text);
            holder.mLunarTxt = (TextView) convertView.findViewById(R.id.lunar_date_text);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight / mRows));
            convertView.setTag(holder);
        } else {
            holder = (MonthHolder) convertView.getTag();
        }

        if (position - mWeek + 1 > 0 && position < mDays + mWeek) {
            final DateTime dateTime = new DateTime(mYear, mMonth, position - mWeek + 1);
            DateTime selectedItem = CalendarData.getInstance().getSelectedDateItem();
            if (dateTime.equals(selectedItem)) {
                holder.mSelected.setVisibility(View.VISIBLE);
                mSelectHolder = holder;
            } else {
                holder.mSelected.setVisibility(View.INVISIBLE);
            }
            //gridview不知道它里面到底能放多少item。因此多次加载position 0来适配,因此以这样的方式防止多次重绘
            //不能放在外面，否则0处由于直接返回不会有选中效果，因此放在里面
            if (0 == position) {
                mCount++;
                if (mCount > 1) {
                    return convertView;
                }
            }

            String mSolarName = dateTime.toDayString();
            String mLunarDateName = CalendarCore.getLunarDateNameMD(dateTime);
            if (AppContext.mSolarHoliday.containsKey(mSolarName)) {
                holder.mLunarTxt.setTextColor(ContextCompat.getColor(mContext, R.color.pink4));
                holder.mLunarTxt.setText(AppContext.mSolarHoliday.get(mSolarName));
            } else if (AppContext.mLunarHoliday.containsKey(mLunarDateName)) {
                holder.mLunarTxt.setTextColor(ContextCompat.getColor(mContext, R.color.pink4));
                holder.mLunarTxt.setText(AppContext.mLunarHoliday.get(mLunarDateName));
            } else {
                holder.mLunarTxt.setTextColor(ContextCompat.getColor(mContext, R.color.purple13));
                mLunarDateName = mLunarDateName.substring(mLunarDateName.indexOf("月") + 1);
                holder.mLunarTxt.setText(mLunarDateName);
            }

            holder.mSolarTxt.setText(dateTime.day + "");

            holder.mItemLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarData.getInstance().setSelectedDateItem(dateTime);
                    if (mSelectHolder != null) {
                        holder.mSelected.setVisibility(View.INVISIBLE);
                    }
                    holder.mSelected.setVisibility(View.VISIBLE);
                    mSelectHolder = holder;
                }
            });
        }
        return convertView;
    }

    class MonthHolder {
        View mItemLay;              //布局
        ImageView mSelected;        //选中
        TextView mSolarTxt;         //阳历
        TextView mLunarTxt;         //阴历
    }

    /**
     * 设置布局的高度，得在绘制之前调用
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    private boolean isGoodDay(DateTime dt) {
        return CalendarCore.isGoodDay4Act(dt, mBirthDateTime, mActionIdx, mSex);
    }

    /**
     * 选中某个index
     */
    public void setSelectedAction(int action) {
        mActionIdx = action;
        notifyDataSetChanged();
    }
}
