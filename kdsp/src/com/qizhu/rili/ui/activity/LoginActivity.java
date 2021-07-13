package com.qizhu.rili.ui.activity;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.listener.OnAuthCallbackListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.ProtocolDialog;
import com.qizhu.rili.utils.AuthUtils;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.SSOSinaUtils;
import com.qizhu.rili.utils.SSOTencentUtils;
import com.qizhu.rili.utils.UIUtils;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.qizhu.rili.YSRLConstants.SERVICE_POLICY_AGREE;

public class LoginActivity extends BaseActivity {
    private boolean redirectToMain;         //是否跳转到主界面,跳转到主页面则表示更新了用户，此时需要重新调用init接口
    private String mJson;                   //传入的额外json

    //支付结果的广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_LOGIN_SUCCESS.equals(action)) {
                if (redirectToMain) {
                    //登录成功之后关闭页面
                    finish();
                } else {
                    if (!TextUtils.isEmpty(mJson)) {
                        Intent intent1 = new Intent();
                        intent1.putExtra(IntentExtraConfig.EXTRA_JSON, mJson);
                        LoginActivity.this.setResult(RESULT_OK, intent1);
                    } else {
                        LoginActivity.this.setResult(RESULT_OK);
                    }
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Intent intent = getIntent();
        redirectToMain = intent.getBooleanExtra(IntentExtraConfig.EXTRA_REDIRECT_MAIN, true);
        mJson = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
        initUI();
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_LOGIN_SUCCESS));

    }



    protected void initUI() {
        findViewById(R.id.header).setBackgroundColor(ContextCompat.getColor(this, R.color.purple27));
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        TextView mRegister = (TextView) findViewById(R.id.right_text);
        mRegister.setTextColor(ContextCompat.getColor(this, R.color.white));
        mTitle.setText(R.string.login);
        mRegister.setText(R.string.register);

        ImageView mBack = (ImageView) findViewById(R.id.go_back);
        mBack.setImageResource(R.drawable.back);
        mBack.setVisibility(View.GONE);

        mRegister.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                RegisterActivity.goToPage(LoginActivity.this);
            }
        });
        //微信授权
        findViewById(R.id.auth_weixin).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AuthUtils.doAuth(LoginActivity.this, AuthUtils.MM_AUTH_CODE, new OnAuthCallbackListener() {
                    @Override
                    public void onAuthSuccess() {
                        UIUtils.toastMsgByStringResource(R.string.auth_success);
                        AuthLogin();
                    }

                    @Override
                    public void onAuthFail(String msg) {
                        if (TextUtils.isEmpty(msg)) {
                            UIUtils.toastMsgByStringResource(R.string.auth_failed);
                        } else {
                            UIUtils.toastMsg(msg);
                        }
                    }

                    @Override
                    public void onCancel() {
                        UIUtils.toastMsgByStringResource(R.string.auth_cancel);
                    }
                });
            }
        });

        //qq授权
        findViewById(R.id.auth_qq).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AuthUtils.doAuth(LoginActivity.this, AuthUtils.QQ_AUTH_CODE, new OnAuthCallbackListener() {
                    @Override
                    public void onAuthSuccess() {
                        UIUtils.toastMsgByStringResource(R.string.auth_success);
                        AuthLogin();
                    }

                    @Override
                    public void onAuthFail(String msg) {
                        if (TextUtils.isEmpty(msg)) {
                            UIUtils.toastMsgByStringResource(R.string.auth_failed);
                        } else {
                            UIUtils.toastMsg(msg);
                        }
                    }

                    @Override
                    public void onCancel() {
                        UIUtils.toastMsgByStringResource(R.string.auth_cancel);
                    }
                });
            }
        });
        findViewById(R.id.auth_mobile).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MobileLoginActivity.goToPage(LoginActivity.this, redirectToMain, mJson);
            }
        });
    }

    private void AuthLogin() {
        //登录成功之后记得调用init接口更新用户配置
        setInitFlag(true);
        if (redirectToMain) {
            if (!SPUtils.getBoolleanValue(YSRLConstants.HAS_ENTER_INFO)) {
                SetInfoActivity.goToPage(LoginActivity.this);
            } else {
                MainActivity.goToPage(LoginActivity.this);
            }
        } else {
            if (!TextUtils.isEmpty(mJson)) {
                Intent intent = new Intent();
                intent.putExtra(IntentExtraConfig.EXTRA_JSON, mJson);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_OK);
            }
        }
        finish();
    }

    @Override
    public boolean onClickBackBtnEvent() {
        //直接返回false，因为mainActivity未启动会造成重复的登陆界面
        return true;
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        SSOSinaUtils.onActivityResult(requestCode, resultCode, data);
        // qq 授权回调
        SSOTencentUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPage(Context context) {
        goToPage(context, false);
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPage(Context context, boolean redirectToMain) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_REDIRECT_MAIN, redirectToMain);
        context.startActivity(intent);
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPageWithResult(BaseActivity baseActivity, boolean redirectToMain, String json) {
        Intent intent = new Intent(baseActivity, LoginActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_REDIRECT_MAIN, redirectToMain);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, json);
        baseActivity.startActivityForResult(intent, RequestCodeConfig.REQUEST_CODE_LOGIN);
    }
}