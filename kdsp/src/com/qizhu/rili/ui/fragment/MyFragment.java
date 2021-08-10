package com.qizhu.rili.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.AuguryListActivity;
import com.qizhu.rili.ui.activity.CartListActivity;
import com.qizhu.rili.ui.activity.CollectListActivity;
import com.qizhu.rili.ui.activity.FateCatListActivity;
import com.qizhu.rili.ui.activity.FeedBackListActivity;
import com.qizhu.rili.ui.activity.LoginActivity;
import com.qizhu.rili.ui.activity.MemberShipActivity;
import com.qizhu.rili.ui.activity.MemberShipCarListActivity;
import com.qizhu.rili.ui.activity.MessageListActivity;
import com.qizhu.rili.ui.activity.OrderListActivity;
import com.qizhu.rili.ui.activity.RegisterActivity;
import com.qizhu.rili.ui.activity.ReplyCommentActivity;
import com.qizhu.rili.ui.activity.SettingActivity;
import com.qizhu.rili.ui.activity.SettingUserInfoActivity;
import com.qizhu.rili.ui.activity.WeChatCouponsActivity;
import com.qizhu.rili.ui.activity.WordRewardListActivity;
import com.qizhu.rili.ui.activity.YSRLWebActivity;
import com.qizhu.rili.ui.dialog.ProtocolDetailDialog;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

/**
 * Created by lindow on 4/6/16.
 * 我的页面
 */
public class MyFragment extends BaseFragment {
    private TextView mTitle;                    //用户名
    private TextView mMember;                   //用户身份
    private YSRLDraweeView mUserAvatar;         //用户头像
    private TextView mUnReadWaiting;            //未读待回复消息数
    private TextView mUnReadReply;              //已读消息数
    private TextView mWaitPay;                  //待付款
    private TextView mWaitSend;                 //待发货
    private TextView mAlreadySend;              //待收货
    private TextView mUnReadFeedback;           //未读反馈
    private TextView mPointSum;                 //我的福豆
    private TextView mCartCount;                //购物车数量
    private TextView mBindPhoneTv;                //手机号
    private TextView mBindPhoneTitleTv;                //手机号

    private boolean mShouldOper = false;
    private int mVipStatus;                     //1是会员

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShouldOper) {
            mShouldOper = false;
            OperUtils.oper(OperUtils.BIG_CAT_COMMENT, "android", AppContext.userId);
        }
        refreshUserData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshUserData();
        }
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.my_fragment_lay, container, false);
    }

    private void initView() {
        mUserAvatar = (YSRLDraweeView) mMainLay.findViewById(R.id.user_avatar);

        mTitle = (TextView) mMainLay.findViewById(R.id.my_title);
        mMember = (TextView) mMainLay.findViewById(R.id.my_member);
        mUnReadWaiting = (TextView) mMainLay.findViewById(R.id.waiting_unread);
        mUnReadReply = (TextView) mMainLay.findViewById(R.id.reply_unread);
        mWaitPay = (TextView) mMainLay.findViewById(R.id.wait_pay_unread);
        mWaitSend = (TextView) mMainLay.findViewById(R.id.has_pay_unread);
        mAlreadySend = (TextView) mMainLay.findViewById(R.id.wait_receive_unread);
        mUnReadFeedback = (TextView) mMainLay.findViewById(R.id.feedback_unread);
        mPointSum = (TextView) mMainLay.findViewById(R.id.my_score);
        mCartCount = (TextView) mMainLay.findViewById(R.id.cart_count);
        mBindPhoneTv = (TextView) mMainLay.findViewById(R.id.bind_phone_tv);
        mBindPhoneTitleTv = (TextView) mMainLay.findViewById(R.id.bind_phone_title_tv);

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingUserInfoActivity.goToPage(mActivity);

            }
        });


        mMainLay.findViewById(R.id.setting).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                SettingActivity.goToPage(mActivity);
            }
        });
        mMainLay.findViewById(R.id.service_tv).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showProtocolDia("http://h5.ishenpo.com/yhxy.html", R.string.user_service_title);
            }
        });
        mMainLay.findViewById(R.id.policy_tv).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showProtocolDia("http://h5.ishenpo.com/privacy.html", R.string.user_policy_title);
            }
        });

        mMainLay.findViewById(R.id.cart_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CartListActivity.goToPage(mActivity);
            }
        });

        mMainLay.findViewById(R.id.message).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MessageListActivity.goToPage(mActivity);
            }
        });

        mMainLay.findViewById(R.id.wait_reply).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AuguryListActivity.goToPage(mActivity, false);
            }
        });
        mMainLay.findViewById(R.id.has_reply).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AuguryListActivity.goToPage(mActivity, true);
            }
        });
        mMainLay.findViewById(R.id.rewarded).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                WordRewardListActivity.goToPage(mActivity);
            }
        });

        mMainLay.findViewById(R.id.un_use_rl).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                WeChatCouponsActivity.goToPage(mActivity);
            }
        });
        mMainLay.findViewById(R.id.completed_order).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OrderListActivity.goToPage(mActivity, OrderDetail.COMPLETED);
            }
        });
        mMainLay.findViewById(R.id.wait_pay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OrderListActivity.goToPage(mActivity, OrderDetail.WAIT_PAY);
            }
        });
        mMainLay.findViewById(R.id.has_pay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OrderListActivity.goToPage(mActivity, OrderDetail.HAS_PAID);
            }
        });
        mMainLay.findViewById(R.id.wait_receive).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OrderListActivity.goToPage(mActivity, OrderDetail.HAS_SEND);
            }
        });
        mMainLay.findViewById(R.id.feed_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FeedBackListActivity.goToPage(mActivity);
            }
        });


        mMainLay.findViewById(R.id.app_score).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //调用积分接口
                mShouldOper = true;
                goToMarketScore();
            }
        });
        mMainLay.findViewById(R.id.membership_card).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
