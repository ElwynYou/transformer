package com.bigdata.transformer.service.rpc;

import com.bigdata.transformer.model.dim.base.BaseDimension;
import org.apache.hadoop.ipc.VersionedProtocol;

import java.io.IOException;

/**
 * @Package com.bigdata.transformer
 * @Description:
 * @Author elwyn
 * @Date 2017/8/22 21:33
 * @Email elonyong@163.com
 */
public interface IDimensionConverter extends VersionedProtocol {
     long versionID=1;
    /**
     * 根据dimension的value值获取id
     * 如果数据库中有,直接返回.没有-插入后返回新的id值
     * @param dimension
     * @return
     * @throws IOException
     */
     int getDimensionIdByValue(BaseDimension dimension) throws IOException;
}
