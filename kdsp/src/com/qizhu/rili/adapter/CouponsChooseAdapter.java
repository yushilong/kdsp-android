package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Coupons;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.StringUtils;

import java.util.List;

/**
 * Created by lindow on 15/9/7.
 * 月历
 */
public class CouponsChooseAdapter extends BaseListAdapter {


    public CouponsChooseAdapter(Context context, List<?> list) {
        super(context, list);

    }

    @Override
    protected int getItemResId() {
        return R.layout.item_coupons_choose;
    }

    @Override
    protected void initItemView(View convertView, int position) {
        ViewHolder holder = new ViewHolder();

        holder.mPriceTv = (TextView) convertView.findViewById(R.id.price_tv);
        holder.mTimeTv = (TextView) convertView.findViewById(R.id.time_tv);
        holder.mCheckBox = (ImageView) convertView.findViewById(R.id.all_check);
        holder.mLine = convertView.findViewById(R.id.line);
        convertView.setTag(holder);
    }

    @Override
    protected void setItemView(Object tag, Object itemData, int position) {
        if (itemData != null) {
            ViewHolder holder = (ViewHolder) tag;
            final Coupons coupons = (Coupons) mList.get(position);

            if (position == 0) {
                holder.mTimeTv.setText(coupons.endTime);
                holder.mTimeTv.setTextSize(12);
                holder.mTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color.gray9));
                holder.mPriceTv.setVisibility(View.GONE);
                holder.mCheckBox.setVisibility(View.GONE);

            } else {
                holder.mPriceTv.setVisibility(View.VISIBLE);
                if (coupons.isChoose) {
                    holder.mCheckBox.setVisibility(View.VISIBLE);
                    holder.mTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color.purple1));
                    holder.mPriceTv.setTextColor(ContextCompat.getColor(mContext, R.color.purple1));

                } else {
                    holder.mCheckBox.setVisibility(View.GONE);
                    holder.mTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color.gray49));
                    holder.mPriceTv.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }

                if (coupons.isDiscount == 0) {
                    holder.mPriceTv.setText(StringUtils.price2String(coupons.price) + "元");
                } else {
                    holder.mPriceTv.setText(StringUtils.price2String(coupons.price) + "折");
                }
                holder.mTimeTv.setVisibility(View.VISIBLE);
                DateTime dateTime = new DateTime(DateUtils.toDate(coupons.endTime).getTime());
                holder.mTimeTv.setText("有效期至:"+ dateTime.toServerDayString());
            }


            if (position == getCount() - 1) {
                holder.mLine.setVisibility(View.GONE);
            } else {
                holder.mLine.setVisibility(View.VISIBLE);
            }

        }
    }


    class ViewHolder {
        TextView  mPriceTv;         //价格
        TextView  mTimeTv;         //时间
        ImageView mCheckBox;
        View      mLine;
    }

}
