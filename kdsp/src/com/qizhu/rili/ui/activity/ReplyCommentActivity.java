package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.adapter.ReplyCommentAdapter;
import com.qizhu.rili.bean.Chat;
import com.qizhu.rili.data.ReplyCommentDataAccessor;
import com.qizhu.rili.listener.OnSingleClickListener;

/**
 * Created by lindow on 08/04/2017.
 * 回复留言页面
 */

public class ReplyCommentActivity extends BaseListActivity {
    private ReplyCommentDataAccessor mDataAccessor;

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

            mTitle.setText(R.string.reply_comment);

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new ReplyCommentDataAccessor();
        }
        if (mAdapter == null) {
            mAdapter = new ReplyCommentAdapter(this, mDataAccessor.mData);
        }
    }

    @Override
    protected void getData() {
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true));
    }

    @Override
    protected void getNextData() {
        mDataAccessor.getNextData(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    public void pullDownToRefresh() {
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RequestCodeConfig.REQUEST_FRESH) {
                Chat chat = data.getParcelableExtra(IntentExtraConfig.EXTRA_JSON);
                int size = mDataAccessor.mData.size();
                for (int i = 0; i < size; i++) {
                    Chat chat1 = mDataAccessor.mData.get(i);
                    //当前用户的回复已更新
                    if (chat.userId.equals(chat1.userId) && !chat.msgId.equals(chat1.msgId)) {
                        mDataAccessor.mData.set(i, chat);
                    }
                }
                mAdapter.reset(mDataAccessor.mData);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, ReplyCommentActivity.class);
        context.startActivity(intent);
    }
}
