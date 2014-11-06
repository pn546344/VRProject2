package com.mustcsie.vrproject2;

import android.graphics.Bitmap;

public class BigPoint {

	private double latitude=0,longitude=0;  //½n«×   , ¸g«×
	private String name=null;
	private Bitmap bitmap = null;
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String getName() {
		return name;
	}
}
