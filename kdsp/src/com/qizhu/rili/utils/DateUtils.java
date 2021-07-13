package com.qizhu.rili.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 */
public class DateUtils {

    private static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat simpleDateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static SimpleDateFormat simpleDateFormater2 = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat simpleDateFormater3 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private static SimpleDateFormat simpleDateFormater4 = new SimpleDateFormat("yyyy-MM");
    private static SimpleDateFormat simpleDateFormater5 = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat simpleDateFormater6 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat simpleDateFormater7 = new SimpleDateFormat("yyyy.MM.dd HH");
    private static SimpleDateFormat textDateFormater = new SimpleDateFormat("MM月dd日 HH:mm");

    private static SimpleDateFormat dayFormater = new SimpleDateFormat("dd");
    private static SimpleDateFormat monthFormater = new SimpleDateFormat("MM");
    private static SimpleDateFormat monthDayFormater = new SimpleDateFormat("MM.dd");
    private static SimpleDateFormat monthYearFormater = new SimpleDateFormat("yyyy.MM");
    private static SimpleDateFormat yearMonthDayFormater = new SimpleDateFormat("yyyy年MM月dd日");

    private static SimpleDateFormat monthDayFormater1 = new SimpleDateFormat("MM月dd日");
    private static SimpleDateFormat monthDayFormater2 = new SimpleDateFormat("MM/dd");
    private static SimpleDateFormat monthDayFormater3 = new SimpleDateFormat("dd/MM");
    private static SimpleDateFormat monthDayFormater4 = new SimpleDateFormat("MM/dd日");
    private static SimpleDateFormat yearFormater = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat dailyFormater = new SimpleDateFormat("dd/MM月   EEEE", Locale.CHINA);        //每日精选特殊日期字串
    private static SimpleDateFormat hourFormater = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat minFormater = new SimpleDateFormat("dd日 HH:mm");

