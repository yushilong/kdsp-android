package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 8/18/16.
 * 添加问题
 */
public class AddProblemActivity extends BaseActivity {
    private EditText mContent;              //问题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_problem_lay);
        initUI();
    }

    private void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.add_problem);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        mContent = (EditText) findViewById(R.id.content);

        findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String content = mContent.getText().toString();
                if (StringUtils.isEmpty(content)) {
                    UIUtils.toastMsg("请输入问题");
                } else {
                    showLoadingDialog();
                    KDSPApiController.getInstance().addPreItem(content, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            AddProblemActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissLoadingDialog();
                                    UIUtils.toastMsg("提交成功，请关注问题列表更新");
                                    finish();
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
            }
        });
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, AddProblemActivity.class);
        context.startActivity(intent);
    }
}
