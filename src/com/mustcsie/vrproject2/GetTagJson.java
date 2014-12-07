package com.mustcsie.vrproject2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class GetTagJson extends Thread{
	String address = null , big ,result="" , tagName;
	String[] list;
	public GetTagJson(String address, String big , String tagName) {
		// TODO Auto-generated constructor stub
		this.address = address;
		this.big = big;
		this.tagName = tagName;
	}
	
	public String[] getList() {
		return list;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			if(connection.getResponseCode()==HttpURLConnection.HTTP_OK)
			{
				Log.i("ttt", "GetSmallJson is connection");
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
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					String name= "",listname = "";
					
					name = json.getString("Device_Tag_Name");
					if (!name.equals(tagName)) {
						continue;
					}else
					{
						listname = json.getString("Device_Tag");
						list = listname.split(" ");
						break;
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ttt", "Malformed");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		super.run();
	}
}
