package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.ItemAnswer;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.MediaStateChangedListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.DeleteOrderDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.VoiceUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 08/12/2016.
 * 手相和面相的订单详情
 */

public class HandsOrFaceOrderDetailActivity extends BaseActivity {
    private YSRLDraweeView mLeftImage;          //左侧图片
    private YSRLDraweeView mRightImage;         //右侧图片
    private LinearLayout mContainer;            //语音容器

    private String mIoId;                       //订单id
    private int mOrderType;                     //订单类型,0是问题,1是手相,2是面相
    private String mImage;                      //头像地址
    private String mItemParam;                  //问题详情
    private int mIsRead = 1;                    //是否已读1未读2已读,默认未读
    private boolean mSetRead = true;            //是否设置已读
    private ArrayList<ItemAnswer> mItemAnswers = new ArrayList<>();
    private TextView    mShare;                    //分享
    private TextView    mDelete;                   //删除
    private PopupWindow mPop;                   //操作弹出框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hands_face_order_lay);
        getIntentExtra();
        initUI();
        initPop();
        getOrderDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceUtils.releaseMedia();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        mIoId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mOrderType = intent.getIntExtra(IntentExtraConfig.EXTRA_MODE, 1);
        mImage = intent.getStringExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE);
        mItemParam = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
        mIsRead = intent.getIntExtra(IntentExtraConfig.EXTRA_PARCEL, 1);
        mSetRead = intent.getBooleanExtra(IntentExtraConfig.EXTRA_IS_MINE, true);
    }

    private void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.more_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mPop != null) {
                    if (mPop.isShowing()) {
                        mPop.dismiss();
                    } else {
                        mPop.showAsDropDown(findViewById(R.id.more_btn), 0, 0);
                    }
                }
            }
        });


        findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getOrderDetail();
            }
        });

        TextView mContent = (TextView) findViewById(R.id.content);
        mLeftImage = (YSRLDraweeView) findViewById(R.id.left_image);
        mRightImage = (YSRLDraweeView) findViewById(R.id.right_image);
        mContainer = (LinearLayout) findViewById(R.id.container);

        if (mOrderType == 1) {
            mTitle.setText(R.string.hands);
            mContent.setText(R.string.hands_answer);
        } else {
            mTitle.setText(R.string.face);
            mContent.setText(R.string.face_answer);
        }
    }

    private void initPop() {
        View catePopLay = mInflater.inflate(R.layout.augury_pop_lay, null);
        mPop = new PopupWindow(catePopLay, DisplayUtils.dip2px(85), DisplayUtils.dip2px(92), true);
        //如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框O
        mPop.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.gray18));
        mShare = (TextView) catePopLay.findViewById(R.id.share);
        mDelete = (TextView) catePopLay.findViewById(R.id.delete);
        mShare.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OperUtils.mSmallCat = OperUtils.SMALL_CAT_ITEM;
                OperUtils.mKeyCat = mIoId;
                ShareActivity.goToMiniShare(HandsOrFaceOrderDetailActivity.this, ShareUtils.getShareTitle(mOrderType == 1 ? ShareUtils.Share_Type_HAND : ShareUtils.Share_Type_FACE, ""),
                        ShareUtils.getShareContent(mOrderType == 1 ? ShareUtils.Share_Type_HAND : ShareUtils.Share_Type_FACE, ""),
                        getShareUrl(), "", mOrderType == 1 ? ShareUtils.Share_Type_HAND : ShareUtils.Share_Type_FACE, "",
                        "pages/home/divination/divination_answer?type=" + (mOrderType == 1?1:2)  + "&ioId="+mIoId);
                VoiceUtils.releaseMedia();

            }
        });
        mDelete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(DeleteOrderDialogFragment.newInstance(DeleteOrderDialogFragment.DEL, mIoId), "删除订单");

            }
        });
    }

    private void getOrderDetail() {
        showLoadingDialog();
        KDSPApiController.getInstance().getItemAnswerByOrderId(mIoId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mItemAnswers = ItemAnswer.parseListFromJSON(response.optJSONArray("itemAnswers"));
                mImage = response.optString("imageUrl");
                HandsOrFaceOrderDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        refreshUI();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                findViewById(R.id.normal_lay).setVisibility(View.GONE);
                findViewById(R.id.request_bad).setVisibility(View.VISIBLE);
                showFailureMessage(error);
            }
        });
    }

    private void refreshUI() {
        try {
            findViewById(R.id.normal_lay).setVisibility(View.VISIBLE);
            findViewById(R.id.request_bad).setVisibility(View.GONE);

            final JSONObject jsonObject = new JSONObject(mItemParam);

            UIUtils.display400Image(jsonObject.optString("image_url1"), mLeftImage, R.drawable.def_loading_img);
            UIUtils.display400Image(jsonObject.optString("image_url2"), mRightImage, R.drawable.def_loading_img);

            mLeftImage.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    ImageZoomViewer.goToPage(HandsOrFaceOrderDetailActivity.this, jsonObject.optString("image_url1"));
                }
            });

            mRightImage.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    ImageZoomViewer.goToPage(HandsOrFaceOrderDetailActivity.this, jsonObject.optString("image_url2"));
                }
            });

            if (!mItemAnswers.isEmpty()) {
                for (final ItemAnswer itemAnswer : mItemAnswers) {
                    if (itemAnswer.type == 2) {
                        View itemLay = mInflater.inflate(R.layout.item_answer_lay, null);
                        YSRLDraweeView avatar = (YSRLDraweeView) itemLay.findViewById(R.id.user_avatar);
                        final ImageView mPlay = (ImageView) itemLay.findViewById(R.id.voice_play);
                        final ImageView mUnread = (ImageView) itemLay.findViewById(R.id.unread);

                        UIUtils.displayAvatarImage(mImage, avatar, R.drawable.default_avatar);
                        if (itemAnswer.isRead == 0) {
                            mUnread.setVisibility(View.VISIBLE);
                        } else {
                            mUnread.setVisibility(View.GONE);
                        }
                        VoiceUtils.loadVoice(itemAnswer.content);
                        mPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                VoiceUtils.playVoice(itemAnswer.content, new MediaStateChangedListener() {
                                    @Override
                                    public void onStart(String url) {
//                                        mPlay.setImageResource(R.drawable.voice_anim);
                                        mPlay.setImageDrawable(ContextCompat.getDrawable(HandsOrFaceOrderDetailActivity.this,R.drawable.voice_anim));
                                        AnimationDrawable mAnimationDrawable = (AnimationDrawable) mPlay.getDrawable();
                                        mAnimationDrawable.start();
                                        if (itemAnswer.isRead == 0) {
                                            itemAnswer.isRead = 1;
                                            mUnread.setVisibility(View.GONE);
                                            KDSPApiController.getInstance().changeAnswerReaded(itemAnswer.iaId, new KDSPHttpCallBack() {
                                                @Override
                                                public void handleAPISuccessMessage(JSONObject response) {

                                                }

                                                @Override
                                                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onPause(String url) {
                                        AnimationDrawable mAnimationDrawable = (AnimationDrawable) mPlay.getDrawable();
                                        mAnimationDrawable.stop();
                                        mPlay.setImageResource(R.drawable.voice3);
                                    }

                                    @Override
                                    public void onStop(String url) {
                                        AnimationDrawable mAnimationDrawable = (AnimationDrawable) mPlay.getDrawable();
                                        mAnimationDrawable.stop();
                                        mPlay.setImageResource(R.drawable.voice3);
                                    }

                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        AnimationDrawable mAnimationDrawable = (AnimationDrawable) mPlay.getDrawable();
                                        mAnimationDrawable.stop();
                                        mPlay.setImageResource(R.drawable.voice3);
                                    }
                                });
                            }
                        });
                        mContainer.addView(itemLay);
                    }
                }
            }

            if (1 == mIsRead && mSetRead) {
                KDSPApiController.getInstance().updateItemStatusByOrderId(mIoId, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        AppContext.mUnReadReply--;
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getShareUrl() {
        return "http://h5.ishenpo.com/kdsp/share/palmistryAndFace?ioId=" + mIoId;
    }

    public static void goToPage(Context context, String ioId, int orderType, String image, String itemParam, int isRead) {
        goToPage(context, ioId, orderType, image, itemParam, isRead, true);
    }

    public static void goToPage(Context context, String ioId, int orderType, String image, String itemParam, int isRead, boolean setRead) {
        Intent intent = new Intent(context, HandsOrFaceOrderDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, ioId);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, orderType);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE, image);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, itemParam);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, isRead);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, setRead);
        context.startActivity(intent);
    }
}
