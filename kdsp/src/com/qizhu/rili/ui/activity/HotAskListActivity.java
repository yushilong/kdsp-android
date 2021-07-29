package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.FateGridAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.data.HotAskDataAccessor;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

/**
 * Created by lindow on 06/03/2017.
 * 热门问题
 */

public class HotAskListActivity extends BaseListActivity {
    private String mThemeName;                  //主题名
    private String mItemId;
    private String mItemName;


    private HotAskDataAccessor mDataAccessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mThemeName = getIntent().getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
        super.onCreate(savedInstanceState);
        mRootLay.setBackgroundColor(ContextCompat.getColor(this, R.color.gray39));

    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);

            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            if (!TextUtils.isEmpty(mThemeName)) {
                mTitle.setText(mThemeName);
            } else {
                mTitle.setText(R.string.hot_ask);
            }

            titleView.addView(view, params);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {
        view = new ListViewHead(this, R.layout.head_text_lay);
        view.findViewById(R.id.master_ask_head_layout).setVisibility(View.GONE);
        TextView textView = (TextView) view.findViewById(R.id.head_text);
        textView.setText("提问频率最高的感情财运健康问题");

        super.addHeadView(refreshableView, view);
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new HotAskDataAccessor();
        }
        if (mAdapter == null) {
            mAdapter = new FateGridAdapter(this, mDataAccessor.mData, 1);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);

        }
    }

    @Override
    protected void getData() {
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                if (mDataAccessor.mAsk != null) {
                    mItemId = mDataAccessor.mAsk.itemId;
                    mItemName = mDataAccessor.mAsk.itemName;
                }
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
                if (mDataAccessor.mAsk != null) {
                    mItemId = mDataAccessor.mAsk.itemId;
                    mItemName = mDataAccessor.mAsk.itemName;
                }
            }
        }));
    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    public static void goToPage(Context context, String name) {
        Intent intent = new Intent(context, HotAskListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, name);
        context.startActivity(intent);
    }
}
