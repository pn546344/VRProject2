package com.mustcsie.vrproject2;

import java.io.Serializable;

import android.graphics.Bitmap;

public class TagData implements Serializable{

	String name , content;     		//名子,內容
	double latitude=0,longitude=0;  //緯度 ,經度
	Bitmap image;					//物件的照片
	String[] str;
	
	public TagData(String name , String content , double latitude , double longitude ,Bitmap bitmap,String[] str)
	{
		this.name = name;
		this.content = content;
		this.latitude = latitude;
		this.longitude = longitude;
		this.image = bitmap;
		this.str = str;
	}
	public Bitmap getImage() {
		return image;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return	longitude;
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getStr() {
		return str;  //屬性
	}
	
	
	public String getContent() {
		return content;
	}
	
}
