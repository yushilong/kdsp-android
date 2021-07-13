package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DisplayUtils;

import java.util.ArrayList;

/**
 * Created by zhouyue on 04/05/2017.
 * 前世今生的viewpager
 */

public class LoveLineViewPagerFragment extends BaseViewPagerFragment {
    private String mItemId;                 //问题id
    private ArrayList<String> mList      = new ArrayList<>();
    private ArrayList<View>   mTitleList = new ArrayList<>();
    private DateTime          mYourBirth = new DateTime();            //你的生日
    private DateTime          mHerBirth  = new DateTime();             //她的生日
    private int mMode;                      //模式

    private int currentTab = 0;
    private TextView mPreviousLifeTv;
    private TextView mThisLifeTv;

    public static LoveLineViewPagerFragment newInstance(DateTime yourBirth, DateTime herBirth, int mode) {
        LoveLineViewPagerFragment fragment = new LoveLineViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, yourBirth);
        bundle.putParcelable(IntentExtraConfig.EXTRA_JSON, herBirth);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, mode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(IntentExtraConfig.EXTRA_POSITION, -1);
        }

        if (bundle != null) {
            mItemId = bundle.getString(IntentExtraConfig.EXTRA_ID);
            mYourBirth = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
            mHerBirth = bundle.getParcelable(IntentExtraConfig.EXTRA_JSON);
            mMode = bundle.getInt(IntentExtraConfig.EXTRA_MODE, 1);
        }
        mList.add("前世");
        mList.add("今生");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected View createTitleView() {
        View title = mInflater.inflate(R.layout.title_love_line, null);
        LinearLayout linearLayout = (LinearLayout) title.findViewById(R.id.content_lay);

        int width = (AppContext.getScreenWidth() - DisplayUtils.dip2px(50)) ;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                (int) mResources.getDimension(R.dimen.header_height));

        linearLayout.setLayoutParams(params);

        View view = mInflater.inflate(R.layout.item_title_love_line, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, DisplayUtils.dip2px(50)));
        mPreviousLifeTv = (TextView)view.findViewById(R.id.previous_life_tv);
        mThisLifeTv = (TextView)view.findViewById(R.id.this_life_tv);

        mPreviousLifeTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                setCurrentFragment(0, false);
                changeTitle(0);
            }
        });
        mThisLifeTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                setCurrentFragment(1, false);
                changeTitle(1);
            }
        });
        linearLayout.addView(view);

        title.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mActivity.goBack();
            }
        });

        return title;
    }

    @Override
    protected void afterInitView() {
        if (currentTab != -1) {
            mViewPager.setCurrentItem(currentTab);
            changeTitle(currentTab);
        }
    }

    @Override
    protected int onViewPagerGetCount() {
        return mList.size();
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn = null;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
                rtn = LoveLineFragment.newInstance(position,mYourBirth,mHerBirth,mMode);

        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        changeTitle(position);
    }

    private void changeTitle(int position) {
        if(position == 0){
            mPreviousLifeTv.setBackgroundResource(R.drawable.round_brown_left);
            mPreviousLifeTv.setTextColor(mActivity.getResources().getColor(R.color.white));
            mThisLifeTv.setBackgroundResource(R.drawable.round_white_right);
            mThisLifeTv.setTextColor(mActivity.getResources().getColor(R.color.black));
        }else if(position == 1){
            mPreviousLifeTv.setBackgroundResource(R.drawable.round_white_left);
            mPreviousLifeTv.setTextColor(mActivity.getResources().getColor(R.color.black));
            mThisLifeTv.setBackgroundResource(R.drawable.round_brown_right);
            mThisLifeTv.setTextColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }
}
