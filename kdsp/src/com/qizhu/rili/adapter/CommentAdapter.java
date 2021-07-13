package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Comment;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;

import java.util.List;

/**
 * Created by zhouyue on 8/24/17.
 * 评论adapter
 */
public class CommentAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.item_comment;


    public CommentAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Comment) {
            final Comment mItem = (Comment) itemData;
            holder.mNickNameTv.setText(mItem.nickName);
            holder.mContent.setText(mItem.commentMsg);
            holder.mTime.setText(mItem.createTimeStr);
            if(TextUtils.isEmpty(mItem.headImg)){
                holder.mAvataImage.setImageResource(R.drawable.default_avatar);
            }else {
                UIUtils.displayAvatarImage(mItem.headImg, holder.mAvataImage, R.drawable.default_avatar);
            }
            if(position == getItemCount()-1){
                holder.line.setVisibility(View.GONE);
            }else {
                holder.line.setVisibility(View.VISIBLE);
            }

        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        FitWidthImageView mAvataImage;         //头像
        TextView          mNickNameTv;         //昵称
        TextView mContent;                     //内容
        TextView mTime;                        //评论时间
        View     line;


        private ItemHolder(View convertView) {
            super(convertView);
            mAvataImage = (FitWidthImageView) convertView.findViewById(R.id.avata_image);
            mNickNameTv = (TextView) convertView.findViewById(R.id.nickname_tv);
            mContent = (TextView) convertView.findViewById(R.id.content_tv);
            mTime = (TextView) convertView.findViewById(R.id.time_tv);
            line =  convertView.findViewById(R.id.line);
        }
    }
}
