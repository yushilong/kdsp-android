package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.YSRLNavigationByUrlUtils;

/**
 * 消息通过跳转的中间Actiivty
 */
public class NewsAgentActivity extends BaseActivity {
    public static final String EXTRA_NOTICEID = "extra_noticeId";               //通知id
    public static final String EXTRA_NOTICE = "extra_notice";                   //通知对象
    public static final String EXTRA_NEED_STATISTICS = "extra_need_statistics"; //是否需要统计
    public static final String EXTRA_LINK = "extra_link"; //链接

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String mNoticeId = intent.getStringExtra(EXTRA_NOTICEID);
        boolean mNeedStatistics = intent.getBooleanExtra(EXTRA_NEED_STATISTICS, false);
        String mLink = intent.getStringExtra(EXTRA_LINK);

        //更新指定消息的已读状态（包括本地数据库中指定消息的已读状态，和调用服务器的read接口）
//        updateReadState(this, mNews);
        if (!TextUtils.isEmpty(mLink)) {
            YSRLNavigationByUrlUtils.navigate(mLink, this, true, false);
        }

        finish();
    }

    /**
     * 跳转到当前页
     */
    public static void goToPage(Context context, String url) {
        Intent intent = new Intent(context, NewsAgentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_LINK, url);
        context.startActivity(intent);
    }

}
