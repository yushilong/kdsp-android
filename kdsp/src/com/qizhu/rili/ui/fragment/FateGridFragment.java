package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.adapter.FateRecyclerAdapter;
import com.qizhu.rili.bean.Cat;
import com.qizhu.rili.data.FateCatDataAccessor;

/**
 * Created by lindow on 21/02/2017.
 * 问题列表
 */

public class FateGridFragment extends BaseListFragment {
    private Cat mItem;
    private FateCatDataAccessor mDataAccessor;

    public static FateGridFragment newInstance(Cat item) {
        FateGridFragment fragment = new FateGridFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mItem = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new FateCatDataAccessor(mItem.catId);
        }
        if (mAdapter == null) {
            mAdapter = new FateRecyclerAdapter(mActivity, mDataAccessor.mData, 1);
        }
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {

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
}