    private static SimpleDateFormat chatTimeFormater = new SimpleDateFormat("yyyy-M-d  H:mm");
    private static SimpleDateFormat serverDateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat webDateFormater = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    /**
     * 将字符串转位日期类型  "yyyy-MM-dd HH:mm:ss"
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 以友好的方式显示时间
     */
    public static String friendlyTime(Date time) {
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();
        long intervalInMillis = cal.getTimeInMillis() - time.getTime();
        int days = (int) (intervalInMillis / 86400000);
        if (days == 0) {
            int hour = (int) (intervalInMillis / 3600000);
            if (hour == 0) {
                long seconds = intervalInMillis / 1000;
                if (seconds < 60) {
                    ftime = (seconds < 0 ? 0 : seconds) + "秒前";
                } else {
                    long minutes = seconds / 60;
                    ftime = minutes + "分钟前";
                }
            } else {
                ftime = hour + "小时前";
            }
        } else if (days <= 7) {
            ftime = days + "天前";
        } else if (days > 7) {
            ftime = monthDayFormater1.format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = simpleDateFormater.format(today);
            String timeDate = simpleDateFormater.format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断是否是同一天
     */
    public static boolean isToday(int time) {
        long currentTime = System.currentTimeMillis();
//        int day = 24 * 3600;//一天秒数
//        return currentTime/(day*1000) == time/day;

        String timeStr = simpleDateFormater.format(getDateFromInt(time));
        String currentStr = simpleDateFormater.format(new Date(currentTime));
        return timeStr.equals(currentStr);
    }

    /**
     * 判断是否是同一天
     */
    public static boolean isToday(long time) {
        long currentTime = System.currentTimeMillis();

        String timeStr = simpleDateFormater.format(new Date(time));
        String currentStr = simpleDateFormater.format(new Date(currentTime));
        return timeStr.equals(currentStr);
    }

    /**
     * 判断是否是同一年
     */
    public static boolean isThisYear(int time) {
        long currentTime = System.currentTimeMillis();
        Date dateNow = new Date(currentTime);
        Date date = getDateFromInt(time);

        Calendar clNow = Calendar.getInstance();
        Calendar cl = Calendar.getInstance();
        clNow.setTime(dateNow);
        cl.setTime(date);

        int yearNow = clNow.get(Calendar.YEAR);
        int year = cl.get(Calendar.YEAR);
        return yearNow == year;
    }

    /**
     * 判断是否是同一天
     */
    public static boolean isSameDay(long time1Mills, long time2Mills) {
        Date date1 = new Date(time1Mills);
        Date date2 = new Date(time2Mills);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断天数是不是单数
     */
    public static boolean isOddDay(int time) {
        Date date = getDateFromInt(time);
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int day = cl.get(Calendar.DAY_OF_MONTH);
        return day % 2 != 0;
    }

    /**
     * 返回日记列表中的相对时间
     */
    public static String getDiaryTime(int time) {
        String timeStr = null;
        if (isToday(time)) {
            timeStr = "今天";
        } else if (isThisYear(time)) {
            timeStr = monthDayFormater.format(getDateFromInt(time));
        } else {
            timeStr = yearMonthDayFormater.format(getDateFromInt(time));
        }
        return timeStr;
    }

    /**
     * 返回年、月、日的时间
     */
    public static String getYearMonthDayTime(int time) {
        return yearMonthDayFormater.format(getDateFromInt(time));
    }

    /**
     * 获取小时、分钟的秒值
     */
    public static int getIntSecondTime(int hour, int minite) {
        return (hour * 60 + minite) * 60;
    }

    /**
     * 从秒值中获取小时
     */
    public static int getHourFromIntTime(int second) {
        return second / 3600;
    }

    /**
     * 从秒值中获取当前小时
     */
    public static int getHourFromIntDayTime(int time) {
        Date date = getDateFromInt(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 从秒值中获取分钟
     */
    public static int getMinuteFromIntTime(int second) {
        return (second % 3600) / 60;
    }

    /**
     * 从秒值中获取秒
     */
    public static int getSecondFromIntTime(int second) {
        return second % 60;
    }

    /**
     * 以xx月xx日的格式显示时间
     */
    public static String getMonthDayTime(int time) {
        return monthDayFormater1.format(getDateFromInt(time));
    }

    /**
     * 以xx/xx的格式显示时间
     */
    public static String getMonthDayTime2(int time) {
        return monthDayFormater2.format(getDateFromInt(time));
    }

    /**
     * 以xx日/xx月的格式显示时间
     */
    public static String getMonthDayTime3(int time) {
        return monthDayFormater3.format(getDateFromInt(time));
    }

    /**
     * 以xx/xx日的格式显示时间
     */
    public static String getMonthDayTime4(int time) {
        return monthDayFormater4.format(getDateFromInt(time));
    }


    /**
     * 以xxxx的格式显示年
     */
    public static String getYearTime(int time) {
        return yearFormater.format(getDateFromInt(time));
    }

    /**
     * 显示星期
     */
    public static String getWeekTime(int time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateFromInt(time));
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String mWeek = null;
        if (1 == week) {
            mWeek = "星期日";
        } else if (2 == week) {
            mWeek = "星期一";
        } else if (3 == week) {
            mWeek = "星期二";
        } else if (4 == week) {
            mWeek = "星期三";
        } else if (5 == week) {
            mWeek = "星期四";
        } else if (6 == week) {
            mWeek = "星期五";
        } else if (7 == week) {
            mWeek = "星期六";
        }
        return mWeek;
    }


    /**
     * 以xxxx.xx的格式显示年月
     */
    public static String getMonthYearTime(int time) {
        return monthYearFormater.format(getDateFromInt(time));
    }

    /**
     * 以xx的格式显示天
     */
    public static String getDayTime(int time) {
        return dayFormater.format(getDateFromInt(time));
    }

    /**
     * 以xx天的格式显示今天
     */
    public static String getTodayTime() {
        return dayFormater.format(Calendar.getInstance().getTime());
    }

    public static String getTodayYearTime() {
        return simpleDateFormater5.format(Calendar.getInstance().getTime());
    }

    /**
     * 以MM的格式显示月份
     */
    public static String getMonthTime(int time) {
        return monthFormater.format(getDateFromInt(time));
    }

    /**
     * 从"yyyy-MM-dd"中获取int值的date，返回秒
     */
    public static int getIntFromSimpleDateString(String sDate) {
        Date date = toSimpleDate(sDate);
        if (date != null) {
            return (int) (date.getTime() / 1000);
        } else {
            return getCurrentIntTime();
        }
    }

    /**
     * 从"yyyy-MM-dd HH:mm:ss"中获取int值的date,返回秒
     */
    public static int getIntFromDateString(String sDate) {
        Date date = toDate(sDate);
        if (date != null) {
            return (int) (date.getTime() / 1000);
        } else {
            return getCurrentIntTime();
        }
    }

    /**
     * 获取当前到"yyyy-MM-dd"0点的秒数
     */
    public static int getIntSinceSimpleDateString(String sDate) {
        Date sinceDate = toSimpleDate(sDate);
        return getCurrentIntTime() - (int) (sinceDate.getTime() / 1000);
    }

    public static Date getDateFromInt(int time) {
        return new Date((long) time * 1000);
    }

    /**
     * 将int转化为"yyyy-MM-dd"的字符串
     */
    public static String getSimpleFormatDateFromInt(int time) {
        Date date = getDateFromInt(time);
        return simpleDateFormater.format(date);
    }

    /**
     * 将date转化为"yyyy-MM-dd"的字符串
     */
    public static String getSimpleFormatDateFromDate(Date date) {
        return simpleDateFormater.format(date);
    }

    /**
     * 将int转化为"yyyy.MM.dd"的字符串
     */
    public static String getSimpleFormatDate2FromInt(int time) {
        Date date = getDateFromInt(time);
        return simpleDateFormater2.format(date);
    }

    /**
     * 将date转化为"yyyy.MM.dd HH:mm"的字符串
     */
    public static String getSimpleFormatDate3FromInt(Date date) {
        return simpleDateFormater3.format(date);
    }

    /**
     * 将date转化为"yyyy-MM"的字符串
     */
    public static String getSimpleFormatDate4FromInt(Date date) {
        return simpleDateFormater4.format(date);
    }

    /**
     * 将date转化为"yyyyMMdd"的int值
     */
    public static String getSimpleFormatDate6FromInt(Date date) {
        return simpleDateFormater6.format(date);
    }

    /**
     * 将date转化为"yyyy.MM.dd HH"的字符串
     */
    public static String getSimpleFormatDate7FromInt(Date date) {
        return simpleDateFormater7.format(date);
    }

    /**
     * 将int转化为"yyyy-MM-dd HH:mm:ss"的字符串
     */
    public static String getFormatDateFromInt(int time) {
        Date date = getDateFromInt(time);
        return dateFormater.format(date);
    }

    /**
     * 将int转化为"yyyy-MM-dd HH:mm:ss"的字符串
     */
    public static String getFormatDateFromDate(Date date) {
        return dateFormater.format(date);
    }

    /**
     * 将int转化为"MM月dd日HH小时mm分钟"的字符串
     */
    public static String getTextFormatDateFromInt(int time) {
        Date date = getDateFromInt(time);
        return textDateFormater.format(date);
    }

    /**
     * 转化"yyyy-MM-dd"的字符串
     */
    public static Date toSimpleDate(String sDate) {
        try {
            return simpleDateFormater.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 将int转化为"dd/MM月 星期 HH:mm发布"的字符串
     * 每日精选日期格式
     */
    public static String getDailyFormatDateFromInt(int time) {
        Date date = getDateFromInt(time);
        return dailyFormater.format(date);
    }

    /**
     * 将int转化为"yyyy-M-d  H:mm"的字符串
     * 留言板格式
     */
    public static String getChatTimeFormatDateFromInt(int time) {
        Date date = getDateFromInt(time);
        return chatTimeFormater.format(date);
    }

    /**
     * 转化为"yyyy-MM-dd HH:mm"的字符串
     */
    public static String getServerTimeFormatDate(Date date) {
        return serverDateFormater.format(date);
    }

    /**
     * 转化为"yyyy-MM-dd-HH-mm"的字符串
     */
    public static String getWebTimeFormatDate(Date date) {
        return webDateFormater.format(date);
    }

    /**
     * 将int转化为"HH:mm"的字符串
     */
    public static String getHourFormatDateFromInt(int time) {
        Date date = getDateFromInt(time);
        return hourFormater.format(date);
    }

    /**
     * 转化为"dd日 HH:mm"的字符串
     */
    public static String getMinFormatDate(Date date) {
        return minFormater.format(date);
    }

    /**
     * 将服务器返回的时间字符串转化为date类型
     * serverDateFormater格式 "yyyy-MM-dd HH:mm" 形式
     */
    public static Date getServerTime(String serverDate) {
        try {
            return serverDateFormater.parse(serverDate);
        } catch (Exception e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * 将服务器返回的时间字符串转化为date类型
     * serverDateFormater格式 "yyyy-MM-dd HH-mm" 形式
     */
    public static Date getServer1Time(String serverDate) {
        try {
            return webDateFormater.parse(serverDate);
        } catch (Exception e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }


    /**
     * 将服务器返回的时间字符串转化为date类型
     * simpleDateFormater4格式 "yyyy-MM"形式
     */
    public static Date getsimpleDateFormater4Time(String serverDate) {
        try {
            return simpleDateFormater4.parse(serverDate);
        } catch (Exception e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * 获取服务器上的date
     */
    public static Date getServerDateTime() {
        try {
            String date = serverDateFormater.format(Calendar.getInstance().getTime());
            return serverDateFormater.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * 获取当前的秒
     */
    public static int getCurrentIntTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 将月份的数字转换为汉字
     */
    public static String formatMonthDigit(String month) {
        if (!TextUtils.isEmpty(month)) {
            if (month.equals("01")) {
                month = "一月";
            } else if (month.equals("02")) {
                month = "二月";
            } else if (month.equals("03")) {
                month = "三月";
            } else if (month.equals("04")) {
                month = "四月";
            } else if (month.equals("05")) {
                month = "五月";
            } else if (month.equals("06")) {
                month = "六月";
            } else if (month.equals("07")) {
                month = "七月";
            } else if (month.equals("08")) {
                month = "八月";
            } else if (month.equals("09")) {
                month = "九月";
            } else if (month.equals("10")) {
                month = "十月";
            } else if (month.equals("11")) {
                month = "十一月";
            } else if (month.equals("12")) {
                month = "十二月";
            }
        }
        return month;
    }

    private static final String[] constellationArr = {"水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"};
    private static final int[] constellationEdgeDay = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};

    /**
     * 从int类型的日期获取星座名称
     */
    public static String getConstellationFromIntDate(int time) {
        Date date = getDateFromInt(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        //default to return 魔羯
        return constellationArr[11];
    }

    /**
     * 从int类型的日期获取星座名称
     */
    public static String getConstellationFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        //default to return 魔羯
        return constellationArr[11];
    }

    /**
     * 转换时间为 天、时、分、秒 数组
     */
    public static int[] convertTimeToDHMS(long time) {
        int[] times = new int[4];
        times[0] = (int) (time / (24 * 60 * 60));
        times[1] = (int) ((time % (24 * 60 * 60)) / (60 * 60));
        times[2] = (int) (((time % (24 * 60 * 60)) % (60 * 60)) / 60);
        times[3] = (int) (((time % (24 * 60 * 60)) % (60 * 60)) % 60);
        return times;
    }

    /**
     * 计算两个日期之间相距多少天
     */
    public static int getDaysOfDate(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        //设置时间为0时
        calendar1.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar1.set(java.util.Calendar.MINUTE, 0);
        calendar1.set(java.util.Calendar.SECOND, 0);
        calendar2.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar2.set(java.util.Calendar.MINUTE, 0);
        calendar2.set(java.util.Calendar.SECOND, 0);
        //得到两个日期相差的天数
        int days = ((int) (calendar2.getTime().getTime() / 1000) - (int) (calendar1
                .getTime().getTime() / 1000)) / 3600 / 24;

        return days;
    }

    public static  String second2String(int second){
        String s;
        if(second/1000%60<10){
            return ""+second/1000/60+":0"+second/1000%60;
        }
        return ""+second/1000/60+":"+second/1000%60;
    }
}
