package com.mustcsie.vrproject2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

public class TagView extends SurfaceView implements	Runnable, LocationListener, SensorEventListener{

	private SurfaceHolder holder;
	private Context context	;
	private boolean stop = false;
	private Thread t;
	private LocationManager lManager;
	private String bestGPS = null;
	private int scanHeight=0,scanWidth=0;	//螢幕高,螢幕寬
	private double latiude=0,longitude=0;	//緯度,經度
	private boolean loopStop = false;
	private SensorManager sm;
	private float currentDegree = 0f;  //電子羅盤角度變數
	
	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		holder = getHolder();
		
		DisplayMetrics metrics = new DisplayMetrics();		//取得螢幕尺寸
		WindowManager wManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		wManager.getDefaultDisplay().getMetrics(metrics);
		scanHeight = metrics.heightPixels;					//取得螢幕高
		scanWidth  = metrics.widthPixels;					//取得螢幕寬
		
		lManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);		//GPS
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		bestGPS = lManager.getBestProvider(criteria, true);
		if(bestGPS != null){
			Location loc = lManager.getLastKnownLocation(bestGPS);
			showLocation(loc);
			lManager.requestLocationUpdates(bestGPS, 1000, 10, this);
		}
		
		sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_FASTEST);
	}
	private void showLocation(Location loc) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "is Update", Toast.LENGTH_SHORT).show();
		latiude = loc.getLatitude();
		longitude = loc.getLongitude();
		Log.i("fff", "latiude ="+latiude);
		Log.i("fff", "longitude ="+longitude);
		
	}
	
	
	protected void resume() {
		t = new Thread(this);
		t.start();
	}
	
	protected void pause() {
		loopStop = true;
		while (true) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(Location location) {
		// 當位置發生改變時
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// 當關閉位置供應器時
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// 當啟用位置供應器時
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// 當定位狀態改變時
		
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// 傳感器報告新的值(方向改變)
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
			   RotateAnimation ra = new RotateAnimation(
			     currentDegree, // 動畫起始時物件的角度
			     -degree,       // 動畫結束時物件旋轉的角度(可大於360度)-表示逆時針旋轉,+表示順時針旋轉
			     Animation.RELATIVE_TO_SELF, 0.5f, //動畫相對於物件的X座標的開始位置, 從0%~100%中取值, 50%為物件的X方向坐標上的中點位置
			     Animation.RELATIVE_TO_SELF, 0.5f); //動畫相對於物件的Y座標的開始位置, 從0%~100%中取值, 50%為物件的Y方向坐標上的中點位置

			   ra.setDuration(200); // 旋轉過程持續時間
			   ra.setRepeatCount(-1); // 動畫重複次數 (-1 表示一直重複)
//			   img.startAnimation(ra); // 羅盤圖片使用旋轉動畫
			   currentDegree = -degree; // 保存旋轉後的度數, currentDegree是一個在類中定義的float類型變量
			   Log.i("fff", "currentDegree="+degree);
			
		}
	}
	
	private double gps2d(Location loc) {
		//算gps兩點座標的角度
		double d = 0;
		double lat_a=0,lng_a=0,lat_b=24.8637871,lng_b=120.9903995;
		lat_a = loc.getLatitude();
		lng_a = loc.getLongitude();
		lat_a=lat_a*Math.PI/180;
		lng_a=lng_a*Math.PI/180;
		lat_b=lat_b*Math.PI/180;
		lng_b=lng_b*Math.PI/180;

		d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
		d=Math.sqrt(1-d*d);
		d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
		d=Math.asin(d)*180/Math.PI;

		// d = Math.round(d*10000);
		return d;
		}

}
