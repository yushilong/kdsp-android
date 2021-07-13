package com.qizhu.rili.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtils {
    private static final Pattern URL_PATTERN = Pattern
            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    /**
     * 判断给定字符串是否空白串。
     * 空白串是指由空格、制表符、回车符、换行符组成的字符串
     * 若输入字符串为null或空字符串，返回true
     *
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String input, String mid) {
        if (input == null || "".equals(input) || mid.equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串转整数
     *
     * @param str      原始字串
     * @param defValue 默认值
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj 转换对象
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null) return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 字符串转整数
     *
     * @param str      原始字串
     * @param defValue 默认值
     */
    public static float toFloat(String str, float defValue) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }
    /**
     * 对象转整数
     *
     * @param obj 转换对象
     * @return 转换异常返回 0
     */
    public static float toFloat(Object obj) {
        if (obj == null) return 0;
        return toFloat(obj.toString(), 0);
    }
    /**
     * 对象转整数
     *
     * @param obj 转换对象
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b 转换对象
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String concatList(List list, String concatStr) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : list) {
            sb.append(obj.toString())
                    .append(concatStr);
        }
        if (sb.length() > 1) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return "";
        }
    }

    /**
     * 将指定包含逗号的字符串转化为字符串数组
     *
     * @param str 转换对象
     */
    public static String[] convertStrToArray2(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");//把","作为分割标志，然后把分割好的字符赋予StringTokenizer对象。
        String[] strArray = new String[st.countTokens()];//通过StringTokenizer 类的countTokens方法计算在生成异常之前可以调用此 tokenizer 的 nextToken 方法的次数。
        int i = 0;
        while (st.hasMoreTokens()) {//看看此 tokenizer 的字符串中是否还有更多的可用标记。
            strArray[i++] = st.nextToken().trim();//返回此 string tokenizer 的下一个标记。
        }
        return strArray;
    }

    /**
     * 将指定包含逗号的字符串转化为字符串容器
     *
     * @param str 转换对象
     */
    public static ArrayList<String> convertStrToList(String str) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (TextUtils.isEmpty(str)) {
            return arrayList;
        }
        StringTokenizer st = new StringTokenizer(str, ",");//把","作为分割标志，然后把分割好的字符赋予StringTokenizer对象。
        while (st.hasMoreTokens()) { //看看此 tokenizer 的字符串中是否还有更多的可用标记。
            arrayList.add(st.nextToken());
        }
        return arrayList;
    }

    /**
     * 定义分割常量 （#在集合中的含义是每个元素的分割，|主要用于map类型的集合用于key与value中的分割）
     */
    private static final String SEP1 = "#";
    private static final String SEP2 = "|";

    /**
     * List转换String
     *
     * @param list :需要转换的List
     * @return String转换后的字符串
     */
    public static String ListToString(List<?> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                // 如果值是list类型则调用自己
                if (list.get(i) instanceof List) {
                    sb.append(ListToString((List<?>) list.get(i)));
                    sb.append(SEP1);
                } else if (list.get(i) instanceof Map) {
                    sb.append(MapToString((Map<?, ?>) list.get(i)));
                    sb.append(SEP1);
                } else {
                    sb.append(list.get(i));
                    sb.append(SEP1);
                }
            }
        }
        return "L" + encodeBase64(sb.toString());
    }

    /**
     * Map转换String
     *
     * @param map :需要转换的Map
     * @return String转换后的字符串
     */
    public static String MapToString(Map<?, ?> map) {
        StringBuffer sb = new StringBuffer();
        // 遍历map
        for (Object obj : map.keySet()) {
            if (obj == null) {
                continue;
            }
            Object key = obj;
            Object value = map.get(key);
            if (value instanceof List<?>) {
                sb.append(key.toString()).append(SEP1).append(ListToString((List<?>) value));
                sb.append(SEP2);
            } else if (value instanceof Map<?, ?>) {
                sb.append(key.toString()).append(SEP1).append(MapToString((Map<?, ?>) value));
                sb.append(SEP2);
            } else {
                sb.append(key.toString()).append(SEP1).append(value.toString());
                sb.append(SEP2);
            }
        }
        return "M" + encodeBase64(sb.toString());
    }

    /**
     * String转换Map
     *
     * @param mapText :需要转换的字符串
     * @return Map<?,?>
     */
    public static Map<String, Object> StringToMap(String mapText) {

        if (mapText == null || mapText.equals("")) {
            return null;
        }
        mapText = mapText.substring(1);

        mapText = decodeBase64(mapText);

        Map<String, Object> map = new HashMap<String, Object>();
        String[] text = mapText.split("\\" + SEP2); // 转换为数组
        for (String str : text) {
            String[] keyText = str.split(SEP1); // 转换key与value的数组
            if (keyText.length <= 1) {
                continue;
            }
            String key = keyText[0]; // key
            String value = keyText[1]; // value
            if (value.charAt(0) == 'M') {
                Map<?, ?> map1 = StringToMap(value);
                map.put(key, map1);
            } else if (value.charAt(0) == 'L') {
                List<?> list = StringToList(value);
                map.put(key, list);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * String转换List
     *
     * @param listText :需要转换的文本
     * @return List<?>
     */
    public static List<Object> StringToList(String listText) {
        if (listText == null || listText.equals("")) {
            return null;
        }
        listText = listText.substring(1);

        listText = decodeBase64(listText);

        List<Object> list = new ArrayList<Object>();
        String[] text = listText.split(SEP1);
        for (String str : text) {
            if (str.charAt(0) == 'M') {
                Map<?, ?> map = StringToMap(str);
                list.add(map);
            } else if (str.charAt(0) == 'L') {
                List<?> lists = StringToList(str);
                list.add(lists);
            } else {
                list.add(str);
            }
        }
        return list;
    }

    /**
     * 编码
     *
     * @return String
     */
    public static String encodeBase64(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    /**
     * 解码
     *
     * @param str 转换对象
     */
    public static String decodeBase64(String str) {
        byte[] bt = null;
        try {
            bt = Base64.decode(str, Base64.DEFAULT);
        } finally {
            if (bt != null) {
                return new String(bt);
            } else {
                return null;
            }
        }
    }

    /**
     * 截取指定小数位数的double
     *
     * @param data  数据
     * @param count 位数
     */
    public static String splitDoubleStr(double data, int count) {
        String result = String.valueOf(data);
        int len = result.length();
        int index = result.indexOf(".");
        if (index != -1) {
            if (count > 0 && index + count + 1 <= len) {
                result = result.substring(0, index + count + 1);
            }
        }
        return result;
    }

    /**
     * 截取指定小数位数的double并四舍五入返回
     *
     * @param data  数据
     * @param count 位数
     */
    public static String roundingDoubleStr(double data, int count) {
        BigDecimal bd = new BigDecimal(data);
        double rtn = bd.setScale(count, BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.valueOf(rtn);
    }

    /**
     * 截取指定小数位数的double并舍入远离零返回
     * 即只要后面非0，则直接前一位加一
     *
     * @param data  数据
     * @param count 位数
     */
    public static String roundingUpDoubleStr(double data, int count) {
        BigDecimal bd = new BigDecimal(data);
        double rtn = bd.setScale(count, BigDecimal.ROUND_UP).doubleValue();
        return String.valueOf(rtn);
    }

    /**
     * 截取指定小数位数的double并舍入远离零返回
     * 即只要后面非0，则直接前一位加一
     *
     * @param data  数据
     * @param count 位数
     */
    public static double roundingUpDouble(double data, int count) {
        BigDecimal bd = new BigDecimal(data);
        return bd.setScale(count, BigDecimal.ROUND_UP).doubleValue();
    }

    /**
     * 根据服务器返回的noticeId获取通知栏的id
     */
    public static int getNotificationId(String noticeId) {
        int rtn = 0;
        if (!TextUtils.isEmpty(noticeId)) {
            int len = noticeId.length();
            String noticeStr = noticeId;
            if (len > 8) {
                noticeStr = noticeId.substring(len - 8, len);
            }
            try {
                rtn = Integer.parseInt(noticeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rtn;
    }

    /**
     * 将double型转换为百分比
     */
    public static String getPercent(Double percent) {
        java.text.NumberFormat percentFormat = java.text.NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2);          //最大小数位数
        percentFormat.setMaximumIntegerDigits(3);           //最大整数位数
        percentFormat.setMinimumFractionDigits(2);          //最小小数位数
        percentFormat.setMinimumIntegerDigits(1);           //最小整数位数
        return percentFormat.format(percent);
    }

    /**
     * 判断int型第几位是否为1
     *
     * @param a int型数字
     * @param N 位数
     */
    public static boolean getBoolOfInt(int a, int N) {
        if (N > 1) {
            return 1 == ((a >> (N - 1)) & 0x1);
        } else {
            return 1 == N && 1 == (a & 0x1);
        }
    }

    /**
     * 获取rating bar的正确比例，因为右侧有24%的空白
     */
    public static float getRatingFloat(float data) {
        int num = (int) data;
        float dec = (data - num) * 76 / 100;
        return num + dec;
    }

    /**
     * 转换大数字为万、百万等
     */
    public static String getSimpleNumber(int count) {
        String countStr = String.valueOf(count);
        if (countStr.length() < 5) {
            return countStr;
        }
        StringBuilder sb = new StringBuilder();
        DecimalFormat fd = new DecimalFormat("0.0");
        if (countStr.length() > 8) {
            return sb.append(countStr.substring(0, countStr.length() - 8))
                    .append("亿").toString();
        } else {
            if (countStr.length() == 5) {
                return sb.append(String.valueOf(fd.format(count / 10000d)))
                        .append("万").toString();
            }
            return sb.append(countStr.substring(0, countStr.length() - 4))
                    .append("万").toString();
        }
    }

    /**
     * 获取时间字段 例:( 周一到周日 08:09~25:00 )
     */
    public static int[] parseDateFromSupportTime(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            int[] times = new int[4];
            String regex = "(([0-1]?[0-9])|2[0-3]):[0-5]?[0-9]";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(str);
            if (m.find()) {
                String[] timeSplits1 = m.group().split(":");
                times[0] = Integer.valueOf(timeSplits1[0]);
                times[1] = Integer.valueOf(timeSplits1[1]);
                if (m.find()) {
                    String[] timeSplits2 = m.group().split(":");
                    times[2] = Integer.valueOf(timeSplits2[0]);
                    times[3] = Integer.valueOf(timeSplits2[1]);
                    return times;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 截取字串的指定长度,发生异常则默认返回空值
     *
     * @param str    指定字串
     * @param length 指定长度
     */
    public static String cutString(String str, int length) {
        if (str.length() <= length) {
            return str;
        } else {
            try {
                return str.substring(0, length - 1);
            } catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * 获得字符串某个字符的个数
     *
     * @param str 指定字串
     * @param a   指定字符
     */
    public static int getCountofChar(String str, char a) {
        char[] charArray = str.toCharArray();
        int result = 0;
        for (char temp : charArray) {
            if (a == temp) {
                result++;
            }
        }
        return result;
    }

    /**
     * 字符串的压缩
     *
     * @param str 待压缩的字符串
     * @return 返回压缩后的字符串
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        // 将 b.length 个字节写入此输出流
        gzip.write(str.getBytes());
        gzip.close();
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("ISO-8859-1");
    }

    /**
     * gzip解压字符串
     *
     * @param str 指定字串
     */
    public static String unCompress(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            // 创建一个新的 byte 数组输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
            ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            // 使用默认缓冲区大小创建新的输入流
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) { // 将未压缩数据读入字节数组
                // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
                out.write(buffer, 0, n);
            }
            // toString()使用平台默认编码，也可以显式的指定如toString()
            return out.toString("GBK");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取指定url的文件名后缀
     *
     * @param url 指定字串
     */
    public static String getFileFormat(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    /**
     * 验证是否为中文字符
     *
     * @param c 输入字符
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 验证是否为中文字符串
     */
    public static boolean isChineseStr(String str) {
        for (char c : str.toCharArray()) {
            if (!isChinese(c)) {
                return false;
            }
        }
        return true;
    }

    public static String price2String(int price) {
        double mPrice = ((double)price)/(double)100;
//       if(mPrice - new Double(mPrice).intValue() > 0) {
//           return  StringUtils.roundingDoubleStr( mPrice,2);
//       }

        return ""+mPrice;
    }

}
