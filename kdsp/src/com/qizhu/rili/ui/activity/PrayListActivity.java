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
import com.qizhu.rili.adapter.PrayListAdapter;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.ShakingSign;
import com.qizhu.rili.data.PrayListDataAccessor;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;

/**
 * Created by lindow on 3/30/16.
 * 求签记录列表
 */
public class PrayListActivity extends BaseListActivity {
    private PrayListDataAccessor mDataAccessor;

    //签文状态更改的广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String shakId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
            int like = intent.getIntExtra(IntentExtraConfig.EXTRA_MODE, 1);
            if (BroadcastUtils.ACTION_SIGN_LIKE_CHANGED.equals(action)) {
                //根据当前签文刷新列表中的签文
                if (mDataAccessor != null) {
                    for (ShakingSign shakingSign : mDataAccessor.mData) {
                        if (shakingSign.shakId.equals(shakId)) {
                            shakingSign.isLike = like;
                        }
                    }
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_SIGN_LIKE_CHANGED));
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
            mTitle.setText(new DateTime().toYearString());

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initEmptyView(RelativeLayout emptyLay) {
        //数据为空时的无数据提示
        View view = mInflater.inflate(R.layout.tip_no_data, null);
        //设置无数据背景
        if (view != null) {
            //设置无数据文案
            ((TextView) view.findViewById(R.id.no_data_text)).setText(R.string.empty_wish);
            mEmptyLay.addView(view);
        }
    }

//    @Override
//    protected void addFooterView(KDSPRecyclerView refreshableView) {
//        TextView textView = new TextView(this);
//        textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtils.dip2px(160)));
//        textView.setGravity(Gravity.CENTER);
//        textView.setText("签文只保留三天哦!");
//        textView.setTextColor(ContextCompat.getColor(this, R.color.purple));
//        refreshableView.addFooterView(textView);
//    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new PrayListDataAccessor();
        }
        if (mAdapter == null) {
            mAdapter = new PrayListAdapter(this, mDataAccessor.mData);
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

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, PrayListActivity.class);
        context.startActivity(intent);
    }
}
