package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.listener.LoginSuccessListener;
import com.qizhu.rili.ui.activity.AugurySubmitActivity;
import com.qizhu.rili.ui.activity.LoginChooserActivity;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;

import java.util.List;

/**
 * Created by lindow on 8/15/16.
 * 运势分类的adapter
 */
public class FateCatListAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.fate_item_lay;

    private String mCatId;               //分类id,如果分类为空，则为福豆兑换界面

    public FateCatListAdapter(Context context, List<?> list, String catId) {
        super(context, list);
        mCatId = catId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof FateItem) {
            final FateItem mItem = (FateItem) itemData;

            holder.mItemImage.setDefheight(AppContext.getScreenWidth(), 750, 400);
            UIUtils.display600Image(mItem.imageUrl, holder.mItemImage, R.drawable.def_loading_img);
            holder.mName.setText(mItem.itemName);
            holder.mCount.setText(mResources.getString(R.string.has_inferred, mItem.playTimes));
            if (TextUtils.isEmpty(mCatId)) {
                holder.mPrice.setText(mItem.price / 5 + "福豆");
            } else {
                holder.mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mItem.price / 100, 2) + " 元");
            }

            if (1 == mItem.isHot) {
                holder.mFlag.setVisibility(View.VISIBLE);
                holder.mFlag.setImageResource(R.drawable.hot_fate);
            } else if (1 == mItem.isNew) {
                holder.mFlag.setVisibility(View.VISIBLE);
                holder.mFlag.setImageResource(R.drawable.new_fate);
            } else {
                holder.mFlag.setVisibility(View.GONE);
            }

            holder.mItemLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppContext.isAnonymousUser()) {
                        LoginChooserActivity.goToPage(mContext, new LoginSuccessListener() {
                            @Override
                            public void success() {
                                AugurySubmitActivity.goToPage(mContext, mItem.itemId, TextUtils.isEmpty(mCatId));
                            }
                        });
                    } else {
                        AugurySubmitActivity.goToPage(mContext, mItem.itemId, TextUtils.isEmpty(mCatId));
                    }
                }
            });
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        View mItemLay;                      //布局
        FitWidthImageView mItemImage;       //图片
        TextView mName;                     //名称
        TextView mCount;                    //个数
        TextView mPrice;                    //价格
        ImageView mFlag;                    //最新或最热

        private ItemHolder(View convertView) {
            super(convertView);
            mItemLay = convertView.findViewById(R.id.item_lay);
            mItemImage = (FitWidthImageView) convertView.findViewById(R.id.item_image);
            mName = (TextView) convertView.findViewById(R.id.item_name);
            mCount = (TextView) convertView.findViewById(R.id.count);
            mPrice = (TextView) convertView.findViewById(R.id.price);
            mFlag = (ImageView) convertView.findViewById(R.id.fate_flag);



        }
    }
}
