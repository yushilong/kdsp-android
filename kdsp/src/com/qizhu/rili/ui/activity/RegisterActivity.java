package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.RegexUtil;
import com.qizhu.rili.utils.SmsCode;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.TimeTextView;

import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {
    private TextView mTitle;                    //标题
    private EditText mEditPhone;                //手机号码
    private EditText mEditCode;                 //验证码
    private EditText mEditPassword;             //密码
    private EditText mConfirmPassword;          //确认密码
    private TimeTextView mBtnCode;              //验证码倒计时

    private SmsCode code;       //获取验证码
    private int mType ;          //0注册 1绑定 2修改

    private Toast mToast;                           //单独的toast
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register);
        mType = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE,0);
        initUI();
        code = new SmsCode(this, mEditCode);
        code.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        code.end();
    }

    protected void initUI() {
        mTitle = (TextView) findViewById(R.id.title_txt);

        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditCode = (EditText) findViewById(R.id.edit_code);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        mBtnCode = (TimeTextView) findViewById(R.id.btn_code);
        if (mType == 1) {
            mTitle.setText(R.string.bind_phone);
        } else if(mType == 2) {
            mTitle.setText(R.string.change_phone);
        }else{
            mTitle.setText(R.string.register);
        }

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });


        mEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 11){
                    mBtnCode.setEnabled(true);
                    mBtnCode.setBackground(ContextCompat.getDrawable(RegisterActivity.this,R.drawable.round_purple1));
                    mBtnCode.setTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.white));
                }else {
                    mBtnCode.setEnabled(false);
                    mBtnCode.setBackground(ContextCompat.getDrawable(RegisterActivity.this,R.drawable.round_gray41));
                    mBtnCode.setTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.gray9));
                }

            }
        });


        mBtnCode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                String mPhone = mEditPhone.getText().toString();
                if (!TextUtils.isEmpty(mPhone)) {
                    if (RegexUtil.isMobileNumber(mPhone)) {
                        mBtnCode.setTimes(DateUtils.convertTimeToDHMS(60), TimeTextView.TYPE_CODE);
                        mBtnCode.setEnabled(false);
                        mBtnCode.setBackground(ContextCompat.getDrawable(RegisterActivity.this,R.drawable.round_gray41));
                        mBtnCode.setTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.gray9));
                         countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                mBtnCode.ComputeTime(TimeTextView.TYPE_CODE);
                            }

                            @Override
                            public void onFinish() {
                                mBtnCode.setBackground(ContextCompat.getDrawable(RegisterActivity.this,R.drawable.round_purple1));
                                mBtnCode.setTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.white));
                                mBtnCode.setEnabled(true);
                                mBtnCode.setText("重新获取验证码");
                            }
                        };
                        countDownTimer.start();
                        if (mType == 1 || mType == 2)
                            KDSPApiController.getInstance().sendBindTelCode(mPhone, new KDSPHttpCallBack() {
                                @Override
                                public void handleAPISuccessMessage(JSONObject response) {
                                }

                                @Override
                                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            countDownTimer.cancel();
                                            stopTime();
                                        }
                                    });
                                    showFailureMessage(error);
                                }
                            });
                        else if (mType == 0){
                            KDSPApiController.getInstance().createCode(mPhone, new KDSPHttpCallBack() {
                                @Override
                                public void handleAPISuccessMessage(JSONObject response) {
                                }

                                @Override
                                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                    showFailureMessage(error);
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            countDownTimer.cancel();
                                            stopTime();
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        UIUtils.toastMsg("无此号码请重新输入");
                    }
                } else {
                    UIUtils.toastMsg("手机号不能为空");
                }
            }
        });

        findViewById(R.id.btn_commit).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                final String mPhone = mEditPhone.getText().toString();
                String password = mEditPassword.getText().toString();
                String confirmPass = mConfirmPassword.getText().toString();
                String mCode = mEditCode.getText().toString();
                if (!TextUtils.isEmpty(mPhone)) {
                    if (!TextUtils.isEmpty(password)) {
                        if (password.length() >= 6) {
                            if (RegexUtil.isMobileNumber(mPhone)) {
                                if (!TextUtils.isEmpty(mCode)) {
                                    if (password.equals(confirmPass)) {
                                        showLoadingDialog();
                                        if (mType == 1) {
                                            KDSPApiController.getInstance().bindTelAndMergeUserMsg(mCode, mPhone, password, new KDSPHttpCallBack() {
                                                @Override
                                                public void handleAPISuccessMessage(JSONObject response) {
                                                    if (response != null) {
                                                        User user = User.parseObjectFromJSON(response.optJSONObject("user"));
                                                        AppContext.doAfterLogin(user);
                                                    }
                                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dismissLoadingDialog();
                                                            showBindToast();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                                    showFailureMessage(error);
                                                    dismissLoadingDialog();
                                                }
                                            });
                                        } else if (mType == 0) {
                                            KDSPApiController.getInstance().register(mCode, mPhone, password, new KDSPHttpCallBack() {
                                                @Override
                                                public void handleAPISuccessMessage(JSONObject response) {
                                                    if (response != null) {
                                                        User user = User.parseObjectFromJSON(response.optJSONObject("user"));
                                                        AppContext.doAfterLogin(user);
                                                        UIUtils.toastMsg("注册成功！");
                                                        SetInfoActivity.goToPage(RegisterActivity.this);
                                                        finish();
                                                    }
                                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dismissLoadingDialog();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                                    showFailureMessage(error);
                                                    dismissLoadingDialog();
                                                }
                                            });
                                        }else  if(mType == 2){
                                            KDSPApiController.getInstance().updatePhoneNum(mCode, mPhone, password, new KDSPHttpCallBack() {
                                                @Override
                                                public void handleAPISuccessMessage(JSONObject response) {
                                                   if(AppContext.mUser != null){
                                                       AppContext.mUser.telephoneNumber = mPhone;
                                                   }
                                                    UIUtils.toastMsg("修改成功！");
                                                }

                                                @Override
                                                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                                    showFailureMessage(error);
                                                    dismissLoadingDialog();
                                                }
                                            });
                                        }
                                    } else {
                                        UIUtils.toastMsg("输入密码不一致");
                                    }
                                } else {
                                    UIUtils.toastMsg("亲，请先输入验证码");
                                }
                            } else {
                                UIUtils.toastMsg("亲，你输入的号码不合理");
                            }
                        } else {
                            UIUtils.toastMsg("亲，密码长度不能少于6位数！");
                        }
                    } else {
                        UIUtils.toastMsg("亲，密码不能为空!");
                    }
                } else {
                    UIUtils.toastMsg("亲，手机号不能为空！");
                }
            }
        });


    }

    private void stopTime() {

        mBtnCode.setText(getString(R.string.get_verification_code));
        mBtnCode.setEnabled(true);
        mBtnCode.setBackground(ContextCompat.getDrawable(RegisterActivity.this,R.drawable.round_purple1));
        mBtnCode.setTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.white));
    }

    @Override
    public boolean onClickBackBtnEvent() {
        //直接返回false，因为mainActivity未启动会造成重复的登陆界面
        return false;
    }

    /**
     * 显示绑定成功提示并跳转
     */
    private void showBindToast() {
        if (mToast == null) {
            mToast = new Toast(AppContext.baseContext);
            LayoutInflater inflater = LayoutInflater.from(AppContext.baseContext);
            View view = inflater.inflate(R.layout.toast, null);
            if (view != null) {
                TextView text = (TextView) view.findViewById(R.id.toast_text);
                text.setText("绑定账号后,以手机号为准");
                mToast.setView(view);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            }
        }

        mToast.show();
        LogUtils.d("---> start time ");

        mTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mToast.cancel();
                setResult(RESULT_OK);
                finish();
                LogUtils.d("---> end time ");
            }
        }, 1000);
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转至登录页面
     */
    public static void goToPageWithResult(BaseActivity activity, int mode) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mode);
        activity.startActivityForResult(intent, RequestCodeConfig.REQUEST_BIND_PHONE_SUCCESS);
    }
}
