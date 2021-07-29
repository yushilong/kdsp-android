package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;


/**
 * 生肖大运
 */
public class AnimalsLuckActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animals_activity);
        initUI();
        getData();
    }


    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.animals_luck);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.share_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MarraigeShareActivity.goToPage(AnimalsLuckActivity.this);
            }
        });


    }

    private void getData() {
        showLoadingDialog();
//        KDSPApiController.getInstance().getItemInfoByItemId(mItemId, new KDSPHttpCallBack() {
//            @Override
//            public void handleAPISuccessMessage(JSONObject response) {
//
//
//                AnimalsLuckActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissLoadingDialog();
//
//                        refreshUI();
//                    }
//                });
//            }
//
//            @Override
//            public void handleAPIFailureMessage(Throwable error, String reqCode) {
//                showFailureMessage(error);
//                mNormalLay.setVisibility(View.GONE);
//                mBadLay.setVisibility(View.VISIBLE);
//            }
//        });
    }


    private void refreshUI() {

    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, AnimalsLuckActivity.class);
        context.startActivity(intent);
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.content_total_goods_btn:
                break;
            case R.id.reload:
                break;
        }
    }

    public void onViewClicked() {
    }
}
