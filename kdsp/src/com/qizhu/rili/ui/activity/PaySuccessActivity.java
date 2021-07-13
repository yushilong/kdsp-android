package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.FateGridAdapter;
import com.qizhu.rili.adapter.GoodsGridAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.bean.Goods;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 8/17/16.
 * 支付成功界面
 */
public class PaySuccessActivity extends BaseListActivity {
    private View mGuessLay;         //推荐布局
    private int mMode;              //支付的订单类型,与对应问题的type一致

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMode = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            TextView title = (TextView) view.findViewById(R.id.title_txt);
            title.setText(R.string.pay_success);
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }

            });
            titleView.addView(view, params);
        }
    }




    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView,View view) {
        view = new ListViewHead(this,R.layout.pay_success_lay);
        view.findViewById(R.id.view_order).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mMode == OrderDetail.COMPLETED) {
                    OrderListActivity.goToPage(PaySuccessActivity.this, OrderDetail.HAS_PAID);
                } else {
                    AuguryListActivity.goToPage(PaySuccessActivity.this, false);
                }

                finish();
            }
        });

        TextView mPayTip = (TextView) view.findViewById(R.id.pay_tip);
        mGuessLay = view.findViewById(R.id.guess_lay);

        switch (mMode) {
            case 0:
                mPayTip.setText(R.string.reply_tip);
                break;
            case 1:
                mPayTip.setText(R.string.reply_hand_tip);
                break;
            case 2:
                mPayTip.setText(R.string.reply_face_tip);
                break;
            case 3:
                mPayTip.setText(R.string.reply_tip);
                break;
            case 4:
                mPayTip.setText(R.string.reply_name_tip);
                break;
            case 5:
                mPayTip.setText(R.string.reply_ask_tip);
                break;
            case 100:
                mPayTip.setText(R.string.reply_paid_tip);
                break;
        }
        super.addHeadView(refreshableView,view);
    }

    @Override
    protected void initAdapter() {
        if (mMode == OrderDetail.COMPLETED) {
            mAdapter = new GoodsGridAdapter(PaySuccessActivity.this, new ArrayList<Goods>());
        } else {
            mAdapter = new FateGridAdapter(PaySuccessActivity.this, new ArrayList<FateItem>(), 1);
        }
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
    }

    @Override
    protected void getData() {
        if (mMode == OrderDetail.COMPLETED) {
            KDSPApiController.getInstance().getRecommendGoodsList(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    ArrayList<Goods> goods = Goods.parseListFromJSON(response.optJSONArray("goods"));
                    hasNoNextData = true;
                    mAdapter.reset(goods);
                    PaySuccessActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshViewByType(LAY_TYPE_NORMAL);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    isRequesting = false;
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    mGuessLay.setVisibility(View.GONE);
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            });
        } else {
            KDSPApiController.getInstance().getRecommendQuestionList(new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    ArrayList<FateItem> fateItems = FateItem.parseListFromJSON(response.optJSONArray("questions"));
                    hasNoNextData = true;
                    mAdapter.reset(fateItems);
                    PaySuccessActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshViewByType(LAY_TYPE_NORMAL);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    isRequesting = false;
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    mGuessLay.setVisibility(View.GONE);
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            });
        }
    }

    @Override
    protected void getNextData() {

    }

    @Override
    public void pullDownToRefresh() {

    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    @Override
    protected boolean canPullDownRefresh() {
        return false;
    }

    public static void goToPage(Context context, int mode) {
        Intent intent = new Intent(context, PaySuccessActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mode);
        context.startActivity(intent);
    }
}
