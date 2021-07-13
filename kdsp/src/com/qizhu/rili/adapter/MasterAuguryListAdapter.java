package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.AuguryCart;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.MasterAuguryCartActivity;
import com.qizhu.rili.widget.FitWidthImageView;

import java.util.List;

/**
 * Created by lindow on 8/17/16.
 * 占卜adapter
 */
public class MasterAuguryListAdapter extends BaseRecyclerAdapter {
    public static final int ITEM_ID = R.layout.master_augury_item_lay;


    public MasterAuguryListAdapter(Context context, List<?> list) {
        super(context, list);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        final ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof AuguryCart) {
            final AuguryCart mItem = (AuguryCart) itemData;
            holder.mItemImage.setBackgroundResource(mItem.imageId);
            holder.mContent.setText(mItem.title);
//            UIUtils.display300Image(mItem.imageUrl, holder.mItemImage, R.drawable.def_loading_img);
//            holder.mTitle.setText(mItem.itemName);
            holder.itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    MasterAuguryCartActivity.goToPage(mContext,mItem.type);
                }
            });
        }
    }




    private class ItemHolder extends RecyclerView.ViewHolder {

        FitWidthImageView mItemImage;       //图片
        TextView          mTitle;
        TextView          mContent;

        private ItemHolder(View convertView) {
            super(convertView);

            mItemImage = (FitWidthImageView) convertView.findViewById(R.id.item_image);
            mTitle = (TextView) convertView.findViewById(R.id.title_tv);
            mContent = (TextView) convertView.findViewById(R.id.content_tv);

        }
    }
}
