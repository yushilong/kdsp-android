package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Name;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.TestNameResultActivity;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 10/29/15.
 * 感情世界的fragment
 */
public class TestNameFragment extends BaseFragment {
    private String mItemId;                 //问题id

    private TextView mlastNameTv;               //姓
    private TextView mfirstNameTv;              //名

    public static TestNameFragment newInstance(String itemId) {
        TestNameFragment fragment = new TestNameFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_ID, itemId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle != null) {
            mItemId = bundle.getString(IntentExtraConfig.EXTRA_ID);
        }

        initView();
        refreshUI();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.test_name_fragment_lay, container, false);
    }

    protected void initView() {
        mlastNameTv = (TextView) mMainLay.findViewById(R.id.lastName_tv);
        mfirstNameTv = (TextView) mMainLay.findViewById(R.id.firstName_tv);
    }

    private void refreshUI() {
        mMainLay.findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                final String lastName = mlastNameTv.getText().toString();
                final String firstName = mfirstNameTv.getText().toString();
                if (TextUtils.isEmpty(lastName)) {
                    UIUtils.toastMsg("请输入你的姓~");
                } else if (TextUtils.isEmpty(firstName)) {
                    UIUtils.toastMsg("请输入你的名~");
                } else if (lastName.length() > 2) {
                    UIUtils.toastMsg("姓不符合规则");
                } else if (firstName.length() > 3) {
                    UIUtils.toastMsg("名不符合规则");
                } else if (!StringUtils.isChineseStr(lastName)) {
                    UIUtils.toastMsg("姓中有非法字符");
                } else if (!StringUtils.isChineseStr(firstName)) {
                    UIUtils.toastMsg("名中有非法字符");
                } else {
                    mActivity.showLoadingDialog();
                    KDSPApiController.getInstance().getNameTestResult(lastName, firstName, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(final JSONObject response) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.dismissLoadingDialog();
                                }
                            });
                            Name name = Name.parseObjectFromJSON(response.optJSONObject("name"));
                            LogUtils.d("response:" + response);
                            TestNameResultActivity.goToPage(mActivity, name, mItemId, mlastNameTv.getText().toString(), mfirstNameTv.getText().toString());
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                            mActivity.dismissLoadingDialog();
                            showFailureMessage(error);
                        }
                    });
                }
            }
        });
    }

}
