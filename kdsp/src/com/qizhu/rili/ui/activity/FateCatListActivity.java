package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.FateGridAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.data.FateCatDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

/**
 * Created by lindow on 8/15/16.
 * 运势分类列表
 */
public class FateCatListActivity extends BaseListActivity {
    private FateCatDataAccessor mDataAccessor;
    private String mCatId;               //分类id,如果分类为空，则为福豆兑换界面

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCatId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.search_title_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            EditText mSearch = (EditText) view.findViewById(R.id.search);
            mSearch.setInputType(InputType.TYPE_NULL);
            view.findViewById(R.id.search).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    SearchProblemListActivity.goToPage(FateCatListActivity.this, mCatId);
                }
            });

            if (TextUtils.isEmpty(mCatId)) {
                view.findViewById(R.id.search_lay).setVisibility(View.GONE);
                view.findViewById(R.id.title_txt).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.search_lay).setVisibility(View.VISIBLE);
                view.findViewById(R.id.title_txt).setVisibility(View.GONE);
            }

            titleView.addView(view, params);
        }
    }

    @Override
    protected void addFooterView(KDSPRecyclerView refreshableView, View view) {

        TextView textView = new TextView(this);
        textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtils.dip2px(80)));
        textView.setGravity(Gravity.CENTER);
        textView.setText("没有更多了");
        textView.setTextColor(ContextCompat.getColor(this, R.color.gray));
        view  = new ListViewHead(this,textView);
        super.addFooterView(refreshableView,view);
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new FateCatDataAccessor(mCatId);
        }
        if (mAdapter == null) {
            mAdapter = new FateGridAdapter(this, mDataAccessor.mData, 4);
        }
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
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

    public static void goToPage(Context context, String catId) {
        Intent intent = new Intent(context, FateCatListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, catId);
        context.startActivity(intent);
    }
}
