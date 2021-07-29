package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.adapter.MasterAuguryListAdapter;
import com.qizhu.rili.bean.AuguryCart;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

import java.util.ArrayList;

/**
 * Created by zhouyue on 19/04/2018.
 * 大师亲算
 */

public class MasterAuguryActivity extends BaseListActivity {
    private String  mItemId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            mTitle.setText(getString(R.string.master_augur));

            titleView.addView(view, params);
        }
    }


    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }


    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            ArrayList<AuguryCart> arrayList = new ArrayList<>();
            AuguryCart auguryItem1 = new AuguryCart();
            auguryItem1.title = getString(R.string.master_augur_vip_content1);
            auguryItem1.imageId = R.drawable.master_augury_vip1;
            auguryItem1.type = 1;
            AuguryCart auguryItem2 = new AuguryCart();
            auguryItem2.title = getString(R.string.master_augur_vip_content2);
            auguryItem2.imageId = R.drawable.master_augury_vip2;
            auguryItem2.type = 2;
            AuguryCart auguryItem3 = new AuguryCart();
            auguryItem3.title = getString(R.string.master_augur_vip_content3);
            auguryItem3.imageId = R.drawable.master_augury_vip3;
            auguryItem3.type = 3;


            arrayList.add(auguryItem1);
            arrayList.add(auguryItem2);
            arrayList.add(auguryItem3);

            mAdapter = new MasterAuguryListAdapter(this, arrayList);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {
        view = new ListViewHead(this, R.layout.master_augur_head_text);
        super.addHeadView(refreshableView, view);

    }

    @Override
    protected void addFooterView(KDSPRecyclerView refreshableView, View view) {
        view = new ListViewHead(this, R.layout.master_augur_head);
        view.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MasterAskDetailActivity.goToPage(MasterAuguryActivity.this, mItemId);
            }
        });
        super.addFooterView(refreshableView, view);
    }

    @Override
    protected void getData() {
        refreshViewByType(LAY_TYPE_NORMAL);
    }

    @Override
    protected void getNextData() {

    }

    @Override
    protected boolean canPullDownRefresh() {
        return false;
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MasterAuguryActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context,  String name) {
        Intent intent = new Intent(context, MasterAuguryActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, name);
        context.startActivity(intent);
    }


}
