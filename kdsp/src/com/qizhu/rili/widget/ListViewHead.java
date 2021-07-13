package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @创建者 zhouyue
 * @创建时间 2017/5/23 11:46
 * @描述  问答的头部
 */
public class ListViewHead extends RelativeLayout{
    private int mLayoutId =-1;
    public ListViewHead(Context context, int id) {
        super(context);
        mLayoutId = id;
        init(context);
    }

    public ListViewHead(Context context, View view) {
        super(context);
        init(context);
        addView(view);
    }




    public ListViewHead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public void init(Context context) {
        if(mLayoutId != -1 ){
            inflate(context, mLayoutId, this);
        }

    }
}
