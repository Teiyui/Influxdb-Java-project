package com.zyw;

import com.zyw.util.InfluxDBSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class InfluxdbApplication implements CommandLineRunner {

    public static Logger logger = LogManager.getLogger(InfluxdbApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(InfluxdbApplication.class, args);
    }

    @Autowired
    private InfluxDBSupport influxDBSupport;

    @Override
    public void run(String... args) throws Exception {
        //插入测试
//        insertTest();

        //查询测试
        querTest();
    }

    /**
     * 插入测试
     * @throws InterruptedException
     */
    public void insertTest() throws InterruptedException {
        Map<String, String> tagsMap = new HashMap<>();
        Map<String, Object> fieldsMap = new HashMap<>();
        System.out.println("influxDB start time :" + System.currentTimeMillis());
        int i = 0;
        for (; ; ) {
            Thread.sleep(100);
            tagsMap.put("value", String.valueOf(i % 10));
            tagsMap.put("host", "https://github.com/Teiyui");
            tagsMap.put("region", "west" + (i % 5));
            fieldsMap.put("count", i % 5);
            influxDBSupport.insert("cpu_test", tagsMap, fieldsMap);
            i++;
        }
    }

    /**
     * 查询测试
     */
    public void querTest(){
        QueryResult rs = influxDBSupport.query("select * from cpu");
        logger.info("query result => {}", rs);
        if (!rs.hasError() && !rs.getResults().isEmpty()) {
            rs.getResults().forEach(System.out::println);
        }
    }

}
