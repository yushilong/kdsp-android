package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 头像线性布局
 */
public class AvatarLinearLayout extends LinearLayout {
    private static final int AVATAR_MAX_COUNT = 4;
    public ArrayList<AvatarHolder> avatarList = new ArrayList<AvatarHolder>(AVATAR_MAX_COUNT);

    public AvatarLinearLayout(Context context) {
        super(context);
        initView(context);
    }

    public AvatarLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AvatarLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化布局
     */
    private void initView(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < AVATAR_MAX_COUNT; i++) {
            addView(buildAvatarLay(context));
        }
    }

    private RelativeLayout buildAvatarLay(Context context) {
        RelativeLayout avatarLay = new RelativeLayout(context);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        avatarLay.setLayoutParams(params);

        RelativeLayout avatarContainer = new RelativeLayout(context);
        int containerSize = DisplayUtils.dip2px(32);
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(containerSize, containerSize);
        containerParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        avatarContainer.setLayoutParams(containerParams);

        //头像
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        builder.setRoundingParams(RoundingParams.asCircle());
        YSRLDraweeView avatarImg = new YSRLDraweeView(context, builder.build());
        int avatarSize = DisplayUtils.dip2px(30);
        int imageId = (int) System.currentTimeMillis();
        RelativeLayout.LayoutParams avatarParams = new RelativeLayout.LayoutParams(avatarSize, avatarSize);
        avatarImg.setPadding(DisplayUtils.dip2px(2), DisplayUtils.dip2px(2), DisplayUtils.dip2px(2), DisplayUtils.dip2px(2));
        avatarImg.setId(imageId);
        avatarImg.setLayoutParams(avatarParams);

        //达人图标
        ImageView authImg = new ImageView(context);
        RelativeLayout.LayoutParams authParams = new RelativeLayout.LayoutParams(DisplayUtils.dip2px(12), DisplayUtils.dip2px(12));
        authParams.addRule(RelativeLayout.ALIGN_RIGHT, imageId);
        authParams.addRule(RelativeLayout.ALIGN_BOTTOM, imageId);
        authParams.setMargins(0, 0, 0 - DisplayUtils.dip2px(2), 0 - DisplayUtils.dip2px(2));
        authImg.setLayoutParams(authParams);
        authImg.setImageResource(R.drawable.boy);
        authImg.setVisibility(View.GONE);

        avatarContainer.addView(avatarImg);
        avatarContainer.addView(authImg);
        avatarLay.addView(avatarContainer);

        avatarList.add(new AvatarHolder(avatarContainer, avatarImg, authImg));
        return avatarLay;
    }

    /**
     * 填充头像信息
     *
     * @param userList 用户列表
     * @param isFromHead 是否从头部开始填充
     */
    public void setAvatarList(List<User> userList, boolean isFromHead) {
        if (userList != null) {

            int len = userList.size();
            LogUtils.d("AvatarLinearLayout setAvatarList userList size = " + len);
            int diff = AVATAR_MAX_COUNT - len;
            diff = diff < 0 ? 0 : diff;

            for (int i = 0; i < diff; i++) {
                AvatarLinearLayout.AvatarHolder avatarHolder = avatarList.get(i);
                avatarHolder.avatarContainer.setVisibility(View.INVISIBLE);
            }

            for (int i = diff; i < AVATAR_MAX_COUNT; i++) {
                AvatarLinearLayout.AvatarHolder avatarHolder = isFromHead ? avatarList.get(i - diff) : avatarList.get(i);
                avatarHolder.avatarContainer.setVisibility(View.VISIBLE);

                //user列表始终是从0开始放置头像
                int j = i - diff;
                User user = userList.get(j);
                if (user != null && user.imageUrl != null && avatarHolder.avatarImg != null) {
                    UIUtils.displayAvatarImage(user.imageUrl, avatarHolder.avatarImg, R.drawable.default_avatar);
                    //认证图标
                    avatarHolder.authImg.setVisibility(View.GONE);
                }
            }
        }
    }

    public static class AvatarHolder {
        public View avatarContainer;
        public YSRLDraweeView avatarImg;
        public ImageView authImg;

        public AvatarHolder() {
        }

        public AvatarHolder(View avatarContainer, YSRLDraweeView avatarImg, ImageView authImg) {
            this.avatarContainer = avatarContainer;
            this.avatarImg = avatarImg;
            this.authImg = authImg;
        }
    }
}
