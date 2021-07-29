package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.GoodsGridAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.GoodsClass;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.data.GoodsDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;
import com.qizhu.rili.widget.TimePickDialogView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by lindow on 01/03/2017.
 * 商品列表页
 */

public class GoodsListActivity extends BaseListActivity {
    private View               mHeadMask;
    private View               mMask;
    private View               mMaskPop;
    private TextView           mTitle;                //标题
    private TextView           mTitleRight;             //标题
    private TextView           mElements;             //五行
    private ImageView          mSelectBirth;         //选择生日
    private LinearLayout       mElementsLLayout;         //五行
    private TimePickDialogView mTimePickDialogView;

    private int mSort = -1;                     //当前分类

    private GoodsDataAccessor mDataAccessor;
    private boolean           mIsTheme;                   //是否主题
    private String            mThemeName;                  //主题名
    private int               mType;                  //主题类型
    private String   mBirthday  = "";
    private DateTime mBirthDate = new DateTime();
    private int mBirthDateMode = 0;
    private boolean isFilter;
    private ArrayList<GoodsClass> mGoodsClasses = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIsTheme = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_MODE, false);
        mThemeName = getIntent().getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
        mType = getIntent().getIntExtra(IntentExtraConfig.EXTRA_ID, -1);
        if (AppContext.mUser != null) {
        mBirthday = DateUtils.getWebTimeFormatDate(new DateTime(AppContext.mUser.birthTime).getDate());
        }

        super.onCreate(savedInstanceState);
        mRootLay.setBackgroundColor(ContextCompat.getColor(this, R.color.gray39));
        LogUtils.d("---"+mBirthday+"---"+new DateTime(AppContext.mUser.birthTime).toString());
        getGoodsClass();
        mRequestBad.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View view) {
                refreshViewByType(LAY_TYPE_LOADING);
                startGetData();
                getGoodsClass();
            }
        });
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.goods_list_head, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (view != null) {
            mTitle = (TextView) view.findViewById(R.id.title_txt);
            mTitleRight = (TextView) view.findViewById(R.id.right_text);
            mHeadMask = view.findViewById(R.id.head_mask);
            mElements = (TextView) view.findViewById(R.id.elements);
            mSelectBirth = (ImageView) view.findViewById(R.id.select_birth);
            mElementsLLayout = (LinearLayout) view.findViewById(R.id.elements_lay);
            mTitleRight.setText("分类");
            if (mIsTheme) {
                if (!TextUtils.isEmpty(mThemeName)) {
                    mTitle.setText(mThemeName);
                    mTitleRight.setVisibility(View.GONE);
                } else {
                    mTitle.setText(R.string.good_luck);
                    mTitleRight.setVisibility(View.GONE);
                }
                mTitleRight.setCompoundDrawables(null, null, null, null);
                mElementsLLayout.setVisibility(View.VISIBLE);
                if (mType == 0) {
                    mElementsLLayout.setVisibility(View.GONE);
                }

            } else {
                mTitleRight.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.good_luck);
                mElementsLLayout.setVisibility(View.GONE);
                mTitleRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initPlusView();
                        if (isFilter) {
                            isFilter = false;
                            mTitleRight.setCompoundDrawables(null, null, getDrawabl(R.drawable.triangle_arrow_black_down), null);
                            findViewById(R.id.pull_update_lay).setVisibility(View.GONE);
                        } else {
                            mTitleRight.setCompoundDrawables(null, null, getDrawabl(R.drawable.triangle_arrow_black_up), null);
                            findViewById(R.id.pull_update_lay).setVisibility(View.VISIBLE);
                            mMaskPop.setVisibility(View.VISIBLE);
                            isFilter = true;

                        }
                    }
                });
            }

            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });

            if (AppContext.mUser != null) {
                if(AppContext.mUser.isLunar == 0){
                    mBirthDateMode = 0 ;
                    mBirthDate = new DateTime(AppContext.mUser.birthTime);
                }else {
                    mBirthDateMode = 1 ;
                    mBirthDate = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime)) ;
                }

                String mElement = CalendarCore.getElementName(new DateTime(AppContext.mUser.birthTime));
                mElements.setText("你的五行：" + mElement);

            }
            view.findViewById(R.id.elements_lay).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    initPlusView();
                    mHeadMask.setVisibility(View.VISIBLE);
                    findViewById(R.id.pull_update_lay).setVisibility(View.VISIBLE);
                    mMask.setVisibility(View.VISIBLE);
                    mSelectBirth.setImageResource(R.drawable.triangle_arrow_gray_up);
                    mTimePickDialogView.setDateTime(mBirthDate,mBirthDateMode);
                    LogUtils.d("------Click:"+ mBirthDateMode+  mBirthDate.toString());
                }
            });

            titleView.addView(view, params);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {
        if (mIsTheme) {
            view = new ListViewHead(this, R.layout.head_text_lay);
            view.findViewById(R.id.master_ask_head_layout).setVisibility(View.GONE);
            TextView textView = (TextView) view.findViewById(R.id.head_text);
            textView.setText("根据每个人自身五行属性量身定制的旺运吊坠，增加你所缺失的五行元素");
            if (mType == 0) {
                textView.setVisibility(View.GONE);
            }
            super.addHeadView(refreshableView, view);
        }
    }

    //增加额外的view
    private void initPlusView() {
        if (mIsTheme) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.custom_lay);
            View view = mInflater.inflate(R.layout.goods_list_mask, null);
            mMask = view.findViewById(R.id.list_mask);
            mTimePickDialogView = (TimePickDialogView) view.findViewById(R.id.time_select);
            linearLayout.removeAllViews();
            linearLayout.addView(view);
            mPullUpdateCountTxt.setVisibility(View.GONE);
            mHeadMask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelPick();
                }
            });
            mMask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelPick();
                }
            });
        } else {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.custom_lay);
            linearLayout.removeAllViews();
            View view = mInflater.inflate(R.layout.goods_list_filter, null);
            LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.content_llayout);
            for (final GoodsClass goodsClass : mGoodsClasses) {
                View goodView = mInflater.inflate(R.layout.goods_list_filter_item, null);
                CheckBox allCheck = (CheckBox) goodView.findViewById(R.id.all_check);
                TextView nameTv = (TextView) goodView.findViewById(R.id.name_tv);
                nameTv.setText(goodsClass.classifyName);
                if (mSort == goodsClass.sort) {
                    allCheck.setChecked(true);
                } else {
                    allCheck.setChecked(false);
                }
                goodView.findViewById(R.id.all_llayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                allCheck.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        mSort = goodsClass.sort;
                        mDataAccessor.setclassifyId(goodsClass.classifyId);
                        pullDownToRefresh();
                        refreshPop();
                    }
                });
                contentLayout.addView(goodView);
            }

            mMaskPop = view.findViewById(R.id.list_mask);
            linearLayout.addView(view);
            mPullUpdateCountTxt.setVisibility(View.GONE);
            mMaskPop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaskPop.setVisibility(View.GONE);
                    refreshPop();
                }
            });


        }

    } //增加额外的view

    private void refreshPop() {
        mTitleRight.setCompoundDrawables(null, null, getDrawabl(R.drawable.triangle_arrow_black_down), null);
        isFilter = false;
        findViewById(R.id.pull_update_lay).setVisibility(View.GONE);
    }

    private Drawable getDrawabl(int id) {
        Drawable drawable = ContextCompat.getDrawable(GoodsListActivity.this, id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        return drawable;
    }

    private void getGoodsClass() {
        KDSPApiController.getInstance().getClassifyList(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                if (response != null) {
                    JSONArray listJson = response.optJSONArray("classifies");
                    mGoodsClasses = GoodsClass.parseListFromJSON(listJson);
                }
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }


    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new GoodsDataAccessor(mBirthday, mIsTheme, mType);
        }
        if (mAdapter == null) {
            mAdapter = new GoodsGridAdapter(this, mDataAccessor.mData);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        }
    }

    @Override
    protected void getData() {
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true));
    }

    @Override
    protected void getNextData() {
        mDataAccessor.getNextData(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    public void pullDownToRefresh() {
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    public void pickDateTime(DateTime dateTime, int mode) {
        mBirthDate = dateTime;
        mBirthDateMode = mode;
        LogUtils.d("------pickDateTime:"+mode+ dateTime.toString());
        DateTime birthdate  ;
        if (0 == mode) {
            birthdate = dateTime;
        } else {
            birthdate = ChinaDateUtil.getSolarByDate(dateTime);
        }

        String mElement = CalendarCore.getElementName(birthdate);
        mElements.setText("你的五行：" + mElement);
        mBirthday = DateUtils.getWebTimeFormatDate(birthdate.getDate());
        mDataAccessor.setBirthday(mBirthday);
        pullDownToRefresh();
    }

    public void cancelPick() {
        mHeadMask.setVisibility(View.GONE);
        mMask.setVisibility(View.GONE);
        mElementsLLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.pull_update_lay).setVisibility(View.GONE);
        mSelectBirth.setImageResource(R.drawable.triangle_arrow_purple1_down);
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, GoodsListActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, boolean isTheme, String name, int type) {
        Intent intent = new Intent(context, GoodsListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, isTheme);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, type);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, name);
        context.startActivity(intent);
    }
}
