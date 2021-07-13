package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Goods;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.GoodsDetailActivity;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * Created by lindow on 01/03/2017.
 * 商品列表页
 */

public class GoodsGridAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.custom_grid_item;

    public GoodsGridAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID,parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Goods item = (Goods)mList.get(position);
        if (item != null) {
            final Goods mLeftItem = (Goods) mList.get(position * 2);
            UIUtils.display400Image(mLeftItem.images[0], holder.mLeftImage, R.drawable.def_loading_img);
            holder.mLeftText.setText(mLeftItem.title);
            holder.mLeftPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mLeftItem.minPrice / 100, 2));
            holder.mLeftView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    GoodsDetailActivity.goToPage(mContext, mLeftItem.goodsId);
                }
            });
            if (position * 2 + 1 >= mList.size()) {
                holder.mRightView.setVisibility(View.INVISIBLE);
            } else {
                holder.mRightView.setVisibility(View.VISIBLE);
                final Goods mRightItem = (Goods) mList.get(position * 2 + 1);
                UIUtils.display400Image(mRightItem.images[0], holder.mRightImage, R.drawable.def_loading_img);
                holder.mRightText.setText(mRightItem.title);
                holder.mRightPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mRightItem.minPrice / 100, 2));
                holder.mRightView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        GoodsDetailActivity.goToPage(mContext, mRightItem.goodsId);
                    }
                });
            }
        }
    }



    @Override
    public int getItemCount() {
        int size = mList.size() / 2;
        size += mList.size() % 2 == 1 ? 1 : 0;
        return size;
    }



    private class ItemHolder extends RecyclerView.ViewHolder {
        View mLeftView;
        YSRLDraweeView mLeftImage;
        TextView mLeftText;
        TextView mLeftPrice;
        View mRightView;
        YSRLDraweeView mRightImage;
        TextView mRightText;
        TextView mRightPrice;

        private ItemHolder(View convertView) {
            super(convertView);
            mLeftView = convertView.findViewById(R.id.left_lay);
            mLeftImage = (YSRLDraweeView) convertView.findViewById(R.id.left_image);
            mLeftText = (TextView) convertView.findViewById(R.id.left_des);
            mLeftPrice = (TextView) convertView.findViewById(R.id.left_price);
            mRightView = convertView.findViewById(R.id.right_lay);
            mRightImage = (YSRLDraweeView) convertView.findViewById(R.id.right_image);
            mRightText = (TextView) convertView.findViewById(R.id.right_des);
            mRightPrice = (TextView) convertView.findViewById(R.id.right_price);
        }
    }


}
