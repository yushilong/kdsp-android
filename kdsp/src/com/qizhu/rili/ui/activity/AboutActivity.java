package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.BuildConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.OperUtils;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.UIUtils;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {
    long[] mHits = new long[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        TextView mVersion = (TextView) findViewById(R.id.version);
        mTitle.setText(R.string.about_text);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
//                FengShuiReportActivity.goToPage(AboutActivity.this);
            }
        });
        findViewById(R.id.share_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OperUtils.mSmallCat = OperUtils.SMALL_CAT_OTHER;
                OperUtils.mKeyCat = OperUtils.KEY_CAT_APP;
                ShareActivity.goToShareApp(AboutActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_APP, ""), ShareUtils.getShareContent(ShareUtils.Share_Type_APP, ""),
                        ShareUtils.DEFAULT_DOWNLOAD_URL, StatisticsConstant.TYPE_APP_SHARE, StatisticsConstant.subType_About_us);
            }
        });

        mVersion.setText(AppContext.version);
        //加入渠道号显示彩蛋
        findViewById(R.id.icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    UIUtils.toastMsg(AppContext.userId + "\n" + AppContext.channel + " " + BuildConfig.ENV + " " + AppContext.versionCode);
                }
            }
        });
        findViewById(R.id.icon).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MethodCompat.copyText(AppContext.userId);
                UIUtils.toastMsg("已复制当前用户的userId");
                return false;
            }
        });
        findViewById(R.id.qq_group).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MethodCompat.copyText("366972105");
                UIUtils.toastMsg("已复制群号码");
                return false;
            }
        });
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }
}
