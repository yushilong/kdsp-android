package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.adapter.WordRewardListAdapter;
import com.qizhu.rili.data.WordRewardListDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.VoiceUtils;

/**
 * Created by lindow on 8/18/16.
 * 测字打赏结果页
 */
public class WordRewardListActivity extends BaseListActivity {
    private WordRewardListDataAccessor mDataAccessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceUtils.releaseMedia();
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            mTitle.setText(R.string.rewarded);

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new WordRewardListDataAccessor();
        }
        if (mAdapter == null) {
            mAdapter = new WordRewardListAdapter(this, mDataAccessor.mData);
        }
    }

    @Override
    protected void getData() {
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true));
    }

    @Override
    protected void getNextData() {
        mDataAccessor.getNextData(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    public void pullDownToRefresh() {
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor));
    }


    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, WordRewardListActivity.class);
        context.startActivity(intent);
    }
}
