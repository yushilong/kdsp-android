package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.adapter.TestNameResultAdapter;
import com.qizhu.rili.bean.Address;
import com.qizhu.rili.bean.Name;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.DeleteOrderDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by zhouyue on 08/05/2017.
 * 测名和取名详情界面
 */

public class TestNameResultActivity extends BaseListActivity {
    private TextView mTitle;
    private TextView mChange;

    private String mItemId;                 //问题id
    private String mIoId;                   //当前的订单id
    private String mItemParam;              //问题详情
    private Name mName;                     //当前的名字结果
//    private ArrayList<Name>     mNames       = new ArrayList<>();
    private  ArrayList<Name> mNames = new ArrayList<>();
    private ArrayList<Address> mAddressList = new ArrayList<>();
    private boolean mLast;                  //是否是第二批名字
    private TextView mShare;                    //分享
    private TextView mDelete;                   //删除
    private PopupWindow mPop;                   //操作弹出框
    private String mlastName;               //姓
    private String mfirstName;              //名

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getExtraData();
        super.onCreate(savedInstanceState);
        initPop();
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_more_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            mTitle = (TextView) view.findViewById(R.id.title_txt);

            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });

            view.findViewById(R.id.more_btn).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (mPop != null) {
                        if (mPop.isShowing()) {
                            mPop.dismiss();
                        } else {
                            mPop.showAsDropDown(findViewById(R.id.more_btn), 0, 0);
                        }
                    }
                }
            });


            refreshTitle();

            titleView.addView(view, params);
        }
    }

    private void refreshTitle() {
        try {
            if (!TextUtils.isEmpty(mIoId)) {
                int type = new JSONObject(mItemParam).optInt("nameType");
                if (type == 1) {
                    mTitle.setText("改名字");
                } else {
                    mTitle.setText("宝宝起名");
                }
            } else {
                mTitle.setText("免费测名");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void addFooterView(KDSPRecyclerView refreshableView, View view) {
         view = new ListViewHead(this,R.layout.test_name_footer);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        mChange = (TextView) view.findViewById(R.id.change);

        if (TextUtils.isEmpty(mIoId)) {
            mChange.setText("改旺运好名");
        } else if (mLast) {
            mChange.setText("查看上一批");
        } else {
            mChange.setText("不满意，换一批");
        }
        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mIoId)) {
                    AugurySubmitActivity.goToPage(TestNameResultActivity.this, "", mItemId, "", "", 4, 1, 2);
                } else if (mLast) {
                    int size = mNames.size();
                    if (size > 5) {
                        mLast = false;
                        mAdapter.reset(mNames.subList(0, 5), mLast);
                        mChange.setText("不满意，换一批");
                    }
                    mAdapter.notifyDataSetChanged();
                    mKDSPRecyclerView.scrollToPosition(0);
                } else {
                    int size = mNames.size();
                    if (size > 5) {
                        mLast = true;
                        mAdapter.reset(mNames.subList(5, size), mLast);
                        mChange.setText("查看上一批");
                    } else {
                        mAdapter.reset(mNames);
                    }
                    mAdapter.notifyDataSetChanged();
                    mKDSPRecyclerView.scrollToPosition(0);
                }
            }
        });
        super.addFooterView(refreshableView,view);
    }

    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new TestNameResultAdapter(this, mNames, TextUtils.isEmpty(mIoId));
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        }
    }


    private void initPop() {
        View catePopLay = mInflater.inflate(R.layout.augury_pop_lay, null);
        mPop = new PopupWindow(catePopLay, DisplayUtils.dip2px(85), DisplayUtils.dip2px(92), true);
        //如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框O
        mPop.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.gray18));
        mShare = (TextView) catePopLay.findViewById(R.id.share);
        mDelete = (TextView) catePopLay.findViewById(R.id.delete);
        mShare.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                try {
                    if (TextUtils.isEmpty(mIoId)) {
                        String url = "http://h5.ishenpo.com/app/share/name_test_result?fn=" + URLEncoder.encode(mName.familyName, "UTF-8") + "&ln=" + URLEncoder.encode(mName.lastName, "UTF-8");
                        ShareActivity.goToMiniShare(TestNameResultActivity.this, "在线测名，探索你的姓名奥秘", "姓名是我们每个人的第一张名片，我们来帮您读懂它", url, "http://pt.qi-zhu.com/@/2017/05/17/8b6cb24c-2abf-4ea7-9c96-4de73f872387.jpg", 0, ""
                        ,"pages/decrypt/reckon/test_answer?f=" + mlastName + "&l=" + mfirstName + "&itemId=" + mItemId);
                    } else {
                        ShareActivity.goToMiniShare(TestNameResultActivity.this, "大师起名，好名更能旺好命", "好的开始是成功的一半，一个好名字就是人生的起点", "http://h5.ishenpo.com/app/share/name_change_result?ioid=" + mIoId, "http://pt.qi-zhu.com/@/2017/05/17/8b6cb24c-2abf-4ea7-9c96-4de73f872387.jpg", 0, "",
                                "pages/home/divination/divination_answer?type= 4" + "&ioId="+mIoId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mDelete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showDialogFragment(DeleteOrderDialogFragment.newInstance(DeleteOrderDialogFragment.DEL, mIoId), "删除订单");
            }
        });
    }

    @Override
    protected void getData() {
        if (!TextUtils.isEmpty(mIoId)) {
            KDSPApiController.getInstance().getNameTestResults(mIoId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    mNames = Name.parseListFromJSON(response.optJSONArray("names"));
                    TestNameResultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            refreshViewByType(LAY_TYPE_NORMAL);
                            int size = mNames.size();
                            if (size > 4) {
                                mAdapter.reset(mNames.subList(0, 5), mLast);
                            } else {
                                mAdapter.reset(mNames);
                            }

                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                }
            });
        } else if (mName != null) {
            refreshViewByType(LAY_TYPE_NORMAL);
            mNames.clear();
            mNames.add(mName);
            mAdapter.reset(mNames);
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }
    @Override
    protected void getNextData() {

    }

    private void getExtraData() {
        Intent intent = getIntent();
        mName = intent.getParcelableExtra(IntentExtraConfig.EXTRA_PARCEL);
        mIoId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mItemParam = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
        mItemId = intent.getStringExtra(IntentExtraConfig.EXTRA_GROUP_ID);
        mlastName = intent.getStringExtra(IntentExtraConfig.EXTRA_TYPE);
        mfirstName = intent.getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
    }

    public static void goToPage(Context context, Name name, String itemId ,String lastName,String firstName ) {
        Intent intent = new Intent(context, TestNameResultActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, name);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_TYPE, lastName);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, firstName);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String itemParam, String id) {
        Intent intent = new Intent(context, TestNameResultActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, itemParam);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, id);
        context.startActivity(intent);
    }
}
