package com.mustcsie.vrproject2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class GetItemPhoto extends Thread{
	private String address = "";
	private String result = "";
	private LinkedList<Bitmap> dataList = new LinkedList<Bitmap>();
	public GetItemPhoto(String viewname , String devicename)
	{
		try {
			viewname = URLEncoder.encode(viewname,"utf-8");
			devicename = URLEncoder.encode(devicename,"utf-8");
			address = "http://120.105.81.47/login/device_picture.php?viewname="+viewname+"&devicename="+devicename;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			if(connection.getResponseCode()==HttpURLConnection.HTTP_OK)
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"));
				String str;
				while((str=reader.readLine())!=null)
				{
					result = result +str;
				}
				reader.close();
				connection.disconnect();
				JSONArray jsonArray = new JSONArray(result);
				Log.d("ttt", "jsonarray.length ===="+jsonArray.length());
				for(int i=0;i<jsonArray.length();i++)
				{
						JSONObject json = jsonArray.getJSONObject(i);
						String name , pic1Url,pic2Url;
						pic1Url = json.getString("Device_Picture1");
						SmallBitmap sBitmap = new SmallBitmap(pic1Url);
						Bitmap onBitmap = sBitmap.getBitmap();
						
						pic2Url = json.getString("Device_Picture2");
						SmallBitmap ofBitmap = new SmallBitmap(pic2Url);
						Bitmap offBitmap = ofBitmap.getBitmap();
						
						dataList.add(onBitmap);
						dataList.add(offBitmap);
						Log.d("ttt", "datalist add 2");
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("ttt", "datalist add 3");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("ttt", "datalist add 4");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("ttt", "datalist add 5");
		}
		super.run();
	}
	
	public LinkedList<Bitmap> getList() {
		return dataList	;
	}
}
