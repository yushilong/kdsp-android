package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.one2oneOrders;
import com.qizhu.rili.widget.KDSPRecyclerView;

import java.util.List;

/**
 * Created by zhouyue on 19/04/2018.
 * 占卜adapter
 */
public class WeChatCouponsAdapter extends BaseRecyclerAdapter {
    public static final int ITEM_ID = R.layout.wechat_coupons_item;


    public WeChatCouponsAdapter(Context context, List<?> list) {
        super(context, list);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        final ItemHolder holder = (ItemHolder) itemHolder;
        final one2oneOrders itemData = (one2oneOrders)mList.get(position);

        if(itemData != null){

            holder.mItemImage.instanceForListView(LinearLayoutManager.VERTICAL, false);
            holder.mItemImage.setAdapter(new WeChatCouponsItemAdapter(mContext, itemData.mOne2oneOrderDetails ,itemData.description));
        }
    }




    private class ItemHolder extends RecyclerView.ViewHolder {

        KDSPRecyclerView mItemImage;


        private ItemHolder(View convertView) {
            super(convertView);

            mItemImage = (KDSPRecyclerView) convertView.findViewById(R.id.coupons_rv);


        }
    }
}
