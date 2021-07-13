package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.bean.OrderItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.ui.activity.LogisticsDetailActivity;
import com.qizhu.rili.ui.activity.OrderListActivity;
import com.qizhu.rili.ui.activity.RefundProgressActivity;
import com.qizhu.rili.ui.dialog.OrderOperDialogFragment;
import com.qizhu.rili.ui.dialog.SkuPayDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by lindow on 14/03/2017.
 * 订单的adapter
 */

public class OrderListAdapter extends BaseRecyclerAdapter {


    public OrderListAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_item_lay, parent,false);
        return new ItemHolder(view);
    }



    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof OrderDetail) {
            final OrderDetail mItem = (OrderDetail) itemData;

            holder.mOrderId.setText("订单编号：" + mItem.orderNum);

            holder.mSkuLay.removeAllViews();
            //默认父订单可以退款
            boolean mRefundItem = true;
            for (final OrderItem orderItem : mItem.mOrderItems) {
                View view = mInflater.inflate(R.layout.order_sku_lay, null);
                YSRLDraweeView image = (YSRLDraweeView) view.findViewById(R.id.sku_image);
                UIUtils.display400Image(orderItem.images[0], image, R.drawable.def_loading_img);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView spec = (TextView) view.findViewById(R.id.spec);
                TextView price = (TextView) view.findViewById(R.id.price);
                TextView mOldPrice = (TextView) view.findViewById(R.id.old_price);
                title.setText(orderItem.goodsName);
                if (TextUtils.isEmpty(orderItem.spec)) {
                    spec.setText("数量：" + orderItem.count);
                } else {
                    spec.setText(orderItem.spec + ",数量：" + orderItem.count);
                }
                price.setText("¥ " + StringUtils.roundingDoubleStr((double) orderItem.price / 100, 2));
//                UIUtils.setThruLine(mOldPrice);
//                mOldPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) orderItem.price / 100, 2));
                //待发货和待收货状态显示子订单的退款
                TextView state = (TextView) view.findViewById(R.id.state);
                if (mItem.orderRefundStatus == OrderItem.REFUND_START && mItem.status != OrderDetail.WAIT_PAY) {
                    if (orderItem.detailStatus != OrderItem.REFUND_START || (mItem.status == OrderDetail.HAS_PAID || mItem.status == OrderDetail.HAS_SEND)) {
                        state.setVisibility(View.VISIBLE);
                        state.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RefundProgressActivity.goToPage(mContext, orderItem.orderId, orderItem.odId);
                            }
                        });
                        if (orderItem.detailStatus != OrderItem.REFUND) {
                            mRefundItem = false;
                        }
                        displayRefund(state, orderItem.detailStatus);
                    }
                } else {
                    state.setVisibility(View.GONE);
                }

                View view1 = new View(mContext);
                view1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                holder.mSkuLay.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                holder.mSkuLay.addView(view1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtils.dip2px(1)));
            }
            holder.mCount.setText("共" + mItem.mOrderItems.size() + "件商品，");
            holder.mPrice.setText("合计：" + StringUtils.roundingDoubleStr((double) mItem.totalFee / 100, 2));

            //根据订单状态显示不同的按钮执行不同的操作
            if (mItem.status == OrderDetail.WAIT_PAY) {
                holder.mText1.setVisibility(View.INVISIBLE);
                holder.mText2.setVisibility(View.VISIBLE);
                holder.mText3.setVisibility(View.VISIBLE);

                holder.mText2.setText(R.string.cancel_order);
                holder.mText2.setBackgroundResource(R.drawable.round_gray9_white);
                holder.mText2.setTextColor(ContextCompat.getColor(mContext, R.color.gray9));
                holder.mText3.setText(R.string.pay);
                holder.mText3.setBackgroundResource(R.drawable.round_purple32_white);
                holder.mText3.setTextColor(ContextCompat.getColor(mContext, R.color.purple32));
                //取消订单
                holder.mText2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OrderListActivity) mContext).showDialogFragment(OrderOperDialogFragment.newInstance(OrderOperDialogFragment.CANCEL, mItem.orderId), "取消订单");
                    }
                });
                //支付
                holder.mText3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OrderListActivity) mContext).showDialogFragment(SkuPayDialogFragment.newInstance(mItem.orderId, mItem.totalFee), "付款");
                    }
                });
            } else if (mItem.status == OrderDetail.HAS_PAID) {
                holder.mText1.setVisibility(View.INVISIBLE);
                holder.mText3.setVisibility(View.VISIBLE);

                if (mRefundItem) {
                    holder.mText2.setVisibility(View.VISIBLE);
                    displayRefund(holder.mText2, mItem.orderRefundStatus);
                    //退款
                    holder.mText2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RefundProgressActivity.goToPage(mContext, mItem.orderId, "");
                        }
                    });
                } else {
                    holder.mText2.setVisibility(View.INVISIBLE);
                }

                holder.mText3.setText(R.string.has_paid);
                holder.mText3.setTextColor(ContextCompat.getColor(mContext, R.color.purple32));
            } else if (mItem.status == OrderDetail.HAS_SEND) {
                holder.mText2.setVisibility(View.VISIBLE);
                holder.mText3.setVisibility(View.VISIBLE);

                if (mRefundItem) {
                    holder.mText1.setVisibility(View.VISIBLE);
                    displayRefund(holder.mText1, mItem.orderRefundStatus);
                    //退款
                    holder.mText1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RefundProgressActivity.goToPage(mContext, mItem.orderId, "");
                        }
                    });
                } else {
                    holder.mText1.setVisibility(View.INVISIBLE);
                }

                holder.mText2.setText(R.string.view_logistics);
                holder.mText2.setTextColor(ContextCompat.getColor(mContext, R.color.gray9));
                holder.mText2.setBackgroundResource(R.drawable.round_gray9_white);
                holder.mText3.setText(R.string.confirm_receive);
                holder.mText3.setTextColor(ContextCompat.getColor(mContext, R.color.purple32));
                holder.mText3.setBackgroundResource(R.drawable.round_purple32_white);

                //查看物流
                holder.mText2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogisticsDetailActivity.goToPage(mContext, mItem.orderId, mItem.mOrderItems.get(0).images[0]);
                    }
                });
                //确认收货
                holder.mText3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OrderListActivity) mContext).showLoadingDialog();
                        KDSPApiController.getInstance().confirmReceipt(mItem.orderId, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                ((OrderListActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((OrderListActivity) mContext).dismissLoadingDialog();
                                        AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
                                        refreshPayOrder(mItem);
                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                ((OrderListActivity) mContext).dismissLoadingDialog();
                                showFailureMessage(error);
                            }
                        });
                    }
                });
            } else {
                holder.mText2.setVisibility(View.VISIBLE);
                holder.mText3.setVisibility(View.VISIBLE);

                //已完成订单里仅仅显示退款中的状态而不让用户主动发起退款
                if (mRefundItem && mItem.orderRefundStatus != OrderItem.REFUND_START) {
                    holder.mText1.setVisibility(View.VISIBLE);
                    displayRefund(holder.mText1, mItem.orderRefundStatus);
                    //退款
                    holder.mText1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RefundProgressActivity.goToPage(mContext, mItem.orderId, "");
                        }
                    });
                } else {
                    holder.mText1.setVisibility(View.INVISIBLE);
                }

                holder.mText2.setText(R.string.delete_order);
                holder.mText2.setBackgroundResource(R.drawable.round_gray9_white);
                holder.mText2.setTextColor(ContextCompat.getColor(mContext, R.color.gray9));
                if (mItem.status == OrderDetail.SUCCESS) {
                    holder.mText3.setText(R.string.deal_success);
                } else if (mItem.status == OrderDetail.CANCEL) {
                    holder.mText3.setText(R.string.deal_close);
                }

                holder.mText3.setTextColor(ContextCompat.getColor(mContext, R.color.purple32));
                //删除订单
                holder.mText2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OrderListActivity) mContext).showDialogFragment(OrderOperDialogFragment.newInstance(OrderOperDialogFragment.DEL, mItem.orderId), "删除订单");
                    }
                });
            }
        }
    }





    private void displayRefund(TextView text, int status) {
        switch (status) {
            case OrderItem.REFUND_START:
                text.setText(R.string.refund);
                text.setTextColor(ContextCompat.getColor(mContext, R.color.gray9));
                text.setBackgroundResource(R.drawable.round_gray9_white);
                break;
            case OrderItem.REFUND_WAITING:
                text.setText(R.string.refunding);
                text.setTextColor(ContextCompat.getColor(mContext, R.color.yellow4));
                text.setBackgroundResource(R.drawable.round_yellow4_white);
                break;
            case OrderItem.REFUND_END:
                text.setText(R.string.refund_success);
                text.setTextColor(ContextCompat.getColor(mContext, R.color.yellow4));
                text.setBackgroundResource(R.drawable.round_yellow4_white);
                break;
            case OrderItem.REFUND_FAILED:
                text.setText(R.string.refund_failed);
                text.setTextColor(ContextCompat.getColor(mContext, R.color.yellow4));
                text.setBackgroundResource(R.drawable.round_yellow4_white);
                break;
        }
    }

    /**
     * 刷新付款成功的订单
     */
    public void refreshPayOrder(OrderDetail order) {
        mList.remove(order);
        notifyDataSetChanged();
    }



    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView mOrderId;                  //订单编号
        LinearLayout mSkuLay;               //sku布局
        TextView mCount;                    //个数
        TextView mPrice;                    //价格
        TextView mText1;                    //价格
        TextView mText2;                    //价格
        TextView mText3;                    //价格

        private ItemHolder(View convertView) {
            super(convertView);
            mOrderId = (TextView) convertView.findViewById(R.id.order_id);
            mSkuLay = (LinearLayout) convertView.findViewById(R.id.sku_lay);
            mCount = (TextView) convertView.findViewById(R.id.count);
            mPrice = (TextView) convertView.findViewById(R.id.price);
            mText1 = (TextView) convertView.findViewById(R.id.text1);
            mText2 = (TextView) convertView.findViewById(R.id.text2);
            mText3 = (TextView) convertView.findViewById(R.id.text3);
        }
    }
}
