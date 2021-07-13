package com.qizhu.rili.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.ui.activity.ImageZoomViewer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * 文件处理工具类
 */
public class FileUtils {

    public static final long KB_DIVIDE = 1024;
    public static final long MB_DIVIDE = 1048576;
    public static final long G_DIVIDE = 1073741824;

    /**
     * 写图片文件
     * 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @throws IOException
     */
    public static void saveImage(Context context, String fileName, Bitmap bitmap) throws IOException {
        saveImage(context, fileName, bitmap, 100);
    }

    public static void saveImage(Context context, String fileName, Bitmap bitmap, int quality) throws IOException {
        if (bitmap == null) return;

        FileOutputStream fos = new FileOutputStream(fileName);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] bytes = stream.toByteArray();
        fos.write(bytes);
        fos.close();
    }

    /**
     * 随机生成一个文件名存储bitmap
     *
     * @param bitmap  对应图片
     * @param quality 图片质量
     */
    public static String saveImage(Bitmap bitmap, int quality, boolean inCache, boolean recycle) {
        try {
            //不要加后缀名
            String fileName;
            if (inCache) {
                fileName = getUserCacheDirPath() + getTempFileName() + ".jpg";
            } else {
                fileName = getUserImageDirPath() + getTempFileName() + ".jpg";
            }
            saveImageToSD(fileName, bitmap, quality, recycle);
            return fileName;
        } catch (FileNotFoundException e) {
            LogUtils.e("file not found", e);
        } catch (IOException e) {
            LogUtils.e("io exception", e);
        }
        return null;
    }

    /**
     * 存储bitmap
     *
     * @param bitmap  对应图片
     * @param inCache 是否存储在缓存文件目录,此目录会随着用户手动清除缓存而清除
     * @param recycle 是否对bitmap进行回收
     */
    public static String saveImage(Bitmap bitmap, boolean inCache, boolean recycle) {
        return saveImage(bitmap, 100, inCache, recycle);
    }

    /**
     * 写图片文件到SD卡
     *
     * @throws IOException
     */
    public static void saveImageToSD(String filePath, Bitmap bitmap, int quality, boolean recycle) throws IOException {
        if (bitmap != null) {
            FileOutputStream fos = new FileOutputStream(filePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            byte[] bytes = stream.toByteArray();
            fos.write(bytes);
            fos.close();
            if (recycle) {
                bitmap.recycle();
            }
        }
    }

    /**
     * 从网络下载图片并保存到图库
     *
     * @param context
     * @param width
     * @param path
     * @param handler
     */
    public static void saveImgToAlbumForNet(Context context, final int width, final String path, final Handler handler) {
        if (handler == null) {
            return;
        }
        if (!TextUtils.isEmpty(path)) {
            final ContentResolver contentResolver = context.getContentResolver();
            AppContext.threadPoolExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    String picUrl = UIUtils.getPicUrl(path, width);
                    Bitmap bitmap = ImageUtils.getBitmapFromUrl(picUrl, 0, 0);
                    if (bitmap != null) {
                        String uri = MediaStore.Images.Media.insertImage(contentResolver, bitmap, FileUtils.getTempFileName() + ".jpg", "");
                        if (uri != null) {
                            BroadcastUtils.sendUpdateGalleryBroadcast();
                        }
                        Message msg = Message.obtain();
                        msg.what = ImageZoomViewer.STATUS_GET_DATA_SUCCESS;
                        Bundle data = new Bundle();
                        data.putString("uri", uri);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    } else {
                        //失败
                        Message msg = Message.obtain();
                        msg.what = ImageZoomViewer.STATUS_GET_DATA_FAIL;
                        handler.sendMessage(msg);
                    }
                }
            });
        } else {
            //失败
            Message msg = Message.obtain();
            msg.what = ImageZoomViewer.STATUS_GET_DATA_FAIL;
            handler.sendMessage(msg);
        }
    }

    /**
     * 保存图片到本地相册
     *
     * @param context 上下文
     * @param bitmap  当前照片
     * @param handler 回调
     */
    public static void saveImgToAlbum(final Context context, final Bitmap bitmap, final Handler handler) {
        if (bitmap != null) {
            final ContentResolver contentResolver = context.getContentResolver();
            AppContext.threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String uri = MediaStore.Images.Media.insertImage(contentResolver, bitmap, FileUtils.getTempFileName() + ".jpg", "");
                    if (uri != null) {
                        BroadcastUtils.sendUpdateGalleryBroadcast();
                    }
                    if (handler != null) {
                        Message msg = Message.obtain();
                        msg.what = ImageZoomViewer.STATUS_GET_DATA_SUCCESS;
                        Bundle data = new Bundle();
                        data.putString("uri", uri);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                }
            });
        }
    }

    /**
     * 使用当前时间戳拼接一个唯一的文件名
     */
    public static String getTempFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
        return format.format(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * 判断是否挂在了sdcard
     */
    public static boolean isStorageUsable() {
        //判断是否挂载了SD卡
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            UIUtils.toastMsgByStringResource(R.string.sd_card_not_mounted);
            return false;
        }
    }

    /**
     * 返回保存图片的文件夹路径
     */
    public static String getUserImageDirPath() {
        String path = AppConfig.USER_IMAGE_PATH;
        if (checkFileSavePath(path)) {
            return path;
        } else {
            return getAppPath();
        }
    }

    /**
     * 获取用户缓存文件夹
     */
    public static String getUserCacheDirPath() {
        if (checkFileSavePath(AppConfig.USER_CACHE_PATH)) {
            return AppConfig.USER_CACHE_PATH;
        } else {
            return getAppPath();
        }
    }

    /**
     * 获取文件缓存文件夹
     */
    public static String getUserFileDirPath() {
        if (checkFileSavePath(AppConfig.USER_FILE_PATH)) {
            return AppConfig.USER_FILE_PATH;
        } else {
            return getAppPath();
        }
    }

    /**
     * 返回保存日志的文件夹路径
     */
    public static String getUserLogDirPath() {
        String path = AppConfig.USER_LOG_PATH + AppContext.userId + "/";
        if (checkFileSavePath(path)) {
            return path;
        } else {
            return getAppPath();
        }
    }

    /**
     * 获取图片缓存文件夹
     */
    public static String getImageCacheDirPath() {
        String path = AppContext.baseContext.getCacheDir().getAbsolutePath() + "/image_cache";
        if (checkFileSavePath(path)) {
            LogUtils.d("---> path = " + path);
            return path;
        } else {
            return getAppPath();
        }
    }

    /**
     * 获取录音缓存文件夹，存储在SD卡上的voice目录下,录音部分只有用户手动清除缓存才清除
     */
    public static String getVoiceCacheDirPath() {
        String path = getUserCacheDirPath() + "/voice";
        if (checkFileSavePath(path)) {
            LogUtils.d("---> path = " + path);
            return path;
        } else {
            return getAppPath();
        }
    }

    /**
     * 应用在data的文件目录,即data/data/com.qizhu.rili
     */
    public static String getAppPath() {
        return AppContext.baseContext.getFilesDir().getAbsolutePath();
    }

    private static boolean checkFileSavePath(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    /**
     * 删除file
     *
     * @param cameraFilePath 指定文件路径
     */
    public static void deleteFileByPath(String cameraFilePath) {
        if (TextUtils.isEmpty(cameraFilePath)) {
            return;
        }
        File file = new File(cameraFilePath);
        if (file.exists()) {
            file.delete();
            LogUtils.d("delete file by path %s", cameraFilePath);
        }
    }

    /**
     * 从文件中获取字节数组
     *
     * @param picPath 文件路径
     * @return 字节数组
     */
    public static byte[] getBytesFromFile(String picPath) {
        FileInputStream fis = null;
        try {
            File file = new File(picPath);
            fis = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            return bytes;
        } catch (FileNotFoundException e) {
            LogUtils.e("file not found", e);
            return null;
        } catch (IOException e) {
            LogUtils.e("io exception", e);
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件的上一级文件夹名称
     */
    public static String getDir(String path) {
        String subString = path.substring(0, path.lastIndexOf('/'));
        return subString.substring(subString.lastIndexOf('/') + 1, subString.length());
    }

    /**
     * 获取文件的上一级文件夹路径
     */
    public static String getDirPath(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    /**
     * 获取文件夹大小
     */
    public static long getDirFilesSize(File f) {
        long size = 0;
        File files[] = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size = size + getDirFilesSize(file);
                } else {
                    size = size + file.length();
                }
            }
        }

        return size;
    }

    /**
     * 转换文件大小(b,kb,mb,g)
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#0.00");
        String fileSizeString;
        if (fileS < KB_DIVIDE) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < MB_DIVIDE) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < G_DIVIDE) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dirFile 被删除的文件夹
     * @return 目录删除成功返回true，否则返回false
     */
    public static void deleteDirectory(File dirFile) {
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else {
                deleteDirectory(file);
            }
        }
    }

    /**
     * 复制文件
     *
     * @param srcFileName  源文件
     * @param destFileName 复制生成的文件
     * @param overlay      是否覆盖目标文件
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            LogUtils.d("源文件：" + srcFileName + "不存在！");
            return false;
        } else if (!srcFile.isFile()) {
            LogUtils.d("复制文件失败，源文件：" + srcFileName + "不是一个文件！");
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                new File(destFileName).delete();
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>将文件转成base64 字符串</p>
     *
     * @param path 文件路径
     */
    public static String encodeBase64File(String path) {
        try {
            File file = new File(path);
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.NO_WRAP);
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 从uri得到文件路径
     * 兼容各个文件系统
     */
    public static String getFilePath(Context context, Uri uri) {
        String path = "";
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }

        //4.4的uri返回格式可能发生变化。4.4返回的却是content://com.Android.providers.media.documents/document/image:3951
        //4.3返回的是content://media/external/images/media/47
        if (TextUtils.isEmpty(path) && MethodCompat.isCompatible(Build.VERSION_CODES.KITKAT)) {
            String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };

            String[] column = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor5 = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                    selection, selectionArgs, null);
            if (cursor5 != null && cursor5.moveToFirst()) {
                int index = cursor5.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor5.getString(index);
                cursor5.close();
            }
        }
        return path;
    }
}
