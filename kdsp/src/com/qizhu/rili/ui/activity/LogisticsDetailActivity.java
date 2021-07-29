package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.LogisticsListAdapter;
import com.qizhu.rili.bean.Logistics;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 15/03/2017.
 * 物流详情
 */

public class LogisticsDetailActivity extends BaseActivity {
    private YSRLDraweeView   mImage;
    private TextView         mStateText;
    private TextView         mCompanyText;
    private TextView         mNumText;
    private LinearLayout     mDetailLay;
    private KDSPRecyclerView mList;

    private String mOrderId;
    private String mImageUrl;
    private int mState;             //快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态，其中4-7需要另外开通才有效
    private String mCompany;        //公司
    private String mNum;            //快递单号
    private ArrayList<Logistics> logisticses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logistics_detail_lay);
        mOrderId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        mImageUrl = getIntent().getStringExtra(IntentExtraConfig.EXTRA_PARCEL);
        initView();
        getData();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mImage = (YSRLDraweeView) findViewById(R.id.image);
        mStateText = (TextView) findViewById(R.id.state);
        mCompanyText = (TextView) findViewById(R.id.company);
        mNumText = (TextView) findViewById(R.id.number);
        mDetailLay = (LinearLayout) findViewById(R.id.detail_lay);
        mList = (KDSPRecyclerView) findViewById(R.id.logistics_lay);
        mList.instanceForListView(LinearLayoutManager.VERTICAL, false);
        mTitle.setText(R.string.logistics_detail);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    private void getData() {
        showLoadingDialog();
        KDSPApiController.getInstance().viewShipInfo(mOrderId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mState = response.optJSONObject("kuaidiInfo").optInt("state");
                mCompany = response.optString("shippingName");
                mNum = response.optString("shipNum");
                logisticses = Logistics.parseListFromJSON(response.optJSONObject("kuaidiInfo").optJSONArray("data"));
                LogisticsDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        findViewById(R.id.content_lay).setVisibility(View.VISIBLE);
                        findViewById(R.id.request_bad).setVisibility(View.GONE);
                        refreshUI();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                showFailureMessage(error);
                findViewById(R.id.content_lay).setVisibility(View.GONE);
                findViewById(R.id.request_bad).setVisibility(View.VISIBLE);
            }
        });
    }

    private void refreshUI() {
        UIUtils.display400Image(mImageUrl, mImage, R.drawable.def_loading_img);
        switch (mState) {
            case 0:
                mStateText.setText("在途中");
                break;
            case 1:
                mStateText.setText("已揽收");
                break;
            case 2:
                mStateText.setText("疑难件");
                break;
            case 3:
                mStateText.setText("已签收");
                break;
            case 4:
                mStateText.setText("退签");
                break;
            case 5:
                mStateText.setText("同城派送中");
                break;
            case 6:
                mStateText.setText("退回");
                break;
            case 7:
                mStateText.setText("转单");
                break;
        }
        mCompanyText.setText("承运公司：" + mCompany);
        mNumText.setText("运单编号：" + mNum);
        if (logisticses.isEmpty()) {
            mDetailLay.setVisibility(View.GONE);
        } else {
            mDetailLay.setVisibility(View.VISIBLE);
            LogisticsListAdapter mAdapter = new LogisticsListAdapter(this, logisticses);
            mList.setAdapter(mAdapter);
        }
    }

    public static void goToPage(Context context, String orderId, String image) {
        Intent intent = new Intent(context, LogisticsDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, orderId);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, image);
        context.startActivity(intent);
    }
}
