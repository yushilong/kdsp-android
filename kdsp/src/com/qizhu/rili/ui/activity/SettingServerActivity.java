package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 12/29/15.
 * 服务器设置activity
 */
public class SettingServerActivity extends BaseActivity {
    private RadioGroup serverListRadio;
    private EditText serverEditText;
    private TextView chooseServerUrlTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_server_lay);

        serverListRadio = (RadioGroup) findViewById(R.id.server_list);
        serverEditText = (EditText) findViewById(R.id.server_text);
        chooseServerUrlTextView = (TextView) findViewById(R.id.choose_server_url_txt);

        chooseServerUrlTextView.setText(AppConfig.API_BASE);

        TextView titleText = (TextView) findViewById(R.id.title_txt);
        titleText.setText("服务器设置");

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.complete).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                saveInfo();
            }
        });
        serverListRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                chooseServerUrlTextView.setText(getServerUrl());
            }
        });
        serverEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (serverListRadio.getCheckedRadioButtonId() == R.id.custom) {
                    chooseServerUrlTextView.setText("http://" + charSequence + "/");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 保存信息,修改只对本次生效，下次应用重启仍然恢复为默认的服务器环境
     */
    public void saveInfo() {
        String newBaseUrl = getServerUrl();

        String oldBaseUrl = AppConfig.API_BASE;
        if (!oldBaseUrl.equals(newBaseUrl)) {
            AppConfig.API_URL = newBaseUrl;
            UIUtils.toastMsg("修改只对本次生效，下次应用重启仍然恢复为默认的服务器环境~");
            LoginActivity.goToPage(SettingServerActivity.this, true);
        }
    }

    public String getServerUrl() {
        int checkedId = serverListRadio.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.none:
                return AppConfig.API_BASE;
            case R.id.debug:
                return "http://test.ishenpo.com/";
            case R.id.release:
                return "http://api.ishenpo.com:8080/Fortune-Calendar/";
            case R.id.custom:
                return "http://" + serverEditText.getText().toString() + "/";
        }
        return "";
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, SettingServerActivity.class);
        context.startActivity(intent);
    }
}
