package com.qizhu.rili.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.adapter.ArticleAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.bean.Article;
import com.qizhu.rili.data.ArticleDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.CollectListActivity;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

/**
 * Created by zhouyue on 8/25/17
 * 知运页面
 */
public class KnowFortuneFragment extends BaseListFragment {

    private ArticleDataAccessor mDataAccessor;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_COLLECT_REFRESH.equals(action)) {
                getData();
                initAdapter();
                mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();
                LogUtils.d("---->ACTION_COLLECT_REFRESH");
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_COLLECT_REFRESH));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setVisibility(View.GONE);
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            mTitle.setText(R.string.know_fortune);

            titleView.addView(view, params);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {
        view = new ListViewHead(mActivity, R.layout.head_know_fortune);
        view.findViewById(R.id.god_daily_rlayout).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CollectListActivity.goToPage(mActivity,1);
            }
        });

        view.findViewById(R.id.god_voice_rlayout).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CollectListActivity.goToPage(mActivity,2);
            }
        });


        super.addHeadView(refreshableView, view);
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new ArticleDataAccessor(0);
        }
        if (mAdapter == null) {
            mAdapter = new ArticleAdapter(mActivity, mDataAccessor.mData);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
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

    @Override
    protected void onAppBarOffsetChanged(int verticalOffset) {
        mCanPullDownRefresh = verticalOffset >= 0;
    }

    @Override
    protected void onDestroyFragment() {
        super.onDestroyFragment();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    public  void refreshData(int position, int  isCollect){
        Article article = (Article)mDataAccessor.mData.get(position);
        article.isCollect = isCollect;
        mAdapter.notifyDataSetChanged();

    }


}
