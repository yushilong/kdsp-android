package com.qizhu.rili.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.ui.activity.MainActivity;
import com.qizhu.rili.ui.activity.NewsAgentActivity;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.WeixinUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


//实现IWXAPIEventHandler接口，微信发送的请求将回调到onReq方法，发送到微信请求的响应结果将回调到onResp方法
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d("---> WXEntryActivity onCreate");
        WeixinUtils.getInstance().handleIntent(getIntent(),this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        LogUtils.d("---> WXEntryActivity onReq " + req.getType());
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        LogUtils.d("---> WXEntryActivity onResp " + resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp instanceof SendAuth.Resp) {
                    SendAuth.Resp temp = (SendAuth.Resp) resp;
                    WeixinUtils.getInstance().authSuccess(temp.code, temp.state);
                } else {
                    WeixinUtils.getInstance().shareWXRespSucceed();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (resp instanceof SendAuth.Resp) {
                    WeixinUtils.getInstance().authDenied("授权取消");
                } else {
                    WeixinUtils.getInstance().shareWXRespFailed("分享取消");
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                if (resp instanceof SendAuth.Resp) {
                    WeixinUtils.getInstance().authDenied("授权失败");
                } else {
                    WeixinUtils.getInstance().shareWXRespFailed("授权失败");
                }
                break;
            default:
                break;
        }
        finish();
    }

    //  向微信发送消息，那么直接跳转到主界面让用户分享
    private void goToGetMsg() {
        MainActivity.goToPage(this);
        finish();
    }

    // 解析收到的消息跳转指定页面
    private void goToShowMsg(ShowMessageFromWX.Req showReq) {
        WXMediaMessage wxMsg = showReq.message;
        WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

        //如果有跳转目标，则跳转，否则跳转到主页面
        if (obj != null && !TextUtils.isEmpty(obj.extInfo)) {
            Intent intent = new Intent(this, NewsAgentActivity.class);
            intent.putExtra(NewsAgentActivity.EXTRA_LINK, obj.extInfo);
            startActivity(intent);
        } else {
            MainActivity.goToPage(this);
        }

        finish();
    }
}
