package com.bigdata.transformer.mr.pv;

import com.bigdata.common.GlobalConstants;
import com.bigdata.transformer.model.dim.StatsUserDimension;
import com.bigdata.transformer.model.dim.base.BaseDimension;
import com.bigdata.transformer.model.value.BaseStatsValueWritable;
import com.bigdata.transformer.model.value.reduce.MapWritableValue;
import com.bigdata.transformer.mr.IOutputCollector;
import com.bigdata.transformer.service.rpc.IDimensionConverter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 定义pageview计算的具体输出代码
 * 
 * @author gerry
 *
 */
public class PageViewCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        StatsUserDimension statsUser = (StatsUserDimension) key;
        int pv = ((IntWritable) ((MapWritableValue) value).getValue().get(new IntWritable(-1))).get();

        // 参数设置
        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getDate()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getBrowser()));
        pstmt.setInt(++i, pv);
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, pv);

        // 加入batch
        pstmt.addBatch();
    }

}
