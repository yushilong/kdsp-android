package com.qizhu.rili.bean;

import com.qizhu.rili.listener.OnSelectedDateItemChangedListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarData {
    private static CalendarData mCalendarData;
    private DateTime mSelectedDateItem;
    private ArrayList<OnSelectedDateItemChangedListener> mListeners = new ArrayList<OnSelectedDateItemChangedListener>();

    public static CalendarData getInstance() {
        if (mCalendarData == null) {
            mCalendarData = new CalendarData();
            mCalendarData.init();
        }
        return mCalendarData;
    }

    private void init() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        mSelectedDateItem = new DateTime(year, month, day);
    }

    public void setSelectedDateItem(DateTime item) {
        if (!mSelectedDateItem.equals(item)) {
            DateTime mOldSelectedDateItem = mSelectedDateItem;
            mSelectedDateItem = item;
            for (OnSelectedDateItemChangedListener listener : mListeners) {
                if (listener != null) {
                    listener.onSelectedDateItemChanged(mOldSelectedDateItem, item);
                }
            }
        }
    }

    public DateTime getSelectedDateItem() {
        return mSelectedDateItem;
    }

    public void addOnSelectedDateItemChangedListener(OnSelectedDateItemChangedListener listener) {
        mListeners.add(listener);
    }

    public void removeOnSelectedDateItemChangedListener(OnSelectedDateItemChangedListener listener) {
        mListeners.remove(listener);
    }
}