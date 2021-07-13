package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.AuguryItem;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.AuguryDetailActivity;
import com.qizhu.rili.ui.activity.AugurySubmitActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderDetailActivity;
import com.qizhu.rili.ui.activity.TenYearsFortuneActivity;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * Created by lindow on 11/05/2017.
 */

public class FateRecyclerAdapter extends BaseRecyclerAdapter {
    private int mMode = 0;      //1为下单,2待回复,3已回复,4福豆兑换

    public FateRecyclerAdapter(Context context, List<?> list, int mode) {
        super(context, list);
        mMode = mode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fate_grid_item,parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        final Object itemData = mList.get(position);
        if (itemData != null) {
            if (itemData instanceof FateItem) {
                //问题列表
                final FateItem mLeftItem = (FateItem) mList.get(position * 2);
                UIUtils.display400Image(mLeftItem.imageUrl, itemHolder.mLeftImage, R.drawable.def_loading_img);
                itemHolder.mLeftText.setText(mLeftItem.itemName);

                switch (mMode) {
                    case 1:
                        itemHolder.mLeftPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mLeftItem.price / 100, 2));
                        itemHolder.mLeftMask.setVisibility(View.GONE);
                        itemHolder.mLeftView.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                if (mLeftItem.type == 0) {
                                    AugurySubmitActivity.goToPage(mContext, mLeftItem.itemId, false);
                                } else if (mLeftItem.type == 1) {
                                    HandsOrFaceOrderActivity.goToPage(mContext, mLeftItem.itemId, true);
                                } else if (mLeftItem.type == 2) {
                                    HandsOrFaceOrderActivity.goToPage(mContext, mLeftItem.itemId, false);
                                }
                            }
                        });
                        break;
                    case 4:
                        itemHolder.mLeftPrice.setText(mLeftItem.price / 5 + "福豆");
                        itemHolder.mLeftMask.setVisibility(View.GONE);
                        itemHolder.mLeftView.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                if (mLeftItem.type == 0) {
                                    AugurySubmitActivity.goToPage(mContext, mLeftItem.itemId, true);
                                } else if (mLeftItem.type == 1) {
                                    HandsOrFaceOrderActivity.goToPage(mContext, mLeftItem.itemId, true);
                                } else if (mLeftItem.type == 2) {
                                    HandsOrFaceOrderActivity.goToPage(mContext, mLeftItem.itemId, false);
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }

                if (position * 2 + 1 >= mList.size()) {
                    itemHolder.mRightView.setVisibility(View.INVISIBLE);
                } else {
                    itemHolder.mRightView.setVisibility(View.VISIBLE);
                    final FateItem mRightItem = (FateItem) mList.get(position * 2 + 1);
                    UIUtils.display400Image(mRightItem.imageUrl, itemHolder.mRightImage, R.drawable.def_loading_img);
                    itemHolder.mRightText.setText(mRightItem.itemName);

                    switch (mMode) {
                        case 1:
                            itemHolder.mRightPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mRightItem.price / 100, 2));
                            itemHolder.mRightMask.setVisibility(View.GONE);
                            itemHolder.mRightView.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    if (mRightItem.type == 0) {
                                        AugurySubmitActivity.goToPage(mContext, mRightItem.itemId, false);
                                    } else if (mRightItem.type == 1) {
                                        HandsOrFaceOrderActivity.goToPage(mContext, mRightItem.itemId, true);
                                    } else if (mRightItem.type == 2) {
                                        HandsOrFaceOrderActivity.goToPage(mContext, mRightItem.itemId, false);
                                    }
                                }
                            });
                            break;
                        case 4:
                            itemHolder.mRightPrice.setText(mRightItem.price / 5 + "福豆");
                            itemHolder.mRightMask.setVisibility(View.GONE);
                            itemHolder.mRightView.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    if (mRightItem.type == 0) {
                                        AugurySubmitActivity.goToPage(mContext, mRightItem.itemId, true);
                                    } else if (mRightItem.type == 1) {
                                        HandsOrFaceOrderActivity.goToPage(mContext, mRightItem.itemId, true);
                                    } else if (mRightItem.type == 2) {
                                        HandsOrFaceOrderActivity.goToPage(mContext, mRightItem.itemId, false);
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            } else if (itemData instanceof AuguryItem) {
                //答案列表
                final AuguryItem mLeftItem = (AuguryItem) mList.get(position * 2);
                UIUtils.display400Image(mLeftItem.imageUrl, itemHolder.mLeftImage, R.drawable.def_loading_img);

                if (mLeftItem.type != 0) {
                    itemHolder.mLeftDesLay.setVisibility(View.GONE);
                } else {
                    itemHolder.mLeftDesLay.setVisibility(View.VISIBLE);
                }
                itemHolder.mLeftText.setText(mLeftItem.itemName);
                itemHolder.mLeftPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mLeftItem.totalFee / 100, 2));

                switch (mMode) {
                    case 2:
                        itemHolder.mLeftMask.setVisibility(View.VISIBLE);
                        switch (mLeftItem.type) {
                            case 0:
                                itemHolder.mLeftTip.setText(R.string.augury_answer_tip);
                                break;
                            case 1:
                            case 2:
                                itemHolder.mLeftTip.setText(R.string.answer_tip);
                                break;
                            case 3:
                                itemHolder.mLeftTip.setText(R.string.augury_answer_tip);
                                break;
                        }
                        break;
                    case 3:
                        itemHolder.mLeftMask.setVisibility(View.VISIBLE);
                        itemHolder.mLeftTip.setText(R.string.click_to_detail);
                        itemHolder.mLeftView.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                switch (mLeftItem.type) {
                                    case 0:
                                        AuguryDetailActivity.goToPage(mContext, mLeftItem.ioId, mLeftItem.itemName, mLeftItem.imageUrl, mLeftItem.answerContent, mLeftItem.isRead,0);
                                        break;
                                    case 1:
                                    case 2:
                                        HandsOrFaceOrderDetailActivity.goToPage(mContext, mLeftItem.ioId, mLeftItem.type, mLeftItem.imageUrl, mLeftItem.itemParam, mLeftItem.isRead);
                                        break;
                                    case 3:
                                        TenYearsFortuneActivity.goToPage(mContext, mLeftItem.ioId, true);
                                        break;
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }

                if (position * 2 + 1 >= mList.size()) {
                    itemHolder.mRightView.setVisibility(View.INVISIBLE);
                } else {
                    itemHolder.mRightView.setVisibility(View.VISIBLE);
                    final AuguryItem mRightItem = (AuguryItem) mList.get(position * 2 + 1);
                    UIUtils.display400Image(mRightItem.imageUrl, itemHolder.mRightImage, R.drawable.def_loading_img);
                    if (mRightItem.type != 0) {
                        itemHolder.mRightDesLay.setVisibility(View.GONE);
                    } else {
                        itemHolder.mRightDesLay.setVisibility(View.VISIBLE);
                    }
                    itemHolder.mRightText.setText(mRightItem.itemName);
                    itemHolder.mRightPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mRightItem.totalFee / 100, 2));

                    switch (mMode) {
                        case 2:
                            itemHolder.mRightMask.setVisibility(View.VISIBLE);
                            switch (mRightItem.type) {
                                case 0:
                                    itemHolder.mRightTip.setText(R.string.augury_answer_tip);
                                    break;
                                case 1:
                                case 2:
                                    itemHolder.mRightTip.setText(R.string.answer_tip);
                                    break;
                                case 3:
                                    itemHolder.mRightTip.setText(R.string.augury_answer_tip);
                                    break;
                            }
                            break;
                        case 3:
                            itemHolder.mRightMask.setVisibility(View.VISIBLE);
                            itemHolder.mRightTip.setText(R.string.click_to_detail);
                            itemHolder.mRightView.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    switch (mRightItem.type) {
                                        case 0:
                                            AuguryDetailActivity.goToPage(mContext, mRightItem.ioId, mRightItem.itemName, mRightItem.imageUrl, mRightItem.answerContent, mRightItem.isRead,0);
                                            break;
                                        case 1:
                                        case 2:
                                            HandsOrFaceOrderDetailActivity.goToPage(mContext, mRightItem.ioId, mRightItem.type, mRightItem.imageUrl, mRightItem.itemParam, mRightItem.isRead);
                                            break;
                                        case 3:
                                            TenYearsFortuneActivity.goToPage(mContext, mRightItem.ioId, true);
                                            break;
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
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
        View mLeftMask;
        YSRLDraweeView mLeftImage;
        TextView mLeftText;
        TextView mLeftTip;
        TextView mLeftPrice;
        View mLeftDesLay;
        View mRightView;
        View mRightMask;
        YSRLDraweeView mRightImage;
        TextView mRightText;
        TextView mRightTip;
        TextView mRightPrice;
        View mRightDesLay;

        private ItemHolder(View itemView) {
            super(itemView);
            mLeftView = itemView.findViewById(R.id.left_lay);
            mLeftMask = itemView.findViewById(R.id.left_tip_lay);
            mLeftImage = (YSRLDraweeView) itemView.findViewById(R.id.left_image);
            mLeftText = (TextView) itemView.findViewById(R.id.left_des);
            mLeftPrice = (TextView) itemView.findViewById(R.id.left_price);
            mLeftTip = (TextView) itemView.findViewById(R.id.left_tip);
            mLeftDesLay = itemView.findViewById(R.id.left_des_lay);
            mRightView = itemView.findViewById(R.id.right_lay);
            mRightMask = itemView.findViewById(R.id.right_tip_lay);
            mRightImage = (YSRLDraweeView) itemView.findViewById(R.id.right_image);
            mRightText = (TextView) itemView.findViewById(R.id.right_des);
            mRightPrice = (TextView) itemView.findViewById(R.id.right_price);
            mRightTip = (TextView) itemView.findViewById(R.id.right_tip);
            mRightDesLay = itemView.findViewById(R.id.right_des_lay);
        }
    }
}
