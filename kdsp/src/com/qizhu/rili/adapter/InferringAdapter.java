package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.bean.Goods;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.GoodsDetailActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderActivity;
import com.qizhu.rili.ui.activity.AugurySubmitActivity;
import com.qizhu.rili.ui.activity.MasterAskDetailActivity;
import com.qizhu.rili.ui.activity.TenYearsFortuneActivity;
import com.qizhu.rili.ui.activity.TestNameActivity;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;

import java.util.List;

/**
 * Created by lindow on 20/02/2017.
 * 解命的adapter
 */
public class InferringAdapter extends BaseRecyclerAdapter {
    private int mType;              //模式
    private int mWidth;             //图片宽度

    public InferringAdapter(Context context, List<?> list, int type) {
        super(context, list);
        mWidth = (AppContext.getScreenWidth() + DisplayUtils.dip2px(25)) / 3;
        mType = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inferring_item_lay, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        final Object item = mList.get(position);
        itemHolder.mItem.setLayoutParams(new RecyclerView.LayoutParams(mWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (item instanceof FateItem) {
            itemHolder.mDes.setText(((FateItem) item).itemName);
            itemHolder.mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) ((FateItem) item).price / 100, 2));

            if (mType == 3) {
                switch (position) {
                    case 0:
                        UIUtils.setImageResource(itemHolder.mItemImg, R.drawable.hot_item_1);
                        break;
                    case 1:
                        UIUtils.setImageResource(itemHolder.mItemImg, R.drawable.hot_item_2);
                        break;
                    case 2:
                        UIUtils.setImageResource(itemHolder.mItemImg, R.drawable.hot_item_3);
                        break;
                    case 3:
                        UIUtils.setImageResource(itemHolder.mItemImg, R.drawable.hot_item_4);
                        break;
                    case 4:
                        UIUtils.setImageResource(itemHolder.mItemImg, R.drawable.hot_item_5);
                        break;
                    default:
                        UIUtils.setImageResource(itemHolder.mItemImg, R.drawable.hot_item_1);
                        break;
                }
            } else {
                UIUtils.display400Image(((FateItem) item).imageUrl, itemHolder.mItemImg, R.drawable.def_loading_img);
            }

            itemHolder.mItemImg.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    int type = ((FateItem) item).type;
                    if (type == 0) {
                        AugurySubmitActivity.goToPage(mContext, ((FateItem) item).itemId, false);
                    } else if (type == 1) {
                        HandsOrFaceOrderActivity.goToPage(mContext, ((FateItem) item).itemId, true);
                    } else if (type == 2) {
                        HandsOrFaceOrderActivity.goToPage(mContext, ((FateItem) item).itemId, false);
                    } else if (type == 3) {
                        TenYearsFortuneActivity.goToPage(mContext, ((FateItem) item).itemId);
                    } else if (type == 4) {
                        TestNameActivity.goToPage(mContext, ((FateItem) item).itemId,0);
                    } else if (type == 5) {
                        MasterAskDetailActivity.goToPage(mContext, ((FateItem) item).itemId);
                    } else {
                        UIUtils.toastMsg("请升级新版本~");
                    }
                }
            });

        } else if (item instanceof Goods) {
            itemHolder.mDes.setText(((Goods) item).title);
            itemHolder.mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) ((Goods) item).minPrice / 100, 2));
            UIUtils.display400Image(((Goods) item).images[0], itemHolder.mItemImg, R.drawable.def_loading_img);
            itemHolder.mItemImg.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    GoodsDetailActivity.goToPage(mContext, ((Goods) item).goodsId);
                }
            });
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private View              mItem;                          //item
        private FitWidthImageView mItemImg;          //图片
        private TextView          mDes;                       //描述
        private TextView          mPrice;                     //价格

        private ItemHolder(View itemView) {
            super(itemView);
            mItem = itemView.findViewById(R.id.item_lay);
            mItemImg = (FitWidthImageView) itemView.findViewById(R.id.item_image);
            mDes = (TextView) itemView.findViewById(R.id.item_des);
            mPrice = (TextView) itemView.findViewById(R.id.item_price);
        }
    }
}
