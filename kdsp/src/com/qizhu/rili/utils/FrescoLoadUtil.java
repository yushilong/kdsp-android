package com.qizhu.rili.utils;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FrescoLoadUtil {
    private static FrescoLoadUtil inst;
    private ExecutorService executeBackgroundTask = Executors.newSingleThreadExecutor();

    public static FrescoLoadUtil getInstance() {
        if (inst == null) {
            inst = new FrescoLoadUtil();
        }
        return inst;
    }

    //加载直接返回Bitmap
    public final void loadImageBitmap(String url, FrescoBitmapCallback<Bitmap> callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            fetch(Uri.parse(url), callback);
        } catch (Exception e) {
            //oom风险.
            e.printStackTrace();
            callback.onFailure(Uri.parse(url), e);
        }
    }

    private void fetch(final Uri uri, final FrescoBitmapCallback<Bitmap> callback) throws Exception {
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = requestBuilder.build();
        DataSource<CloseableReference<CloseableImage>> dataSource = ImagePipelineFactory.getInstance().getImagePipeline().fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                 @Override
                                 public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                                     if (callback == null)
                                         return;
                                     if (bitmap != null && !bitmap.isRecycled()) {
                                         handlerBackgroundTask(new Callable<Bitmap>() {
                                             @Override
                                             public Bitmap call() throws Exception {
                                                 final Bitmap resultBitmap = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
                                                 if (resultBitmap != null && !resultBitmap.isRecycled())
                                                     postResult(resultBitmap, uri, callback);
                                                 return resultBitmap;
                                             }
                                         });
                                     }
                                 }

                                 @Override
                                 public void onCancellation(DataSource<CloseableReference<CloseableImage>> dataSource) {
                                     super.onCancellation(dataSource);
                                     if (callback == null)
                                         return;
                                     callback.onCancel(uri);
                                 }

                                 @Override
                                 public void onFailureImpl(DataSource dataSource) {
                                     if (callback == null)
                                         return;
                                     Throwable throwable = null;
                                     if (dataSource != null) {
                                         throwable = dataSource.getFailureCause();
                                     }
                                     callback.onFailure(uri, throwable);
                                 }
                             },
                UiThreadImmediateExecutorService.getInstance());
    }

    /**
     * @param callable Callable
     * @param <T>      T
     * @return Future
     */
    private <T> Future<T> handlerBackgroundTask(Callable<T> callable) {
        return executeBackgroundTask.submit(callable);
    }

    /**
     * 回调UI线程中去
     *
     * @param result   result
     * @param uri      uri
     * @param callback FrescoBitmapCallback
     * @param <T>      T
     */
    private <T> void postResult(final T result, final Uri uri, final FrescoBitmapCallback<T> callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(uri, result);
            }
        });
    }


    public interface FrescoBitmapCallback<T> {

        void onSuccess(Uri uri, T result);

        void onFailure(Uri uri, Throwable throwable);

        void onCancel(Uri uri);
    }
}



