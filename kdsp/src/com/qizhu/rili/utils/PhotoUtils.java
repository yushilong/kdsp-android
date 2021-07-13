package com.qizhu.rili.utils;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.ui.activity.BaseActivity;

import java.io.File;

/**
 * Created by lindow on 15/8/31.
 * 照片工具类
 */
public class PhotoUtils {

    /**
     * 拍照事件 imgName 图像名称，拍照时，图像会自动保存
     * @param activity 当前的界面
     * @return  返回拍照的照片路径
     */
    public static String chooseImageFromCamera(BaseActivity activity) {
        String cameraFilePath;
        //判断是否挂在了sdcard
        if (!FileUtils.isStorageUsable()) {
            UIUtils.toastMsg("请检查您的sd卡是否正确安装？");
            return "";
        }

        try {
            cameraFilePath = FileUtils.getUserImageDirPath() + FileUtils.getTempFileName() + ".jpg";
        } catch (Exception e) {
            UIUtils.toastMsgByStringResource(R.string.img_dir_not_exist);
            return "";
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //设定相机图片的保存路径
        Uri filePath = null;
        if (Build.VERSION.SDK_INT >= 24) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFilePath);
            filePath = activity.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            filePath = Uri.fromFile(new File(cameraFilePath));
        }

        //启动相机

        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        activity.startActivityForResult(intent, RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYCAMERA);
        return cameraFilePath;
    }

    /**
     * 请求相册事件
     */
    public static void chooseImageFromPhoto(BaseActivity activity) {
        //请求相册
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, activity.getResources().getString(R.string.choose_photo)), RequestCodeConfig.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }
}