//                MemberShipCardActivity.goToPage(mActivity);
                MemberShipCarListActivity.goToPage(mActivity);

            }
        });
        mMainLay.findViewById(R.id.score_convert).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FateCatListActivity.goToPage(mActivity, "");
            }
        });
        mMainLay.findViewById(R.id.my_score).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                YSRLWebActivity.goToPage(mActivity, "http://h5.ishenpo.com/kdsp/h5/integral");
            }
        });
        mMainLay.findViewById(R.id.membership_right).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MemberShipActivity.goToPage(mActivity);
            }
        });

        mMainLay.findViewById(R.id.my_collect_llayout).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CollectListActivity.goToPage(mActivity,4);
            }
        });

        mMainLay.findViewById(R.id.bind_phone_llayout).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(AppContext.mUser !=null){
                    if(TextUtils.isEmpty(AppContext.mUser.telephoneNumber)){
                        RegisterActivity.goToPageWithResult(mActivity,1);
                    }else {
                        RegisterActivity.goToPageWithResult(mActivity,2);
                    }
                }
            }
        });
    }

    private void showProtocolDia(String url, int p) {
        ProtocolDetailDialog protocolDetailDialog = new ProtocolDetailDialog(getContext());
//        String url = "file:///android_asset/" + s;
        protocolDetailDialog.setTitleContent(p, url);
        protocolDetailDialog.show();
    }

    private void refreshUI() {
        //异步调用的时候可能出问题 http://bugly.qq.com/detail?app=900009923&pid=1&ii=984#stack
        try {
            if (AppContext.mUser != null) {
                UIUtils.displayBigAvatarImage(AppContext.mUser.imageUrl, mUserAvatar, R.drawable.default_avatar);
                mTitle.setText(StringUtils.isEmpty(AppContext.mUser.nickName) ? getString(R.string.enter_nickname) : AppContext.mUser.nickName);
                if(TextUtils.isEmpty(AppContext.mUser.telephoneNumber)){
                    mBindPhoneTitleTv.setText(mResources.getText(R.string.bind_phone));
                }else{
                    mBindPhoneTitleTv.setText(mResources.getText(R.string.have_bind_phone));
                }
                mBindPhoneTv.setText(AppContext.mUser.telephoneNumber);
            }
            if (AppContext.isAnonymousUser()) {
                mMainLay.findViewById(R.id.no_login).setVisibility(View.VISIBLE);
                mMainLay.findViewById(R.id.avatar_lay).setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        LoginActivity.goToPage(mActivity);
                    }
                });
            } else {
                mMainLay.findViewById(R.id.no_login).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.avatar_lay).setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        SettingUserInfoActivity.goToPage(mActivity);
                    }
                });
            }
            if (mVipStatus == 1) {
                mMember.setText(R.string.member);
            } else {
                mMember.setText(R.string.normal);
            }
            refreshUnread();
            setPoint();
            setCartCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshUnread() {
        if (AppContext.mUnReadWaiting > 0) {
            mUnReadWaiting.setText(AppContext.mUnReadWaiting + "");
            mUnReadWaiting.setVisibility(View.VISIBLE);
        } else {
            mUnReadWaiting.setVisibility(View.GONE);
        }
        if (AppContext.mUnReadReply > 0) {
            mUnReadReply.setText(AppContext.mUnReadReply + "");
            mUnReadReply.setVisibility(View.VISIBLE);
        } else {
            mUnReadReply.setVisibility(View.GONE);
        }
        if (AppContext.mWaitPay > 0) {
            mWaitPay.setText(AppContext.mWaitPay + "");
            mWaitPay.setVisibility(View.VISIBLE);
        } else {
            mWaitPay.setVisibility(View.GONE);
        }
        if (AppContext.mWaitSend > 0) {
            mWaitSend.setText(AppContext.mWaitSend + "");
            mWaitSend.setVisibility(View.VISIBLE);
        } else {
            mWaitSend.setVisibility(View.GONE);
        }
        if (AppContext.mAlreadySend > 0) {
            mAlreadySend.setText(AppContext.mAlreadySend + "");
            mAlreadySend.setVisibility(View.VISIBLE);
        } else {
            mAlreadySend.setVisibility(View.GONE);
        }
        if (AppContext.mUnReadFeedback > 0) {
            mUnReadFeedback.setText(AppContext.mUnReadFeedback + "");
            mUnReadFeedback.setVisibility(View.VISIBLE);
        } else {
            mUnReadFeedback.setVisibility(View.GONE);
        }
    }

    /**
     * 更新user信息
     */
    public void refreshUserData() {
        //如果需要，再从网络同步一次数据
        if (AppContext.isNeedUpdateUserData) {
            refreshUI();
            KDSPApiController.getInstance().getUserInfoByUserId(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    if (response != null) {
                        User user = User.parseObjectFromJSON(response.optJSONObject("user"));
                        AppContext.doAfterLogin(user);
                        AppContext.isNeedUpdateUserData = false;
                        mVipStatus = response.optInt("vipStatus");
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshUI();
                            }
                        });
                    }
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                }
            });
            KDSPApiController.getInstance().getPointByUserId(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    AppContext.mPointSum = response.optInt("pointSum");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setPoint();
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                }
            });
            KDSPApiController.getInstance().findCartCountByUserId(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    AppContext.mCartCount = response.optInt("count");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCartCount();
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                }
            });
            KDSPApiController.getInstance().getShippingCount(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    AppContext.mAddressNum = response.optInt("count");
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                }
            });
        } else {
            refreshUI();
        }
        if (AppContext.userId.equals("f39cd2724e5227a0014e6102cebf09cb")) {
            mMainLay.findViewById(R.id.reply_divide).setVisibility(View.VISIBLE);
            mMainLay.findViewById(R.id.reply_comment).setVisibility(View.VISIBLE);
            mMainLay.findViewById(R.id.reply_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplyCommentActivity.goToPage(mActivity);
                }
            });
        } else {
            mMainLay.findViewById(R.id.reply_divide).setVisibility(View.GONE);
            mMainLay.findViewById(R.id.reply_comment).setVisibility(View.GONE);
        }
    }

    /**
     * 设置福豆
     */
    private void setPoint() {
        mPointSum.setText("我的福豆" + AppContext.mPointSum);
    }

    /**
     * 设置购物车数量
     */
    private void setCartCount() {
        if (AppContext.mCartCount > 0) {
            mCartCount.setVisibility(View.VISIBLE);
            mCartCount.setText(AppContext.mCartCount + "");
        } else {
            mCartCount.setVisibility(View.GONE);
        }
    }

    /**
     * 跳转应用市场进行评分
     */
    private void goToMarketScore() {
        try {
            Uri uri = Uri.parse("market://details?id=com.qizhu.rili");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
