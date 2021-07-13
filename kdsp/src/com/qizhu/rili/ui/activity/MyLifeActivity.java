package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.CalendarUtil;
import com.qizhu.rili.utils.DateUtils;

/**
 * Created by zhouyue on 5/26/17.
 * 我的命格
 */
public class MyLifeActivity extends BaseActivity {
    private LinearLayout mContentLLayout;
    private DateTime     mBirDateTime;                      //出生时间
    private boolean      isMyLife;                          //是否命格

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_life_activity);
        isMyLife = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_MODE, true);
        if (AppContext.mUser != null) {
            mBirDateTime = new DateTime(AppContext.mUser.birthTime);
        } else {
            mBirDateTime = new DateTime();
        }
        initUI();
    }

    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        if (isMyLife) {
            mTitle.setText(R.string.my_life);
        } else {
            mTitle.setText(R.string.my_wuxing);
        }

        mContentLLayout = (LinearLayout) findViewById(R.id.content_llayout);

        if (isMyLife) {

            String character = CalendarCore.getCharacterDesc(mBirDateTime);
            String love = CalendarCore.getLoveDesc(mBirDateTime);
            String health = CalendarCore.getWeakDesc(mBirDateTime);
            String congenitalMoney = CalendarCore.getCongenitalMoney(mBirDateTime);
            String moneyView = CalendarCore.getMoneyView(mBirDateTime);
            mContentLLayout.addView(getItemView("性格", character));
            mContentLLayout.addView(getItemView("爱情观", love));
            mContentLLayout.addView(getItemView("健康", health));
            mContentLLayout.addView(getItemView("先天财运", congenitalMoney));
            mContentLLayout.addView(getItemView("金钱观", moneyView));
        } else

        {
            String mElement = CalendarCore.getElementName(mBirDateTime);
            String mLikeProperty = CalendarUtil.mLikePropertyMap.get(mElement);
            String mDislikeProperty = CalendarUtil.mDislikePropertyMap.get(mElement);
            String mLikeColor = CalendarUtil.mLikeColorMap.get(mElement);
            String mDislikeColor = CalendarUtil.mDislikeColorMap.get(mElement);
            String mFitJob = CalendarUtil.mFitJobMap.get(mElement);
            String mLikeFood = CalendarUtil.mLikeFoodMap.get(mElement);
            String mDislikeFood = CalendarUtil.mDislikeFoodMap.get(mElement);
            mContentLLayout.addView(getItemView("我的五行：", mElement));
            mContentLLayout.addView(getItemView("与我最配的属性：", mLikeProperty));
            mContentLLayout.addView(getItemView("我要远离的属性：", mDislikeProperty));
            mContentLLayout.addView(getItemView("最适合我的颜色：", mLikeColor));
            mContentLLayout.addView(getItemView("不适合我的颜色：", mDislikeColor));
            mContentLLayout.addView(getItemView("最适合我的职业：", mFitJob));
            mContentLLayout.addView(getItemView("最适合我的食物：", mLikeFood));
            mContentLLayout.addView(getItemView("不适合我的食物：", mDislikeFood));
        }


        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.share_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                int type;
                String shareTitle;
                String shareContent;
                String picUrl;
                String path;
                if (isMyLife) {
                    type = 1;
                    shareTitle = "命格全解析，就在口袋神婆";
                    shareContent = "性格财运桃花运，你的一切我们都知道";
                    picUrl = "http://pt.qi-zhu.com/@/2017/06/05/28cbd1fb-64d9-4fcf-9d43-05359ebab66b.jpg";
                    path = "pages/pocket/calculate/mingge/mingge?birthTime=" + DateUtils.getWebTimeFormatDate(AppContext.mUser.birthTime);

                } else {
                    type = 2;
                    shareTitle = "用五行分析你的宜和忌";
                    shareContent = "想知道最适合你的职业？想知道你的幸运色？五行大师坐镇分析，把握你人生中的相生相克。";
                    picUrl = "http://pt.qi-zhu.com/@/2017/06/05/a25adccf-ab38-4509-953b-ecc010fbfead.jpg";
                    path = "pages/pocket/calculate/five/five?birthTime=" + DateUtils.getWebTimeFormatDate(AppContext.mUser.birthTime);
                }
                ShareActivity.goToMiniShare(MyLifeActivity.this, shareTitle, shareContent,
                        "http://h5.ishenpo.com/app/share/fate_and_five?type=" + type + "&birthday=" + DateUtils.getWebTimeFormatDate(AppContext.mUser.birthTime), picUrl, 0, "", path);
            }
        });


    }

    private View getItemView(String title, String content) {
        View view = mInflater.inflate(R.layout.my_life_item_lay, null);

        TextView titleTv = (TextView) view.findViewById(R.id.title_tv);
        TextView contentTv = (TextView) view.findViewById(R.id.content_tv);
        titleTv.setText(title);

        if (isMyLife) {
            titleTv.setVisibility(View.VISIBLE);
            contentTv.setText(content);
        } else {
            titleTv.setVisibility(View.GONE);
            contentTv.setText(getTextSpan(title, content));
        }
        return view;
    }

    private SpannableStringBuilder getTextSpan(String title, String content) {
        SpannableStringBuilder mLifeReportText = new SpannableStringBuilder();
        mLifeReportText.append(title).append(content);
        mLifeReportText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mLifeReportText.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return mLifeReportText;
    }


    public static void goToPage(Context context, boolean isMyLife) {
        Intent intent = new Intent(context, MyLifeActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, isMyLife);
        context.startActivity(intent);
    }
}
