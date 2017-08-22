package com.bigdata.etl.util;


import com.bigdata.common.EventLogConstants;
import com.bigdata.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package com.bigdata.etl.util
 * @Description:处理日志数据的工具类
 * @Author elwyn
 * @Date 2017/8/18 22:04
 * @Email elonyong@163.com
 */
public class LoggerUtil {
    private static final Logger LOGGER = Logger.getLogger(LoggerUtil.class);
    private static IPSeekerExt ipSeekerExt = new IPSeekerExt();

    /**
     * 处理日志方法
     * 如果logtext没有指定数据格式,直接返回empty集合
     *
     * @param logText
     * @return
     */
    public static Map<String, String> handleLog(String logText) {
        Map<String, String> clientInfo = new HashMap<>();
        if (StringUtils.isNotBlank(logText)) {
            String[] splits = logText.trim().split(EventLogConstants.LOG_SEPARTIOR);
            if (splits.length == 4) {
                //格式ip^A服务器时间^Ahost^A请求参数
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_IP, splits[0].trim());
                clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME, String.valueOf(TimeUtil.parseNginxServerTime2Long(splits[1].trim())));
                int index = splits[3].indexOf("?");
                if (index > -1) {
                    String requestBody = splits[3].substring(index + 1);//获取请求参数,也就是收集的数据
                    //处理请求参数
                    handleRequestBody(requestBody, clientInfo);
                    //处理userAgent
                    handleUserAgent(clientInfo);
                    //处理ip地址
                    handleIp(clientInfo);

                } else {
                    //数据格式异常
                    clientInfo.clear();
                }
            }
        }
        return clientInfo;
    }


    private static void handleIp(Map<String, String> clinetInfo) {
        if (clinetInfo.containsKey(EventLogConstants.LOG_COLUMN_NAME_IP)) {
            String ip = clinetInfo.get(EventLogConstants.LOG_COLUMN_NAME_IP);
            IPSeekerExt.RegionInfo regionInfo = ipSeekerExt.analyticIp(ip);
            if (regionInfo != null) {
                clinetInfo.put(EventLogConstants.LOG_COLUMN_NAME_COUNTRY, regionInfo.getCountry());
                clinetInfo.put(EventLogConstants.LOG_COLUMN_NAME_PROVINCE, regionInfo.getProvince());
                clinetInfo.put(EventLogConstants.LOG_COLUMN_NAME_CITY, regionInfo.getCity());
                LOGGER.info("解析ip"+regionInfo);
            }
        }
    }

    /**ip
     * 处理userAgent
     *
     * @param clinetInfo
     */
    private static void handleUserAgent(Map<String, String> clinetInfo) {
        if (clinetInfo.containsKey(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT)) {
            UserAgentUtil.UserAgentInfo userAgentInfo = UserAgentUtil.analyticUserAgent(clinetInfo.get(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT));
            if (userAgentInfo != null) {
                clinetInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_NAME, userAgentInfo.getOsName());
                clinetInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_VERSION, userAgentInfo.getOsVersion());
                clinetInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME, userAgentInfo.getBrowserName());
                clinetInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION, userAgentInfo.getBrowserVersion());
            }
        }
    }


    /**
     * 处理requestBody
     *
     * @param requestBody
     * @param clientInfo
     */
    private static void handleRequestBody(String requestBody, Map<String, String> clientInfo) {
        if (StringUtils.isNotBlank(requestBody)) {
            String[] requestParams = requestBody.split("&");
            for (String requestParam : requestParams) {
                if (StringUtils.isNotBlank(requestParam)) {
                    int index = requestParam.indexOf("=");
                    if (index < 0) {
                        LOGGER.warn("无法解析参数:" + requestParam + "请求参数为:" + requestBody);
                        continue;
                    }
                    String key = null;
                    String value = null;
                    try {
                        key = requestParam.substring(0, index);
                        value = URLDecoder.decode(requestParam.substring(index + 1), "utf-8");
                    } catch (Exception e) {
                        LOGGER.warn("解码操作异常");
                        continue;
                    }
                    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                        clientInfo.put(key, value);
                    }
                }
            }
        }

    }
}
