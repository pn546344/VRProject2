package com.mustcsie.vrproject2;

import android.graphics.Bitmap;

public class ImageItemButton {

	private String viewname;
	private Bitmap enableImage, disableImage;
	
	
	public void setViewName(String viewname) {
		this.viewname = viewname;
	}
	
	public void setEnableImage(Bitmap image) {
		this.enableImage = image;
	}
	
	public void setDisableImage(Bitmap image) {
		this.disableImage = image;
	}
	
	public String getViewName() {
		return viewname;
	}
	
	public Bitmap getEnableImage() {
		return enableImage;
	}

	public Bitmap getDisableImage() {
		return disableImage;
	}

	
}
