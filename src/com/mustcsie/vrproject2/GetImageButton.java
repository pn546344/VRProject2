package com.mustcsie.vrproject2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class GetImageButton extends Thread{

	private String viewname , address = null , result = "";
	private LinkedList<ImageItemButton> dataList = new LinkedList<ImageItemButton>();
	public GetImageButton(String viewname) {
		// TODO Auto-generated constructor stub
		this.viewname = viewname;
		try {
			this.viewname = URLEncoder.encode(this.viewname,"utf-8");
			address = "http://120.105.81.47/login/area_android.php?viewname="+this.viewname;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LinkedList<ImageItemButton> getDataList() {
		return dataList	;
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
					String item_Name;
					Bitmap EnableImage , DisableImage;
					int count = 0;
					item_Name = json.getString("Item_Name");
					SmallBitmap enable = new SmallBitmap(json.getString("Item_Images_Enable"));
					EnableImage = enable.getBitmap();
					SmallBitmap disable =  new SmallBitmap(json.getString("Item_Images_Disable"));
					DisableImage = disable.getBitmap();
					ImageItemButton IM_button = new ImageItemButton();
					IM_button.setViewName(viewname);
					IM_button.setEnableImage(EnableImage);
					IM_button.setDisableImage(DisableImage);
					dataList.add(IM_button);
				}
			}
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
