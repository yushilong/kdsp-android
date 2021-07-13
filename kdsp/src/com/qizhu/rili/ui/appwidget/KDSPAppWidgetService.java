package com.qizhu.rili.ui.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by lindow on 11/16/15.
 * 桌面widget的service，桌面插件相关统一写在此文件夹
 */
public class KDSPAppWidgetService extends RemoteViewsService {
    private static final String TAG = "KDSPAppWidgetService --->";
    public static final String ACTION_CLICK_GRID = "com.qizhu.service.CLICK_GRID";      //点击appwidget中的grid

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this, intent);
    }

    private class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private DateTime mDateTime;         //今天的日期
        private int mAppWidgetId;           //当前组件的id
        private int mWeek;                  //星期
        private int mDays;                  //天数
        private int mRows;                  //行数
        private ArrayList<DateTime> mDateTimes;         //日期列表

        /**
         * 构造GridRemoteViewsFactory
         */
        public GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            mDateTime = new DateTime();
            LogUtils.d(TAG, "GridRemoteViewsFactory mAppWidgetId:" + mAppWidgetId + " ,mDateTime = " + mDateTime);
            // 初始化“集合视图”中的数据
            try {
                initGridViewData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public RemoteViews getViewAt(int position) {
            LogUtils.d(TAG, "GridRemoteViewsFactory getViewAt:" + position);
            // 获取 grid_view_item.xml 对应的RemoteViews
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.calendar_day_item_lay);

            try {
                if (position - mWeek + 1 > 0 && position < mDays + mWeek) {
                    DateTime dateTime = mDateTimes.get(position - mWeek + 1);
                    // 设置第position位的“视图”的数据
                    rv.setTextViewText(R.id.solar_date_text, dateTime.day + "");

                    // 设置第position位的“视图”对应的响应事件,暂不加入点击
//                    Intent fillInIntent = new Intent();
//                    fillInIntent.putExtra(ACTION_CLICK_GRID, position);
//                    rv.setOnClickFillInIntent(R.id.item_lay, fillInIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rv;
        }

        /**
         * 初始化GridView的数据
         */
        private void initGridViewData() {
            mDateTimes = new ArrayList<DateTime>();
            mDays = CalendarCore.daysInMonth(mDateTime.year, mDateTime.month);
            mWeek = CalendarCore.getWeekDay(mDateTime);
            //行数为总天数加上第一天开始的星期(不用减1，因为星期从0开始)除以7,如果余数不为0,则加上1行
            int allDay = mDays + mWeek;
            if (allDay % 7 == 0) {
                mRows = allDay / 7;
            } else {
                mRows = (allDay / 7 + 1);
            }
            int all = mRows * 7;
            for (int i = 0; i < all; i++) {
                DateTime dateTime;
                if (i - mWeek + 1 > 0 && i < mDays + mWeek) {
                    dateTime = new DateTime(mDateTime.year, mDateTime.month, i - mWeek + 1);
                } else {
                    dateTime = new DateTime();
                }
                mDateTimes.add(dateTime);
            }
        }

        @Override
        public void onCreate() {
            LogUtils.d(TAG, "onCreate");
        }

        @Override
        public int getCount() {
            // 返回“集合视图”中的数据的总数
            return mRows * 7;
        }

        @Override
        public long getItemId(int position) {
            // 返回当前项在“集合视图”中的位置
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            // 只有一类 GridView
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {
            mDateTimes.clear();
        }
    }
}
