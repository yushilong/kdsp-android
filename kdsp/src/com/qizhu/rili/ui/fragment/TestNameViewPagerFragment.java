package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DisplayUtils;

import java.util.ArrayList;

/**
 * Created by zhouyue on 04/05/2017.
 * 測名的viewpager
 */

public class TestNameViewPagerFragment extends BaseViewPagerFragment {
    private String mItemId;                 //问题id
    private ArrayList<String> mList      = new ArrayList<>();
    private ArrayList<View>   mTitleList = new ArrayList<>();

    private int currentTab = 0;

    public static TestNameViewPagerFragment newInstance(ArrayList<String> mCats, String itemId, int type) {
        TestNameViewPagerFragment fragment = new TestNameViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(IntentExtraConfig.EXTRA_PARCEL, mCats);
        bundle.putString(IntentExtraConfig.EXTRA_ID, itemId);
        bundle.putInt(IntentExtraConfig.EXTRA_TYPE, type);
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
            mList = bundle.getStringArrayList(IntentExtraConfig.EXTRA_PARCEL);
            currentTab = bundle.getInt(IntentExtraConfig.EXTRA_TYPE);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected View createTitleView() {
        View title = mInflater.inflate(R.layout.title_test_name, null);
        LinearLayout linearLayout = (LinearLayout) title.findViewById(R.id.content_lay);
        title.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mActivity.goBack();
            }
        });
        int size = mList.size();
        int width = (AppContext.getScreenWidth() - DisplayUtils.dip2px(50)) / size;
        for (int i = 0; i < size; i++) {
            View view = mInflater.inflate(R.layout.fate_viewpager_title_item, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(width, DisplayUtils.dip2px(50)));
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(mList.get(i));
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCurrentFragment(finalI, false);
                    changeTitle(finalI);
                }
            });
            mTitleList.add(i, view);
            linearLayout.addView(view);
        }
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
            if (position == 0) {
                rtn = TestNameFragment.newInstance(mItemId);
            } else if (position == 1) {
                rtn = AugurySubmitFragment.newInstance("", mItemId, false, "", "", 4, 0, 2);
            }
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
        int k = mTitleList.size();
        for (int i = 0; i < k; i++) {
            if (position == i) {
                ((TextView) mTitleList.get(i).findViewById(R.id.text)).setTextColor(ContextCompat.getColor(mActivity, R.color.purple32));
                mTitleList.get(i).findViewById(R.id.line).setVisibility(View.VISIBLE);
            } else {
                ((TextView) mTitleList.get(i).findViewById(R.id.text)).setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                mTitleList.get(i).findViewById(R.id.line).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }
}
