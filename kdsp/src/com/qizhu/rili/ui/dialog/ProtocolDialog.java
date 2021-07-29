package com.qizhu.rili.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.R;


public class ProtocolDialog extends Dialog {
    private ViewHolder holder;
    private Context context;

    private IProtocolClickListener listener;

    public ProtocolDialog(Context context) {
        this(context, R.style.general_dialog, null);
    }

    public ProtocolDialog(Context context, IProtocolClickListener listener) {
        this(context, R.style.general_dialog, listener);
    }

    public ProtocolDialog(final Context context, int theme, IProtocolClickListener listener) {
        super(context, theme);
        holder = new ViewHolder();
        this.context = context;
        View view = View.inflate(context, R.layout.protocol_dia, null);
        holder.init(view);
        setContentView(view);
        setGravity();
        this.listener = listener;
        setCanceledOnTouchOutside(false);
        holder.contentTv.setText(generateLink(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ProtocolDetailDialog protocolDetailDialog = new ProtocolDetailDialog(context);
                String url = "http://jp.ixiangxue.cn/yc-mgt-web/www/yonghu_kdsp.html";
                protocolDetailDialog.setTitleContent(R.string.user_service_title,url);
                protocolDetailDialog.show();
            }
        }, new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ProtocolDetailDialog protocolDetailDialog = new ProtocolDetailDialog(context);
                String url = "http://jp.ixiangxue.cn/yc-mgt-web/www/yinsi_kdsp.html";
                protocolDetailDialog.setTitleContent(R.string.user_policy_title,url);
                protocolDetailDialog.show();
            }
        }));
        holder.contentTv.setMovementMethod(LinkMovementMethod.getInstance());
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


    protected SpannableString generateLink(ClickableSpan serviceClickSpan,ClickableSpan  policyClickSpan) {
        String msg = context.getString(R.string.service_policy_description);
        SpannableString tcPsSpan = new SpannableString(msg);
        int serviceStartIndex = msg.indexOf("《");
        int serviceEndIndex = msg.indexOf("和");
        int policyStartIndex = msg.indexOf("《", serviceEndIndex);
        int policyEndIndex = msg.indexOf("》", serviceEndIndex);
        Log.e("---->",serviceStartIndex+";"+serviceEndIndex+";"+policyStartIndex+";"+policyEndIndex);
        ForegroundColorSpan blueTextSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.color_107FE0));
        tcPsSpan.setSpan(blueTextSpan, serviceStartIndex, serviceEndIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tcPsSpan.setSpan(serviceClickSpan, serviceStartIndex, serviceEndIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tcPsSpan.setSpan(blueTextSpan, policyStartIndex, policyEndIndex+1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tcPsSpan.setSpan(policyClickSpan, policyStartIndex, policyEndIndex+1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return tcPsSpan;
    }

//    /**
//     * 横向全屏
//     */
//    private void setWidthHeight() {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        if (wm != null) {
//            int width = wm.getDefaultDisplay().getWidth() * 4 / 5;
////            int height = wm.getDefaultDisplay().getHeight() * 2 / 5;
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, holder.protocolRl.getHeight());
//            holder.protocolRl.setLayoutParams(lp);
//        }
//    }


    public class ViewHolder {
        RelativeLayout protocolRl;
        TextView refuseTv, agreeTv,contentTv;

        public void init(View view) {
            protocolRl = view.findViewById(R.id.smart_pro_rl);
            refuseTv = view.findViewById(R.id.refuse_tv);
            agreeTv = view.findViewById(R.id.agree_tv);
            contentTv = view.findViewById(R.id.content_tv);

            refuseTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(refuseTv);
                }
            });

            agreeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(agreeTv);
                }
            });
        }
    }

    public IProtocolClickListener getListener() {
        return listener;
    }

    public void setListener(IProtocolClickListener listener) {
        this.listener = listener;
    }

    public interface IProtocolClickListener {
        void onClick(View view);
    }
}
