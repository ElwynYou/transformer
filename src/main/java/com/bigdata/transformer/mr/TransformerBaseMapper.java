package com.bigdata.transformer.mr;

import com.bigdata.common.EventLogConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @Package com.bigdata.transformer.mr
 * @Description:
 * @Author elwyn
 * @Date 2017/8/29 21:53
 * @Email elonyong@163.com
 */
public class TransformerBaseMapper<KEYOUT, VALUEOUT> extends TableMapper<KEYOUT, VALUEOUT> {
    private static final Logger LOGGER = Logger.getLogger(TransformerBaseMapper.class);
    private long startTime = System.currentTimeMillis();
    protected Configuration configuration = null;
    protected int inputRecords = 0;//输入记录数
    protected int filterRecords = 0;//过滤的记录数,要求输入的记录没有进行任何输出
    protected int outputRecords = 0;//输出的记录条数
    protected byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        this.configuration = context.getConfiguration();
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
        try {
            long endTime = System.currentTimeMillis();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("job_id:").append(context.getJobID().toString());
            stringBuilder.append("; start_time").append(this.startTime);
            stringBuilder.append("; end_time").append(endTime);
            stringBuilder.append("; using_time").append(endTime - this.startTime).append("ms");
            stringBuilder.append("; input records").append(this.inputRecords);
            stringBuilder.append("; filter records").append(this.filterRecords);
            stringBuilder.append("; output records").append(this.outputRecords);
            System.out.println(stringBuilder.toString());
            LOGGER.info(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getUuid(Result value) {
        return fetchValue(value, EventLogConstants.LOG_COLUMN_NAME_UUID);
    }

    public String getPlatform(Result value) {
        return fetchValue(value, EventLogConstants.LOG_COLUMN_NAME_PLATFORM);
    }

    public String getSeverTime(Result value) {
        return fetchValue(value, EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME);
    }

    public String getBrowserName(Result value) {
        return fetchValue(value, EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME);
    }

    public String getBrowserVersion(Result value) {
        return fetchValue(value, EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION);
    }

    private String fetchValue(Result value, String column) {
        return Bytes.toString(value.getValue(family, Bytes.toBytes(column)));
    }
}

