package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.FateCatListAdapter;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.widget.KDSPRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 8/18/16.
 * 搜索列表
 */
public class SearchProblemListActivity extends BaseListActivity {
    private EditText mContent;
    private ImageView mCancel;

    private ArrayList<FateItem> mFateItems = new ArrayList<FateItem>();
    private String mCatId;                  //分类id
    private String mKey;                    //搜索关键字

    private int mFlag;                  //是否是推荐列表,当flag=1时,说明没有根据关键字搜索到结果,返回的是推荐结果,当flag=0时,则是根据关键字搜索到的结果
    private View mHeadView;             //头部

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCatId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        super.onCreate(savedInstanceState);
        mHeadView.setVisibility(View.GONE);
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.search_item_title_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });

            mContent = (EditText) view.findViewById(R.id.search);
            mContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mKey = editable.toString();
                    if (TextUtils.isEmpty(mKey)) {
                        mCancel.setVisibility(View.GONE);
                    } else {
                        mCancel.setVisibility(View.VISIBLE);
                    }
                    if (!TextUtils.isEmpty(mKey)) {
                        KDSPApiController.getInstance().searchItem(mCatId, mKey, mFlag, 1, 10, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                mFlag = response.optInt("flag");
                                mFateItems.clear();
                                mFateItems.addAll(FateItem.parseListFromJSON(response.optJSONArray("items")));
                                SearchProblemListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyDataSetChanged();
                                        if (mFlag == 1) {
                                            mHeadView.setVisibility(View.VISIBLE);
                                        } else {
                                            mHeadView.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                isRequesting = false;
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {

                            }
                        });
                    }
                }
            });
            mCancel = (ImageView) view.findViewById(R.id.cancel);
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFlag = 0;
                    mKey = "";
                    mFateItems.clear();
                    mHeadView.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    hasNoNextData = false;
                    mContent.setText("");
                }
            });
            titleView.addView(view, params);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView,View view) {
        mHeadView = mInflater.inflate(R.layout.search_head_lay, null);
        mHeadView.findViewById(R.id.add_problem).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AddProblemActivity.goToPage(SearchProblemListActivity.this);
            }
        });
        super.addHeadView(refreshableView,mHeadView);
    }

    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new FateCatListAdapter(this, mFateItems,mCatId);
        }
    }

    @Override
    protected boolean needGetDataOnStart() {
        //重写此函数，不需要一进入就获取数据
        return false;
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void getNextData() {
        if (!TextUtils.isEmpty(mKey)) {
            isRequesting = true;
            mPage = mPage + 1;
            KDSPApiController.getInstance().searchItem(mCatId, mKey, mFlag, mPage, 10, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    mFlag = response.optInt("flag");
                    ArrayList<FateItem> fateItems = FateItem.parseListFromJSON(response.optJSONArray("items"));
                    if (fateItems.isEmpty()) {
                        hasNoNextData = true;
                    }
                    mFateItems.addAll(fateItems);
                    SearchProblemListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    isRequesting = false;
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                }
            });
        }
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

    public static void goToPage(Context context, String catId) {
        Intent intent = new Intent(context, SearchProblemListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, catId);
        context.startActivity(intent);
    }
}
