package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.one2oneOrders;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;

import java.util.List;

/**
 * Created by zhouyue on 19/04/2018.
 */
public class WeChatCouponsItemAdapter extends BaseRecyclerAdapter {
    public static final int ITEM_ID = R.layout.wechat_coupons_item_lay;
    private String mDec;


    public WeChatCouponsItemAdapter(Context context, List<?> list, String dec) {
        super(context, list);
        mDec = dec;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        final ItemHolder holder = (ItemHolder) itemHolder;
        final one2oneOrders.one2oneOrderDetails itemData = (one2oneOrders.one2oneOrderDetails) mList.get(position);
        if (itemData != null) {
            holder.mContentTv.setText(itemData.itemName + ":  " + itemData.num);
            if (itemData.isUsed == 0) {
                holder.mStatusTv.setBackground(ContextCompat.getDrawable(mContext,R.drawable.round_purple40));
                holder.mStatusTv.setText(mContext.getString(R.string.copy));
                holder.mStatusTv.setEnabled(true);
            } else {
                holder.mStatusTv.setBackground(ContextCompat.getDrawable(mContext,R.drawable.round_radius15_gray41));
                holder.mStatusTv.setText(mContext.getString(R.string.un_Usable));
                holder.mStatusTv.setEnabled(false);
            }
        }

        holder.mStatusTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MethodCompat.copyText(itemData.num);
                UIUtils.toastMsg("已复制");

            }
        });
        if (position == 0) {
            holder.mTitleTv.setVisibility(View.VISIBLE);
            holder.mTitleTv.setText(mDec);
            holder.mBgLay.setBackgroundResource(R.drawable.coupons_item_2);
        } else {
            holder.mTitleTv.setVisibility(View.GONE);
            holder.mBgLay.setBackgroundResource(R.drawable.coupons_item_3);
        }


    }


    private class ItemHolder extends RecyclerView.ViewHolder {

        TextView     mContentTv;
        TextView     mStatusTv;
        TextView     mTitleTv;
        RelativeLayout mBgLay;


        private ItemHolder(View convertView) {
            super(convertView);
            mContentTv = (TextView) convertView.findViewById(R.id.content_tv);
            mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
            mTitleTv = (TextView) convertView.findViewById(R.id.title_gb_tv);
            mBgLay = (RelativeLayout) convertView.findViewById(R.id.bg_lay);


        }
    }
}
