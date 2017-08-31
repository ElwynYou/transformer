package com.bigdata.transformer.mr;

import com.bigdata.common.GlobalConstants;
import com.bigdata.common.KpiType;
import com.bigdata.transformer.model.dim.base.BaseDimension;
import com.bigdata.transformer.model.value.BaseStatsValueWritable;
import com.bigdata.transformer.service.rpc.IDimensionConverter;
import com.bigdata.transformer.service.rpc.server.DimensionConverterImpl;
import com.bigdata.util.JdbcManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package com.bigdata.transformer.mr
 * @Description:
 * @Author elwyn
 * @Date 2017/8/22 22:11
 * @Email elonyong@163.com
 */
public class TransformerOutputFormat extends OutputFormat<BaseDimension, BaseStatsValueWritable> {
    private static Logger logger = Logger.getLogger(TransformerOutputFormat.class);

    @Override
    public RecordWriter<BaseDimension, BaseStatsValueWritable> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        Configuration conf = taskAttemptContext.getConfiguration();
        Connection conn = null;
        IDimensionConverter converter = new DimensionConverterImpl();
        try {
            conn = JdbcManager.getConnection(conf, GlobalConstants.WAREHOUSE_OF_REPORT);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("获取数据库连接失败", e);
            throw new IOException("获取数据库连接失败", e);
        }
        return new TransformerRecordWriter(conn, conf, converter);
    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {

    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new FileOutputCommitter(FileOutputFormat.getOutputPath(taskAttemptContext), taskAttemptContext);
    }

    public class TransformerRecordWriter extends RecordWriter<BaseDimension, BaseStatsValueWritable> {
        private Connection conn;
        private Configuration conf;
        private IDimensionConverter converter;
        private Map<KpiType, PreparedStatement> map = new HashMap<>();
        private Map<KpiType, Integer> batch = new HashMap<KpiType, Integer>();

        public TransformerRecordWriter(Connection connection, Configuration configuration, IDimensionConverter converter) {
            this.conn = connection;
            this.conf = configuration;
            this.converter = converter;
        }

        @Override
        public void write(BaseDimension key, BaseStatsValueWritable value) throws IOException, InterruptedException {
            if (key == null || value == null) {
                return;
            }

            try {
                KpiType kpi = value.getKpi();
                PreparedStatement pstmt = null;
                int count = 1;
                if (map.get(kpi) == null) {
                    // 使用kpi进行区分，返回sql保存到config中
                    pstmt = this.conn.prepareStatement(conf.get(kpi.name));
                    map.put(kpi, pstmt);
                } else {
                    pstmt = map.get(kpi);
                    count = batch.get(kpi);
                    count++;
                }
                batch.put(kpi, count); // 批量次数的存储

                String collectorName = conf.get(GlobalConstants.OUTPUT_COLLECTOR_KEY_PREFIX + kpi.name);
                Class<?> clazz = Class.forName(collectorName);
                IOutputCollector collector = (IOutputCollector) clazz.newInstance();
                collector.collect(conf, key, value, pstmt, converter);

                if (count % Integer.valueOf(conf.get(GlobalConstants.JDBC_BATCH_NUMBER, GlobalConstants.DEFAULT_JDBC_BATCH_NUMBER)) == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                    batch.remove(kpi); // 对应批量计算删除
                }
            } catch (Throwable e) {
                logger.error("在writer中写数据出现异常", e);
                throw new IOException(e);
            }
        }

        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            try {
                for (Map.Entry<KpiType, PreparedStatement> entry : this.map.entrySet()) {
                    entry.getValue().executeBatch();
                }
            } catch (SQLException e) {
                logger.error("执行executeUpdate方法异常", e);
                throw new IOException(e);
            } finally {
                try {
                    if (conn != null) {
                        conn.commit(); // 进行connection的提交动作
                    }
                } catch (Exception e) {
                    // nothing
                } finally {
                    for (Map.Entry<KpiType, PreparedStatement> entry : this.map.entrySet()) {
                        try {
                            entry.getValue().close();
                        } catch (SQLException e) {
                            // nothing
                        }
                    }
                    if (conn != null)
                        try {
                            conn.close();
                        } catch (Exception e) {
                            // nothing
                        }
                }
            }
        }
    }
}
