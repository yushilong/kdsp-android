package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.TestAdapter;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.data.TestDataAccessor;
import com.qizhu.rili.listener.LoginSuccessListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.BaZiActivity;
import com.qizhu.rili.ui.activity.CalendarGoodActivity;
import com.qizhu.rili.ui.activity.InferringBloodActivity;
import com.qizhu.rili.ui.activity.InferringWordActivity;
import com.qizhu.rili.ui.activity.LifeNumberActivity;
import com.qizhu.rili.ui.activity.LoginChooserActivity;
import com.qizhu.rili.ui.activity.LoveLineSettingActivity;
import com.qizhu.rili.ui.activity.MyLifeActivity;
import com.qizhu.rili.ui.activity.PrayActivity;
import com.qizhu.rili.ui.activity.SetFriendsInfoActivity;
import com.qizhu.rili.utils.LogUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 4/6/16.
 * 口袋页面
 */
public class PocketFragment extends BaseListFragment {
    private ImageView mTestFontUnread;              //测字未读

    private TestDataAccessor mDataAccessor;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUnread();
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setVisibility(View.GONE);
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            mTitle.setText(R.string.pocket);

            titleView.addView(view, params);
        }
    }

    @Override
    protected void addScrollEnterView() {
        View view = mInflater.inflate(R.layout.pocket_head_lay, null);
        if (view != null) {
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mTestFontUnread = (ImageView) view.findViewById(R.id.test_font_unread);
            view.findViewById(R.id.inferring_word).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (AppContext.isAnonymousUser()) {
                        LoginChooserActivity.goToPage(mActivity, new LoginSuccessListener() {
                            @Override
                            public void success() {
                                InferringWordActivity.goToPage(mActivity);
                            }
                        });
                    } else {
                        InferringWordActivity.goToPage(mActivity);
                        KDSPApiController.getInstance().addStatistics(StatisticsConstant.SOURCE_POCKET,
                                StatisticsConstant.TYPE_PAGE, StatisticsConstant.subType_Inferring, new KDSPHttpCallBack() {
                                    @Override
                                    public void handleAPISuccessMessage(JSONObject response) {

                                    }

                                    @Override
                                    public void handleAPIFailureMessage(Throwable error, String reqCode) {

                                    }
                                });
                    }
                }
            });

            view.findViewById(R.id.love_line).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (AppContext.isAnonymousUser()) {
                        LoginChooserActivity.goToPage(mActivity, new LoginSuccessListener() {
                            @Override
                            public void success() {
                                LoveLineSettingActivity.goToPage(mActivity);
                            }
                        });
                    } else {
                        LoveLineSettingActivity.goToPage(mActivity);
                        KDSPApiController.getInstance().addStatistics(StatisticsConstant.SOURCE_POCKET, StatisticsConstant.TYPE_PAGE, StatisticsConstant.subType_YUANLAIRUCI, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {

                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                            }
                        });
                    }
                }
            });

            view.findViewById(R.id.shake).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (AppContext.isAnonymousUser()) {
                        LoginChooserActivity.goToPage(mActivity, new LoginSuccessListener() {
                            @Override
                            public void success() {
                                PrayActivity.goToPage(mActivity);
                            }
                        });
                    } else {
                        PrayActivity.goToPage(mActivity);
                        KDSPApiController.getInstance().addStatistics(StatisticsConstant.SOURCE_POCKET, StatisticsConstant.TYPE_PAGE, StatisticsConstant.subType_YAOYIYAO, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {

                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                            }
                        });
                    }
                }
            });

            view.findViewById(R.id.inferring_blood).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    InferringBloodActivity.goToPage(mActivity);
                }
            });

            view.findViewById(R.id.analysis_friend).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    SetFriendsInfoActivity.goToPage(mActivity);
                }
            });

            view.findViewById(R.id.good_day).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    CalendarGoodActivity.goToPage(mActivity);
                }
            });
            view.findViewById(R.id.life_llayout).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    MyLifeActivity.goToPage(mActivity, true);
                }
            });

            view.findViewById(R.id.wu_xing_llayout).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    MyLifeActivity.goToPage(mActivity, false);
                }
            });
            view.findViewById(R.id.bazi_llayout).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    BaZiActivity.goToPage(mActivity);
                }
            });
            view.findViewById(R.id.number_llayout).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    LifeNumberActivity.goToPage(mActivity);
                }
            });


            mScrollEnterView.addView(view);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new TestDataAccessor(false);
        }
        if (mAdapter == null) {
            mAdapter = new TestAdapter(mActivity, mDataAccessor.mData);
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

    @Override
    protected void onAppBarOffsetChanged(int verticalOffset) {
        LogUtils.d("---->fragment Offset:" + verticalOffset);
        mCanPullDownRefresh = verticalOffset >= 0;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            changeUnread();
        }
    }

    private void changeUnread() {
        if (AppContext.mUnReadTestFont > 0) {
            mTestFontUnread.setVisibility(View.VISIBLE);
        } else {
            mTestFontUnread.setVisibility(View.GONE);
        }
    }
}
