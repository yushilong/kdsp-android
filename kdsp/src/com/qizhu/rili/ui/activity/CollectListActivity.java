package com.qizhu.rili.ui.activity;

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

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.CollectAdapter;
import com.qizhu.rili.data.ArticleDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;


/**
 * Created by zhouyue on 08/29/2017.
 * 收藏列表页
 */

public class CollectListActivity extends BaseListActivity {

    private ArticleDataAccessor mDataAccessor;
    private int mTypeId;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_COLLECT_REFRESH.equals(action)) {
                getData();
                initAdapter();
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getIntentExtra();
        super.onCreate(savedInstanceState);
        if(mTypeId == 4){
            BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_COLLECT_REFRESH));
        }
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
            if(mTypeId == 0 || mTypeId == 1){
                mTitle.setText(R.string.god_daily);
            }else if (mTypeId == 2){
                mTitle.setText(R.string.god_voice);
            }else if (mTypeId == 4){
                mTitle.setText(R.string.my_collect);
            }


            titleView.addView(view, params);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new ArticleDataAccessor(mTypeId);

        }
        if (mAdapter == null) {
            mAdapter = new CollectAdapter(CollectListActivity.this, mDataAccessor.mData,mTypeId);
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
    private void getIntentExtra() {
        Intent intent = getIntent();
        mTypeId = intent.getIntExtra(IntentExtraConfig.EXTRA_ID,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, CollectListActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context,  int type) {
        Intent intent = new Intent(context, CollectListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, type);
        context.startActivity(intent);
    }
}
