package com.qizhu.rili.widget;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by lindow on 10/21/15.
 * 自定义的RecyclerView
 */
public class KDSPRecyclerView extends RecyclerView {

    public KDSPRecyclerView(Context context) {
        super(context);
    }

    public KDSPRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KDSPRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param orientation   方向
     * @param reverseLayout 倒序布局
     * @return 初始化一个纵向或者横向的列表布局
     */
    public void instanceForListView(int orientation, boolean reverseLayout) {
        setLayoutManager(new LinearLayoutManager(getContext(), orientation, reverseLayout));
    }

    /**
     * @param spanCount     列数
     * @param orientation   方向
     * @param reverseLayout 倒序布局
     * @return 初始化一个纵向或者横向的网格布局
     */
    public void instanceForGridView(int spanCount, int orientation, boolean reverseLayout) {
        setLayoutManager(new GridLayoutManager(getContext(), spanCount, orientation, reverseLayout));
    }


    /**
     * @param spanCount   列数
     * @param orientation 方向
     * @return 初始化一个纵向或者横向的瀑布流
     */
    public void instanceForWaterFallView(int spanCount, int orientation) {
        setLayoutManager(new StaggeredGridLayoutManager(spanCount, orientation));
    }
}
