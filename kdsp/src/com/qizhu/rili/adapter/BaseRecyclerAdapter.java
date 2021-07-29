package com.qizhu.rili.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by lindow on 11/05/2017.
 */

public class BaseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected List<?> mList;  //列表数据
    protected Context mContext;
    protected Resources mResources;
    protected LayoutInflater mInflater;
    protected boolean mLast;                //是否是第二批名字

    public BaseRecyclerAdapter(Context context, List<?> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mResources = context.getResources();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public boolean isDataEmpty() {
        return mList == null || mList.isEmpty();
    }
    public void reset(List list) {
        mList = list;
    }

    public void reset(List list, boolean last) {
        mList = list;
        mLast = last;
    }
}
