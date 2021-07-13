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
import com.qizhu.rili.ui.activity.MasterAskResultActivity;
import com.qizhu.rili.ui.activity.TenYearsFortuneActivity;
import com.qizhu.rili.ui.activity.TestNameResultActivity;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * Created by lindow on 24/03/2017.
 * 占卜问题
 */
public class FateGridAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.fate_grid_item;

    private int mMode = 0;      //1为下单,2待回复,3已回复,4福豆兑换

    public FateGridAdapter(Context context, List<?> list, int mode) {
        super(context, list);
        mMode = mode;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null) {
            if (itemData instanceof FateItem) {
                //问题列表
                final FateItem mLeftItem = (FateItem) mList.get(position * 2);
                UIUtils.display400Image(mLeftItem.imageUrl, holder.mLeftImage, R.drawable.def_loading_img);
                holder.mLeftText.setText(mLeftItem.itemName);

                switch (mMode) {
                    case 1:
                        holder.mLeftPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mLeftItem.price / 100, 2));
                        holder.mLeftMask.setVisibility(View.GONE);
                        holder.mLeftView.setOnClickListener(new OnSingleClickListener() {
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
                        holder.mLeftPrice.setText(mLeftItem.price / 5 + "福豆");
                        holder.mLeftMask.setVisibility(View.GONE);
                        holder.mLeftView.setOnClickListener(new OnSingleClickListener() {
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
                    holder.mRightView.setVisibility(View.INVISIBLE);
                } else {
                    holder.mRightView.setVisibility(View.VISIBLE);
                    final FateItem mRightItem = (FateItem) mList.get(position * 2 + 1);
                    UIUtils.display400Image(mRightItem.imageUrl, holder.mRightImage, R.drawable.def_loading_img);
                    holder.mRightText.setText(mRightItem.itemName);

                    switch (mMode) {
                        case 1:
                            holder.mRightPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mRightItem.price / 100, 2));
                            holder.mRightMask.setVisibility(View.GONE);
                            holder.mRightView.setOnClickListener(new OnSingleClickListener() {
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
                            holder.mRightPrice.setText(mRightItem.price / 5 + "福豆");
                            holder.mRightMask.setVisibility(View.GONE);
                            holder.mRightView.setOnClickListener(new OnSingleClickListener() {
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
                UIUtils.display400Image(mLeftItem.imageUrl, holder.mLeftImage, R.drawable.def_loading_img);

                if (mLeftItem.type != 0) {
                    holder.mLeftDesLay.setVisibility(View.GONE);
                } else {
                    holder.mLeftDesLay.setVisibility(View.VISIBLE);
                }
                holder.mLeftText.setText(mLeftItem.itemName);
                holder.mLeftPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mLeftItem.totalFee / 100, 2));

                switch (mMode) {
                    case 2:
                        holder.mLeftMask.setVisibility(View.VISIBLE);
                        switch (mLeftItem.type) {
                            case 0:
                                holder.mLeftTip.setText(R.string.augury_answer_tip);
                                break;
                            case 1:
                            case 2:
                                holder.mLeftTip.setText(R.string.answer_tip);
                                break;
                            case 3:
                                holder.mLeftTip.setText(R.string.augury_answer_tip);
                                break;
                            case 4:
                                holder.mLeftTip.setText(R.string.augury_answer_for_name_tip);
                                break;
                            case 5:
                                holder.mLeftTip.setText(R.string.answer_tip);
                                break;
                            case 6:
                                holder.mLeftTip.setText(R.string.augury_answer_tip);
                                break;
                            case 7:
                                holder.mLeftTip.setText(R.string.augury_answer_tip);
                                break;
                                default:
                                    holder.mLeftTip.setText(R.string.augury_answer_tip);
                                    break;
                        }
                        break;
                    case 3:
                        holder.mLeftMask.setVisibility(View.VISIBLE);
                        holder.mLeftTip.setText(R.string.click_to_detail);
                        holder.mLeftView.setOnClickListener(new OnSingleClickListener() {
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
                                    case 4:
                                        TestNameResultActivity.goToPage(mContext, mLeftItem.itemParam, mLeftItem.ioId);
                                        break;
                                    case 5:
                                        MasterAskResultActivity.goToPage(mContext, mLeftItem.ioId, mLeftItem.itemParam, mLeftItem.isRead);
                                        break;
                                    case 6:
                                        AuguryDetailActivity.goToPage(mContext, mLeftItem.ioId, mLeftItem.itemName, mLeftItem.imageUrl, mLeftItem.answerContent, mLeftItem.isRead,6);
                                        break;
                                    case 7:
                                        AuguryDetailActivity.goToPage(mContext, mLeftItem.ioId, mLeftItem.itemName, mLeftItem.imageUrl, mLeftItem.answerContent, mLeftItem.isRead,7);
                                        break;
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }

                if (position * 2 + 1 >= mList.size()) {
                    holder.mRightView.setVisibility(View.INVISIBLE);
                } else {
                    holder.mRightView.setVisibility(View.VISIBLE);
                    final AuguryItem mRightItem = (AuguryItem) mList.get(position * 2 + 1);
                    UIUtils.display400Image(mRightItem.imageUrl, holder.mRightImage, R.drawable.def_loading_img);
                    if (mRightItem.type != 0) {
                        holder.mRightDesLay.setVisibility(View.GONE);
                    } else {
                        holder.mRightDesLay.setVisibility(View.VISIBLE);
                    }
                    holder.mRightText.setText(mRightItem.itemName);
                    holder.mRightPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mRightItem.totalFee / 100, 2));

                    switch (mMode) {
                        case 2:
                            holder.mRightMask.setVisibility(View.VISIBLE);
                            switch (mRightItem.type) {
                                case 0:
                                    holder.mRightTip.setText(R.string.augury_answer_tip);
                                    break;
                                case 1:
                                case 2:
                                    holder.mRightTip.setText(R.string.answer_tip);
                                    break;
                                case 3:
                                    holder.mRightTip.setText(R.string.augury_answer_tip);
                                    break;
                                case 4:
                                    holder.mRightTip.setText(R.string.augury_answer_for_name_tip);
                                    break;
                                case 5:
                                    holder.mRightTip.setText(R.string.answer_tip);
                                case 6:
                                    holder.mLeftTip.setText(R.string.augury_answer_tip);
                                    break;
                                case 7:
                                    holder.mLeftTip.setText(R.string.augury_answer_tip);
                                    break;
                                default:
                                    holder.mLeftTip.setText(R.string.augury_answer_tip);
                                    break;
                            }
                            break;
                        case 3:
                            holder.mRightMask.setVisibility(View.VISIBLE);
                            holder.mRightTip.setText(R.string.click_to_detail);
                            holder.mRightView.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    switch (mRightItem.type) {
                                        case 0:
                                            AuguryDetailActivity.goToPage(mContext, mRightItem.ioId, mRightItem.itemName, mRightItem.imageUrl, mRightItem.answerContent, mRightItem.isRead);
                                            break;
                                        case 1:
                                        case 2:
                                            HandsOrFaceOrderDetailActivity.goToPage(mContext, mRightItem.ioId, mRightItem.type, mRightItem.imageUrl, mRightItem.itemParam, mRightItem.isRead);
                                            break;
                                        case 3:
                                            TenYearsFortuneActivity.goToPage(mContext, mRightItem.ioId, true);
                                            break;
                                        case 4:
                                            TestNameResultActivity.goToPage(mContext, mRightItem.itemParam, mRightItem.ioId);
                                            break;
                                        case 5:
                                            MasterAskResultActivity.goToPage(mContext, mRightItem.ioId, mRightItem.itemParam, mRightItem.isRead);
                                            break;
                                        case 6:
                                            AuguryDetailActivity.goToPage(mContext, mRightItem.ioId, mRightItem.itemName, mRightItem.imageUrl, mRightItem.answerContent, mRightItem.isRead,6);
                                            break;
                                        case 7:
                                            AuguryDetailActivity.goToPage(mContext, mRightItem.ioId, mRightItem.itemName, mRightItem.imageUrl, mRightItem.answerContent, mRightItem.isRead);
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

    /**
     * 刷新删除的订单
     */
    public void refreshDeleteItem(AuguryItem item) {
        mList.remove(item);
        notifyDataSetChanged();
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        View           mLeftView;
        View           mLeftMask;
        YSRLDraweeView mLeftImage;
        TextView       mLeftText;
        TextView       mLeftTip;
        TextView       mLeftPrice;
        View           mLeftDesLay;
        View           mRightView;
        View           mRightMask;
        YSRLDraweeView mRightImage;
        TextView       mRightText;
        TextView       mRightTip;
        TextView       mRightPrice;
        View           mRightDesLay;

        private ItemHolder(View convertView) {
            super(convertView);
            mLeftView = convertView.findViewById(R.id.left_lay);
            mLeftMask = convertView.findViewById(R.id.left_tip_lay);
            mLeftImage = (YSRLDraweeView) convertView.findViewById(R.id.left_image);
            mLeftText = (TextView) convertView.findViewById(R.id.left_des);
            mLeftPrice = (TextView) convertView.findViewById(R.id.left_price);
            mLeftTip = (TextView) convertView.findViewById(R.id.left_tip);
            mLeftDesLay = convertView.findViewById(R.id.left_des_lay);
            mRightView = convertView.findViewById(R.id.right_lay);
            mRightMask = convertView.findViewById(R.id.right_tip_lay);
            mRightImage = (YSRLDraweeView) convertView.findViewById(R.id.right_image);
            mRightText = (TextView) convertView.findViewById(R.id.right_des);
            mRightPrice = (TextView) convertView.findViewById(R.id.right_price);
            mRightTip = (TextView) convertView.findViewById(R.id.right_tip);
            mRightDesLay = convertView.findViewById(R.id.right_des_lay);
        }
    }

}
