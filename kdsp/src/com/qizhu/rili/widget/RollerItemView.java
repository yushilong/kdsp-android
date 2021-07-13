package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 11/26/15.
 * 轮滑转盘的子view
 */
public class RollerItemView extends LinearLayout {
    public final static int SELECTED_MODE = 1;           //选中模式
    public final static int UN_SELECTED_MODE = 2;        //非选中模式

    private Context mContext;
    private ImageView mItemImage;                   //图片
    private int mDisableId;                         //不可用的资源id
    private int mEnableId;                          //可用的资源id
    private int mWidth;                             //组件的宽度
    private int mState = UN_SELECTED_MODE;          //当前的状态,最初都是默认不可用

    public RollerItemView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public RollerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public RollerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    public void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.circle_menu_item, this);
        mItemImage = (ImageView) findViewById(R.id.id_circle_menu_item_image);
        mWidth = (int) (AppContext.getScreenWidth() / 4);
    }

    public void setImageAndText(int disableId, int enableId, String texts, int index) {
        mDisableId = disableId;
        mEnableId = enableId;
        setMode(UN_SELECTED_MODE, index);
    }

    public void setImage(int disableId, int enableId, int index) {
        mDisableId = disableId;
        mEnableId = enableId;
//        mItemText.setText(index + "");
        setMode(UN_SELECTED_MODE, index);
    }

    public void setMode(int mode, int index) {
        try {
            switch (mode) {
                case SELECTED_MODE:
                    Bitmap mEnableBitmap = BitmapFactory.decodeResource(mContext.getResources(), mEnableId);
                    int mEnableWidth = mWidth;
                    if (mEnableWidth > 0) {
                        mItemImage.setMaxWidth(mEnableWidth);
                    }
                    mItemImage.setImageBitmap(mEnableBitmap);
                    setScale(mItemImage, 1.1f);
                    break;
                case UN_SELECTED_MODE:
                    Bitmap mDisableBitmap = BitmapFactory.decodeResource(mContext.getResources(), mDisableId);
                    int mDisableWidth = mWidth * 13 / 25;
                    if (mDisableWidth > 0) {
                        mItemImage.setMaxWidth(mDisableWidth);
                    }
                    mItemImage.setImageBitmap(mDisableBitmap);
                    setScale(mItemImage, 1f);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mState = mode;
    }

    private void setScale(float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mEnableId);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        mItemImage.setImageMatrix(matrix);
        mItemImage.setImageBitmap(resizeBmp);
        MethodCompat.setBackground(mItemImage, new BitmapDrawable(mContext.getResources(), resizeBmp));
    }

    /**
     * 图片缩放
     */
    private void setScale(ImageView mImage, float scale) {
        ScaleAnimation animation = new ScaleAnimation(0.0f, scale, 0.0f, scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(0);//设置动画持续时间
        animation.setFillAfter(true);
        mImage.startAnimation(animation);
    }
}
