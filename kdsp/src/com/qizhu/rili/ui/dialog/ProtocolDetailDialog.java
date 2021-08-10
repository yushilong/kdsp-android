package com.qizhu.rili.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;


public class ProtocolDetailDialog extends Dialog {
    private ViewHolder holder;
    private Context context;

    public ProtocolDetailDialog(Context context) {
        this(context, R.style.general_dialog);
    }

    public ProtocolDetailDialog(Context context, int theme) {
        super(context, theme);
        holder = new ViewHolder();
        this.context = context;
        View view = View.inflate(context, R.layout.protocol_detail_dia, null);
        holder.init(view);
        setContentView(view);
        setGravity();
        setWidthHeight();
        initWebSetting();
//        setCanceledOnTouchOutside(false);
    }

    private void initWebSetting() {
        WebSettings mWebSettings = holder.wv.getSettings();
        mWebSettings.setJavaScriptEnabled(true);      //可用JS
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);    //设置js可以直接打开窗口，如window.open()
        mWebSettings.setDomStorageEnabled(true);          //支持DOM Storage
        mWebSettings.setUseWideViewPort(true);            //使用推荐窗口
        mWebSettings.setLoadWithOverviewMode(true);       //设置加载模式
        mWebSettings.setUseWideViewPort(true); // 将图片调整到适合 WebView 的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
// 缩放操作
        mWebSettings.setSupportZoom(true); // 支持缩放，默认为 true
        mWebSettings.setBuiltInZoomControls(true); // 设置内置的缩放控件，若为 false，则该 WebView 不可缩放
        mWebSettings.setDisplayZoomControls(false); // 隐藏原生的缩放控件
//        int screenDensity = context.getResources().getDisplayMetrics().densityDpi;
//        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
//        switch (screenDensity) {
//            case DisplayMetrics.DENSITY_LOW:
//                zoomDensity = WebSettings.ZoomDensity.CLOSE;
//                break;
//            case DisplayMetrics.DENSITY_MEDIUM:
//                zoomDensity = WebSettings.ZoomDensity.MEDIUM;
//                break;
//            case DisplayMetrics.DENSITY_HIGH:
//                zoomDensity = WebSettings.ZoomDensity.FAR;
//                break;
//        }
//        mWebSettings.setDefaultZoom(zoomDensity);

//        mWebSettings.setTextZoom(350);

    }


    /**
     * 居中
     */
    private void setGravity() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
        }
    }


    /**
     * 横向全屏
     */
    private void setWidthHeight() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            int width = wm.getDefaultDisplay().getWidth() * 4 / 5;
            int height = wm.getDefaultDisplay().getHeight() * 3/ 4;
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            holder.protocolRl.setLayoutParams(lp);
        }
    }

    public void setTitleContent(int titleId,String url){
        holder.titleTv.setText(titleId);
//        String url = "file:///android_asset/" + "kdsp_policy.html";
//        holder.wv.loadData(context.getString(contentId), "text/html",
//                "UTF-8");
        holder.wv.loadUrl(url);
    }

    public class ViewHolder {
        RelativeLayout protocolRl;
        TextView titleTv;
        WebView wv;

        public void init(View view) {
            titleTv = view.findViewById(R.id.title_tv);
            protocolRl = view.findViewById(R.id.pro_rl);
            wv = view.findViewById(R.id.content_wv);
        }
    }


}
