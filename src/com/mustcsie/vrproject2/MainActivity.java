package com.mustcsie.vrproject2;

import java.text.BreakIterator;
import java.util.LinkedList;

import com.google.android.gms.internal.lp;
import com.google.android.gms.internal.ma;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class MainActivity extends Activity implements OnMyLocationChangeListener, OnMarkerClickListener, SensorEventListener {
	GoogleMap map;
	private GetBigJson gbJson;
	private LocationManager lManager;
	private Location loc , myLoc;
	private String bestGPS=null;
	private String[] item;
	private LinkedList<BigPoint> list = new LinkedList<BigPoint>();
	private SensorManager sm;
	private CameraPosition cp ;
	private int count = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar bar = getActionBar();
		bar.hide();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		lManager = (LocationManager)getSystemService(LOCATION_SERVICE);
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//		bestGPS = lManager.getBestProvider(criteria, true);
//		if (bestGPS != null) {
//			loc = lManager.getLastKnownLocation(bestGPS);
//			Log.i("fff", "bestGPS ="+bestGPS);
			
//		}
		MapFragment frag=(MapFragment)getFragmentManager().findFragmentById(R.id.fragment1);
		map=frag.getMap();
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		//LatLng l = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
		
		sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		/*if(myLoc != null){
			gbJson = new GetBigJson("http://120.105.81.47/login/big_android.php?latiude="+myLoc.getLatitude()+""+"&longitude="+myLoc.getLongitude()+"");
			gbJson.start();
			try {
				gbJson.join();
				item = gbJson.getItem();
				list = gbJson.getBigJsonData();
				makerTag();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			Log.i("fff", "myLoc is null");
		*/
		count = 0;
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		sm.unregisterListener(this);   //撤銷羅盤感應器
		super.onPause();
	}
	
	private void startMapPoint() {
		if(myLoc != null){
			gbJson = new GetBigJson("http://120.105.81.47/login/big_android.php?latiude="+myLoc.getLatitude()+""+"&longitude="+myLoc.getLongitude()+"");
			gbJson.start();
			try {
				gbJson.join();
				item = gbJson.getItem();
				list = gbJson.getBigJsonData();
				makerTag();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}else
			Log.i("fff", "myLoc is null");
	}
	
	private void makerTag() {
		MarkerOptions options = new MarkerOptions();
		Marker marker ;
		LatLng latlng;
		BigPoint bigPoint;
		for (int i = 0; i < list.size(); i++) {
			bigPoint = list.get(i);
			latlng = new LatLng(bigPoint.getLatitude(), bigPoint.getLongitude());
//			Log.i("ttt", "bigPoint.getLatitude = "+bigPoint.getLatitude());
//			Log.i("ttt", "latlong = "+latlng);
			options.position(latlng);
			Log.i("ttt", "name ="+bigPoint.getName());
			options.title(bigPoint.getName());	
			marker = map.addMarker(options);
//			marker.showInfoWindow();
		}
		map.setOnMarkerClickListener(this);
	}
	
	
	
	@Override
	public void onMyLocationChange(Location arg0) {
		// TODO Auto-generated method stub
		LatLng loc=new LatLng(arg0.getLatitude(), arg0.getLongitude());
		CameraUpdate update=CameraUpdateFactory.newLatLng(loc);
		map.moveCamera(update);
		BitmapDescriptor icon=BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
	/*	MarkerOptions marker=new MarkerOptions();
		marker.icon(icon);
		marker.title(loc+"");
		marker.position(loc);
		map.addMarker(marker);*/
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		//依照所點選的marker換頁
		Intent intent = new Intent(this,SecondActivity.class);
		intent.putExtra("BigPoint", arg0.getTitle());
		startActivity(intent);
		return false;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			   float degree = arg0.values[0];

			   float currentDegree = degree+90; // 保存旋轉後的度數, currentDegree是一個在類中定義的float類型變量
			   if(currentDegree<=-360)
				   currentDegree = currentDegree+360;
			   Location getMyLocation = map.getMyLocation();
			   myLoc = map.getMyLocation();
			   if (getMyLocation == null) {
				   Log.i("fff", "getMyLocation is null");
			   }else 
			   { 
				   LatLng myLatLng = new LatLng(getMyLocation.getLatitude(), getMyLocation.getLongitude());
				   cp = new CameraPosition(myLatLng, map.getCameraPosition().zoom, 0, currentDegree);
				   map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
				   
			}
			   if(count ==0 && myLoc != null)
				   startMapPoint();
		}
	}
}
