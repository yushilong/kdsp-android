package com.qizhu.rili.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

public class SmsCode {
    /**
     * 上下文
     */
    private Context context;
    /**
     * 填写验证码的输入文本框
     */
    private EditText et;
    /**
     * 利用广播，接受短信
     */
    private BroadcastReceiver smsReceiver;
    private IntentFilter filter2;
    /**
     * 通过线程，输入验证码
     */
    private Handler handler;
    private String strContent;
    /**
     * 正则匹配短信里的6为连续的数字(验证码)
     */
    private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";

    /**
     * @param context 上下文
     * @param et      填写验证码的编辑文本框EditText
     */
    public SmsCode(Context context, EditText et) {
        super();
        this.context = context;
        this.et = et;
        filter2 = new IntentFilter();
        filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter2.setPriority(Integer.MAX_VALUE);
    }

    /**
     * 启动广播，利用广播接受短信
     */
    public void start() {
        try {

            /**
             * 线程更新UI
             */
            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    et.setText(strContent);
                }
            };
            /**
             * 广播，获取短信
             */
            smsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Object[] objs = (Object[]) intent.getExtras().get("pdus");
                    for (Object obj : objs) {
                        byte[] pdu = (byte[]) obj;
                        SmsMessage sms = SmsMessage.createFromPdu(pdu);
                        // 短信的内容
                        String message = sms.getMessageBody();
                        Log.d("logo", "message     " + message);
                        // 获取短信的手机号码，该号码一般都是+86开头
                        String from = sms.getOriginatingAddress();
                        if (!TextUtils.isEmpty(from)) {
                            String code = patternCode(message);
                            if (!TextUtils.isEmpty(code)) {
                                strContent = code;
                                handler.sendEmptyMessage(1);
                            }
                        }
                    }
                }
            };
            context.registerReceiver(smsReceiver, filter2);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 关闭广播
     */
    public void end() {
        try {
            context.unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    /**
     * 匹配短信中的6个连续数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
