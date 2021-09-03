package com.zyw.util;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: ywzheng
 * @Description: TODO
 * @date: 2021/9/3 8:52 AM
 */
@Slf4j
@Component
public class InfluxDBSupport {

    /**
     * 数据保存策略
     */
    @Value("${spring.influx.retentionPolicy:}")
    private String retentionPolicy;
    /**
     * 数据保存策略中数据保存时间
     */
    @Value("${spring.influx.retentionPolicyTime:}")
    private String retentionPolicyTime;

    @Value("${spring.influx.database:}")
    private String database;

    /**
     * InfluxDB实例
     */
    @Autowired
    private InfluxDB influxDB;

    public InfluxDBSupport() {
        // autogen默认的数据保存策略
        this.retentionPolicy = retentionPolicy == null || "".equals(retentionPolicy) ? "autogen" : retentionPolicy;
        this.retentionPolicyTime = retentionPolicyTime == null || "".equals(retentionPolicy) ? "30d" : retentionPolicyTime;
    }

    /**
     * 设置数据保存策略 defalut 策略名 /database 数据库名/ 30d 数据保存时限30天/ 1 副本个数为1/ 结尾DEFAULT
     * 表示 设为默认的策略
     */
    public void createRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                retentionPolicy, database, retentionPolicyTime, 1);
        this.query(command);
    }

    /**
     * 查询
     *
     * @param command 查询语句
     * @return
     */
    public QueryResult query(String command) {
        return influxDB.query(new Query(command, database));
    }

    /**
     * 插入
     *
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     */
    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        Point.Builder builder = Point.measurement(measurement);
        // 纳秒时会出现异常信息：partial write: points beyond retention policy dropped=1
        // builder.time(System.nanoTime(), TimeUnit.NANOSECONDS);
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        builder.tag(tags);
        builder.fields(fields);

        log.info("influxDB insert data:[{}]", builder.build().toString());
        influxDB.write(database, "", builder.build());
    }

}
