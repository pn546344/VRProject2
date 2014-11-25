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

public class MainActivity extends Activity implements OnMyLocationChangeListener, OnMarkerClickListener, SensorEventListener {
	GoogleMap map;
	private GetBigJson gbJson;
	private LocationManager lManager;
	private Location loc;
	private String bestGPS=null;
	private String[] item;
	private LinkedList<BigPoint> list = new LinkedList<BigPoint>();
	private SensorManager sm;
	CameraPosition cp ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar bar = getActionBar();
		bar.hide();
		lManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		bestGPS = lManager.getBestProvider(criteria, true);
		if (bestGPS != null) {
			loc = lManager.getLastKnownLocation(bestGPS);
			Log.i("fff", "bestGPS ="+bestGPS);
			
		}
		MapFragment frag=(MapFragment)getFragmentManager().findFragmentById(R.id.fragment1);
		map=frag.getMap();
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		//LatLng l = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
		
		
		sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(bestGPS != null){
		gbJson = new GetBigJson("http://120.105.81.47/login/big_android.php?latiude="+loc.getLatitude()+""+"&longitude="+loc.getLongitude()+"");
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
		}
		
		super.onResume();
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
			   /*
			   RotateAnimation類別：旋轉變化動畫類
			    
			   參數說明:
			   fromDegrees：旋轉的開始角度。
			   toDegrees：旋轉的結束角度。
			   pivotXType：X軸的伸縮模式，可以取值為ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
			   pivotXValue：X坐標的伸縮值。
			   pivotYType：Y軸的伸縮模式，可以取值為ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
			   pivotYValue：Y坐標的伸縮值。
			   */
//			   RotateAnimation ra = new RotateAnimation(
//			     currentDegree, // 動畫起始時物件的角度
//			     -degree,       // 動畫結束時物件旋轉的角度(可大於360度)-表示逆時針旋轉,+表示順時針旋轉
//			     Animation.RELATIVE_TO_SELF, 0.5f, //動畫相對於物件的X座標的開始位置, 從0%~100%中取值, 50%為物件的X方向坐標上的中點位置
//			     Animation.RELATIVE_TO_SELF, 0.5f); //動畫相對於物件的Y座標的開始位置, 從0%~100%中取值, 50%為物件的Y方向坐標上的中點位置

//			   ra.setDuration(200); // 旋轉過程持續時間
//			   ra.setRepeatCount(-1); // 動畫重複次數 (-1 表示一直重複)
//			   img.startAnimation(ra); // 羅盤圖片使用旋轉動畫
			   float currentDegree = degree+90; // 保存旋轉後的度數, currentDegree是一個在類中定義的float類型變量
			   if(currentDegree<=-360)
				   currentDegree = currentDegree+360;
//			   LatLng l = new LatLng(24.9449639, 121.0905384);
			   Location getMyLocation = map.getMyLocation();
			   if (getMyLocation == null) {
				   Log.i("fff", "getMyLocation is null");
			   }else 
			   { 
				   LatLng myLatLng = new LatLng(getMyLocation.getLatitude(), getMyLocation.getLongitude());
				   cp = new CameraPosition(myLatLng, map.getCameraPosition().zoom, 0, currentDegree);
				   map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			}
			   
		}
	}
}
