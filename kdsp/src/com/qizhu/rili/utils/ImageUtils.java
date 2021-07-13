package com.qizhu.rili.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.widget.ImageView;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.StartupImage;
import com.qizhu.rili.db.KDSPStartupImageDBHandler;
import com.qizhu.rili.ui.activity.BaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * 图片处理类
 */
public class ImageUtils {


    public static final int THUMB_SIZE = 150;

    /**
     * 将图片等比例缩放
     * 会失真
     * Resizes the bitmap to fit in the window of given width and height
     *
     * @param bitmap - bitmap to resize
     * @param width  - width in pixels
     * @param height - height in pixels
     * @return - resized bitmap
     */
    public static Bitmap resizeBitmapToFit(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            LogUtils.d("bitmap is null!");
            return null;
        }

        LogUtils.d("resize pic begins");
        int originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();
        LogUtils.d(String.format("original width is %d, height is %d ", originalWidth, originalHeight));
        float ratioW = width / (float) originalWidth;
        float rationH = height / (float) originalHeight;
        float ratio = Math.min(ratioW, rationH);
        int resizeWidth = (int) (originalWidth * ratio), resizeHeight = (int) (originalHeight * ratio);
        LogUtils.d(String.format("resize width is %d, height is %d ", resizeWidth, resizeHeight));
        LogUtils.d("resize pic ends");

