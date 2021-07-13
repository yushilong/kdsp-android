package com.qizhu.rili.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.AugurySubmitActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderActivity;
import com.qizhu.rili.ui.activity.MasterAskDetailActivity;
import com.qizhu.rili.ui.activity.TenYearsFortuneActivity;
import com.qizhu.rili.ui.activity.TestNameActivity;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

public class CustomGridAdapter extends BaseListAdapter {
    private static final int ITEM_ID = R.layout.custom_grid_item;

    public CustomGridAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    protected int getItemResId() {
        return ITEM_ID;
    }

    @Override
    protected void initItemView(View convertView, int position) {
        ViewHolder holdler = new ViewHolder();
        holdler.mLeftView = convertView.findViewById(R.id.left_lay);
        holdler.mLeftImage = (YSRLDraweeView) convertView.findViewById(R.id.left_image);
        holdler.mLeftText = (TextView) convertView.findViewById(R.id.left_des);
        holdler.mLeftPrice = (TextView) convertView.findViewById(R.id.left_price);
        holdler.mRightView = convertView.findViewById(R.id.right_lay);
        holdler.mRightImage = (YSRLDraweeView) convertView.findViewById(R.id.right_image);
        holdler.mRightText = (TextView) convertView.findViewById(R.id.right_des);
        holdler.mRightPrice = (TextView) convertView.findViewById(R.id.right_price);
        convertView.setTag(holdler);
    }

    @Override
    protected void setItemView(Object tag, Object itemData, int position) {
        if (itemData != null) {
            ViewHolder holder = (ViewHolder) tag;
            final FateItem mLeftItem = (FateItem) mList.get(position * 2);
            UIUtils.display400Image(mLeftItem.imageUrl, holder.mLeftImage, R.drawable.def_loading_img);
            holder.mLeftText.setText(mLeftItem.itemName);
            holder.mLeftPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mLeftItem.price / 100, 2));
            holder.mLeftView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (mLeftItem.type == 0) {
                        AugurySubmitActivity.goToPage(mContext, mLeftItem.itemId, false);
                    } else if (mLeftItem.type == 1) {
                        HandsOrFaceOrderActivity.goToPage(mContext, mLeftItem.itemId, true);
                    } else if (mLeftItem.type == 2) {
                        HandsOrFaceOrderActivity.goToPage(mContext, mLeftItem.itemId, false);
                    } else if (mLeftItem.type == 3) {
                        TenYearsFortuneActivity.goToPage(mContext, mLeftItem.itemId);
                    } else if (mLeftItem.type == 4) {
                        TestNameActivity.goToPage(mContext, mLeftItem.itemId,0);
                    } else if (mLeftItem.type == 5) {
                        MasterAskDetailActivity.goToPage(mContext, mLeftItem.itemId);
                    } else {
                        UIUtils.toastMsg("请升级新版本~");
                    }
                }
            });
            if (position * 2 + 1 >= mList.size()) {
                holder.mRightView.setVisibility(View.INVISIBLE);
            } else {
                holder.mRightView.setVisibility(View.VISIBLE);
                final FateItem mRightItem = (FateItem) mList.get(position * 2 + 1);
                UIUtils.display400Image(mRightItem.imageUrl, holder.mRightImage, R.drawable.def_loading_img);
                holder.mRightText.setText(mRightItem.itemName);
                holder.mRightPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mRightItem.price / 100, 2));
                holder.mRightView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if (mRightItem.type == 0) {
                            AugurySubmitActivity.goToPage(mContext, mRightItem.itemId, false);
                        } else if (mRightItem.type == 1) {
                            HandsOrFaceOrderActivity.goToPage(mContext, mRightItem.itemId, true);
                        } else if (mRightItem.type == 2) {
                            HandsOrFaceOrderActivity.goToPage(mContext, mRightItem.itemId, false);
                        } else if (mRightItem.type == 3) {
                            TenYearsFortuneActivity.goToPage(mContext, mRightItem.itemId);
                        } else if (mRightItem.type == 4) {
                            TestNameActivity.goToPage(mContext, mRightItem.itemId,0);
                        } else if (mRightItem.type == 5) {
                            MasterAskDetailActivity.goToPage(mContext, mRightItem.itemId);
                        } else {
                            UIUtils.toastMsg("请升级新版本~");
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getCount() {
        int size = mList.size() / 2;
        size += mList.size() % 2 == 1 ? 1 : 0;
        return size;
    }

    class ViewHolder {
        View mLeftView;
        YSRLDraweeView mLeftImage;
        TextView mLeftText;
        TextView mLeftPrice;
        View mRightView;
        YSRLDraweeView mRightImage;
        TextView mRightText;
        TextView mRightPrice;
    }
}
