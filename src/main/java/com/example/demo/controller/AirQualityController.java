/*
 * This version is problematic in Time Stamp, Don't use this.
 * author: Sixuan Huang
 */

package com.example.demo.controller;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.csvreader.CsvReader;


@RestController
public class AirQualityController {
	
	/* part used for transfer result, now buggy!!!
	@Measurement(name = "AirParameters")
	public class MeanResult {
		@TimeColumn
	    @Column(name = "time")
	    private Instant time;
	    @Column(name = "mean")
	    private double mean;
	}
	*/
	
	@RequestMapping("/air")
	public String db() throws ParseException, InterruptedException {
		System.out.println("Air Request!!!");
		InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
		String dbName = "AirQuality";
		influxDB.query(new Query("CREATE DATABASE " + dbName));
		influxDB.setDatabase(dbName);
		String rpName = "aRetentionPolicy";
		influxDB.query(new Query("CREATE RETENTION POLICY " + rpName + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));
		influxDB.setRetentionPolicy(rpName);

		influxDB.enableBatch(BatchOptions.DEFAULTS);
		
		/* Not care about time now!!!
		SimpleDateFormat fm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		*/
		
		String filePath = "AirQualityUCI.csv";
		try {
            CsvReader csvReader = new CsvReader(filePath, ';');

            csvReader.readHeaders();
            int count = 0;
            while (csvReader.readRecord() && csvReader.get("Date").length() > 0){
            	count++;      	
            	/* Not care about time now!!!
            	String time = csvReader.get("Date") + " " + csvReader.get("Time");
            	// System.out.println(time);
            	Date d = fm.parse(time);
            	cal.setTime(d);
            	long timeStamp = cal.getTimeInMillis() * 1000000;
            	System.out.println(timeStamp);
            	*/

                influxDB.write(Point.measurement("AirParameters")
                		.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                		.addField("Date", csvReader.get("Date"))
                		.addField("Time", csvReader.get("Time"))
                		.addField("CO_GT", Double.valueOf(csvReader.get("CO(GT)").replaceAll(",", ".")))
                		.addField("PT08.S1_CO", Double.valueOf(csvReader.get("PT08.S1(CO)").replaceAll(",", ".")))
                		.addField("NMHC_GT", Double.valueOf(csvReader.get("NMHC(GT)").replaceAll(",", ".")))
                		.addField("C6H6_GT", Double.valueOf(csvReader.get("C6H6(GT)").replaceAll(",", ".")))
                		.addField("PT08.S2_NMHC", Double.valueOf(csvReader.get("PT08.S2(NMHC)").replaceAll(",", ".")))
                		.addField("NOx_GT", Double.valueOf(csvReader.get("NOx(GT)").replaceAll(",", ";").replaceAll(",", ".")))
                		.addField("PT08.S3_NOx", Double.valueOf(csvReader.get("PT08.S3(NOx)").replaceAll(",", ".")))
                		.addField("NO2_GT", Double.valueOf(csvReader.get("NO2(GT)").replaceAll(",", ".")))
                		.addField("PT08.S4_NO2", Double.valueOf(csvReader.get("PT08.S4(NO2)").replaceAll(",", ".")))
                		.addField("PT08.S5_O3", Double.valueOf(csvReader.get("PT08.S5(O3)").replaceAll(",", ".")))
                		.addField("T", Double.valueOf(csvReader.get("T").replaceAll(",", ".")))
                		.addField("RH", Double.valueOf(csvReader.get("RH").replaceAll(",", ".")))
                		.addField("AH", Double.valueOf(csvReader.get("AH").replaceAll(",", ".")))
                		.build());
                Thread.sleep(1);
            }
            // verify the result seems right
            // print number of total records
            System.out.println(count);
            

        } catch (IOException e) {
            e.printStackTrace();
        }
		

		Query query = new Query("SELECT mean(NO2_GT) FROM AirParameters",dbName);
		QueryResult queryResult = influxDB.query(query);
		System.out.println(queryResult);
		/* part used for transfer result, now buggy!!!
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
		List<MeanResult> meanResultLst = resultMapper.toPOJO(queryResult, MeanResult.class);
		System.out.println(meanResultLst);
		for (MeanResult result : meanResultLst)
			System.out.println(result.mean);	
		*/
		
		//influxDB.query(new Query("DROP RETENTION POLICY " + rpName + " ON " + dbName));
		//influxDB.query(new Query("DROP DATABASE " + dbName));
		influxDB.close();
		
		return "Loaded!!";
	}
}

