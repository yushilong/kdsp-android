package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.RegexUtil;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 09/02/2017.
 * 手机登录页
 */
public class MobileLoginActivity extends BaseActivity {
    private EditText mEditPhone;            //手机
    private EditText mEditPassword;         //密码
    private View mPprogressLay;         //密码

    private boolean redirectToMain;         //是否跳转到主界面,跳转到主页面则表示更新了用户，此时需要重新调用init接口
    private String mJson;                   //传入的额外json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_login);
        Intent intent = getIntent();
        redirectToMain = intent.getBooleanExtra(IntentExtraConfig.EXTRA_REDIRECT_MAIN, true);
        mJson = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
        initUI();
    }

    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.login);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        mPprogressLay = findViewById(R.id.progress_lay);

        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditPhone.setText(SPUtils.getStringValue(YSRLConstants.LOGIN_PHONE_NUMBER));
        mEditPassword.setText(SPUtils.getStringValue(YSRLConstants.LOGIN_PHONE_PASSWORD));

        findViewById(R.id.login_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String username = mEditPhone.getText().toString();
                String password = mEditPassword.getText().toString();
                if (!TextUtils.isEmpty(username)) {
                    if (!TextUtils.isEmpty(password)) {
                        if (RegexUtil.isMobileNumber(username)) {
                            SPUtils.putStringValue(YSRLConstants.LOGIN_PHONE_NUMBER,username);
                            SPUtils.putStringValue(YSRLConstants.LOGIN_PHONE_PASSWORD,password);
                            showLoadingDialog();
                            KDSPApiController.getInstance().userLogin(username, password, new KDSPHttpCallBack() {
                                @Override
                                public void handleAPISuccessMessage(JSONObject response) {
                                    if (response != null) {
                                        User user = User.parseObjectFromJSON(response.optJSONObject("user"));
                                        String birthTime = response.optJSONObject("user").optString("birthTime");
                                        if (user != null) {
                                            //如果登录并未返回生日，那么让用户输入生日

                                            if (TextUtils.isEmpty(birthTime) || "null".equals(birthTime)) {
                                                SPUtils.putBoolSync(YSRLConstants.HAS_ENTER_INFO, false);
                                                AppContext.doAfterLogin(user);
                                                SetInfoActivity.goToPage(MobileLoginActivity.this);
                                            } else {
                                                SPUtils.putBoolSync(YSRLConstants.HAS_ENTER_INFO, true);
                                                AppContext.doAfterLogin(user);
                                                if (redirectToMain) {
                                                    //登录成功之后记得调用init接口更新用户配置
                                                    setInitFlag(true);
                                                    MainActivity.goToPage(MobileLoginActivity.this);
                                                }
                                                BroadcastUtils.sendLoginSuccessBroadcast(mJson);
                                                finish();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                    MobileLoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                        }
                                    });
                                    showFailureMessage(error);
                                }
                            });
                        } else {
                            UIUtils.toastMsg("亲，你输入的号码不合理");
                        }
                    } else {
                        UIUtils.toastMsg("亲，密码不能为空!");
                    }
                } else {
                    UIUtils.toastMsg("亲，手机号不能为空！");
                }
            }
        });
        findViewById(R.id.forget_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LoginForgetActivity.goToPage(MobileLoginActivity.this);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            //点击空白位置 隐藏软键盘
            UIUtils.hideSoftKeyboard(MobileLoginActivity.this, focusView);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPage(Context context, boolean redirectToMain, String json) {
        Intent intent = new Intent(context, MobileLoginActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_REDIRECT_MAIN, redirectToMain);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, json);
        context.startActivity(intent);
    }
}
