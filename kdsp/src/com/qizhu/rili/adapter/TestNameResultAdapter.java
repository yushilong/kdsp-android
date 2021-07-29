package com.qizhu.rili.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.Name;

import java.util.List;

/**
 * Created by lindow on 01/03/2017.
 * 商品列表页
 */

public class TestNameResultAdapter extends BaseRecyclerAdapter {
    private static final int      ITEM_ID = R.layout.name_item;
    private              String[] mDigit  = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    private boolean mTestName;            //是否测名


    public TestNameResultAdapter(Context context, List<?> list, boolean changeName) {
        super(context, list);
        mTestName = changeName;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);
        if (itemData != null && itemData instanceof Name) {
            final Name mItem = (Name) itemData;
            holder.mNameTv.setText(mItem.familyName + mItem.lastName);
            holder.mScoreTv.setText(mItem.score + "分");

            if (mTestName) {
                holder.mRecommendTv.setVisibility(View.GONE);

                ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.black));
                SpannableStringBuilder builder1 = new SpannableStringBuilder().append("总论: ").append(mItem.pandect);
                builder1.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mPandectTv.setText(builder1);

                SpannableStringBuilder builder2 = new SpannableStringBuilder().append("性格: ").append(mItem.character);
                builder2.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mCharacterTv.setText(builder2);

                SpannableStringBuilder builder3 = new SpannableStringBuilder().append("事业: ").append(mItem.career);
                builder3.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mCareerTv.setText(builder3);

                SpannableStringBuilder builder4 = new SpannableStringBuilder().append("家庭: ").append(mItem.family);
                builder4.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mFamilyTv.setText(builder4);

                SpannableStringBuilder builder5 = new SpannableStringBuilder().append("婚姻: ").append(mItem.marriage);
                builder5.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mMarriageTv.setText(builder5);

                SpannableStringBuilder builder6 = new SpannableStringBuilder().append("子女: ").append(mItem.children);
                builder6.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mChildrenTv.setText(builder6);

                SpannableStringBuilder builder7 = new SpannableStringBuilder().append("财运: ").append(mItem.fortune);
                builder7.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mFortuneTv.setText(builder7);

                SpannableStringBuilder builder8 = new SpannableStringBuilder().append("健康: ").append(mItem.health);
                builder8.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mHealthTv.setText(builder8);

                SpannableStringBuilder builder9 = new SpannableStringBuilder().append("老运: ").append(mItem.oldLucky);
                builder9.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mOldLuckyTv.setText(builder9);
            } else {
                holder.mRecommendTv.setVisibility(View.VISIBLE);
                holder.mPandectTv.setText(mItem.pandect);
                holder.mCharacterTv.setVisibility(View.GONE);
                holder.mCareerTv.setVisibility(View.GONE);
                holder.mFamilyTv.setVisibility(View.GONE);
                holder.mMarriageTv.setVisibility(View.GONE);
                holder.mChildrenTv.setVisibility(View.GONE);
                holder.mFortuneTv.setVisibility(View.GONE);
                holder.mHealthTv.setVisibility(View.GONE);
                holder.mOldLuckyTv.setVisibility(View.GONE);
                String recommend = "";
                if (mLast) {
                    recommend = mDigit[position + 5];
                } else {
                    recommend = mDigit[position];
                }
                holder.mRecommendTv.setText("推荐" + recommend);
            }
            holder.mFortuneBaseTv.setText(mItem.baseFortune);
            holder.mFortuneSuccessTv.setText(mItem.successFortune);
            holder.mFortuneFriendTv.setText(mItem.friendFortune);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView mRecommendTv;
        TextView mNameTv;
        TextView mScoreTv;
        TextView mFortuneBaseTv;
        TextView mFortuneSuccessTv;
        TextView mFortuneFriendTv;
        TextView mPandectTv;
        TextView mCharacterTv;
        TextView mCareerTv;
        TextView mFamilyTv;
        TextView mMarriageTv;
        TextView mChildrenTv;
        TextView mFortuneTv;
        TextView mHealthTv;
        TextView mOldLuckyTv;

        private ItemHolder(View convertView) {
            super(convertView);
            mRecommendTv = (TextView) convertView.findViewById(R.id.recommend_tv);
            mNameTv = (TextView) convertView.findViewById(R.id.name_tv);
            mScoreTv = (TextView) convertView.findViewById(R.id.score_tv);
            mFortuneBaseTv = (TextView) convertView.findViewById(R.id.fortune_base_tv);
            mFortuneSuccessTv = (TextView) convertView.findViewById(R.id.fortune_success_tv);
            mFortuneFriendTv = (TextView) convertView.findViewById(R.id.fortune_friend_tv);
            mPandectTv = (TextView) convertView.findViewById(R.id.pandect);
            mCharacterTv = (TextView) convertView.findViewById(R.id.character);
            mCareerTv = (TextView) convertView.findViewById(R.id.career);
            mFamilyTv = (TextView) convertView.findViewById(R.id.family);
            mMarriageTv = (TextView) convertView.findViewById(R.id.marriage);
            mChildrenTv = (TextView) convertView.findViewById(R.id.children);
            mFortuneTv = (TextView) convertView.findViewById(R.id.fortune);
            mHealthTv = (TextView) convertView.findViewById(R.id.health);
            mOldLuckyTv = (TextView) convertView.findViewById(R.id.oldLucky);
        }
    }


}
