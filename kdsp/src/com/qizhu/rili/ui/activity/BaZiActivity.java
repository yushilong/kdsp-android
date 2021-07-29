package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.BaziRecyclerAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.bean.Bazi;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.widget.CirclePercentView;
import com.qizhu.rili.widget.CircleView;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 11/29/17.
 * 八字论命的activity
 */
public class BaZiActivity extends BaseListActivity {
    private ArrayList<Bazi> mBazis = new ArrayList<>();
    private ArrayList<String> mColors = new ArrayList<>();
    private TextView mNameTv;
    private TextView mSolarTv;
    private TextView mLunarTv;
    private TextView mTotalWordsTv;
    private String birthday = new DateTime().toServerMinString();
    private CirclePercentView mCirclePercentView;
    private LinearLayout mCircleLlayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            mTitle.setText(R.string.bazi_life);
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });

            titleView.addView(view, params);
        }
    }




    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {

        view = new ListViewHead(this, R.layout.head_bazi);
        mNameTv = (TextView) view.findViewById(R.id.name_tv);
        mSolarTv = (TextView) view.findViewById(R.id.solar_tv);
        mLunarTv = (TextView) view.findViewById(R.id.lunar_tv);
        mTotalWordsTv = (TextView) view.findViewById(R.id.total_words_tv);
         mCirclePercentView = (CirclePercentView) view.findViewById(R.id.circle_percent_view);
         mCircleLlayout = (LinearLayout) view.findViewById(R.id.circle_llayout);

        StringBuffer nameBuffer = new StringBuffer();

        if (AppContext.mUser != null) {
            birthday = new DateTime(AppContext.mUser.birthTime).toServerMinString();
            nameBuffer.append(AppContext.mUser.nickName);
            nameBuffer.append(",  ");

            if (AppContext.mUser.userSex == User.GIRL) {
                nameBuffer.append(getString(R.string.girl));
            } else {
                nameBuffer.append(getString(R.string.boy));
            }
            mNameTv.setText(nameBuffer);
            mSolarTv.setText(getString(R.string.time_1) + new DateTime(AppContext.mUser.birthTime).toCHHourString());
            mLunarTv.setText(getString(R.string.time_2) + " " + CalendarCore.getYearSBName(new DateTime(AppContext.mUser.birthTime)) + "年" + ChinaDateUtil.solarToStringLunar(new DateTime(AppContext.mUser.birthTime)));
        }

        super.addHeadView(refreshableView, view);
    }




    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    @Override
    protected void getNextData() {

    }

    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new BaziRecyclerAdapter(this, mBazis);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        }
    }

    @Override
    protected boolean canPullDownRefresh() {
        return false;
    }

    @Override
    protected void getData() {

        KDSPApiController.getInstance().getEightFontLifeData(birthday, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                JSONObject eightFontLife = response.optJSONObject("eightFontLife");
                mBazis = Bazi.parseListFromJSON(eightFontLife.optJSONArray("eightFontAttrs"));
                final String dayMainAttr = eightFontLife.optString("dayMainAttr");
                final String pattern = eightFontLife.optString("pattern");
                final String totalWords = eightFontLife.optString("totalWords");
                final ArrayList<Float> mNum = new ArrayList<>();
                for (Bazi bazi : mBazis) {
                    mColors.add(getColor(bazi.name));
                    mNum.add(bazi.percent);
                }

                BaZiActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
//                        StringBuffer wordBuffer = new StringBuffer();
//                        wordBuffer.append(dayMainAttr);
//                        wordBuffer.append(",  ");
//                        wordBuffer.append(pattern);
//                        wordBuffer.append(",  ");
//                        wordBuffer.append(totalWords);

                        mTotalWordsTv.setText(getString(R.string.total_words) + totalWords);


                        mCirclePercentView.setData(mNum,mColors);
                        for (Bazi bazi : mBazis) {
                            View view  = mInflater.inflate(R.layout.head_item_bazi,null);
                            CircleView circleView = (CircleView)view.findViewById(R.id.circle_view);
                            TextView colorTv = (TextView)view.findViewById(R.id.color_tv);
                            colorTv.setText(bazi.percent + "%");
                            circleView.setData(getColor(bazi.name));
                            mCircleLlayout.addView(view);
                        }

                        refreshViewByType(LAY_TYPE_NORMAL);
                        mAdapter.reset(mBazis);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }



    private  String getColor(String name){
        String color =  "#aa85b5";
        switch (name) {
            case "劫财":
                color =  "#aa85b5";
                break;
            case "食神":
                color =  "#93aad6";
                break;
            case "伤官":
                color =  "#fff8b3";
                break;
            case "正官":
                color =  "#f5a79a";
                break;
            case "正财":
                color =  "#f9cd9c";
                break;
            case "偏财":
                color =  "#c8b6d5";
                break;
            case "正印":
                color =  "#f6b16e";
                break;
            case "偏印":
                color =  "#bfcae6";
                break;
            case "比肩":
                color =  "#c57bac";
                break;
            case "偏官":
                color =  "#fcdbd6";
                break;
            default:
                break;
        }
        return color;
    }
    /**
     * 跳转至命格页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, BaZiActivity.class);
        context.startActivity(intent);
    }


}
