package com.qizhu.rili.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Article;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.ArticleDetailListActivity;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;

import java.util.List;

/**
 * Created by zhouyue on 8/24/17.
 * 神婆日报adapter
 */
public class ArticleAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.item_article;
    private int mImageWidth;
    private Context mContext;

    public ArticleAdapter(Context context, List<?> list) {
        super(context, list);
        this.mContext = context;
        mImageWidth = AppContext.getScreenWidth() - DisplayUtils.dip2px(40);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, final int position) {
        final ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Article) {
            final Article mItem = (Article) itemData;
            holder.mCommentTv.setText("" + mItem.commentCount);
            holder.mSeeTv.setText(""+ mItem.readCount);
            holder.mTitleTv.setText(mItem.title);
            char []s =  mItem.subTitle.toCharArray();
            StringBuffer sb = new StringBuffer();
            for(int i=0;i< s.length;i++){
                sb.append(s[i]);
                if(i != s.length -1){
                    sb.append("   ");
                }

            }

            holder.mSuTitleTv.setText(sb.toString());
            holder.mContentTv.setText(mItem.content);
            if(mItem.articleType == 1){
                holder.mSeeAllTv.setText("阅读全文");
            }else {
                holder.mSeeAllTv.setText("听语音");
            }
//            holder.mArticleImage.setDefheight(mImageWidth, mImageWidth, 600);
//            UIUtils.display600Image(mItem.poster, holder.mArticleImage, R.drawable.def_loading_img);
            UIUtils.displayImage(mItem.poster, holder.mArticleImage, AppContext.getScreenWidth(), R.drawable.def_loading_img,new BaseControllerListener(){


                @Override
                public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    holder.mArticleImage.setInfoHeight(AppContext.mScreenWidth,(ImageInfo)imageInfo);
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                }
            });
            LogUtils.d("-----"+ mImageWidth +","+ AppContext.getScreenWidth());
            itemHolder.itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    ArticleDetailListActivity.goToPage(mContext ,mItem.articleId,mItem.commentCount,mItem.isCollect,mItem.title,mItem.content,position,mItem.poster,mItem.articleType,mItem.fileUrl);

                }
            });

        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        FitWidthImageView mArticleImage;             //
        TextView mSeeTv;                     //查看数
        TextView mCommentTv;                    //评论数
        TextView mTitleTv;                    //标题
        TextView mSuTitleTv;                    //标题
        TextView mContentTv;                 //内容
        TextView mSeeAllTv;                 //内容


        private ItemHolder(View convertView) {
            super(convertView);
            mArticleImage = (FitWidthImageView) convertView.findViewById(R.id.article_image);
            mSeeTv = (TextView) convertView.findViewById(R.id.see_tv);
            mCommentTv = (TextView) convertView.findViewById(R.id.comment_tv);
            mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);
            mContentTv = (TextView) convertView.findViewById(R.id.content_tv);
            mSuTitleTv = (TextView) convertView.findViewById(R.id.subtitle_tv);
            mSeeAllTv = (TextView) convertView.findViewById(R.id.see_all_tv);



        }
    }
}
