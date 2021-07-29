package com.qizhu.rili.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Divination;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.MediaStateChangedListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.ShareActivity;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.VoiceUtils;

import java.util.List;

/**
 * Created by lindow on 8/18/16.
 * 测字打赏
 */
public class WordRewardListAdapter extends BaseRecyclerAdapter {
    public static final int ITEM_ID = R.layout.word_reward_item_lay;

    public WordRewardListAdapter(Context context, List<?> list) {
        super(context, list);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(ITEM_ID, parent,false);
        return new ItemHolder(view);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        final  ItemHolder holder = (ItemHolder) itemHolder;
        final Object itemData = mList.get(position);

        if (itemData != null && itemData instanceof Divination) {
            final Divination mItem = (Divination) itemData;

            holder.mWord.setText(mItem.word);
            if (!mItem.answers.isEmpty()) {
                final Divination mAnswer = mItem.answers.get(0);
                holder.mPlayLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        VoiceUtils.playVoice(mAnswer.content, new MediaStateChangedListener() {
                            @Override
                            public void onStart(String url) {
                                holder.mVoice.setImageResource(R.drawable.voice_anim);
                                AnimationDrawable mAnimationDrawable = (AnimationDrawable) holder.mVoice.getDrawable();
                                mAnimationDrawable.start();
                            }

                            @Override
                            public void onPause(String url) {
                                AnimationDrawable mAnimationDrawable = (AnimationDrawable) holder.mVoice.getDrawable();
                                mAnimationDrawable.stop();
                                holder.mVoice.setImageResource(R.drawable.voice3);
                            }

                            @Override
                            public void onStop(String url) {
                                AnimationDrawable mAnimationDrawable = (AnimationDrawable) holder.mVoice.getDrawable();
                                mAnimationDrawable.stop();
                                holder.mVoice.setImageResource(R.drawable.voice3);
                            }

                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                AnimationDrawable mAnimationDrawable = (AnimationDrawable) holder.mVoice.getDrawable();
                                mAnimationDrawable.stop();
                                holder.mVoice.setImageResource(R.drawable.voice3);
                            }
                        });
                    }
                });
            }

            holder.mShare.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    OperUtils.mSmallCat = OperUtils.SMALL_CAT_FONT;
                    OperUtils.mKeyCat = mItem.dtId;
                    ShareActivity.goToShare(mContext, ShareUtils.getShareTitle(ShareUtils.Share_Type_CEZI, ""),
                            ShareUtils.getShareContent(ShareUtils.Share_Type_CEZI, ""),
                            getShareUrl(mItem), "", ShareUtils.Share_Type_CEZI, StatisticsConstant.subType_RENYICE);
                    VoiceUtils.releaseMedia();
                }
            });

        }

    }

    private String getShareUrl(Divination divination) {
        return AppConfig.API_BASE + "app/shareExt/testFontAndPalmResult" + "?userId=" + AppContext.userId + "&dtId=" + divination.dtId;
    }




    private class ItemHolder extends RecyclerView.ViewHolder {
        View mItemLay;                      //布局
        ImageView mShare;                   //分享
        TextView mWord;                     //字
        View mPlayLay;                      //播放
        ImageView mVoice;                   //语音

        private ItemHolder(View convertView) {
            super(convertView);
            mItemLay = convertView.findViewById(R.id.item_lay);
            mItemLay = convertView.findViewById(R.id.item_lay);
            mShare = (ImageView) convertView.findViewById(R.id.share_btn);
            mWord = (TextView) convertView.findViewById(R.id.word);
            mPlayLay = convertView.findViewById(R.id.play_lay);
            mVoice = (ImageView) convertView.findViewById(R.id.voice_play);
        }
    }
}
