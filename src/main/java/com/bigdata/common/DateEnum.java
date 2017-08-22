package com.bigdata.common;

/**
 * @Package com.bigdata.common
 * @Description:
 * @Author elwyn
 * @Date 2017/8/21 22:48
 * @Email elonyong@163.com
 */
public enum DateEnum {
    YEAR("year"),
    SEASON("season"),
    MONTH("month"),
    WEEK("week"),
    DAY("day"),
    HOUR("hour");

    public final String name;

    DateEnum(String name) {
        this.name = name;
    }

    public static DateEnum valueOfName(String name){
        for (DateEnum dateEnum : values()) {
            if (dateEnum.name.equals(name)){
                return dateEnum;
            }
        }
        return null;
    }
}
