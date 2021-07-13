package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.SPUtils;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 15/9/19.
 * 闪屏activity
 */
public class SplashActivity extends BaseActivity {
    private static final int MSG_GO_TO_MAIN = 1;        //跳转主页
    private ImageView mSplashImage;                     //闪屏图片
    private TextView mText1;                            //文字1
    private TextView mText2;                            //文字2
    private TextView mText3;                            //文字3
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_lay);
        mSplashImage = (ImageView) findViewById(R.id.splash_image);
        mText1 = (TextView) findViewById(R.id.text1);
        mText2 = (TextView) findViewById(R.id.text2);
        mText3 = (TextView) findViewById(R.id.text3);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_GO_TO_MAIN:
                        MainActivity.goToPage(SplashActivity.this);
                        finish();
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImage();

        findViewById(R.id.splash_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mHandler.removeMessages(MSG_GO_TO_MAIN);
                MainActivity.goToPage(SplashActivity.this);
                finish();
            }
        });
        mHandler.sendEmptyMessageDelayed(MSG_GO_TO_MAIN, 5000);
    }

    private void setImage() {
        int i = SPUtils.getIntValue(YSRLConstants.DISPLAY_SPLASH);
        try {
            String[] mGoodTips = null;
            if (AppContext.mUser != null) {
                String mGoodDay = CalendarCore.getDayTitle(new DateTime(), new DateTime(AppContext.mUser.birthTime), AppContext.mUser.userSex == User.BOY);
                if (AppContext.mUser.userSex == User.BOY) {
//                    if ("锻炼日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.duanlian_boy);
//                        mSplashImage.setImageResource(R.drawable.duanlian_boy_girl);
//                    } else if ("家庭日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jiating_boy);
//                        mSplashImage.setImageResource(R.drawable.jiating_boy_girl);
//                    } else if ("愿望日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.yuanwang_boy);
//                        mSplashImage.setImageResource(R.drawable.yuanwang_boy_girl);
//                    } else if ("贵人日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.guiren_boy);
//                        mSplashImage.setImageResource(R.drawable.guiren_boy_girl);
//                    } else if ("能量日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.nengliang_boy);
//                        mSplashImage.setImageResource(R.drawable.nengliang_boy_girl);
//                    } else if ("戒懒日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jielan_boy);
//                        mSplashImage.setImageResource(R.drawable.jielan_boy_girl);
//                    } else if ("麻烦日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.mafan_boy);
//                        mSplashImage.setImageResource(R.drawable.mafan_boy_girl);
//                    } else if ("学习日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.xuexi_boy);
//                        mSplashImage.setImageResource(R.drawable.xuexi_boy_girl);
//                    } else if ("机会日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jihui_boy);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("低调日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.didiao_boy);
//                        mSplashImage.setImageResource(R.drawable.didiao_boy_girl);
//                    } else if ("聆听日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.lingting_boy);
//                        mSplashImage.setImageResource(R.drawable.lingting_boy_girl);
//                    } else if ("发挥日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.fahui_boy);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("环保日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.huanbao_boy);
//                        mSplashImage.setImageResource(R.drawable.huanbao_boy_girl);
//                    } else if ("吵架日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.chaojia_boy);
//                        mSplashImage.setImageResource(R.drawable.chaojia_boy_girl);
//                    } else if ("戒躁日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jiezao_boy);
//                        mSplashImage.setImageResource(R.drawable.jiezao_boy_girl);
//                    } else if ("是非日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.shifei_boy);
//                        mSplashImage.setImageResource(R.drawable.shifei_boy_girl);
//                    } else if ("堵车日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.duche_boy);
//                        mSplashImage.setImageResource(R.drawable.chaojia_boy_girl);
//                    } else if ("收获日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.shouhuo_boy);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("桃花日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.taohua_boy);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("面试日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.mianshi_boy);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("造型日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.zaoxing_boy);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("散财日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.sancai_boy);
//                        mSplashImage.setImageResource(R.drawable.sancai_boy_girl);
//                    } else if ("惊喜日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jingxi_boy);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("小财日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.xiaocai_boy);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("捡漏日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jianlou_boy);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("朋友日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.pengyou_boy);
//                        mSplashImage.setImageResource(R.drawable.pengyou_boy_girl);
//                    } else if ("才艺日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.caiyi_boy);
//                        mSplashImage.setImageResource(R.drawable.caiyi_boy_girl);
//                    } else if ("改变日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.gaibian_boy);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("苦逼日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.kubi_boy);
//                        mSplashImage.setImageResource(R.drawable.kubi_boy_xiaohao_girl);
//                    }
//                } else {
//                    if ("锻炼日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.duanlian_girl);
//                        mSplashImage.setImageResource(R.drawable.duanlian_boy_girl);
//                    } else if ("家庭日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jiating_girl);
//                        mSplashImage.setImageResource(R.drawable.jiating_boy_girl);
//                    } else if ("愿望日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.yuanwang_girl);
//                        mSplashImage.setImageResource(R.drawable.yuanwang_boy_girl);
//                    } else if ("贵人日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.guiren_girl);
//                        mSplashImage.setImageResource(R.drawable.guiren_boy_girl);
//                    } else if ("能量日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.nengliang_girl);
//                        mSplashImage.setImageResource(R.drawable.nengliang_boy_girl);
//                    } else if ("戒懒日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jielan_girl);
//                        mSplashImage.setImageResource(R.drawable.jielan_boy_girl);
//                    } else if ("麻烦日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.mafan_girl);
//                        mSplashImage.setImageResource(R.drawable.mafan_boy_girl);
//                    } else if ("学习日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.xuexi_girl);
//                        mSplashImage.setImageResource(R.drawable.xuexi_boy_girl);
//                    } else if ("机会日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jihui_girl);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("低调日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.didiao_girl);
//                        mSplashImage.setImageResource(R.drawable.didiao_boy_girl);
//                    } else if ("聆听日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.lingting_girl);
//                        mSplashImage.setImageResource(R.drawable.lingting_boy_girl);
//                    } else if ("发挥日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.fahui_girl);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("环保日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.huanbao_girl);
//                        mSplashImage.setImageResource(R.drawable.huanbao_boy_girl);
//                    } else if ("吵架日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.chaojia_girl);
//                        mSplashImage.setImageResource(R.drawable.chaojia_boy_girl);
//                    } else if ("戒躁日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jiezao_girl);
//                        mSplashImage.setImageResource(R.drawable.jiezao_boy_girl);
//                    } else if ("是非日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.shifei_girl);
//                        mSplashImage.setImageResource(R.drawable.shifei_boy_girl);
//                    } else if ("堵车日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.duche_girl);
//                        mSplashImage.setImageResource(R.drawable.chaojia_boy_girl);
//                    } else if ("收获日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.shouhuo_girl);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("桃花日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.taohua_girl);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("面试日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.mianshi_girl);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("造型日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.zaoxing_girl);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("散财日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.sancai_girl);
//                        mSplashImage.setImageResource(R.drawable.sancai_boy_girl);
//                    } else if ("惊喜日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jingxi_girl);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("小财日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.xiaocai_girl);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("捡漏日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.jianlou_girl);
//                        mSplashImage.setImageResource(R.drawable.jihui_boy_girl);
//                    } else if ("朋友日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.pengyou_girl);
//                        mSplashImage.setImageResource(R.drawable.pengyou_boy_girl);
//                    } else if ("才艺日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.caiyi_girl);
//                        mSplashImage.setImageResource(R.drawable.caiyi_boy_girl);
//                    } else if ("改变日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.gaibian_girl);
//                        mSplashImage.setImageResource(R.drawable.mianshi_boy_girl);
//                    } else if ("姨妈日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.yima_girl);
//                        mSplashImage.setImageResource(R.drawable.mafan_boy_girl);
//                    } else if ("消耗日".equals(mGoodDay)) {
//                        mGoodTips = getResources().getStringArray(R.array.xiaohao_girl);
//                        mSplashImage.setImageResource(R.drawable.kubi_boy_xiaohao_girl);
//                    }
                }
            }
            if (mGoodTips != null) {
                int length = mGoodTips.length;
                //i对length取模得到不重复的数字，从而显示该段话
                String tip = mGoodTips[i % length];
                if ("都是一被辈子的兄弟，好好聊聊吧".equals(tip)) {
                    mText1.setText("都是一");
                    mText2.setText("被");
                    UIUtils.setThruLine(mText2);
                    mText3.setText("辈子的兄弟，好好聊聊吧");
                } else if ("机会就是现在！把握裆当下吧".equals(tip)) {
                    mText1.setText("机会就是现在！把握");
                    mText2.setText("裆");
                    UIUtils.setThruLine(mText2);
                    mText3.setText("当下吧");
                } else {
                    mText1.setText(tip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //异常的话就xml里有显示默认的能量日图片
        }

        SPUtils.putIntValue(YSRLConstants.DISPLAY_SPLASH, i + 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }
}