        return Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true);
    }

    /**
     * 将图片转化为固定大小，图片比例不变，自动填充透明背景
     * Resizes bitmap to exact size given by width and height. If the bitmap does not fit the given exactly, it creates a transparent bitmap of given size which includes the given
     * resized bitmap
     *
     * @param bitmap - bitmap to reisze
     * @param width  - target width in pixels
     * @param height - target height in pixels
     * @return resized bitmap
     */
    public static Bitmap resizeBitmapToSize(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            bitmap = resizeBitmapToFit(bitmap, width, height);
            Bitmap wideBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
            Canvas canvas = new Canvas(wideBitmap);
            canvas.drawBitmap(bitmap, (width - bitmap.getWidth()) / 2, (height - bitmap.getHeight()) / 2, null);
            return wideBitmap;
        } else {
            return null;
        }
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap  bitmap to reisze
     * @param w       target width in pixels
     * @param h       target height in pixels
     * @param recycle should recycle bitmap
     * @return resized bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h, boolean recycle) {
        Bitmap newbmp = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            LogUtils.d("zoomBitmap original w,h is " + width + "," + height);
            LogUtils.d("zoomBitmap post w,h is " + w + "," + h);
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            float scale = Math.min(scaleWidht, scaleHeight);
            //保证之前的长宽比
            matrix.postScale(scale, scale);
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            if (recycle && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return newbmp;
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap bitmap to reisze
     * @param w      target width in pixels
     * @param h      target height in pixels
     * @return resized bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        return zoomBitmap(bitmap, w, h, true);
    }

    /**
     * 得到指定大小的bitmap
     */
    public static Bitmap getFitBitmap(Bitmap bitmap, int w, int h) {
        Bitmap newbmp = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            LogUtils.d("zoomBitmap original w,h is " + width + "," + height);
            LogUtils.d("zoomBitmap post w,h is " + w + "," + h);
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            float scale = Math.min(scaleWidht, scaleHeight);
            //保证之前的长宽比
            matrix.postScale(scale, scale);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            newbmp = Bitmap.createBitmap(w, h, bitmap.getConfig());
            Canvas canvas = new Canvas(newbmp);
            canvas.drawColor(0xFFFFFFFF);
            canvas.drawBitmap(bitmap, 0, 0, null);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return newbmp;
    }

    /**
     * 更改bitmap透明部分的颜色
     */
    public static Bitmap changeBitmapTransColor(Bitmap bitmap, int color) {
        int bitmap_w = bitmap.getWidth();
        int bitmap_h = bitmap.getHeight();
        int[] arrayColor = new int[bitmap_w * bitmap_h];
        int count = 0;
        for (int i = 0; i < bitmap_h; i++) {
            for (int j = 0; j < bitmap_w; j++) {
                int color1 = bitmap.getPixel(j, i);
                //这里也可以取出 RGB 可以扩展一下 做更多的处理，暂时我只是要处理除了透明的颜色，改变其他的颜色
                if (color1 == -1) {
                    color1 = color;
                }
                arrayColor[count] = color1;
                count++;
            }
        }
        bitmap = Bitmap.createBitmap(arrayColor, bitmap_w, bitmap_h, Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    /**
     * 将Drawable转化为Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = null;
        //异常 java.lang.IllegalArgumentException: width and height must be > 0
        if (width > 0 && height > 0) {
            bitmap = Bitmap.createBitmap(width, height, drawable
                    .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }
        return bitmap;

    }


    /**
     * 获得圆角图片的方法
     *
     * @param bitmap  待转化的bitmap
     * @param roundPx 一般设成14
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setAntiAlias(true);

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    /**
     * 获取bitmap
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    public static Bitmap getBitmap(Context context, String fileName) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = context.openFileInput(fileName);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 获取bitmap
     *
     * @param filePath 文件路径
     */
    public static Bitmap getBitmapByPath(String filePath) {
        return getBitmapByPath(filePath, null);
    }


    /**
     * 获取经过压缩的bitmap，防止读取大文件内存溢出的错误，但不是精确的scale，是选择最近的一个比例进行缩放
     * oom时，会gc一下，重新读取图片
     *
     * @param filePath    文件路径
     * @param scaleWidth  缩放的宽
     * @param scaleHeight 缩放的高
     */
    public static Bitmap getScaledBitmapByPath(String filePath, int scaleWidth, int scaleHeight) {
        int tryTime = 0;
        while (tryTime < 2) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //设置此项，读取图片时，只会返回图片的大小，不会真正读取bitmap
                options.inJustDecodeBounds = true;
                Bitmap rtn = BitmapFactory.decodeFile(filePath, options);
                int outWidth, outHeight;
                if (options.outWidth < options.outHeight) {
                    outWidth = options.outWidth;
                    outHeight = options.outHeight;
                } else {
                    outWidth = options.outHeight;
                    outHeight = options.outWidth;
                }
                //图片的最大宽度不超过1600,如果传入的值不对，那么设置为默认宽度720
                if (scaleWidth > AppConfig.IMAGE_SCALE_MAX_WIDTH) {
                    scaleWidth = AppConfig.IMAGE_SCALE_MAX_WIDTH;
                } else if (scaleWidth <= 0) {
                    scaleWidth = 720;
                }
                int widthRatio = Math.round(outWidth / (float) scaleWidth);
                int heightRatio = Math.round(outHeight / (float) scaleHeight);
                int ratio = Math.max(widthRatio, heightRatio);
                //如果缩放的比例小于1，那么不缩放返回
                if (ratio < 1) {
                    ratio = 1;
                }
                LogUtils.d("getScaledBitmapByPath" + String.format("filePath is %s,original width is %d,height is %d,ratio is %d,scale width is %d", filePath, outWidth, outHeight, ratio, scaleWidth));
                //压缩率
                options.inSampleSize = ratio;
                options.inJustDecodeBounds = false;
                //此时会真正的读取经过压缩后的图片
                rtn = BitmapFactory.decodeFile(filePath, options);
                logBitmapWidthAndHeight("after scale bitmap by ratio ", rtn);
                return rtn;
            } catch (OutOfMemoryError error) {
                LogUtils.e("oom error", error);
                System.gc();
                tryTime++;
                try {
                    Thread.sleep(tryTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap getBitmapByPath(String filePath, BitmapFactory.Options opts) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, opts);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 获取bitmap
     *
     * @param file 文件
     */
    public static Bitmap getBitmapByFile(File file) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 获取BitmapFactory decode的选项参数
     */
    public static BitmapFactory.Options getBitmapFactoryOpt() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return opt;
    }

    /**
     * 获取resource中的字节数组
     */
    public static byte[] getBytesFromResource(Context ctx, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap b = BitmapFactory.decodeResource(ctx.getResources(), resId, opt);
        return getBytesFromBitmap(b);
    }

    /**
     * 获取网络图片的字节数组
     */
    public static byte[] getBytesFromUrl(String url) {
        Bitmap bitmap = getBitmapFromUrl(url);
        if (bitmap != null) {
            return getBytesFromBitmap(bitmap);
        }
        return null;
    }

    /**
     * 获取网络图片的字节数组
     */
    public static byte[] getBytesFromUrl(String url,int width,int height) {
        Bitmap bitmap = getBitmapFromUrl(url,width,height);
        if (bitmap != null) {
            return getBytesFromBitmap(bitmap);
        }
        return null;
    }


    /**
     * 如何从bitmap中获取字节数组
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return outputStream.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * 如何从bitmap中获取字节数组
     */
    public static int getByteCountFromOriginBitmap(Bitmap bitmap) {
        byte[] bytes = getBytesFromBitmap(bitmap);
        return bytes == null ? 0 : bytes.length;
    }

    public static InputStream getInputStreamFromBitmap(Bitmap bitmap) {
        byte[] bytes = getBytesFromBitmap(bitmap);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 将字节数组转化为bitmap
     */
    public static Bitmap bytesToBitmap(byte[] Bytes) {
        if (Bytes == null || Bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(Bytes, 0, Bytes.length);
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context 上下文
     * @param resId   资源名
     */
    public static Bitmap getResourceBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 以最省内存的方式读取本地资源的图片(150*150)
     *
     * @param context 上下文
     * @param resId   资源名
     */
    public static Bitmap getResourceThumbBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap thumbBmp = null;
        try {
            Bitmap bmp = BitmapFactory.decodeStream(is);
            thumbBmp = zoomBitmap(bmp, THUMB_SIZE, THUMB_SIZE);
            bmp.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return thumbBmp;
    }

    /**
     * 仅recycle imageView的bitmap，背景图片不释放
     *
     * @param imageView release的imageview
     */
    public static void releaseImgBitmap(ImageView imageView) {
        if (imageView == null) {
            return;
        }

        try {
            Drawable tmpDrawable = imageView.getDrawable();

            if (!(tmpDrawable instanceof BitmapDrawable)) {
                return;
            }
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            imageView.setImageResource(R.color.white);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片资源的inputstream
     */
    public static InputStream getInputStreamFromRes(Context context, int resId) {
        return context.getResources().openRawResource(resId);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 通过指定URL获取Bitmap对象(注意如果是服务器返回的url，需要先调用UIUtils.getPicWebUrl进行处理)
     * 注意从服务器获取bitmap必须为异步!!!
     *
     * @param picUrl 图片地址
     */
    public static Bitmap getBitmapFromUrl(String picUrl) {
        return getBitmapFromUrl(picUrl, THUMB_SIZE, THUMB_SIZE);
    }



    public static Bitmap getBigBitmapFromUrl(String picUrl) {

        if (TextUtils.isEmpty(picUrl)) {
            return null;
        }
        Bitmap bmp = null;
        //判断图片路径为本地路径
        boolean isLocalUrl = picUrl.contains(AppConfig.FILE_SAVEPATH);
        if (isLocalUrl) {
            File file = new File(picUrl);
            if (!file.exists()) {
                LogUtils.d("getBitmapFromUrl 文件:" + picUrl + "不存在");
                return null;
            }
            LogUtils.d("YSRL suit orientation :" + readPictureDegree(picUrl));

            bmp = BitmapFactory.decodeFile(picUrl);

        } else {
            InputStream inputStream = null;
            try {
                URL url = new URL(picUrl);
                inputStream = url.openStream();
                bmp = BitmapFactory.decodeStream(inputStream);

            } catch (Exception e) {
                LogUtils.d("ImageUtils.getBitmapFromUrl failed url = " + picUrl);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        LogUtils.d("ImageUtils.getBitmapFromUrl inputsteam.close url = " + picUrl);
                    }
                }
            }
        }
        return bmp;

    }

    /**
     * 判断是否为本地地址
     *
     * @param picUrl
     * @param scaleWidth
     * @param scaleHeight
     * @param localContain
     * @return
     */
    public static Bitmap getBitmapFromUrl(String picUrl, int scaleWidth, int scaleHeight, String localContain) {
        if (TextUtils.isEmpty(picUrl)) {
            return null;
        }
        Bitmap thumbBmp = null;
        //判断图片路径为本地路径
        boolean isLocalUrl = picUrl.contains(localContain);
        LogUtils.i("---getBitmapFromUrl-------" + picUrl);
        if (isLocalUrl) {
            thumbBmp = loadLocalImage(picUrl, scaleWidth, scaleHeight);
        } else {
            thumbBmp = loadNetImage(picUrl, scaleWidth, scaleHeight);
        }
        return thumbBmp;
    }

    /**
     * 判断是否为网络地址
     *
     * @param picUrl
     * @param scaleWidth
     * @param scaleHeight
     * @param newContain
     * @return
     */
    public static Bitmap getBitmapFromNetUrl(String picUrl, int scaleWidth, int scaleHeight, String newContain) {
        if (TextUtils.isEmpty(picUrl)) {
            return null;
        }
        Bitmap thumbBmp = null;
        //判断图片路径为网络路径
        boolean isNetUrl = picUrl.contains(newContain) || picUrl.startsWith(UIUtils.HTTP_HEADER_TAG) || picUrl.startsWith(UIUtils.HTTPS_HEADER_TAG);
        LogUtils.i("---getBitmapFromUrl-------" + picUrl);
        if (isNetUrl) {
            thumbBmp = loadNetImage(picUrl, scaleWidth, scaleHeight);
        } else {
            thumbBmp = loadLocalImage(picUrl, scaleWidth, scaleHeight);
        }
        return thumbBmp;
    }

    /**
     * 从服务器获取数据，因此必须要为异步
     */
    private static Bitmap loadNetImage(String picUrl, int scaleWidth, int scaleHeight) {
        InputStream inputStream = null;
        Bitmap thumbBmp = null;
        try {
            URL url = new URL(picUrl);
            inputStream = url.openStream();
            //压缩处理,防止图片太大
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeStream(inputStream, null, options);
            int mWidth = options.outWidth;
            int mHeight = options.outHeight;
            int inSampleSize = 1;
            int screenWidth = AppContext.getScreenWidth();
            int screenHeight = AppContext.getScreenHeight();
            if (mHeight > mWidth) {
                inSampleSize = mHeight / screenHeight > 1 ? (int) Math.ceil(mHeight * 1f / screenHeight) : 1;
            } else {
                inSampleSize = mWidth / screenWidth > 1 ? (int) Math.ceil(mWidth * 1f / screenWidth) : 1;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;
            //获取bitmap的宽高之后流就不能复用了，必须重新新建
            if (inputStream != null) {
                inputStream.close();
            }
            inputStream = url.openStream();

            bmp = BitmapFactory.decodeStream(inputStream, null, options);
            if (scaleWidth > 0) {
                if (scaleHeight > 0) {
                    thumbBmp = getFitBitmap(bmp, scaleWidth, scaleHeight);
                } else {
                    thumbBmp = zoomBitmap(bmp, scaleWidth, Integer.MAX_VALUE);
                }
                bmp.recycle();
            } else if (scaleHeight > 0) {
                thumbBmp = zoomBitmap(bmp, Integer.MAX_VALUE, scaleHeight);
                bmp.recycle();
            } else {
                thumbBmp = bmp;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.d("ImageUtils.getBitmapFromUrl failed url = " + picUrl);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LogUtils.d("ImageUtils.getBitmapFromUrl inputsteam.close url = " + picUrl);
                }
            }
        }
        return thumbBmp;
    }

    private static Bitmap loadLocalImage(String picUrl, int scaleWidth, int scaleHeight) {
        //包含文件头可能造成获取图片失败，因此排除
        if (picUrl.contains(UIUtils.FILE_HEADER_TAG)) {
            picUrl = picUrl.substring(UIUtils.FILE_HEADER_TAG.length(), picUrl.length());
        }

        File file = new File(picUrl);
        if (!file.exists()) {
            LogUtils.d("getBitmapFromUrl 文件:" + picUrl + "不存在");
            return null;
        }
        int degree = ImageUtils.readPictureDegree(picUrl);
        LogUtils.d("YSRL suit orientation :" + readPictureDegree(picUrl));
        //压缩处理,防止图片太大
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picUrl, options);
        int mWidth = options.outWidth;
        int mHeight = options.outHeight;
        int inSampleSize = 1;
        int screenWidth = AppContext.getScreenWidth();
        int screenHeight = AppContext.getScreenHeight();
        if (mHeight > mWidth) {
            inSampleSize = mHeight / screenHeight > 1 ? (int) Math.ceil(mHeight * 1f / screenHeight) : 1;
        } else {
            inSampleSize = mWidth / screenWidth > 1 ? (int) Math.ceil(mWidth * 1f / screenWidth) : 1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        Bitmap bmp = BitmapFactory.decodeFile(picUrl, options);
        Bitmap thumbBmp = null;
        if (scaleWidth > 0) {
            if (scaleHeight > 0) {
                thumbBmp = getFitBitmap(bmp, scaleWidth, scaleHeight);
            } else {
                thumbBmp = zoomBitmap(bmp, scaleWidth, Integer.MAX_VALUE);
            }
            bmp.recycle();
        } else if (scaleHeight > 0) {
            thumbBmp = zoomBitmap(bmp, Integer.MAX_VALUE, scaleHeight);
            bmp.recycle();
        } else {
            thumbBmp = bmp;
        }
        if (thumbBmp != null) {
            thumbBmp = ImageUtils.rotaingImageView(degree, thumbBmp);
        }
        return thumbBmp;
    }

    public static Bitmap getBitmapFromUrl(String picUrl, int scaleWidth, int scaleHeight) {
        return getBitmapFromUrl(picUrl, scaleWidth, scaleHeight, AppConfig.FILE_SAVEPATH);
    }


    /**
     * 返回接近size的压缩比例，不改变图片原有的分辨率
     * 仅仅能改变bitmap存储的文件大小，转化为bitmap之后大小并不变化的
     * 因为图片的像素不会发生变化
     *
     * @param bitmap bitmap to reisze
     * @param size   比例
     */
    public static int compressBitmapToSpecifiedSizeQuality(Bitmap bitmap, int size) {
        if (bitmap == null || bitmap.isRecycled()) {
            return 100;
        }
        int quality = 100;
        int step = 3;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            ImageUtils.logBitmapWidthAndHeight("before compress => " + quality, bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            int actualLen = baos.size() / 1000;
            ImageUtils.logBitmapWidthAndHeight("after compress => " + quality + ",length is " + actualLen, bitmap);
            if (actualLen <= size) {
                LogUtils.d("compress complete,quality is %d!", quality);
                return quality;
            } else {
                //如果尺寸差距在20以内，则将step设为1，防止step过大导致图片过小
                if ((actualLen - size) < 30) {
                    step = 1;
                    LogUtils.d("compress step adjust size is %d,actual length is %d,step is %d", size, actualLen, step);
                }
                quality -= step;
                baos.reset();
            }
        }

    }

    /**
     * 压缩bitmap到指定大小
     *
     * @param bitmap 当前bitmap
     * @param size   以KB为单位
     */
    public static Bitmap compressBitmapToSpecifiedSize(Bitmap bitmap, int size) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapByteArr = byteArrayOutputStream.toByteArray();
        double actualLen = bitmapByteArr.length / 1000;
        LogUtils.d("camera => original pic length is " + actualLen);
        if (actualLen < size) {
            return bitmap;
        }
        double resizeNum = actualLen / size;
        double scaleNum = 1 / Math.sqrt(resizeNum);//缩放比例
        LogUtils.d("camera => scale num is " + scaleNum);
        Matrix matrix = new Matrix();
        matrix.postScale((float) scaleNum, (float) scaleNum);
        ImageUtils.logBitmapWidthAndHeight("before compress => ", bitmap);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ImageUtils.logBitmapWidthAndHeight("after compress => ", newBitmap);
        if (newBitmap != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

    }

    public static void logBitmapWidthAndHeight(String msg, Bitmap bitmap) {
        if (bitmap != null) {
            LogUtils.d(msg + "bitmap %s => width is %d,height is %d", bitmap, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            LogUtils.d("YSRL width:" + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) + ".height : " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + ", orientation :" + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 旋转图片
     *
     * @param angle  角度
     * @param bitmap 转化的bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        try {
            //旋转图片 动作
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            // 创建新的图片
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将picpath中的图片压缩后存入finalpath的路径,返回finalpath
     */
    public static String compressImage(String picPath) {
        String mFinalPicPath = "";
        //处理图片，按一定比例压缩存储
        if (picPath == null) {
            return "";
        }
        LogUtils.d("picPath is " + picPath);
        //压缩图片并且保存到缓存中，文件名暂定为时间戳，这样的话自动清理机制可以起作用
//        int degree = ImageUtils.readPictureDegree(picPath);
        String tmpFileName = FileUtils.getTempFileName();
        try {
            mFinalPicPath = FileUtils.getUserCacheDirPath() + tmpFileName + "new.jpg";
            //直接读取压缩过的图片，防止读入的过大，导致内存溢出
            Bitmap bitmap = ImageUtils.getScaledBitmapByPath(picPath, AppContext.getScreenWidth(), Integer.MAX_VALUE);

//            if (bitmap != null) {
//                bitmap = ImageUtils.rotaingImageView(degree, bitmap);
//            }

            if (bitmap != null) {
                //图片的压缩率为60%
                int quality = 60;
                //保存到新文件，不影响原有文件的使用
                FileUtils.saveImageToSD(mFinalPicPath, bitmap, quality, true);
            } else {
                UIUtils.toastMsg("图片上传失败，请重新上传");
            }

            LogUtils.d("camera => " + " original image path is %s , compress image path is %s", picPath, mFinalPicPath);
        } catch (Throwable e) {
            LogUtils.e("存储临时图片失败", e);
        }
        return mFinalPicPath;
    }

    /**
     * 下载启动界面图片
     */
    public static void loadStartImg() {
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("---> loadStartImg");
                List<StartupImage> imageList = KDSPStartupImageDBHandler.getAllList();
                //循环下载更新图片到数据库
                for (StartupImage startupImage : imageList) {
                    String startupImageURL = startupImage.imageUrl;
                    //若当前对应的启动图已经下载完毕，则不再次下载
                    if (startupImage.bytes != null) {
                        continue;
                    }
                    if (!TextUtils.isEmpty(startupImageURL) && !startupImageURL.equals("null")) {
                        //先获取原图
                        Bitmap bitmap = ImageUtils.getBigBitmapFromUrl(UIUtils.getPicWebUrl(startupImageURL, 600));
                        if (bitmap != null) {
                            //获取压缩比例
                            float scaleSize = 1;
                            try {
                                int width = bitmap.getWidth();
                                int height = bitmap.getHeight();
                                float scaleWidth = ((float) AppContext.getScreenWidth()) / width;
                                float scaleHeight = ((float) AppContext.getScreenHeight()) / height;
                                //由宽高比大的比例压缩，尽力保证清晰度
                                scaleSize = Math.max(scaleWidth, scaleHeight);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //只对bitmap进行缩小操作，不放大
                            Bitmap finalBitmap;
                            LogUtils.d("---> loadStartImg bitmap scaleSize = " + scaleSize);
                            if (scaleSize > 0 && scaleSize < 1) {
                                Matrix m = new Matrix();
                                m.setScale(scaleSize, scaleSize);
                                finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                                        .getHeight(), m, true);
                            } else {
                                finalBitmap = bitmap;
                            }

                            startupImage.bytes = ImageUtils.getBytesFromBitmap(finalBitmap);
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                            if (finalBitmap != bitmap && !finalBitmap.isRecycled()) {
                                finalBitmap.recycle();
                            }
                        }

                        KDSPStartupImageDBHandler.insertOrUpdate(startupImage);
                    }
                }
                LogUtils.d("---> loadStartImg complete");
            }
        });
    }

    /**
     * 对图片进行高斯模糊处理
     */
    public static void gaussianBlur(final BaseActivity baseActivity, final ImageView view, final String url) {
        AppContext.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (!TextUtils.isEmpty(url) && !url.equals("null")) {
                    bitmap = ImageUtils.getBigBitmapFromUrl(UIUtils.getPicWebUrl(url, 600));
                }
                if (bitmap == null) {
                    return;
                }

                try {
                    //开始高斯模糊指定图片
                    final long startMs = System.currentTimeMillis();
                    float radius = 5;
                    float scaleFactor = 4;

                    //先取1/4大小的缩略图
                    Bitmap newBmp = Bitmap.createScaledBitmap(bitmap, (int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor), true);

                    bitmap.recycle();
                    //对缩略图进行高斯模糊
                    newBmp = FastBlur.doBlur(newBmp, (int) radius, true);
                    final BitmapDrawable drawable = new BitmapDrawable(newBmp);
                    baseActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MethodCompat.setBackground(view, drawable);
                            LogUtils.d("cost " + (System.currentTimeMillis() - startMs) + "ms");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
