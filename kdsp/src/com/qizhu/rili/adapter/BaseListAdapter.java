package com.qizhu.rili.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 适配器基类
 */
public abstract class BaseListAdapter extends BaseAdapter {
    protected List<?> mList;  //列表数据
    protected Context mContext;
    protected Resources mResources;
    protected LayoutInflater mInflater;
    protected boolean mLast;                //是否是第二批名字

    private int mItemId; //列表项的布局资源id

    public BaseListAdapter(Context context, List<?> list) {
        mContext = context;
        this.mList = list;
        this.mItemId = getItemResId();
        this.mInflater = LayoutInflater.from(context);
        this.mResources = context.getResources();
    }

    /**
     * 获取列表项布局
     * 若为0则必须重写getCustomedViewCount()保证得到列表的长度
     */
    protected abstract int getItemResId();

    @Override
    public boolean isEmpty() {
        return isDataEmpty();
    }

    @Override
    public int getCount() {
        return getActualLen();
    }

    @Override
    public Object getItem(int position) {
        if (mList != null) {
            if (position < 0) {
                return null;
            }

            if (position < mList.size()) {
                return mList.get(position);
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 判断是否为最后一行数据
     */
    protected boolean isLastPos(int position) {
        if (mList != null) {
            if (position >= 0 && position == mList.size() - 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //如果为动态生成布局
        if (getItemResId() == 0) {
            return buildView(position, convertView, parent);
        }
        if (convertView == null || convertView.getTag() == null) {
            //xml布局
            convertView = mInflater.inflate(mItemId, null);
            initItemView(convertView, position);
        }

        setItemView(convertView.getTag(), getItem(position), position);
        return convertView;
    }

    /**
     * 获取真实的列表长度
     */
    public int getActualLen() {
        int len = 0;
        if (mList != null) {
            len += (getItemResId() == 0 ? getCustomedViewCount() : mList.size());
        }
        return len;
    }

    public List<?> getListData() {
        return mList;
    }

    public boolean isDataEmpty() {
        return mList == null || mList.isEmpty();
    }

    /**
     * 根据相应位置生成布局(需要与getResId配套使用)
     */
    public View buildView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    /**
     * 获取自定义动态生成布局项的个数(需要与getItemResId配套使用)
     */
    public int getCustomedViewCount() {
        return 0;
    }

    /**
     * 初始化列表项布局
     */
    protected abstract void initItemView(View convertView, int position);

    /**
     * 根据指定的数据展示列表项
     */
    protected abstract void setItemView(Object tag, Object itemData, int position);

    public void clear() {
        mList.clear();
    }

    public void reset(List list) {
        mList = list;
    }
    public void reset(List list, boolean last) {
        mList = list;
        mLast = last;
    }
}
