package com.mustcsie.vrproject2;

import android.graphics.Bitmap;

public class PropertyData {
	private String name="" ;
	private Bitmap onBitmap,offBitmap;
	public PropertyData(String name,Bitmap onBitmap,Bitmap offBitmap)
	{
		this.name = name;
		this.onBitmap = onBitmap;
		this.offBitmap = offBitmap;
	}
	public String getName() {
		return name;
	}
	
	public Bitmap getOnBitmap() {
		return onBitmap;
	}
	
	public Bitmap getOffBitmap() {
		return offBitmap;
	}
}
