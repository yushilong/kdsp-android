package com.qizhu.rili.ui.activity;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.ui.dialog.ProtocolDialog;
import com.qizhu.rili.ui.fragment.GuideViewPagerFragment;
import com.qizhu.rili.utils.SPUtils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import static com.qizhu.rili.YSRLConstants.SERVICE_POLICY_AGREE;

/**
 * 引导activity
 */
public class GuideActivity extends BaseActivity {
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_lay);
        boolean isAgree = SPUtils.getBoolleanValue(SERVICE_POLICY_AGREE);
        if (!isAgree) {
            showProtocolDia();
        }
        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
        imageView3 = (ImageView) findViewById(R.id.image3);
        initViewPagerFragment();
        changeIndicator(0);
    }

    private void showProtocolDia() {
        final ProtocolDialog protocolDialog = new ProtocolDialog(this);
        protocolDialog.setListener(new ProtocolDialog.IProtocolClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.refuse_tv) {
                    GuideActivity.this.finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else if (view.getId() == R.id.agree_tv) {
                    SPUtils.putBoolleanValue(SERVICE_POLICY_AGREE, true);
                    Application app = getApplication();
                    if(app instanceof AppContext){
                        ((AppContext) app).initAll();
                    }
                    protocolDialog.dismiss();
                }
            }
        });
        protocolDialog.show();
    }

    private void initViewPagerFragment() {
        GuideViewPagerFragment guideViewPagerFragment = GuideViewPagerFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, guideViewPagerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void changeIndicator(int position) {
        switch (position) {
            case 0:
                imageView1.setImageResource(R.drawable.circle_purple);
                imageView2.setImageResource(R.drawable.circle_gray30);
                imageView3.setImageResource(R.drawable.circle_gray30);
                break;
            case 1:
                imageView1.setImageResource(R.drawable.circle_gray30);
                imageView2.setImageResource(R.drawable.circle_purple);
                imageView3.setImageResource(R.drawable.circle_gray30);
                break;
            case 2:
                imageView1.setImageResource(R.drawable.circle_gray30);
                imageView2.setImageResource(R.drawable.circle_gray30);
                imageView3.setImageResource(R.drawable.circle_purple);
                break;
        }
    }

    /**
     * 跳转至引导页
     *
     * @param context 上下文环境
     */
    public static void goToPage(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }
}

