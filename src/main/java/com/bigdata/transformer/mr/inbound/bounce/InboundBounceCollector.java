package com.bigdata.transformer.mr.inbound.bounce;

import com.bigdata.common.GlobalConstants;
import com.bigdata.transformer.model.dim.StatsInboundDimension;
import com.bigdata.transformer.model.dim.base.BaseDimension;
import com.bigdata.transformer.model.value.BaseStatsValueWritable;
import com.bigdata.transformer.model.value.reduce.InboundBounceReduceValue;
import com.bigdata.transformer.mr.IOutputCollector;
import com.bigdata.transformer.service.IDimensionConverter;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InboundBounceCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        StatsInboundDimension inboundDimension = (StatsInboundDimension) key;
        InboundBounceReduceValue inboundReduceValue = (InboundBounceReduceValue) value;

        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getDate()));
        pstmt.setInt(++i, inboundDimension.getInbound().getId()); // 直接设置，在mapper类中已经设置
        pstmt.setInt(++i, inboundReduceValue.getBounceNumber());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, inboundReduceValue.getBounceNumber());

        pstmt.addBatch();
    }

}
