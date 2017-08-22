package com.bigdata.transformer.model.dim.base;

import com.bigdata.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package com.bigdata.transformer.model.dim.base
 * @Description:
 * @Author elwyn
 * @Date 2017/8/21 20:40
 * @Email elonyong@163.com
 */
public class BrowserDimension extends BaseDimension {
    private int id;
    private String browserName;
    private String browserVersion;

    public BrowserDimension() {
    }

    public BrowserDimension(String browserName, String browserVersion) {
        this.browserName = browserName;
        this.browserVersion = browserVersion;
    }

    public static BrowserDimension newInstance(String browserName, String browserVersion) {
        BrowserDimension browserDimension = new BrowserDimension();
        browserDimension.browserName = browserName;
        browserDimension.browserVersion = browserVersion;
        return browserDimension;
    }

    /**
     * 构建多个浏览器纬度信息对象集合
     *
     * @param browserName
     * @param browserVersion
     * @return
     */
    public static List<BrowserDimension> buildList(String browserName, String browserVersion) {
        List<BaseDimension> list = new ArrayList<>();
        if (StringUtils.isBlank(browserName)) {
            //浏览器名称为空,设置为unknow
            browserName = GlobalConstants.DEFAULT_VALUE;
            browserVersion = GlobalConstants.DEFAULT_VALUE;
        }
        if (StringUtils.isEmpty(browserVersion)) {
            browserVersion = GlobalConstants.DEFAULT_VALUE;
        }
        //list.add(BrowserDimension.newInstance(GlobalConstants.VALUE_OF_ALL,GlobalConstants.VALUE_OF_ALL));
        list.add(BrowserDimension.newInstance(browserName, GlobalConstants.VALUE_OF_ALL));
        list.add(BrowserDimension.newInstance(browserName, browserVersion));
        return null;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.browserName);
        dataOutput.writeUTF(this.browserVersion);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.browserName = dataInput.readUTF();
        this.browserVersion = dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this == o) {
            return 0;
        }
        BrowserDimension other = (BrowserDimension) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.browserName.compareTo(other.browserName);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.browserVersion.compareTo(other.browserVersion);
        if (tmp != 0) {
            return tmp;
        }
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BrowserDimension that = (BrowserDimension) o;

        if (id != that.id) return false;
        if (browserName != null ? !browserName.equals(that.browserName) : that.browserName != null) return false;
        return browserVersion != null ? browserVersion.equals(that.browserVersion) : that.browserVersion == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (browserName != null ? browserName.hashCode() : 0);
        result = 31 * result + (browserVersion != null ? browserVersion.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
