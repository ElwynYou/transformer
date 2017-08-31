package com.bigdata.transformer.mr;

import com.bigdata.transformer.model.dim.base.BaseDimension;
import com.bigdata.transformer.model.value.BaseStatsValueWritable;
import com.bigdata.transformer.service.rpc.IDimensionConverter;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Package com.bigdata.transformer.mr
 * @Description:
 * @Author elwyn
 * @Date 2017/8/22 22:07
 * @Email elonyong@163.com
 */
public interface IOutputCollector {
    void collect(Configuration configuration, BaseDimension key, BaseStatsValueWritable value, PreparedStatement preparedStatement, IDimensionConverter converter) throws SQLException, IOException;
}