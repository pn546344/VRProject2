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

import android.util.Log;

public class GetPropertyJson extends Thread{
	
	String address = "";
	private String result="";
	private LinkedList<PropertyData> dataList = new LinkedList<PropertyData>();
	public GetPropertyJson(String str)
	{
		try {
			str = URLEncoder.encode(str,"utf-8");
			address = "http://120.105.81.47/login/tag_android.php?viewname="+str;
			Log.i("ttt", "getPropertyJson is create");
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
//				Log.d("ttt", "pro result = "+result);
				JSONArray jsonArray = new JSONArray(result);
				Log.i("ttt", "property json size = "+jsonArray.length() );
				for(int i=0;i<jsonArray.length();i++)
				{
					JSONArray json2Array = jsonArray.getJSONArray(i);
						JSONObject json = json2Array.getJSONObject(0);
						String name , onUrl,offUrl;
						name = json.getString("Tag_Name");
						Log.d("ttt", "name ="+name);
						onUrl = json.getString("Tag_Image1");
						offUrl = json.getString("Tag_Image2");
						PropertyData pData = new PropertyData(name, onUrl, offUrl);
						dataList.add(pData);
					
				}
			}
		} catch (MalformedURLException e) {
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

	public LinkedList<PropertyData> getList() {
		return dataList	;
	}
}
