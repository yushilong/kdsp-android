package com.qizhu.rili.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Feedback;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.ImageZoomViewer;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * Created by lindow on 11/5/15.
 * 意见反馈的adapter
 */
public class FeedbackListAdapter extends BaseListAdapter {
    private static final int VIEW_TYPE_KDSP = 0;
    private static final int VIEW_TYPE_SELF = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public FeedbackListAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mList.size()) {
            Feedback feedback = (Feedback) mList.get(position);
            //如果replyUserId不为空，那么就为我们系统的回复，否则为发帖者的回复
            if (TextUtils.isEmpty(feedback.replyUserId)) {
                return VIEW_TYPE_SELF;
            }
        }
        return VIEW_TYPE_KDSP;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    @Override
    protected int getItemResId() {
        return 0;
    }


    @Override
    public View buildView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (getItemViewType(position) == VIEW_TYPE_KDSP) {
                convertView = mInflater.inflate(R.layout.item_feedback_kdsp, null);
            } else {
                convertView = mInflater.inflate(R.layout.item_feedback_self, null);
            }
            holder.lastItem = convertView.findViewById(R.id.last_item);
            holder.avatarLay = convertView.findViewById(R.id.avatar_lay);
            holder.avatar = (YSRLDraweeView) convertView.findViewById(R.id.user_avatar);
            holder.talentImg = (ImageView) convertView.findViewById(R.id.brand_auth);
            holder.dateTxt = (TextView) convertView.findViewById(R.id.date);
            holder.contentLay = convertView.findViewById(R.id.content_lay);
            holder.contentTxt = (TextView) convertView.findViewById(R.id.content);
            holder.contentImg = (FitWidthImageView) convertView.findViewById(R.id.content_img);
            holder.contentTxt.setMaxWidth(AppContext.getScreenWidth() - DisplayUtils.dip2px(60 + 60));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setData(holder, position);

        return convertView;
    }

    /**
     * 填充数据
     */
    private void setData(final ViewHolder holder, int position) {
        if (position < mList.size()) {
            final Feedback feedback = (Feedback) mList.get(position);
            holder.lastItem.setVisibility(View.GONE);
            if (0 == feedback.time) {
                holder.dateTxt.setVisibility(View.GONE);
            } else {
                holder.dateTxt.setVisibility(View.VISIBLE);
                //回复
                holder.dateTxt.setText(DateUtils.getFormatDateFromInt(feedback.time));
            }

            holder.contentLay.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(feedback.content)) {
                if (1 == feedback.type) {
                    holder.contentTxt.setVisibility(View.GONE);
                    holder.contentImg.setVisibility(View.VISIBLE);
                    holder.contentImg.setDefheight(DisplayUtils.dip2px(100), feedback.width, feedback.height);
                    UIUtils.displayImage(feedback.content, holder.contentImg, 400, null, new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            holder.contentImg.setInfoHeight(DisplayUtils.dip2px(100), imageInfo);
                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {
                            super.onFailure(id, throwable);
                        }
                    });
                    holder.contentImg.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            ImageZoomViewer.goToPage(mContext, feedback.content);
                        }
                    });
                } else {
                    holder.contentTxt.setText(feedback.content);
                    holder.contentTxt.setVisibility(View.VISIBLE);
                    holder.contentImg.setVisibility(View.GONE);
                    holder.contentTxt.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            MethodCompat.copyText(feedback.content);
                            UIUtils.toastMsg("已复制", Toast.LENGTH_SHORT, DisplayUtils.dip2px(145));
                            return false;
                        }
                    });
                }
            } else {
                holder.contentTxt.setVisibility(View.GONE);
            }

            holder.avatarLay.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(feedback.replyUserId)) {
                UIUtils.displayAvatarImage(feedback.imageUrl, holder.avatar, R.drawable.default_avatar);
            } else {
                UIUtils.displayAvatarImage(feedback.replyImageUrl, holder.avatar, R.drawable.default_avatar);
            }

            holder.talentImg.setVisibility(View.GONE);
        } else {
            holder.lastItem.setVisibility(View.VISIBLE);
            holder.dateTxt.setVisibility(View.GONE);
            holder.contentLay.setVisibility(View.GONE);
            holder.avatarLay.setVisibility(View.GONE);
        }
    }

    /**
     * 由于getItemResId()返回的是0，那么必须重写此函数返回列表长度。否则列表默认长度为空
     */
    @Override
    public int getCustomedViewCount() {
        return mList.size() + 1;
    }

    @Override
    protected void initItemView(View convertView, int position) {
    }

    @Override
    protected void setItemView(Object tag, Object itemData, int position) {
    }

    class ViewHolder {
        public View lastItem;               //最后一个元素，虚拟的，仅用于定位
        public YSRLDraweeView avatar;
        public ImageView talentImg;
        public TextView dateTxt;
        public View avatarLay;
        public View contentLay;
        public TextView contentTxt;
        public FitWidthImageView contentImg;
    }
}
