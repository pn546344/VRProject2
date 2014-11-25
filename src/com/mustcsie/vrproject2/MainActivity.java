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
		//�̷ө��I�諸marker����
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
			   RotateAnimation���O�G�����ܤưʵe��
			    
			   �Ѽƻ���:
			   fromDegrees�G���઺�}�l���סC
			   toDegrees�G���઺�������סC
			   pivotXType�GX�b�����Y�Ҧ��A�i�H���Ȭ�ABSOLUTE�BRELATIVE_TO_SELF�BRELATIVE_TO_PARENT�C
			   pivotXValue�GX���Ъ����Y�ȡC
			   pivotYType�GY�b�����Y�Ҧ��A�i�H���Ȭ�ABSOLUTE�BRELATIVE_TO_SELF�BRELATIVE_TO_PARENT�C
			   pivotYValue�GY���Ъ����Y�ȡC
			   */
//			   RotateAnimation ra = new RotateAnimation(
//			     currentDegree, // �ʵe�_�l�ɪ��󪺨���
//			     -degree,       // �ʵe�����ɪ�����઺����(�i�j��360��)-��ܰf�ɰw����,+��ܶ��ɰw����
//			     Animation.RELATIVE_TO_SELF, 0.5f, //�ʵe�۹�󪫥�X�y�Ъ��}�l��m, �q0%~100%������, 50%������X��V���ФW�����I��m
//			     Animation.RELATIVE_TO_SELF, 0.5f); //�ʵe�۹�󪫥�Y�y�Ъ��}�l��m, �q0%~100%������, 50%������Y��V���ФW�����I��m

//			   ra.setDuration(200); // ����L�{����ɶ�
//			   ra.setRepeatCount(-1); // �ʵe���Ʀ��� (-1 ��ܤ@������)
//			   img.startAnimation(ra); // ù�L�Ϥ��ϥα���ʵe
			   float currentDegree = degree+90; // �O�s����᪺�׼�, currentDegree�O�@�Ӧb�����w�q��float�����ܶq
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
