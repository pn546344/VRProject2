package com.mustcsie.vrproject2;

import java.util.LinkedList;

import com.google.android.gms.internal.ho;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
	private double scanHeight=0,scanWidth=0 , angle=0;	//�ù���,�ù��e  , �ù���
	private double latiude=0,longitude=0;	//�n��,�g��
	private boolean loopStop = false;
	private SensorManager sm;
	private float currentDegree = 0f;  //�q�lù�L�����ܼ�
	private LinkedList<TagData> dataList;
	private Canvas canvas;
	private TagData tag ;
	private Location loc = new Location("");
	private Location myLoc = new Location("");
	
	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		holder = getHolder();
		
		DisplayMetrics metrics = new DisplayMetrics();		//���o�ù��ؤo
		WindowManager wManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		wManager.getDefaultDisplay().getMetrics(metrics);
		scanHeight = metrics.heightPixels;					//���o�ù���
		scanWidth  = metrics.widthPixels;					//���o�ù��e
		
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
			canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR); //���s�e����TagView�����z��
			for(int i=0;i<dataList.size();i++)
			{
				tag = dataList.get(i);
				Bitmap tagImage = tag.getImage();
				if(tagImage == null)
					tagImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				loc.setLatitude(tag.getLatitude());
				loc.setLongitude(tag.getLongitude());
				angle = (int) (currentDegree-gps2d(loc));
				float dest =  myLoc.distanceTo(loc)*10;
				
				/*���p�q�lù�L������-�ؼЪ�����쨤>180,�N���ؼЧڻݭn���ॻ��W�L�b��,�]���i�H�P�w���骺��m�b�t�~�@��
				�]���N��X������-360���o�ϦV���઺����
				angle ���צp�G���� , �N����b���骺���� , �Ϥ��p�G���׬��t , �N����b���骺����
				*/
				if(angle > 180 )		
					angle -= 360;    
				canvas.drawBitmap(tagImage, (float) (scanWidth/2-angle*11),-(dest-1080),null);
//				if(angle < 0)
//				Log.i("fff", "angle ="+angle);
//				Log.i("fff", "gps2d ="+gps2d(loc));
//				Log.i("fff", "currentDegree ="+currentDegree);
				if(dest > 0 )
				Log.i("fff", "loc.distance = "+dest);
			}

			holder.unlockCanvasAndPost(canvas);
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		// ���m�o�ͧ��ܮ�
		showLocation(location);
	}
	@Override
	public void onProviderDisabled(String provider) {
		// ��������m��������
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// ��ҥΦ�m��������
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// ��w�쪬�A���ܮ�
		
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// �ǷP�����i�s����(��V����)
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
			   currentDegree = degree+90; // �O�s����᪺�׼�, currentDegree�O�@�Ӧb�����w�q��float�����ܶq
			   if(currentDegree>=360)
				   currentDegree = currentDegree-360;
//			   Log.i("fff", "currentDegree="+currentDegree);
			
		}
	}
	
	private double gps2d(Location loc) {
		//��gps���I�y�Ъ�����
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
		if(d <0 )
			d += 360;

		// d = Math.round(d*10000);
//		Log.i("fff", "gps2d ="+d);
		return d;
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
