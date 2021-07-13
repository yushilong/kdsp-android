package com.qizhu.rili.wxapi;

import android.os.Bundle;

import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.WeixinUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


/**
 * 微信支付反馈的activity
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d("---> WXPayEntryActivity onCreate");
        WeixinUtils.getInstance().handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.d("onPayFinish, errCode = " + baseResp.errCode);
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    WeixinUtils.getInstance().payWXRespSucceed(true,"支付成功！");
                    break;
                default:
                    WeixinUtils.getInstance().payWXRespSucceed(false,"支付失败！");
                    break;
            }
        }
        finish();
    }
}
