package com.bigdata.etl.util;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;

import java.io.IOException;

/**
 * @Package com.bigdata.etl.util
 * @Description: 解析浏览器的userAgent工具类, 内部就是调用uasparser
 * @Author elwyn
 * @Date 2017/8/16 22:58
 * @Email elonyong@163.com
 */
public class UserAgentUtil {
    static UASparser uaSparser = null;

    static {
        try {
            //初始化uasParser对象
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析useragent字符串返回对象
     *
     * @param userAgent 要解析的字符串
     * @return 失败返回null
     */
    public static UserAgentInfo analyticUserAgent(String userAgent) {
        UserAgentInfo result = new UserAgentInfo();
        if (!(userAgent == null || userAgent.trim().isEmpty())) {
            try {
                cz.mallat.uasparser.UserAgentInfo info = null;
                info = uaSparser.parse(userAgent);
                result.setBrowserName(info.getUaFamily());
                result.setBrowserVersion(info.getBrowserVersionInfo());
                result.setOsName(info.getOsFamily());
                result.setOsVersion(info.getOsName());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    /**
     * 解析后的浏览器信息model对象
     */
    public static class UserAgentInfo {
        private String browserName;
        private String browserVersion;
        private String osName;
        private String osVersion;

        public String getBrowserName() {
            return browserName;
        }

        public void setBrowserName(String browserName) {
            this.browserName = browserName;
        }

        public String getBrowserVersion() {
            return browserVersion;
        }

        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        @Override
        public String toString() {
            return "UserAgentInfo{" +
                    "browserName='" + browserName + '\'' +
                    ", browserVersion='" + browserVersion + '\'' +
                    ", osName='" + osName + '\'' +
                    ", osVersion='" + osVersion + '\'' +
                    '}';
        }
    }
}
