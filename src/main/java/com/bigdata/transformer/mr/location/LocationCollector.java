package com.bigdata.transformer.mr.location;

import com.bigdata.common.GlobalConstants;
import com.bigdata.transformer.model.dim.StatsLocationDimension;
import com.bigdata.transformer.model.dim.base.BaseDimension;
import com.bigdata.transformer.model.value.BaseStatsValueWritable;
import com.bigdata.transformer.model.value.reduce.LocationReducerOutputValue;
import com.bigdata.transformer.mr.IOutputCollector;
import com.bigdata.transformer.service.IDimensionConverter;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LocationCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        StatsLocationDimension locationDimension = (StatsLocationDimension) key;
        LocationReducerOutputValue locationReducerOutputValue = (LocationReducerOutputValue) value;

        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIdByValue(locationDimension.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(locationDimension.getStatsCommon().getDate()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(locationDimension.getLocation()));
        pstmt.setInt(++i, locationReducerOutputValue.getUvs());
        pstmt.setInt(++i, locationReducerOutputValue.getVisits());
        pstmt.setInt(++i, locationReducerOutputValue.getBounceNumber());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, locationReducerOutputValue.getUvs());
        pstmt.setInt(++i, locationReducerOutputValue.getVisits());
        pstmt.setInt(++i, locationReducerOutputValue.getBounceNumber());

        pstmt.addBatch();
        
        
    }

}
