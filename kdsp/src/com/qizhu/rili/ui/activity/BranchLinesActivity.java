package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.BranchLinesAdapter;
import com.qizhu.rili.bean.LineDetail;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 支线的功能实现
 */
public class BranchLinesActivity extends BaseActivity {
    private int mType;                      //支线类型
    private BranchLinesAdapter mAdapter;    //adapter
    private ArrayList<LineDetail> mLines = new ArrayList<LineDetail>();    //支线列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branch_lines_lay);
        mType = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE, 2);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initView();
        getData();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        GridView mPalmGrid = (GridView) findViewById(R.id.palm_grid);

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        mAdapter = new BranchLinesAdapter(BranchLinesActivity.this, mLines,mType);
        mPalmGrid.setAdapter(mAdapter);
    }

    private void getData() {
        showLoadingDialog();
        KDSPApiController.getInstance().findPalmBranchList(mType, 1, 10, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mLines = LineDetail.parseListFromJSON(response.optJSONArray("palmBranchs"));
                BranchLinesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        mAdapter.reset(mLines);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                showFailureMessage(error);
            }
        });
    }

    public static void goToPage(Context context, int type) {
        Intent intent = new Intent(context, BranchLinesActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, type);
        context.startActivity(intent);
    }
}
