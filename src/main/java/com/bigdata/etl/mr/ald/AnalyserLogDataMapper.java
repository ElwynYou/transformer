package com.bigdata.etl.mr.ald;

import com.bigdata.common.EventLogConstants;
import com.bigdata.etl.util.LoggerUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * @Package com.bigdata.etl.mr.ald
 * @Description: 自定义数据解析mapper类
 * @Author elwyn
 * @Date 2017/8/19 0:16
 * @Email elonyong@163.com
 */
public class AnalyserLogDataMapper extends Mapper<Object, Text, NullWritable, Put> {
    private static final Logger LOGGER = Logger.getLogger(AnalyserLogDataMapper.class);
    private byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);
    private CRC32 crc32 = new CRC32();
    private int inputRecordes;
    private int filterRecords;
    private int outputRecordes;

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        this.inputRecordes++;
        LOGGER.debug("Analyse data of :" + value);
        try {
            //解析日志
            Map<String, String> clinetInfo = LoggerUtil.handleLog(value.toString());
            if (clinetInfo.isEmpty()) {
                this.filterRecords++;
                return;
            }
            String evnetAlaisName = clinetInfo.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME);
            //获取事件名称
            EventLogConstants.EventEnum eventEnum = EventLogConstants.EventEnum.valueOfAlias(evnetAlaisName);
            switch (eventEnum) {
                case LAUNCH:
                case PAGEVIEW:
                case CHARGEREQUEST:
                case CHARGEREFUND:
                case CHARGESUCCESS:
                case EVENT:
                    this.handleData(clinetInfo, eventEnum, context);
                    this.outputRecordes++;
                    break;
                default:
                    this.filterRecords++;
                    this.LOGGER.warn("该事件无法进行解析,事件名称" + evnetAlaisName);
            }
        } catch (Exception e) {
            this.filterRecords++;
            this.LOGGER.error("处理数据发生异常,数据:" + value, e);
        }
    }

    /**
     * 具体处理数据的方法
     *
     * @param clientInfo
     * @param contenxt
     */
    private void handleData(Map<String, String> clientInfo, EventLogConstants.EventEnum event, Context contenxt) throws IOException, InterruptedException {
        String uuid = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_UUID);
        String memberId = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_MEMBER_ID);
        String serverTime = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME);

        if (StringUtils.isNotBlank(serverTime)) {
            //服务器时间不为空
            clientInfo.remove(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT);//去除浏览器信息
            String rowkey = this.generateRowkey(uuid, memberId, event.alias, serverTime);//timestamp+(uuid+memberid+event).crc
            Put put = new Put(Bytes.toBytes(rowkey));
            for (Map.Entry<String, String> stringStringEntry : clientInfo.entrySet()) {
                if (StringUtils.isNotBlank(stringStringEntry.getKey()) && StringUtils.isNotBlank(stringStringEntry.getValue())) {
                    put.add(family, Bytes.toBytes(stringStringEntry.getKey()), Bytes.toBytes(stringStringEntry.getValue()));
                }
            }
            contenxt.write(NullWritable.get(), put);
            this.outputRecordes++;
        } else {
            this.filterRecords++;
        }

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
        LOGGER.info("输入数据:" + this.outputRecordes + "输出数据:" + this.outputRecordes + "过滤数据:" + this.filterRecords);
    }

    /**
     * 创建rowkey
     *
     * @param uuid
     * @param memberId
     * @param serverTime
     * @return
     */
    private String generateRowkey(String uuid, String memberId, String eventAliasName, String serverTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(serverTime).append("_");
        this.crc32.reset();
        if (StringUtils.isNotBlank(uuid)) {
            this.crc32.update(uuid.getBytes());
        }
        if (StringUtils.isNotBlank(memberId)) {
            this.crc32.update(memberId.getBytes());
        }
        this.crc32.update(eventAliasName.getBytes());
        stringBuilder.append(this.crc32.getValue() % 100000000L);
        return stringBuilder.toString();
    }
}
