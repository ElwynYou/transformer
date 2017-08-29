package com.bigdata.util;

import com.bigdata.common.DateEnum;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Package com.bigdata.util
 * @Description:
 * @Author elwyn
 * @Date 2017/8/18 22:28
 * @Email elonyong@163.com
 */
public class TimeUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 判断输入的参数是否一个有效的时间格式数据
     *
     * @param input
     * @return
     */
    public static boolean isValidateRunningDate(String input) {
        Matcher matcher = null;
        boolean result = false;
        String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        if (input != null && !input.isEmpty()) {
            Pattern pattern = Pattern.compile(regex);
            matcher = pattern.matcher(input);

        }
        if (matcher != null) {
            result = matcher.matches();
        }
        return result;
    }

    /**
     * yyyy-MM-dd转换为时间戳
     *
     * @param input
     * @return
     */
    public static long parseString2Long(String input) {
        return parseString2Long(input, DATE_FORMAT);
    }

    private static long parseString2Long(String input, String dateFormat) {
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat).parse(input);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date.getTime();
    }


    public static String parseLong2String(long input) {
        return parseLong2String(input, DATE_FORMAT);
    }

    public static String parseLong2String(long input, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(input).getTime());
    }

    /**
     * 将nginx服务器时间转换为时间戳,如果解析失败返回-1
     *
     * @param input
     * @return
     */
    public static long parseNginxServerTime2Long(String input) {
        Date date = parseNginxServerTime2Date(input);
        return date == null ? -1 : date.getTime();
    }

    public static Date parseNginxServerTime2Date(String input) {
        if (StringUtils.isNotBlank(input)) {
            try {
                long timestamp = Double.valueOf(Double.valueOf(input.trim()) * 1000).longValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                return calendar.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public static String getYesterday() {
        return getYesterday(DATE_FORMAT);
    }

    public static String getYesterday(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 从时间戳中获取需要的信息
     *
     * @param time
     * @param dateEnum
     * @return
     */
    public static int getDateInfo(long time, DateEnum dateEnum) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        switch (dateEnum) {
            case DAY:
                return calendar.get(Calendar.DAY_OF_MONTH);
            case HOUR:
                return  calendar.get(Calendar.HOUR_OF_DAY);
            case WEEK:
                return calendar.get(Calendar.WEEK_OF_YEAR);
            case YEAR:
                return calendar.get(Calendar.YEAR);
            case MONTH:
                return calendar.get(Calendar.MONTH) + 1;
            case SEASON:
                int month = calendar.get(Calendar.MONTH) + 1;
                if (month % 3 == 0) {
                    return month / 3;
                }
                return month / 3 + 1;
            default:
                throw new RuntimeException("没有对应的时间类型");
        }
    }


    public static long getFirstDayOfThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
