package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.CalendarUtil;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 9/28/15.
 * 分析小伙伴的activity
 */
public class AnalysisFriendActivity extends BaseActivity {
    private TextView mElements;         //五行
    private TextView mStar;             //星宿
    private TextView mXiGod;            //喜用神
    private DateTime mDateTime = new DateTime();         //日期
    private int mUserSex;               //用户的性别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDateTime = getIntent().getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        mUserSex = getIntent().getIntExtra(IntentExtraConfig.EXTRA_USER_SEX, User.BOY);
        setContentView(R.layout.analysis_friend_lay);
        initUI();
    }

    private void initUI() {
        mElements = (TextView) findViewById(R.id.elements);
        mStar = (TextView) findViewById(R.id.star);
        mXiGod = (TextView) findViewById(R.id.xi_god);
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.analysis_friend);
        String mElement = CalendarCore.getElementName(mDateTime);
        mElements.setText("五行：" + mElement);
        mStar.setText("星宿：" + CalendarCore.getStellarName(mDateTime));
        mXiGod.setText("喜用神：" + CalendarUtil.getXiGod(mElement));

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.life).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LifeActivity.goToPage(AnalysisFriendActivity.this, mUserSex, mDateTime, 0, false);
            }
        });
        findViewById(R.id.star_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                StarActivity.goToPage(AnalysisFriendActivity.this, mUserSex, mDateTime, 0, false);
            }
        });
        findViewById(R.id.shadow).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ShadowActivity.goToPage(AnalysisFriendActivity.this, mUserSex, mDateTime, 0, false);
            }
        });
        findViewById(R.id.lifetime).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LifeTimeActivity.goToPage(AnalysisFriendActivity.this, mUserSex, mDateTime, 0, false);
            }
        });

        ImageView mStarBg = (ImageView) findViewById(R.id.star_bg);
        MethodCompat.setBackground(mStarBg, new BitmapDrawable(mResources, ImageUtils.getResourceBitMap(this, R.drawable.star_bg)));
        mStarBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.star_alpha));
        ImageView mInfoBg = (ImageView) findViewById(R.id.elements_bg);
        mInfoBg.setImageBitmap(ImageUtils.getResourceBitMap(this, R.drawable.analysis_info));
    }

    public static void goToPage(Context context, DateTime dateTime, int mSex) {
        Intent intent = new Intent(context, AnalysisFriendActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        intent.putExtra(IntentExtraConfig.EXTRA_USER_SEX, mSex);
        context.startActivity(intent);
    }
}
