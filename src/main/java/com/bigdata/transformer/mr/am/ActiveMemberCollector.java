package com.bigdata.transformer.mr.am;

import com.bigdata.common.GlobalConstants;
import com.bigdata.common.KpiType;
import com.bigdata.transformer.model.dim.StatsUserDimension;
import com.bigdata.transformer.model.dim.base.BaseDimension;
import com.bigdata.transformer.model.value.BaseStatsValueWritable;
import com.bigdata.transformer.model.value.reduce.MapWritableValue;
import com.bigdata.transformer.mr.IOutputCollector;
import com.bigdata.transformer.service.IDimensionConverter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 定义具体的active member kpi的输出类
 * 
 * @author gerry
 *
 */
public class ActiveMemberCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        // 第一步: 将key&value进行强制转换
        StatsUserDimension statsUser = (StatsUserDimension) key;
        IntWritable activeMembers = (IntWritable) ((MapWritableValue) value).getValue().get(new IntWritable(-1));

        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getStatsCommon().getDate()));
        if (KpiType.BROWSER_ACTIVE_MEMBER.name.equals(statsUser.getStatsCommon().getKpi().getKpiName())) {
            // 表示输出结果是统计browser active member的，那么进行browser维度信息设置
            pstmt.setInt(++i, converter.getDimensionIdByValue(statsUser.getBrowser()));
        }
        pstmt.setInt(++i, activeMembers.get());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, activeMembers.get());

        // 将pstmt添加到批量执行中
        pstmt.addBatch();
    }

}
