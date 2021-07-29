package com.qizhu.rili.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.HomeItem;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class HomeFragment extends BaseFragment {
    private View           mMonthFortuneLay;                      //月运势
    private TextView       mMonthLoveImage;                   //感情
    private TextView       mMonthCareerImage;                 //事业
    private TextView       mMonthMoneyImage;                  //财运
    private ImageView      mMonthLoveLevel;                   //感情
    private ImageView      mMonthCareerLevel;                 //事业
    private ImageView      mMonthMoneyLevel;                  //财运
    private TextView       mMonthLove;                        //感情
    private TextView       mMonthCareer;                      //事业
    private TextView       mMonthMoney;                       //财运
    private TextView       mCurrentDayTv;                     //月份
    private View           mProgressLay;
    private View           mBadLay;
    private ScrollView     mScrollView;
    private PtrFrameLayout mPtrFrameLayout;   //下拉刷新整体
    private boolean        mIsMale;                            //是否男性
    private DateTime       mDateTime;                         //当前时间
    private DateTime       mBirDateTime;                      //出生时间
    private DateTime       mCurrentDate;                      //当前选中日期

    private String mLocationText = "";                  //幸运位
    private String mColorText    = "";                     //幸运色
    private String mEventText    = "";                     //今日总况

    private SpannableStringBuilder mMonthLoveText   = new SpannableStringBuilder();       //感情正文
    private SpannableStringBuilder mMonthCareerText = new SpannableStringBuilder();     //事业正文
    private SpannableStringBuilder mMonthMoneyText  = new SpannableStringBuilder();      //财运正文
    private SpannableStringBuilder mFortuneText     = new SpannableStringBuilder();      //财运正文

    private int          mLoveLevel;                             //感情
    private int          mCareerLevel;                           //事业
    private int          mMoneyLevel;                            //财运
    private LinearLayout mContentLlayout;
    private ArrayList<HomeItem> mHomeItem = new ArrayList<HomeItem>();
    private int      itemHight;
    private boolean  mView1isClose;
    private String[] mMonth;
    private boolean  mIsMonthChange;                        //月份改变从服务器获取，否则读取缓存

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //默认时间设置为当前时间
        mDateTime = new DateTime();
        mCurrentDate = mDateTime;

        //先获取用户信息
        if (AppContext.mUser != null) {
            mBirDateTime = new DateTime(AppContext.mUser.birthTime);
            mIsMale = AppContext.mUser.userSex == User.BOY;
        } else {
            mBirDateTime = new DateTime();
            mIsMale = false;
        }
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        itemHight = dm.heightPixels;
        mMonth = SPUtils.getStringArrayValue("MonthFortune");
        if (mDateTime.month == SPUtils.getIntValue("homeMonth")) {
            mIsMonthChange = false;
        } else {
            mIsMonthChange = true;
        }
        initView();
        getDayEvent();
        getMonthFortune(false);
    }


    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    protected void initView() {
        mMonthFortuneLay = mMainLay.findViewById(R.id.month_fortune);
        mMonthLoveImage = (TextView) mMainLay.findViewById(R.id.month_love_img);
        mMonthCareerImage = (TextView) mMainLay.findViewById(R.id.month_career_img);
        mMonthMoneyImage = (TextView) mMainLay.findViewById(R.id.month_money_img);
        mMonthLoveLevel = (ImageView) mMainLay.findViewById(R.id.month_love_level);
        mMonthCareerLevel = (ImageView) mMainLay.findViewById(R.id.month_career_level);
        mMonthMoneyLevel = (ImageView) mMainLay.findViewById(R.id.month_money_level);
        mMonthLove = (TextView) mMainLay.findViewById(R.id.month_love);
        mContentLlayout = (LinearLayout) mMainLay.findViewById(R.id.content_llayout);
        mMonthCareer = (TextView) mMainLay.findViewById(R.id.month_career);
        mMonthMoney = (TextView) mMainLay.findViewById(R.id.month_money);
        mCurrentDayTv = (TextView) mMainLay.findViewById(R.id.current_day_tv);
        mProgressLay = mMainLay.findViewById(R.id.progress_lay);
        mCurrentDayTv.setText(mDateTime.toCHMonth3String() + "历");
        mBadLay = mMainLay.findViewById(R.id.bad_lay);
        mScrollView = (ScrollView) mMainLay.findViewById(R.id.scroll_view);
        mPtrFrameLayout = (PtrFrameLayout) mMainLay.findViewById(R.id.ptr_frame);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getDayEvent();
                getMonthFortune(false);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //是否可以下拉刷新
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        DateTime mBirth = new DateTime();
        boolean isMale = false;
        if (AppContext.mUser != null) {
            mBirth = new DateTime(AppContext.mUser.birthTime);
            isMale = AppContext.mUser.userSex == User.BOY;
        }
        //用户的生日和性别发生改变,则更新数据和view
        if (!mBirth.toString().equals(mBirDateTime.toString()) || isMale != mIsMale) {
            mBirDateTime = mBirth;
            mIsMale = isMale;
            mBirDateTime = mBirth;
            getDayEvent();
            getMonthFortune(true);

        }
        getDayEvent();
    }

    private View getItemView(final int num) {
        mCurrentDate = new DateTime(num);
        View view = mInflater.inflate(R.layout.home_item, null);
        YSRLDraweeView avatar = (YSRLDraweeView) view.findViewById(R.id.avatar);
        TextView dayTitleTv = (TextView) view.findViewById(R.id.day_title_tv); //今日说
        TextView homeTitleTv = (TextView) view.findViewById(R.id.home_title); //今日宜
        TextView lunarTv = (TextView) view.findViewById(R.id.lunar_tv);      //阴历日期
        TextView dayTv = (TextView) view.findViewById(R.id.day_tv);            //阳历日期
        TextView colorTv = (TextView) view.findViewById(R.id.color_tv);//幸运色
        TextView locationTv = (TextView) view.findViewById(R.id.location_tv);//幸运位
        TextView todayFortuneTv = (TextView) view.findViewById(R.id.today_fortune_tv);//今天是你的（今天运势）
        TextView eventTv = (TextView) view.findViewById(R.id.event_tv);//今天大事件
        TextView fortuneTv = (TextView) view.findViewById(R.id.fortune_tv);//分数
        TextView fortuneDayTv = (TextView) view.findViewById(R.id.fortune_day_tv);//今天运势（锻炼日）
        final LinearLayout eventLLayout = (LinearLayout) view.findViewById(R.id.envent_content_llayout);
        final LinearLayout seeAllLLayout = (LinearLayout) view.findViewById(R.id.see_all_llayout);
        final LinearLayout itemRLayout = (LinearLayout) view.findViewById(R.id.item_rlayout);
        HomeItem item = mHomeItem.get(num);
        seeAllLLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                eventLLayout.setVisibility(View.VISIBLE);
                seeAllLLayout.setVisibility(View.GONE);
                if (num == 0) {
                    mView1isClose = false;
                }
            }
        });

        if (item != null) {


//        mLocationText = CalendarCore.getPosition(mCurrentDate, mBirDateTime);
//        mColorText = CalendarCore.getDayColor(mCurrentDate, mBirDateTime);
//
//        String todayFortun = CalendarCore.getDayGoodOrBad(mCurrentDate, mBirDateTime, mIsMale);
//        int score = CalendarCore.getDayScore(mCurrentDate, mBirDateTime, mIsMale);
//        String dayEvent = CalendarCore.getDayTitle(mCurrentDate, mBirDateTime, mIsMale);

            fortuneTv.setText(item.luckyScore + "分");
            fortuneDayTv.setText(item.luckyTitle);
//        mFortuneText.append(item.dayEvent);


            if (num == 0) {
                UIUtils.displayBigAvatarImage("", avatar, R.drawable.home_bg1);
                dayTitleTv.setText("今日说");
                homeTitleTv.setBackgroundResource(R.drawable.home_title_bg_purple);
                fortuneTv.setBackgroundResource(R.drawable.bg_home_orange);
                seeAllLLayout.setVisibility(View.GONE);
                eventLLayout.setVisibility(View.VISIBLE);
            } else {
                UIUtils.displayBigAvatarImage("", avatar, R.drawable.home_bg2);
                dayTitleTv.setText("明日说");
                homeTitleTv.setBackgroundResource(R.drawable.home_title_bg_blue);
                fortuneTv.setBackgroundResource(R.drawable.bg_home_purple);
                if (AppContext.isNetworkConnected()) {
                    seeAllLLayout.setVisibility(View.VISIBLE);
                } else {
                    seeAllLLayout.setVisibility(View.GONE);
                }

                eventLLayout.setVisibility(View.GONE);
            }


            locationTv.setText("幸运位：" + item.luckyPos);
            colorTv.setText("幸运色：" + item.luckyColor);


            //今天是你的（今天运势）
            todayFortuneTv.setText("今天是你的" + item.luckyTitle);
            eventTv.setText(item.dayEvent);
            dayTv.setText(item.publicCalendar);
            dayTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mActivity.getResources().getDimension(R.dimen.text_size_32));
            LogUtils.d("-----" + mActivity.getResources().getDimension(R.dimen.text_size_32));
            lunarTv.setText(item.lunarCalendar);

            for (int i = 0; i < 6; i++) {
                final View eventView = mInflater.inflate(R.layout.home_event_day_item, null);
                TextView eventTitleTv = (TextView) eventView.findViewById(R.id.title_tv);
                TextView eventContnetTv = (TextView) eventView.findViewById(R.id.event_content_tv);
                ImageView eventIv = (ImageView) eventView.findViewById(R.id.event_iv);
                LinearLayout closeLLayout = (LinearLayout) eventView.findViewById(R.id.close_llayout);
                LinearLayout contentLay = (LinearLayout) eventView.findViewById(R.id.content_lay);
                TextView closeTv = (TextView) eventView.findViewById(R.id.close_tv);
                String title = "";
                String content = "";
                int imageId = 0;
                if (mHomeItem.size() == 0) {
                    break;
                }

                switch (i) {
                    case 0:
                        title = "起床";
                        content = item.getUp;
                        imageId = R.drawable.ic_wake_up;
                        break;
                    case 1:
                        title = "出行";
                        content = item.travel;
                        imageId = R.drawable.ic_going_out;
                        break;
                    case 2:
                        if (TextUtils.isEmpty(item.work)) {
                            title = "学习";
                            content = item.study;
                        } else {
                            title = "工作";
                            content = item.work;
                        }

                        imageId = R.drawable.ic_working;
                        break;
                    case 3:
                        title = "饮食";
                        content = item.diet;
                        imageId = R.drawable.ic_eating;
                        break;
                    case 4:
                        title = "生日";
                        content = item.birth;
                        imageId = R.drawable.ic_birthday;
                        break;
                    case 5:
                        title = "吉日";
                        content = item.luckyDay;
                        imageId = R.drawable.ic_lucky_day;
                        break;
                    default:
                        title = "吉日";
                        content = item.getUp;
                        imageId = R.drawable.ic_wake_up;
                        break;
                }
                if (TextUtils.isEmpty(content)) {
                    eventView.setVisibility(View.GONE);
                    if (i == 5) {
                        contentLay.setVisibility(View.GONE);
                    }
                } else {
                    eventView.setVisibility(View.VISIBLE);
                    contentLay.setVisibility(View.VISIBLE);
                }
                if (i == 5) {
                    closeLLayout.setVisibility(View.VISIBLE);

                } else {
                    closeLLayout.setVisibility(View.GONE);
                }
                closeTv.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        eventLLayout.setVisibility(View.GONE);
                        seeAllLLayout.setVisibility(View.VISIBLE);
                        if (num == 0) {
                            mScrollView.scrollTo(0, itemHight /3 + 120);
                            mView1isClose = true;
                        } else {
                            if (mView1isClose) {
                                mScrollView.scrollTo(0, itemHight * 3/2 + 20);
                            } else {
                                mScrollView.scrollTo(0, itemHight * 3);
                            }

                        }

                    }
                });
                eventTitleTv.setText(title);
                eventContnetTv.setText(content);
                eventIv.setImageResource(imageId);
                eventLLayout.addView(eventView);
            }
        }

        return view;
    }

    private void getDayEvent() {
        if (AppContext.isNetworkConnected()) {
            mProgressLay.setVisibility(View.VISIBLE);


            KDSPApiController.getInstance().getMainData(mBirDateTime.toServerMinString(), mIsMale ? 1 : 2, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {


                    if (response != null) {
                        mHomeItem.clear();
                        mHomeItem.add(HomeItem.parseObjectFromJSON(response.optJSONObject("today")));
                        mHomeItem.add(HomeItem.parseObjectFromJSON(response.optJSONObject("tomorrow")));
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressLay.setVisibility(View.GONE);
                                mContentLlayout.setVisibility(View.VISIBLE);
                                mContentLlayout.removeAllViews();
                                mContentLlayout.addView(getItemView(0));
                                mContentLlayout.addView(getItemView(1));
                                mMonthFortuneLay.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {


                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                        mProgressLay.setVisibility(View.GONE);
//                        mBadLay.setVisibility(View.VISIBLE);
//                        mBadLay.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
//                            @Override
//                            public void onSingleClick(View v) {
//
//                                getDayEvent();
//                            }
//                        });
                        }
                    });

                }

            });
        } else {
            mProgressLay.setVisibility(View.GONE);
            mContentLlayout.setVisibility(View.GONE);
            mContentLlayout.removeAllViews();
//            mContentLlayout.addView(getItemView(0));
//            mContentLlayout.addView(getItemView(1));
            mMonthFortuneLay.setVisibility(View.VISIBLE);
        }
    }

    private void getMonthFortune(boolean isRefresh) {
        mProgressLay.setVisibility(View.VISIBLE);
        //没有缓存，生日改变，月份改变
        if (mMonth.length == 0 || isRefresh || mIsMonthChange) {

            KDSPApiController.getInstance().findLuckymonth(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (response != null) {
                        final JSONArray jsonArray = response.optJSONArray("list");
                        String[] month = new String[6];
                        month[0] = jsonArray.optJSONObject(0).optString("description");
                        month[1] = jsonArray.optJSONObject(1).optString("description");
                        month[2] = jsonArray.optJSONObject(2).optString("description");
                        month[3] = "" + StringUtils.getCountofChar(jsonArray.optJSONObject(0).optString("name"), '★');
                        month[4] = "" + StringUtils.getCountofChar(jsonArray.optJSONObject(1).optString("name"), '★');
                        month[5] = "" + StringUtils.getCountofChar(jsonArray.optJSONObject(2).optString("name"), '★');
                        SPUtils.clearByKey("MonthFortune");
                        SPUtils.clearByKey("homeMonth");
                        SPUtils.putStringArrayValue("MonthFortune", month);
                        SPUtils.putIntValue("homeMonth", mDateTime.month);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressLay.setVisibility(View.GONE);
                            }
                        });

                        reMonthFortune(month);
                        mPtrFrameLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                mPtrFrameLayout.refreshComplete();
                            }
                        }, 3000);
                    }
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {


                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressLay.setVisibility(View.GONE);
                            mMonthFortuneLay.setVisibility(View.GONE);
                            mContentLlayout.setVisibility(View.GONE);
                            mBadLay.setVisibility(View.VISIBLE);
                            mBadLay.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    getDayEvent();
                                    getMonthFortune(false);
                                }
                            });
                        }
                    });

                }

            });

        } else {
            mPtrFrameLayout.refreshComplete();
            reMonthFortune(mMonth);
        }
    }


    private void reMonthFortune(final String[] month) {
        mMonthLoveText.clear();
        mMonthCareerText.clear();
        mMonthMoneyText.clear();


        mMonthLoveText.append("感情: ").append(month[0]).append("\n");
        mMonthCareerText.append("事业: ").append(month[1]).append("\n");
        mMonthMoneyText.append("财运: ").append(month[2]).append("\n");
        mMonthLoveText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.black)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMonthLoveText.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMonthCareerText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.black)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMonthCareerText.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMonthMoneyText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.black)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMonthMoneyText.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {


                    mBadLay.setVisibility(View.GONE);
                    switch (StringUtils.toInt(month[3])) {
                        case 1:
                            mMonthLoveImage.setBackgroundResource(R.drawable.level1);
                            mMonthLoveLevel.setImageResource(R.drawable.star11);
                            break;
                        case 2:
                            mMonthLoveImage.setBackgroundResource(R.drawable.level2);
                            mMonthLoveLevel.setImageResource(R.drawable.star2);
                            break;
                        case 3:
                            mMonthLoveImage.setBackgroundResource(R.drawable.level3);
                            mMonthLoveLevel.setImageResource(R.drawable.star3);
                            break;
                        case 4:
                            mMonthLoveImage.setBackgroundResource(R.drawable.level4);
                            mMonthLoveLevel.setImageResource(R.drawable.star4);
                            break;
                        case 5:
                            mMonthLoveImage.setBackgroundResource(R.drawable.level5);
                            mMonthLoveLevel.setImageResource(R.drawable.star5);
                            break;
                    }
                    switch (StringUtils.toInt(month[4])) {
                        case 1:
                            mMonthCareerImage.setBackgroundResource(R.drawable.level1);
                            mMonthCareerLevel.setImageResource(R.drawable.star11);
                            break;
                        case 2:
                            mMonthCareerImage.setBackgroundResource(R.drawable.level2);
                            mMonthCareerLevel.setImageResource(R.drawable.star2);
                            break;
                        case 3:
                            mMonthCareerImage.setBackgroundResource(R.drawable.level3);
                            mMonthCareerLevel.setImageResource(R.drawable.star3);
                            break;
                        case 4:
                            mMonthCareerImage.setBackgroundResource(R.drawable.level4);
                            mMonthCareerLevel.setImageResource(R.drawable.star4);
                            break;
                        case 5:
                            mMonthCareerImage.setBackgroundResource(R.drawable.level5);
                            mMonthCareerLevel.setImageResource(R.drawable.star5);
                            break;
                    }
                    switch (StringUtils.toInt(month[5])) {
                        case 1:
                            mMonthMoneyImage.setBackgroundResource(R.drawable.level1);
                            mMonthMoneyLevel.setImageResource(R.drawable.star11);
                            break;
                        case 2:
                            mMonthMoneyImage.setBackgroundResource(R.drawable.level2);
                            mMonthMoneyLevel.setImageResource(R.drawable.star2);
                            break;
                        case 3:
                            mMonthMoneyImage.setBackgroundResource(R.drawable.level3);
                            mMonthMoneyLevel.setImageResource(R.drawable.star3);
                            break;
                        case 4:
                            mMonthMoneyImage.setBackgroundResource(R.drawable.level4);
                            mMonthMoneyLevel.setImageResource(R.drawable.star4);
                            break;
                        case 5:
                            mMonthMoneyImage.setBackgroundResource(R.drawable.level5);
                            mMonthMoneyLevel.setImageResource(R.drawable.star5);
                            break;
                    }
                    mMonthLove.setText(mMonthLoveText);
                    mMonthCareer.setText(mMonthCareerText);
                    mMonthMoney.setText(mMonthMoneyText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
