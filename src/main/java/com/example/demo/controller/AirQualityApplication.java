/*
 * This is old version which didn't apply Spring Boot yet.
 * Author: Sixuan Huang
 */

package com.example.demo.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import com.csvreader.CsvReader;

public class AirQualityApplication {
	
	
	
	

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
		String dbName = "AirQuality";
		influxDB.query(new Query("CREATE DATABASE " + dbName));
		influxDB.setDatabase(dbName);
		String rpName = "aRetentionPolicy";
		influxDB.query(new Query("CREATE RETENTION POLICY " + rpName + " ON " + dbName + " DURATION 30h REPLICATION 2 SHARD DURATION 30m DEFAULT"));
		influxDB.setRetentionPolicy(rpName);

		influxDB.enableBatch(BatchOptions.DEFAULTS);

		SimpleDateFormat fm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		
		String filePath = "AirQualityTest.csv";
		try {
            CsvReader csvReader = new CsvReader(filePath, ',');

            csvReader.readHeaders();
            while (csvReader.readRecord()){
            	String time = csvReader.get("Date") + " " + csvReader.get("Time");
//            	System.out.println(time);
            	Date d = fm.parse(time);
            	cal.setTime(d);
            	long timeStamp = cal.getTimeInMillis() * 1000000;
            	System.out.println(timeStamp);
            	/*
            	 * Printed is 19-digit long integer, the same as the time stamp in InfluxDB.
            	 * timeStamp is always problematic. Temporarily use current milliseconds.
            	 */
                influxDB.write(Point.measurement("COandNO2")
                		.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                		.addField("CO(GT)", csvReader.get("CO(GT)"))
                		.addField("NO2(GT)", csvReader.get("NO2(GT)"))
                		.build());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
		

		//Query query = new Query("SELECT idle FROM cpu", dbName);
		//influxDB.query(query);
		//influxDB.query(new Query("DROP RETENTION POLICY " + rpName + " ON " + dbName));
		//influxDB.query(new Query("DROP DATABASE " + dbName));
		influxDB.close();
	}

}
