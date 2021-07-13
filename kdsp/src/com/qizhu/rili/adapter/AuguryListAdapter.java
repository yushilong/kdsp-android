package com.qizhu.rili.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.AuguryItem;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.ui.activity.AuguryDetailActivity;
import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderDetailActivity;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;
import com.qizhu.rili.widget.TimeTextView;

import java.util.List;

/**
 * Created by lindow on 8/17/16.
 * 占卜adapter
 */
public class AuguryListAdapter extends BaseRecyclerAdapter {
    public static final int ITEM_ID = R.layout.augury_item_lay;

    private boolean mHasReply;                  //是否已回复

    public AuguryListAdapter(Context context, List<?> list, boolean hasReply) {
        super(context, list);
        mHasReply = hasReply;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        final ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof AuguryItem) {
            final AuguryItem mItem = (AuguryItem) itemData;

            holder.mItemImage.setDefheight(AppContext.getScreenWidth(), 750, 400);
            UIUtils.display600Image(mItem.imageUrl, holder.mItemImage, R.drawable.def_loading_img);
            holder.mName.setText(mItem.itemName);

            if (mHasReply) {
                holder.mMask.setVisibility(View.GONE);
                holder.mOverTime.setVisibility(View.GONE);
                holder.mCountDown.setVisibility(View.GONE);
                holder.mPrice.setTextSize(16);
                holder.mPrice.setText(R.string.click_to_detail);
                holder.mItemLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItem.type != 0) {
                            HandsOrFaceOrderDetailActivity.goToPage(mContext, mItem.ioId, mItem.type, mItem.imageUrl, mItem.itemParam, mItem.isRead);
                        } else {
                            AuguryDetailActivity.goToPage(mContext, mItem.ioId, mItem.itemName, mItem.imageUrl, mItem.answerContent, mItem.isRead);
                        }
                    }
                });
            } else {
                holder.mMask.setVisibility(View.VISIBLE);
                DateTime dateTime1 = new DateTime();
                if (dateTime1.hour < 8 || dateTime1.hour >= 22) {
                    holder.mOverTime.setVisibility(View.VISIBLE);
                    holder.mCountDown.setVisibility(View.GONE);
                    holder.mPrice.setVisibility(View.GONE);
                } else {
                    holder.mOverTime.setVisibility(View.GONE);
                    holder.mCountDown.setVisibility(View.VISIBLE);
                    DateTime dateTime = new DateTime(DateUtils.getServerTime(mItem.payTime));
                    if (mItem.type != 0) {
                        long time = 7200 - (new DateTime().getTime() - dateTime.getTime()) / 1000;
                        if (time > 0) {
                            holder.mTime.setTimes(DateUtils.convertTimeToDHMS(time), TimeTextView.TYPE_REPLY_HANDS);
                            CountDownTimer countDownTimer = new CountDownTimer(time * 1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    if (mContext instanceof BaseActivity && !((BaseActivity) mContext).isFinishing()) {
                                        holder.mTime.ComputeTime(TimeTextView.TYPE_REPLY_HANDS);
                                    } else {
                                        holder.mTime.cancelCountDown();
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    holder.mCountDown.setVisibility(View.GONE);
                                }
                            };
                            holder.mTime.setCountDownTimer(countDownTimer);
                            countDownTimer.start();
                        } else {
                            holder.mCountDown.setVisibility(View.GONE);
                        }
                    } else {
                        long time = 300 - (new DateTime().getTime() - dateTime.getTime()) / 1000;
                        if (time > 0) {
                            holder.mTime.setTimes(DateUtils.convertTimeToDHMS(time), TimeTextView.TYPE_REPLY);
                            CountDownTimer countDownTimer = new CountDownTimer(time * 1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    if (mContext instanceof BaseActivity && !((BaseActivity) mContext).isFinishing()) {
                                        holder.mTime.ComputeTime(TimeTextView.TYPE_REPLY);
                                    } else {
                                        holder.mTime.cancelCountDown();
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    holder.mCountDown.setVisibility(View.GONE);
                                }
                            };
                            holder.mTime.setCountDownTimer(countDownTimer);
                            countDownTimer.start();
                        } else {
                            holder.mCountDown.setVisibility(View.GONE);
                        }
                    }

                    holder.mPrice.setText(R.string.answer_tip);
                }
            }

        }
    }




    private class ItemHolder extends RecyclerView.ViewHolder {
        View              mItemLay;                      //布局
        View              mMask;                         //遮罩层
        FitWidthImageView mItemImage;       //图片
        TextView          mName;                     //名称
        TextView          mPrice;                    //价格
        View              mOverTime;                     //超时
        View              mCountDown;                    //倒计时
        TimeTextView      mTime;                 //时间

        private ItemHolder(View convertView) {
            super(convertView);
            mItemLay = convertView.findViewById(R.id.item_lay);
            mMask = convertView.findViewById(R.id.mask_view);
            mItemImage = (FitWidthImageView) convertView.findViewById(R.id.item_image);
            mName = (TextView) convertView.findViewById(R.id.item_name);
            mPrice = (TextView) convertView.findViewById(R.id.price);
            mOverTime = convertView.findViewById(R.id.overtime);
            mCountDown = convertView.findViewById(R.id.count_down);
            mTime = (TimeTextView) convertView.findViewById(R.id.time);
        }
    }
}
