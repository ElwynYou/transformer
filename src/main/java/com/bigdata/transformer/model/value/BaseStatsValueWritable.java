package com.bigdata.transformer.model.value;

import com.bigdata.common.KpiType;
import org.apache.hadoop.io.Writable;

/**
 * @Package com.bigdata.transformer.model.value
 * @Description:
 * @Author elwyn
 * @Date 2017/8/22 22:08
 * @Email elonyong@163.com
 */
public abstract class BaseStatsValueWritable  implements Writable {
    /**
     * 获取当前value获取的kpi值
     * @return
     */
    public abstract KpiType getKpi();
}
