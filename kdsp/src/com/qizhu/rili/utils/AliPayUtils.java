package com.qizhu.rili.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.ui.activity.BaseActivity;

import org.json.JSONObject;

/**
 * Created by lindow on 20/12/2016.
 * 支付宝的支付封装
 */
public class AliPayUtils {
    private static final int SDK_PAY_FLAG = 1;

    private Handler mAliHandler;
    private BaseActivity mAliActivity;

    private static AliPayUtils mInstance = new AliPayUtils();

    private AliPayUtils() {
        mAliHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        PayResult payResult = new PayResult((String) msg.obj);
                        /**
                         * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                         * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                         * docType=1) 建议商户依赖异步通知
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            LogUtils.d("-----支付成功time:" +new DateTime().toString());
                            payAliRespSucceed(true, "支付成功！");

                        } else {
                            LogUtils.d("-----支付失败:" +new DateTime().toString());
                            payAliRespSucceed(false, "支付失败！");


                        }

                        break;
                    }
                    default:
                        break;
                }
            }
        };
    }

    public static AliPayUtils getInstance() {
        if (mInstance == null) {
            mInstance = new AliPayUtils();
        }
        return mInstance;
    }

    /**
     * 微信支付
     *
     * @param context 上下文
     * @param data    数据
     */
    public void startPay(BaseActivity context, final JSONObject data) {
        mAliActivity = context;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mAliActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(data.optString("sign"), true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mAliHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void payAliRespSucceed(boolean isSuccess, String errStr) {
        if (mAliActivity != null) {
            mAliActivity.payAliSuccessd(isSuccess, errStr);
        }
    }
}
