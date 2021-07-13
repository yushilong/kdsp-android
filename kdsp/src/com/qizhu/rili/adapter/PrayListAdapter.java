package com.qizhu.rili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.ShakingSign;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.PrayDetailActivity;
import com.qizhu.rili.utils.DateUtils;

import java.util.List;

/**
 * Created by lindow on 3/31/16.
 * 签文记录的adapter
 */
public class PrayListAdapter extends BaseRecyclerAdapter {
    public static final int ITEM_ID = R.layout.pray_item_lay;

    public PrayListAdapter(Context context, List<?> list) {
        super(context, list);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, null);
        return new ItemHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof ShakingSign) {
            final ShakingSign shakingSign = (ShakingSign) itemData;
            DateTime dateTime = new DateTime(DateUtils.toDate(shakingSign.createTime));
            if (dateTime.min < 10) {
                holder.mTime.setText(dateTime.day + "日\n" + dateTime.hour + ":0" + dateTime.min);
            } else {
                holder.mTime.setText(dateTime.day + "日\n" + dateTime.hour + ":" + dateTime.min);
            }

            holder.mAsk.setText("求" + shakingSign.askSth);
            holder.mWord.setText(shakingSign.name + " " + shakingSign.mean);
            holder.mSignLay.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    PrayDetailActivity.goToPage(mContext, shakingSign);
                }
            });
        }
    }


    class Holder {
        View mSignLay;                      //签文布局
        TextView mTime;                     //时间
        TextView mAsk;                      //所求
        TextView mWord;                     //签文
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        View mSignLay;                      //签文布局
        TextView mTime;                     //时间
        TextView mAsk;                      //所求
        TextView mWord;                     //签文

        private ItemHolder(View convertView) {
            super(convertView);
            mSignLay = convertView.findViewById(R.id.sign_lay);
            mTime = (TextView) convertView.findViewById(R.id.create_time);
            mAsk = (TextView) convertView.findViewById(R.id.ask);
            mWord = (TextView) convertView.findViewById(R.id.word);
        }
    }

}
