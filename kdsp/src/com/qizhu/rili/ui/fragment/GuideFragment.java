package com.qizhu.rili.ui.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.LoginActivity;
import com.qizhu.rili.ui.activity.MainActivity;
import com.qizhu.rili.ui.activity.SetInfoActivity;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.SPUtils;

public class GuideFragment extends BaseFragment {
    private int position;       //处于引导页的第几页

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getExtraBundleData();
        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.guide_item, container, false);
    }

    public static GuideFragment newInstance(int position) {
        GuideFragment fragment = new GuideFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(IntentExtraConfig.EXTRA_POSITION, position);
        fragment.setArguments(arguments);
        return fragment;
    }

    //获取bundle的值
    private void getExtraBundleData() {
        position = MethodCompat.getIntFromBundle(getArguments(), IntentExtraConfig.EXTRA_POSITION, 0);
    }

    //根据position初始化界面
    private void initView() {
        ImageView imageView = (ImageView) mMainLay.findViewById(R.id.image);
        TextView textView = (TextView) mMainLay.findViewById(R.id.start);
        switch (position) {
            case 0:
                MethodCompat.setBackground(imageView, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(mActivity, R.drawable.slogan1)));
                break;
            case 1:
                MethodCompat.setBackground(imageView, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(mActivity, R.drawable.slogan2)));
                break;
            case 2:
                MethodCompat.setBackground(imageView, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(mActivity, R.drawable.slogan3)));
                imageView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        //启动主页的时候必须调用init接口
                        if (AppContext.isInVaildUser()) {
                            LoginActivity.goToPage(mActivity, true);
                        } else if (!SPUtils.getBoolleanValue(YSRLConstants.HAS_ENTER_INFO)) {
                            SetInfoActivity.goToPage(mActivity);
                        } else {
                            mActivity.setInitFlag(true);
                            MainActivity.goToPage(mActivity);
                        }

                        mActivity.finish();
                    }
                });
                break;
        }
    }
}
