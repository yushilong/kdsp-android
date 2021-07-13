package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Cat;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.fragment.FateViewPagerFragment;
import com.qizhu.rili.utils.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 21/02/2017.
 * 大师问答
 */

public class MasterAskActivity extends BaseActivity {
    private FateViewPagerFragment mFateViewPagerFragment;

    private ArrayList<Cat> mCats = new ArrayList<>();
    private int mIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_ask_lay);
        mIndex = getIntent().getIntExtra(IntentExtraConfig.EXTRA_TYPE ,-1);
        initView();
        getData();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.master_ask);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getData();
            }
        });
    }


    private void getData() {
        KDSPApiController.getInstance().findQuestionCatList(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mCats = Cat.parseListFromJSON(response.optJSONArray("cats"));
                final FateItem fateItem = FateItem.parseObjectFromJSON(response.optJSONArray("questions").optJSONObject(0));
                MasterAskActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d("-----" +mIndex);
                        mFateViewPagerFragment = FateViewPagerFragment.newInstance(mCats, fateItem.itemId, fateItem.itemName,mIndex);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.body_fragment, mFateViewPagerFragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                findViewById(R.id.body_fragment).setVisibility(View.GONE);
                findViewById(R.id.request_bad).setVisibility(View.VISIBLE);
            }
        });
    }

    public static void goToPage(Context context,int index) {
        Intent intent = new Intent(context, MasterAskActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_TYPE,index);
        context.startActivity(intent);
    }


}
