package com.qizhu.rili.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Bazi;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.LifeNumberResultActivity;
import com.qizhu.rili.utils.DisplayUtils;

import java.util.List;

/**
 * Created by zhouyue on 12/1/17.
 * 八字adapter
 */
public class BaziRecyclerAdapter extends BaseRecyclerAdapter {
    private static final int ITEM_ID = R.layout.item_bazi;
    private Context mContext;

    public BaziRecyclerAdapter(Context context, List<?> list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent, false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, final int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Bazi) {
            final Bazi mItem = (Bazi) itemData;
            holder.mContentTv.setText(mItem.title);
            holder.mTitleTv.setText(mItem.name );
            float width = 0 ;
            width = mItem.percent *20 / 10 ;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtils.dip2px(width), DisplayUtils.dip2px(14));
            holder.mBaziImage.setLayoutParams(layoutParams);
            holder.mBaziImage.setBackgroundColor(Color.parseColor(getColor(mItem.name)));
            if(position == mList.size() -1){
                holder.mBaziWord.setVisibility(View.VISIBLE);
            }else {
                holder.mBaziWord.setVisibility(View.GONE);
            }
            holder.mItemLay.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    LifeNumberResultActivity.goToPage(mContext, 3, "", 0, "", "", mItem.desc, null, mItem.attrs);

                }
            });

        }
    }

    private  String getColor(String name){
        String color =  "#aa85b5";
        switch (name) {
            case "劫财":
                color =  "#aa85b5";
                break;
            case "食神":
                color =  "#93aad6";
                break;
            case "伤官":
                color =  "#fff8b3";
                break;
            case "正官":
                color =  "#f5a79a";
                break;
            case "正财":
                color =  "#f9cd9c";
                break;
            case "偏财":
                color =  "#c8b6d5";
                break;
            case "正印":
                color =  "#f6b16e";
                break;
            case "偏印":
                color =  "#bfcae6";
                break;
            case "比肩":
                color =  "#c57bac";
                break;
            case "偏官":
                color =  "#fcdbd6";
                break;
            default:
                break;
        }
        return color;
    }
    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView  mTitleTv;                    //标题
        TextView  mContentTv;                 //内容
        TextView  mBaziWord;                 //总言
        TextView mBaziImage;                 //总言
        LinearLayout mItemLay;                 //总言


        private ItemHolder(View convertView) {
            super(convertView);
            mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);
            mContentTv = (TextView) convertView.findViewById(R.id.content_tv);
            mBaziWord = (TextView) convertView.findViewById(R.id.bazi_word);
            mBaziImage = (TextView) convertView.findViewById(R.id.ba_zi_image);
            mItemLay = (LinearLayout) convertView.findViewById(R.id.item_lay);

        }
    }
}
