package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.UIUtils;

/**
 * 八字姻缘分享
 */
public class MarraigeShareActivity extends BaseActivity {
    private  View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marriage_share);
        initUI();

    }

    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        TextView rightText = (TextView) findViewById(R.id.right_text);
        rightText.setText(R.string.save_pic);
        view = findViewById(R.id.marriage_llay);


        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareActivity.mShareBitmap = UIUtils.getViewBitmap(view);
                ShareActivity.goToShareImage(MarraigeShareActivity.this, "", "", "", "", ShareUtils.Share_Type_QR_CODE, "");
            }
        });



    }




    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MarraigeShareActivity.class);
        context.startActivity(intent);
    }
}
