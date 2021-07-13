package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.AuguryCart;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.ui.activity.MasterAuguryCartActivity;
import com.qizhu.rili.ui.dialog.MasterAuguryCartFragment;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindow on 8/17/16.
 * 占卜adapter
 */
public class MasterAuguryCartListAdapter extends BaseRecyclerAdapter {
    public static final int ITEM_ID = R.layout.master_augury_cart_item;


    public MasterAuguryCartListAdapter(Context context, List<?> list) {
        super(context, list);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        final ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof AuguryCart) {
            final AuguryCart mItem = (AuguryCart) itemData;




            LogUtils.d("---- perPrice:" +mItem.itemName +(double)10* 0.01+  mItem.price + ":"+mItem.perPrice);
            holder.mTitle.setText(mItem.itemName);
            holder.mContent.setText(mItem.title);
            holder.mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mItem.price / 100, 2));
            if (mItem.mHasSelected) {
                holder.mSelected.setImageResource(R.drawable.circle_pink16);
            } else {
                holder.mSelected.setImageResource(R.drawable.circle_gray_white_bg);
            }
            holder.itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (mItem.mHasSelected) {
                        holder.mSelected.setImageResource(R.drawable.circle_gray_white_bg);
                        mItem.mHasSelected = false;
                        if (mContext instanceof MasterAuguryCartActivity) {
                            ((MasterAuguryCartActivity) mContext).deleteCart((ArrayList<AuguryCart>)mList);
                        }
                    } else {
                        String s = "¥ " + mContext.getString(R.string.click_price);

                        if (s.equals(holder.mPrice.getText().toString())) {
                            ((BaseActivity) mContext).showDialogFragment(MasterAuguryCartFragment.newInstance(mItem.price,mItem.perPrice,new MasterAuguryCartFragment.MasterAuguryCartInterface() {
                                @Override
                                public void getArea(String s) {
                                    setPrice(holder, mItem, s);

                                }
                            }), "房屋面积");
                        } else {
                            holder.mSelected.setImageResource(R.drawable.circle_pink16);
                            mItem.mHasSelected = true;
                            if (mContext instanceof MasterAuguryCartActivity) {
                                ((MasterAuguryCartActivity) mContext).addCart((ArrayList<AuguryCart>)mList);
                            }
                        }

                    }
                }
            });
            if (mItem.type == 2) {

                if (mItem.mHasSelected) {
                    holder.mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mItem.price /100, 2));
                } else {
                    holder.mPrice.setText("¥ " + mContext.getString(R.string.click_price));
                }

                holder.mPrice.setEnabled(true);
            } else {
                holder.mPrice.setEnabled(false);
            }
            holder.mPrice.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    ((BaseActivity) mContext).showDialogFragment(MasterAuguryCartFragment.newInstance(mItem.price,mItem.perPrice,new MasterAuguryCartFragment.MasterAuguryCartInterface() {
                        @Override
                        public void getArea(String s) {
                            setPrice(holder, mItem, s);

                        }
                    }), "房屋面积");
                }
            });

        }
    }

    private void setPrice(ItemHolder holder, AuguryCart mItem, String s) {
        holder.mPrice.setText("¥ " + StringUtils.toFloat(s, 0) * (double)mItem.perPrice/100);
        LogUtils.d("---" +s + "--"+ StringUtils.toFloat(s, 0) );
        mItem.price = (int) (StringUtils.toFloat(s, 0) * mItem.perPrice);
        holder.mSelected.setImageResource(R.drawable.circle_pink16);
        mItem.mHasSelected = true;
        if (mContext instanceof MasterAuguryCartActivity) {
            ((MasterAuguryCartActivity) mContext).addCart((ArrayList<AuguryCart>)mList);

        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        TextView  mTitle;
        TextView  mContent;
        ImageView mSelected;                //选择
        TextView  mPrice;                    //价格

        private ItemHolder(View convertView) {
            super(convertView);

            mSelected = (ImageView) convertView.findViewById(R.id.select);
            mTitle = (TextView) convertView.findViewById(R.id.title_tv);
            mContent = (TextView) convertView.findViewById(R.id.content_tv);
            mPrice = (TextView) convertView.findViewById(R.id.price_tv);

        }
    }
}
