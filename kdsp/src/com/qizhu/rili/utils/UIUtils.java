package com.qizhu.rili.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.ui.activity.ImageZoomViewer;
import com.qizhu.rili.widget.YSRLDraweeView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.fresco.processors.BlurPostprocessor;

/**
 * UI?????????
 */
public class UIUtils {

    public static final String HTTP_HEADER_TAG = "http://";
    public static final String HTTPS_HEADER_TAG = "https://";
    public static final String FILE_HEADER_TAG = "file://";
    private static final String NO_HEADER_TAG = "://";
    private static final String THUMB_TAG = "thumbnail/";
    public static final String FORMAT_PNG_TAG = "/format/png";
    public static final String FORMAT_JPG_TAG = "/format/jpg";
    public static final String FORMAT_WEBP_TAG = "/format/WebP";
    private static final String QUALITY_CRITICAL_TAG = "/quality/70";
    private static Toast lastToast;
    private static TextView mLastToastText;

    /**
     * ???????????????
     *
     * @param activity ??????activity
     */
    public static void alwaysHideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * ??????EditText???????????????????????????????????????????????????????????????????????????????????????????????????EditText??????????????????
     *
     * @param v     ????????????view
     * @param event ????????????
     * @return ??????????????????????????????????????????????????????????????????edittext???
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        // ??????????????????EditText?????????????????????????????????????????????????????????????????????EditText????????????????????????????????????????????????
        return false;
    }

    public static void toastMsgByStringResource(int strResId) {
        toastMsgByStringResource(strResId, Toast.LENGTH_SHORT);
    }

    public static void toastMsgByStringResource(int strResId, int showTime) {
        String msg = getString(strResId);
        toastMsg(msg, showTime);
    }

    public static void toastMsg(String msg) {
        toastMsg(msg, Toast.LENGTH_SHORT);
    }

    public static void toastMsg(String msg, int showTime) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        AppContext.getAppHandler().sendMessage(AppContext.getAppHandler().obtainMessage(AppContext.CUSTOM_TOAST_MESSAGE, showTime, 0, msg));
    }

    public static void toastMsgByHandler(String msg, int showTime) {
        if (lastToast != null) {
            mLastToastText.setText(msg);
        } else {
            lastToast = new Toast(AppContext.baseContext);
            LayoutInflater inflater = LayoutInflater.from(AppContext.baseContext);
            View view = inflater.inflate(R.layout.toast, null);
            if (view != null) {
                mLastToastText = (TextView) view.findViewById(R.id.toast_text);
                mLastToastText.setText(msg);
                lastToast.setView(view);
            }
        }
        lastToast.setDuration(showTime);
        lastToast.setGravity(Gravity.CENTER | Gravity.TOP, 0, DisplayUtils.dip2px(170));
        lastToast.show();
        LogUtils.d("toast msg is =>" + msg);
    }

    /**
     * offsetY?????????????????????Y????????????
     */
    public static void toastMsg(String msg, int showTime, int yOffset) {
        //?????????Y?????????????????????X???????????????
//        if (TextUtils.isEmpty(msg)) {
//            return;
//        }
//        if (lastToast != null) {
//            mLastToastText.setText(msg);
//        } else {
//            lastToast = new Toast(AppContext.baseContext);
//            LayoutInflater inflater = LayoutInflater.from(AppContext.baseContext);
//            View view = inflater.inflate(R.layout.toast, null);
//            if (view != null) {
//                mLastToastText = (TextView) view.findViewById(R.id.toast_text);
//                mLastToastText.setText(msg);
//                view.setBackgroundResource(R.drawable.tip_bg);
//                lastToast.setView(view);
//            }
//        }
//        lastToast.setDuration(showTime);
//        lastToast.setGravity(Gravity.CENTER | Gravity.TOP, 0, yOffset);
//        lastToast.show();
//        //???toast???????????????
//        lastToast = null;
        LogUtils.d("toast reset location and msg is =>" + msg);
    }

    public static void toastDismiss() {
        if (lastToast != null) {
            lastToast.cancel();
        }
    }

    public static String getString(int id) {
        return AppContext.baseContext.getResources().getString(id);
    }

    /**
     * ????????????
     *
     * @param userAvatar ????????????
     * @param imageView  ??????view
     */
    public static void displayAvatarImage(String userAvatar, YSRLDraweeView imageView, Integer resId) {
        display200Image(userAvatar, imageView, resId);
    }

    public static void displayBigAvatarImage(String userAvatar, YSRLDraweeView imageView, Integer resId) {
        display300Image(userAvatar, imageView, resId);
    }

    public static void displayAvatarBgImage(String userAvatar, YSRLDraweeView imageView, Integer resId) {
        display600Image(userAvatar, imageView, resId);
    }

    //specified width load method
    public static void display200Image(String pic, YSRLDraweeView imageView, Integer resId) {
        displayImage(pic, imageView, 200, resId, null);
    }

    public static void display300Image(String pic, YSRLDraweeView imageView, Integer resId) {
        displayImage(pic, imageView, 300, resId, null);
    }

    public static void display300Image(String pic, YSRLDraweeView imageView, Integer resId, BaseControllerListener listener) {
        displayImage(pic, imageView, 300, resId, listener);
    }

    public static void display400Image(String pic, YSRLDraweeView imageView, Integer resId) {
        displayImage(pic, imageView, 400, resId, null);
    }

    public static void display600Image(String pic, YSRLDraweeView imageView, Integer resId) {
        displayImage(pic, imageView, 600, resId, null);
    }

    public static void display600Image(String pic, YSRLDraweeView imageView, Integer resId, BaseControllerListener listener) {
        displayImage(pic, imageView, 600, resId, listener);
    }

    public static void displayImageAsBg(String pic, YSRLDraweeView imageView, int width) {
        displayImage(pic, imageView, width, R.drawable.def_loading_img, null);
    }

    public static void displayImageAsBg(String pic, YSRLDraweeView imageView, int width, BaseControllerListener listener) {
        displayImage(pic, imageView, width, R.drawable.def_loading_img, listener);
    }

    public static void displayRoundImage(String pic, YSRLDraweeView imageView, int width) {
        try {
            pic = getPicUrl(pic, width);
            if (!TextUtils.isEmpty(pic)) {
                GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                        .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, R.drawable.def_round_loading_img), ScalingUtils.ScaleType.CENTER_INSIDE)
                        .setRoundingParams(RoundingParams.fromCornersRadius(DisplayUtils.dip2px(10)))
                        .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP).build();
                imageView.setHierarchy(hierarchy);
                imageView.setImageURI(Uri.parse(pic));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param pic       ????????????
     * @param imageView ??????view
     * @param width     ????????????
     */
    public static void displayImage(String pic, YSRLDraweeView imageView, int width) {
        displayImage(pic, imageView, width, null, null);
    }

    /**
     * ????????????
     *
     * @param resId ????????????
     */
    public static void displayImage(String picUrl, YSRLDraweeView imageView, int width, Integer resId) {
        try {
            picUrl = getPicUrl(picUrl, width);
            if (!TextUtils.isEmpty(picUrl)) {
                if (resId != null) {
                    GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
                    if (hierarchy == null) {
                        hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                                .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, resId), ScalingUtils.ScaleType.CENTER_INSIDE).build();
                        imageView.setHierarchy(hierarchy);
                    } else {
                        hierarchy.setPlaceholderImage(resId);
                    }
                }
                imageView.setImageURI(Uri.parse(picUrl));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param listener ?????????
     */
    public static void displayImage(String picUrl, YSRLDraweeView imageView, int width, Integer resId, BaseControllerListener listener) {
        try {
            picUrl = getPicUrl(picUrl, width);
            if (!TextUtils.isEmpty(picUrl)) {
                if (resId != null) {
                    if (listener != null) {
                        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
                        if (hierarchy == null) {
                            hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                                    .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, resId), ScalingUtils.ScaleType.CENTER_INSIDE).build();
                            imageView.setHierarchy(hierarchy);
                        } else {
                            hierarchy.setPlaceholderImage(resId);
                        }
                        DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(Uri.parse(picUrl)).setOldController(imageView.getController()).setControllerListener(listener).build();
                        imageView.setController(controller);
                    } else {
                        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
                        if (hierarchy == null) {
                            hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                                    .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, resId), ScalingUtils.ScaleType.CENTER_INSIDE).build();
                            imageView.setHierarchy(hierarchy);
                        } else {
                            hierarchy.setPlaceholderImage(resId);
                        }
                        imageView.setImageURI(Uri.parse(picUrl));
                    }
                } else {
                    if (listener != null) {
                        DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(Uri.parse(picUrl)).setOldController(imageView.getController()).setControllerListener(listener).build();
                        imageView.setController(controller);
                    } else {
                        imageView.setImageURI(Uri.parse(picUrl));
                    }
                    //??????????????????????????????
                    GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
                    if (hierarchy != null) {
                        hierarchy.setPlaceholderImage(null);
                    }
                }
            } else {
                //pic ??????,???????????????
                if (resId != null) {
                    GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
                    if (hierarchy == null) {
                        hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                                .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, resId), ScalingUtils.ScaleType.CENTER_INSIDE).build();
                        imageView.setHierarchy(hierarchy);
                    } else {
                        hierarchy.setPlaceholderImage(resId);
                    }
                    imageView.setHierarchy(hierarchy);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void displayImageMe(final String path, final ImageView imageView, final int width, Integer resId, final Activity activity, final Handler handler) {
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String picUrl = UIUtils.getPicUrl(path, width, FORMAT_JPG_TAG);
                if (picUrl.contains(FILE_HEADER_TAG)) {
                    picUrl = picUrl.substring(FILE_HEADER_TAG.length(), picUrl.length());
                }
                final Bitmap bitmap = ImageUtils.getBitmapFromNetUrl(picUrl, 0, 0, AppConfig.API_IMG_URL);
                if (bitmap != null) {
                    Message msg = Message.obtain();
                    msg.obj = bitmap;
                    msg.what = ImageZoomViewer.STATUS_GET_IMAGE_SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(ImageZoomViewer.STATUS_GET_IMAGE_FAIL);
                }
            }
        });
    }

    public static void setImageResource(YSRLDraweeView imageView, Integer resId) {
        try {
            if (imageView == null || resId == null) {
                return;
            }
            GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
            if (hierarchy == null) {
                hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                        .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, resId), ScalingUtils.ScaleType.CENTER_CROP).build();
            } else {
                hierarchy.setPlaceholderImage(resId);
            }
            imageView.setHierarchy(hierarchy);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????????????????
     *
     * @param radius ??????????????????
     */
    public static void displayBlurImage(YSRLDraweeView imageView, String picUrl, int width, int radius, BaseControllerListener listener) {
        try {
            if (imageView == null || TextUtils.isEmpty(picUrl)) {
                return;
            }
            picUrl = getPicUrl(picUrl, width);
            BlurPostprocessor processor = new BlurPostprocessor(AppContext.baseContext, radius);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(picUrl))
                    .setPostprocessor(processor)
                    .build();
            PipelineDraweeController controller =
                    (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(imageView.getController())
                            .setControllerListener(listener)
                            .build();
            imageView.setController(controller);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void playGif(YSRLDraweeView imageView, Integer resId, BaseControllerListener listener) {
        try {
            if (imageView == null || resId == null) {
                return;
            }
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(imageView.getController())
                    .setAutoPlayAnimations(true)
                    .setUri(Uri.parse("res://com.qizhu.rili/" + resId))
                    .setControllerListener(listener)
                    .build();
            imageView.setController(controller);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     *
     * @param subscriber ???????????????
     */
    public static void displayAndDownload(String picUrl, YSRLDraweeView imageView, Integer resId, BaseBitmapDataSubscriber subscriber) {
        try {
            picUrl = getPicUrl(picUrl, 600);

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(picUrl)).setProgressiveRenderingEnabled(true).build();
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, AppContext.baseContext);
            dataSource.subscribe(subscriber, CallerThreadExecutor.getInstance());

            GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
            if (hierarchy == null) {
                hierarchy = GenericDraweeHierarchyBuilder.newInstance(AppContext.baseContext.getResources())
                        .setPlaceholderImage(ContextCompat.getDrawable(AppContext.baseContext, resId), ScalingUtils.ScaleType.CENTER_INSIDE).build();
                imageView.setHierarchy(hierarchy);
            } else {
                hierarchy.setPlaceholderImage(resId);
            }

            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(imageView.getController())
                    .setImageRequest(imageRequest)
                    .build();
            imageView.setController(controller);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????????????????web?????????
     *
     * @param pic   ????????????
     * @param width ??????
     */
    public static String getPicUrl(String pic, int width) {
        if (TextUtils.isEmpty(pic)) {
            return "";
        }
        //pic??????????????????????????????http??????
        StringBuilder builder = new StringBuilder();
        if (!pic.contains(NO_HEADER_TAG) || pic.startsWith(HTTP_HEADER_TAG) || pic.startsWith(HTTPS_HEADER_TAG)) {
            if (pic.contains(AppConfig.FILE_SAVEPATH)) {
                builder.append(FILE_HEADER_TAG).append(pic);
            } else {
                builder.append(getPicWebUrl(pic, width));
            }
        } else if (pic.startsWith(FILE_HEADER_TAG)) {
            builder.append(pic);
        }
        return builder.toString();
    }

    public static String getPicUrl(String pic, int width, String format) {
        if (TextUtils.isEmpty(pic)) {
            return "";
        }
        //pic??????????????????????????????http??????
        StringBuilder builder = new StringBuilder();
        if (!pic.contains(NO_HEADER_TAG) || pic.startsWith(HTTP_HEADER_TAG) || pic.startsWith(HTTPS_HEADER_TAG)) {
            if (pic.contains(AppConfig.FILE_SAVEPATH)) {
                builder.append(FILE_HEADER_TAG).append(pic);
            } else {
                builder.append(getPicFormatUrl(pic, width, format));
            }
        } else if (pic.startsWith(FILE_HEADER_TAG)) {
            builder.append(pic);
        }
        return builder.toString();
    }

    /**
     * ????????????????????? ??????web??????
     *
     * @param pic   ????????????
     * @param width ????????????
     */
    public static String getPicFormatUrl(String pic, int width, String format) {
        if (TextUtils.isEmpty(pic) || pic.contains(HTTP_HEADER_TAG) || pic.startsWith(HTTPS_HEADER_TAG)) {
            return pic;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(AppConfig.API_IMG_URL).append(pic);
        //??????width??????0??????????????????????????????
        if (width > 0) {
            builder.append(AppConfig.API_IMG_THUMBNAIL).append(THUMB_TAG).append(width).append(QUALITY_CRITICAL_TAG).append(format);
        }
        return builder.toString();
    }


    /**
     * ??????web??????
     *
     * @param pic   ????????????
     * @param width ????????????
     */
    public static String getPicWebUrl(String pic, int width) {
        if (TextUtils.isEmpty(pic) || pic.contains(HTTP_HEADER_TAG) || pic.startsWith(HTTPS_HEADER_TAG)) {
            return pic;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(AppConfig.API_IMG_URL).append(pic);
        //??????width??????0??????????????????????????????
        if (width > 0) {
            if (width >= 600) {
                builder.append(AppConfig.API_IMG_THUMBNAIL).append(THUMB_TAG).append(width).append(FORMAT_JPG_TAG);
            } else {
                builder.append(AppConfig.API_IMG_THUMBNAIL).append(THUMB_TAG).append(width).append(QUALITY_CRITICAL_TAG).append(FORMAT_JPG_TAG);
            }
        }
        return builder.toString();
    }

    /**
     * ???????????????png?????????????????????????????????????????????drawable????????????????????????????????????sdk??????bug
     *
     * @param resId ??????id
     * @param color ??????
     */
    public static Drawable getMutableDrawable(Resources res, int resId, int color) {
        Bitmap immutable = BitmapFactory.decodeResource(res, resId);
        Bitmap mutable = immutable.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(mutable);
        Paint p = new Paint();
        p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        c.drawBitmap(mutable, 0.f, 0.f, p);
        return new BitmapDrawable(res, mutable);
    }

    /**
     * ??????view???bitmap???,???????????????view??????????????????????????????bitmap??????????????????null
     */
    public static Bitmap getBitmapFromView(View v) {
        if (v != null) {
            v.clearFocus();
            v.setPressed(false);
            boolean willNotCache = v.willNotCacheDrawing();
            v.setWillNotCacheDrawing(false);
            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = v.getDrawingCacheBackgroundColor();
            v.setDrawingCacheBackgroundColor(0);
            if (color != 0) {
                v.destroyDrawingCache();
            }
            v.buildDrawingCache();
            Bitmap cacheBitmap = v.getDrawingCache();
            if (cacheBitmap == null) {
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            v.destroyDrawingCache();
            v.setWillNotCacheDrawing(willNotCache);
            v.setDrawingCacheBackgroundColor(color);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * ??????view???bitmap???,?????????????????????bitmap?????????view???????????????,??????????????????view?????????????????????????????????
     */
    public static Bitmap getBitmapFromView(View view, int defWidth, int defHeight) {
        Bitmap bitmap = null;
        try {
            int width = view.getWidth();
            int height = view.getHeight();
            if (width == 0 || height == 0) {
                width = defWidth;
                height = defHeight;
            }
            if (width != 0 && height != 0) {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.layout(0, 0, width, height);
                view.draw(canvas);
            }
        } catch (Throwable e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    /**
     * ???View?????????Bitmap???,???????????????????????????,????????????????????????bitmap
     *
     * @param view   ???????????????View
     * @param width  ???View?????????
     * @param height ???View?????????
     * @return ??????Bitmap??????
     * add by csj 13-11-6
     */
    public static Bitmap getViewBitmap(View view, int width, int height) {
        Bitmap bitmap = null;
        if (view != null) {
            view.clearFocus();
            view.setPressed(false);

            boolean willNotCache = view.willNotCacheDrawing();
            view.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = view.getDrawingCacheBackgroundColor();
            view.setDrawingCacheBackgroundColor(0);
            float alpha = view.getAlpha();
            view.setAlpha(1.0f);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            view.measure(widthSpec, heightSpec);
            view.layout(0, 0, width, height);

            view.buildDrawingCache();
            Bitmap cacheBitmap = view.getDrawingCache();
            if (cacheBitmap == null) {
                Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + view + ")");
                return null;
            }
            try {
                bitmap = Bitmap.createBitmap(cacheBitmap);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            // Restore the view
            view.setAlpha(alpha);
            view.destroyDrawingCache();
            view.setWillNotCacheDrawing(willNotCache);
            view.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }

    /**
     * ???View?????????Bitmap???,????????????????????????view??????????????????
     * ??????getDrawingCache????????????????????????????????????
     *
     * @return ??????Bitmap??????
     */
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        } catch (Throwable e) {
            //??????OOM,?????????????????????
            bitmap = Bitmap.createBitmap(view.getWidth() / 2, view.getHeight() / 2, Bitmap.Config.ARGB_8888);
            e.printStackTrace();
        }
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
        }

        return bitmap;
    }

    /**
     * ???????????????url????????????Bitmap??????
     * @param url
     * @return
     */
    public static Bitmap urlToBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    /**
     * ?????????????????????quality
     *
     * @param tmpBitmap ?????????bitmap
     * @param quality   ????????????
     */
    public static Bitmap compressBitmap(Bitmap tmpBitmap, int quality) {
        if (tmpBitmap == null) {
            return null;
        }
        //??????????????????
        ByteArrayOutputStream tmpOutputstream = new ByteArrayOutputStream();
        tmpBitmap.compress(Bitmap.CompressFormat.JPEG, quality, tmpOutputstream);
        byte[] bitmapBytes = tmpOutputstream.toByteArray();
        tmpBitmap.recycle();
        tmpBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        return tmpBitmap;
    }

    /**
     * ??????????????????
     *
     * @param textView ??????textview
     */
    public static void setVerticalTxt(TextView textView, CharSequence str) {
        if (str != null) {
            StringBuilder result = new StringBuilder("");
            int len = str.length();
            for (int i = 0; i < len; i++) {
                char strSingle = str.charAt(i);
                result.append(strSingle);
                if (i < len - 1) {
                    result.append("\n");
                }
            }
            textView.setText(result);
        } else {
            textView.setText("");
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param context    ?????????
     * @param contentTxt ??????view
     * @param content    ????????????
     */
    public static void addLinkToContent(final Context context, TextView contentTxt, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        SpannableString spanStr = new SpannableString(content);
        URLSpan[] spans = contentTxt.getUrls();
        int start = 0;
        int end = 0;
        String urlPattern = "(((https?|ysrl):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*))";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            final String urlStr = matcher.group();

            spanStr.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    String url = urlStr;
                    if (url.startsWith(" ")) {
                        url = url.trim();
                        url = "http://" + url;
                    }

                    LogUtils.d("?????????????????? urlStr = " + urlStr + ", url = " + url);
//                    NavigationByUrlUtils.navigate(url, context, true);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }


        //????????????
//        contentTxt.setText(SmileyManager.getSmileySpannable(spanStr, (int) contentTxt.getTextSize()));
        contentTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static ClickableSpan buildClickableSapn(final View.OnClickListener listener) {
        return new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false); //???????????????
            }

            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(view);
                }
            }
        };
    }

    /**
     * ???????????????
     *
     * @param txt ?????????textview??????
     */
    public static void setThruLine(TextView txt) {
        txt.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    /**
     * ???????????????
     *
     * @param view ?????????view??????
     */
    public static void setMargins(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.setLayoutParams(p);
        }
    }

    /**
     * ????????????????????????
     *
     * @param paramActivity ?????????activity??????
     */
    public static int getStatusBarHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.top;
    }

    /**
     * ??????listview????????????????????????
     */
    public static int getListViewFirstPosition(ListView listView) {
        return listView.getLastVisiblePosition();
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // ??????????????????
        File appDir = new File(AppConfig.USER_IMAGE_PATH);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ????????????????????????????????????
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ????????????????????????
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));

    }
}
