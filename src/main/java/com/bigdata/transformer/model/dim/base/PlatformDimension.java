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
 * @Date 2017/8/21 23:21
 * @Email elonyong@163.com
 */
public class PlatformDimension extends BaseDimension {
    private int id;
    private String platformName;

    public PlatformDimension() {
    }

    public PlatformDimension(String platformName) {
        this.platformName = platformName;
    }

    public PlatformDimension(int id, String platformName) {
        this.platformName=platformName;
        this.id=id;
    }

    public static List<PlatformDimension> buildList(String platformName) {
        if (StringUtils.isBlank(platformName)) {
            platformName = GlobalConstants.DEFAULT_VALUE;
        }
        List<PlatformDimension> list = new ArrayList<>();
        list.add(new PlatformDimension(platformName));
       // list.add(new PlatformDimension(GlobalConstants.VALUE_OF_ALL));
        return list;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this == o) {
            return 0;
        }
        PlatformDimension other = (PlatformDimension) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.platformName.compareTo(other.platformName);
        return tmp;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.platformName);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.platformName = dataInput.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformDimension that = (PlatformDimension) o;

        if (id != that.id) return false;
        return platformName != null ? platformName.equals(that.platformName) : that.platformName == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (platformName != null ? platformName.hashCode() : 0);
        return result;
    }
}
