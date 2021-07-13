package com.qizhu.rili.listener;

import android.view.MotionEvent;
import android.view.View;

/**
 * 监听双击事件
 */
public abstract class OnDoubleClickListener implements View.OnTouchListener{
    private int count = 0;
    private long firstTime = 0,secondeTime = 0;
    private long interval = 500;//小于1s计算双击
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(MotionEvent.ACTION_DOWN == event.getAction()){
            count++;
            switch (count){
                case 1:
                    firstTime = System.currentTimeMillis();
                    break;
                case 2:
                    secondeTime = System.currentTimeMillis();
                    if(secondeTime - firstTime < interval){
                        onDoubleClick(v,event);
                    }
                    count = 0;
                    break;
                default:
                    count = 0;
            }
            return true;
        }
        return false;
    }

    abstract public boolean onDoubleClick(View v,MotionEvent event);
}
