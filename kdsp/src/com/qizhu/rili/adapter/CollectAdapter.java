package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Article;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.ArticleDetailListActivity;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;

import java.util.List;

/**
 * Created by zhouyue on 8/24/17.
 * 收藏adapter
 */
public class CollectAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.item_collect;
    private int mTypeId;


    public CollectAdapter(Context context, List<?> list,int typeId) {
        super(context, list);
        mTypeId = typeId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, final int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Article) {
            final Article mItem = (Article) itemData;
            holder.mTitleTv.setText(mItem.title);
            holder.mContentTv.setText(mItem.content);
//            holder.mArticleImage.setDefheight(mImageWidth, 720, 320);
            UIUtils.display400Image(mItem.poster, holder.mCollectImage, R.drawable.def_loading_img);//
            itemHolder.itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    int collect;
                    if(mTypeId == 4){
                        collect = 0;
                    }else {
                        collect = mItem.isCollect;
                    }
                    ArticleDetailListActivity.goToPage(mContext, mItem.articleId,mItem.commentCount,collect,mItem.subTitle,mItem.content,position ,mItem.poster,mItem.articleType,mItem.fileUrl );
                }
            });

        }


}


        private class ItemHolder extends RecyclerView.ViewHolder {
            FitWidthImageView mCollectImage;             //价格
            TextView          mTitleTv;                     //标题
             TextView          mContentTv;                    //内容


    private ItemHolder(View convertView) {
        super(convertView);
        mCollectImage = (FitWidthImageView) convertView.findViewById(R.id.collect_image);
        mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);
        mContentTv = (TextView) convertView.findViewById(R.id.content_tv);
    }
}
}
