package com.qizhu.rili.ui.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.ui.activity.MainActivity;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.Arrays;

/**
 * Created by lindow on 11/16/15.
 * 桌面小组件,必须要接收android.appwidget.action.APPWIDGET_UPDATE 广播
 */
public class KDSPAppWidgetProvider extends AppWidgetProvider {
    private static String TAG = "KDSPAppWidgetProvider --->";

    public KDSPAppWidgetProvider() {
        super();
    }

    /**
     * 接收广播，这个接收到每个广播时都会被调用，而且在上面的回调函数之前。
     * 你通常不需要实现这个方法，因为缺省的AppWidgetProvider 实现过滤所有App Widget 广播并恰当的调用上述方法。
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (action.equals(KDSPAppWidgetService.ACTION_CLICK_GRID)) {
            // 接受“gridView”的点击事件的广播
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    /**
     * 更新部件时调用，在第一次添加部件时也会调用
     * 如果你已经声明了一个配置活动，这个方法在用户添加App Widget时将不会被调用，而只在后续更新时被调用
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        LogUtil.d(TAG, "onUpdate widget：" + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            //跳转到主页
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent clickIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.timer_widget);

            DateTime mDateTime = new DateTime();
            //设置文字
            if (AppContext.mUser != null) {
                remoteViews.setTextViewText(R.id.good_day, CalendarCore.getDayTitle(mDateTime, new DateTime(AppContext.mUser.birthTime), AppContext.mUser.userSex == User.BOY));
            } else {
                remoteViews.setTextViewText(R.id.good_day, CalendarCore.getDayTitle(mDateTime, mDateTime, true));
            }
            remoteViews.setOnClickPendingIntent(R.id.good_day, clickIntent);

            //设置gridView
            Intent serviceIntent = new Intent(context, KDSPAppWidgetService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                remoteViews.setRemoteAdapter(R.id.month_grid, serviceIntent);
                /**
                 * 设置响应 “GridView(gridview)” 的intent模板
                 * 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
                 * 它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
                 * 1.通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
                 * 2.然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
                 */
                Intent gridIntent = new Intent();
                gridIntent.setAction(KDSPAppWidgetService.ACTION_CLICK_GRID);
                //将appWidgetId传入
                gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // 设置intent模板
                remoteViews.setPendingIntentTemplate(R.id.month_grid, pendingIntent);
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    /**
     * 当widget被初次添加或者当widget的大小被改变时，执行onAppWidgetOptionsChanged()，Android 4.1 引入
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidtgetIds) {
        super.onDeleted(context, appWidtgetIds);
        LogUtil.d(TAG, "onDeleted widget");
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法，可添加多次但只第一次调用
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        LogUtil.d(TAG, "onEnabled widget");
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法，注意是最后一个
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        LogUtil.d(TAG, "onDisabled widget");
    }
}
