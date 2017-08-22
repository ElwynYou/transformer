package com.bigdata.etl.util;

import com.bigdata.common.GlobalConstants;
import com.bigdata.etl.util.ip.IPSeeker;
import org.apache.log4j.Logger;

/**
 * @Package com.bigdata.etl.util.ip
 * @Description:
 * @Author elwyn
 * @Date 2017/8/17 0:34
 * @Email elonyong@163.com
 */
public class IPSeekerExt extends IPSeeker {
    private static Logger logger=Logger.getLogger(IPSeekerExt.class);
    private RegionInfo DEFAULT_INFO = new RegionInfo();

    public RegionInfo analyticIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return DEFAULT_INFO;
        }
        RegionInfo info = new RegionInfo();
        try {
            String country = super.getCountry(ip);
            logger.info("解析Ip"+ip);
            if ("局域网".equals(country)) {
                info.setCountry("中国");
                info.setProvince("成都");
                return info;
            }
            int length = country.length();
            int index = country.indexOf("省");
            if (index != -1) {
                info.setCountry("中国");
                if (index == length - 1) {
                    info.setProvince(country);
                } else {
                    info.setProvince(country.substring(0, index + 1));
                    int index2 = country.indexOf("市", index);
                    if (index2 > 0) {
                        country.substring(1, 1);
                        info.setCity(country.substring(index + 1, Math.min(index2 + 1, length)));
                    }
                }

            } else {
                String flag = country.substring(0, 2);
                switch (flag) {
                    case "内蒙":
                        info.setCountry("中国");
                        info.setProvince("内蒙古自治区");
                        country = country.substring(3);
                        if (!country.isEmpty()) {
                            index = country.indexOf("市");
                            if (index > 0) {
                                info.setCountry(country.substring(0, Math.min(index + 1, country.length())));
                            }
                        }
                        break;
                    case "广西":
                    case "西藏":
                    case "宁夏":
                    case "新疆":
                        info.setCountry("中国");
                        info.setProvince(flag);
                        country = country.substring(2);
                        if (!country.isEmpty()) {
                            index = country.indexOf("市");
                            if (index > 0) {
                                info.setCountry(country.substring(0, Math.min(index + 1, country.length())));
                            }
                        }
                        break;
                    case "上海":
                    case "北京":
                    case "天津":
                    case "重庆":
                        info.setCountry("中国");
                        info.setProvince(flag + "市");
                        if (!country.isEmpty()) {
                            index = country.indexOf("区");
                            if (index > 0) {
                                char ch = country.charAt(index - 1);
                                if (ch != '校' || ch != '小') {
                                    info.setCity(country.substring(0, Math.min(index + 1, country.length())));
                                }
                            }

                            if (RegionInfo.DEFAULT_VALUE.equals(info.getCity())) {
                                index = country.indexOf("县");
                                if (index > 0) {
                                    info.setCity(country.substring(0, Math.min(index + 1, country.length())));

                                }
                            }
                        }
                        break;
                    case "香港":
                        info.setCountry("中国");
                        info.setProvince(flag + "特别行政区");
                        break;
                    default:
                        break;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return info;
    }

    /**
     * ip 地域信息
     */
    public static class RegionInfo {
        public static final String DEFAULT_VALUE = GlobalConstants.DEFAULT_VALUE;
        private String country = DEFAULT_VALUE;
        private String province = DEFAULT_VALUE;
        private String city = DEFAULT_VALUE;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return "Region{" +
                    "country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    '}';
        }
    }
}
