package com.qizhu.rili.widget;

import android.view.View;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WheelMain {
    private View view;
    private LoopView mYear;
    private LoopView mMonth;
    private LoopView mDay;
    private LoopView mHours;
    private LoopView mMins;

    private static int START_YEAR = 1901, END_YEAR = 2100;
    //注意此处定义一定要为int基本类型的对象类型Integer，否则用Arrays.asList转会出错，因为Arrays.asList的泛型必须为对象类型
    private static Integer[] BIG_MONTH = {1, 3, 5, 7, 8, 10, 12};
    private static Integer[] SMALL_MONTH = {4, 6, 9, 11};
    private static List<Integer> mBigMonth = Arrays.asList(BIG_MONTH);
    private static List<Integer> mSmallMonth = Arrays.asList(SMALL_MONTH);

    private int mMode = 0;                    //默认为阳历

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public WheelMain(View view) {
        super();

        this.view = view;
        setView(view);
    }

    /**
     * 弹出日期时间选择器
     */
    public void initDateTimePicker(DateTime time, final int mode) {
        // 年
        mYear = (LoopView) view.findViewById(R.id.year);
        mYear.setItems(getList(START_YEAR, END_YEAR, "年"));// 设置"年"的显示数据
        mYear.setItemsVisibleCount(7);
        mYear.setCurrentPosition(time.year - START_YEAR);// 初始化时显示的数据

        // 月
        mMonth = (LoopView) view.findViewById(R.id.month);
        mMonth.setItems(getList(1, 12, "月"));
        mMonth.setItemsVisibleCount(7);
        if(time.month == 0){
            mMonth.setInitPosition(time.month);
        }else {
            mMonth.setCurrentPosition(time.month);
        }
        // 时
        mHours = (LoopView) view.findViewById(R.id.hours);
        mHours.setItems(getList(0, 23, "时"));
        mHours.setItemsVisibleCount(7);
        if(time.hour == 0){
            mHours.setInitPosition(time.hour);
        }else {
            mHours.setCurrentPosition(time.hour);
        }

        // 分
        mMins = (LoopView) view.findViewById(R.id.minute);
        mMins.setItems(getList(0, 59, "分"));
        mMins.setItemsVisibleCount(7);
        if(time.min == 0){
            mMins.setInitPosition(time.min);
        }else {
            mMins.setCurrentPosition(time.min);
        }

        // 日
        mDay = (LoopView) view.findViewById(R.id.day);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (mBigMonth.contains(time.month + 1)) {
            mDay.setItems(getList(1, 31, "日"));
        } else if (mSmallMonth.contains(time.month + 1)) {
            mDay.setItems(getList(1, 30, "日"));
        } else {
            // 闰年
            if ((time.year % 4 == 0 && time.year % 100 != 0) || time.year % 400 == 0) {
                mDay.setItems(getList(1, 29, "日"));
            } else {
                mDay.setItems(getList(1, 28, "日"));
            }
        }
        mDay.setItemsVisibleCount(7);
        if(time.day == 1){
            mDay.setInitPosition(time.day - 1);
        }else {
            mDay.setCurrentPosition(time.day - 1);
        }

        // 添加"年"监听
        OnItemSelectedListener mSelectYearListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int newValue) {
                if (0== mMode) {
                    // 判断大小月及是否闰年,用来确定"日"的数据
                    updateSolarDays(newValue + START_YEAR, mMonth.getSelectedItem() + 1);
                } else {
                    // 根据农历年刷新天数
                    updateLunarDays(newValue + START_YEAR, mMonth.getSelectedItem() + 1);
                }
            }
        };
        // 添加"月"监听
        OnItemSelectedListener mSelectMonthListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int newValue) {
                if (0 == mMode) {
                    // 判断大小月及是否闰年,用来确定"日"的数据
                    updateSolarDays(mYear.getSelectedItem() + START_YEAR, newValue + 1);
                } else {
                    // 根据农历月刷新天数
                    updateLunarDays(mYear.getSelectedItem() + START_YEAR, newValue + 1);
                }
            }
        };
        mYear.setListener(mSelectYearListener);
        mMonth.setListener(mSelectMonthListener);
        mMode = mode;
    }

    private void updateSolarDays(int year, int month) {
        if (mBigMonth.contains(month)) {
            mDay.setItems(getList(1, 31, "日"));
        } else if (mSmallMonth.contains(month)) {
            mDay.setItems(getList(1, 30, "日"));
        } else {
            if (((year) % 4 == 0 && (year) % 100 != 0) || (year) % 400 == 0) {
                mDay.setItems(getList(1, 29, "日"));
            } else {
                mDay.setItems(getList(1, 28, "日"));
            }
        }
    }

    private void updateLunarDays(int year, int month) {
        int days = ChinaDateUtil.getLunarMonthDays(year, month);
        mDay.setItems(getList(1, days, "日"));
    }

    //设置日期形式，0为阳历，1为阴历
    public void setDateMode(int mode) {
        //模式发生改变
        if (mode != mMode) {
            //刷新选项
            if (0 == mode) {
                updateSolarDays(mYear.getSelectedItem() + START_YEAR, mMonth.getSelectedItem() + 1);
            } else {
                updateLunarDays(mYear.getSelectedItem() + START_YEAR, mMonth.getSelectedItem() + 1);
            }
            mMode = mode;
        }
    }

    //将时间以DateTime形式返回
    public DateTime getDateTime() {
        return new DateTime(mYear.getSelectedItem() + START_YEAR, mMonth.getSelectedItem(), mDay.getSelectedItem() + 1, mHours.getSelectedItem(), mMins.getSelectedItem(), 0);
    }

    public int getMode() {
        return mMode;
    }

    //返回string型的数组,可以添加string的format来将数字转换
    private List<String> getList(int minValue, int maxValue, String label) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = minValue; i < maxValue + 1; i++) {
            list.add(i + label);
        }
        return list;
    }
}
