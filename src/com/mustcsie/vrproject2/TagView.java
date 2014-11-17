package com.mustcsie.vrproject2;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import com.google.android.gms.internal.ho;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class TagView extends SurfaceView implements	Runnable, LocationListener, SensorEventListener{

	private SurfaceHolder holder;
	private Context context	;
	private boolean stop = false;
	private Thread t;
	private LocationManager lManager;
	private String bestGPS = null;
	private double scanHeight=0,scanWidth=0 , angle=0;	//螢幕高,螢幕寬  , 螢幕角
	private double latiude=0,longitude=0;	//緯度,經度
	private boolean loopStop = false;
	private SensorManager sm;
	private float currentDegree = 0f;  //電子羅盤角度變數
	private LinkedList<TagData> dataList;
	private Canvas canvas;
	private TagData tag ;
	private float senserAngleData=0;
	private Location loc = new Location("");
	private Location myLoc = new Location("");
	
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
			lManager.requestLocationUpdates(bestGPS, 1000, 1, this);
		}
		
		sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_FASTEST);
	
	
	}
	private void showLocation(Location loc) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "is Update", Toast.LENGTH_SHORT).show();
		latiude = loc.getLatitude();
		longitude = loc.getLongitude();
		myLoc.setLatitude(latiude);
		myLoc.setLongitude(longitude);
		Log.i("fff", "latiude ="+latiude);
		Log.i("fff", "longitude ="+longitude);
		
	}
	
	
	protected void resume() {
		t = new Thread(this);
		t.start();
	}
	
	protected void destory() {
		loopStop = true;
	}
	
	protected void pause() {
		loopStop = true;
		while (!loopStop) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_POINTER_2_DOWN:
			float x = event.getX();
			float y = event.getY();
			Log.i("fff", "x = "+x);
			Log.i("fff", "y = "+y);
			break;

		}
		return super.onTouchEvent(event);
	}
	
	public void setTagDataList(LinkedList<TagData> dataList) {
		this.dataList = dataList;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.i("fff", "scanHeight="+scanHeight );
		Log.i("fff", "scanWidth="+scanWidth);
		while(!loopStop)
		{
			
			if(!holder.getSurface().isValid())
				continue;
				
			canvas = holder.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR); //重製畫面使TagView維持透明
			
			Paint myPaint = new Paint();
			myPaint.setColor(Color.BLUE);
			myPaint.setStrokeCap(Paint.Cap.ROUND);
			Paint myPaint2 = new Paint();
			myPaint2.setColor(Color.GREEN);
			myPaint2.setStrokeCap(Paint.Cap.ROUND);
			myPaint2.setStrokeWidth(10f);
			canvas.drawCircle((float)scanWidth/2, (float)(scanHeight*0.95), (float)40, myPaint);
			canvas.drawLine((float)scanWidth/2, (float)(scanHeight*0.95), (float)scanWidth, (float)(scanHeight*0.95-100), myPaint2);
			canvas.drawLine((float)scanWidth/2, (float)(scanHeight*0.95), (float)0, (float)(scanHeight*0.95-100), myPaint2);
			for(int i=0;i<dataList.size();i++)
			{
				tag = dataList.get(i);
				Bitmap tagImage = tag.getImage();
				if(tagImage == null)
					tagImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				loc.setLatitude(tag.getLatitude());
				loc.setLongitude(tag.getLongitude());
				angle = (int) (currentDegree-myLoc.bearingTo(loc));
				float dest =  myLoc.distanceTo(loc)*10;
				
				/*假如電子羅盤的角度-目標物的方位角>180,代表此目標我需要旋轉本體超過半圈,因此可以判定物體的位置在另外一邊
				因此將算出的角度-360取得反向旋轉的角度
				angle 角度如果為正 , 代表物體在本體的左邊 , 反之如果角度為負 , 代表物體在本體的左邊
				*/
				if(angle > 180 )		
					angle -= 360;    
				canvas.drawBitmap(tagImage, (float) (scanWidth/2-angle*10),-(dest-1080),null);
//				if(angle < 0)
//				Log.i("fff", "angle ="+angle);
//				Log.i("fff", "currentDegree ="+currentDegree);
//				if(dest > 0 ) 
//				Log.i("fff", "loc.distance = "+dest);
//				Log.i("fff", "data info = "+i);
//				Log.i("fff", "bearing = "+myLoc.bearingTo(loc));
			}

			holder.unlockCanvasAndPost(canvas);
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		// 當位置發生改變時
		showLocation(location);
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
			   if(senserAngleData != degree && (senserAngleData-degree<-30 || senserAngleData-degree>30))
			   {
				   senserAngleData = degree;
			   }
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
			   currentDegree = degree+90; // 保存旋轉後的度數, currentDegree是一個在類中定義的float類型變量
			   if(currentDegree<=-360)
				   currentDegree = currentDegree+360;
//			   Log.i("fff", "currentDegree="+currentDegree);
			   
			  
		}
	}
	
	public void setScanHeight(int heightPixels) {
		// TODO Auto-generated method stub
		scanHeight = heightPixels;
	}
	
	public void setScanWidth(int widthPixels) {
		// TODO Auto-generated method stub
		scanWidth = widthPixels;
	}
	
	
	
	
}
