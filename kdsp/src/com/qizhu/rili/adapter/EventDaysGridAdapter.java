package com.qizhu.rili.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.utils.DisplayUtils;

import java.util.List;

/**
 * Created by lindow on 12/17/15.
 * 吉日的grid
 */
public class EventDaysGridAdapter extends BaseListAdapter {
    private int mHeight;                //布局的高度

    public EventDaysGridAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    protected int getItemResId() {
        return R.layout.event_days_item;
    }

    @Override
    protected void initItemView(View convertView, int position) {
        ViewHolder holdler = new ViewHolder();
        holdler.mEventDay = (TextView) convertView.findViewById(R.id.event_day);
        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight / 2));
        int width = mHeight / 2 - DisplayUtils.dip2px(2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        holdler.mEventDay.setLayoutParams(layoutParams);
        convertView.setTag(holdler);
    }

    @Override
    protected void setItemView(Object tag, Object itemData, final int position) {
        if (itemData != null && itemData instanceof DateTime) {
            final DateTime dateTime = (DateTime) itemData;
            final ViewHolder holder = (ViewHolder) tag;

            holder.mEventDay.setText(dateTime.day + "");
        }
    }

    /**
     * 设置布局的高度，得在绘制之前调用
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    class ViewHolder {
        TextView mEventDay;
    }
}
