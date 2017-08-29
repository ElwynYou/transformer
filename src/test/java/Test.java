import com.bigdata.etl.util.IPSeekerExt;

/**
 * @Package PACKAGE_NAME
 * @Description:
 * @Author elwyn
 * @Date 2017/8/16 23:19
 * @Email elonyong@163.com
 */
public class Test {
    public static void main(String[] args) {
      String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
 /*
          UserAgentUtil.UserAgentInfo userAgentInfo = UserAgentUtil.analyticUserAgent(userAgent);
         System.out.println(userAgentInfo);

        UserAgent userAgent1= UserAgent.parseUserAgentString(userAgent);
        UserAgentUtil.UserAgentInfo userAgentInfo=new UserAgentUtil.UserAgentInfo();
        userAgentInfo.setBrowserVersion(userAgent1.getBrowserVersion().getVersion());
        userAgentInfo.setBrowserName(userAgent1.getBrowser().getName());
        //userAgentInfo.setOsVersion(userAgent1.getOperatingSystem());
        OperatingSystem operatingSystem = userAgent1.getOperatingSystem();
        userAgentInfo.setOsName(userAgent1.getOperatingSystem().getName());

        System.out.println(Test.class.getResource("").getFile());
*//*
        IPSeeker ipSeeker=IPSeeker.getInstance();
        String country = ipSeeker.getCountry("192.168.128.1");
        System.out.println(country);*/

        IPSeekerExt ipSeeker=new IPSeekerExt();
        IPSeekerExt.RegionInfo regionInfo = ipSeeker.analyticIp("192.168.128.1");
        System.out.println(regionInfo);

       /* String ip = "192.168.128.1^A1502160738.734^Ahadoop-senior.ibeifeng.com^A/BfImg.gif?ver=1&u_mid=fdasfdas&c_time=1502721801703&en=e_cs&oid=orderId23&sdk=jdk&pl=java_server";

        System.out.println(LoggerUtil.handleLog(ip));*/
    }
}
