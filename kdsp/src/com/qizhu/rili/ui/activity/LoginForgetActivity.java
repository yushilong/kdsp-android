package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.RegexUtil;
import com.qizhu.rili.utils.SmsCode;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.TimeTextView;

import org.json.JSONObject;

public class LoginForgetActivity extends BaseActivity {
    private TextView mTitle;                    //标题
    private EditText mEditPhone;                //手机号码
    private EditText mEditCode;                 //验证码
    private EditText mEditPassword;             //密码
    private EditText mConfirmPassword;          //确认密码
    private TimeTextView mBtnCode;              //验证码倒计时

    //获取验证码
    SmsCode code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_forget);
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

        mTitle.setText(R.string.find_password);
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
                    mBtnCode.setBackground(ContextCompat.getDrawable(LoginForgetActivity.this,R.drawable.round_purple1));
                    mBtnCode.setTextColor(ContextCompat.getColor(LoginForgetActivity.this,R.color.white));
                }else {
                    mBtnCode.setEnabled(false);
                    mBtnCode.setBackground(ContextCompat.getDrawable(LoginForgetActivity.this,R.drawable.round_gray41));
                    mBtnCode.setTextColor(ContextCompat.getColor(LoginForgetActivity.this,R.color.gray9));
                }

            }
        });

        mBtnCode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String mobile = mEditPhone.getText().toString();
                if (!TextUtils.isEmpty(mobile)) {
                    if (RegexUtil.isMobileNumber(mobile)) {
                        mBtnCode.setTimes(DateUtils.convertTimeToDHMS(60), TimeTextView.TYPE_CODE);
                        mBtnCode.setEnabled(false);
                        mBtnCode.setBackground(ContextCompat.getDrawable(LoginForgetActivity.this,R.drawable.round_gray41));
                        mBtnCode.setTextColor(ContextCompat.getColor(LoginForgetActivity.this,R.color.gray9));
                        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                mBtnCode.ComputeTime(TimeTextView.TYPE_CODE);
                            }

                            @Override
                            public void onFinish() {
                                mBtnCode.setEnabled(true);
                                mBtnCode.setText("重新获取");
                                mBtnCode.setBackground(ContextCompat.getDrawable(LoginForgetActivity.this,R.drawable.round_purple1));
                                mBtnCode.setTextColor(ContextCompat.getColor(LoginForgetActivity.this,R.color.white));
                            }
                        };
                        countDownTimer.start();
                        KDSPApiController.getInstance().editPasswordCode(mobile, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                showFailureMessage(error);
                            }
                        });
                    } else {
                        UIUtils.toastMsg("无此号码,请重新输入~");
                    }
                } else {
                    UIUtils.toastMsg("手机号不能为空");
                }
            }
        });
        findViewById(R.id.btn_commit).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                final String username = mEditPhone.getText().toString();
                String code = mEditCode.getText().toString();
                final String password = mEditPassword.getText().toString();
                String confirmPass = mConfirmPassword.getText().toString();
                if (!TextUtils.isEmpty(username)) {
                    if (!TextUtils.isEmpty(password)) {
                        if (password.length() >= 6) {
                            if (RegexUtil.isMobileNumber(username)) {
                                if (!TextUtils.isEmpty(code)) {
                                    if (password.equals(confirmPass)) {
                                        KDSPApiController.getInstance().editPassword(code, username, password, new KDSPHttpCallBack() {
                                            @Override
                                            public void handleAPISuccessMessage(final JSONObject response) {
                                                UIUtils.toastMsg("修改成功！");
                                                LoginForgetActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        KDSPApiController.getInstance().userLogin(username, password, new KDSPHttpCallBack() {
                                                            @Override
                                                            public void handleAPISuccessMessage(JSONObject response) {
                                                                User user = User.parseObjectFromJSON(response.optJSONObject("user"));
                                                                if (user != null) {
                                                                    AppContext.doAfterLogin(user);

                                                                    MainActivity.goToPage(LoginForgetActivity.this);
                                                                    finish();
                                                                }
                                                            }

                                                            @Override
                                                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                                                showFailureMessage(error);
                                                            }
                                                        });
                                                    }
                                                });

                                            }

                                            @Override
                                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                                showFailureMessage(error);
                                            }
                                        });
                                    } else {
                                        UIUtils.toastMsg("输入密码不一致");
                                    }
                                } else {
                                    UIUtils.toastMsg("亲，请先输入验证码");
                                }
                            } else {
                                UIUtils.toastMsg("无此号码,请重新输入~");
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

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, LoginForgetActivity.class);
        context.startActivity(intent);
    }
}
