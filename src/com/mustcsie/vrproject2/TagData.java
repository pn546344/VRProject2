package com.mustcsie.vrproject2;

import java.io.Serializable;

import android.graphics.Bitmap;

public class TagData implements Serializable{

	String name , content;     		//�W�l,���e
	double latitude=0,longitude=0;  //�n�� ,�g��
	Bitmap image;					//���󪺷Ӥ�
	
	public TagData(String name , String content , double latitude , double longitude ,Bitmap bitmap)
	{
		this.name = name;
		this.content = content;
		this.latitude = latitude;
		this.longitude = longitude;
		this.image = bitmap;
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
}
