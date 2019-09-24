/*
 * Controller.
 * Author: Sixuan Huang
 */

package com.example.demo.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csvreader.CsvReader;


@RestController
public class AirQualityController2 {
	
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
	
	@RequestMapping("/airquality")
	public String db() throws ParseException {	
		InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
		String dbName = "AirQuality";
		influxDB.query(new Query("CREATE DATABASE " + dbName));
		influxDB.setDatabase(dbName);
		influxDB.enableBatch(BatchOptions.DEFAULTS);
		SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss");
		Calendar cal = Calendar.getInstance();
		
		String filePath = "AirQualityUCI.csv";
		try {
            CsvReader csvReader = new CsvReader(filePath, ';');

            csvReader.readHeaders();
            while (csvReader.readRecord() && csvReader.get("Date").length() > 0){    	
            	String time = csvReader.get("Date") + " " + csvReader.get("Time");
            	Date d = fm.parse(time);
            	cal.setTime(d);
            	long timeStamp = cal.getTimeInMillis();
                influxDB.write(Point.measurement("AirParameters")
                		.time(timeStamp, TimeUnit.MILLISECONDS)
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
            }
           
        } catch (IOException e) {
            e.printStackTrace();
        }

		// Calculate the min and max timeStamp of year 2004
	    cal.setTime(fm.parse("01/01/2004 00.00.00"));
	    long minTime = cal.getTimeInMillis() * 1000000;
	    cal.setTime(fm.parse("31/12/2004 23.59.59"));
	    long maxTime = cal.getTimeInMillis() * 1000000;
	    String command = "SELECT mean(NO2_GT) FROM AirParameters WHERE time>=" + Long.toString(minTime) + " AND time<=" + Long.toString(maxTime);
	    QueryResult queryResult = influxDB.query(new Query(command, dbName));
		
		System.out.println(queryResult);
		
		/* part used for transfer result, now buggy!!!
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
		List<MeanResult> meanResultLst = resultMapper.toPOJO(queryResult, MeanResult.class);
		System.out.println(meanResultLst);
		for (MeanResult result : meanResultLst)
			System.out.println(result.mean);	
		*/
		//influxDB.query(new Query("DROP DATABASE " + dbName));
		influxDB.close();
		
		return "Loaded! MEAN Result is: " + queryResult;
		
	}
}

