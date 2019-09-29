package com.example.demo.model;

public class AirQuery {

	public String date;
	public String co;
	public String no2;
	
	
	

	public String getCo() {
		return co;
	}

	public void setCo(String co) {
		this.co = co;
	}

	public String getNo2() {
		return no2;
	}

	public void setNo2(String no2) {
		this.no2 = no2;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "AirQuery [date=" + date + ", co=" + co + ", no2=" + no2 + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AirQuery[] airs = new AirQuery[5];
		airs[0] = new AirQuery();
		airs[0].setCo("1.23");
		System.out.println(airs[0].toString());
	}

}
