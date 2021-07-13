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
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.adapter.WeChatCouponsAdapter;
import com.qizhu.rili.data.WeChatCouponsDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.ResultShowDialogFragment;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

/**
 * Created by zhouyue on 19/04/2018.
 * 微信优惠券码
 */

public class WeChatCouponsActivity extends BaseListActivity {
    private WeChatCouponsDataAccessor mDataAccessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            mTitle.setText(getString(R.string.un_use));

            titleView.addView(view, params);
        }
    }


    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }


    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new WeChatCouponsDataAccessor();
        }

        if (mAdapter == null) {

            mAdapter = new WeChatCouponsAdapter(this, mDataAccessor.mData);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {

        view = new ListViewHead(this, R.layout.wechat_coupons_head_text);
        view.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(ResultShowDialogFragment.newInstance(getString(R.string.rule_content)),"");
            }
        });
        super.addHeadView(refreshableView, view);

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


    public static void goToPage(Context context) {
        Intent intent = new Intent(context, WeChatCouponsActivity.class);
        context.startActivity(intent);
    }




}
