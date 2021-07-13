package com.qizhu.rili.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Chat;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.GoodsDetailActivity;
import com.qizhu.rili.ui.activity.ImageZoomViewer;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by lindow on 23/03/2017.
 * 留言
 */

public class MessageListAdapter extends BaseListAdapter {
    private static final int VIEW_TYPE_KDSP = 0;
    private static final int VIEW_TYPE_SELF = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private String mAvatarUrl;

    public MessageListAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mList.size()) {
            Chat chat = (Chat) mList.get(position);
            if (chat.direction == 0) {
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
                convertView = mInflater.inflate(R.layout.item_chat_kdsp, null);
            } else {
                convertView = mInflater.inflate(R.layout.item_chat_self, null);
            }
            holder.lastItem = convertView.findViewById(R.id.last_item);
            holder.avatarLay = convertView.findViewById(R.id.avatar_lay);
            holder.avatar = (YSRLDraweeView) convertView.findViewById(R.id.user_avatar);
            holder.talentImg = (ImageView) convertView.findViewById(R.id.brand_auth);
            holder.contentLay = convertView.findViewById(R.id.content_lay);
            holder.contentTxt = (TextView) convertView.findViewById(R.id.content);
            holder.contentImg = (FitWidthImageView) convertView.findViewById(R.id.content_img);
            holder.goodsLay = convertView.findViewById(R.id.goods_lay);
            holder.mGoodsImage = (YSRLDraweeView) convertView.findViewById(R.id.image);
            holder.mGoodsTitle = (TextView) convertView.findViewById(R.id.goods_name);
            holder.mGoodsPrice = (TextView) convertView.findViewById(R.id.goods_price);

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
            final Chat chat = (Chat) mList.get(position);
            holder.lastItem.setVisibility(View.GONE);

            holder.contentLay.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(chat.content)) {
                if (1 == chat.msgType) {
                    holder.contentTxt.setVisibility(View.GONE);
                    holder.contentImg.setVisibility(View.VISIBLE);
                    holder.goodsLay.setVisibility(View.GONE);
                    UIUtils.display400Image(chat.content, holder.contentImg, R.drawable.def_loading_img);
                    holder.contentImg.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            ImageZoomViewer.goToPage(mContext, chat.content);
                        }
                    });
                } else if (2 == chat.msgType) {
                    holder.contentTxt.setVisibility(View.GONE);
                    holder.contentImg.setVisibility(View.GONE);
                    holder.goodsLay.setVisibility(View.VISIBLE);
                    try {
                        JSONObject jsonObject = new JSONObject(chat.content);
                        UIUtils.display200Image(jsonObject.optString("images"), holder.mGoodsImage, R.drawable.def_loading_img);
                        holder.mGoodsTitle.setText(jsonObject.optString("title"));
                        holder.mGoodsPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) jsonObject.optInt("minPrice") / 100, 2));
                        final String goodsId = jsonObject.optString("goodsId");
                        holder.goodsLay.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                GoodsDetailActivity.goToPage(mContext, goodsId);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    holder.contentTxt.setText(chat.content);
                    holder.contentTxt.setVisibility(View.VISIBLE);
                    holder.contentImg.setVisibility(View.GONE);
                    holder.goodsLay.setVisibility(View.GONE);
                    holder.contentTxt.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            MethodCompat.copyText(chat.content);
                            UIUtils.toastMsg("已复制", Toast.LENGTH_SHORT, DisplayUtils.dip2px(145));
                            return false;
                        }
                    });
                }
            } else {
                holder.contentTxt.setVisibility(View.GONE);
            }

            holder.avatarLay.setVisibility(View.VISIBLE);
            if (chat.direction == 0) {
                UIUtils.displayAvatarImage(AppContext.mUser.imageUrl, holder.avatar, R.drawable.default_avatar);
            } else {
                UIUtils.displayAvatarImage(mAvatarUrl, holder.avatar, R.drawable.default_avatar);
            }

            holder.talentImg.setVisibility(View.GONE);
        } else {
            holder.lastItem.setVisibility(View.VISIBLE);
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

    public void setAvatarUrl(String url) {
        mAvatarUrl = url;
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
        public View avatarLay;
        public View contentLay;
        public TextView contentTxt;
        public FitWidthImageView contentImg;
        public View goodsLay;
        public YSRLDraweeView mGoodsImage;
        public TextView mGoodsTitle;
        public TextView mGoodsPrice;
    }
}
