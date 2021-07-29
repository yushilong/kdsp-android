package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Cart;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.ui.activity.CartListActivity;
import com.qizhu.rili.ui.activity.GoodsDetailActivity;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lindow on 08/03/2017.
 * 购物车数据
 */

public class CartListAdapter extends BaseRecyclerAdapter {
    private int mSkuCount;

    public CartListAdapter(Context context, List<?> list) {
        super(context, list);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item, parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        final ItemHolder holder = (ItemHolder) itemHolder;


        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Cart) {
            final Cart cart = (Cart) itemData;

            mSkuCount = cart.count;
            UIUtils.display400Image(cart.images[0], holder.mItemImage, R.drawable.def_loading_img);
            holder.mTitle.setText(cart.goodsName);

            holder.mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) cart.price / 100, 2));
            holder.mCount.setText("数量：" + cart.count);
            holder.mCountResult.setText("" + mSkuCount);

            if (cart.mHasSelected) {
                holder.mSelected.setImageResource(R.drawable.selected);
            } else {
                holder.mSelected.setImageResource(R.drawable.unselected);
            }

            //商品下架
            if (cart.sku != null && cart.sku.showStatus == 1) {
                if (position > 0) {
                    Cart temp = (Cart) mList.get(position - 1);
                    if (temp.sku != null && temp.sku.showStatus == 1) {
                        holder.mWhiteLine.setVisibility(View.GONE);
                    } else {
                        holder.mWhiteLine.setVisibility(View.VISIBLE);
                    }
                }
                holder.mSelected.setVisibility(View.GONE);
                holder.mInvalid.setVisibility(View.VISIBLE);
                holder.mSpec.setText(R.string.invalid_goods);
                holder.mPrice.setVisibility(View.INVISIBLE);
                holder.mOperateLay.setVisibility(View.GONE);
                if (position == mList.size() - 1) {
                    holder.mClearLay.setVisibility(View.VISIBLE);
                } else {
                    holder.mClearLay.setVisibility(View.GONE);
                }
                holder.mDividerLine.setVisibility(View.GONE);
            } else {
                holder.mWhiteLine.setVisibility(View.VISIBLE);
                holder.mSelected.setVisibility(View.VISIBLE);
                holder.mInvalid.setVisibility(View.GONE);
                holder.mSpec.setText(cart.spec);
                holder.mPrice.setVisibility(View.VISIBLE);
                holder.mOperateLay.setVisibility(View.VISIBLE);
                holder.mClearLay.setVisibility(View.GONE);
                holder.mDividerLine.setVisibility(View.VISIBLE);
            }

            holder.mSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cart.mHasSelected) {
                        cart.mHasSelected = false;
                        holder.mSelected.setImageResource(R.drawable.unselected);
                        if (mContext instanceof CartListActivity) {
                            ((CartListActivity) mContext).deleteCart(cart);
                        }
                    } else {
                        cart.mHasSelected = true;
                        holder.mSelected.setImageResource(R.drawable.selected);
                        if (mContext instanceof CartListActivity) {
                            ((CartListActivity) mContext).addCart(cart);
                        }
                    }
                }
            });
            holder.mItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cart.sku != null) {
                        GoodsDetailActivity.goToPage(mContext, cart.sku.goodsId);
                    }
                }
            });
            holder.mSkuLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cart.sku != null) {
                        GoodsDetailActivity.goToPage(mContext, cart.sku.goodsId);
                    }
                }
            });
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof CartListActivity) {
                        ((CartListActivity) mContext).showLoadingDialog();
                    }
                    KDSPApiController.getInstance().deleteCartById(cart.cartId, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            //刷新购物车数量
                            AppContext.mCartCount = response.optInt("count");
                            AppContext.getAppHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mList.remove(cart);
                                    notifyDataSetChanged();
                                    if (mContext instanceof CartListActivity) {
                                        ((CartListActivity) mContext).dismissLoadingDialog();
                                        ((CartListActivity) mContext).deleteCart(cart);
                                        if (mList.size() == 0) {
                                            ((CartListActivity) mContext).displayEmpty();
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                            if (mContext instanceof CartListActivity) {
                                ((CartListActivity) mContext).dismissLoadingDialog();
                            }
                            showFailureMessage(error);
                        }
                    });
                }
            });
            holder.mCountLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mCountLay.setVisibility(View.GONE);
                    holder.mChangeLay.setVisibility(View.VISIBLE);
                }
            });

            holder.mMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSkuCount > 1) {
                        mSkuCount = mSkuCount - 1;
                        holder.mCountResult.setText("" + mSkuCount);
                    }
                }
            });

            holder.mPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSkuCount = mSkuCount + 1;
                    holder.mCountResult.setText("" + mSkuCount);
                }
            });
            holder.mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof CartListActivity) {
                        ((CartListActivity) mContext).showLoadingDialog();
                    }
                    KDSPApiController.getInstance().addCart(cart.skuId, mSkuCount, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            cart.count = mSkuCount;
                            //刷新购物车数量
                            AppContext.mCartCount = response.optInt("count");
                            AppContext.getAppHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mContext instanceof CartListActivity) {
                                        ((CartListActivity) mContext).dismissLoadingDialog();
                                        if (cart.mHasSelected) {
                                            ((CartListActivity) mContext).addCart(cart);
                                        }
                                    }
                                    holder.mCount.setText("数量：" + cart.count);
                                    holder.mCountLay.setVisibility(View.VISIBLE);
                                    holder.mChangeLay.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                            if (mContext instanceof CartListActivity) {
                                ((CartListActivity) mContext).dismissLoadingDialog();
                            }
                            showFailureMessage(error);
                        }
                    });
                }
            });

            holder.mClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof CartListActivity) {
                        ((CartListActivity) mContext).showLoadingDialog();
                    }
                    KDSPApiController.getInstance().clearInvalidCart(new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            //刷新购物车数量
                            AppContext.mCartCount = response.optInt("count");
                            AppContext.getAppHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    //用iterator遍历删除多个元素，防止由于循环遍历删除时，列表长度发生变化，会发生IndexOutOfBoundsException和ConcurrentModificationException错误
                                    Iterator<Cart> it = (Iterator<Cart>) mList.iterator();
                                    while (it.hasNext()) {
                                        Cart cart1 = it.next();
                                        if (cart1.sku != null && cart1.sku.showStatus == 1) {
                                            it.remove();
                                        }
                                    }
                                    notifyDataSetChanged();
                                    if (mContext instanceof CartListActivity) {
                                        ((CartListActivity) mContext).dismissLoadingDialog();
                                        if (mList.size() == 0) {
                                            ((CartListActivity) mContext).displayEmpty();
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                            if (mContext instanceof CartListActivity) {
                                ((CartListActivity) mContext).dismissLoadingDialog();
                            }
                            showFailureMessage(error);
                        }
                    });
                }
            });
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        View           mWhiteLine;                    //白线
        ImageView      mSelected;                //选择
        TextView       mInvalid;                  //失效
        View           mSkuLay;                       //商品
        YSRLDraweeView mItemImage;          //图片
        TextView       mTitle;                    //名称
        TextView       mSpec;                     //描述
        TextView       mPrice;                    //价格
        View           mOperateLay;                   //操作布局
        TextView       mDelete;                   //删除
        View           mCountLay;                     //数量布局
        TextView       mCount;                    //数量
        View           mChangeLay;                    //数量改变布局
        TextView       mMinus;                    //减
        TextView       mPlus;                     //加
        TextView       mCountResult;              //数量结果
        TextView       mConfirm;                  //确定
        View           mClearLay;                     //清除
        TextView       mClear;                    //清除失效商品
        View           mDividerLine;                  //分割线

        private ItemHolder(View convertView) {
            super(convertView);
            mWhiteLine = convertView.findViewById(R.id.white_line);
            mSelected = (ImageView) convertView.findViewById(R.id.select);
            mInvalid = (TextView) convertView.findViewById(R.id.invalid);
            mSkuLay = convertView.findViewById(R.id.sku_lay);
            mItemImage = (YSRLDraweeView) convertView.findViewById(R.id.sku_image);
            mTitle = (TextView) convertView.findViewById(R.id.title);
            mSpec = (TextView) convertView.findViewById(R.id.spec);
            mPrice = (TextView) convertView.findViewById(R.id.price);
            mOperateLay = convertView.findViewById(R.id.operate_lay);
            mDelete = (TextView) convertView.findViewById(R.id.delete);
            mCountLay = convertView.findViewById(R.id.count_lay);
            mCount = (TextView) convertView.findViewById(R.id.count_text);
            mChangeLay = convertView.findViewById(R.id.change_lay);
            mMinus = (TextView) convertView.findViewById(R.id.minus);
            mPlus = (TextView) convertView.findViewById(R.id.plus);
            mCountResult = (TextView) convertView.findViewById(R.id.count);
            mConfirm = (TextView) convertView.findViewById(R.id.confirm);
            mClearLay = convertView.findViewById(R.id.clear_lay);
            mClear = (TextView) convertView.findViewById(R.id.clear_invalid_goods);
            mDividerLine = convertView.findViewById(R.id.divider_line);
        }
    }
}
