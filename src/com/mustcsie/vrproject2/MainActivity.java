package com.mustcsie.vrproject2;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

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
	private LinearLayout logoView;
	private Timer timer = new Timer();
	private TimerTask timerTask = new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			cancelLogoView();
			Message msg = new Message();
			msg.what = 1;
			Handler.sendMessage(msg);
			timerTask.cancel();
		}
	};
	
	private Handler Handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
				alphaAnimation.setDuration(1000);
				logoView.setVisibility(View.GONE);
				logoView.startAnimation(alphaAnimation);
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//刪除ActionBar
		ActionBar bar = getActionBar();
		bar.hide();
		
		//設定螢幕顯示範圍(只留狀態欄)
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		lManager = (LocationManager)getSystemService(LOCATION_SERVICE);

		
		logoView = (LinearLayout)findViewById(R.id.logoView);
		
		//設定google map
		MapFragment frag=(MapFragment)getFragmentManager().findFragmentById(R.id.fragment1);
		map=frag.getMap();
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		
		//取用感應器服務(sensor)
		sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		timer.schedule(timerTask, 2000);
		
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
				Log.i("fff","datasize ===== "+list.size());
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
			//從網路上取得大項資料
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
		// 當感應器的值產生變化的時候
		if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			   //如果感應器是羅盤的話(指北針)
			   float degree = arg0.values[0];

			   float currentDegree = degree+90; // 保存旋轉後的度數, currentDegree是一個在類中定義的float類型變量
			   if(currentDegree<=-360)
				   currentDegree = currentDegree+360;
			   myLoc = map.getMyLocation();
			   if ( myLoc != null) {
				   //如果GPS不是空值
				   LatLng myLatLng = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
//				   LatLng myLatLng = new LatLng(24.863870, 120.988013);
				   cp = new CameraPosition(myLatLng, map.getCameraPosition().zoom, 0, currentDegree);
				   map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			   }
			   
			   if(count ==0 && myLoc != null)
				   startMapPoint();
		}
	}
	
}
