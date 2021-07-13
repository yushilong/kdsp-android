package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.FileUtils;
import com.qizhu.rili.utils.ImageUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.TouchImageView;

/**
 * 查看图片的activity，可以拖动缩放
 */
public class ImageZoomViewer extends BaseActivity {
    public static final int STATUS_GET_DATA_SUCCESS = 1;    //获取数据成功
    public static final int STATUS_GET_DATA_FAIL = 2;       //获取数据失败
    public static final int STATUS_GET_IMAGE_FAIL = 3;      //获取图片失败
    public static final int STATUS_GET_IMAGE_SUCCESS = 4;   //获取图片成功

    public static final String EXTRA = "imgPath";
    public static final int DELAYED_LOAD_DATA = 1001;

    private TouchImageView image;
    private View savePicBtn;

    private String imgPath;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STATUS_GET_DATA_SUCCESS:
                    String uri = msg.getData().getString("uri");
                    LogUtils.d("save pic to " + uri);
                    dismissLoadingDialog();
                    if (uri != null) {
                        UIUtils.toastMsg("已保存到本地相册！");
                    } else {
                        UIUtils.toastMsg("保存失败！");
                    }
                    break;
                case STATUS_GET_DATA_FAIL:
                    UIUtils.toastMsg("保存失败！");
                    break;
                case STATUS_GET_IMAGE_FAIL:
                    UIUtils.toastMsg("获取图片失败");
                    dismissLoadingDialog();
                    break;
                case STATUS_GET_IMAGE_SUCCESS:
                    if (msg.obj != null && msg.obj instanceof Bitmap) {
                        image.setImageBitmap((Bitmap) msg.obj);
                    }
                    dismissLoadingDialog();
                    break;
                case DELAYED_LOAD_DATA:
                    UIUtils.displayImageMe(imgPath, image, 600, null, ImageZoomViewer.this, handler);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_viewer_viewpager_item);
        imgPath = getIntent().getStringExtra(EXTRA);
        if (imgPath == null || imgPath.equals("")) {
            UIUtils.toastMsg("照片路径不正确！");
            finish();
        }
        initView();
        initClickListener();
        showLoadingDialog();
        if (!TextUtils.isEmpty(imgPath)) {
            handler.sendEmptyMessageDelayed(DELAYED_LOAD_DATA, 1000);
        }
    }

    private void initView() {
        image = (TouchImageView) findViewById(R.id.handler_image);
        savePicBtn = findViewById(R.id.save_pic_btn);
    }

    /**
     * 初始化按钮点击事件
     */
    private void initClickListener() {
        savePicBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                savePic();
            }
        });
        image.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 保存图片到本地相册
     */
    private void savePic() {
        showLoadingDialog();
        Bitmap bitmap = ImageUtils.drawableToBitmap(image.getDrawable());
        if (bitmap != null) {
            FileUtils.saveImgToAlbum(ImageZoomViewer.this, bitmap, handler);
        } else {
            FileUtils.saveImgToAlbumForNet(ImageZoomViewer.this, 600, imgPath, handler);
        }
    }

    /**
     * 跳转接口，可以用于adapter
     */
    public static void goToPage(Context context, String picPath) {
        Intent intent = new Intent(context, ImageZoomViewer.class);
        intent.putExtra(ImageZoomViewer.EXTRA, picPath);
        context.startActivity(intent);
    }
}
