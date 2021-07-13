package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.controller.StatisticsConstant;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.ShareUtils;

/**
 * 今日详情
 */
public class TodayDetailActivity extends BaseActivity {
    private DateTime nowtime;
    private ListView mContent;
    private String[] str;
    private String mDayTitle;
    private String mDaySummary;
    private String daycontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_details);
        DateTime data = getIntent().getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        if (data != null) {
            nowtime = data;
        } else {
            nowtime = new DateTime();
        }
        initUI();
    }

    protected void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.today_notice);
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        findViewById(R.id.share_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goToShare();
            }
        });
        findViewById(R.id.share_tip).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goToShare();
            }
        });
        if (AppContext.mUser != null) {
            mDayTitle = CalendarCore.getDayTitle(nowtime, new DateTime(AppContext.mUser.birthTime), AppContext.mUser.userSex == User.BOY);
            mDaySummary = CalendarCore.getDaySummary(nowtime, new DateTime(AppContext.mUser.birthTime), AppContext.mUser.userSex == User.BOY);
            mContent = (ListView) findViewById(R.id.content);
            daycontent = CalendarCore.getDayDetail(nowtime, new DateTime(AppContext.mUser.birthTime), AppContext.mUser.userSex == User.BOY);
            str = daycontent.split("。");
            mContent.setAdapter(new Myadapter());
        }
    }

    class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return str.length > 5 ? 5 : str.length;
        }

        @Override
        public Object getItem(int position) {
            return str[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold vh;
            if (convertView == null) {
                vh = new ViewHold();
                convertView = View.inflate(TodayDetailActivity.this, R.layout.detail_item_lay, null);
                vh.mText = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(vh);
            } else {
                vh = (ViewHold) convertView.getTag();
            }
            if (position > 3) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 4; i < str.length; i++) {
                    stringBuilder.append(str[i]);
                }
                vh.mText.setText(stringBuilder);
            } else {
                vh.mText.setText(str[position]);
            }
            return convertView;
        }

        class ViewHold {
            TextView mText;
        }
    }

    private void goToShare() {
        ShareActivity.goToShare(TodayDetailActivity.this, ShareUtils.getShareTitle(ShareUtils.Share_Type_DAY_DETAIL, mDayTitle),
                ShareUtils.getShareContent(ShareUtils.Share_Type_DAY_DETAIL, mDaySummary), getShareUrl(), "", ShareUtils.Share_Type_DAY_DETAIL, StatisticsConstant.subType_Daily_Desc);
    }

    private String getShareUrl() {
        return ShareUtils.BASE_SHARE_URL + "?userId=" + AppContext.userId + "&shareType=4" + "&shareDate=" + DateUtils.getServerTimeFormatDate(nowtime.getDate());
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, TodayDetailActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, DateTime date) {
        Intent intent = new Intent(context, TodayDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, date);
        context.startActivity(intent);
    }
}
