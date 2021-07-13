package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.MediaStateChangedListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.VoiceUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 8/17/16.
 * 订单详情
 */
public class MasterAuguryDetailActivity extends BaseActivity {
    private  TextView  mItemName;                 //标题
    private  TextView  mContent;                  //内容
    private  TextView  mShare;                    //分享
    private  TextView  mDelete;                   //删除
    private ImageView mVoice;                     //语音
    private PopupWindow mPop;                   //操作弹出框

    private String mIoId;                       //订单id
    private String mName;                       //问题
    private String mImage;                      //图片
    private String mResult;                     //结果
    private int mIsRead = 1;                    //是否已读1未读2已读,默认未读
    private boolean mSetRead = true;            //是否设置已读
    private AnimationDrawable mAnimationDrawable;   //动画
    private boolean mIsNotEnd = false;      //语音处于未结束状态
    private  String mVoicePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_augury_detail_activity);
        getIntentExtra();
        initUI();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        mIoId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mName = intent.getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
        mImage = intent.getStringExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE);
        mResult = intent.getStringExtra(IntentExtraConfig.EXTRA_PARCEL);
        mIsRead = intent.getIntExtra(IntentExtraConfig.EXTRA_MODE, 1);
        mSetRead = intent.getBooleanExtra(IntentExtraConfig.EXTRA_JSON, true);
    }

    private void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.order_detail);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });



        mItemName = (TextView) findViewById(R.id.item_name);
        mContent = (TextView) findViewById(R.id.content);
        mVoice = (ImageView) findViewById(R.id.voice_play);
        mContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        mItemName.setText(mName);
        mContent.setText(mResult);
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
        mVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceUtils.playVoice(mVoicePath, new MediaStateChangedListener() {
                    @Override
                    public void onStart(String url) {
                        mIsNotEnd = true;
                        mVoice.setImageResource(R.drawable.voice_anim);
                        mAnimationDrawable = (AnimationDrawable) mVoice.getDrawable();
                        mAnimationDrawable.start();

                    }

                    @Override
                    public void onPause(String url) {
                        mAnimationDrawable.stop();
                        mVoice.setImageResource(R.drawable.voice3);
                    }

                    @Override
                    public void onStop(String url) {
                        mAnimationDrawable.stop();
                        mVoice.setImageResource(R.drawable.voice3);
                    }

                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mIsNotEnd = false;
                        mAnimationDrawable.stop();
                        mVoice.setImageResource(R.drawable.voice3);
                    }
                });
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
        initPop();
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
                ShareActivity.goToShare(MasterAuguryDetailActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_ORDER_DETAIL, mName),
                        ShareUtils.getShareContent(ShareUtils.Share_Type_ORDER_DETAIL, ""), "http://www.qi-zhu.com:8080/Fortune-Calendar/app/shareExt/itemOrder?ioId=" + mIoId, "http://pt.qi-zhu.com/@/2016/08/18/51b23090-99a5-439d-89a7-969fde7537de.png", ShareUtils.Share_Type_ORDER_DETAIL, "");
            }
        });
        mDelete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showLoadingDialog();
                KDSPApiController.getInstance().delAuguryOrder(mIoId, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        BroadcastUtils.sendRefreshBroadcast(mIoId);
                        finish();
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        dismissLoadingDialog();
                        showFailureMessage(error);
                    }
                });
            }
        });
    }


    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MasterAuguryDetailActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String ioId) {
        Intent intent = new Intent(context, MasterAuguryDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, ioId);
        context.startActivity(intent);
    }
}
