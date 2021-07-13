package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.ZoomableImagePagerAdapter;
import com.qizhu.rili.widget.LoopViewPager;

import java.util.ArrayList;

/**
 * Created by lindow on 07/03/2017.
 * 图片横滑预览缩放
 */

public class ZoomableImageViewpagerActivity extends BaseActivity {
    private LoopViewPager mImagePager;
    private ZoomableImagePagerAdapter mImagePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomable_lay);
        initView();
    }

    private void initView() {
        mImagePager = (LoopViewPager) findViewById(R.id.image_pager);
        ArrayList<String> mUrls = getIntent().getStringArrayListExtra(IntentExtraConfig.EXTRA_PARCEL);
        mImagePagerAdapter = new ZoomableImagePagerAdapter(ZoomableImageViewpagerActivity.this, mUrls);
        mImagePager.setAdapter(mImagePagerAdapter);
    }

    public static void goToPage(Context context, ArrayList<String> list) {
        Intent intent = new Intent(context, ZoomableImageViewpagerActivity.class);
        intent.putStringArrayListExtra(IntentExtraConfig.EXTRA_PARCEL, list);
        context.startActivity(intent);
    }
}
