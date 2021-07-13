package com.qizhu.rili.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.qizhu.rili.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Zhang on 2015/4/16.
 * 日期时间bean，封装系统的date对象
 * 月份表示与date一致，0-11表示1-12月
 */
public class DateTime implements Parcelable {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int min;
    public int sec;

    public DateTime() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        sec = c.get(Calendar.SECOND);
    }

    public DateTime(int num) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, num);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        sec = c.get(Calendar.SECOND);
    }

    /**
     * 是否仅仅是到天
     */
    public DateTime(boolean toDay) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        if (!toDay) {
            hour = c.get(Calendar.HOUR_OF_DAY);
            min = c.get(Calendar.MINUTE);
            sec = c.get(Calendar.SECOND);
        }
    }

    public DateTime(long timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        sec = c.get(Calendar.SECOND);
    }

    public DateTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        sec = c.get(Calendar.SECOND);
    }

    /**
     * 使用此构造函数若月份为1-12则应该月份减1
     */
    public DateTime(int year, int month, int day) {
        this(year, month, day, 0, 0, 0);
    }

    public DateTime(Calendar c) {
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH);
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.min = c.get(Calendar.MINUTE);
        this.sec = c.get(Calendar.SECOND);
    }

    /**
     * 使用此构造函数若月份为1-12则应该月份减1
     */
    public DateTime(int year, int month, int day, int hour, int min, int sec) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, min, sec);
        return calendar.getTime();
    }

    public long getTime() {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, min, sec);
        return c.getTimeInMillis();
    }

    @Override
    public String toString() {
        return year + "." + (month + 1) + "." + day + " " + hour + ":" + min + ":" + sec;
    }

    /**
     * Y.M.D 形式返回
     */
    public String toYearString() {
        return year + "." + (month + 1) + "." + day;
    }

    /**
     * Y-M 形式返回
     */
    public String toMonthString() {
        return year + "-" + (month + 1);
    }

    /**
     * Y-M-D 形式返回
     */
    public String toServerDayString() {
        return year + "-" + (month + 1) + "-" + day;
    }
    /**
     * Y-M-D 形式返回
     */
    public String toServerMinString() {
        return year + "-" + (month + 1) + "-" + day + "-" + hour + "-" + min;
    }

    /**
     * M.D 形式返回
     */
    public String toDayString() {
        return (month + 1) + "." + day;
    }

    /**
     * 精确到分钟
     */
    public String toMinString() {
        return DateUtils.getSimpleFormatDate3FromInt(getDate());
    }

    /**
     * 精确到小时
     */
    public String toHourString() {
        return DateUtils.getSimpleFormatDate7FromInt(getDate());
    }

    /**
     * 精确到中文分钟
     */
    public String toCHMinString() {
        return year + "年" + (month + 1) + "月" + day + "日" + hour + "时" + min + "分";
    }
    public String toCHHourString() {
        String  tempMonth ;
        String tempDay ;
        String tempHour ;
        if(month < 9){
            tempMonth = "0" + (month + 1);
        }else
        {
            tempMonth =""+ (month + 1);
        }
        if(day < 10){
            tempDay = "0" + day ;
        }else
        {
            tempDay =""+ day ;
        }
        if(hour < 10){
            tempHour = "0" + hour;
        }else
        {
            tempHour =""+ hour;
        }

        return year + "年" + tempMonth + "月" +tempDay + "日" + tempHour + "时" ;
    }
    /**
     * 精确到中文天
     */
    public String toCHDayString() {
        return year + "年" + (month + 1) + "月" + day + "日";
    }

    /**
     * 精确到中文天
     */
    public String toCHDay2String() {
        return (month + 1) + "月" + day + "日";
    }

    /**
     * 精确到中文天
     */
    public String toCHDay3String() {


        String dayStr;
        if (month < 9) {
            if (day < 10) {
                dayStr = year + "年" + "." + "0" + (month + 1) + "." + "0" + +day + "/";
            } else {
                dayStr = year + "年" + "." + "0" + (month + 1) + "." + day + "/";
            }

        } else {

            if (day < 10) {
                dayStr = year + "年" + "." + (month + 1) + "." + "0" + day + "/";
            } else {
                dayStr = year + "年" + "." + (month + 1) + "." + day + "/";
            }

        }
        return dayStr;
    }

    /**
     * 精确到中文月
     */
    public String toCHMonthString() {
        return year + "年" + (month + 1) + "月";
    }

    /**
     * 精确到中文月
     */
    public String toCHMonth2String() {
        return year + "." + (month + 1) + "月";
    }
    /**
     * 精确到中文月
     */
    public String toCHMonth3String() {
        String[] monthString = {"一", "二", "三", "四", "五", "六", "七","八","九","十","十一","十二"};
        return  monthString[month] + "月";
    }


    /**
     * 精确到天的int值的字符串
     */
    public String toDayInt() {
        return DateUtils.getSimpleFormatDate6FromInt(getDate());
    }

    /**
     * 是否在当前时间之前
     */
    public static boolean isBeforeNow(DateTime dateTime) {
        return isBefore(dateTime, new DateTime());
    }


    public static String getWeekOfDate(int num) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, num);
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 比较datetime是否在dateTime2之前
     */
    public static boolean isBefore(DateTime dateTime, DateTime dateTime2) {
        return dateTime.getTime() < dateTime2.getTime();
    }

    /**
     * 比较datetime是否在dateTime2之前n天
     */
    public static boolean isBeforeDays(DateTime dateTime, DateTime dateTime2, int days) {
        return dateTime.getTime() < dateTime2.getTime() + days * 24 * 3600 * 1000;
    }

    /**
     * 是否同一月
     */
    public static boolean isSameMonth(DateTime dateTime, DateTime dateTime2) {
        return dateTime.year == dateTime2.year && dateTime.month == dateTime2.month;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.hour);
        dest.writeInt(this.min);
        dest.writeInt(this.sec);
    }

    protected DateTime(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.hour = in.readInt();
        this.min = in.readInt();
        this.sec = in.readInt();
    }

    public static final Creator<DateTime> CREATOR = new Creator<DateTime>() {
        public DateTime createFromParcel(Parcel source) {
            return new DateTime(source);
        }

        public DateTime[] newArray(int size) {
            return new DateTime[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof DateTime) {
            DateTime dateTime = (DateTime) o;
            return dateTime.year == year && dateTime.month == month && dateTime.day == day;
        }
        return false;
    }


}