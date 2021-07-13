package com.qizhu.rili.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.CameraPreview;
import com.qizhu.rili.widget.DrawLineView;

/**
 * Created by lindow on 6/7/16.
 * 手相拍照界面
 */
public class TakeHandsPhotoActivity extends BaseActivity implements CameraPreview.OnCameraStatusListener {
    private CameraPreview mCameraPreview;                   //相机预览view
    private DrawLineView mDrawLine;                         //划线
    private View mGuideTip;                                 //划线的引导布局
    private ImageView mGuideHandTip;                        //划线手掌引导图
    private ImageView mHandClickTip;                        //手掌点击提示
    private View mTipLay;                                   //操作手册的提示布局
    private ImageView mTipTriangle;                         //操作手册三角形
    private View mHandLay;                                  //拍照时手的背景布局
    private ImageView mHandBg;                              //拍照的手的背景
    private TextView mTip;                                  //解锁提示
    private View mLineLay;                                  //拍照操作布局
    private View mCompleteLay;                              //拍照完成布局
    private ImageView mPhotoTip;                            //初次进入时功能提示
    private View mFooterLay;                                //底部布局
    private View mDrawLay;                                  //划线布局
    private TextView mGuideTextTip;                         //文字划线提示
    private DrawLineView mDrawLineAnim;                     //划线动画
    private View mLineTip;                                  //有无此线的提示
    private TextView mNoLineTip;                            //无此线文本提示
    private ImageView mLineTipImage;                        //有无此线的提示图
    private TextView mExistLine;                            //有此线
    private TextView mWithoutLine;                          //无此线

    private TextView mLineOne;                              //1线感情线
    private TextView mLineTwo;                              //2线智慧线
    private TextView mLineThree;                            //3线生命线
    private TextView mLineFour;                             //4线健康线
    private TextView mLineFive;                             //4线事业线

