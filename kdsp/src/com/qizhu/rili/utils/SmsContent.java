package com.qizhu.rili.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.qizhu.rili.listener.SmsGetListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自动获取验证码
 */
public class SmsContent extends ContentObserver {
    private ContentResolver mContentResolver=null;
    SmsGetListener smsGetListener;

    public SmsContent(Context context, Handler handler, SmsGetListener smsGetListener) {
        super(handler);
        mContentResolver = context.getContentResolver();
        this.smsGetListener = smsGetListener;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //防止少量的权限异常 http://www.umeng.com/apps/4000004142510725308f2b05/error_types/54b7c0b64981ef87c28e7c0d
        try {
            Uri inUri = Uri.parse("content://sms/inbox");
            Cursor cursor = mContentResolver.query(inUri, new String[]{"_id", "address", "read", "body"}, null, null, "date desc");

            if (cursor != null && cursor.moveToNext()) {
                int smsbodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(smsbodyColumn);
                String dynamicPassword = getDynamicPass(smsBody);
                if (!TextUtils.isEmpty(dynamicPassword)) {
                    if (smsGetListener != null) {
                        smsGetListener.OnGetSms(dynamicPassword);
                    }

                    ContentValues values = new ContentValues();
                    values.put("read", "1"); // 修改短信为已读模式
                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    Uri updateIdUri = ContentUris.withAppendedId(inUri, id);
                    mContentResolver.update(updateIdUri, values, null, null);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取大于4位数字验证码
     * @param str 短信内容
     * @return 截取得到的4位动态密码
     */
    private String getDynamicPass(String str) {
        Pattern continuousNumberPattern = Pattern.compile("[0-9]{4,}");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            dynamicPassword = m.group();
        }
        return dynamicPassword;
    }
}
