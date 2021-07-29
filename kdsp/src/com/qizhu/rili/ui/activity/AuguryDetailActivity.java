package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.DeleteOrderDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 8/17/16.
 * 订单详情
 */
public class AuguryDetailActivity extends BaseActivity {
    private TextView mItemName;                 //标题
    private TextView mContent;                  //内容
    private TextView mShare;                    //分享
    private TextView mDelete;                   //删除
    private PopupWindow mPop;                   //操作弹出框

    private String mIoId;                       //订单id
    private String mName;                       //问题
    private String mImage;                      //图片
    private String mResult;                     //结果
    private int mIsRead = 1;                    //是否已读1未读2已读,默认未读
    private boolean mSetRead = true;            //是否设置已读
    private int mType;                      ////1为下单,2待回复,3已回复,4福豆兑换 6八字婚姻 7生肖大运

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augury_detail_lay);
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
        mType = intent.getIntExtra(IntentExtraConfig.EXTRA_TYPE, 0);
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

        mItemName = (TextView) findViewById(R.id.item_name);
        mContent = (TextView) findViewById(R.id.content);
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

                ShareActivity.goToMiniShare(AuguryDetailActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_ORDER_DETAIL, mName),
                        ShareUtils.getShareContent(ShareUtils.Share_Type_ORDER_DETAIL, ""), "http://www.qi-zhu.com:8080/Fortune-Calendar/app/shareExt/itemOrder?ioId=" + mIoId, "http://pt.qi-zhu.com/@/2016/08/18/51b23090-99a5-439d-89a7-969fde7537de.png", ShareUtils.Share_Type_ORDER_DETAIL, ""
                ,"pages/home/divination/divination_answer?type=" + mType  + "&ioId="+mIoId);


            }
        });
        mDelete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(DeleteOrderDialogFragment.newInstance(DeleteOrderDialogFragment.DEL, mIoId), "删除订单");
            }
        });
    }

    public static void goToPage(Context context, String ioId, String itemName, String image, String content, int isRead) {
        Intent intent = new Intent(context, AuguryDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, ioId);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, itemName);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE, image);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, content);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, isRead);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String ioId, String itemName, String image, String content, boolean setRead) {
        Intent intent = new Intent(context, AuguryDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, ioId);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, itemName);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE, image);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, content);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, setRead);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String ioId, String itemName, String image, String content, int isRead ,int type) {
        Intent intent = new Intent(context, AuguryDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, ioId);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, itemName);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_IMAGE, image);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, content);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, isRead);
        intent.putExtra(IntentExtraConfig.EXTRA_TYPE, type);
        context.startActivity(intent);
    }
}
