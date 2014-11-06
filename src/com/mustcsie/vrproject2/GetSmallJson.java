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

public class GetSmallJson extends Thread{

	String result="",address=null , getData;
	LinkedList<TagData> dataList = new LinkedList<TagData>();
	public GetSmallJson(String url) {
		getData = url;
		try {
			getData	= URLEncoder.encode(getData,"utf-8");  //解決get傳送中文的問題
			address = "http://120.105.81.47/login/small_android.php?viewname="+getData;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LinkedList<TagData> getList() {
		return dataList;
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
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONObject json = jsonArray.getJSONObject(i);
					String name , content;
					double latitude,longitude;
					Bitmap bitmap;
					name = json.getString("Device_Name");
					content = json.getString("Device_Content");
					latitude = json.getDouble("Device_Latitude");
					longitude = json.getDouble("Device_Longitude");
					SmallBitmap sBitmap = new SmallBitmap(json.getString("Device_Images"));
					bitmap = sBitmap.getBitmap();
					TagData tData = new TagData(name, content, latitude, longitude, bitmap);
					dataList.add(tData);
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ttt", "Malformed");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ttt", "Unsupport");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ttt", "Io");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}
}
