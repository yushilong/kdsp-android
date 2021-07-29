package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Coupons;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.StringUtils;

import java.util.List;

/**
 * Created by zhouyue on 8/24/17.
 * 优惠券adapter
 */
public class CouponsAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.item_coupons_lay;


    public CouponsAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Coupons) {
            final Coupons mItem = (Coupons) itemData;
            if(mItem.isDiscount == 0){
                holder.mPrice.setText("¥" +  StringUtils.price2String(mItem.price));
            }else {

                holder.mPrice.setText( StringUtils.price2String(mItem.price) +"折");
            }

            if (mItem.isUsable == 0){
                holder.mIsUse.setText(mContext.getString(R.string.Usable));
                holder.mIsUse.setTextColor(ContextCompat.getColor(mContext,R.color.purple28));
                holder.mCouponsBgLlayout.setBackgroundResource(R.drawable.coupons_bg_selected);
            }else {
                holder.mIsUse.setText(mContext.getString(R.string.un_Usable));
                holder.mIsUse.setTextColor(ContextCompat.getColor(mContext,R.color.yellow6));
                holder.mCouponsBgLlayout.setBackgroundResource(R.drawable.coupons_bg_unselected);
            }
            DateTime currentTime = new DateTime();
           DateTime startTime = new DateTime(DateUtils.toDate(mItem.startTime).getTime());
           DateTime endTime = new DateTime(DateUtils.toDate(mItem.endTime).getTime());

            if(currentTime.getTime() > endTime.getTime()){
                holder.mIsUse.setText(mContext.getString(R.string.un_date));
                holder.mIsUse.setTextColor(ContextCompat.getColor(mContext,R.color.yellow6));
                holder.mCouponsBgLlayout.setBackgroundResource(R.drawable.coupons_bg_unselected);
            }
            holder.mDate.setText("使用日期："+ startTime.toYearString() +"-" +endTime.toYearString());
            holder.mUseRange.setText(mItem.couponName );

        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView     mPrice;                     //价格
        TextView     mIsUse;                    //未使用
        TextView     mDate;                    //有效期
        TextView     mUseRange;                    //使用范围
        LinearLayout mCouponsBgLlayout;                    //使用范围

        private ItemHolder(View convertView) {
            super(convertView);
            mPrice = (TextView) convertView.findViewById(R.id.price_tv);
            mIsUse = (TextView) convertView.findViewById(R.id.is_use_tv);
            mDate = (TextView) convertView.findViewById(R.id.date_tv);
            mUseRange = (TextView) convertView.findViewById(R.id.use_range_tv);
            mCouponsBgLlayout = (LinearLayout) convertView.findViewById(R.id.coupons_bg_llayout);



        }
    }
}
