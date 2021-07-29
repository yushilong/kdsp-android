package com.qizhu.rili.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.FateGridAdapter;
import com.qizhu.rili.bean.AuguryItem;
import com.qizhu.rili.data.AuguryListDataAccessor;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;

/**
 * Created by lindow on 8/17/16.
 * 占卜列表页
 */
public class AuguryListActivity extends BaseListActivity {
    private AuguryListDataAccessor mDataAccessor;
    private boolean mHasReply;                  //是否已回复

    //刷新删除项的广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_REFRESH.equals(action)) {
                String ioid = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
                AuguryItem auguryItem = new AuguryItem();
                auguryItem.ioId = ioid;
                if (mAdapter instanceof FateGridAdapter) {
                    ((FateGridAdapter) mAdapter).refreshDeleteItem(auguryItem);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mHasReply = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);
        super.onCreate(savedInstanceState);
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_REFRESH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
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

            if (mHasReply) {
                mTitle.setText(R.string.has_reply);
            } else {
                mTitle.setText(R.string.wait_reply);
            }

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initEmptyView(RelativeLayout emptyLay) {
        //数据为空时的无数据提示
        View view = mInflater.inflate(R.layout.empty_order, null);
        //设置无数据背景
        if (view != null) {
            //设置无数据文案
            view.findViewById(R.id.to_inferring).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    MainActivity.goToPage(AuguryListActivity.this, MainActivity.POS_INFERRING);
                }
            });
            mEmptyLay.addView(view);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new AuguryListDataAccessor(mHasReply);
        }
        if (mAdapter == null) {
            mAdapter = new FateGridAdapter(this, mDataAccessor.mData, mHasReply ? 3 : 2);
        }
    }

    @Override
    protected void getData() {

        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                if (isEmpty) {
                    refreshViewByType(LAY_TYPE_EMPTY);
                } else {
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            }
        }));
    }

    @Override
    protected void getNextData() {
        mDataAccessor.getNextData(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    public void pullDownToRefresh() {
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor, false, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                if (isEmpty) {
                    refreshViewByType(LAY_TYPE_EMPTY);
                } else {
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            }
        }));
    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    public static void goToPage(Context context, boolean mHasReply) {
        Intent intent = new Intent(context, AuguryListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mHasReply);
        context.startActivity(intent);
    }
}
