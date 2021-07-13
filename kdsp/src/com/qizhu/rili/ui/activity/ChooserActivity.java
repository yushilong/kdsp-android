package com.qizhu.rili.ui.activity;

import android.view.MotionEvent;
import android.view.View;

/**
 * 选择框的基本类，增加在框外单击关闭的功能
 */
public class ChooserActivity extends BaseActivity{

    /**
     *在选择框外面单击时，关闭该窗口
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void cancel(View v){
        finish();
    }
}
