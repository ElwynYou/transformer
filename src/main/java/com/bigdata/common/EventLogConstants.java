package com.bigdata.common;

/**
 * @Package com.bigdata.common
 * @Description: 定义日志手机客户端手机到的用户数据的name名称
 * 以及event_logs hbase表
 * 用户数据参数的name名称就是event_logs的列名
 * @Author elwyn
 * @Date 2017/8/18 21:46
 * @Email elonyong@163.com
 */
public class EventLogConstants {

    /**
     * 事件枚举类
     */
    public static enum EventEnum {
        LAUNCH(1, "launch event", "e_l"),
        PAGEVIEW(2, "page view event", "e_pv"),
        CHARGEREQUEST(3, "charge request event", "e_crt"),
        CHARGESUCCESS(4, "charge sucess event", "e_cs"),
        CHARGEREFUND(5, "charge refund event", "e_cr"),
        EVENT(6, "event duration event", "e_e");
        public final int id;
        public final String name;
        public final String alias;//别名,用于数据手机的简写

        EventEnum(int id, String name, String alias) {
            this.id = id;
            this.name = name;
            this.alias = alias;
        }

        /**
         * 获取匹配别名的event对象
         * @param alias
         * @return
         */
        public static EventEnum valueOfAlias(String alias){
            for (EventEnum eventEnum : values()) {
                if (eventEnum.alias.equals(alias)){
                    return eventEnum;
                }
            }
            return null;
        }
    }


    //表名
    public static final String HBASE_NAME_EVENT_LOGS = "event_logs2";
    //event_logs表的列簇名
    public static final String EVENT_LOGS_FAMILY_NAME = "info";
    //日志分隔符
    public static final String LOG_SEPARTIOR = "\\^A";
    //用户ip
    public static final String LOG_COLUMN_NAME_IP = "ip";
    //服务器时间
    public static final String LOG_COLUMN_NAME_SERVER_TIME = "s_time";
    //事件名称
    public static final String LOG_COLUMN_NAME_EVENT_NAME = "en";
    public static final String LOG_COLUMN_NAME_VERSION = "ver";
    public static final String LOG_COLUMN_NAME_UUID = "v_ud";
    public static final String LOG_COLUMN_NAME_MEMBER_ID = "u_mid";
    public static final String LOG_COLUMN_NAME_SESSION_ID = "u_sd";
    public static final String LOG_COLUMN_NAME_LANGUAGE = "l";
    public static final String LOG_COLUMN_NAME_USER_AGENT = "b_iev";
    /**
     * 定义platform
     */
    public static final String LOG_COLUMN_NAME_PLATFORM = "pl";
    public static final String LOG_COLUMN_NAME_RESOLUTION = "b_rst";
    public static final String LOG_COLUMN_NAME_CURRENT_URL = "p_url";
    public static final String LOG_COLUMN_NAME_REFERRER_URL = "p_ref";
    public static final String LOG_COLUMN_NAME_TITTLE = "tt";
    public static final String LOG_COLUMN_NAME_ORDER_ID = "oid";
    public static final String LOG_COLUMN_NAME_ORDER_NAME = "on";
    public static final String LOG_COLUMN_NAME_ORDER_CURRENCY_AMOUNT = "cua";
    public static final String LOG_COLUMN_NAME_ORDER_CURRENCY_TYPE = "cut";
    public static final String LOG_COLUMN_NAME_ORDER_PAYMENT_TYPE = "pt";
    public static final String LOG_COLUMN_NAME_EVENT_CATEGORY = "ca";
    public static final String LOG_COLUMN_NAME_EVENT_ACTION = "ac";
    public static final String LOG_COLUMN_NAME_EVENT_KV_START = "kv_";
    public static final String LOG_COLUMN_NAME_EVENT_DURATION = "du";
    public static final String LOG_COLUMN_NAME_OS_NAME = "os";
    public static final String LOG_COLUMN_NAME_OS_VERSION = "os_v";
    public static final String LOG_COLUMN_NAME_BROWSER_NAME = "browser";
    public static final String LOG_COLUMN_NAME_BROWSER_VERSION = "browser_v";
    public static final String LOG_COLUMN_NAME_COUNTRY = "country";
    public static final String LOG_COLUMN_NAME_PROVINCE = "province";
    public static final String LOG_COLUMN_NAME_CITY = "city";
}