    private Handler mHandler;
    private Bitmap mBitmap;                                 //当前的图像
    private int mWidth;                                     //拍照区域宽度
    private int mHeight;                                    //拍照区域高度
    private int mGuideWidth = DisplayUtils.dip2px(120);     //拍照区域宽度
    private int mGuideHeight = DisplayUtils.dip2px(160);    //拍照区域高度
    private boolean mIsMale;                                //是否男生
    public int mType = 2;                                   //类型，2为一线类推,默认为2线
    private boolean mIsPlayAnim;                            //是否正在进行动画
    private boolean mIsMagnify = true;                      //是否放大动画，默认为true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo_lay);
        initHandler();
        initView();
        boolean mShowTip = getIntent().getBooleanExtra(IntentExtraConfig.EXTRA_MODE, true);
        if (mShowTip) {
            mHandler.sendEmptyMessage(1);
            mHandler.sendEmptyMessageDelayed(2, 2500);
        } else {
            mHandLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initView() {
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        mDrawLine = (DrawLineView) findViewById(R.id.draw_line);
        mGuideTip = findViewById(R.id.guide_tip);
        mGuideHandTip = (ImageView) findViewById(R.id.guide_hand_tip);
        mHandClickTip = (ImageView) findViewById(R.id.hand_click);
        mTipLay = findViewById(R.id.tip_lay);
        mTipTriangle = (ImageView) findViewById(R.id.tip_triangle);
        mHandLay = findViewById(R.id.bg_lay);
        mHandBg = (ImageView) findViewById(R.id.hand_bg);
        mTip = (TextView) findViewById(R.id.tip);
        mLineLay = findViewById(R.id.line_lay);
        mCompleteLay = findViewById(R.id.complete_lay);
        mPhotoTip = (ImageView) findViewById(R.id.photo_tip);
        mFooterLay = findViewById(R.id.footer_lay);
        mDrawLay = findViewById(R.id.use_line_lay);
        mLineOne = (TextView) findViewById(R.id.line_one);
        mLineTwo = (TextView) findViewById(R.id.line_two);
        mLineThree = (TextView) findViewById(R.id.line_three);
        mLineFour = (TextView) findViewById(R.id.line_four);
        mLineFive = (TextView) findViewById(R.id.line_five);
        mGuideTextTip = (TextView) findViewById(R.id.hand_text_tip);
        mDrawLineAnim = (DrawLineView) findViewById(R.id.draw_line_anim);
        mLineTip = findViewById(R.id.line_tip);
        mNoLineTip = (TextView) findViewById(R.id.no_line_tip);
        mLineTipImage = (ImageView) findViewById(R.id.line_tip_image);
        mExistLine = (TextView) findViewById(R.id.exist);
        mWithoutLine = (TextView) findViewById(R.id.without);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        mTipLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePhotoTip();
            }
        });

        mPhotoTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePhotoTip();
            }
        });

        mLineOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 2;
                mLineOne.setBackgroundResource(R.drawable.round_white);
                mLineTwo.setBackgroundResource(R.drawable.round_white1);
                mLineThree.setBackgroundResource(R.drawable.round_white1);
                mLineFour.setBackgroundResource(R.drawable.round_white1);
                mLineFive.setBackgroundResource(R.drawable.round_white1);
            }
        });

        mLineTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 3;
                mLineOne.setBackgroundResource(R.drawable.round_white1);
                mLineTwo.setBackgroundResource(R.drawable.round_white);
                mLineThree.setBackgroundResource(R.drawable.round_white1);
                mLineFour.setBackgroundResource(R.drawable.round_white1);
                mLineFive.setBackgroundResource(R.drawable.round_white1);
            }
        });

        mLineThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 4;
                mLineOne.setBackgroundResource(R.drawable.round_white1);
                mLineTwo.setBackgroundResource(R.drawable.round_white1);
                mLineThree.setBackgroundResource(R.drawable.round_white);
                mLineFour.setBackgroundResource(R.drawable.round_white1);
                mLineFive.setBackgroundResource(R.drawable.round_white1);
            }
        });
        mLineFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 5;
                mLineOne.setBackgroundResource(R.drawable.round_white1);
                mLineTwo.setBackgroundResource(R.drawable.round_white1);
                mLineThree.setBackgroundResource(R.drawable.round_white1);
                mLineFour.setBackgroundResource(R.drawable.round_white);
                mLineFive.setBackgroundResource(R.drawable.round_white1);
            }
        });
        mLineFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 6;
                mLineOne.setBackgroundResource(R.drawable.round_white1);
                mLineTwo.setBackgroundResource(R.drawable.round_white1);
                mLineThree.setBackgroundResource(R.drawable.round_white1);
                mLineFour.setBackgroundResource(R.drawable.round_white1);
                mLineFive.setBackgroundResource(R.drawable.round_white);
            }
        });

        mGuideTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGuideAnimation(500);
            }
        });

        findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraPreview.takePicture();
            }
        });

        findViewById(R.id.re_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraPreview.setVisibility(View.VISIBLE);
                mCameraPreview.start();
                if (mBitmap != null) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                mTipLay.setVisibility(View.VISIBLE);
                mLineLay.setVisibility(View.VISIBLE);
                mCompleteLay.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.use_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (5 == mType || 6 == mType) {
                    showLineTip();
                } else {
                    showLine();
                }
            }
        });

        findViewById(R.id.re_draw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawLine.reset();
            }
        });

        findViewById(R.id.use_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEnd = mDrawLine.getEndPoint();
                if ("0.0,0.0".equals(mEnd)) {
                    UIUtils.toastMsg("请划线之后再提交~");
                } else {
                    LineDetailActivity.goToPage(TakeHandsPhotoActivity.this, mType, mDrawLine.getStartPoint(), mDrawLine.getEndPoint(), getPicSize());
                    finish();
                }
            }
        });
        mCameraPreview.setOnCameraStatusListener(this);
        mIsMale = AppContext.mUser != null && AppContext.mUser.userSex == User.BOY;
        if (mIsMale) {
            mGuideHandTip.setBackgroundResource(R.drawable.left_hand_tip);
            mHandBg.setImageResource(R.drawable.left_hand);
            mHandClickTip.setImageResource(R.drawable.left_hand_click);
        } else {
            mGuideHandTip.setBackgroundResource(R.drawable.right_hand_tip);
            mHandBg.setImageResource(R.drawable.right_hand);
            mHandClickTip.setImageResource(R.drawable.right_hand_click);
        }

        mExistLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLineTip.setVisibility(View.GONE);
                showLine();
            }
        });

        mWithoutLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LineDetailActivity.goToPage(TakeHandsPhotoActivity.this, mType, "0,0", "0,0", getPicSize());
            }
        });

        ViewTreeObserver viewTreeObserver = mCameraPreview.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (MethodCompat.isCompatible(Build.VERSION_CODES.JELLY_BEAN)) {
                    mCameraPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mCameraPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mWidth = mCameraPreview.getWidth();
                mHeight = mCameraPreview.getHeight();
            }
        });
    }

    private void showLine() {
        mLineLay.setVisibility(View.GONE);
        mCompleteLay.setVisibility(View.GONE);
        mHandLay.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams;
        if (mIsMale) {
            layoutParams = new RelativeLayout.LayoutParams(DisplayUtils.dip2px(120), DisplayUtils.dip2px(160));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(DisplayUtils.dip2px(120), DisplayUtils.dip2px(160));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        }
        mGuideTip.setLayoutParams(layoutParams);
        mGuideTip.setVisibility(View.VISIBLE);
        mDrawLine.setVisibility(View.VISIBLE);
        mDrawLine.setCanDraw(true);

        mDrawLay.setVisibility(View.VISIBLE);
        mFooterLay.setBackgroundColor(ContextCompat.getColor(this, R.color.purple1));
        drawSmallLineTip();
    }

    private void showLineTip() {
        switch (mType) {
            case 5:
                if (mIsMale) {
                    mLineTipImage.setImageResource(R.drawable.line_four_tip_left);
                } else {
                    mLineTipImage.setImageResource(R.drawable.line_four_tip_right);
                }
                mNoLineTip.setText(R.string.no_line_four);
                mExistLine.setText(R.string.exist_line_four);
                mWithoutLine.setText(R.string.without_line_four);
                break;
            case 6:
                if (mIsMale) {
                    mLineTipImage.setImageResource(R.drawable.line_five_tip_left);
                } else {
                    mLineTipImage.setImageResource(R.drawable.line_five_tip_right);
                }
                mNoLineTip.setText(R.string.no_line_five);
                mExistLine.setText(R.string.exist_line_five);
                mWithoutLine.setText(R.string.without_line_five);
                break;
        }

        mLineLay.setVisibility(View.GONE);
        mCompleteLay.setVisibility(View.GONE);
        mHandLay.setVisibility(View.GONE);
        mLineTip.setVisibility(View.VISIBLE);
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        togglePhotoTip();
                        break;
                    case 2:
                        if (mPhotoTip.getVisibility() == View.VISIBLE) {
                            togglePhotoTip();
                        }
                        break;
                    case 3:
                        mTip.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        if (mTip.getVisibility() == View.VISIBLE) {
                            mTip.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        };
    }

    private void togglePhotoTip() {
        if (mPhotoTip.getVisibility() == View.VISIBLE) {
            mPhotoTip.setVisibility(View.GONE);
            mHandLay.setVisibility(View.VISIBLE);
            mTipTriangle.setImageResource(R.drawable.triangle_arrow_purple1_down);
        } else {
            mPhotoTip.setVisibility(View.VISIBLE);
            mHandLay.setVisibility(View.GONE);
            mTipTriangle.setImageResource(R.drawable.triangle_arrow_purple1_up);
        }
    }

    @Override
    public void onCameraStopped(byte[] data) {
        //拍照结束
        LogUtils.d("---> mBitmap onCameraStopped ");

        mTipLay.setVisibility(View.GONE);
        mLineLay.setVisibility(View.GONE);
        mCompleteLay.setVisibility(View.VISIBLE);
    }

    private String getPicSize() {
        return mWidth + "," + mHeight;
    }

    private void drawSmallLineTip() {
        switch (mType) {
            case 2:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(372, 540, 596, 648, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                } else {
                    mDrawLineAnim.drawLine(357, 531, 132, 651, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                }
                break;
            case 3:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(273, 522, 495, 680, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                } else {
                    mDrawLineAnim.drawLine(456, 526, 226, 680, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                }
                mHandClickTip.setPadding(0, 50, 0, 0);
                break;
            case 4:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(232, 537, 371, 853, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(0, 50, 70, 0);
                } else {
                    mDrawLineAnim.drawLine(493, 536, 356, 851, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(170, 50, 0, 0);
                }
                break;
            case 5:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(507, 640, 430, 806, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(0, 50, 70, 0);
                } else {
                    mDrawLineAnim.drawLine(217, 643, 345, 785, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(170, 50, 0, 0);
                }
                break;
            case 6:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(400, 637, 410, 825, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(0, 50, 70, 0);
                } else {
                    mDrawLineAnim.drawLine(325, 646, 310, 812, mGuideWidth, mGuideHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(170, 50, 0, 0);
                }
                break;
        }
        mGuideTextTip.setVisibility(View.GONE);
        mHandClickTip.setVisibility(View.GONE);
    }

    private void drawLineTip() {
        switch (mType) {
            case 2:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(372, 540, 596, 648, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                } else {
                    mDrawLineAnim.drawLine(357, 531, 132, 651, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                }
                break;
            case 3:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(273, 522, 495, 680, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                } else {
                    mDrawLineAnim.drawLine(456, 526, 226, 680, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                }
                mHandClickTip.setPadding(0, 50, 0, 0);
                break;
            case 4:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(232, 537, 371, 853, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(0, 50, 70, 0);
                } else {
                    mDrawLineAnim.drawLine(493, 536, 356, 851, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(170, 50, 0, 0);
                }
                break;
            case 5:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(507, 640, 430, 806, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(0, 50, 70, 0);
                } else {
                    mDrawLineAnim.drawLine(217, 643, 345, 785, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(170, 50, 0, 0);
                }
                break;
            case 6:
                if (mIsMale) {
                    mDrawLineAnim.drawLine(400, 637, 410, 825, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(0, 50, 70, 0);
                } else {
                    mDrawLineAnim.drawLine(325, 646, 310, 812, mWidth, mHeight, 750, 1000, 1000, mType, mIsMale);
                    mHandClickTip.setPadding(170, 50, 0, 0);
                }
                break;
        }
        mGuideTextTip.setVisibility(View.VISIBLE);
        mHandClickTip.setVisibility(View.VISIBLE);
    }

    private void startGuideAnimation(int time) {
        if (!mIsPlayAnim) {
            mDrawLineAnim.stopAnim();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
            if (mGuideTextTip.getVisibility() == View.VISIBLE) {
                mIsMagnify = false;
            } else {
                mIsMagnify = true;
            }

            mGuideTextTip.setVisibility(View.GONE);
            mHandClickTip.setVisibility(View.GONE);

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    if (mIsMagnify) {
                        mGuideTip.getLayoutParams().width = (int) (mGuideWidth + (mWidth - mGuideWidth) * value);
                        mGuideTip.getLayoutParams().height = (int) (mGuideHeight + (mHeight - mGuideHeight) * value);
                    } else {
                        mGuideTip.getLayoutParams().width = (int) (mWidth - (mWidth - mGuideWidth) * value);
                        mGuideTip.getLayoutParams().height = (int) (mHeight - (mHeight - mGuideHeight) * value);
                    }
                    mGuideTip.requestLayout();
                }
            });

            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mIsPlayAnim = true;
                    if (mIsMagnify) {
                        mDrawLine.clearLine();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mIsPlayAnim = false;
                    if (mIsMagnify) {
                        mDrawLine.setCanDraw(false);
                        drawLineTip();
                    } else {
                        mDrawLine.setCanDraw(true);
                        drawSmallLineTip();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            valueAnimator.setDuration(time);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.start();
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, TakeHandsPhotoActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, boolean showTip) {
        Intent intent = new Intent(context, TakeHandsPhotoActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, showTip);
        context.startActivity(intent);
    }
}
