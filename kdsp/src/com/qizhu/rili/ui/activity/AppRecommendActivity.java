package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.adapter.AppRecommendListAdapter;
import com.qizhu.rili.data.AppRecommendDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;

public class AppRecommendActivity extends BaseListActivity {
    private AppRecommendDataAccessor mDataAccessor;

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.setBackgroundResource(R.color.purple7);
            TextView title = (TextView) view.findViewById(R.id.title_txt);
            title.setText("应用推荐");
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }

            });
            titleView.addView(view, params);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new AppRecommendDataAccessor(this);
        }
        if (mAdapter == null) {
            mAdapter = new AppRecommendListAdapter(this, mDataAccessor.mData);
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
        Intent intent = new Intent();
        intent.setClass(context, AppRecommendActivity.class);
        context.startActivity(intent);
    }
}
