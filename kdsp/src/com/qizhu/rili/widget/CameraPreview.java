package com.qizhu.rili.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.utils.CameraUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lindow on 6/7/16.
 * 相机预览
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final int MIN_PREVIEW_PIXELS = 480 * 320;

    private int viewWidth = 0;
    private int viewHeight = 0;

    /**
     * 监听接口
     */
    private OnCameraStatusListener listener;

    private SurfaceHolder mHolder;
    private Camera camera;

    //创建一个PictureCallback对象，并实现其中的onPictureTaken方法
    private PictureCallback pictureCallback = new PictureCallback() {

        // 该方法用于处理拍摄后的照片数据,data为拍摄的照片
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 停止照片拍摄
            try {
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 调用结束事件
            if (null != listener) {
                listener.onCameraStopped(data);
            }
        }
    };

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    // Preview类的构造方法
    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 获得SurfaceHolder对象
        mHolder = getHolder();
        mHolder.setKeepScreenOn(true);
        // 指定用于捕捉拍照事件的SurfaceHolder.Callback对象
        mHolder.addCallback(this);
    }

    // 在surface创建时激发
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.d("---> CameraPreview surfaceCreated");
        if (!CameraUtils.checkCameraHardware(getContext())) {
            openCameraError();
            return;
        }
        // 获得Camera对象
        camera = getCameraInstance();
        try {
            // 设置用于显示拍照摄像的SurfaceHolder对象
            if (mHolder == null) {
                init();
            }
            camera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            e.printStackTrace();
            // 释放手机摄像头
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }
    }

    // 在surface销毁时激发
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.d("---> CameraPreview surfaceDestroyed");
        // 释放手机摄像头
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    // 在surface的大小发生改变时触发
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        LogUtils.d("---> CameraPreview surfaceChanged,w = " + w + ",h = " + h);
        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        updateCameraParameters();
        // start preview with new settings
        try {
            camera.startPreview();
        } catch (Exception e) {
            LogUtils.e("---> Error starting camera preview:", e);
            openCameraError();
        }
    }

    /**
     * 获取摄像头实例
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras(); // get cameras number

            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    try {
                        c = Camera.open(camIdx);   //打开后置摄像头
                    } catch (Exception e) {
                        e.printStackTrace();
                        openCameraError();
                    }
                }
            }
            if (c == null) {
                c = Camera.open(0); // attempt to get a Camera instance
            }
        } catch (Exception e) {
            e.printStackTrace();
            openCameraError();
        }
        return c;
    }

    private void updateCameraParameters() {
        if (camera != null) {
            try {
                setParameters();
                LogUtils.d("---> CameraPreview updateCameraParameters ");
            } catch (Exception e) {
                LogUtils.d("---> CameraPreview updateCameraParameters error");
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置摄像头参数
     */
    private void setParameters() {
        Camera.Parameters parameters = camera.getParameters();

        getWidthAndHeight();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        LogUtils.d("---> CameraPreview setParameters viewWidth = " + viewWidth + ",viewHeight = " + viewHeight);
        Camera.Size optimalSize = getBestPreviewSize(viewWidth, viewHeight, parameters);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        Camera.Size pictureSize = getBestPictureSize(viewWidth, viewHeight, parameters);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        if (getContext().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.d("---> CameraPreview setParameters setRotation");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        }

        camera.setParameters(parameters);

        LogUtils.d("---> CameraPreview setParameters previewSize.width = " + optimalSize.width + ",previewSize.height = " + optimalSize.height);
        LogUtils.d("---> CameraPreview setParameters pictureSize.width = " + pictureSize.width + ",pictureSize.height = " + pictureSize.height);
    }

    // 进行拍照，并将拍摄的照片传入PictureCallback接口的onPictureTaken方法
    public void takePicture() {
        if (camera != null) {
            try {
                camera.takePicture(null, null, pictureCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 设置监听事件
    public void setOnCameraStatusListener(OnCameraStatusListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    public void stop() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    /**
     * 打开相机失败
     */
    private void openCameraError() {
        UIUtils.toastMsg("摄像头打开失败~");
    }

    /**
     * 相机拍照监听接口
     */
    public interface OnCameraStatusListener {
        // 相机拍照结束事件
        void onCameraStopped(byte[] data);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        viewWidth = MeasureSpec.getSize(widthSpec);
        viewHeight = MeasureSpec.getSize(heightSpec);
        super.onMeasure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));
    }

    private void getWidthAndHeight() {
        if (viewWidth == 0) {
            viewWidth = AppContext.getScreenWidth();
        }
        if (viewHeight == 0) {
            viewHeight = AppContext.getScreenHeight();
        }
    }

    /**
     * 获取最合适的预览尺寸
     */
    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {

        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            LogUtils.d("---> CameraPreview Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            return defaultSize;
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        double screenAspectRatio = (double) width / (double) height;

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            LogUtils.d("---> CameraPreview supportedPreviewSize : " + supportedPreviewSize.width + "," + supportedPreviewSize.height);
            //竖屏拍照，所以预览的宽高互换
            int realWidth = supportedPreviewSize.height;
            int realHeight = supportedPreviewSize.width;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }


            double aspectRatio = (double) realWidth / (double) realHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            LogUtils.d("---> CameraPreview aspectRatio : " + aspectRatio + " ,distortion = " + distortion);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            if (realWidth == width && realHeight == height) {
                LogUtils.d("---> CameraPreview Found preview size exactly matching screen size: " + width + "," + height);
                return supportedPreviewSize;
            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices
        // because of the additional computation needed. We're likely to get here on newer Android 4+ devices,
        // where the CPU is much more powerful.
        if (!supportedPreviewSizes.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            LogUtils.d("---> CameraPreview Using largest suitable preview size: " + largestPreview.width + "," + largestPreview.height);
            return largestPreview;
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        LogUtils.d("---> CameraPreview No suitable preview sizes, using default: " + defaultPreview.width + "," + defaultPreview.height);
        return defaultPreview;
    }

    private Camera.Size getBestPictureSize(int width, int height, Camera.Parameters parameters) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPictureSizes();
        if (rawSupportedSizes == null) {
            LogUtils.d("---> CameraPreview Device returned no supported picture sizes; using default");
            Camera.Size defaultSize = parameters.getPictureSize();
            return defaultSize;
        }

        // Sort by size, Ascending
        List<Camera.Size> supportedPictureSizes = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPictureSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return 1;
                }
                if (bPixels > aPixels) {
                    return -1;
                }
                return 0;
            }
        });

        double screenAspectRatio = (double) width / (double) height;

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPictureSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPictureSize = it.next();
            LogUtils.d("---> CameraPreview supportedPictureSize : " + supportedPictureSize.width + "," + supportedPictureSize.height);
            //竖屏拍照，所以宽高互换
            int realWidth = supportedPictureSize.height;
            int realHeight = supportedPictureSize.width;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            //如果尺寸比vie的尺寸小，那么忽略
            if (realWidth < width && realHeight < height) {
                it.remove();
                continue;
            }

            double aspectRatio = (double) realWidth / (double) realHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            LogUtils.d("---> CameraPreview getBestPictureSize aspectRatio : " + aspectRatio + " ,distortion = " + distortion);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            if (realWidth == width && realHeight == height) {
                LogUtils.d("---> CameraPreview Found picture size exactly matching screen size: " + width + "," + height);
                return supportedPictureSize;
            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices
        // because of the additional computation needed. We're likely to get here on newer Android 4+ devices,
        // where the CPU is much more powerful.
        if (!supportedPictureSizes.isEmpty()) {
            Camera.Size largestPreview = supportedPictureSizes.get(0);
            LogUtils.d("---> CameraPreview Using most suitable picture size: " + largestPreview.width + "," + largestPreview.height);
            return largestPreview;
        }

        // If there is nothing at all suitable, return current picture size
        Camera.Size defaultPreview = parameters.getPictureSize();
        LogUtils.d("---> CameraPreview No suitable picture sizes, using default: " + defaultPreview.width + "," + defaultPreview.height);
        return defaultPreview;
    }
}