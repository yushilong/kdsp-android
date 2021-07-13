package com.qizhu.rili.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.AdverInfo;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义pageview
 *
 * @author zhouyue
 */
public class MyPageView {

    // 自动轮播的时间间隔
    private final static int TIME_INTERVAL = 5;

    // 放轮播图片的ImageView 的list
    // 放圆点的View的list
    private List<View> dotViewsList;
    private ViewPager  viewPager;
    private static List<AdverInfo> activityDatas = new ArrayList<AdverInfo>();// 活动列表，用于图片轮播
    // 当前轮播页
    private        int             currentItem   = 0;
    private View             view;
    private FragmentActivity fragmentActivity;

    boolean isUp = true;
    private static AdClickListen mAdClickListen;

    public interface AdClickListen {
        void  click(String id,String linkUrl);
    }

    // Handler用于控制没五秒轮播一次
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (!isUp) {
                    return;
                }
                currentItem = viewPager.getCurrentItem();
                if (currentItem == Integer.MAX_VALUE) {
                    currentItem = -1;
                }
                currentItem++;
                viewPager.setCurrentItem(currentItem);
                currentItem = currentItem % dotViewsList.size();
                int size = dotViewsList.size();
                for (int i = 0; i < size; i++) {
                    if (i == currentItem) {
                        ((View) dotViewsList.get(i))
                                .setBackgroundResource(R.drawable.circle_purple16);
                    } else {
                        ((View) dotViewsList.get(i))
                                .setBackgroundResource(R.drawable.circle_white);
                    }
                }
                handler.sendEmptyMessageDelayed(1, TIME_INTERVAL * 1000);

            }
        }
    };

    @SuppressLint("ValidFragment")
    public MyPageView(View view, FragmentActivity fragmentActivity,
                      List<AdverInfo> data, AdClickListen adClickListen) {
        this.view = view;
        this.fragmentActivity = fragmentActivity;
        this.mAdClickListen = adClickListen;
        if (data != null) {
            this.activityDatas = data;
        }
        initData();

        initUI(fragmentActivity);
    }

    /**
     * 初始化相关Data
     */
    private void initData() {

        dotViewsList = new ArrayList<View>();

    }

    /**
     * 初始化Views等UI
     */
    @SuppressLint("ResourceAsColor")
    private void initUI(Context context) {
        int size = activityDatas.size();

        // 设置圆点的大小，可根据需要设置圆点大小与间距
        int y = 20;
        if (size > 1) {

            for (int i = 0; i < size; i++) {
                View dotView = new View(context);
                LinearLayout dotarea = (LinearLayout) view
                        .findViewById(R.id.ll_viewarea_dot);
                if (size > 0) {
                    dotarea.setGravity(Gravity.CENTER_HORIZONTAL);
                }

                if (i > 0) {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            y, y);
                    params.setMargins(15, 0, 0, 0);
                    dotView.setLayoutParams(params);
                    dotView.setBackgroundResource(R.drawable.circle_white);
                } else {
                    dotView.setLayoutParams(new LayoutParams(y, y));
                    dotView.setBackgroundResource(R.drawable.circle_purple16);
                }
                dotarea.addView(dotView);
                dotViewsList.add(dotView);
            }
        }
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        // 设置滚动效果
        viewPager.setFocusable(true);
        ViewPagerScroller vs = new ViewPagerScroller(context);
        vs.initViewPagerScroll(viewPager);

        viewPager.setAdapter(new MyFragmentPagerAdapter1(fragmentActivity
                .getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        viewPager.setCurrentItem(size * 100);

        if (size > 1) {
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    private final class MyFragmentPagerAdapter1 extends
            FragmentStatePagerAdapter {

        public MyFragmentPagerAdapter1(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt("arg", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return activityDatas.size() == 1 ? 1 : Integer.MAX_VALUE;
        }
    }

    @SuppressLint("ValidFragment")
    public static class MyFragment extends Fragment {

        @SuppressLint("ResourceAsColor")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = null;
            try {
                view = inflater.inflate(R.layout.pager_item, container, false);
                final FitWidthImageView mImageView = (FitWidthImageView) view
                        .findViewById(R.id.ad_image);
                Bundle args = getArguments();
                final int resId = args.getInt("arg");

                final AdverInfo adverInfo = activityDatas.get(resId % activityDatas.size());

                UIUtils.displayImage(adverInfo.imgUrl, mImageView, AppContext.getScreenWidth(), R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mImageView.setInfoHeight(AppContext.getScreenWidth(), imageInfo);
                        LogUtils.d("---AdverInfo" + AppContext.getScreenWidth() + adverInfo.imgUrl);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdClickListen.click(adverInfo.carouselId,adverInfo.linkUrl);


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }

    /**
     * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
     *
     * @author caizhiming
     */
    private class MyPageChangeListener implements OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {

            // currentItem = pos;
            int size = dotViewsList.size();
            if (size > 0) {
                int curPos = pos % size;
                for (int i = 0; i < size; i++) {
                    if (i == curPos) {
                        ((View) dotViewsList.get(i))
                                .setBackgroundResource(R.drawable.circle_purple16);
                    } else {
                        ((View) dotViewsList.get(i))
                                .setBackgroundResource(R.drawable.circle_white);
                    }
                }
            }

        }
    }


    public void setState(boolean b) {
        isUp = b;
        if (!isUp) {
            handler.removeMessages(1);
        } else {
            handler.sendEmptyMessageDelayed(1, 5000);
        }
    }


}
