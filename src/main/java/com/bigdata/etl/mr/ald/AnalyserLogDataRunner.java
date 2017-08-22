package com.bigdata.etl.mr.ald;

import com.bigdata.common.EventLogConstants;
import com.bigdata.common.GlobalConstants;
import com.bigdata.util.EJob;
import com.bigdata.util.TimeUtil;
import jdk.nashorn.internal.scripts.JO;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @Package com.bigdata.etl.mr.ald
 * @Description:
 * @Author elwyn
 * @Date 2017/8/19 17:55
 * @Email elonyong@163.com
 */
public class AnalyserLogDataRunner implements Tool {

    private static final Logger LOGGER = Logger.getLogger(AnalyserLogDataRunner.class);
    private Configuration configuration;

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(), new AnalyserLogDataRunner(), args);
        } catch (Exception e) {
            LOGGER.error("日志解析异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        //解决权限错误问题
        System.setProperty("HADOOP_USER_NAME", "beifeng");

        Configuration conf = this.getConf();
        this.processArgs(conf, args);
        Job job = Job.getInstance(conf, "analyser_logdata");
        //本地提交到yarn上需要用 开始
     /*   File jarFile= EJob.createTempJar("target/classes");
        ( (JobConf)job.getConfiguration()).setJar(jarFile.toString());*/
        //本地提交到yarn上需要用 结束

        job.setJarByClass(AnalyserLogDataRunner.class);
        job.setMapperClass(AnalyserLogDataMapper.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Put.class);
        //设置reduce配置
        //1.集群上运行,打成jar运行(要求addDependencyJars参数为true,默认就是true)
        //  TableMapReduceUtil.initTableReducerJob(EventLogConstants.HBASE_NAME_EVENT_LOGS,null,job);
        //2.本地运行,要求是false
        TableMapReduceUtil.initTableReducerJob(EventLogConstants.HBASE_NAME_EVENT_LOGS, null, job, null, null, null, null, false);
        job.setNumReduceTasks(0);
        //设置输入路径
        setJobInputPaths(job);
        return job.waitForCompletion(true) ? 0 : -1;
    }

    private void processArgs(Configuration conf, String[] args) {
        String date = null;
        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i])) {
                if (i + 1 < args.length) {
                    date = args[++i];
                    break;
                }
            }
        }

        if (StringUtils.isBlank(date) || !TimeUtil.isValidateRunningDate(date)) {
            date = TimeUtil.getYesterday();
        }
        conf.set(GlobalConstants.RUNNING_DATE_PARAMES, date);

    }

    private void setJobInputPaths(Job job) {
        Configuration configuration = job.getConfiguration();
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(configuration);
            String date = configuration.get(GlobalConstants.RUNNING_DATE_PARAMES);
            Path path = new Path("/logs/" + TimeUtil.parseLong2String(TimeUtil.parseString2Long(date), "MM/dd/"));
            if (fileSystem.exists(path)) {
                FileInputFormat.addInputPath(job, path);
            } else {
                throw new RuntimeException("文件不存在" + path);
            }

        } catch (Exception e) {
            throw new RuntimeException("设置job的mapreduce输入路径出现异常", e);
        } finally {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void setConf(Configuration configuration) {
        this.configuration = HBaseConfiguration.create(configuration);
    }

    @Override
    public Configuration getConf() {
        return this.configuration;
    }
}
