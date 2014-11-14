package com.mustcsie.vrproject2;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements OnMyLocationChangeListener, OnMarkerClickListener {
	GoogleMap map;
	private GetBigJson gbJson;
	private LocationManager lManager;
	private Location loc;
	private String bestGPS=null;
	private String[] item;
	private LinkedList<BigPoint> list = new LinkedList<BigPoint>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar bar = getActionBar();
		bar.hide();
		lManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		bestGPS = lManager.getBestProvider(criteria, true);
		if (bestGPS != null) {
			loc = lManager.getLastKnownLocation(bestGPS);
			Log.i("fff", "bestGPS is not null");
		}
		MapFragment frag=(MapFragment)getFragmentManager().findFragmentById(R.id.fragment1);
		map=frag.getMap();
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
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
		Intent intent = new Intent(this,SecondActivity.class);
		intent.putExtra("BigPoint", arg0.getTitle());
		startActivity(intent);
		return false;
	}
}
