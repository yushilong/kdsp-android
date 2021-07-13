package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Chat;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.ReplyCommentActivity;
import com.qizhu.rili.ui.activity.ReplyListActivity;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.util.List;

/**
 * Created by lindow on 08/04/2017.
 * 回复留言
 */

public class ReplyCommentAdapter extends BaseRecyclerAdapter {

    public ReplyCommentAdapter(Context context, List<?> list) {
        super(context, list);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reply_comment_lay, null);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Chat) {
            final Chat item = (Chat) itemData;

            UIUtils.display400Image(item.imageUrl, holder.avatar, R.drawable.def_loading_img);
            switch (item.msgType) {
                case 0:
                    holder.contentTxt.setText(item.content);
                    break;
                case 1:
                    holder.contentTxt.setText("[图片]");
                    break;
                case 2:
                    holder.contentTxt.setText("[商品]");
                    break;
            }
            holder.mTime.setText(item.createTime);
            if (item.count > 0) {
                holder.mUnread.setVisibility(View.VISIBLE);
                holder.mUnread.setText(item.count + "");
            } else {
                holder.mUnread.setVisibility(View.GONE);
            }

            holder.contentLay.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    ReplyListActivity.goToPageWithResult((ReplyCommentActivity)mContext, item.userId);
                }
            });
        }
    }







    private class ItemHolder extends RecyclerView.ViewHolder {
        private View contentLay;
        private YSRLDraweeView avatar;
        private TextView contentTxt;
        private TextView mTime;
        private TextView mUnread;

        private ItemHolder(View convertView) {
            super(convertView);
            contentLay = convertView.findViewById(R.id.item_lay);
            avatar = (YSRLDraweeView) convertView.findViewById(R.id.user_avatar);
            contentTxt = (TextView) convertView.findViewById(R.id.content);
            mTime = (TextView) convertView.findViewById(R.id.time);
            mUnread = (TextView) convertView.findViewById(R.id.reply_comment_unread);
        }
    }
}
