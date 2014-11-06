package com.mustcsie.vrproject2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SmallBitmap {
	private Bitmap bitmap;
	public SmallBitmap(String url)
	{
		bitmap = LoadImageFromNetwork(url);
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	private Bitmap LoadImageFromNetwork(String url) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
		
	}
}
