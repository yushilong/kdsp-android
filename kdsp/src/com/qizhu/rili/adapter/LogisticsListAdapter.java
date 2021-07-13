package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Logistics;

import java.util.List;

/**
 * Created by lindow on 22/03/2017.
 * 物流信息adapter
 */

public class LogisticsListAdapter extends BaseRecyclerAdapter {

    public LogisticsListAdapter(Context context, List<?> list) {
        super(context, list);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.logistics_item, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Logistics) {
            Logistics mItem = (Logistics) itemData;
            holder.mContext.setText(mItem.context);
            holder.mTime.setText(mItem.ftime);

            //第一个
            if (0 == position) {
                holder.mUpLine.setVisibility(View.INVISIBLE);
                holder.mSmallCircle.setBackgroundResource(R.drawable.circle_cyan3);
                holder.mBigCircle.setVisibility(View.VISIBLE);
            } else {
                holder.mUpLine.setVisibility(View.VISIBLE);
                holder.mSmallCircle.setBackgroundResource(R.drawable.circle_gray9);
                holder.mBigCircle.setVisibility(View.GONE);
            }

            //最后一个
            if (mList.size() - 1 == position) {
                holder.mBottomLine.setVisibility(View.GONE);
            } else {
                holder.mBottomLine.setVisibility(View.VISIBLE);
            }
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        View mUpLine;                       //上部分线
        ImageView mSmallCircle;             //小圈
        ImageView mBigCircle;               //大圈
        TextView mContext;                  //信息
        TextView mTime;                     //时间
        View mBottomLine;                   //底部线
        private ItemHolder(View convertView) {
            super(convertView);
            mUpLine = convertView.findViewById(R.id.up_line);
            mSmallCircle = (ImageView) convertView.findViewById(R.id.small_circle);
            mBigCircle = (ImageView) convertView.findViewById(R.id.big_circle);
            mContext = (TextView) convertView.findViewById(R.id.context);
            mTime = (TextView) convertView.findViewById(R.id.time);
            mBottomLine = convertView.findViewById(R.id.bottom_line);
        }
    }


}
