package com.mustcsie.vrproject2;

import java.util.LinkedList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LoadGPS implements Runnable{
	private LocationManager lManager;
	private String bestGPS = null;
	private Location loc;
	private boolean GPSReadey = false;
	private Thread t;
	private GetBigJson gbJson;
	private LinkedList<BigPoint> list = new LinkedList<BigPoint>();
	private String[] item;
	public LoadGPS(Context context) {
		lManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		bestGPS = lManager.getBestProvider(criteria, true);
		if(bestGPS != null)
			loc = lManager.getLastKnownLocation(bestGPS);
			
	}
	
	public void onResume() {
		t = new Thread(this);
		t.start();
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!GPSReadey)
		{
			if(loc != null)
			{
				GPSReadey = true;
				gbJson = new GetBigJson("http://120.105.81.47/login/big_android.php?latiude="+loc.getLatitude()+""+"&longitude="+loc.getLongitude()+"");
				gbJson.start();
				try {
					gbJson.join();
					item = gbJson.getItem();
					list = gbJson.getBigJsonData();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public LinkedList<BigPoint> getListData() {
		return list;
	}
	
	
}
