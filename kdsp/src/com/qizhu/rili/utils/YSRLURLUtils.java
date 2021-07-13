package com.qizhu.rili.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.qizhu.rili.AppConfig;
import com.qizhu.rili.AppContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URL工具类
 */
public class YSRLURLUtils {

    /**
     * 替换url的host
     *
     * @param originUrl 原地址
     * @param newHost   新的host
     */
    public static String replaceHost(String originUrl, String newHost) {
        String rtn = null;
        if (!TextUtils.isEmpty(originUrl)) {
            Uri uri = Uri.parse(originUrl);
            String host = uri.getHost();
            rtn = originUrl.replace(host, newHost);
        }
        return rtn;
    }

    /**
     * 判断是否是http的url
     *
     * @param url 原地址
     */
    public static boolean isHttpHost(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            return !TextUtils.isEmpty(scheme) && (scheme.equals("http") || scheme.equals("https"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否是YSRL的url
     *
     * @param url 原地址
     */
    public static boolean isYSRLHost(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            return !TextUtils.isEmpty(scheme) && scheme.equals("ysrl");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断url是否为ysrl的url
     */
    public static boolean hostContainYSRL(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                Uri uri = Uri.parse(url);
                String host = uri.getHost();
                return !TextUtils.isEmpty(host) && (host.contains("qi-zhu.com"));
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 如果为url并且没有http头部，自动加http头部
     *
     * @param url 原地址
     */
    public static String setHttpScheme(String url) {
        if (TextUtils.isEmpty(url) || isDigit(url)) {
            return url;
        }

        StringBuilder rtn = new StringBuilder(url);
        try {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if (TextUtils.isEmpty(scheme)) {
                rtn.insert(0, "http://");
            }
            return rtn.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断一个字符串是否都为数字
     */
    public static boolean isDigit(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher(strNum);
        return matcher.matches();
    }

    /**
     * 判断是否是http的url，并且host中不包含login
     *
     * @param url 原地址
     */
    public static boolean isHostNotContainLogin(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            return !TextUtils.isEmpty(host) && !host.contains("login");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 截取字符串前面的正整数，如"22天"得"22","18个人"得到"18".
     */
    public static String getQuantity(String regular) {
        int index = 0;
        for (int i = 0; i < regular.length(); i++) {
            char c = regular.charAt(i);
            if (Character.isDigit(c)) {
                if (i == regular.length() - 1) {
                    index = i + 1;
                } else {
                    index = i;
                }
            } else {
                index = i;
                break;
            }
        }
        return regular.substring(0, index);
    }


    /**
     * 获取分享的图片
     *
     * @param url 原地址
     */
    public static String getImageByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        // 获取img标签正则
        final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
        // 获取src路径的正则
        final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

        String imgUrl = "";
        Matcher urlMatcher = Pattern.compile(IMGURL_REG).matcher(url);
        while (urlMatcher.find()) {
            Matcher srcMatcher = Pattern.compile(IMGSRC_REG).matcher(urlMatcher.group());
            while (srcMatcher.find()) {
                imgUrl = srcMatcher.group().substring(0, srcMatcher.group().length() - 1);
                break;
            }
            break;
        }
        return imgUrl;
    }


    /**
     * 获取ImageUrl地址
     *
     * @param url 原地址
     */
    public static String getUrlTitle(String url) {
        String title = "";
        Matcher matcher = Pattern.compile("<title>.*?</title>").matcher(url);
        while (matcher.find()) {
            title = matcher.group().substring(7, matcher.group().length() - 8);
            break;
        }
        return title;
    }

    /**
     * 若Url中有userId与session，则进行替换，否则不做操作
     * 传入的链接userId和session必须均不为空才进行替换，否则不替换
     */
    public static String modifyQiZhuUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                Uri uri = Uri.parse(url);
                String session = uri.getQueryParameter(AppConfig.API_SESSION_NAME);
                String userId = uri.getQueryParameter(AppConfig.API_USERID_NAME);
                if (!TextUtils.isEmpty(session) && !TextUtils.isEmpty(userId)) {
                    if (!session.equals(AppContext.session) || !userId.equals(AppContext.userId)) {
                        String tempUrl = modifyUrl(url, AppConfig.API_USERID_NAME, AppContext.userId);
                        url = modifyUrl(tempUrl, AppConfig.API_SESSION_NAME, AppContext.session);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * 更改url中的参数
     *
     * @param url 原地址
     */
    public static String modifyUrl(String url, String key, String value) {
        String newUrl;
        int index = url.indexOf(key + "=");
        if (index != -1) {
            StringBuilder sb = new StringBuilder();
            sb.append(url.substring(0, index)).append(key).append("=")
                    .append(value);
            int idx = url.indexOf("&", index);
            if (idx != -1) {
                sb.append(url.substring(idx));
            }
            newUrl = sb.toString();
            return newUrl;
        }

        return url;
    }

    /**
     * 分享链接检测
     * 所有分享的链接，如果本身不带有f=xxx参数，统一加上f=share-qq/share-weixin/share-wxtimeline/share-qzone/share-weibo 参数
     *
     * @param url 原地址
     */
    public static String checkShareUrl(String url, String parameter) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (TextUtils.isEmpty(uri.getEncodedQuery())) {
                return url + "?" + parameter;
            }
            if (TextUtils.isEmpty(uri.getQueryParameter("f"))){
                return url + "&" + parameter;
            }
        }

        return url;
    }

    /**
     * 链接增加userId
     *
     * @param url 原地址
     */
    public static String addUserIdUrl(String url, String userId) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (TextUtils.isEmpty(uri.getEncodedQuery())) {
                return url + "?" + "userId=" + userId;
            }
            if (TextUtils.isEmpty(uri.getQueryParameter("userId"))){
                return url + "&" + "userId=" + userId;
            }
        }

        return url;
    }

    /**
     * 检测该包名下的应用是否存在
     */
    public static boolean checkPackage(Context context , String packageName)
    {
        if (packageName == null || "".equals(packageName))
            return false;
        try{
            context.getPackageManager().getApplicationInfo(packageName, PackageManager
                    .GET_UNINSTALLED_PACKAGES);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }

    }

}