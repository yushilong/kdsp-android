package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Cat;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.AnimalsSubmitActivity;
import com.qizhu.rili.ui.activity.AugurySubmitActivity;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by lindow on 21/02/2017.
 * 问题的viewpager
 */

public class FateViewPagerFragment extends BaseViewPagerFragment {
    private String mItemId;
    private String mItemName;

    private TextView mItemNameTxt;
    private TextView mAsk;

    private ArrayList<View> mTitleList = new ArrayList<>();
    private ArrayList<Cat> mList = new ArrayList<>();
    private int currentTab = 0;
    private String mMarriageItemId;
    private String mAnimalItemId;
    private int mIndex = -1;



    public static FateViewPagerFragment newInstance(ArrayList<Cat> mCats, String itemId, String itemName,int index) {
        FateViewPagerFragment fragment = new FateViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(IntentExtraConfig.EXTRA_PARCEL, mCats);
        bundle.putString(IntentExtraConfig.EXTRA_ID, itemId);
        bundle.putString(IntentExtraConfig.EXTRA_JSON, itemName);
        bundle.putInt(IntentExtraConfig.EXTRA_TYPE, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mList = bundle.getParcelableArrayList(IntentExtraConfig.EXTRA_PARCEL);
            mItemId = bundle.getString(IntentExtraConfig.EXTRA_ID);
            mItemName = bundle.getString(IntentExtraConfig.EXTRA_JSON);
            mIndex = bundle.getInt(IntentExtraConfig.EXTRA_TYPE,-1);
        }
        LogUtils.d("-----" +mIndex);

        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(IntentExtraConfig.EXTRA_POSITION, -1);
        }



        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getData() {
        KDSPApiController.getInstance().findSpeItem(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                JSONArray jsonArray =  response.optJSONArray("items");
                for (int i = 0 ,length = jsonArray.length(); i < length; i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                 int type =  jsonObject.optInt("type");
                    if(type == 6){
                        mMarriageItemId = jsonObject.optString("itemId");
                    }else if(type == 7){
                        mAnimalItemId = jsonObject.optString("itemId");
                    }
                  }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }

    @Override
    protected View createScrollEnterView() {
        View head = mInflater.inflate(R.layout.master_ask_head, null);
        head.findViewById(R.id.marriage_llay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AugurySubmitActivity.goToPage(mActivity,  mMarriageItemId, 5,  3,0);
            }
        });

        head.findViewById(R.id.animals_llay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                AnimalsSubmitActivity.goToPage(mActivity);
            }
        });

        return head;
    }

    @Override
    protected View createScrollFixedView() {
        LinearLayout linearLayout = new LinearLayout(mActivity);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtils.dip2px(50)));
        linearLayout.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.white));
        int size = mList.size();
        int width;
        if (size > 0 && size <= 5) {
            width = AppContext.getScreenWidth() / size;
        } else {
            width = DisplayUtils.dip2px(80);
        }
        for (int i = 0; i < size; i++) {
            View view = mInflater.inflate(R.layout.fate_viewpager_title_item, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(width, DisplayUtils.dip2px(50)));
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(mList.get(i).name);
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
        return linearLayout;
    }

    @Override
    protected void afterInitView() {
        if(mIndex != -1){
            mViewPager.setCurrentItem(mIndex);
            changeTitle(mIndex);
        }else {
            if (currentTab != -1) {
                mViewPager.setCurrentItem(currentTab);
                changeTitle(currentTab);
            }
        }


        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                LogUtils.d("---------------> verticalOffset = " + verticalOffset);
                BaseListFragment frag = (BaseListFragment) getCurrentFragment();
                if(frag != null){
                    frag.mCanPullDownRefresh = verticalOffset >= 0;
                }

            }
        });
    }

    @Override
    protected int onViewPagerGetCount() {
        return mList.size();
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            rtn = FateGridFragment.newInstance(mList.get(position));
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

    @Override
    public void onDetach() {
        super.onDetach();
     // 防止Java.lang.IllegalStateException Activity has been destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

