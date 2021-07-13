package com.qizhu.rili.ui.activity;

import android.os.Bundle;

import com.qizhu.rili.utils.YSRLNavigationByUrlUtils;


/**
 * Created by lindow on 15-7-23 00:36.
 * 浏览器activity，用于监听浏览器的链接，实现我们自己的超链接处理
 */
public class BrowserActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YSRLNavigationByUrlUtils.navigate(String.valueOf(getIntent().getData()), this, true, false);
        finish();
    }
}
